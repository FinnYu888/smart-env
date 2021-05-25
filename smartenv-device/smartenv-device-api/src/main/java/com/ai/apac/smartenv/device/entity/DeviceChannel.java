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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 录像设备通道信息实体类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Data
@TableName("ai_device_channel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceChannel对象", description = "录像设备通道信息")
public class DeviceChannel extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* ID
	*/
		@ApiModelProperty(value = "ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 通道编码
	*/
		@ApiModelProperty(value = "通道编码")
		private String channelCode;
	/**
	* 通道名称
	*/
		@ApiModelProperty(value = "通道名称")
		private String channelName;
	/**
	* 通道序列
	*/
		@ApiModelProperty(value = "通道序列")
		private String channelSeq;
	/**
	* 设备ID
	*/
		@ApiModelProperty(value = "设备ID")
		private String deviceId;


}
