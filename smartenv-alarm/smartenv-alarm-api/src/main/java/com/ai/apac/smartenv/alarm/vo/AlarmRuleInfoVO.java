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
package com.ai.apac.smartenv.alarm.vo;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 告警规则基本信息表视图实体类
 *
 * @author Blade
 * @since 2020-02-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlarmRuleInfoVO对象", description = "告警规则基本信息表")
public class AlarmRuleInfoVO extends AlarmRuleInfo {
	private static final long serialVersionUID = 1L;
	/**
	 * 告警规则实体名称
	 */
	private String entityCategoryName;
	/**
	 * 告警规则参数信息
	 */
	private List<AlarmRuleExtVO> alarmRuleExtVOList;
	/**
	 * 告警规则绑定实体类型信息（暂时是车辆类型）
	 */
	private List<AlarmRuleRelVO> alarmRuleRelVOList;
	/**
	 * 规则绑定的实体类型名称
	 */
	private String relateEntityTypeNames;

}
