package com.ai.apac.smartenv.alarm;

import com.ai.apac.smartenv.alarm.mq.AlarmProductSource;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/10 5:21 下午
 **/
@EnableBladeFeign
@Configuration
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@SpringCloudApplication
@EnableWebSocket
@EnableBinding(AlarmProductSource.class)
public class AlarmApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_ALARM_NAME, AlarmApplication.class, args);
    }
}
