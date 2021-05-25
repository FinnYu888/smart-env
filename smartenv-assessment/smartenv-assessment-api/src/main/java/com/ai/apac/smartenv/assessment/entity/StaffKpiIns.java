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
package com.ai.apac.smartenv.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 考核实例表，存放每个人的kpi实体类
 *
 * @author Blade
 * @since 2020-02-08
 */
@Data
@TableName("ai_staff_kpi_ins")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "StaffKpiIns对象", description = "考核实例表，存放每个人的kpi")
public class StaffKpiIns extends TenantEntity {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;


	@JsonSerialize(using = ToStringSerializer.class)
	private Long kpiTargetId;

	private String kpiTargetName;
	/**
	* 员工ID
	*/
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "员工ID")
		private Long staffId;
	/**
	* 考核开始时间
	*/
	@ApiModelProperty(value = "考核开始日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
		private Date startTime;
	/**
	* 考核开始时间
	*/
	@ApiModelProperty(value = "考核结束日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
		private Date endTime;
	/**
	* 评分时间
	*/
	@ApiModelProperty(value = "评分日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
		private Date scoreTime;
	/**
	* 得分
	*/
		@ApiModelProperty(value = "得分")
		private String totalScore;

		private String grade;

	@JsonSerialize(using = ToStringSerializer.class,nullsUsing = NullSerializer.class)
	private Long scorer;

	private String staffRemark;
	private String managerRemark;
	private String kpiResult;

	/**
	 * 最终结束时间
	 */
	@ApiModelProperty(value = "考核最后结束日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date deadLine;

}
