package com.heima.user.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private ApUserService apUserService;
    @PostMapping("/list")
    public ResponseResult list(@RequestBody AuthDto dto){
        return apUserService.list_(dto);
    }
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto){
        return apUserService.authPass(dto);
    }
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto)
    {
        return apUserService.authFail(dto);
    }

}
