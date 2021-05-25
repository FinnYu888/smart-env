package com.ai.apac.smartenv.websocket.mq.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import lombok.Data;

import java.util.Date;

@Data
public class DevicePositionMessage {


    private String deviceCode;
    @Longitude
    private String lng;
    @Latitude
    private String lat;
    private Date time;
    private Long entityType;
    private Long entityId;
    private Object extProperties;



}
