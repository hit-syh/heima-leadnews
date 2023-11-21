package com.heima.wemedia.controller.v1;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {
    @Autowired
    WmChannelService wmChannelService;
    @GetMapping("/channels")
    public ResponseResult finall(){
        return wmChannelService.findAll();
    }
    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmChannelDto dto){
        return wmChannelService.list(dto);
    }
    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmChannel wmChannel){

        return wmChannelService.update(wmChannel);
    }
    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmChannel wmChannel){
        return wmChannelService.save_(wmChannel);
    }
    @GetMapping("/del/{id}")
    public ResponseResult del(@PathVariable Integer id){
        return wmChannelService.del(id);
    }
}
