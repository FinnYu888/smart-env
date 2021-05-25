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

import com.ai.apac.smartenv.address.entity.TrackExportTask;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 历史轨迹导出任务表视图实体类
 *
 * @author Blade
 * @since 2020-03-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "TrackExportTaskVO对象", description = "历史轨迹导出任务表")
public class TrackExportTaskVO extends TrackExportTask {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "导出状态 1：进行中  2 已完成")
	private String exportStatusName;

	/**
	 * 查询开始时间
	 */
	@ApiModelProperty(value = "查询开始时间")
	private Timestamp conditionBeginTime;
	/**
	 * 查询结束时间
	 */
	@ApiModelProperty(value = "查询结束时间")
	private Timestamp conditionEndTime;
	/**
	 * 设备状态 1、开启  2 关闭 3 空
	 */
	@ApiModelProperty(value = "设备状态 1、开启  2 关闭 3 空")
	private Integer deviceStatus;

	@ApiModelProperty(value = "设备状态 1、开启  2 关闭 3 空")
	private String deviceStatusName;

}
