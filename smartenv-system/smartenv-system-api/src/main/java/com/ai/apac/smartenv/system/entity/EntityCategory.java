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
import org.springblade.core.tenant.mp.TenantEntity;


/**
 * 车辆,设备,物资等实体的分类信息实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@TableName("ai_entity_category")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EntityCategory对象", description = "车辆,设备,物资等实体的分类信息")
public class EntityCategory extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 分类ID
	*/
		@ApiModelProperty(value = "分类ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 分类名称
	*/
		@ApiModelProperty(value = "分类名称")
		private String categoryName;
	/**
	* 分类编码
	*/
		@ApiModelProperty(value = "分类编码")
		private String categoryCode;
	/**
	* 父级分类ID
	*/
		@ApiModelProperty(value = "父级分类ID")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long parentCategoryId;
	/**
	* 实体类型
	*/
		@ApiModelProperty(value = "实体类型")
		private String entityType;
	/**
	* 排序
	*/
		@ApiModelProperty(value = "排序")
		private Integer sortId;


}
