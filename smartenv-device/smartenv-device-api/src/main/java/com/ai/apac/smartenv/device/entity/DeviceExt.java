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
package com.ai.apac.smartenv.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 记录设备属性实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_device_ext")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceExt对象", description = "记录设备属性")
public class DeviceExt extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设备属性Id
	*/
		@ApiModelProperty(value = "设备属性Id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 设备ID
	*/
		@ApiModelProperty(value = "设备ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long deviceId;
	/**
	* 属性ID
	*/
		@ApiModelProperty(value = "属性ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long attrId;
	/**
	* 属性名称
	*/
		@ApiModelProperty(value = "属性名称")
		private String attrName;
	/**
	* 属性值ID
	*/
		@ApiModelProperty(value = "属性值ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long attrValueId;
	/**
	* 属性值
	*/
		@ApiModelProperty(value = "属性值")
		private String attrValue;
	/**
	* 属性值显示名称
	*/
		@ApiModelProperty(value = "属性值显示名称")
		private String attrDisplayValue;


}
