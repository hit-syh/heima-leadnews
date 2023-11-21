package com.heima.kafka.listenner;

import com.alibaba.fastjson.JSON;
import com.heima.kafka.pojos.User;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class HelloListener {
    @KafkaListener(topics = "itcast-topic")
    public void  onMessage(String message){
        if(!StringUtils.isEmpty(message))
        {
            System.out.println(message);
        }


    }
    @KafkaListener(topics = "user-topic")
    public void  listenerForUser(String message){
        User user = JSON.parseObject(message, User.class);
        if(!StringUtils.isEmpty(message))
        {
            System.out.println(user);
        }


    }
}
