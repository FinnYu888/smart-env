package com.ai.apac.smartenv.person;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/14 11:57 上午
 **/
@EnableBladeFeign
@Configuration
//@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv.person"})
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@SpringCloudApplication
public class PersonApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_PERSON_NAME, PersonApplication.class, args);
    }
}
