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
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 实体类
 *
 * @author Blade
 * @since 2020-03-02
 */
@Data
@TableName("ai_kpi_target")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "KpiTarget对象", description = "KpiTarget对象")
public class KpiTarget extends TenantEntity {

    private static final long serialVersionUID = 1L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 考核指标名称
     */
    @ApiModelProperty(value = "考核指标名称")
    private String targetName;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long kpiTplId;
    private String stationId;

    @ApiModelProperty(value = "考核目标开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value = "考核目标结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private Long graderId;
    /**
     * kpi描述
     */
    @ApiModelProperty(value = "kpi描述")
    private String remark;


}
