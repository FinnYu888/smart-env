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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * 记录加油信息实体类
 *
 * @author Blade
 * @since 2020-08-13
 */
@Data
@TableName("ai_refuel_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RefuelInfo对象", description = "记录加油信息")
public class RefuelInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 加油数据主键Id
	*/
//		@ApiModelProperty(value = "加油数据主键Id")
//		private Long id;
	/**
	* 加油车辆Id
	*/
		@ApiModelProperty(value = "加油车辆Id")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long vehicleId;
	/**
	* 加油前车辆公里数
	*/
		@ApiModelProperty(value = "加油前车辆公里数")
		private String beforeKm;
	/**
	* 加油时间
	*/
		@ApiModelProperty(value = "加油时间")
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		@NotNull(message = "需要输入加油时间")
		private LocalDateTime refuelTime;
	/**
	* 加油区域id
	*/
		@ApiModelProperty(value = "加油区域id")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long areaId;
	/**
	* 加油人
	*/
		@ApiModelProperty(value = "加油人")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long personId;
	/**
	* 实体分类
	*/
		@ApiModelProperty(value = "实体分类")
		@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
		private Long entityCategoryId;
	/**
	* 油品
	*/
		@ApiModelProperty(value = "油品")
		private Integer oilType;
	/**
	* 油单价每升
	*/
		@ApiModelProperty(value = "油单价每升")
		private String perPrice;
	/**
	* 加油总价
	*/
		@ApiModelProperty(value = "加油总价")
		private String amountPrice;
	/**
	* 加油总量单位升
	*/
		@ApiModelProperty(value = "加油总量单位升")
		private String oilVolume;
	/**
	* 车辆仪表盘
	*/
		@ApiModelProperty(value = "车辆仪表盘")
		private String picCarDb;
	/**
	* 加油站仪表照
	*/
		@ApiModelProperty(value = "加油站仪表照")
		private String picGasBd;
	/**
	* 加油小票
	*/
		@ApiModelProperty(value = "加油小票")
		private String picReceipt;
	/**
	* 照片备用字段
	*/
		@ApiModelProperty(value = "照片备用字段")
		private String picBackup;


}
