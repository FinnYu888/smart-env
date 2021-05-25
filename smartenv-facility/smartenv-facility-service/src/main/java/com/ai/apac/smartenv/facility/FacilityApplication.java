package com.ai.apac.smartenv.facility;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/10 5:35 下午
 **/
@EnableFeignClients({"org.springblade","com.ai.apac.smartenv"})
//@EnableFeignClients({"org.springblade","com.ai.apac.smartenv.facility"})
@MapperScan({"org.springblade.**.mapper.**","com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
@Configuration
@EnableAsync
public class FacilityApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_FACILITY_NAME, FacilityApplication.class, args);
    }
}
