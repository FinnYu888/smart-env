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

import com.ai.apac.smartenv.common.constant.ApplicationConstant;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.ai.apac.smartenv.statistics.dto.SynthInfoDTO;
import com.ai.apac.smartenv.statistics.entity.VehicleDistanceInfo;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign接口类
 *
 */
@FeignClient(value = ApplicationConstant.APPLICATION_STATISTIC_NAME, fallback = IStatisticsClientFallback.class)
public interface IStatisticsClient {

    String API_PREFIX = "/client";
    String SYNC_VEHICLE_STAY = API_PREFIX + "/sync-vehicle-stay";
    String SYNC_PERSON_STAY = API_PREFIX + "/sync-person-stay";
    String SYNC_VEHICLE_INFO = API_PREFIX + "/sync-vehicle-info";
    String SYNC_PERSON_INFO = API_PREFIX + "/sync-person-info";
    String SYNC_PERSON_SAFE = API_PREFIX + "/sync-person-safe";
    String SYNC_PERSON_OUT_OF_AREA = API_PREFIX + "/sync-person-out-of-area";
    String SYNC_VEHICLE_OIL = API_PREFIX + "/sync-vehicle-oil";
    String SYNC_TOILET_INFO = API_PREFIX + "/sync-toilet-info";


    String VEHICLE_WORK_STATE_RUN = API_PREFIX + "/vehicle-work-state-run";

    @PostMapping(VEHICLE_WORK_STATE_RUN)
    R<Boolean> vehicleWorkStatRun(@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,@RequestParam("projectCodeList") List<String> projectCodeList);

    /*
     * 车辆违规停留
     */
    @PostMapping(SYNC_VEHICLE_STAY)
	R<Boolean> syncVehicleStay(@RequestParam("date") String date);

    /*
     * 车辆作业完成情况
     */
    @PostMapping(SYNC_VEHICLE_INFO)
    R syncVehicleInfo(@RequestParam("date") String date);

    /*
     * 人员作业完成情况
     */
    @PostMapping(SYNC_PERSON_INFO)
	R syncPersonInfo(@RequestParam("date") String date);

    /*
     * 人员违规停留
     */
    @PostMapping(SYNC_PERSON_STAY)
	R syncPersonStay(String date);

    /*
     * 人员主动安全
     */
    @PostMapping(SYNC_PERSON_SAFE)
	R syncPersonSafe(String date);

    /*
     * 人员脱离工作区域统计
     */
    @PostMapping(SYNC_PERSON_OUT_OF_AREA)
	R syncPersonOutOfArea(String date);

    @PostMapping(SYNC_VEHICLE_OIL)
	R syncVehicleOil(String date) throws IOException;

    @PostMapping(SYNC_TOILET_INFO)
	R syncToiletInfo(String date);


}
