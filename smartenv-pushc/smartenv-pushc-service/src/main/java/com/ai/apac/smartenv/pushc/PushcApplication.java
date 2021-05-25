package com.ai.apac.smartenv.pushc;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.pushc.mq.PushcProducerSource;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/24 7:43 下午
 **/

@EnableBladeFeign
@Configuration
@ComponentScan({ "com.ai.apac" })
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv.pushc"})
@EnableBinding(PushcProducerSource.class)
@SpringCloudApplication
public class PushcApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_PUSHC_NAME, PushcApplication.class, args);
    }
}
