package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApAtricleConfigServices;
import com.heima.model.article.constrants.KafkaTopic;
import com.heima.model.article.pojos.ApArticleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApAtricleConfigServicesImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApAtricleConfigServices {
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Override
    public void updatebyMap(Map map) {

        boolean isDown=map.get("enable").equals(1) ? false:true;
        lambdaUpdate().eq(ApArticleConfig::getArticleId,map.get("articleId")).set(ApArticleConfig::getIsDown,isDown).update();
        kafkaTemplate.send(KafkaTopic.UP_OR_DOWN,String.valueOf(map.get("articleId")),String.valueOf(isDown));
    }
}
