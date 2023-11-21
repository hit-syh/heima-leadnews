package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.article.service.ApArticleSercive;
import com.heima.article.service.ApAtricleConfigServices;
import com.heima.article.service.ApCollerctionService;
import com.heima.model.article.constrants.CollectionConstrant;
import com.heima.model.article.constrants.KafkaTopic;
import com.heima.model.article.dtos.CollectionDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.behavior.constants.KafkaConstrant;
import com.heima.model.common.constants.WmNewsMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Transactional
public class ArticleKafkaLinstener {
    @Autowired
    ApAtricleConfigServices apAtricleConfigServices;
    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void upOrDown(String message){
        if(StringUtils.isNotBlank(message))
        {
            Map map = JSON.parseObject(message, Map.class);

            apAtricleConfigServices.updatebyMap(map);
        }

    }
    @Autowired
    ApCollerctionService apCollerctionService;
    @Autowired
    ApArticleSercive apArticleSercive;
    @KafkaListener(topics = KafkaTopic.COLLECTION_TOPIC)
    public void CollectionOrCancel(ConsumerRecord<String,String> record)
    {
        CollectionDto dto = JSON.parseObject(record.value(), CollectionDto.class);
        ApArticle article = apArticleSercive.getById(dto.getEntryId());
        if(dto.getOperation().equals(CollectionConstrant.COLLECTION_OPERATION))
        {
            ApCollection apCollection = new ApCollection();
            apCollection.setArticleId(dto.getEntryId());
            apCollection.setEntryId(Integer.valueOf(record.key()));
            apCollection.setType(Integer.valueOf(dto.getType()));
            apCollection.setCollectionTime(new Date());
            try {
                apCollection.setPublishedTime(dto.getPublishedYear());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            apCollerctionService.save(apCollection);

            if(article.getCollection()!=null &&article.getCollection()>=0)
                article.setCollection(article.getCollection()+1);
            else
                article.setCollection(1);
            apArticleSercive.updateById(article);
        }
        else {
            List<ApCollection> list = apCollerctionService.lambdaQuery().eq(ApCollection::getArticleId, dto.getEntryId()).eq(ApCollection::getEntryId, record.key()).list();
            if(list!=null && list.size()!=0)
                list.forEach(apCollection -> {apCollerctionService.removeById(apCollection.getId());
                article.setCollection(article.getCollection()-1);});
            if(article.getCollection()<0)
                article.setCollection(0);
            apArticleSercive.updateById(article);
        }
    }
    @KafkaListener(topics = KafkaConstrant.LIKE_ARTICLE_TOPIC)
    public void likeBhavorListener(ConsumerRecord<String,String> record){
        ApArticle article = apArticleSercive.getById(Long.valueOf(record.key()));
        if(article==null)
            return;
        Integer likes = article.getLikes();
        if(likes==null)
            likes=0;
        likes=likes+Integer.valueOf(record.value());
        article.setLikes(likes);
        apArticleSercive.updateById(article);

    }
    @KafkaListener(topics = KafkaConstrant.READ_ARTICLE_TOPIC)
    public void readBhavorListener(ConsumerRecord<String,String> record){
        ApArticle article = apArticleSercive.getById(Long.valueOf(record.key()));
        if(article==null)
            return;
        Integer views = article.getViews();
        if (views==null)
            views=0;
        views=views+Integer.valueOf(record.value());
        article.setViews(views);
        apArticleSercive.updateById(article);

    }

}
