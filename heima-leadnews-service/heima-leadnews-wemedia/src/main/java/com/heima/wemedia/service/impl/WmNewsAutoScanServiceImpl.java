package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IAtricleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.ContentScanUtils;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.data.Json;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    IAtricleClient atricleClient;
    @Autowired
    WmChannelMapper wmChannelMapper;

    @Autowired
    WmUserMapper wmUserMapper;

    @Autowired
    WmSensitiveMapper wmSensitiveMapper;


    @Override
    @Async
    public void autoScanWmNews(Integer id) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //1.查询文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if(wmNews==null)
        {
            throw  new RuntimeException("WmNewsAutoScanServiceImpl不存在id:"+id);
        }


        //2审核
        if(wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode()))
        {
            Map<String,Object> textImages=handleTextAndImages(wmNews);
            List<String> senstives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives)).stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
            SensitiveWordUtil.initMap(senstives);
//          boolean imageScan = ContentScanUtils.imageScan(textImages.get("image"));
//          List<String> textScan = ContentScanUtils.textScan(textImages.get("text"));
            Map<String, Integer> textScanResult = SensitiveWordUtil.matchWords((String) textImages.get("text"));
            int illegalTime=textScanResult.values().stream().mapToInt(x->x).sum();
            if(illegalTime>3 )
            {
                wmNews.setStatus(WmNews.Status.FAIL.getCode());
                wmNews.setReason("文章内容包含敏感词"+textScanResult.toString());
            }
            else if(illegalTime>0)
            {
                wmNews.setStatus(WmNews.Status.ADMIN_AUTH.getCode());
                wmNews.setReason("由于包含"+textScanResult.toString()+"需要手动审核");
            }
            else
            {
                wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
            }

            wmNewsMapper.updateById(wmNews);
            if(wmNews.getStatus()!=WmNews.Status.SUCCESS.getCode()) return;


        }
        //3.成功保存app相关数据
        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,dto);
        dto.setLayout(wmNews.getType());
        WmChannel wmChannel = wmChannelMapper.selectById(dto.getChannelId());
        if(wmChannel!=null)
        {
            dto.setChannelName(wmChannel.getName());
        }
        dto.setAuthorId(Long.valueOf(wmNews.getUserId()));
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser!=null)
            dto.setAuthorName(wmUser.getName());

        dto.setId(wmNews.getArticleId());
        dto.setCreatedTime(new Date());
        ResponseResult responseResult = atricleClient.saveArticle(dto);
        if(!responseResult.getCode().equals(200))
        {
            throw new RuntimeException("WmNewsAutoScanServiceImpl文章审核保存app端报错 code: "+responseResult.getCode());
        }
        wmNews.setArticleId((Long) responseResult.getData());
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        wmNews.setReason("自动审核成功");
        wmNewsMapper.updateById(wmNews);

    }

    /**从自媒体文章封面内容提取text image
     *
     * @param wmNews
     * @return
     */

    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        StringBuilder texts = new StringBuilder();
        List<String> images=new ArrayList<>();

        if(StringUtils.isNoneBlank(wmNews.getContent()))
        {
            List<Map> maps = JSON.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if(map.get("type").equals("text"))
                {
                  texts.append(" "+map.get("value").toString());
                }
                if(map.get("type").equals("image"))
                {
                    images.add(map.get("value").toString());
                }
            }
        }
        if(StringUtils.isNotBlank(wmNews.getImages()))
        {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        if(StringUtils.isNotBlank(wmNews.getTitle()))
        {
            texts.append(" "+wmNews.getTitle());
        }
        Map<String,Object> resMap=new HashMap<>();
        resMap.put("image",images);
        resMap.put("text",texts.toString());
        return resMap;

    }
}
