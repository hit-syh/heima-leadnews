package com.heima.wemedia.service;

import java.util.Date;

public interface WmnewsTaskService {
    /**
     * 添加任务到延迟队列中
     * @param id
     * @param publishTime
     */
    public void addNewsToTask(Integer id, Date publishTime);


    /**
     * 消费任务
     */
    public void scanNewsByTask();
}
