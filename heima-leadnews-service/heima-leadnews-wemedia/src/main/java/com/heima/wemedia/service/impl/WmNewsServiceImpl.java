package com.heima.wemedia.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.article.IAtricleClient;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.constants.WemediaConstants;
import com.heima.model.common.constants.WmNewsMessageConstants;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.schedule.dtos.WmAuditDto;
import com.heima.model.schedule.vos.WmAuditVo;
import com.heima.model.wemedia.dtos.WmNewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.*;
import com.heima.utils.thread.WmThreadLocalUtils;
import com.heima.wemedia.mapper.*;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmnewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl  extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult finList(WmNewsPageReqDto dto) {
        dto.checkParam();

        IPage<WmNews> page=new Page<>(dto.getPage(),dto.getSize());
        lambdaQuery().eq(dto.getStatus()!=null,WmNews::getStatus,dto.getStatus())
                .eq(dto.getChannelId()!=null,WmNews::getChannelId,dto.getChannelId())
                .between(dto.getBeginPubDate()!=null &&dto.getEndPubDate()!=null ,WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate())
                .like(StringUtils.isNotBlank(dto.getKeyword()),WmNews::getTitle,dto.getKeyword())
                .eq(WmNews::getUserId, WmThreadLocalUtils.getUser().getId())
                .orderByDesc(WmNews::getPublishTime)
                .page(page);
        PageResponseResult pageResponseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }
    @Autowired
    WmNewsAutoScanService wmNewsAutoScanService;
    @Autowired
    private WmnewsTaskService wmnewsTaskService;
    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        //0.条件判断
        if(dto==null || dto.getContent()==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        //1.报错或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto,wmNews);
        //image无法复制
        if(dto.getImages()!=null &&dto.getImages().size()>0) {
            String imageStr = org.apache.commons.lang3.StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        //如果当前封面类型为自动-1
         if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO))
                wmNews.setType(null);
         saveOrUpdateWmNews(wmNews);
        //2.判断是否为草稿
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode()))
        {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        //3.不是草稿，保存文章图片与素材的关系
        List<String> materials=extractUrlInfo(dto.getContent());
        saverelativeInfoForContent(materials,wmNews.getId());
        //4.不是草稿，保存封面与图片的关系

        saverelativeInfoForCover(dto,wmNews,materials);
//        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        wmnewsTaskService.addNewsToTask(wmNews.getId(),wmNews.getPublishTime());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {

        //1.检查参数
        if(dto.getId()==null || dto.getEnable()==null)
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询文章
        WmNews wmNews=getById(dto.getId());
        if(wmNews==null)
        {
            return  ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3判断文章是否已发布
        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode()))
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //修改enable
        wmNews.setEnable(dto.getEnable());
        updateById(wmNews);

        //发送消息通知Article
        if(wmNews.getArticleId()!=null)
        {
            Map<String,Object> map=new HashMap<>();
            map.put("articleId",wmNews.getArticleId());
            map.put("enable",wmNews.getEnable());
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));

        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
    @Autowired
    WmUserMapper wmUserMapper;
    @Override
    public ResponseResult list_vo(WmAuditDto dto) {
        Page<WmNews> page = new Page<>(dto.getPage(),dto.getSize());
        lambdaQuery().like(StringUtils.isNotBlank(dto.getTitle()),WmNews::getTitle,dto.getTitle()).eq(dto.getStatus()!=null,WmNews::getStatus,dto.getStatus()).page(page);

        ArrayList<WmAuditVo> wmAuditVos = new ArrayList<>();
        page.getRecords().stream().forEach(wmNews -> {
            WmAuditVo wmAuditVo = new WmAuditVo();
            WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
            if(wmUser!=null)
            {
                BeanUtils.copyProperties(wmNews,wmAuditVo);
                wmAuditVo.setAuthorName(wmUser.getName());
                wmAuditVos.add(wmAuditVo);
            }
        });
        PageResponseResult pageResponseResult = new PageResponseResult((int) page.getCurrent(), wmAuditVos.size(), (int) page.getTotal());
        pageResponseResult.setData(wmAuditVos);
        return pageResponseResult;
    }

    @Override
    public ResponseResult one_vo(Integer id) {
        WmNews wmNews = getById(id);
        WmAuditVo wmAuditVo = new WmAuditVo();
        BeanUtils.copyProperties(wmNews,wmAuditVo);
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser!=null)
        {
            wmAuditVo.setAuthorName(wmUser.getName());
        }
        return ResponseResult.okResult(wmAuditVo);

    }
    @Autowired
    WmChannelMapper wmChannelMapper;
    @Autowired
    IAtricleClient atricleClient;
    @Override
    public ResponseResult authPass(WmNews wmNews) {
        updateById(wmNews);
        //成功保存app相关数据
        wmNews=getById(wmNews.getId());
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
            throw new RuntimeException("WmNewsServiceImpl文章审核保存app端报错 code: "+responseResult.getCode());
        }
        wmNews.setArticleId((Long) responseResult.getData());
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
        updateById(wmNews);
        return ResponseResult.okResult("审核成功");
    }

    @Override
    public ResponseResult authFail(WmNewsAuthDto dto) {
        WmNews wmNews = new WmNews();
        wmNews.setId(dto.getId());
        wmNews.setStatus(dto.getStatus().shortValue());
        wmNews.setReason(StringUtils.isNotBlank(dto.getMsg())?dto.getMsg():dto.getTitle());
        updateById(wmNews);
        return ResponseResult.okResult("审核成功");
    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     *
     * 第二个功能：保存封面图片与素材的关系
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saverelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {
        List<String> images = dto.getImages();
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO))
        {
            //多图
            if(materials.size()>=3)
            {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images=materials.stream().limit(3).collect(Collectors.toList());
            }
            //单图
            else if(materials.size()>=1 &&materials.size()<3)
            {
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images=materials.stream().limit(1).collect(Collectors.toList());
            }
            //无图
            else
            {
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images!=null && images.size()>0){
                wmNews.setImages(org.apache.commons.lang3.StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        if (images!=null &&images.size()>0)
        {
            saverelativeInfo(images,wmNews.getId(),WemediaConstants.WM_COVER_REFERENCE);
        }

    }

    /**
     * 处理文章内容和图片的关系
     * @param materials
     * @param id
     */
    private void saverelativeInfoForContent(List<String> materials, Integer id) {
        saverelativeInfo(materials,id,WemediaConstants.WM_CONTENT_REFERENCE);
    }
    @Autowired
    WmMaterialMapper wmMaterialMapper;
    private void saverelativeInfo(List<String> materials, Integer newsId, Short type) {
        if(materials.size()!=0)
        {
            List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));
            if(wmMaterials.size()!=materials.size())
            {
                throw new CustomException(AppHttpCodeEnum.MATRIALS_REFER_INVAID);
            }
            List<Integer> matralIds = wmMaterials.stream().map(wmMaterial -> wmMaterial.getId()).collect(Collectors.toList());
            wmNewsMaterialMapper.saveRelations(matralIds,newsId,type);

        }
    }

    /**
     * 提取文章内容中的图片
     * @param content
     * @return
     */
    private List<String> extractUrlInfo(String content) {
        List<String> materials=new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image"))
            {
                String imgUrl = map.get("value").toString();
                materials.add(imgUrl);
            }
        }
        return materials;
    }

    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;
    private void saveOrUpdateWmNews(WmNews wmNews) {

        //补全属性
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);//默认上架

        if(wmNews.getId()==null)
        {
            save(wmNews);
        }
        else {
            //删除图片素材关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            updateById(wmNews);
        }
    }
}
