package com.heima.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.heima.kafka.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    @GetMapping("/hello")
    public String hello()
    {
        kafkaTemplate.send("hello-topic","黑马程序员");
        return "ok";
    }
    @GetMapping("/user")
    public String user()
    {
        User user = new User();
        user.setAge(10);
        user.setUsename("黑马");
        kafkaTemplate.send("user-topic", JSON.toJSONString(user));
        return "ok";
    }
}
