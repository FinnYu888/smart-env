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
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-08-19
 */
@Data
@TableName("ai_person_job_number")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonJobNumber对象", description = "PersonJobNumber对象")
public class PersonJobNumber extends TenantEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	* 当前工号
	*/
		@ApiModelProperty(value = "当前工号")
		private Integer currentNumber;
	/**
	* 工号位数
	*/
		@ApiModelProperty(value = "工号位数")
		private Integer figures;
	/**
	* 前缀
	*/
		@ApiModelProperty(value = "前缀")
		private String prefix;


}
