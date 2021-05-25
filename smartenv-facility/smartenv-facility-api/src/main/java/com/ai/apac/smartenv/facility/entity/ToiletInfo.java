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
package com.ai.apac.smartenv.facility.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-09-16
 */
@Data
@TableName("ai_toilet_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ToiletInfo对象", description = "ToiletInfo对象")
public class ToiletInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	* 公厕名称
	*/
		@ApiModelProperty(value = "公厕名称")
		private String toiletName;
	/**
	* 公厕编码
	*/
		@ApiModelProperty(value = "公厕编码")
		private String toiletCode;
	/**
	* 公厕级别
	*/
		@ApiModelProperty(value = "公厕级别")
		private String toiletLevel;
	/**
	* 公厕头像
	*/
		@ApiModelProperty(value = "公厕头像")
		private String toiletImage;
	/**
	* 公厕联系号码
	*/
		@ApiModelProperty(value = "公厕联系号码")
		private String phoneNumber;
	/**
	* 公厕PM值
	*/
		@ApiModelProperty(value = "公厕PM值")
		private String toiletPm;
	/**
	* 公厕清洁分
	*/
		@ApiModelProperty(value = "公厕清洁分")
		private String clearPoint;
	/**
	* 公厕清洁描述
	*/
		@ApiModelProperty(value = "公厕清洁描述")
		private String clearDesc;
	/**
	* 公厕负责人
	*/
		@ApiModelProperty(value = "公厕负责人")
		private Long chargePersonId;
	/**
	* 公厕是否支持安装终端
	*/
		@ApiModelProperty(value = "公厕是否支持安装终端")
		private String supportDevice;

		@ApiModelProperty(value = "所属单位")
		private String companyCode;

	/**
	* 所属部门
	*/
		@ApiModelProperty(value = "所属部门")
		private Long deptId;
	/**
	* 所属路线/区域
	*/
		@ApiModelProperty(value = "所属路线/区域")
		private Long workareaId;
	/**
	* 所属片区
	*/
		@ApiModelProperty(value = "所属片区")
		private Long regionId;
	/**
	* 经度
	*/
		@ApiModelProperty(value = "经度")
		private String lng;
	/**
	* 纬度
	*/
		@ApiModelProperty(value = "纬度")
		private String lat;
	/**
	* 公厕地址
	*/
		@ApiModelProperty(value = "公厕地址")
		private String location;
	/**
	* 公厕详细地址
	*/
		@ApiModelProperty(value = "公厕详细地址")
		private String detailLocation;
	/**
	* 公厕工作状态：正常，关闭，临时关闭
	*/
		@ApiModelProperty(value = "公厕工作状态：正常，关闭，临时关闭")
		private String workStatus;
	/**
	* 公厕二维码
	*/
		@ApiModelProperty(value = "公厕二维码")
		private String toiletQrCode;


}
