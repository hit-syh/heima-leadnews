package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.constants.WmChannelConstrant;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
    @Autowired
    private WmNewsMapper wmNewsMapper;

    /**
     * 查询所有频道
     * @return
     */
    @Override
    public ResponseResult findAll() {
        return ResponseResult.okResult(list());
    }

    @Override
    public ResponseResult list(WmChannelDto dto) {
        Page<WmChannel> page = new Page<>(dto.getPage(), dto.getSize());
        lambdaQuery().like(StringUtils.isNotBlank(dto.getName()),WmChannel::getName,dto.getName()).page(page);
        PageResponseResult pageResponseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult update(WmChannel wmChannel) {
        if(wmChannel.getStatus().equals(WmChannelConstrant.WM_CHANNEL_DOWN))
        {
            Integer count = wmNewsMapper.selectCount(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, wmChannel.getId()));
            if(count>0)
                return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_CANT_DOWN);
        }
        updateById(wmChannel);
        return ResponseResult.okResult("修改成功");
    }

    @Override
    public ResponseResult save_(WmChannel wmChannel) {
        wmChannel.setCreatedTime(new Date());
        save(wmChannel);
        return ResponseResult.okResult("添加成功");
    }

    @Override
    public ResponseResult del(Integer id) {
        WmChannel one = lambdaQuery().eq(WmChannel::getId, id).one();
        if(one.getStatus().equals(WmChannelConstrant.WM_CHANNEL_UP))
        {
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_CANT_DELE);
        }
        removeById(id);
        return ResponseResult.okResult("删除成功");
    }
}