package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.service.ApCollerctionService;
import com.heima.common.redis.CacheService;
import com.heima.model.article.constrants.CollectionConstrant;
import com.heima.model.article.constrants.KafkaTopic;
import com.heima.model.article.dtos.CollectionDto;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.behavior.constants.RedisPrefixConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service

public class ApCollerctionServiceImpl extends  ServiceImpl<ApCollectionMapper,ApCollection> implements  ApCollerctionService{
    @Autowired
    CacheService cacheService;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public ResponseResult collection_behavior(CollectionDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        if(user==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        if(dto.getEntryId()==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        if(dto.getOperation().equals(CollectionConstrant.COLLECTION_OPERATION))
        {
            if(cacheService.hExists(RedisPrefixConstants.ARTICLE_COLLECTION+dto.getEntryId(), String.valueOf(user.getId())))
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"重复收藏");

            ApCollection apCollection = new ApCollection();
            apCollection.setArticleId(dto.getEntryId());
            apCollection.setEntryId(user.getId());
            try {
                apCollection.setPublishedTime(dto.getPublishedYear());
            } catch (ParseException e) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"日期格式有误");
            }
            apCollection.setType(Integer.valueOf(dto.getType()));
            apCollection.setCollectionTime(new Date());
            cacheService.hPut(RedisPrefixConstants.ARTICLE_COLLECTION+dto.getEntryId(), String.valueOf(user.getId()), JSON.toJSONString(apCollection));
            kafkaTemplate.send(KafkaTopic.COLLECTION_TOPIC, String.valueOf(user.getId()),JSON.toJSONString(dto));
            return ResponseResult.okResult("收藏成功");
        }
        else if(dto.getOperation().equals(CollectionConstrant.CANCEL_COLLECTION_OPERATION)){
            if (!cacheService.hExists(RedisPrefixConstants.ARTICLE_COLLECTION+dto.getEntryId(), String.valueOf(user.getId()))) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"根本没收藏");
            }
            cacheService.hDelete(RedisPrefixConstants.ARTICLE_COLLECTION+dto.getEntryId(), String.valueOf(user.getId()));
            kafkaTemplate.send(KafkaTopic.COLLECTION_TOPIC,String.valueOf(user.getId()),JSON.toJSONString(dto));
            return ResponseResult.okResult("取消收藏成功");
        }
        else
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }
}
