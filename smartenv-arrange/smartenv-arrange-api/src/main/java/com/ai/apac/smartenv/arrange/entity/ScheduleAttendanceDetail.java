/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.arrange.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.Date;

/**
 * 打卡详情表实体类
 *
 * @author Blade
 * @since 2020-05-12
 */
@Data
@TableName("ai_schedule_attendance_detail")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScheduleAttendanceDetail对象", description = "打卡详情表")
public class ScheduleAttendanceDetail extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long id;
    /**
     * 排班ID
     */
    @ApiModelProperty(value = "排班ID")
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

    @ApiModelProperty(value = "打卡状态，1： 已打卡，0：未打卡")
    private Long attendanceStatus;

    @ApiModelProperty(value = "排班纬度")
    private String scheduleAreaLat;

    @ApiModelProperty(value = "排班经度")
    private String scheduleAreaLng;

    @ApiModelProperty(value = "上下班标记。1：上班  2：下班")
    private Long goOffWorkFlag;

    @ApiModelProperty(value = "排班时间/应该打卡时间")
    private Date scheduleTime;


}
