package com.heima.model.user.dtos;

import lombok.Data;

@Data
public class FollowDto {
    Long articleId;
    Integer authorId;
    Short operation;
}
