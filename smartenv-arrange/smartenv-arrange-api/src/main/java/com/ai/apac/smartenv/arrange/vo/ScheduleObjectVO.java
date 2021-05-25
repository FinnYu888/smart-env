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

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 敏捷排班表视图实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScheduleObjectVO对象", description = "敏捷排班表")
public class ScheduleObjectVO extends ScheduleObject {
	private static final long serialVersionUID = 1L;

	private String arrangeDate;
	private String scheduleTime;
	private String scheduleName;
	private List<Long> entityIds;

	private Long deptId;
	private String deptName;
	private String personName;
	private Long personPositionId;
	private String personPositionName;
	private String plateNumber;
	private Long entityCategoryId;
	private String entityCategoryName;
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date scheduleBeginTime;
	@DateTimeFormat(pattern = "HH:mm")
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	private Date scheduleEndTime;
	
	private Integer submitType;
	private List<Long> scheduleIds;
	private List<ScheduleVO> schedules;
	
	
	
}
