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

import com.ai.apac.smartenv.statistics.entity.RptVehicleOil;
import com.ai.apac.smartenv.statistics.vo.RptVehicleOilVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.statistics.mapper.RptVehicleOilMapper;
import com.ai.apac.smartenv.statistics.service.IRptVehicleOilService;

import java.io.IOException;
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
 * @since 2020-09-16
 */
@Service
@AllArgsConstructor
public class RptVehicleOilServiceImpl extends BaseServiceImpl<RptVehicleOilMapper, RptVehicleOil> implements IRptVehicleOilService {

	private IVehicleClient vehicleClient;
	
	@Override
	public IPage<RptVehicleOilVO> selectRptVehicleOilPage(IPage<RptVehicleOilVO> page, RptVehicleOilVO rptVehicleOil) {
		return page.setRecords(baseMapper.selectRptVehicleOilPage(page, rptVehicleOil));
	}

	@Override
	@Async("statisticsThreadPool")
	public Boolean syncVehicleOil(String date) throws IOException {
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("date", date);
		removeByMap(columnMap);
		DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, fmt1);
		
		
		// 查询所有机动车
		VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();
		vehicleInfoVO.setKindCode(1225410941508714499L);
		List<VehicleInfo> listVehicle = vehicleClient.listVehicle(new VehicleInfoVO()).getData();
		
		for (VehicleInfo vehicleInfo : listVehicle) {
			
			/*List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(vehicleInfo.getId(), vehicleInfo.getTenantId());
			if (deviceRelList == null || deviceRelList.isEmpty()) {
				continue;
			}
			for (DeviceRel deviceRel : deviceRelList) {
				DeviceInfo deviceInfo = DeviceCache.getDeviceById(deviceRel.getTenantId(), deviceRel.getDeviceId());
				String deviceCode = deviceInfo.getDeviceCode();
				
				
				Map<String, Object> params = new HashMap<>();
			    params.put("deviceId", deviceInfo.getDeviceCode());
			    params.put("beginTime", date.replaceAll("-", "") + "000000");
			    params.put("endTime", date.replaceAll("-", "") + "235959");
			    //从大数据获取轨迹数据
			    BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
			    List<TrackPositionDto> data = bigDataBody.getData();
			    if (data != null && !data.isEmpty()) {
			    	TrackPositionDto trackPositionDto = data.get(0);
			        List<Position> tracks = trackPositionDto.getTracks();
			        Statistics statistics = trackPositionDto.getStatistics();
					if ((tracks == null || tracks.isEmpty()) && StringUtil.isBlank(statistics.getTotalDistance())) {
						continue;
					}
			        if (tracks != null && !tracks.isEmpty()) {
			        	String oilLeft = tracks.get(0).getOilLeft();
			        	String oilLeft = tracks.get(0).getOilLeft();
					}
			        
			        
				}
			    
			}*/
			
			
			RptVehicleOil oil = new RptVehicleOil();
			oil.setEntityId(vehicleInfo.getId());
			oil.setStartOil(30);
			oil.setEndOil(40);
			oil.setAddOil(50);
			oil.setOilConsumption(40);
			oil.setMileage(100);
			oil.setOilConsumptionHundred(40);
			oil.setTenantId(vehicleInfo.getTenantId());
			oil.setDate(localDate);
			oil.setMonth(date.substring(0, 7));
			save(oil);
		}
		
		return true;
	}

}
