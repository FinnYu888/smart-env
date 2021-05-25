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
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 扩展属性表实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@TableName("ai_char_spec")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CharSpec对象", description = "扩展属性表")
public class CharSpec extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		private Long id;
	/**
	* 属性名称
	*/
		@ApiModelProperty(value = "属性名称")
		private String charSpecName;
	/**
	* 属性编码
	*/
		@ApiModelProperty(value = "属性编码")
		private String charSpecCode;
	/**
	* 实体所属分类
	*/
		@ApiModelProperty(value = "实体所属分类")
		private Long entityCategoryId;
	/**
	* 属性值是否允许前端自定义
	*/
		@ApiModelProperty(value = "属性值是否允许前端自定义")
		private Integer isCustomized;
	/**
	* 属性是否前端可见
	*/
		@ApiModelProperty(value = "属性是否前端可见")
		private Integer isDisplay;
	/**
	* 属性值是否允许多选
	*/
		@ApiModelProperty(value = "属性值是否允许多选")
		private Integer isMultiple;
	/**
	* 排序
	*/
		@ApiModelProperty(value = "排序")
		private Integer sortId;


}
