package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleSercive;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.constants.ArticleConstrant;
import com.heima.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.checkerframework.checker.index.qual.SameLen;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

@Service
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {
    @Autowired
    Configuration configuration;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    ApArticleMapper apArticleMapper;
    @Override
    @Async
    public void buildMinIO(ApArticle article, String content) {
        if(StringUtils.isNotBlank(content))
        {

            try {
                Template template = template = configuration.getTemplate("article.ftl");
                HashMap<String, Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSONArray.parseArray(content));
                StringWriter out=new StringWriter();
                template.process(contentDataModel,out);
                //上传至minio
                InputStream in = new ByteArrayInputStream(out.toString().getBytes());
                String path = fileStorageService.uploadHtmlFile("", article.getId() + ".html", in);
                //修改article表保存static_url字段
                System.out.println(path);
                LambdaUpdateWrapper<ApArticle> apArticleLambdaUpdateWrapper = new LambdaUpdateWrapper<ApArticle>().set(ApArticle::getStaticUrl,path).eq(ApArticle::getId,article.getId());

                apArticleMapper.update(null,apArticleLambdaUpdateWrapper);
                
                
                //发送消息创建索引
                createArticleESIndex(article,content,path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }



    }
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private void createArticleESIndex(ApArticle article, String content, String path) {
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(article,searchArticleVo);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(path);
        kafkaTemplate.send(ArticleConstrant.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
    }
}
