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
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IStatisticsClientFallback implements IStatisticsClient {

	@Override
	public R<Boolean> vehicleWorkStatRun(String startTime, String endTime, List<String> projectCodeList) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> syncVehicleStay(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncVehicleInfo(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncPersonInfo(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncPersonStay(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncPersonSafe(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncPersonOutOfArea(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncVehicleOil(String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R syncToiletInfo(String date) {
		return R.fail("获取数据失败");
	}
}
