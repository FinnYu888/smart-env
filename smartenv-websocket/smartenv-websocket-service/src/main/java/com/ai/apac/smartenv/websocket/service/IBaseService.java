package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.person.entity.Person;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/19 9:14 上午
 **/
public interface IBaseService {


    /**
     * 获取推送线程池
     * @return
     */
    ExecutorService getPushExecutor();

    /**
     * 获取任务执行线程池
     * @return
     */
    ExecutorService getTaskExecutor();
}
