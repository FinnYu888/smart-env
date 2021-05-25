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
package com.ai.apac.smartenv.facility.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springblade.core.tenant.mp.TenantEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-07-20
 */
@Data
@TableName("ai_ashcan_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AshcanInfo对象", description = "AshcanInfo对象")
public class AshcanInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 垃圾桶编码
	*/
	@ApiModelProperty(value = "垃圾桶编码")
	@Length(max = 64, message = "垃圾桶编码长度不能超过64")
	@NotBlank(message = "需要输入垃圾桶编码")
	private String ashcanCode;
	/**
	* 垃圾桶类型
	*/
	@ApiModelProperty(value = "垃圾桶类型")
	@NotBlank(message = "需要输入垃圾桶类型")
	private String ashcanType;
	/**
	* 垃圾桶容量，单位L
	*/
	@ApiModelProperty(value = "垃圾桶容量，单位L")
	@NotNull(message = "需要输入垃圾桶大小")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long capacity;
	/**
	* 垃圾桶是否支持安装终端
	*/
	@ApiModelProperty(value = "垃圾桶是否支持安装终端")
//	@NotBlank(message = "需要输入是否支持终端")
	private String supportDevice;
	/**
	* 所属部门
	*/
	@ApiModelProperty(value = "所属部门")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;
	/**
	* 所属路线/区域
	*/
	@ApiModelProperty(value = "所属路线/区域")
	@NotNull(message = "需要输入所属路线/区域")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long workareaId;
	/**
	* 所属片区
	*/
	@ApiModelProperty(value = "所属片区")
//	@NotNull(message = "需要输入所属片区")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long regionId;
	/**
	* 经度
	*/
	@ApiModelProperty(value = "经度")
	private String lng;
	/**
	* 纬度
	*/
	@ApiModelProperty(value = "纬度")
	private String lat;
	/**
	* 垃圾桶地址
	*/
	@ApiModelProperty(value = "垃圾桶地址")
	private String location;
	/**
	* 垃圾桶详细地址
	*/
	@ApiModelProperty(value = "垃圾桶详细地址")
	private String detailLocation;
	/**
	* 垃圾桶厂家
	*/
	@ApiModelProperty(value = "垃圾桶厂家")
	private String companyCode;
	/**
	* 垃圾桶状态：正常，损坏
	*/
	@ApiModelProperty(value = "垃圾桶状态：正常，损坏")
	private String ashcanStatus;
	/**
	* 工作状态：正常，溢满，无信号
	*/
	@ApiModelProperty(value = "工作状态：正常，溢满，无信号")
	private String workStatus;
	/**
	* 垃圾桶二维码
	*/
	@ApiModelProperty(value = "垃圾桶二维码")
	private String ashcanQrCode;
	/**
	 * 传感器上传经度
	 */
	@ApiModelProperty(value = "传感器上传经度")
	private String deviceLng;
	/**
	 * 传感器上传纬度
	 */
	@ApiModelProperty(value = "传感器上传纬度")
	private String deviceLat;

	/**
	 * 设备
	 */
	@ApiModelProperty(value = "设备")
	private String deviceId;

}
