package com.ai.apac.smartenv.wechat;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/2 3:55 下午
 **/
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv.wechat"})
@ComponentScan({ "com.ai.apac" })
@MapperScan({"com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
public class WeChatApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_WECHAT_NAME, WeChatApplication.class, args);
    }
}
