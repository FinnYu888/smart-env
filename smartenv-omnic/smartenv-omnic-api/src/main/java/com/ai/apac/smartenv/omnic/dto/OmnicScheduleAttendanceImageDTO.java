package com.ai.apac.smartenv.omnic.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: OmnicScheduleAttendanceImageDTO
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/5/15
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/5/15 15:19     panfeng          v1.0.0             修改原因
 */
@Data
public class OmnicScheduleAttendanceImageDTO {


    /**
     * 排班ID
     */
    @ApiModelProperty(value = "排班ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long scheduleAttendanceId;
    /**
     * 图片上传纬度
     */
    @ApiModelProperty(value = "图片上传纬度")
    private String lat;
    /**
     * 图片上传经度
     */
    @ApiModelProperty(value = "图片上传经度")
    private String lng;
    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String imagePath;

    @ApiModelProperty(value = "上传时间")
    private Date uploadTime;

    @ApiModelProperty(value = "上传地址")
    private String address;

    @ApiModelProperty(value = "打卡状态，1： 已打卡，0：未打卡")
    private Long attendanceStatus;


    @ApiModelProperty(value = "上下班标记。1：上班  2：下班")
    private Long goOffWorkFlag;



}
