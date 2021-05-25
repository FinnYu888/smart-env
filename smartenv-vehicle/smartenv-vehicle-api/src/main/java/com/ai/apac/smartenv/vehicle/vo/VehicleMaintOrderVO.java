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
package com.ai.apac.smartenv.vehicle.vo;

import java.util.List;

import com.ai.apac.smartenv.vehicle.entity.VehicleMaintOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-08-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleMaintOrderVO对象", description = "VehicleMaintOrderVO对象")
public class VehicleMaintOrderVO extends VehicleMaintOrder {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "申请类型")
	private String applyTypeName;
	@ApiModelProperty(value = "维修类型名称")
	private String maintTypeName;
	@ApiModelProperty(value = "车辆大类名称")
	private String vehicleKindName;
	@ApiModelProperty(value = "车辆类型名称")
	private String vehicleTypeName;
	
	private List<VehicleMaintMilestoneNode> maintMilestoneNodes;
}
