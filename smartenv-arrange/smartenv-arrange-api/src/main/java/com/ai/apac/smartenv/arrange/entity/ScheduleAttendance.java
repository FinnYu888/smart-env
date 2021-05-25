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

import java.sql.Timestamp;
import java.util.Date;

/**
 * 打卡记录表实体类
 *
 * @author Blade
 * @since 2020-05-12
 */
@Data
@TableName("ai_schedule_attendance")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScheduleAttendance对象", description = "打卡记录表")
public class ScheduleAttendance extends TenantEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 敏捷排班ID
     */
	@JsonSerialize(
			using = ToStringSerializer.class
	)
    @ApiModelProperty(value = "敏捷排班ID")
    private Long id;
    /**
     * 排班ID
     */
    @ApiModelProperty(value = "排班ID")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
    private Long scheduleObjectId;



	@ApiModelProperty(value = "实体在考勤日期所属部门")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long deptId;
	@ApiModelProperty(value = "实体在考勤日期所属部门名称")
	private String deptName;
	@ApiModelProperty(value = "实体在考勤日期所属片区")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long regionId;
	@ApiModelProperty(value = "实体在考勤日期所属片区名称")
	private String regionName;
	@ApiModelProperty(value = "实体在考勤日期工作区域")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long workareaId;
	@ApiModelProperty(value = "实体在考勤日期的工作区域名称")
	private String workareaName;
	@ApiModelProperty(value = "考勤对象ID")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long entityId;
	@ApiModelProperty(value = "考勤对象类型")
	private Long entityType;
	@ApiModelProperty(value = "考勤对象名称")
	private String entityName;
	@ApiModelProperty(value = "考勤对象分类")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long entityCategoryId;
	@ApiModelProperty(value = "考勤对象分类名称")
	private String entityCategoryName;



    /**
     * 上班时间
     */
    @ApiModelProperty(value = "上班时间")
    private Date workStartTime;
    /**
     * 下班时间
     */
    @ApiModelProperty(value = "下班时间")
    private Date workEndTime;

	@ApiModelProperty(value = "打卡状态，1： 已打卡，0：未打卡")
	private Long attendanceStatus;


}
