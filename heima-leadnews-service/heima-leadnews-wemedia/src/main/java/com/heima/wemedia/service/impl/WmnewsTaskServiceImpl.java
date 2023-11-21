package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmnewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;


@Service
@Slf4j
public class WmnewsTaskServiceImpl implements WmnewsTaskService {
    @Autowired
    private IScheduleClient scheduleClient;
    @Override
    @Async
    public void addNewsToTask(Integer id, Date publishTime) {

        log.info("添加任务{}到延迟服务中----begin",id);
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));
        scheduleClient.addTask(task);
        log.info("添加任务{}到延迟服务中----end",id);
    }
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;
    @Override
    @Scheduled(fixedRate = 1000)
    public void scanNewsByTask() {
        try{
            ResponseResult responseResult = scheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
            if(responseResult.getCode()==200 &&responseResult.getData()!=null)
            {

                Task task = JSON.parseObject(JSON.toJSONString(responseResult.getData()), Task.class);
                WmNews wmNews = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);
                log.info("消费任务，审核文章 :{}",wmNews.getId());
                wmNewsAutoScanService.autoScanWmNews(wmNews.getId());

            }
        }catch (Exception e)
        {

        }


    }
}
