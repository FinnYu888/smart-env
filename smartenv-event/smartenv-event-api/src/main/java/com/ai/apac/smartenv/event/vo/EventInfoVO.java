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
package com.ai.apac.smartenv.event.vo;

import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventMedium;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 事件基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-02-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "EventInfoVO对象", description = "事件基本信息表")
public class EventInfoVO extends EventInfo {
	private static final long serialVersionUID = 1L;

	private List<EventAssignedHistoryVO> assignedHistoryVOS;

	/**
	 * 整改前照片
	 */
	@ApiModelProperty(value = "整改前照片")
	private List<EventMediumVO> preEventMediumList;

	/**
	 * 整改后照片
	 */
	@ApiModelProperty(value = "整改后照片")
	private List<EventMediumVO> afterEventMediumList;

	/**
	 * 事件类型
	 */
	@ApiModelProperty(value = "事件类型")
	private String eventTypeName;
	/**
	 * 事件检查类型
	 */
	@ApiModelProperty(value = "事件检查类型")
	private String eventInspectTypeName;
	/**
	 * 事件等级
	 */
	@ApiModelProperty(value = "事件等级")
	private String eventLevelName;

	/**
	 * 状态
	 */
	@ApiModelProperty(value = "状态")
	private String statusName;

	/**
	 * 区域名称
	 */
	@ApiModelProperty(value = "区域名称")
	private String workareaName;

	@ApiModelProperty(value = "业务区域主管名称")
	private String workAreaManageName;

	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	private Timestamp startTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	private Timestamp endTime;

	/**
	 * 按钮列表
	 */
	@ApiModelProperty(value = "按钮列表")
	private List<ButtonsVO> buttons;

	/**
	 * 抄送人员
	 */
	@ApiModelProperty(value = "抄送人员")
	private List<CcPeopleVO> ccPeopleVOS;


	@ApiModelProperty(value = "Kpi名称")
	private String eventKpiName;

}
