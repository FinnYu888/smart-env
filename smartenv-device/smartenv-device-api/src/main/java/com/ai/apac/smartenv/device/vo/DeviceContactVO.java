package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.entity.DeviceContact;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName DeviceContactVO
 * @Desc 设备联系人
 * @Author ZHANGLEI25
 * @Date 2020/2/26 16:06
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceContactVO对象", description = "设备联系人信息")
public class DeviceContactVO extends DeviceContact {
    private static final long serialVersionUID = 1L;

}
