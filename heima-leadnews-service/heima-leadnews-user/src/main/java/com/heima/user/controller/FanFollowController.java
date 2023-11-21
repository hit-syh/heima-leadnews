package com.heima.user.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.FollowDto;
import com.heima.user.service.FanFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class FanFollowController {
    @Autowired
    FanFollowService fanFollowService;
    @PostMapping("/user_follow")
    public ResponseResult user_follow(@RequestBody FollowDto  dto){
        return fanFollowService.userFollow(dto);
    }
}
