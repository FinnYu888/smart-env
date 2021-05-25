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

/**
 * 考核指标分类实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@TableName("ai_event_kpi_catalog")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventKpiCatalog对象", description = "事件指标分类")
public class EventKpiCatalog extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long parentId;

	private Integer catalogLevel;


	/**
	* 考核指标名称
	*/
	@ApiModelProperty(value = "事件指标分类名称")
	@Length(max = 64, message = "事件指标分类名称长度不能超过64")
	@NotBlank(message = "需要输入事件指标分类名称")
	private String catalogName;
	/**
	* kpi描述
	*/
	@ApiModelProperty(value = "kpi描述")
	@Length(max = 255, message = "描述长度不能超过255")
	private String remark;

}
