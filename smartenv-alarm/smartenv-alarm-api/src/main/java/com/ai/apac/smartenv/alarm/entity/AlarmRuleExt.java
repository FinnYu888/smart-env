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
package com.ai.apac.smartenv.alarm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 告警规则参数表实体类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Data
@TableName("ai_alarm_rule_ext")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmRuleExt对象", description = "告警规则参数表")
public class AlarmRuleExt extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 父扩展属性Id
	*/
		@ApiModelProperty(value = "父扩展属性Id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long parentId;
	/**
	* 告警规则Id
	*/
		@ApiModelProperty(value = "告警规则Id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long alarmRuleId;
	/**
	* 扩展属性ID
	*/
		@ApiModelProperty(value = "扩展属性ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long attrId;
	/**
	* 扩展属性名称
	*/
		@ApiModelProperty(value = "扩展属性名称")
		private String attrName;
	/**
	* 扩展属性编码
	*/
		@ApiModelProperty(value = "扩展属性编码")
		private String attrCode;
		/**
		 * 告警等级
		 */
		@ApiModelProperty(value = "告警等级")
		private Integer attrLevel;
	/**
	* 扩展属性值序列
	*/
		@ApiModelProperty(value = "扩展属性值序列")
		private Integer attrSeq;
	/**
	* 参数值输入的方式：1：输入框 2：复选框
	*/
		@ApiModelProperty(value = "参数值输入的方式：1：输入框 2：复选框")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long inputType;
	/**
	* 输入方式的值，输入框设为空，勾选框为0未选中和1选中，其他方式再定义
	*/
		@ApiModelProperty(value = "输入方式的值，输入框设为空，勾选框为0未选中和1选中，其他方式再定义")
		private String inputValue;
	/**
	* 计算单位，如km/h
	*/
		@ApiModelProperty(value = "计算单位，如km/h")
		private String measurementUnitCode;
	/**
	* 计算单位名称，如公里每小时
	*/
		@ApiModelProperty(value = "计算单位名称，如公里每小时")
		private String measurementUnitName;
	/**
	 * 是否可编辑:0-不可编辑 1-可编辑
	 */
		@ApiModelProperty(value = "是否可编辑:0-不可编辑 1-可编辑")
		private Integer editable;
}
