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
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 记录设备信息实体类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Data
@TableName("ai_device_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceInfo对象", description = "记录设备信息")
public class DeviceInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设备ID
	*/
		@ApiModelProperty(value = "设备ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 设备编码
	*/
		@ApiModelProperty(value = "设备编码")
		private String deviceCode;
	/**
	* 设备名称
	*/
		@ApiModelProperty(value = "设备名称")
		private String deviceName;
	/**
	* 设备型号
	*/
		@ApiModelProperty(value = "设备型号")
		private String deviceType;
	/**
	* 设备厂家
	*/
		@ApiModelProperty(value = "设备厂家")
		private String deviceFactory;
	/**
	* 实体分类
	*/
		@ApiModelProperty(value = "实体分类")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long entityCategoryId;

	/**
	 * 实体分类
	 */
	@ApiModelProperty(value = "设备实时状态")
	private Long deviceStatus;

	@ApiModelProperty(value = "设备实时坐标")
	private String deviceLocation;

	@ApiModelProperty(value = "设备实时位置")
	private String deviceLocationName;
}
