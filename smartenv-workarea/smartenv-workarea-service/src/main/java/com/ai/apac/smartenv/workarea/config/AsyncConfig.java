package com.ai.apac.smartenv.workarea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "workareaThreadPool")
    public Executor getScorePoolTaskExecutor() {
        ExecutorService taskExecutor= Executors.newFixedThreadPool(10);
        return taskExecutor;
    }

}
