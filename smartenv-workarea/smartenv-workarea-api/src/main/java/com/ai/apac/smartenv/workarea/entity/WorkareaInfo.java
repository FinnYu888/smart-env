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
package com.ai.apac.smartenv.workarea.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 工作区域信息实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_workarea_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WorkareaInfo对象", description = "工作区域信息")
public class WorkareaInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 工作区域ID
	*/
		@ApiModelProperty(value = "工作区域ID")
		@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
		private Long id;
	/**
	* 区域ID
	*/
		@ApiModelProperty(value = "区域ID")
		private Long regionId;
	/**
	* 1线
2区域
	*/
		@ApiModelProperty(value = "1线 2区域")
		private Long areaType;
	/**
	* 区域名称
	*/
		@ApiModelProperty(value = "区域名称")
		private String areaName;
	/**
	* 区域地址
	*/
		@ApiModelProperty(value = "区域地址")
		private String areaAddress;

	/**
	 * 所属大区
	 */
	@ApiModelProperty(value = "所属大区")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long division;

	/**
	 * 工作区域类型
	 */
	@ApiModelProperty(value = "1工作 2加油 3加水 4行驶 5维修")
	private Long workAreaType;


	/**
	 * 长度
	 */
	@ApiModelProperty(value = "长度")
	private String length;

	/**
	 * 宽度
	 */
	@ApiModelProperty(value = "宽度")
	private String width;

	/**
	 * 面积
	 */
	@ApiModelProperty(value = "面积")
	private String area;

	/**
	 * 绑定人员数量
	 */
	@ApiModelProperty(value = "绑定人员数量")
	private Long personCount;

	/**
	 * 绑定车辆数量
	 */
	@ApiModelProperty(value = "绑定车辆数量")
	private Long vehicleCount;
	/**
	 * 绑定类型
	 */
	@ApiModelProperty(value = "1人员2车辆")
	private Long bindType;
	/**
	 * 片区负责人
	 */
	@ApiModelProperty(value = "片区负责人")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
	private Long areaHead;
	/**
	 * 区域等级 / 路线等级
	 * 
	 */
	@ApiModelProperty(value = "区域等级")
	private Integer areaLevel;
}
