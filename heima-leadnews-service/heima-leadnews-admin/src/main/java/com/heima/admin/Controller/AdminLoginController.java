package com.heima.admin.Controller;

import com.heima.admin.service.AdminLoginService;
import com.heima.model.admin.dtos.AdminLoginDto;
import com.heima.model.common.dtos.ResponseResult;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class AdminLoginController {
    @Autowired
    private AdminLoginService adminLoginService;
    @PostMapping("/in")
    public ResponseResult in(@RequestBody AdminLoginDto dto)
    {
        return adminLoginService.in(dto);
    }

}
