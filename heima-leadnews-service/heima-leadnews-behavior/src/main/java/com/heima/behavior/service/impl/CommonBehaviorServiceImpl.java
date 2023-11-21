package com.heima.behavior.service.impl;

import com.heima.behavior.service.CommonBehaviorService;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.constants.KafkaConstrant;
import com.heima.model.behavior.constants.LikesOperation;
import com.heima.model.behavior.constants.RedisPrefixConstants;
import com.heima.model.behavior.dtos.LikeDto;
import com.heima.model.behavior.dtos.ReadDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommonBehaviorServiceImpl implements CommonBehaviorService {
    @Autowired
    CacheService cacheService;
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public ResponseResult un_likes_behavior(LikeDto dto) {

        ApUser user = AppThreadLocalUtils.getUser();
        if(dto.getArticleId()==null )
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        if(user==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        if(dto.getType().equals(LikesOperation.ADD_UNLIKE))
        {
            if (cacheService.hExists(RedisPrefixConstants.UNLIKES_ARTICLE+dto.getArticleId(),user.getId().toString())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"不喜欢成功");
            }
            cacheService.hPut(RedisPrefixConstants.UNLIKES_ARTICLE+dto.getArticleId(),user.getId().toString(),"");
            return ResponseResult.okResult("取消不喜欢成功");
        } else if (dto.getType().equals(LikesOperation.REMOVE_UNLIKE)) {
            if (cacheService.hExists(RedisPrefixConstants.UNLIKES_ARTICLE+dto.getArticleId(),user.getId().toString())) {
                cacheService.hDelete(RedisPrefixConstants.UNLIKES_ARTICLE+dto.getArticleId(),user.getId().toString());
                return ResponseResult.okResult("取消成功");
            }
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"根本没点赞");
        }else
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    @Override
    public ResponseResult likes_behavior(LikeDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        if(dto.getArticleId()==null )
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        if(user==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        if(dto.getOperation().equals(LikesOperation.ADD_LIKE))
        {
            if (cacheService.hExists(RedisPrefixConstants.LIKES_ARTICLE+dto.getArticleId(),user.getId().toString())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"已点赞");
            }
            cacheService.hPut(RedisPrefixConstants.LIKES_ARTICLE+dto.getArticleId(),user.getId().toString(),"");
            kafkaTemplate.send(KafkaConstrant.LIKE_ARTICLE_TOPIC,dto.getArticleId().toString(),"1");
            return ResponseResult.okResult("点赞成功");
        } else if (dto.getOperation().equals(LikesOperation.REMOVE_LIKE)) {
            if (cacheService.hExists(RedisPrefixConstants.LIKES_ARTICLE+dto.getArticleId(),user.getId().toString())) {
                cacheService.hDelete(RedisPrefixConstants.LIKES_ARTICLE+dto.getArticleId(),user.getId().toString());
                kafkaTemplate.send(KafkaConstrant.LIKE_ARTICLE_TOPIC,dto.getArticleId().toString(),"-1");
                return ResponseResult.okResult("取消成功");
            }
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"根本没点赞");
        }else
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

    }

    @Override
    public ResponseResult read_behavior(ReadDto dto) {
        if(dto.getArticleId()==null || dto.getCount()<=0)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        ApUser user = AppThreadLocalUtils.getUser();
        if(user==null)
            return ResponseResult.okResult("浏览成功");
        if(!cacheService.hExists(RedisPrefixConstants.READ_ARTICLE + dto.getArticleId(), user.getId().toString()))
        {
            cacheService.hPut(RedisPrefixConstants.READ_ARTICLE + dto.getArticleId(), user.getId().toString(), String.valueOf(1));
        }
        else
        {
            String count = (String) cacheService.hGet(RedisPrefixConstants.READ_ARTICLE + dto.getArticleId(), user.getId().toString());
            cacheService.hPut(RedisPrefixConstants.READ_ARTICLE + dto.getArticleId(), user.getId().toString(), String.valueOf(Integer.valueOf(count)+1));
        }
        kafkaTemplate.send(KafkaConstrant.READ_ARTICLE_TOPIC,dto.getArticleId().toString(),"1");
        return ResponseResult.okResult(200,"");

    }
}
