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
 * 人员紧急联系人信息表实体类
 *
 * @author Blade
 * @since 2020-02-26
 */
@Data
@TableName("ai_device_contact")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceContact对象", description = "人员紧急联系人信息表")
public class DeviceContact extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long deviceId;
	/**
	* 联系人姓名
	*/
		@ApiModelProperty(value = "联系人姓名")
		private String contactPersonName;
	/**
	* 联系人号码
	*/
		@ApiModelProperty(value = "联系人号码")
		private String contactPersonNumber;
	/**
	* 联系人优先级
	*/
		@ApiModelProperty(value = "联系人优先级")
		private Long contactPersonSeq;


}
