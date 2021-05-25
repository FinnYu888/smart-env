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
package com.ai.apac.smartenv.omnic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "", description = "设置考勤查询")
public class QScheduleObject extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "排班ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long scheduleId;
	
	@ApiModelProperty(value = "实体类型，1车2人")
	private String entityType;
	
	@ApiModelProperty(value = "实体ID，车或人")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long entityId;
	
	@ApiModelProperty(value = "排班时开始排班日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate scheduleBeginDate;
	
	@ApiModelProperty(value = "排班时开始排班日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate scheduleEndDate;
	
	@ApiModelProperty(value = "车牌号")
	private String plateNumber;
	
	@ApiModelProperty(value = "车辆类型")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long entityCategoryId;

	@ApiModelProperty(value = "车辆部门id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long vehicleDeptId;
	
	@ApiModelProperty(value = "车辆部门名称")
	private String vehicleDeptName;
	
	@ApiModelProperty(value = "姓名")
	private String personName;
	
	@ApiModelProperty(value = "工号")
	private String jobNumber;
	
	@ApiModelProperty(value = "员工所属部门")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long personDeptId;

	@ApiModelProperty(value = "员工所属部门")
	private String personDeptName;
	
	@ApiModelProperty(value = "排班名称")
	private String scheduleName;
	
	@ApiModelProperty(value = "周一")
	private Integer scheduleMonday;

	@ApiModelProperty(value = "周二")
	private Integer scheduleTuesday;

	@ApiModelProperty(value = "周三")
	private Integer scheduleWednesday;

	@ApiModelProperty(value = "周四")
	private Integer scheduleThursday;

	@ApiModelProperty(value = "周五")
	private Integer scheduleFriday;

	@ApiModelProperty(value = "周六")
	private Integer scheduleSaturday;

	@ApiModelProperty(value = "周日")
	private Integer scheduleSunday;
	
	@ApiModelProperty(value = "班次起始时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date scheduleBeginTime;

	@ApiModelProperty(value = "班次结束时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date scheduleEndTime;

	@ApiModelProperty(value = "休息起始时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date breaksBeginTime;

	@ApiModelProperty(value = "休息结束时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date breaksEndTime;
	
}
