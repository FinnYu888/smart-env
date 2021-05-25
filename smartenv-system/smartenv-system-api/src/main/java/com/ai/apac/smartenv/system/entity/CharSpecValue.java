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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 扩展属性值表实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_char_spec_value")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CharSpecValue对象", description = "扩展属性值表")
public class CharSpecValue extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 属性ID
	*/
		@ApiModelProperty(value = "属性ID")
		private Long charSpecId;
	/**
	* 属性值
	*/
		@ApiModelProperty(value = "属性值")
		private String value;
	/**
	* 属性值序列
	*/
		@ApiModelProperty(value = "属性值序列")
		private Integer valueSeq;
	/**
	* 属性展现值
	*/
		@ApiModelProperty(value = "属性展现值")
		private String displayValue;


}
