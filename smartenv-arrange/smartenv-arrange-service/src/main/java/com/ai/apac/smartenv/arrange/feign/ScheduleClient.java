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
import com.ai.apac.smartenv.arrange.service.*;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 
 * Copyright: Copyright (c) 2020 Asiainfo
 * 
 * @ClassName: ScheduleClient.java
 * @Description: 排班Feign实现类
 *
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月20日 下午6:35:51 
 *
 * Modification History:
 * Date         Author          Version            Description
 *------------------------------------------------------------
 * 2020年2月20日     zhaoaj           v1.0.0               修改原因
 */
@RestController
@AllArgsConstructor
public class ScheduleClient implements IScheduleClient {

	private IScheduleService scheduleService;
	private IScheduleObjectService scheduleObjectService;
	private IScheduleWorkService scheduleWorkService;

	private IScheduleAttendanceService scheduleAttendanceService;
	private IScheduleAttendanceDetailService scheduleAttendanceDetailService;

	@Override
	@GetMapping(UNFINISH_SCHEDULE_BY_ENTITY)
	public R<List<ScheduleObject>> listUnfinishScheduleByEntity(Long entityId, String entityType) {
		return R.data(scheduleObjectService.listUnfinishScheduleByEntity(entityId, entityType));
	}

	@Override
	@GetMapping(CHECK_NOW_NEED_WORK)
	public R<Boolean> checkNowNeedWork(Long entityId, String entityType) {
		return R.data(scheduleObjectService.checkNeedWork(entityId, entityType, LocalDateTime.now()));
	}
	@Override
	@GetMapping(CHECK_TODAY_NEED_WORK)
	public R<Boolean> checkTodayNeedWork(Long entityId, String entityType) {
		return R.data(scheduleObjectService.checkTodayNeedWork(entityId, entityType));
	}

	@Override
	@GetMapping(CHECK_NEED_WORK)
	public R<Boolean> checkNeedWork(Long entityId, String entityType, Date checkTime) {
		LocalDateTime localDateTime = checkTime.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
		return R.data(scheduleObjectService.checkNeedWork(entityId, entityType, localDateTime));
	}

	@Override
	@PostMapping(UNBIND_SCHEDULE)
	public R<Boolean> unbindSchedule(Long entityId, String entityType) {
		return R.data(scheduleObjectService.unbindSchedule(entityId, entityType));
	}

	@Override
	@GetMapping(COUNT_VEHICLE_FOR_TODAY)
	public R<Integer> countVehicleForToday(@RequestParam String tenantId) {
		return R.data(scheduleObjectService.countByDate(LocalDate.now(), ArrangeConstant.ScheduleObjectEntityType.VEHICLE, tenantId, null));
	}

	@Override
	@GetMapping(LIST_VEHICLE_FOR_TODAY)
	public R<List<ScheduleObject>> listVehicleForToday(@RequestParam String tenantId) {
		return R.data(scheduleObjectService.listForToday(ArrangeConstant.ScheduleObjectEntityType.VEHICLE, tenantId, null));
	}


	@Override
	@GetMapping(LIST_VEHICLE_FOR_NOW)
	public R<List<ScheduleObject>> listVehicleForNow(@RequestParam String tenantId) {
		return R.data(scheduleObjectService.listForTime(new Date(),ArrangeConstant.ScheduleObjectEntityType.VEHICLE, tenantId, null));
	}

	@Override
	@GetMapping(LIST_PERSON_FOR_NOW)
	public R<List<ScheduleObject>> listPersonForNow(@RequestParam String tenantId) {
		return R.data(scheduleObjectService.listForTime(new Date(),ArrangeConstant.ScheduleObjectEntityType.PERSON,tenantId, null));
	}

	@Override
	@GetMapping(LIST_ENTITY_FOR_NOW)
	public R<List<ScheduleObject>> listEntityForNow(String tenantId) {
		return R.data(scheduleObjectService.listForTime(new Date(),null,tenantId, null));
	}


	@Override
	@GetMapping(COUNT_PERSON_FOR_TODAY)
	public R<Integer> countPersonForToday(@RequestParam String tenantId) {
		return R.data(scheduleObjectService.countByDate(LocalDate.now(), ArrangeConstant.ScheduleObjectEntityType.PERSON, tenantId, null));
	}

	@Override
	@GetMapping(LIST_PERSON_FOR_TODAY)
	public R<List<ScheduleObject>> listPersonForToday(@RequestParam String tenantId) {
		return R.data(scheduleObjectService.listForToday(ArrangeConstant.ScheduleObjectEntityType.PERSON,tenantId, null));
	}

	@Override
	@GetMapping(LIST_ALL_SCHEDULE)
	public R<List<Schedule>> listAllSchedule() {
		return R.data(scheduleService.list());
	}

	@Override
	@GetMapping(LIST_ALL_SCHEDULE_OBJECT_BY_DATE)
	public R<List<ScheduleObject>> listAllScheduleObjectByDate(LocalDate date) {
		ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
		scheduleObject.setScheduleDate(date);
		return R.data(scheduleObjectService.listAllByVO(scheduleObject ));
	}

