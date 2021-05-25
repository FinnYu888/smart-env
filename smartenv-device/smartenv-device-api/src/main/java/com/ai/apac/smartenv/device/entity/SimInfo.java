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
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * SIM卡信息实体类
 *
 * @author Blade
 * @since 2020-05-08
 */
@Data
@TableName("ai_sim_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SimInfo对象", description = "SIM卡信息")
public class SimInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* SIM ID
	*/
		@ApiModelProperty(value = "SIM ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* SIM卡号
	*/
		@ApiModelProperty(value = "SIM卡号")
		private String simCode;

	@ApiModelProperty(value = "SIM卡号2")
	private String simCode2;
	/**
	* 电话号码
	*/
		@ApiModelProperty(value = "电话号码")
		private String simNumber;
	/**
	* SIM卡类型
	*/
		@ApiModelProperty(value = "SIM卡类型")
		private String simType;
	/**
	* 备注
	*/
		@ApiModelProperty(value = "备注")
		private String remark;


}
