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
package com.ai.apac.smartenv.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
 * @since 2020-08-20
 */
@Data
@TableName("ai_training_object")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TrainingObject对象", description = "TrainingObject对象")
public class TrainingObject extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "培训对象表Id")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long id;

	/**
	 * 培训记录表Id
	 */
	@ApiModelProperty(value = "培训记录表Id")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long trainingRecordId;
	/**
	* 培训对象类型：1-人 2-部门 
	*/
		@ApiModelProperty(value = "培训对象类型：1-人 2-部门 ")
		private Integer objectType;
	/**
	* 培训对象Id
	*/
		@ApiModelProperty(value = "培训对象Id")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long objectId;
	/**
	* 培训对象名称
	*/
		@ApiModelProperty(value = "培训对象名称")
		private String objectName;


}
