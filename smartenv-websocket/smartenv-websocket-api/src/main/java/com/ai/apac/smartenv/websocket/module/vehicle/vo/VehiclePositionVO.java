package com.ai.apac.smartenv.websocket.module.vehicle.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class VehiclePositionVO extends WebSocketDTO implements Serializable {
    //人员ID
    private String vehicleId;
    private String plateNumber;


    private String lat;
    private String lng;



    private Integer status;
    private String statusName;
    private String icon;
    private String deviceCode;
    private Long deviceId;



}
