package com.ai.apac.smartenv.omnic.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;
import sun.dc.pr.PRError;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: RealTimePosition
 * @Description: 实时位置
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/6
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/6  16:52    panfeng          v1.0.0             修改原因
 */
@Data
@ToString
public class TrackPositionDto {

    private String deviceId;
    private Position position;
    private List<Position> tracks;


    private Integer rounds;

    private Statistics statistics;


    @Data
    @ToString
    public static class Position {
        private String lng;
        private String lat;
        private String speed;
        private String eventTime;
        private String distance;
        //油量剩余
        private String oilLeft = "0.0";
        // TODO 水量字段待大数据确定
        private String waterLeft = "0.0";


        //大数据传过来的accstatus
        private String accStatus;

        private String mileage;


        //最后在线时间 应用平台自己设定
        private String lastOnlineTime;

        //工作状态，应用平台自己设定
        private String workStatus;

        //坐标所在地址，应用平台自己设定
        private String address;

    }


    @Data
    public static class Statistics {
        String avgSpeed;
        String totalCount;
        String totalDistance;
        String maxSpeed;
        String totalDistanceWork;
    }
}
