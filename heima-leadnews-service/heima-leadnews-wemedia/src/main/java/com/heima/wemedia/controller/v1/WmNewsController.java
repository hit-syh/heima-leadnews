package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.dtos.WmAuditDto;
import com.heima.model.wemedia.dtos.WmNewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    WmNewsService wmNewsService;
    @PostMapping("/list")
    public ResponseResult finList(@RequestBody WmNewsPageReqDto dto)
    {
        return wmNewsService.finList(dto);
    }
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submitNews(dto);
    }
    @PostMapping("/down_or_up")
    public ResponseResult dowmOrUp(@RequestBody  WmNewsDto dto)
    {
        return wmNewsService.downOrUp(dto);
    }
    @PostMapping("/list_vo")
    public ResponseResult list_vo(@RequestBody WmAuditDto dto){
        return wmNewsService.list_vo(dto);
    }
    @GetMapping("/one_vo/{id}")
    public ResponseResult one_vo(@PathVariable Integer id)
    {
        return wmNewsService.one_vo(id);
    }
    @PostMapping("/auth_pass") ResponseResult authPass(@RequestBody WmNews wmnews){
        return wmNewsService.authPass(wmnews);
    }
    @PostMapping("/auth_fail") ResponseResult authFail(@RequestBody WmNewsAuthDto dto){
        return wmNewsService.authFail(dto);
    }
//    @PostMapping("/auth_fail")
//    public ResponseResult

}
