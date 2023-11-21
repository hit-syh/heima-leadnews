package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

public interface ApArticleSercive extends IService<ApArticle> {
    /**
     * 加载文章列表
     * @param dto
     * @param type
     * @return
     */
    public ResponseResult load(ArticleHomeDto dto,short type);

    public ResponseResult savaArticle(ArticleDto dto);

    ResponseResult load_article_behavior(ArticleInfoDto dto);
}
