package com.heima.admin.service;

import com.heima.model.admin.dtos.AdminLoginDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;

public interface AdminLoginService {

    ResponseResult in(AdminLoginDto dto);
}
