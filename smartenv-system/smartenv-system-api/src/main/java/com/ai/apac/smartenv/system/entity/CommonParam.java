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
 * 公共参数表实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_common_param")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CommonParam对象", description = "公共参数表")
public class CommonParam extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		private Long id;
	/**
	* 参数值
	*/
		@ApiModelProperty(value = "参数值")
		private Long paramValue;
	/**
	* 参数展现文本
	*/
		@ApiModelProperty(value = "参数展现文本")
		private String paramAttr;
	/**
	* 参数类型,比如手环厂家
	*/
		@ApiModelProperty(value = "参数类型,比如手环厂家")
		private String paramType;


}
