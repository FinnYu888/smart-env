package com.ai.apac.smartenv.device.dto.mongo;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class GreenScreenDeviceDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String tenantId;
    String deviceId;
    String greenAreaId;
    String deviceCode;
    List<GreenScreenDeviceDTLDTO> indexList;
}
