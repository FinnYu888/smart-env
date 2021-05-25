package com.ai.apac.smartenv.ops;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/20 8:26 下午
 **/
@EnableBladeFeign
@SpringCloudApplication
public class OpsToolApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_OPS_TOOL, OpsToolApplication.class, args);
    }
}
