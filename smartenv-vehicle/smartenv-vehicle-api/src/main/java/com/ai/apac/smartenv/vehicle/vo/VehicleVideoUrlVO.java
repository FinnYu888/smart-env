package com.ai.apac.smartenv.vehicle.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName VehicleLiveVideoUrlVO
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/6/4 10:25
 * @Version 1.0
 */
@Data
public class VehicleVideoUrlVO implements Serializable {
    private String deviceId;
    private String channelSeq;
    private String channelName;
    private String url;
    private int videoErrCode;
}
