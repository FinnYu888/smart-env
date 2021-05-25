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
package com.ai.apac.smartenv.facility.entity;

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
 * 实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_facility_ext")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityExt对象", description = "FacilityExt对象")
public class FacilityExt extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 设施扩展属性id
	*/
		@ApiModelProperty(value = "设施扩展属性id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
	/**
	* 设施id
	*/
		@ApiModelProperty(value = "设施id")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long facilityId;
	/**
	* 车,人,设施,物资,设备
	*/
		@ApiModelProperty(value = "车,人,设施,物资,设备")
		private Long attrId;
	/**
	* 属性名称
	*/
		@ApiModelProperty(value = "属性名称")
		private String attrName;
	/**
	* 属性值ID
	*/
		@ApiModelProperty(value = "属性值ID")
		private Long attrValueId;
	/**
	* 扩展属性值序列
	*/
		@ApiModelProperty(value = "扩展属性值序列")
		private Integer attrValueSeq;
	/**
	* 属性值
	*/
		@ApiModelProperty(value = "属性值")
		private String attrValue;
	/**
	* 属性显示值
	*/
		@ApiModelProperty(value = "属性显示值")
		private String attrDisplayValue;


}
