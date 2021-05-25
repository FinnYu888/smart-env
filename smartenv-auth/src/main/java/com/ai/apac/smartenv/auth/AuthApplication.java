package com.ai.apac.smartenv.auth;


import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 用户认证服务器
 *
 * @author qianlong
 */
@EnableBladeFeign
@SpringCloudApplication
public class AuthApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_AUTH_NAME, AuthApplication.class, args);
    }

}
