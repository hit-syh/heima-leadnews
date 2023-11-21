package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.pojos.ApArticleConfig;

import java.util.Map;

public interface ApAtricleConfigServices extends IService<ApArticleConfig> {
    void updatebyMap(Map map);
}
