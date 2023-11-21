package com.heima.article.feign;

import com.heima.apis.article.IAtricleClient;
import com.heima.article.service.ApArticleSercive;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleClient implements IAtricleClient {
    @Autowired
    ApArticleSercive apArticleSercive;
    @Override
    @PostMapping("/api/v1/acticle/sava")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto)
    {
        return apArticleSercive.savaArticle(dto);
    }
}
