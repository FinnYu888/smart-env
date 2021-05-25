package com.ai.apac.smartenv.oss;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/6 4:18 下午
 **/
@EnableBladeFeign
@Configuration
@ComponentScan({ "com.ai.apac" })
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@SpringCloudApplication
public class OssApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_OSS_NAME, OssApplication.class, args);
    }
}
