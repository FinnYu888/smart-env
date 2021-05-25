package com.ai.apac.smartenv.websocket.module.person.vo;

import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class PersonPositionVO extends WebSocketDTO implements Serializable {
    //人员ID
    private String personId;
    private String personName;

    private String lat;
    private String lng;
    //当前人员在session中的位置
    private Integer index;


    private Integer status;
    private String statusName;
    private String icon;
    private String deviceCode;
    private Long deviceId;



}

