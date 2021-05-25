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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 敏捷排班表实体类
 *
 * @author Blade
 * @since 2020-02-12
 */
@Data
@TableName("ai_schedule_object")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScheduleObject对象", description = "敏捷排班表")
public class ScheduleObject extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 敏捷排班ID
	*/
	@ApiModelProperty(value = "敏捷排班ID")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long id;
	/**
	* 排班ID
	*/
	@ApiModelProperty(value = "排班ID")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long scheduleId;
	/**
	* 实体类型，1人2车
	*/
	@ApiModelProperty(value = "实体类型，1车2人")
	private String entityType;
	/**
	* 实体ID，车或人
	*/
	@ApiModelProperty(value = "实体ID，车或人")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long entityId;
	/**
	* 排班日期
	*/
	@ApiModelProperty(value = "排班日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate scheduleDate;
	/**
	* 新建排班时开始排班日期
	*/
	@ApiModelProperty(value = "新建排班时开始排班日期")
	private LocalDate scheduleBeginDate;
	/**
	* 新建排班时开始排班日期
	*/
	@ApiModelProperty(value = "新建排班时开始排班日期")
	private LocalDate scheduleEndDate;
	/**
	 * 新建排班时开始排班日期
	 */
	@ApiModelProperty(value = "是否是临时排班，1是，0或空不是")
	private Integer temporary;
	
}
