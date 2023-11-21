package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;

import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {
    @Override
    public ResponseResult pageList(WmSensitiveDto dto) {
        Page<WmSensitive> page = new Page<>(dto.getPage(), dto.getSize());
        lambdaQuery().page(page);

        PageResponseResult pageResponseResult = new PageResponseResult();
        pageResponseResult.setCurrentPage((int) page.getCurrent());
        pageResponseResult.setSize((int) page.getSize());
        pageResponseResult.setTotal((int) page.getTotal());
        pageResponseResult.setData(page.getRecords());

        return  pageResponseResult;
    }

    @Override
    public ResponseResult del(Integer id) {
        boolean b = removeById(id);
        if(b)
        {
            return ResponseResult.okResult("删除成功");
        }
        else
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
    }

    @Override
    public ResponseResult save_(WmSensitive wmSensitive) {
        wmSensitive.setCreatedTime(new Date());
        save(wmSensitive);
        return ResponseResult.okResult("添加成功");
    }

    @Override
    public ResponseResult update_(WmSensitive wmSensitive) {
        wmSensitive.setCreatedTime(new Date());
        updateById(wmSensitive);
        return ResponseResult.okResult("更新成功");
    }


}
