package com.ai.apac.smartenv.address;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/29 6:35 下午
 **/
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
public class AddressApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_ADDR_NAME, AddressApplication.class, args);
    }
}
