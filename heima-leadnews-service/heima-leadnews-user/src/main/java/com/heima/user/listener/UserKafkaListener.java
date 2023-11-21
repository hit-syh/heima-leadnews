package com.heima.user.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.model.user.Constrants.KafkaTopic;
import com.heima.model.user.Constrants.UserConstrant;
import com.heima.model.user.dtos.FollowDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.mapper.ApUserMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.heima.model.user.pojos.ApUserFan;
import com.heima.model.user.pojos.ApUserFollow;

import java.util.Date;
import java.util.List;

@Component
public class UserKafkaListener {
    @Autowired
    ApUserMapper apUserMapper;
    @Autowired
    ApUserFanMapper apUserFanMapper;
    @Autowired
    ApUserFollowMapper apUserFollowMapper;
    @KafkaListener(topics = KafkaTopic.FAN_FOLLOW)
    public void fanFollow(ConsumerRecord<String,String > record)
    {
        FollowDto dto = JSON.parseObject(record.value(), FollowDto.class);
        ApUser authUser = apUserMapper.selectById(dto.getAuthorId());
        ApUser user=apUserMapper.selectById(Integer.valueOf(record.key()));
        if(dto.getOperation().equals(UserConstrant.FOLLOW_OPERATE))
        {
            //关注列表添加
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(authUser.getId());
            apUserFollow.setFollowName(authUser.getName());
            apUserFollow.setLevel(0);
            apUserFollow.setIsNotice(0);
            apUserFollow.setCreatedTime(new Date());
            apUserFollowMapper.insert(apUserFollow);

            //添加作者的粉丝表
            ApUserFan apUserFan = new ApUserFan();
            apUserFan.setUserId(authUser.getId());
            apUserFan.setFansId(user.getId());
            apUserFan.setFansName(user.getName());
            apUserFan.setLevel(0);
            apUserFan.setCreatedTime(new Date());
            apUserFan.setIsDisplay(0);
            apUserFan.setIsShieldLetter(0);
            apUserFan.setIsShieldComment(0);
            apUserFanMapper.insert(apUserFan);
        } else if (dto.getOperation().equals(UserConstrant.UN_FOLLOW_OPERATE)) {
            //关注列表删除
            List<ApUserFollow> apUserFollows = apUserFollowMapper.selectList(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getUserId, user.getId()).eq(ApUserFollow::getFollowId, authUser.getId()));
            if(apUserFollows!=null &&apUserFollows.size()!=0)
                apUserFollows.forEach(apUserFollow -> apUserFollowMapper.deleteById(apUserFollow.getId()));
            //作者粉丝列表删除
            List<ApUserFan> apUserFans = apUserFanMapper.selectList(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getUserId, authUser.getId()).eq(ApUserFan::getFansId, user.getId()));
            if(apUserFans!=null&&apUserFans.size()!=0 )
                apUserFans.forEach(apUserFan -> apUserFanMapper.deleteById(apUserFan.getId()));
        }
    }

}
