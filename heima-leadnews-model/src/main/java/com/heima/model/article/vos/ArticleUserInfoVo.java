package com.heima.model.article.vos;

import lombok.Data;

@Data
public class ArticleUserInfoVo {
    Boolean islike=false;
    Boolean isunlike=false;
    Boolean iscollection=false;
    Boolean isfollow=false;

}
