package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleSercive;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {
    @Autowired
    ApArticleSercive apArticleSercive;
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto)
    {

        return apArticleSercive.load(dto, (short) 1);
    }
    @PostMapping("/loadmore")
    public ResponseResult loadmore(@RequestBody ArticleHomeDto dto)
    {

        return apArticleSercive.load(dto, (short) 1);
    }
    @PostMapping("/loadnew")
    public ResponseResult loadnew(@RequestBody ArticleHomeDto dto)
    {

        return apArticleSercive.load(dto, (short) 2);
    }
    @PostMapping("/load_article_behavior")
    public ResponseResult load_article_behavior(@RequestBody ArticleInfoDto dto)
    {
        return apArticleSercive.load_article_behavior(dto);
    }

}