	@Override
	@GetMapping(LIST_ALL_SCHEDULE_OBJECT_BY_DATE_TYPE)
	public R<List<ScheduleObject>> listAllScheduleObjectByDateAndType(String date, String entityType) {
		ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
		scheduleObject.setScheduleDate(LocalDate.parse(date));
		scheduleObject.setEntityType(entityType);
		return R.data(scheduleObjectService.listAllByVO(scheduleObject ));
	}
	@Override
	@GetMapping(SCHEDULE_OBJECT_BY_ENTITY_AND_DATE)
	public R<List<ScheduleObject>> getScheduleObjectByEntityAndDate(Long entityId, String entityType, String scheduleDate) {
		ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
		scheduleObject.setEntityId(entityId);
		scheduleObject.setEntityType(entityType);
		scheduleObject.setScheduleDate(LocalDate.parse(scheduleDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		List<ScheduleObject> list = scheduleObjectService.listAllByVO(scheduleObject);
		return R.data(list);
	}

	@Override
	@GetMapping(SCHEDULE_OBJECT_BY_ID)
	public R<ScheduleObject> getScheduleObjectById(Long scheduleObjectId){
		return R.data(scheduleObjectService.getByIdWithDel(scheduleObjectId));
	}

	@Override
	@GetMapping(SCHEDULE_BY_ID)
	public R<Schedule> getScheduleById(Long scheduleId) {
		return R.data(scheduleService.getById(scheduleId));
	}

	@Override
	@PostMapping(SCHEDULE_WORK)
	public R saveScheduleWork(ScheduleWork scheduleWork) {
		return R.status(scheduleWorkService.save(scheduleWork));
	}


	@Override
	@GetMapping(SCHEDULE_AttENDANCE_DETAIL_BY_ANNENDANCE_ID)
	public R<List<ScheduleAttendanceDetail>> getAttendanceDetailListByAttendanceId(@RequestParam("id") Long id){
		ScheduleAttendanceDetail detail=new ScheduleAttendanceDetail();
		detail.setScheduleAttendanceId(id);
		List<ScheduleAttendanceDetail> list = scheduleAttendanceDetailService.list(Condition.getQueryWrapper(detail));
		return R.data(list);
	}



	@Override
	@PostMapping(GET_ATTENDANCE)
	public R<List<ScheduleAttendance>> getAttendance(@RequestBody ScheduleAttendance scheduleAttendance){
		List<ScheduleAttendance> list = scheduleAttendanceService.list(Condition.getQueryWrapper(scheduleAttendance));
		return R.data(list);
	}


	@Override
	@PostMapping(GET_ATTENDANCE_BY_DATE)
	public R<List<ScheduleAttendance>> getAttendanceByDate(@RequestBody ScheduleAttendance scheduleAttendance, @RequestParam("date") String date){

		QueryWrapper<ScheduleAttendance> queryWrapper = Condition.getQueryWrapper(scheduleAttendance);
		if (StringUtil.isNotBlank(date)){
			ScheduleObject queryEntity=new ScheduleObject();
			queryEntity.setScheduleDate(LocalDate.parse(date));
			List<ScheduleObject> list = scheduleObjectService.list(Condition.getQueryWrapper(queryEntity));
			List<Long> ids=new ArrayList<>();

			list.forEach(scheduleObject -> {
				ids.add(scheduleObject.getId());
			});
			queryWrapper.in("schedule_object_id",ids);
		}

		List<ScheduleAttendance> list = scheduleAttendanceService.list(queryWrapper);
		return R.data(list);
	}

	@Override
	public R<Map<Long, Boolean>> checkTodayNeedWorkMap(List<Long> scheduleVehicleIdList, String entityType) {
		Map<Long,Boolean> todayNeewworkMap = new HashMap<>();
		if (null == scheduleVehicleIdList) return R.data(todayNeewworkMap);
		for (Long aLong : scheduleVehicleIdList) {
			todayNeewworkMap.put(aLong,scheduleObjectService.checkNeedWork(aLong, entityType, LocalDateTime.now()));
		}
		return R.data(todayNeewworkMap);
	}

	@Override
	@PostMapping(LIST_SCHEDULE_OBJECT)
	public R<List<ScheduleObject>> listScheduleObject(String entityType, LocalDate beginDate, LocalDate endDate, String entityIdStr) {
		ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
		scheduleObject.setEntityType(entityType);
		scheduleObject.setScheduleBeginDate(beginDate);
		scheduleObject.setScheduleEndDate(endDate);
		scheduleObject.setEntityIds(Func.toLongList(entityIdStr));
		List<ScheduleObject> listAllByVO = scheduleObjectService.listAllByVO(scheduleObject);
		return R.data(listAllByVO);
	}

	@Override
	@PostMapping(LIST_ALL_SCHEDULE_OBJECT_BY_SCHEDULE_ID)
	public R<List<ScheduleObject>> listScheduleObjectByScheduleId(Long scheduleId) {
		ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
		scheduleObject.setScheduleId(scheduleId);
		List<ScheduleObject> listAllByVO = scheduleObjectService.listAllByVO(scheduleObject);
		return R.data(listAllByVO);
	}


	@Override
	@PostMapping(LIST_SCHEDULE_OBJECT_BY_CONDITION)
	public R<List<ScheduleObject>> listScheduleObjectByCondition(@RequestBody ScheduleObject scheduleObject){
		QueryWrapper<ScheduleObject> wrapper=new QueryWrapper<>(scheduleObject);
		List<ScheduleObject> list = scheduleObjectService.list(wrapper);
		return R.data(list);
	}




}
