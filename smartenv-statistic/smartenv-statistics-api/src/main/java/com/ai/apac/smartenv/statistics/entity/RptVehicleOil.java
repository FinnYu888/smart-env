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
package com.ai.apac.smartenv.statistics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.tenant.mp.TenantEntity;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-09-16
 */
@Data
@TableName("ai_rpt_vehicle_oil")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RptVehicleOil对象", description = "RptVehicleOil对象")
public class RptVehicleOil extends TenantEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	private LocalDate date;
	private String month;
	private Long entityId;
	private Integer startOil;
	private Integer endOil;
	private Integer addOil;
	private Integer oilConsumption;
	private Integer mileage;
	private Integer oilConsumptionHundred;


}
