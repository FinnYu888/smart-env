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
 * 绿化养护信息实体类
 *
 * @author Blade
 * @since 2020-07-22
 */
@Data
@TableName("ai_greenarea_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GreenareaInfo对象", description = "绿化养护信息")
public class GreenareaInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 绿化养护ID
	 */
	@ApiModelProperty(value = "绿化养护ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 绿化区域ID
	 */
	@ApiModelProperty(value = "绿化区域ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long regionId;

	@ApiModelProperty(value = "绿化区域名称")
	private String regionName;
	/**
	 * 绿化区域面积（平方米）
	 */
	@ApiModelProperty(value = "绿化区域面积（平方米）")
	private String regionArea;
	/**
	 * 绿化养护面积（平方米）
	 */
	@ApiModelProperty(value = "绿化养护面积（平方米）")
	private String greenareaArea;
	/**
	 * 草坪面积（平方米）
	 */
	@ApiModelProperty(value = "草坪面积（平方米）")
	private String lawnArea;

	/**
	 * 片区负责人
	 */
	@ApiModelProperty(value = "片区负责人")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long greenareaHead;


}
