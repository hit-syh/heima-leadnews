package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.CollectionDto;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCollerctionService extends IService<ApCollection> {
    ResponseResult collection_behavior(CollectionDto dto);
}
