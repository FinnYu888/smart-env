package com.ai.apac.smartenv.device.dto.third;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeviceInfoParamDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/6/3 14:50
 * @Version 1.0
 */
@Data
public class DeviceInfoParamDTO implements Serializable {
    private String id;//设备的 GUID

    private String simNo;//设备的 sim 卡号，12 位(必需)

    private String deviceID;//设备 id，7 位(必需)

}
