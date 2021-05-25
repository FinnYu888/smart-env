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

import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;
import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 告警基本信息表包装类,返回视图层所需的字段
 *
 * @author Blade
 * @since 2020-02-18
 */
public class AlarmInfoWrapper extends BaseEntityWrapper<AlarmInfo, AlarmInfoVO>  {

	public static AlarmInfoWrapper build() {
		return new AlarmInfoWrapper();
 	}

	@Override
	public AlarmInfoVO entityVO(AlarmInfo alarmInfo) {
		AlarmInfoVO alarmInfoVO = BeanUtil.copy(alarmInfo, AlarmInfoVO.class);

		return alarmInfoVO;
	}

	public AlarmInfoHandleInfoVO copyAlarmInfo4AlarmHandleInfoVO(AlarmInfo alarmInfo) {
		AlarmInfoHandleInfoVO alarmInfoHandleInfoVO = BeanUtil.copy(alarmInfo, AlarmInfoHandleInfoVO.class);
		return alarmInfoHandleInfoVO;
	}
	
	public List<AlarmInfoHandleInfoVO> listAlarmHandleInfoVO(List<AlarmInfo> alarmInfoList) {
		return alarmInfoList.stream().map(this::copyAlarmInfo4AlarmHandleInfoVO).collect(Collectors.toList());
	}

}
