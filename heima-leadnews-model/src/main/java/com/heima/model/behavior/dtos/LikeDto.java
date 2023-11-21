package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class LikeDto {
    private Long articleId;
    private Short type;
    private Short operation;

}
