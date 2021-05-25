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
package com.ai.apac.smartenv.alarm.wrapper;

import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleRel;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleRelVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 告警规则关联表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-02-07
 */
public class AlarmRuleRelWrapper extends BaseEntityWrapper<AlarmRuleRel, AlarmRuleRelVO>  {

	public static AlarmRuleRelWrapper build() {
		return new AlarmRuleRelWrapper();
 	}

	@Override
	public AlarmRuleRelVO entityVO(AlarmRuleRel alarmRuleRel) {
		AlarmRuleRelVO alarmRuleRelVO = BeanUtil.copy(alarmRuleRel, AlarmRuleRelVO.class);

		return alarmRuleRelVO;
	}

	public AlarmRuleRel voEntity(AlarmRuleRelVO alarmRuleRelVO) {
		AlarmRuleRel alarmRuleRel = BeanUtil.copy(alarmRuleRelVO, AlarmRuleRel.class);
		return alarmRuleRel;
	}
	
	public List<AlarmRuleRel> listEntity(List<AlarmRuleRelVO> alarmRuleRelVOList) {
		return alarmRuleRelVOList.stream().map(this::voEntity).collect(Collectors.toList());
	}

}
