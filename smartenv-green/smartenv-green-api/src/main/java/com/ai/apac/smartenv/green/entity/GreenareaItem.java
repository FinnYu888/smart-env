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
package com.ai.apac.smartenv.green.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springblade.core.tenant.mp.TenantEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 绿化养护项信息实体类
 *
 * @author Blade
 * @since 2020-07-22
 */
@Data
@TableName("ai_greenarea_item")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GreenareaItem对象", description = "绿化养护项信息")
public class GreenareaItem extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 绿化养护ID
	 */
	@ApiModelProperty(value = "绿化养护ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long greenareaId;
	/**
	 * 项ID
	 */
	@ApiModelProperty(value = "项ID")
	private String itemSpecId;
	/**
	 * 项数量
	 */
	@ApiModelProperty(value = "项数量")
	private String itemCount;

}
