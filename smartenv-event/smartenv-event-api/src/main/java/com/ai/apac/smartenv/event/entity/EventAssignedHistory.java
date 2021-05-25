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
package com.ai.apac.smartenv.event.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.util.List;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-03-03
 */
@Data
@TableName("ai_event_assigned_history")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventAssignedHistory对象", description = "EventAssignedHistory对象")
public class EventAssignedHistory extends TenantEntity {

	private static final long serialVersionUID = 1L;


	/**
	* 事件指派历史表主键id
	*/
		@ApiModelProperty(value = "事件指派历史表主键id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 事件id
	*/
		@ApiModelProperty(value = "事件id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long eventInfoId;
	/**
	* 被指派人id
	*/
		@ApiModelProperty(value = "被指派人id")
		private String assignedPersonId;
	/**
	* 被指派人姓名
	*/
		@ApiModelProperty(value = "被指派人姓名")
		private String assignedPersonName;
	/**
	* 处理意见
	*/
		@ApiModelProperty(value = "处理意见")
		private String handleAdvice;
	/**
	* 评价
	*/
		@ApiModelProperty(value = "评价")
		private String evaluation;
	/**
	* 打分
	*/
		@ApiModelProperty(value = "打分")
		private Integer score;
	/**
	* 检查结果，检查结果，3-合格，2-不合格
	*/
		@ApiModelProperty(value = "检查结果，3-合格，2-不合格")
		private Integer checkResult;
	/**
	* 类型，1-指派，2-检查
	*/
		@ApiModelProperty(value = "类型，1-指派，2-检查")
		private Integer type;


}
