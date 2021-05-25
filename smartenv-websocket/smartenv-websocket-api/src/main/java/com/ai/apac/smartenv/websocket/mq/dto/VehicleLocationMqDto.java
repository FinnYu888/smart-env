package com.ai.apac.smartenv.websocket.mq.dto;

import lombok.Data;

@Data
public class VehicleLocationMqDto {
    private String id;
    private String phone;
    private String device_id;
    private String latitude;
    private String longitude;
    private String altitude;
    private String speed;
    private String directon;
    private String gps_time;
    private String adas_alarm_info;
    private String dsm_alarm_info;
    private String gps_recv_time;
    private String vehicle_plate;
    private String tenant_id;
    private String Mileage;
    private String status;
    private String alarm;
    private String annex_uuid;
    private String Oil;

}
