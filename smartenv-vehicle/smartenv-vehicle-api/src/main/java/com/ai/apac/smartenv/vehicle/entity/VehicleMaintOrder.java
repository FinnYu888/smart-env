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

import org.springblade.core.tenant.mp.TenantEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-08-06
 */
@Data
@TableName("ai_vehicle_maint_order")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "VehicleMaintOrder对象", description = "VehicleMaintOrder对象")
public class VehicleMaintOrder extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 申请维修单号
	 */
	@ApiModelProperty(value = "申请维修单号")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 申请类型；维修；保养
	 */
	@ApiModelProperty(value = "申请类型；维修；保养")
	private String applyType;
	/**
	 * 维修车辆ID
	 */
	@ApiModelProperty(value = "维修车辆ID")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long vehicleId;
	/**
	 * 维修车辆号牌或名称
	 */
	@ApiModelProperty(value = "维修车辆号牌或名称")
	private String vehicleName;
	/**
	 * 车辆大类型
	 */
	@ApiModelProperty(value = "车辆大类型")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long vehicleKind;
	/**
	 * 车辆类型
	 */
	@ApiModelProperty(value = "车辆类型")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long vehicleType;
	/**
	 * 申请人id
	 */
	@ApiModelProperty(value = "申请人id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applyPersonId;
	/**
	 * 申请人名称
	 */
	@ApiModelProperty(value = "申请人名称")
	private String applyPersonName;
	/**
	 * 申请人部门
	 */
	@ApiModelProperty(value = "申请人部门")
	private String applyPersonDept;
	/**
	 * 申请人工号
	 */
	@ApiModelProperty(value = "申请人工号")
	private String applyJobNum;
	/**
	 * 当前里程
	 */
	@ApiModelProperty(value = "当前里程")
	private String mileage;

	/**
	 * 保养维修类型
	 */
	@ApiModelProperty(value = "保养维修类型")
	private String maintType;
	/**
	 * 保养维修原因
	 */
	@ApiModelProperty(value = "保养维修原因")
	private String maintReason;
	/**
	 * 车况照片
	 */
	@ApiModelProperty(value = "车况照片")
	private String picture;
	/**
	 * 维修估算金额
	 */
	@ApiModelProperty(value = "维修估算金额")
	private String maintAmount;
	/**
	 * 打算维修地点
	 */
	@ApiModelProperty(value = "打算维修地点")
	private String maintAddress;
	/**
	 * 计划维修时间段
	 */
	@ApiModelProperty(value = "计划维修时间段")
	private String maintDate;
	/**
	 * 计划维修内容
	 */
	@ApiModelProperty(value = "计划维修内容")
	private String maintContext;
	/**
	 * 计划维修图片
	 */
	@ApiModelProperty(value = "计划维修图片")
	private String maintPicture;

	/**
	 * 实际维修时间
	 */
	@ApiModelProperty(value = "实际维修时间")
	private String maintFinishDate;
	/**
	 * 实际维修图片
	 */
	@ApiModelProperty(value = "实际维修图片")
	private String maintFinishPicture;
	/**
	 * 实际完成内容
	 */
	@ApiModelProperty(value = "实际完成内容")
	private String maintFinishContext;
	/**
	 * 实际维修估算金额
	 */
	@ApiModelProperty(value = "实际维修估算金额")
	private String maintFinishPrice;

	@ApiModelProperty(value = "审批流程id")
	private String workflowId;


	private String ext3;
	private String ext2;
	private String ext1;


}
