package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.redis.CacheService;
import com.heima.model.common.constants.ScheduleConstants;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class TaskServiceImp implements TaskService {
    @Override
    public long addTask(Task task) {

        //1.添加任务到数据库
        boolean success =addTaskToDB(task);
        //2.添加任务到redis
        if(success)
        {
            addTaskToRedis(task);
        }
        return task.getTaskId();
    }

    @Override
    public boolean cancelTask(long taskId) {

        try {
            //删除任务更新日志
            Task task=updateDb(taskId,ScheduleConstants.CANCELLED);

            //删除redis中数据
            if(task!=null)
            {
                String key=task.getTaskType()+"_"+task.getPriority();
                if(task.getExecuteTime()<=System.currentTimeMillis()){
                    cacheService.lRemove(ScheduleConstants.TOPIC+key,0,JSON.toJSONString(task));
                }
                else {
                    cacheService.zRemove(ScheduleConstants.FUTURE+key,0,JSON.toJSONString(task));
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public Task poll(int type, int priority) {
        Task task=null;
        try {
            //从redis拉
            String key=type+"_"+priority;

            String task_json=cacheService.lRightPop(ScheduleConstants.TOPIC+key);
            task = JSON.parseObject(task_json, Task.class);
            //修改db

            updateDb(task.getTaskId(),ScheduleConstants.EXECUTED);
        } catch (Exception e) {

        }
        return task;
    }

    private Task updateDb(long taskId, int status) {
        Task task = null;

        try {
            //删除任务
            taskinfoMapper.deleteById(taskId);

            //删除日志
            TaskinfoLogs taskinfoLogs=taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs,task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.info("task updateDb exception taskId={}",taskId);
        }
        return task;
    }

    @Autowired
    CacheService  cacheService;
    private void addTaskToRedis(Task task) {
        String key=task.getTaskType()+"_"+task.getPriority();
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        long nextScheduleTime = calendar.getTimeInMillis();
        //2.1如果任务的执行时间小于等于当前时间，存入list
        if(task.getExecuteTime()<=System.currentTimeMillis())
        {
            cacheService.lLeftPush(ScheduleConstants.TOPIC+key, JSON.toJSONString(task));
        }
        //2.2如果任务执行时间大于当前时间&&小于等于预设时间（未来5min）存入zset
        else if (task.getExecuteTime()<=nextScheduleTime) {
            cacheService.zAdd(ScheduleConstants.FUTURE+key,JSON.toJSONString(task), task.getExecuteTime());

        }
    }

    @Autowired
    TaskinfoMapper taskinfoMapper;
    @Autowired
    TaskinfoLogsMapper taskinfoLogsMapper;
    private boolean addTaskToDB(Task task) {

        try {
            //保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task,taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);
            task.setTaskId(taskinfo.getTaskId());
            //保存日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo,taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 定时刷新
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh(){
        log.info("未来数据定时刷新--定时任务");
        //获取所有未来数据keys
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        for (String futureKey : futureKeys) {
            String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];
            Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
            //同步数据
            if(!tasks.isEmpty()){
                cacheService.refreshWithPipeline(futureKey,topicKey,tasks);
                log.info("成功将{}刷新到了{}",futureKey,topicKey);
            }

        }
    }
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * ?")
    public void reloadData()
    {
        //清理缓存中的数据
        clearCache();
        //查询符合条件的任务
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        List<Taskinfo> taskinfoList = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));
        for (Taskinfo taskinfo : taskinfoList) {
            Task task = new Task();
            BeanUtils.copyProperties(taskinfo,task);
            task.setExecuteTime(taskinfo.getExecuteTime().getTime());
            addTaskToRedis(task);
        }
        log.info("数据库的任务同步到了redis");

        //添加到redis
    }
    public void clearCache()
    {
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        cacheService.delete(topicKeys);
        cacheService.delete(futureKeys);
    }

}
