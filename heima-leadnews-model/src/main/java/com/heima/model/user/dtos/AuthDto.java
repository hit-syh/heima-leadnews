package com.heima.model.user.dtos;

import lombok.Data;

@Data
public class AuthDto {
    private Integer id;
    private Integer page;
    private Integer size;
    private Integer status;
    private String msg;


}
