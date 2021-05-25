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

import com.ai.apac.smartenv.address.entity.AttendanceExportTask;
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
@ApiModel(value = "AttendanceExportTaskVO对象", description = "考勤记录导出任务表")
public class AttendanceExportTaskVO extends AttendanceExportTask {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("所有区域名称")
	private String regionNameTags;
	@ApiModelProperty("所有部门名称")
	private String deptNameTags;
	@ApiModelProperty("所有分类名称")
	private String categoryNameTags;

	@ApiModelProperty("查询日期")
	private String conditionDate;
	@ApiModelProperty("导出状态名称")
	private String exportStatusName;

}
