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
package com.ai.apac.smartenv.person.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 人员扩展信息表实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_person_ext")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonExt对象", description = "人员扩展信息表")
public class PersonExt extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		private Long id;
	/**
	* 人员ID
	*/
		@ApiModelProperty(value = "人员ID")
		private Long personId;
	/**
	* 扩展属性ID
	*/
		@ApiModelProperty(value = "扩展属性ID")
		private Long attrId;
	/**
	* 扩展属性名称
	*/
		@ApiModelProperty(value = "扩展属性名称")
		private String attrName;
	/**
	* 扩展属性值ID
	*/
		@ApiModelProperty(value = "扩展属性值ID")
		private Long attrValueId;
	/**
	* 扩展属性值序列
	*/
		@ApiModelProperty(value = "扩展属性值序列")
		private Integer attrValueSeq;
	/**
	* 扩展属性值
	*/
		@ApiModelProperty(value = "扩展属性值")
		private String attrValue;
	/**
	* 扩展属性展现值
	*/
		@ApiModelProperty(value = "扩展属性展现值")
		private String attrDisplayValue;


}
