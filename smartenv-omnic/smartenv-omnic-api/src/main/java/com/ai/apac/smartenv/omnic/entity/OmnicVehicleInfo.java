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
package com.ai.apac.smartenv.omnic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.validator.constraints.Length;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 车辆基本信息表实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@ApiModel(value = "VehicleInfo对象", description = "车辆基本信息表")
public class OmnicVehicleInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 车辆基本信息表主键id
	*/
	/*
	 * @ApiModelProperty(value = "车辆基本信息表主键id") private Long id;
	 */
	/**
	* 车牌号
	*/
	@ApiModelProperty(value = "车牌号")
	@Length(max = 20, message = "车牌号长度不能超过20")
	@NotBlank(message = "需要输入车牌号")
	private String plateNumber;
	/**
	 * 车辆大类
	 */
	@ApiModelProperty(value = "车辆大类")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@NotNull(message = "需要输入车辆大类")
	private Long kindCode;
	/**
	* 车辆类型
	*/
	@ApiModelProperty(value = "车辆类型")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@NotNull(message = "需要输入车辆类型")
	private Long entityCategoryId;
	/**
	 * 所属部门	
	 */
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	@ApiModelProperty(value = "所属部门")
	@NotNull(message = "需要输入所属部门")
	private Long deptId;
	/**
	 * 加入时间
	 */
	@ApiModelProperty(value = "加入时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "需要输入加入日期")
	private Date deptAddTime;
	/**
	 * 移除时间
	 */
	@ApiModelProperty(value = "移除时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date deptRemoveTime;
	/**
	* 车辆初始里程
	*/
	@ApiModelProperty(value = "车辆初始里程")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal initGpsMile;
	/**
	* 首次安装车载GPS的时间
	*/
	@ApiModelProperty(value = "首次安装车载GPS的时间")
	private Timestamp initGpsDate;
	/**
	* GPS最后上线的时间
	*/
	@ApiModelProperty(value = "GPS最后上线的时间")
	private Timestamp lastGpsTime;
	/**
	* EPC编码，用于进道闸识别
	*/
	@ApiModelProperty(value = "EPC编码，用于进道闸识别")
	private String epc;
	/**
	* 发动机号
	*/
	@ApiModelProperty(value = "发动机号")
	private String engineNo;
	/**
	* 车辆品牌
	*/
	@ApiModelProperty(value = "车辆品牌")
	private String brand;
	/**
	* 吨位
	*/
	@ApiModelProperty(value = "吨位")
	private String tonnage;
	/**
	* 车辆型号
	*/
	@ApiModelProperty(value = "车辆型号")
	private String vehicleModel;
	/**
	* 发票号
	*/
	@ApiModelProperty(value = "发票号")
	private String invoice;
	/**
	* 行驶证号
	*/
	@ApiModelProperty(value = "行驶证号")
	private String drivingLicense;
	/**
	* 车辆识别代号
	*/
	@ApiModelProperty(value = "车辆识别代号")
	private String identifyNumber;
	/**
	* 车架号
	*/
	@ApiModelProperty(value = "车架号")
	private String vin;
	/**
	* 制造厂名称
	*/
	@ApiModelProperty(value = "制造厂名称")
	private String manufacturer;
	/**
	* 车辆产地
	*/
	@ApiModelProperty(value = "车辆产地")
	private String originProductionPlace;
	/**
	* 出厂日期
	*/
	@ApiModelProperty(value = "出厂日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Timestamp productionDate;
	/**
	* 驱动形式:前驱, 后驱, 四驱
	*/
	@ApiModelProperty(value = "驱动形式:前驱, 后驱, 四驱")
	private String drivetrainLayout;
	/**
	* 发动机功率
	*/
	@ApiModelProperty(value = "发动机功率")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal enginePower;
	/**
	* 自有重量(t)
	*/
	@ApiModelProperty(value = "自有重量(t)")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal ownWeight;
	/**
	* 发动机排量(L)
	*/
	@ApiModelProperty(value = "发动机排量(L)")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal engineDisplacement;
	/**
	* 主发动机厂牌
	*/
	@ApiModelProperty(value = "主发动机厂牌")
	private String mainEngineBrand;
	/**
	* 主发动机型号
	*/
	@ApiModelProperty(value = "主发动机型号")
	private String mainEngineModel;
	/**
	* 副发动机厂牌
	*/
	@ApiModelProperty(value = "副发动机厂牌")
	private String supplementaryEngineBrand;
	/**
	* 副发动机型号
	*/
	@ApiModelProperty(value = "副发动机型号")
	private String supplementaryEngineModel;
	/**
	* 底盘厂牌
	*/
	@ApiModelProperty(value = "底盘厂牌")
	private String chassisFactoryBrand;
	/**
	* 底盘型号
	*/
	@ApiModelProperty(value = "底盘型号")
	private String chassisFactoryModel;
	/**
	* 轮胎规格
	*/
	@ApiModelProperty(value = "轮胎规格")
	private String tireType;
	/**
	* 轮胎数量
	*/
	@ApiModelProperty(value = "轮胎数量")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal tireAmount;
	/**
	* 整备质量
	*/
	@ApiModelProperty(value = "整备质量")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal curbWeight;
	/**
	 * 核载质量
	 */
	@ApiModelProperty(value = "核载质量")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal ratedWeight;
	/**
	* 排放标准
	*/
	@ApiModelProperty(value = "排放标准")
	private String emissionStandard;
	/**
	* 燃油种类
	*/
	@ApiModelProperty(value = "燃油种类")
	private String fuelType;
	/**
	* 燃油标号
	*/
	@ApiModelProperty(value = "燃油标号")
	private String roz;
	/**
	 * 燃油性质
	 */
	@ApiModelProperty(value = "燃油性质")
	private String fuelProperty;
	/**
	* 油箱容量(L)
	*/
	@ApiModelProperty(value = "油箱容量(L)")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	private BigDecimal fuelCapacity;
	/**
	* 责任人
	*/
	@ApiModelProperty(value = "责任人")
	private String personLiable;
	/**
	* 联系方式
	*/
	@ApiModelProperty(value = "联系方式")
	private String contactPhone;

}
