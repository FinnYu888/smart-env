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

import com.ai.apac.smartenv.statistics.entity.RptVehicleInfo;
import com.ai.apac.smartenv.statistics.vo.RptVehicleInfoVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.statistics.mapper.RptVehicleInfoMapper;
import com.ai.apac.smartenv.statistics.service.IRptVehicleInfoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
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
public class RptVehicleInfoServiceImpl extends BaseServiceImpl<RptVehicleInfoMapper, RptVehicleInfo> implements IRptVehicleInfoService {

	private IVehicleClient vehicleClient;
	
	@Override
	public IPage<RptVehicleInfoVO> selectRptVehicleInfoPage(IPage<RptVehicleInfoVO> page, RptVehicleInfoVO rptVehicleInfo) {
		return page.setRecords(baseMapper.selectRptVehicleInfoPage(page, rptVehicleInfo));
	}

	@Override
	@Async("statisticsThreadPool")
	public Boolean syncVehicleInfo(String date) {
		DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, fmt1);
		
		VehicleInfoVO vehicle = new VehicleInfoVO();
		vehicle.setIsDeleted(0);
		List<VehicleInfo> vehicleList = vehicleClient.listVehicle(vehicle).getData();
		
		RptVehicleInfo rptVehicleInfo = new RptVehicleInfo();
		for (VehicleInfo vehicleInfo : vehicleList) {
			rptVehicleInfo = new RptVehicleInfo();
			rptVehicleInfo.setDate(localDate);
			rptVehicleInfo.setEntityId(vehicleInfo.getId());
			rptVehicleInfo.setMonth(date.substring(0, 7));
			rptVehicleInfo.setTenantId(vehicleInfo.getTenantId());
			
			RptVehicleInfo detail = getOne(Condition.getQueryWrapper(rptVehicleInfo).last("LIMIT 1"));
			if (detail == null || detail.getId() == null) {
				save(rptVehicleInfo);
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
