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
package com.ai.apac.smartenv.inventory.vo;

import com.ai.apac.smartenv.inventory.entity.ResOperate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 视图实体类
 *
 * @author Blade
 * @since 2020-02-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ResOperateVO对象", description = "ResOperateVO对象")
public class ResOperateVO extends ResOperate {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "查询条件开始时间")
	/*@DateTimeFormat(
			pattern = "yyyy/MM/dd HH:mm:ss"
	)
	@JsonFormat(
			pattern = "yyyy/MM/dd HH:mm:ss"
	)*/
	private Timestamp startDate;
	@ApiModelProperty(value = "查询条件结束时间")
	/*@DateTimeFormat(
			pattern = "yyyy/MM/dd HH:mm:ss"
	)
	@JsonFormat(
			pattern = "yyyy/MM/dd HH:mm:ss"
	)*/
	private Timestamp endDate;
	@ApiModelProperty(value = "申请人工号")
	private String purchasingAgentId;

}
