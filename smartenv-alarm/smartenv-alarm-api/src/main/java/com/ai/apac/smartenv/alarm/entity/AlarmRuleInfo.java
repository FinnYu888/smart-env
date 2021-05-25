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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;

/**
 * 告警规则基本信息表实体类
 *
 * @author Blade
 * @since 2020-02-15
 */
@Data
@TableName("ai_alarm_rule_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmRuleInfo对象", description = "告警规则基本信息表")
public class AlarmRuleInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 告警规则基本信息表主键id
	*/
		@ApiModelProperty(value = "告警规则基本信息表主键id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 名称/描述
	*/
		@ApiModelProperty(value = "名称/描述")
		private String name;
	/**
	* 告警规则分类
	*/
		@ApiModelProperty(value = "告警规则分类")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long entityCategoryId;
	/**
	* 分类编码
	*/
		@ApiModelProperty(value = "分类编码")
		private String entityCategoryCode;
	/**
	* 告警开始时间
	*/
		@ApiModelProperty(value = "告警开始时间")
		private Timestamp alarmStartTime;
	/**
	* 告警结束时间
	*/
		@ApiModelProperty(value = "告警结束时间")
		private Timestamp alarmEndTime;
	/**
	* 备注
	*/
		@ApiModelProperty(value = "备注")
		private String remark;


}
