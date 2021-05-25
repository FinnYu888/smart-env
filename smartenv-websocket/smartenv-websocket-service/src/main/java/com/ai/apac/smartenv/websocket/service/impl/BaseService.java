package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Service
public class BaseService implements IBaseService {



    @Override
    public ExecutorService getPushExecutor(){
        return SpringUtil.getBean("pushExecutor", ExecutorService.class);
    }

    @Override
    public ExecutorService getTaskExecutor(){
        return SpringUtil.getBean("websocketTaskExecutor", ExecutorService.class);
    }

}
