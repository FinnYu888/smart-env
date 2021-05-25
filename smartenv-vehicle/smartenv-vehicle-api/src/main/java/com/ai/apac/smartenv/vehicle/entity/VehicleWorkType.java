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
package com.ai.apac.smartenv.vehicle.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 车辆作业类型表实体类
 *
 * @author Blade
 * @since 2021-01-12
 */
@Data
@TableName("ai_vehicle_work_type")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleWorkType对象", description = "车辆作业类型表")
public class VehicleWorkType extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键ID
	*/
		@ApiModelProperty(value = "主键ID")
		private Long id;
	/**
	* 车辆类型ID
	*/
		@ApiModelProperty(value = "车辆类型ID")
		private String vehicleCategoryCode;
	/**
	* 工作类型名称
	*/
		@ApiModelProperty(value = "工作类型名称")
		private String workTypeName;
	/**
	* 工作类型编码
	*/
		@ApiModelProperty(value = "工作类型编码")
		private String workTypeCode;


}
