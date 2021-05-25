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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 人员与车辆关系表实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@TableName("ai_person_vehicle_rel")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonVehicleRel对象", description = "人员与车辆关系表")
public class PersonVehicleRel extends TenantEntity {

	private static final long serialVersionUID = 1L;

//	/**
//	* 主键
//	*/
//		@ApiModelProperty(value = "主键")
//		private Long id;
	/**
	* 人员ID
	*/
		@ApiModelProperty(value = "人员ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long personId;
	/**
	 * 人员姓名
	 */
		@ApiModelProperty(value = "人员姓名")
		private String personName;
	/**
	* 车辆ID
	*/
		@ApiModelProperty(value = "车辆ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long vehicleId;
	/**
	* 车辆分类ID
	*/
		@ApiModelProperty(value = "车辆分类ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long entityCategoryId;


}
