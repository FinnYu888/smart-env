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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.validation.constraints.NotNull;

import org.springblade.core.tenant.mp.TenantEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 员工用户关联表实体类
 *
 * @author Blade
 * @since 2020-03-31
 */
@Data
@TableName("ai_person_user_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonUserRel对象", description = "员工用户关联表")
public class PersonUserRel extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* id
	*/
	@ApiModelProperty(value = "id")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long id;
	/**
	* 人员ID
	*/
	@ApiModelProperty(value = "人员ID")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@NotNull(message = "需要输入人员编号")
	private Long personId;
	/**
	* 用户ID
	*/
	@ApiModelProperty(value = "用户ID")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@NotNull(message = "需要输入用户编号")
	private Long userId;


}
