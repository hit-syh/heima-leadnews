package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.WmAuditDto;
import com.heima.model.wemedia.dtos.WmNewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {

    public ResponseResult finList(WmNewsPageReqDto dto);
    public ResponseResult submitNews(WmNewsDto dto);

    ResponseResult downOrUp(WmNewsDto dto);

    ResponseResult list_vo(WmAuditDto dto);

    ResponseResult one_vo(Integer id);

    ResponseResult authPass(WmNews wmnews);

    ResponseResult authFail(WmNewsAuthDto dto);


}
