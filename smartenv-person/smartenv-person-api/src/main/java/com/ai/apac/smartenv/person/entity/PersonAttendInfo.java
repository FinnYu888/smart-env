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
package com.ai.apac.smartenv.person.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * 人员考勤信息表实体类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Data
@TableName("ai_person_attend_info")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PersonAttendInfo对象", description = "人员考勤信息表")
public class PersonAttendInfo extends TenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	* 主键
	*/
		@ApiModelProperty(value = "主键")
		private Long id;
	/**
	* 人员ID
	*/
		@ApiModelProperty(value = "人员ID")
		private Long personId;
	/**
	* 姓名
	*/
		@ApiModelProperty(value = "姓名")
		private String personName;
	/**
	* 员工所属组织
	*/
		@ApiModelProperty(value = "员工所属组织")
		private Long personOrgId;
	/**
	* 考勤日期
	*/
		@ApiModelProperty(value = "考勤日期")
		private LocalDate attendTime;
	/**
	* 上班日期
	*/
		@ApiModelProperty(value = "上班日期")
		private Timestamp upTime;
	/**
	* 下班日期
	*/
		@ApiModelProperty(value = "下班日期")
		private Timestamp offTime;
	/**
	* 工作时长(秒单位)
	*/
		@ApiModelProperty(value = "工作时长(秒单位)")
		private String workDuration;
	/**
	* 工作里程(米单位)
	*/
		@ApiModelProperty(value = "工作里程(米单位)")
		private String workMiles;


}
