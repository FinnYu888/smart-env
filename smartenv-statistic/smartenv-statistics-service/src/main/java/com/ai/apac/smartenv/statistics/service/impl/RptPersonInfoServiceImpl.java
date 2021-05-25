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

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.statistics.entity.RptPersonInfo;
import com.ai.apac.smartenv.statistics.entity.RptVehicleInfo;
import com.ai.apac.smartenv.statistics.vo.RptPersonInfoVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.statistics.mapper.RptPersonInfoMapper;
import com.ai.apac.smartenv.statistics.service.IRptPersonInfoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import lombok.AllArgsConstructor;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-09-09
 */
@Service
@AllArgsConstructor
public class RptPersonInfoServiceImpl extends BaseServiceImpl<RptPersonInfoMapper, RptPersonInfo> implements IRptPersonInfoService {

	private IPersonClient personClient;
	
	@Override
	public IPage<RptPersonInfoVO> selectRptPersonInfoPage(IPage<RptPersonInfoVO> page, RptPersonInfoVO rptPersonInfo) {
		return page.setRecords(baseMapper.selectRptPersonInfoPage(page, rptPersonInfo));
	}

	@Override
	@Async("statisticsThreadPool")
	public Boolean syncPersonInfo(String date) {
		DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, fmt1);
		
		PersonVO personVo = new PersonVO();;
		personVo.setIsDeleted(0);
		List<Person> personList = personClient.listPerson(personVo).getData();
		
		RptPersonInfo rptPersonInfo = new RptPersonInfo();
		for (Person person : personList) {
			rptPersonInfo = new RptPersonInfo();
			rptPersonInfo.setDate(localDate);
			rptPersonInfo.setEntityId(person.getId());
			rptPersonInfo.setMonth(date.substring(0, 7));
			rptPersonInfo.setTenantId(person.getTenantId());
			
			RptPersonInfo detail = getOne(Condition.getQueryWrapper(rptPersonInfo).last("LIMIT 1"));
			if (detail == null || detail.getId() == null) {
				save(rptPersonInfo);
			} else {
				Integer operationRate = detail.getOperationRate();
				if (operationRate == null) {
					operationRate = 0;
				}
				detail.setOperationRate(operationRate + (int) (Math.random() * (100 - operationRate)));// mock数据
				updateById(detail);
			}
		}
		return true;
	}

}
