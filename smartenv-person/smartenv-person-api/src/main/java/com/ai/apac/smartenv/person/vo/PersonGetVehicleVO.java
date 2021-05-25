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
package com.ai.apac.smartenv.person.vo;

import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 人员与车辆关系表视图实体类
 *
 * @author Blade
 * @since 2020-02-07
 */
@Data
@ApiModel(value = "PersonGetVehicleVO对象")
public class PersonGetVehicleVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long vehicleId;
	private String plateNmuber;

	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private Long personId;
	private String personName;

}
