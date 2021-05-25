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
package com.ai.apac.smartenv.statistics.feign;

import com.ai.apac.smartenv.statistics.dto.SynthInfoDTO;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import com.ai.apac.smartenv.statistics.service.*;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 系统服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class StatisticsClient implements IStatisticsClient {

    private IRptVehicleStayService vehicleStayService;
    private IRptPersonStayService personStayService;
    private IRptVehicleInfoService vehicleInfoService;
    private IRptPersonInfoService personInfoService;
    private IRptPersonSafeService personSafeService;
    private IRptPersonOutOfAreaService personOutOfAreaService;
    private IRptVehicleOilService vehicleOilService;
    private IRptToiletInfoService toiletInfoService;
	private IDataStatisticsService dataStatisticsService;
	private IVehicleDistanceInfoService vehicleDistanceInfoService;
	private IVehicleWorkStatService vehicleWorkStatService;


	@Override
	public R<Boolean> vehicleWorkStatRun(String startTime, String endTime, List<String> projectCodeList) {
		vehicleWorkStatService.vehicleWorkStatRun(startTime,endTime,"",projectCodeList);
		return R.data(true);
	}

	@Override
	public R<Boolean> syncVehicleStay(String date) {
		vehicleStayService.syncVehicleStay(date);
		return R.status(true);
	}

	@Override
	public R syncVehicleInfo(String date) {
		vehicleInfoService.syncVehicleInfo(date);
		return R.status(true);
	}

	@Override
	public R syncPersonInfo(String date) {
		personInfoService.syncPersonInfo(date);
		return R.status(true);
	}

	@Override
	public R syncPersonStay(String date) {
		personStayService.syncPersonStay(date);
		return R.status(true);
	}

	@Override
	public R syncPersonSafe(String date) {
		personSafeService.syncPersonSafe(date);
		return R.status(true);
	}

	@Override
	public R syncPersonOutOfArea(String date) {
		personOutOfAreaService.syncPersonOutOfArea(date);
		return R.status(true);
	}

	@Override
	public R syncVehicleOil(String date) throws IOException {
		vehicleOilService.syncVehicleOil(date);
		return R.status(true);
	}

	@Override
	public R syncToiletInfo(String date) {
		toiletInfoService.syncToiletInfo(date);
		return R.status(true);
	}


}
