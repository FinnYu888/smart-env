package com.ai.apac.smartenv.device.dto.mongo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GreenScreenDeviceDTO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/7/22 16:21
 * @Version 1.0
 */
@Data
public class GreenScreenDeviceDTLDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    String divisor;
    String finalIndexValue;
    String index;
    String indexName;
    String indexValue;
    String unit;
}
