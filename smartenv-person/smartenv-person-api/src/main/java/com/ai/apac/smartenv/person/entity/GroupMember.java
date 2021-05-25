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
 * 组成员信息表实体类
 *
 * @author Blade
 * @since 2020-09-10
 */
@Data
@TableName("ai_group_member")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GroupMember对象", description = "组成员信息表")
public class GroupMember extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long id;
	/**
	* 成员ID
	*/
		@ApiModelProperty(value = "成员ID")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long memberId;

	/**
	 * 组ID
	 */
	@ApiModelProperty(value = "组ID")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long groupId;
	/**
	* 成员工号
	*/
		@ApiModelProperty(value = "成员工号")
		private String memberJobNumber;
	/**
	* 成员姓名
	*/
		@ApiModelProperty(value = "成员姓名")
		private String memberName;
	/**
	* 成员所属部门
	*/
		@ApiModelProperty(value = "成员所属部门")
		private Long memberDeptId;
	/**
	* 成员所属职位
	*/
		@ApiModelProperty(value = "成员所属职位")
		private Long memberPositionId;
	/**
	* 成员手机号码
	*/
		@ApiModelProperty(value = "成员手机号码")
		private String memberMobileNumber;
	/**
	* 成员的设备CODE
	*/
		@ApiModelProperty(value = "成员的设备CODE")
		private String memberDeviceCode;


}
