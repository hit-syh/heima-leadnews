package com.heima.behavior.service;

import com.heima.model.behavior.dtos.LikeDto;
import com.heima.model.behavior.dtos.ReadDto;
import com.heima.model.common.dtos.ResponseResult;

public interface CommonBehaviorService {
    ResponseResult un_likes_behavior(LikeDto dto);

    ResponseResult likes_behavior(LikeDto dto);

    ResponseResult read_behavior(ReadDto dto);
}
