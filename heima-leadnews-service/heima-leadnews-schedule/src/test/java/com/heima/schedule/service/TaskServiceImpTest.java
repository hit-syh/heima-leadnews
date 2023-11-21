package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceImpTest {
    @Autowired
    TaskService taskService;
    @Test
    public void addTask() {
        for (int i=0;i<5;i++)
        {
            Task task = new Task();
            task.setTaskType(100+i);
            task.setPriority(50);
            task.setParameters("Task test".getBytes());
            task.setExecuteTime(new Date().getTime()+500*i);
            System.out.println(taskService.addTask(task));
        }
//        Task task = new Task();
//        task.setTaskType(100);
//        task.setPriority(50);
//        task.setParameters("Task test".getBytes());
//        task.setExecuteTime(new Date().getTime());
//        System.out.println(taskService.addTask(task));
    }
    @Test
    public void cancelTask() {
        System.out.println(taskService.cancelTask(1713878086334607361L));
    }

    @Autowired
    TaskinfoLogsMapper taskinfoLogsMapper;
    @Test
    public void test()
    {
//        TaskinfoLogs taskinfoLogs=taskinfoLogsMapper.selectById(1713875022282309633L);
//        Task task = new Task();
//        BeanUtils.copyProperties(taskinfoLogs,task);
//        task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        Date date = new Date(new Date().getTime());
        System.out.println(date);
    }


    @Test
    public void poll() {

        System.out.println( taskService.poll(100,50));
    }
}