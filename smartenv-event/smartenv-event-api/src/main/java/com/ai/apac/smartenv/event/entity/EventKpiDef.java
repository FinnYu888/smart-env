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
import java.math.BigDecimal;

/**
 * 考核指标定义表实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@TableName("ai_event_kpi_def")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventKpiDef对象", description = "事件指标定义表")
public class EventKpiDef extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	* 指标分类
	*/
	@ApiModelProperty(value = "事件指标分类")
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull(message = "需要输入事件指标分类")
	private Long eventKpiCatalog;
	/**
	* 考核指标名称
	*/
	@ApiModelProperty(value = "事件指标名称")
	@Length(max = 64, message = "事件指标名称长度不能超过64")
	@NotBlank(message = "需要输入事件指标名称")
	private String eventKpiName;
	/**
	 * kpi描述
	 */
	@ApiModelProperty(value = "具体工作任务和行动")
	@Length(max = 2555, message = "具体工作任务和行动长度不能超过2555")
	@NotBlank(message = "需要输入具体工作任务和行动")
	private String eventKpiDescription;
	/**
	* kpi备注
	*/
	@ApiModelProperty(value = "备注")
	@Length(max = 255, message = "备注长度不能超过2555")
	private String eventKpiRemark;
	/**
	* 评分标准
	*/
	@ApiModelProperty(value = "评分标准")
	@Length(max = 2555, message = "评分标准长度不能超过2555")
	@NotBlank(message = "需要输入评分标准")
	private String appraisalCriteria;

	/**
	 * 评分标准
	 */
	@ApiModelProperty(value = "事件整改时限")
	@Length(max = 10, message = "事件整改时限长度不能超过10")
//	@NotBlank(message = "需要输入事件整改时限")
	private String handleLimitTime;

}
