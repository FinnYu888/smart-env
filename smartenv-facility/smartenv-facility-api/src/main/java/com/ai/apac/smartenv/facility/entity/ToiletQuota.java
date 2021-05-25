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
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-09-16
 */
@Data
@TableName("ai_toilet_quota")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ToiletQuota对象", description = "ToiletQuota对象")
public class ToiletQuota extends TenantEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long toiletId;
	/**
	* 配额类型
	*/
		@ApiModelProperty(value = "配额类型")
		private String quotaType;
	/**
	* 公厕是否支持安装终端
	*/
		@ApiModelProperty(value = "公厕是否支持安装终端")
		private String supportDevice;
	/**
	* 配额工作状态：使用，空闲，临时关闭
	*/
		@ApiModelProperty(value = "配额工作状态：使用，空闲，临时关闭")
		private String workStatus;
	/**
	* 配额二维码
	*/
		@ApiModelProperty(value = "配额二维码")
		private String toiletQrCode;


}
