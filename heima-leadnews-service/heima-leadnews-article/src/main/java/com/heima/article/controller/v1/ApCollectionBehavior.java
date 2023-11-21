package com.heima.article.controller.v1;

import com.heima.article.service.ApCollerctionService;
import com.heima.model.article.dtos.CollectionDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApCollectionBehavior {
    @Autowired
    ApCollerctionService apCollerctionService;
    @PostMapping("/collection_behavior")
    public ResponseResult collection_behavior(@RequestBody CollectionDto dto)
    {
        return apCollerctionService.collection_behavior(dto);
    }
}
