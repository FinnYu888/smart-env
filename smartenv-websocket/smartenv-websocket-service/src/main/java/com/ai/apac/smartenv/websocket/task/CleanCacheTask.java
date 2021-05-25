package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/11 6:52 下午
 **/
@Configuration
@EnableScheduling
@Slf4j
public class CleanCacheTask {

    @Autowired
    private IWebSocketTaskService webSocketTaskService;

    /**
     * 每天凌晨1点清理缓存中的websocket相关的任务
     */
    @Scheduled(cron = "* * 1 * * ?")
    public void cleanAllTask(){
        log.info("执行清理websocket 残留任务 ");
        webSocketTaskService.clearTask();
    }
}
