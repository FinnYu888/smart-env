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
package com.ai.apac.smartenv.device.vo;

import com.ai.apac.smartenv.device.entity.SimInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * SIM卡信息视图实体类
 *
 * @author Blade
 * @since 2020-05-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SimInfoVO对象", description = "SIM卡信息")
public class SimInfoVO extends SimInfo {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "SIM卡类型名称")
	private String simTypeName;

	@ApiModelProperty(value = "设备类型")
	private String deviceEntityCategoryId;

	@ApiModelProperty(value = "设备类型名称")
	private String deviceEntityCategoryName;

	@ApiModelProperty(value = "设备型号")
	private String deviceType;

	@ApiModelProperty(value = "设备编码")
	private String deviceCode;

}
