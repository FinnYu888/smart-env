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
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 组信息表实体类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Data
@TableName("ai_group")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Group对象", description = "组信息表")
public class Group extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long id;
	/**
	* 组名
	*/
		@ApiModelProperty(value = "组名")
		private String groupName;
	/**
	* 成员数量
	*/
		@ApiModelProperty(value = "成员数量")
		private Long memberNum;

	/**
	 * 成员数量
	 */
	@ApiModelProperty(value = "是否默认组")
	private Long isDefault;
	/**
	* 组的描述
	*/
		@ApiModelProperty(value = "组的描述")
		private String groupDesc;


}
