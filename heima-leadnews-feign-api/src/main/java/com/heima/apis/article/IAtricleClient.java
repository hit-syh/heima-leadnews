package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallBack;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-article",fallback = IArticleClientFallBack.class)
public interface IAtricleClient {
    @PostMapping("/api/v1/acticle/sava")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto);
}
