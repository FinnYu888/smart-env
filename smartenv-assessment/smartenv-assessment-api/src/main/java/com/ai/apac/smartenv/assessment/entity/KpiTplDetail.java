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

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springblade.core.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springblade.core.tenant.mp.TenantEntity;

/**
 * 考核模板明细实体类
 *
 * @author Blade
 * @since 2020-03-02
 */
@Data
@TableName("ai_kpi_tpl_detail")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "KpiTplDetail对象", description = "考核模板明细")
public class KpiTplDetail extends TenantEntity {

	private static final long serialVersionUID = 1L;
	@JsonSerialize(
			using = ToStringSerializer.class
	)
	private Long id;
	/**
	* 考核指标名称
	*/
	@JsonSerialize(
			using = ToStringSerializer.class
	)
		@ApiModelProperty(value = "考核指标ID")
		private Long kpiTplId;
	/**
	* 考核指标名称
	*/
	@JsonSerialize(
			using = ToStringSerializer.class
	)
		@ApiModelProperty(value = "考核指标id")
		private Long kpiId;
	/**
	 * 考核指标名称
	 */
	@ApiModelProperty(value = "考核指标名称")
	private String kpiName;


	/**
	* kpi描述
	*/
		@ApiModelProperty(value = "kpi描述")
		private String kpiRemark;
	/**
	* 评分标准
	*/
		@ApiModelProperty(value = "评分标准")
		private String appraisalCriteria;
	/**
	* 参考权重，单位是百分比
	*/
		@ApiModelProperty(value = "参考权重，单位是百分比")
		private Integer weighting;
	/**
	* 排序
	*/
		@ApiModelProperty(value = "排序")
		private Integer sort;


}
