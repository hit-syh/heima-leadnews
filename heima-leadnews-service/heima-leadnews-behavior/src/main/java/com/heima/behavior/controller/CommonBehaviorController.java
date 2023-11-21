package com.heima.behavior.controller;

import com.heima.behavior.service.CommonBehaviorService;
import com.heima.model.behavior.dtos.LikeDto;
import com.heima.model.behavior.dtos.ReadDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CommonBehaviorController {
    @Autowired
    CommonBehaviorService commonBehaviorService;
    @PostMapping("/un_likes_behavior")
    public ResponseResult un_likes_behavior(@RequestBody LikeDto dto){
        return commonBehaviorService.un_likes_behavior(dto);
    }
    @PostMapping("/likes_behavior")
    public ResponseResult likes_behavior(@RequestBody LikeDto dto){
        return commonBehaviorService.likes_behavior(dto);
    }
    @PostMapping("/read_behavior")
    public ResponseResult read_behavior(@RequestBody ReadDto dto){
        return commonBehaviorService.read_behavior(dto);

    }

}
