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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 事件指标与事件信息关联表实体类
 *
 * @author Blade
 * @since 2020-12-16
 */
@Data
@TableName("ai_event_info_kpi_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventInfoKpiRel对象", description = "事件指标与事件信息关联表")
public class EventInfoKpiRel extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键ID
	*/
		@ApiModelProperty(value = "主键ID")
		private Long id;
	/**
	* 事件信息ID
	*/
		@ApiModelProperty(value = "事件信息ID")
		private Long eventInfoId;
	/**
	* 事件指标ID
	*/
		@ApiModelProperty(value = "事件指标ID")
		private Long kpiId;
	/**
	* 扣分值
	*/
		@ApiModelProperty(value = "扣分值")
		private String deducted;


}
