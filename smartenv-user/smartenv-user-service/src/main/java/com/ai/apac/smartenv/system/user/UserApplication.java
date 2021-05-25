package com.ai.apac.smartenv.system.user;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/8 9:03 下午
 **/
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
public class UserApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_USER_NAME, UserApplication.class, args);
    }

}
