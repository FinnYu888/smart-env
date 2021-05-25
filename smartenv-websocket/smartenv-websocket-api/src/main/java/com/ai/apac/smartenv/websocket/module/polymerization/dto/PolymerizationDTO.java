package com.ai.apac.smartenv.websocket.module.polymerization.dto;

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class PolymerizationDTO {

    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private String objID;
    private String objName;
    /**
     * @see WebSocketConsts.PolymerizationType
     */
    private Integer objType;
    @Latitude
    private String objLat;
    @Longitude
    private String objLng;
    private Long gpsTime;
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long categoryId;
    private String categoryName;


    /**
     * 0:无告警 1 ：有告警
     */
    private Boolean isAlarm;
    private String alarmName;
    private Integer status;
    private String statusName;
    private Integer workStatus;
    private String workStatusName;
    private Long regionId;
    private String regionName;
    private String objIcon;

    // 事件 start
    private String reportTime;
    private String reportPersonName;
    private String areaManageName;
    // 事件 end

}
