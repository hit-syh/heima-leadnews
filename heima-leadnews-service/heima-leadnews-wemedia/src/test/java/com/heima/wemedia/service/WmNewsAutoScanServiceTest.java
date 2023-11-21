package com.heima.wemedia.service;

import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
@EnableAsync
public class WmNewsAutoScanServiceTest {
    @Autowired
    WmNewsAutoScanService wmNewsAutoScanService;
    @Test
    public void antuScanWmNews() {
        wmNewsAutoScanService.autoScanWmNews(6244);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}