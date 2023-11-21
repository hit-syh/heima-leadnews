package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;
import org.apache.kafka.common.protocol.types.Field;

public interface ArticleFreemarkerService {
    public void buildMinIO(ApArticle article, String content);
}
