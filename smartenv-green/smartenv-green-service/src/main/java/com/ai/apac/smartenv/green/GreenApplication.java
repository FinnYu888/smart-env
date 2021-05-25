package com.ai.apac.smartenv.green;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/14 11:35 上午
 **/
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv.green"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
public class GreenApplication {
    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_GREEN_NAME, GreenApplication.class, args);
    }
}
