package com.heima.model.article.dtos;

import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class CollectionDto {
    Long entryId;
    Short operation;
    Integer publishedTime;
    Short type;
    public Date getPublishedYear() throws ParseException {
        // 根据前端传入的整数创建日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
       return dateFormat.parse(publishedTime.toString());
    }
}
