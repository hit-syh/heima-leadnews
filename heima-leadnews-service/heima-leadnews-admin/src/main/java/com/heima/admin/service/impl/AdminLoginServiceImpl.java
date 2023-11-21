package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdminLoginService;
import com.heima.model.admin.dtos.AdminLoginDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AdminLoginServiceImpl implements AdminLoginService {
    @Autowired
    private AdUserMapper adUserMapper;
    @Override
    public ResponseResult in(AdminLoginDto dto) {
        AdUser adUser = adUserMapper.selectOne(new LambdaQueryWrapper<AdUser>().eq(AdUser::getName, dto.getName()));
        if(dto==null)
        {
            return  ResponseResult.errorResult(AppHttpCodeEnum.ADMIN_USER_NOT_EXTIST);
        }
        String password = dto.getPassword();
        String saltPassword = DigestUtils.md5DigestAsHex((password + adUser.getSalt()).getBytes());
        if(!saltPassword.equals(adUser.getPassword()))
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        String token = AppJwtUtil.getToken(adUser.getId().longValue());
        adUser.setLoginTime(new Date());
        adUser.setSalt("");
        adUser.setPassword("");

        Map map=new HashMap<String, Object>();
        map.put("user",adUser);
        map.put("token",token);
        return ResponseResult.okResult(map);
    }
}
