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

import org.hibernate.validator.constraints.Length;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 排班表实体类
 *
 * @author Blade
 * @since 2020-02-11
 */
@Data
@TableName("ai_schedule")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Schedule对象", description = "排班表")
public class Schedule extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 排班ID
	*/
	@ApiModelProperty(value = "排班ID")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long id;
	/**
	* 排班名称
	*/
	@ApiModelProperty(value = "排班名称")
	@Length(max = 64, message = "班次名称长度不能超过64")
	@NotBlank(message = "需要输入班次名称")
	private String scheduleName;
	/**
	* 排班类型,1是周期性、2是弹性
	*/
	@ApiModelProperty(value = "排班类型,1是周期性、2是弹性")
	@NotBlank(message = "需要输入班次类型")
	private String scheduleType;
	/**
	* 周一
	*/
	@ApiModelProperty(value = "周一")
	private Integer scheduleMonday;
	/**
	* 周二
	*/
	@ApiModelProperty(value = "周二")
	@TableField("schedule_Tuesday")
	private Integer scheduleTuesday;
	/**
	* 周三
	*/
	@ApiModelProperty(value = "周三")
	@TableField("schedule_Wednesday")
	private Integer scheduleWednesday;
	/**
	* 周四
	*/
	@ApiModelProperty(value = "周四")
	@TableField("schedule_Thursday")
	private Integer scheduleThursday;
	/**
	* 周五
	*/
	@ApiModelProperty(value = "周五")
	@TableField("schedule_Friday")
	private Integer scheduleFriday;
	/**
	* 周六
	*/
	@ApiModelProperty(value = "周六")
	@TableField("schedule_Saturday")
	private Integer scheduleSaturday;
	/**
	* 周日
	*/
	@ApiModelProperty(value = "周日")
	@TableField("schedule_Sunday")
	private Integer scheduleSunday;
	/**
	* 法定节假日是否休息,1是0否
	*/
	@ApiModelProperty(value = "法定节假日是否休息,1是0否")
	private String needHoliday;
	/**
	* 班次起始时间
	*/
	@ApiModelProperty(value = "班次起始时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	@NotNull(message = "需要输入工作开始时间")
	private Date scheduleBeginTime;
	/**
	* 班次结束时间
	*/
	@ApiModelProperty(value = "班次结束时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	@NotNull(message = "需要输入工作结束时间")
	private Date scheduleEndTime;
	/**
	* 休息起始时间
	*/
	@ApiModelProperty(value = "休息起始时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date breaksBeginTime;
	/**
	* 休息结束时间
	*/
	@ApiModelProperty(value = "休息结束时间")
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date breaksEndTime;
	/**
	* 班次起始浮动时间
	*/
	@ApiModelProperty(value = "班次起始浮动时间")
	private String beginFloatTime;
	/**
	* 班次结束浮动时间
	*/
	@ApiModelProperty(value = "班次结束浮动时间")
	private String endFloatTime;
	/**
	* 考勤方式，1是手表，2是手机，3是电脑
	*/
	@ApiModelProperty(value = "考勤方式，1是手表，2是手机，3是电脑")
	private String attendanceType;

}
