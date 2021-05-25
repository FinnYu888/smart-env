package com.ai.apac.smartenv.inventory;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.cloud.feign.EnableBladeFeign;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/1/10 5:36 下午
 **/
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv.inventory"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@EnableBladeFeign
@SpringCloudApplication
public class InventoryApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_INVENTORY_NAME, InventoryApplication.class, args);
    }
}
