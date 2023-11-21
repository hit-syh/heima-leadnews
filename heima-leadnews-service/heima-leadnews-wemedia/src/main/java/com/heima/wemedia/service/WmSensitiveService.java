package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {
    ResponseResult pageList(WmSensitiveDto dto);

    ResponseResult del(Integer id);

    ResponseResult save_(WmSensitive wmSensitive);


    ResponseResult update_(WmSensitive wmSensitive);
}
