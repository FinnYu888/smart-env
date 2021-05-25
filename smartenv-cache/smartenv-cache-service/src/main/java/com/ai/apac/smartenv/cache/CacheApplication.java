package com.ai.apac.smartenv.cache;

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
 * 缓存模块启动器
 * @author qianlong
 */
@EnableBladeFeign
@Configuration
@ComponentScan({ "com.ai.apac" })
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableCaching
@SpringCloudApplication
public class CacheApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_CACHE_NAME, CacheApplication.class, args);
    }
}
