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

import com.ai.apac.smartenv.vehicle.entity.RefuelInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 记录加油信息视图实体类
 *
 * @author Blade
 * @since 2020-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RefuelInfoVO对象", description = "记录加油信息")
public class RefuelInfoVO extends RefuelInfo {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "车牌号")
	String plateNmuber;
	@ApiModelProperty(value = "加油人名")
	String personName;
	@ApiModelProperty(value = "油品名")
	String oilTypeName;
	@ApiModelProperty(value = "加油区域名")
	String areaName;
	@ApiModelProperty(value = "车辆仪表盘照片URL")
	String picCarDbURL;
	@ApiModelProperty(value = "加油站仪表照片URL")
	String picGasDdURL;
	@ApiModelProperty(value = "加油小票URL")
	String picReceiptURL;

}
