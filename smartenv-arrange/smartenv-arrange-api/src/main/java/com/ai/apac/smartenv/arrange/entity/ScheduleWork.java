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

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springblade.core.tenant.mp.TenantEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实际作业表实体类
 *
 * @author Blade
 * @since 2020-03-17
 */
@Data
@TableName("ai_schedule_work")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScheduleWork对象", description = "实际作业表")
public class ScheduleWork extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* id
	*/
	@ApiModelProperty(value = "id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	* 工作日
	*/
	@ApiModelProperty(value = "工作日")
	private LocalDate workDate;
	/**
	* 实体类型，1车2人
	*/
	@ApiModelProperty(value = "实体类型，1车2人")
	private String entityType;
	/**
	* 实体ID，车或人
	*/
	@ApiModelProperty(value = "实体ID，车或人")
	private Long entityId;
	/**
	* 实际上班时间
	*/
	@ApiModelProperty(value = "实际上班时间")
	private LocalTime workBeginTime;
	/**
	* 实际下班时间
	*/
	@ApiModelProperty(value = "实际下班时间")
	private LocalTime workEndTime;
	/**
	* 实际作业里程
	*/
	@ApiModelProperty(value = "实际作业里程")
	private BigDecimal workMileage;

}
