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
package com.ai.apac.smartenv.statistics.service.impl;

import com.ai.apac.smartenv.alarm.entity.AlarmInfo;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.statistics.entity.RptPersonSafe;
import com.ai.apac.smartenv.statistics.vo.RptPersonSafeVO;
import com.ai.apac.smartenv.statistics.mapper.RptPersonSafeMapper;
import com.ai.apac.smartenv.statistics.service.IRptPersonSafeService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import lombok.AllArgsConstructor;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-09-11
 */
@Service
@AllArgsConstructor
public class RptPersonSafeServiceImpl extends BaseServiceImpl<RptPersonSafeMapper, RptPersonSafe> implements IRptPersonSafeService {

	private IAlarmInfoClient alarmInfoClient;
	
	@Override
	public IPage<RptPersonSafeVO> selectRptPersonSafePage(IPage<RptPersonSafeVO> page, RptPersonSafeVO rptPersonSafe) {
		return page.setRecords(baseMapper.selectRptPersonSafePage(page, rptPersonSafe));
	}

	@Override
	@Async("statisticsThreadPool")
	public Boolean syncPersonSafe(String date) {
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("date", date);
		removeByMap(columnMap);
		DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, fmt1);
		
		Long categoryId = 1227854314542731267L;// 人员主动安全
//		List<AlarmInfo> alarmInfoList = alarmInfoClient.listAlarmInfoByCategory(categoryId, date).getData();
		List<AlarmInfo> alarmInfoList = new ArrayList<>();// 数据很多，超时
		
		for (AlarmInfo alarmInfo : alarmInfoList) {
			if (alarmInfo.getEntityId() == null) {
				continue;
			}
			RptPersonSafe personStay = new RptPersonSafe();
			personStay.setEntityId(alarmInfo.getEntityId());
			personStay.setAlarmId(0L);
			personStay.setStartTime(alarmInfo.getAlarmTime().toLocalDateTime());
			personStay.setDuration(0);
			personStay.setDate(localDate);
			personStay.setMonth(date.substring(0, 7));
			personStay.setTenantId(alarmInfo.getTenantId());
			save(personStay);
		}
		return true;
	}

}
