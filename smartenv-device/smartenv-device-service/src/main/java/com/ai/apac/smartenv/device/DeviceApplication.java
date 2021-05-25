package com.ai.apac.smartenv.device;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: DeviceApplication
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/1/10
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/1/10  15:50    panfeng          v1.0.0             修改原因
 */

@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
public class DeviceApplication {


    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_DEVICE_NAME, DeviceApplication.class, args);
    }
}
