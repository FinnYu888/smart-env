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
import com.ai.apac.smartenv.statistics.entity.RptPersonStay;
import com.ai.apac.smartenv.statistics.vo.RptPersonStayVO;
import com.ai.apac.smartenv.statistics.mapper.RptPersonStayMapper;
import com.ai.apac.smartenv.statistics.service.IRptPersonStayService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-09-11
 */
@Service
@AllArgsConstructor
public class RptPersonStayServiceImpl extends BaseServiceImpl<RptPersonStayMapper, RptPersonStay> implements IRptPersonStayService {

	private IAlarmInfoClient alarmInfoClient;
	
	@Override
	public IPage<RptPersonStayVO> selectRptPersonStayPage(IPage<RptPersonStayVO> page, RptPersonStayVO rptPersonStay) {
		return page.setRecords(baseMapper.selectRptPersonStayPage(page, rptPersonStay));
	}

	@Override
	@Async("statisticsThreadPool")
	public Boolean syncPersonStay(String date) {
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("date", date);
		removeByMap(columnMap);
		DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, fmt1);
		
		Long categoryId = 1227854269462351874L;// 人员工作路线/区域超时滞留告警
//		List<AlarmRuleInfo> alarmRuleInfoList = alarmRuleInfoClient.listAlarmRuleInfoByType(type).getData();
		List<AlarmInfo> alarmInfoList = alarmInfoClient.listAlarmInfoByCategory(categoryId, date).getData();
		
		
		Long lastEntityId = 0L;
		Long lastAlarmId = 0L;
		int lastDuration = 0;
		LocalDateTime startTime = null;
		
		for (int i = 0; i < alarmInfoList.size(); i++) {
			AlarmInfo alarmInfo = alarmInfoList.get(i);
			String dataStr = alarmInfo.getData();
			if (StringUtil.isNotBlank(dataStr)) {
				JSONObject data = JSONUtil.parseObj(dataStr);
				if (StringUtil.isBlank(data.getStr("DURATION")) || StringUtil.isBlank(data.getStr("eventTime"))) {
					continue;
				}
				int duration = (int) (Long.parseLong(data.getStr("DURATION")) / 1000);
				String eventTime = data.getStr("eventTime");// 2020-08-28 10:57:45
				if (!lastEntityId.equals(alarmInfo.getEntityId()) || lastDuration > duration) {
					lastAlarmId = alarmInfo.getId();
					if (eventTime.length() == 14) {
						startTime = LocalDateTime.parse(eventTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
					} else if (eventTime.length() == 19) {
						startTime = LocalDateTime.parse(eventTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					}
				}
				RptPersonStay personStay = new RptPersonStay();
				personStay.setEntityId(alarmInfo.getEntityId());
				personStay.setAlarmId(lastAlarmId);
				if (eventTime.length() == 14) {
					personStay.setStartTime(LocalDateTime.parse(eventTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
				} else if (eventTime.length() == 19) {
					personStay.setStartTime(LocalDateTime.parse(eventTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				}
				personStay.setDuration(duration);
				personStay.setDate(localDate);
				personStay.setMonth(date.substring(0, 7));
				personStay.setTenantId(alarmInfo.getTenantId());
				
				if (i + 1 == alarmInfoList.size()) {
					personStay.setEndTime(startTime.plusSeconds(duration));
				} else {
					AlarmInfo nextAlarmInfo = alarmInfoList.get(i + 1);
					String nextDataStr = nextAlarmInfo.getData();
					if (StringUtil.isNotBlank(nextDataStr)) {
						JSONObject nextData = JSONUtil.parseObj(nextDataStr);
						if (StringUtil.isNotBlank(nextData.getStr("DURATION"))) {
							int nextDuration = (int) (Long.parseLong(nextData.getStr("DURATION")) / 1000);
							if (duration > nextDuration) {
								personStay.setEndTime(startTime.plusSeconds(duration));
							}
						}
					}
				}
				save(personStay);
				lastEntityId = alarmInfo.getEntityId();
				lastDuration = duration;
			}
		}
		return true;
	}
}
