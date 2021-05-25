package com.ai.apac.smartenv.websocket.config;

import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

@EnableAsync
@Configuration
public class ExcutorConfig {


    @Bean("pushExecutor")
    public ExecutorService pushExecutor(){
        ExecutorService taskExecutor=new ThreadPoolExecutor(200,500,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(10),new NamedThreadFactory("smartenv-push-executor-",false));
        return taskExecutor;
    }

    @Bean("websocketTaskExecutor")
    public ExecutorService taskExecutor(){
        ExecutorService taskExecutor=new ThreadPoolExecutor(200,500,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(5),new NamedThreadFactory("smartenv-task-executor-",false));
        return taskExecutor;
    }



}
