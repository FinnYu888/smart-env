package com.ai.apac.smartenv.device.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * @ClassName DeviceStatusVO
 * @Desc 设备实时状态
 * @Author ZHANGLEI25
 * @Date 2020/2/15 16:58
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceRelVO对象", description = "DeviceRelVO对象")
public class DeviceStatusVO extends TenantEntity {
    String deviceId;
    String statusCode;
    String statusName;
}
