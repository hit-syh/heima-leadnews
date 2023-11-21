package com.heima.model.article.dtos;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class ArticleInfoDto {
    Long articleId;
    Integer authorId;
}
