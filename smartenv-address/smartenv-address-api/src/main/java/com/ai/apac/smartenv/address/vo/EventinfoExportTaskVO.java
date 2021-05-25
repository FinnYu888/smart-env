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
package com.ai.apac.smartenv.address.vo;

import com.ai.apac.smartenv.address.entity.EventinfoExportTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤记录导出任务表视图实体类
 *
 * @author Blade
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventinfoExportTaskVO对象", description = "事件记录导出任务表")
public class EventinfoExportTaskVO extends EventinfoExportTask {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "导出状态 1：进行中  2 已完成")
	private String exportStatusName;
}
