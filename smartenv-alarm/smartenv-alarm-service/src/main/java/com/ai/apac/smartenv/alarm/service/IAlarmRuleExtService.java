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
package com.ai.apac.smartenv.alarm.service;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleExt;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleExtVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

/**
 * 告警规则参数表 服务类
 *
 * @author Blade
 * @since 2020-02-07
 */
public interface IAlarmRuleExtService extends BaseService<AlarmRuleExt> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param alarmRuleExt
	 * @return
	 */
	IPage<AlarmRuleExtVO> selectAlarmRuleExtPage(IPage<AlarmRuleExtVO> page, AlarmRuleExtVO alarmRuleExt);

	/**
	 * 根据告警规则Id删除扩展信息(逻辑删)
	 * @param alarmRuleId
	 */
	void removeByAlarmRuleId(Long alarmRuleId);

	/**
	 * 根据规则id列出所有属性
	 * @param alarmRuleId
	 * @return
	 */
	List<AlarmRuleExt> listByAlarmRuleId(@NonNull Long alarmRuleId);

	/**
	 * 循环保存或者更新数据
	 * @param alarmRuleExtList
	 */
	void saveOrUpdateAlarmRuleExtBatch(@NonNull List<AlarmRuleExt> alarmRuleExtList);

	/**
	 * 根据alarmRuleExtVOS计算父子关系
	 * @param alarmRuleExtVOS
	 * @return
	 */
	List<AlarmRuleExtVO> calculateRelationship(List<AlarmRuleExtVO> alarmRuleExtVOS);
}
