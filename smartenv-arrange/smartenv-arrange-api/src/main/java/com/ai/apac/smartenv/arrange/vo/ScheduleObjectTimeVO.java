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
package com.ai.apac.smartenv.arrange.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: ScheduleObjectTimeVO.java
 * @Description: 该类的功能描述
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月29日 下午4:29:21 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月29日     zhaoaj           v1.0.0               修改原因
 */
@Data
@ApiModel(value = "同步大数据对象", description = "同步大数据")
public class ScheduleObjectTimeVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long scheduleObjectId;

	@ApiModelProperty(value = "实体ID，车或人")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long entityId;
	
	@ApiModelProperty(value = "实体类型，1车2人")
	private String entityType;
	
	@ApiModelProperty(value = "排班日期")
	private String scheduleDate;
	
	@ApiModelProperty("状态, 1正常，0休息")
	private Integer status;
	
	@ApiModelProperty(value = "班次起始时间")
	@DateTimeFormat(pattern = "HH:mm:ss")
	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	@NotNull(message = "需要输入工作开始时间")
	private Date scheduleBeginTime;

	@ApiModelProperty(value = "班次结束时间")
	@DateTimeFormat(pattern = "HH:mm:ss")
	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	@NotNull(message = "需要输入工作结束时间")
	private Date scheduleEndTime;

	@ApiModelProperty(value = "休息起始时间")
	@DateTimeFormat(pattern = "HH:mm:ss")
	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	private Date breaksBeginTime;

	@ApiModelProperty(value = "休息结束时间")
	@DateTimeFormat(pattern = "HH:mm:ss")
	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	private Date breaksEndTime;
}
