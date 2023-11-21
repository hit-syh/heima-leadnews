package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.model.article.constrants.KafkaTopic;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.behavior.constants.KafkaConstrant;
import com.heima.model.common.constants.ArticleConstrant;
import com.heima.model.search.vos.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SyncArticleListener {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    /**
     * 收到添加文章的消息
     */
    @KafkaListener(topics =ArticleConstrant.ARTICLE_ES_SYNC_TOPIC)
    public void addArticle(String message) throws IOException {
        if(StringUtils.isNotBlank(message))
        {
            log.info("接收到消息,将其加入es message:{}",message);
            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);
            IndexRequest request = new IndexRequest("app_info_article").id(searchArticleVo.getId().toString());
            request.source(message, XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);

        }

    }
    @Autowired
    RestHighLevelClient client;
    @KafkaListener(topics = KafkaTopic.UP_OR_DOWN)
    public void UpOrDownListener(ConsumerRecord<String,String> record) throws IOException {
        Long articleId = Long.valueOf(record.key());
        Boolean isDown=Boolean.valueOf(record.value());
        UpdateRequest request = new UpdateRequest("app_info_article",articleId.toString());
        request.doc("isDown",isDown);
        client.update(request, RequestOptions.DEFAULT);
        log.info("search  文章{} isDown状态修改为{}",articleId,isDown);
    }
}
