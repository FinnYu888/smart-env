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
package com.ai.apac.smartenv.facility.vo;

import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-07-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AshcanInfoVO对象", description = "AshcanInfoVO对象")
public class AshcanInfoVO extends AshcanInfo {
	private static final long serialVersionUID = 1L;
	
	private String deptName;
	private String ashcanTypeName;
	private String ashcanStatusName;
	private String workStatusName;
	private String supportDeviceName;
	private String workareaName;
	private String regionName;
	private String deviceName;

	/**
	 * 展示经度
	 */
	private String showLng;
	/**
	 * 展示纬度
	 */
	private String showLat;

	private String ashcanPicture;

}
