package com.ai.apac.smartenv.omnic;

import cn.hutool.core.thread.ExecutorBuilder;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.mq.OmnicConsumerSource;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhanglei25
 * @description //TODO
 * @Date 2020/1/10 5:33 下午
 **/
@EnableBladeFeign
@Configuration
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@SpringCloudApplication
@EnableAsync
@EnableWebSocket
@EnableBinding(OmnicProducerSource.class)
public class OmnicApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_OMNIC_NAME, OmnicApplication.class, args);
    }

    /**
     * 创建线程池
     * 初始5个线程
     * 最大10个线程
     * 有界等待队列，最大等待数是100
     * @return
     */
    @Bean
    public ExecutorService buildsExecutor(){
        ExecutorService executor = ExecutorBuilder.create()
                .setCorePoolSize(5)
                .setMaxPoolSize(10)
                .setWorkQueue(new LinkedBlockingQueue<>(100))
                .build();
        return executor;
    }
}
