package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;


import java.util.List;

public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     *
     * @param dto
     * @param type 1加载更多 2加载最新
     * @return
     */
    public List<ApArticle> loadArticleList(ArticleHomeDto dto,short type);

}
