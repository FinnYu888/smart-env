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

import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import com.ai.apac.smartenv.facility.feign.IToiletClient;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import com.ai.apac.smartenv.statistics.entity.RptToiletInfo;
import com.ai.apac.smartenv.statistics.vo.RptToiletInfoVO;
import com.ai.apac.smartenv.statistics.mapper.RptToiletInfoMapper;
import com.ai.apac.smartenv.statistics.service.IRptToiletInfoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * @since 2020-09-18
 */
@Service
@AllArgsConstructor
public class RptToiletInfoServiceImpl extends BaseServiceImpl<RptToiletInfoMapper, RptToiletInfo> implements IRptToiletInfoService {

	private IToiletClient toiletClient;
	
	@Override
	public IPage<RptToiletInfoVO> selectRptToiletInfoPage(IPage<RptToiletInfoVO> page, RptToiletInfoVO rptToiletInfo) {
		return page.setRecords(baseMapper.selectRptToiletInfoPage(page, rptToiletInfo));
	}

	@Override
	@Async("statisticsThreadPool")
	public Boolean syncToiletInfo(String date) {
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("date", date);
		removeByMap(columnMap);
		
		DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, fmt1);
		
		List<ToiletInfoVO> toiletInfoList = toiletClient.listToiletAll().getData();
		RptToiletInfo rptToiletInfo = new RptToiletInfo();
		for (ToiletInfo toiletInfo : toiletInfoList) {
			rptToiletInfo = new RptToiletInfo();
			rptToiletInfo.setDate(localDate);
			rptToiletInfo.setEntityId(toiletInfo.getId());
			rptToiletInfo.setMonth(date.substring(0, 7));
			rptToiletInfo.setTenantId(toiletInfo.getTenantId());
			rptToiletInfo.setWaterVolume(100);
			rptToiletInfo.setElectricVolume(100);
			rptToiletInfo.setServiceNumber(1000);;
			save(rptToiletInfo);
		}
		return true;
	}
}
