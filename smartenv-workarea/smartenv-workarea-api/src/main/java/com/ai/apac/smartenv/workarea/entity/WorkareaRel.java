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
package com.ai.apac.smartenv.workarea.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 工作区域关联表实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_workarea_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WorkareaRel对象", description = "工作区域关联表")
public class WorkareaRel extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 工作区域关联ID
	*/
		@ApiModelProperty(value = "工作区域关联ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 工作区域ID
	*/
		@ApiModelProperty(value = "工作区域ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long workareaId;
	/**
	* 关联实体ID
	*/
		@ApiModelProperty(value = "关联实体ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long entityId;
	/**
	* 实体类型
	*/
		@ApiModelProperty(value = "实体类型,1-人员，2-车辆")
		private Long entityType;
	/**
	* 关联实体分类
	*/
		@ApiModelProperty(value = "关联实体分类")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long entityCategoryId;


}
