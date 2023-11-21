package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.constants.RedisPrefixConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.Constrants.KafkaTopic;
import com.heima.model.user.Constrants.UserConstrant;
import com.heima.model.user.dtos.FollowDto;
import com.heima.model.user.pojos.ApUser;

import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.FanFollowService;
import com.heima.utils.thread.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
@Transactional
@Service
public class FanFollowServiceImpl implements FanFollowService {
    @Autowired
    ApUserMapper apUserMapper;
    @Autowired
    ApUserFollowMapper apUserFollowMapper;
    @Autowired
    ApUserFanMapper apUserFanMapper;
    @Autowired
    CacheService cacheService;
    @Autowired
    KafkaTemplate<String,String > kafkaTemplate;
    @Override
    public ResponseResult userFollow(FollowDto dto) {

        ApUser user = AppThreadLocalUtils.getUser();
        if(user==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH,"未登录");
        if(dto.getAuthorId()==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);

        if(dto.getOperation().equals(UserConstrant.FOLLOW_OPERATE))
        {
            if(cacheService.hExists(RedisPrefixConstants.FOLLOW_AUTHOR+user.getId(), String.valueOf(dto.getAuthorId()))
                    && cacheService.hExists(RedisPrefixConstants.AUTHOR_FANS+dto.getAuthorId(), String.valueOf(user.getId()))
            )
            {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"您已经关注过作者");
            }
            cacheService.hPut(RedisPrefixConstants.FOLLOW_AUTHOR+user.getId(), String.valueOf(dto.getAuthorId()),"");
            //添加作者的粉丝表
            cacheService.hPut(RedisPrefixConstants.AUTHOR_FANS+dto.getAuthorId(), String.valueOf(user.getId()),"");
            kafkaTemplate.send(KafkaTopic.FAN_FOLLOW, String.valueOf(user.getId()), JSON.toJSONString(dto));
            return ResponseResult.okResult("关注成功");
        } else if (dto.getOperation().equals(UserConstrant.UN_FOLLOW_OPERATE)) {
            if(!cacheService.hExists(RedisPrefixConstants.FOLLOW_AUTHOR+user.getId(), String.valueOf(dto.getAuthorId()))
                    || !cacheService.hExists(RedisPrefixConstants.AUTHOR_FANS+dto.getAuthorId(), String.valueOf(user.getId()))
            )
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"根本没关注");
            //关注列表删除
            cacheService.hDelete(RedisPrefixConstants.FOLLOW_AUTHOR+user.getId(), String.valueOf(dto.getAuthorId()));
            //作者粉丝列表删除
            cacheService.hDelete(RedisPrefixConstants.AUTHOR_FANS+dto.getAuthorId(), String.valueOf(user.getId()));
            kafkaTemplate.send(KafkaTopic.FAN_FOLLOW, String.valueOf(user.getId()), JSON.toJSONString(dto));
            return ResponseResult.okResult("取消关注成功");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }
    /*@Override
    public ResponseResult userFollow(FollowDto dto) {

        ApUser user = AppThreadLocalUtils.getUser();
        if(user==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH,"未登录");
        if(dto.getAuthorId()==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        ApUser authUser = apUserMapper.selectById(dto.getAuthorId());
        user=apUserMapper.selectById(user.getId());
        if(authUser==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"作者不存在");
        if(dto.getOperation().equals(UserConstrant.FOLLOW_OPERATE))
        {
            ApUserFollow apUserFollow = apUserFollowMapper.selectOne(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getUserId, user.getId()).eq(ApUserFollow::getFollowId, authUser.getId()));
            ApUserFan apUserFan = apUserFanMapper.selectOne(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getUserId, authUser.getId()).eq(ApUserFan::getFansId, user.getId()));
            if(apUserFan!=null ||apUserFollow!=null)
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"您已经关注过作者");
            //关注列表添加
             apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(authUser.getId());
            apUserFollow.setFollowName(authUser.getName());
            apUserFollow.setLevel(0);
            apUserFollow.setIsNotice(0);
            apUserFollow.setCreatedTime(new Date());
            apUserFollowMapper.insert(apUserFollow);

            //添加作者的粉丝表
             apUserFan = new ApUserFan();
            apUserFan.setUserId(authUser.getId());
            apUserFan.setFansId(user.getId());
            apUserFan.setFansName(user.getName());
            apUserFan.setLevel(0);
            apUserFan.setCreatedTime(new Date());
            apUserFan.setIsDisplay(0);
            apUserFan.setIsShieldLetter(0);
            apUserFan.setIsShieldComment(0);
            apUserFanMapper.insert(apUserFan);
            return ResponseResult.okResult("关注成功");
        } else if (dto.getOperation().equals(UserConstrant.UN_FOLLOW_OPERATE)) {
            //关注列表删除
            ApUserFollow apUserFollow = apUserFollowMapper.selectOne(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getUserId, user.getId()).eq(ApUserFollow::getFollowId, authUser.getId()));

            //作者粉丝列表删除
            ApUserFan apUserFan = apUserFanMapper.selectOne(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getUserId, authUser.getId()).eq(ApUserFan::getFansId, user.getId()));
            if(apUserFollow==null || apUserFan==null)
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"根本没关注");
            apUserFollowMapper.deleteById(apUserFollow);
            apUserFanMapper.deleteById(apUserFan);
            return ResponseResult.okResult("取消关注成功");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }*/
}
