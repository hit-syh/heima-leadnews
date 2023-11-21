package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.constants.ApUserStatusConstrant;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.model.user.vos.ApUserVo;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import com.jcraft.jsch.MAC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Autowired
    ApUserRealnameMapper apUserRealnameMapper;
    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名密码
        if(StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword()))
        {
            ApUser dbUser = lambdaQuery().eq(ApUser::getPhone, dto.getPhone()).one();
            if(dbUser==null)
            {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(!pswd.equals(dbUser.getPassword()))
            {
                return  ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            Map<String,Object> map=new HashMap<>();
            map.put("token",token);
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("user",dbUser);
            return  ResponseResult.okResult(map);
        }
        else
        {
            Map<String,Object> map=new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }

    @Override
    public ResponseResult list_(AuthDto dto) {
        Page<ApUser> apUserPage = new Page<>(dto.getPage(), dto.getSize());
        lambdaQuery().eq(dto.getId()!=null,ApUser::getId,dto.getId()).eq(dto.getStatus()!=null,ApUser::getStatus,dto.getStatus()).page(apUserPage);

        List<ApUserVo> apUserVos=new ArrayList<>();
        apUserPage.getRecords().forEach(apUser -> {
            ApUserRealname apUserRealname = apUserRealnameMapper.selectOne(Wrappers.<ApUserRealname>lambdaQuery().eq(ApUserRealname::getUserId, apUser.getId()));
            if(apUserRealname!=null)
            {
                ApUserVo apUserVo = new ApUserVo();
                BeanUtils.copyProperties(apUserRealname,apUserVo);
                BeanUtils.copyProperties(apUser,apUserVo);
                apUserVo.setStatus(apUserRealname.getStatus());
                apUserVo.setSalt("");
                apUserVo.setPassword("");
                apUserVos.add(apUserVo);
            }
        });
        PageResponseResult pageResponseResult = new PageResponseResult((int) apUserPage.getPages(), apUserVos.size(), (int) apUserPage.getTotal());
        pageResponseResult.setData(apUserVos);
        return pageResponseResult;


    }

    @Override
    public ResponseResult authPass(AuthDto dto) {
        apUserRealnameMapper.update(null,Wrappers.<ApUserRealname>lambdaUpdate().eq(ApUserRealname::getUserId,dto.getId()).set(ApUserRealname::getStatus,ApUserStatusConstrant.PASS));
    return ResponseResult.okResult("审核成功");
    }

    @Override
    public ResponseResult authFail(AuthDto dto) {
        apUserRealnameMapper.update(null,Wrappers.<ApUserRealname>lambdaUpdate().eq(ApUserRealname::getUserId,dto.getId()).set(ApUserRealname::getStatus,ApUserStatusConstrant.NOT_PASS).set(StringUtils.isNotBlank(dto.getMsg()),ApUserRealname::getReason,dto.getMsg()));
        return ResponseResult.okResult("审核成功");
    }
}
