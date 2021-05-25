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
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 告警通知方式配置表实体类
 *
 * @author Blade
 * @since 2020-12-28
 */
@Data
@TableName("ai_alarm_inform")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmInform对象", description = "告警通知方式配置表")
public class AlarmInform extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 告警通知方式配置表主键id
	*/
		@ApiModelProperty(value = "告警通知方式配置表主键id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 报警等级
	*/
		@ApiModelProperty(value = "报警等级")
		private Integer alarmLevel;
	/**
	* 关联的告警Id,预留不实现
	*/
		@ApiModelProperty(value = "关联的告警Id,预留不实现")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long alarmRuleId;
		/**
		 * 应用当前通知类型的实体类型
		 */
		@ApiModelProperty(value = "应用当前通知类型的实体类型")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long entityType;
	/**
	* 通知类型：1.短信 2.微信 3.后台通知 4.手环
	*/
		@ApiModelProperty(value = "通知类型：1.短信 2.微信 3.后台通知 4.手环")
		private String informType;
	/**
	* 是否抄送领导，0不抄送，1抄送
	*/
		@ApiModelProperty(value = "是否抄送领导，0不抄送，1抄送")
		private Integer ccToLeader;
	/**
	* 抄送领导通知类型：1.短信 2.微信 3.后台通知 4.手环
	*/
		@ApiModelProperty(value = "抄送领导通知类型：1.短信 2.微信 3.后台通知 4.手环")
		private String ccToLeaderInformType;
	/**
	* 备注
	*/
		@ApiModelProperty(value = "备注")
		private String remark;


}
