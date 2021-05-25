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

import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-02-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityInfoVO对象", description = "FacilityInfoVO对象")
public class FacilityInfoVO extends FacilityInfo {
	private static final long serialVersionUID = 1L;
	//日转运垃圾总量
	private String garbageWeight;
	//转运次数
	private Integer transferTimes;

	private String statusName;
	//中转站规模
	private String transtationModel;

	private String odoyLevel;

	private Long deviceCount;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private String facilityId;
}
