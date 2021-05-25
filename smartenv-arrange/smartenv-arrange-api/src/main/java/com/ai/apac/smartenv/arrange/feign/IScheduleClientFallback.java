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
package com.ai.apac.smartenv.arrange.feign;

import com.ai.apac.smartenv.arrange.entity.*;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IScheduleClientFallback implements IScheduleClient {

	@Override
	public R<List<ScheduleObject>> listUnfinishScheduleByEntity(Long entityId, String entityType) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> checkNowNeedWork(Long entityId, String entityType) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> checkNeedWork(Long entityId, String entityType, Date checkTime) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> unbindSchedule(Long entityId, String entityType) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Integer> countVehicleForToday(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Integer> countPersonForToday(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> checkTodayNeedWork(Long entityId, String entityType) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<Schedule>> listAllSchedule() {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Schedule> getScheduleById(Long scheduleId) {
		return R.fail("获取数据失败");
	}


	@Override
	public R<List<ScheduleObject>> listAllScheduleObjectByDateAndType(String date, String entityType) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listAllScheduleObjectByDate(LocalDate date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<ScheduleObject> getScheduleObjectById(Long scheduleObjectId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> getScheduleObjectByEntityAndDate(Long entityId, String entityType,
			String scheduleDate) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listVehicleForToday(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listVehicleForNow(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listPersonForNow(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listEntityForNow(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listPersonForToday(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R saveScheduleWork(ScheduleWork scheduleWork) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleAttendanceDetail>> getAttendanceDetailListByAttendanceId(Long id) {
		return R.fail("获取数据失败");
	}
//
//	@Override
//	public R<String> addVehicleAttendance(ScheduleAttendanceVO attendanceVO) {
//		return R.fail("获取数据失败");
//	}

	@Override
	public R<List<ScheduleAttendance>> getAttendance(ScheduleAttendance scheduleAttendance) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleAttendance>> getAttendanceByDate(ScheduleAttendance scheduleAttendance, String date) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Map<Long,Boolean>> checkTodayNeedWorkMap(@RequestParam(value = "scheduleVehicleIdList")List<Long> scheduleVehicleIdList, @RequestParam("entityType") String entityType) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listScheduleObject(String entityType, LocalDate date, LocalDate localDate,
			String entityIdStr) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listScheduleObjectByScheduleId(Long scheduleId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<ScheduleObject>> listScheduleObjectByCondition(ScheduleObject scheduleObject) {
		return R.fail("获取数据失败");
	}
}
