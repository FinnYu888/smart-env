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
package com.ai.apac.smartenv.assessment.dto;

import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 考核模板明细数据传输对象实体类
 *
 * @author Blade
 * @since 2020-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KpiTplDetailDTO extends KpiTplDetail {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "具体工作任务和行动")
	@Length(max = 255, message = "具体工作任务和行动长度不能超过255")
	@NotBlank(message = "需要输入具体工作任务和行动")
	private String kpiDescription;

	/**
	 * 指标分类
	 */
	@ApiModelProperty(value = "指标分类")
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull(message = "需要输入考核指标分类")
	private String kpiCatalogName;

}