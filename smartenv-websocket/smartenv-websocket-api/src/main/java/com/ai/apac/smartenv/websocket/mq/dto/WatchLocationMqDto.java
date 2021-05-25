package com.ai.apac.smartenv.websocket.mq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WatchLocationMqDto {


    private static final long serialVersionUID = 1L;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 设备厂商
     */
    private String vendor;

    /**
     * 上报时间
     */
    private String gpsTime;

    /**
     * 经度标识,E表示东经,W表示西经
     */
    private String lngType;

    /**
     * 经度
     */
    private String lng;

    /**
     * 纬度标识,N表示北纬,S表示南纬.
     */
    private String latType;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 速度,单位为公里/小时
     */
    private String speed;

    /**
     * 如果是152,则方向在152度
     */
    private String direction;

    /**
     * 电量百分比
     */
    private String battery;

    /**
     * 计步数
     */
    private Integer stepCount;

    /**
     * 手表状态
     * 1 - 正常
     */
    private Integer deviceStatus;

    /**
     * 手表状态名称
     */
    private String deviceStatusName;


}
