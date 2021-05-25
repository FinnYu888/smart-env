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

import com.ai.apac.smartenv.common.annotation.Latitude;
import com.ai.apac.smartenv.common.annotation.Longitude;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-02-11
 */
@Data
@TableName("ai_facility_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FacilityInfo对象", description = "FacilityInfo对象")
public class FacilityInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@TableField("FACILITY_NAME")
	private String facilityName;
	@TableField("FACILITY_TYPE")
	private String facilityType;
	@TableField("LNG")
	@Longitude
	private String lng;
	@TableField("LAT")
	@Latitude
	private String lat;
	@TableField("REGION_ID")
	private String regionId;
	@TableField("PARENT_REGION_ID")
	private String parentRegionId;
	@TableField("LOCATION")
	private String location;
	@TableField("PHONE")
	private String phone;
	@TableField("COMPANY_CODE")
	private String companyCode;
	@TableField("DIRECTOR")
	private String director;
	@TableField("EXT1")
	private String ext1;
	@TableField("EXT2")
	private String ext2;
	@TableField("EXT3")
	private String ext3;
	@TableField("FACILTY_VOLUME")
	private String faciltyVolume;
	@TableField("FACILTY_AREA")
	private String faciltyArea;
	@TableField("FACILTY_USE_DATE")
	private String faciltyUseDate;
	@TableField("FACILTY_GPB")
	private String faciltyGpb;
	@TableField("PROJECT_NO")
	private String projectNo;
	@TableField("DONE_DATE")
	private Timestamp doneDate;
	@TableField("DONE_CODE")
	private String doneCode;
	@TableField("OP_ID")
	private String opId;
	@TableField("ORG_ID")
	private String orgId;



}
