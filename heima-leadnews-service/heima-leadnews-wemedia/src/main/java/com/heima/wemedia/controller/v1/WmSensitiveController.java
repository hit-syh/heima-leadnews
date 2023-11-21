package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {
    @Autowired
    private WmSensitiveService wmSensitiveService;
    @PostMapping("/list")
    public ResponseResult pageList(@RequestBody WmSensitiveDto dto)
    {
        return wmSensitiveService.pageList(dto);
    }
    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable Integer id)
    {
        return wmSensitiveService.del(id);
    }
    @PostMapping("/save")
    public ResponseResult save_(@RequestBody WmSensitive wmSensitive){

        return  wmSensitiveService.save_(wmSensitive);
    }
    @PostMapping("/update")
    public ResponseResult update_(@RequestBody WmSensitive wmSensitive){

        return  wmSensitiveService.update_(wmSensitive);
    }


}
