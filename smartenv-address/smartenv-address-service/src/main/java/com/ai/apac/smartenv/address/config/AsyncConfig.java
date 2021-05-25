package com.ai.apac.smartenv.address.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AsyncConfig
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/3
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/3  15:24    panfeng          v1.0.0             修改原因
 */
@EnableAsync
@Configuration
public class AsyncConfig {


    @Bean(name = "trackThreadPool")
    public Executor getScorePoolTaskExecutor() {
        ExecutorService taskExecutor= Executors.newFixedThreadPool(10);
        return taskExecutor;
    }



}
