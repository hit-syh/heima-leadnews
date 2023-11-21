package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.FollowDto;

public interface FanFollowService {
    public ResponseResult userFollow(FollowDto dto) ;
}
