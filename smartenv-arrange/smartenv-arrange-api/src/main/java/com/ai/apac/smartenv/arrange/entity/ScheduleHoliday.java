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

import org.springblade.core.tenant.mp.TenantEntity;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 排班节假日表实体类
 *
 * @author Blade
 * @since 2020-02-11
 */
@Data
@TableName("ai_schedule_holiday")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScheduleHoliday对象", description = "排班节假日表")
public class ScheduleHoliday extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* id
	*/
		@ApiModelProperty(value = "id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 节假日名称
	*/
		@ApiModelProperty(value = "节假日名称")
		private String holidayName;
	/**
	* 节假日,1是指定日期、2是按周重复
	*/
		@ApiModelProperty(value = "节假日,1是指定日期、2是按周重复")
		private String holidayType;
	/**
	* 节假日起始时间
	*/
		@ApiModelProperty(value = "节假日起始时间")
		private LocalDate holidayBeginDate;
	/**
	* 节假日结束时间
	*/
		@ApiModelProperty(value = "节假日结束时间")
		private LocalDate holidayEndDate;
	/**
	* 按周期，具体周几，类似0,0,0,0,0,1,0指周六
	*/
		@ApiModelProperty(value = "按周期，具体周几，类似0,0,0,0,0,1,0指周六")
		private String holidayPeriod;
	/**
	* 假期描述
	*/
		@ApiModelProperty(value = "假期描述")
		private String holidayDescripition;


}
