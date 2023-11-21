package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;
import org.springframework.scheduling.annotation.Async;

public interface ApUserSearchService {
    /**
     * 保存用户搜索记录
     * @param keword
     * @param userId
     */
    @Async
    public void insert(String keword,Integer userId);

    ResponseResult load();

    ResponseResult del(HistorySearchDto dto);
}
