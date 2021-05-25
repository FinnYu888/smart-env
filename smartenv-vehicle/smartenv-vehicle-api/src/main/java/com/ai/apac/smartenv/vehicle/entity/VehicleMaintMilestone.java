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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-02-25
 */
@Data
@TableName("ai_vehicle_maint_milestone")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleMaintMilestone对象", description = "VehicleMaintMilestone对象")
public class VehicleMaintMilestone extends TenantEntity {

	private static final long serialVersionUID = 1L;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderId;
	@JsonSerialize(using = ToStringSerializer.class)
	private String assignmentId;
	private String assignmentName;
	private String assignmentJobNum;
	private String description;
	private String taskDefineName;
	private String taskId;
	private Integer doneResult;
	private String doneRemark;




}
