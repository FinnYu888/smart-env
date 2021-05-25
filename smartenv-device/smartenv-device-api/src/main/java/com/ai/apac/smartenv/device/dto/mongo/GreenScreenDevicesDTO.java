package com.ai.apac.smartenv.device.dto.mongo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GreenScreenDeviceDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 16:21
 * @Version 1.0
 */
@Data
public class GreenScreenDevicesDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String tenantId;
    List<GreenScreenDeviceDTO> greenScreenDevices;
}
