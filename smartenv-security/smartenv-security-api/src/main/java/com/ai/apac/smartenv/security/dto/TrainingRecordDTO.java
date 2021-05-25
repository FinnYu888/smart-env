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
package com.ai.apac.smartenv.security.dto;

import com.ai.apac.smartenv.security.entity.TrainingRecord;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 培训记录表数据传输对象实体类
 *
 * @author Blade
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "信息", description = "人员基本信息")
public class TrainingRecordDTO extends TrainingRecord {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "培训查询开始时间")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	Long trainingQueryStartTime;

	@ApiModelProperty(value = "培训查询结束时间")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	Long trainingQueryEndTime;

	@ApiModelProperty(value = "记录查询开始时间")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	Long recordStartTime;

	@ApiModelProperty(value = "记录查询结束时间")
	@JsonSerialize(using = ToStringSerializer.class, nullsUsing = NullSerializer.class)
	Long recordEndTime;
}
