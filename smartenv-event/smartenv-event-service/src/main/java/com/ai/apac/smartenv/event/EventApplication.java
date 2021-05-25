package com.ai.apac.smartenv.event;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/10 5:34 下午
 **/
@EnableBladeFeign
@Configuration
@ComponentScan({ "com.ai.apac" })
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableCaching
@SpringCloudApplication
public class EventApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_EVENT_NAME, EventApplication.class, args);
    }
}
