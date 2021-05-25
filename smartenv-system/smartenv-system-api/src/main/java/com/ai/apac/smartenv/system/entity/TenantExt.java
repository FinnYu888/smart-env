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
package com.ai.apac.smartenv.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 租户扩展表实体类
 *
 * @author Blade
 * @since 2020-07-05
 */
@Data
@TableName("blade_tenant_ext")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TenantExt对象", description = "租户扩展表")
public class TenantExt extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		private Long id;

	/**
	* 地图缩放
	*/
		@ApiModelProperty(value = "地图缩放")
		private String mapZoom;
	/**
	* 平台名称
	*/
		@ApiModelProperty(value = "平台名称")
		private String webTitle;
	/**
	* 小程序名称
	*/
		@ApiModelProperty(value = "小程序名称")
		private String appTitle;
	/**
	* 大屏名称
	*/
		@ApiModelProperty(value = "大屏名称")
		private String screenTitle;
	/**
	* log图片
	*/
		@ApiModelProperty(value = "log图片")
		private String logoUri;


}
