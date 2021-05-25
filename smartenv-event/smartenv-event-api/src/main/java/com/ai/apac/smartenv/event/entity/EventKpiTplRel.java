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
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springblade.core.tenant.mp.TenantEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 考核指标定义表实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@TableName("ai_event_kpi_tpl_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventKpiTplRel对象", description = "事件考核模板关联表")
public class EventKpiTplRel extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 考核指标模板
	 */
	@ApiModelProperty(value = "考核指标模板")
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull(message = "需要输入考核指标模板")
	private Long eventKpiTplId;


	/**
	 * 考核指标
	 */
	@ApiModelProperty(value = "考核指标")
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull(message = "需要输入考核指标")
	private Long eventKpiId;

	/**
	 * 考核指标
	 */
	@ApiModelProperty(value = "扣分最高阈值")
	@NotNull(message = "需要输入扣分最高阈值")
	private Double threshold;

}
