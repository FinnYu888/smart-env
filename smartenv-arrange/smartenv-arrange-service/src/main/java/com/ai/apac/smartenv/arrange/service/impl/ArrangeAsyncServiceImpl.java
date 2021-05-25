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
package com.ai.apac.smartenv.arrange.service.impl;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ai.apac.smartenv.arrange.service.IArrangeAsyncService;
import com.ai.apac.smartenv.arrange.service.IScheduleObjectService;
import com.ai.apac.smartenv.arrange.service.IScheduleService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArrangeAsyncServiceImpl implements IArrangeAsyncService {

	@Autowired
	@Lazy
	private IScheduleObjectService scheduleObjectService;
	@Autowired
	private IScheduleService scheduleService;
	@Autowired
	private IDataChangeEventClient dataChangeEventClient;

	@Async("arrangeThreadPool")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void submitArrangeAsync(ScheduleObjectVO scheduleObject, BladeUser bladeUser) {
		// 已有排班的实体id
		HashSet<Long> entitySet = scheduleObjectService.checkScheduleObjectSet(scheduleObject);
		// 获取所有实体对象
		List<Long> entityIds = scheduleObject.getEntityIds();
		Integer submitType = scheduleObject.getSubmitType();
		if (submitType == null) {
			submitType = ArrangeConstant.SUBMIT_TYPE.UPDATE;
		}
		// 需要同步大数据的排班
		List<ScheduleObject> bigDataListAdd = new ArrayList<>();
		List<ScheduleObject> bigDataListRemove = new ArrayList<>();
		List<ScheduleObject> removeScheduleObjectList = new ArrayList<>();
		List<ScheduleObject> newScheduleObjectList = new ArrayList<>();
		HashSet<String> updateSet = new HashSet<>();
		List<Long> scheduleIds = scheduleObject.getScheduleIds();
		for (Long scheduleId : scheduleIds) {
			// 获取该班次每周情况
			List<Integer> schedulePriods = scheduleService.getSchedulePriods(scheduleId);
			// 获取日期范围所有日期
			List<LocalDate> betweenDates = scheduleObjectService.getBetweenDate(scheduleObject.getScheduleBeginDate(),
					scheduleObject.getScheduleEndDate());
			// 遍历休息排班的日期
			Iterator<LocalDate> datesIterator = betweenDates.iterator();
			HashMap<LocalDate, Integer> dateStatusMap = new HashMap<>();
			while (datesIterator.hasNext()) {
				LocalDate currentDate = datesIterator.next();
				// 周几
				int dayOfWeek = currentDate.getDayOfWeek().getValue();
				if (null == schedulePriods.get(dayOfWeek - 1) || ArrangeConstant.TureOrFalse.INT_FALSE == schedulePriods.get(dayOfWeek - 1)) {
					dateStatusMap.put(currentDate, 0);
				} else {
					dateStatusMap.put(currentDate, 1);
				}
			}
			// 循环插表
			for (Long entityId : entityIds) {
				if (entitySet.contains(entityId)) {
					// 已有排班
					if (submitType == ArrangeConstant.SUBMIT_TYPE.IGNORE) {
						continue;
					} else if (submitType == ArrangeConstant.SUBMIT_TYPE.UPDATE) {
						scheduleObject.setEntityId(entityId);
						scheduleObject.setScheduleId(null);
						List<ScheduleObject> removeScheduleObjects = scheduleObjectService.listAllByVO(scheduleObject);
						removeScheduleObjects.forEach(removeScheduleObject -> {
							ScheduleCache.delScheduleObject(removeScheduleObject.getEntityId(), removeScheduleObject.getEntityType(), removeScheduleObject.getScheduleDate());
							removeScheduleObjectList.add(removeScheduleObject);
							if (removeScheduleObject.getScheduleDate().equals(LocalDate.now())) {
								bigDataListRemove.add(removeScheduleObject);
							}
							// 需要更新旧考勤日期
							LocalDate oldBeginDate = removeScheduleObject.getScheduleBeginDate();//2020-10-01
							LocalDate oldEndDate = removeScheduleObject.getScheduleEndDate();//2020-10-31
							LocalDate newBeginDate = scheduleObject.getScheduleBeginDate();//2020-10-01
							LocalDate newEndDate = scheduleObject.getScheduleEndDate();//2020-10-10
							String updateStr = removeScheduleObject.getEntityId() + "," + removeScheduleObject.getEntityType() + ","
							+ removeScheduleObject.getScheduleId() + "," + oldBeginDate.toString() + "," + oldEndDate.toString();
							if (newBeginDate.isAfter(oldBeginDate) && newBeginDate.isBefore(oldEndDate) && !newEndDate.isBefore(oldBeginDate)) {
								// 旧考勤日期被一分为二，更新前段
								updateStr += "," + oldBeginDate.toString() + "," + newBeginDate.minusDays(1).toString();
								updateSet.add(updateStr);
							} else if (newEndDate.isBefore(oldEndDate) && newEndDate.isAfter(oldBeginDate) && !newBeginDate.isAfter(oldBeginDate)) {
								// 旧考勤日期被一分为二，更新后段
								updateStr += "," + newEndDate.plusDays(1).toString() + "," + oldEndDate.toString();
								updateSet.add(updateStr);
							} else if (newBeginDate.isAfter(oldBeginDate) && newEndDate.isBefore(oldEndDate)) {
								// 旧考勤日期被一分为三，更新前后段
								updateSet.add(updateStr + "," + oldBeginDate.toString() + "," + newBeginDate.minusDays(1).toString());
								updateSet.add(updateStr + "," + newEndDate.plusDays(1).toString() + "," + oldEndDate.toString());
							}
						});
					}
				}
				scheduleObject.setEntityId(entityId);
				betweenDates.forEach(everyday -> {
					// 判断同一班次，统一人或车，同一天是否已排班
					// scheduleObjectService.deleteLogicSameObject(scheduleObject, everyday);
					// 新增
					ScheduleObject newScheduleObject = BeanUtil.copy(scheduleObject, ScheduleObject.class);
					newScheduleObject.setScheduleId(scheduleId);
					newScheduleObject.setScheduleDate(everyday);
					newScheduleObject.setStatus(dateStatusMap.get(everyday));
					newScheduleObject.setTemporary(ArrangeConstant.Temporary.FALSE);
					newScheduleObject.setCreateUser(bladeUser.getUserId());
					newScheduleObject.setUpdateUser(bladeUser.getUserId());
					newScheduleObject.setCreateDept(Long.parseLong(bladeUser.getDeptId()));
					newScheduleObject.setTenantId(bladeUser.getTenantId());
					ScheduleCache.delScheduleObject(entityId, newScheduleObject.getEntityType(), everyday);
					newScheduleObjectList.add(newScheduleObject);
					if (everyday.equals(LocalDate.now())) {
						bigDataListAdd.add(newScheduleObject);
					}
				});
			}
		}
		// remove
 		if (!removeScheduleObjectList.isEmpty()) {
 			for (ScheduleObject removeScheduleObject : removeScheduleObjectList) {
 				ScheduleCache.delScheduleObject(removeScheduleObject.getEntityId(), removeScheduleObject.getEntityType(), removeScheduleObject.getScheduleDate());
 				scheduleObjectService.deleteLogic(Arrays.asList(removeScheduleObject.getId()));
			}
 		}
 		// save
 		scheduleObjectService.saveBatch(newScheduleObjectList);
 		// update
 		for (String updateString : updateSet) {
 			String[] splits = updateString.split(",");
 			QueryWrapper<ScheduleObject> updateWrapper = new QueryWrapper<>();
 			updateWrapper.eq("entity_id", splits[0]);
 			updateWrapper.eq("entity_type", splits[1]);
 			updateWrapper.eq("schedule_id", splits[2]);
 			updateWrapper.eq("schedule_begin_date", splits[3]);
 			updateWrapper.eq("schedule_end_date", splits[4]);
 			updateWrapper.eq("is_deleted", 0);
			ScheduleObject updateEntity = new ScheduleObject();
			updateEntity.setScheduleBeginDate(LocalDate.parse(splits[5], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			updateEntity.setScheduleEndDate(LocalDate.parse(splits[6], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			scheduleObjectService.update(updateEntity , updateWrapper);
 		}
		// 同步大数据
		if (!bigDataListRemove.isEmpty()) {
			scheduleObjectService.syncArrangeToBigData(bigDataListRemove, BigDataHttpClient.OptFlag.REMOVE, null);
		}
		if (!bigDataListAdd.isEmpty()) {
			scheduleObjectService.syncArrangeToBigData(bigDataListAdd, BigDataHttpClient.OptFlag.ADD, null);
		}
		for (Long entityId : entityIds) {
    		ScheduleCache.deleteAsyncEntity(entityId, scheduleObject.getEntityType());
    	}

		dataChangeEventClient.doDbEvent(new BaseDbEventDTO(DbEventConstant.EventType.ARRANGE_EVENT, bladeUser.getTenantId(), Func.join(scheduleObject.getEntityIds())));

	}

	@Override
	@Async("arrangeThreadPool")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void syncForChangeSchedule(Schedule schedule, LocalDate now) {
		ScheduleObject qScheduleObject = new ScheduleObject();
		qScheduleObject.setScheduleDate(now);
		qScheduleObject.setScheduleId(schedule.getId());
		List<ScheduleObject> scheduleObjectList = scheduleObjectService.list(Condition.getQueryWrapper(qScheduleObject));
		if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
			scheduleObjectService.syncArrangeToBigData(scheduleObjectList, BigDataHttpClient.OptFlag.EDIT, schedule);
		}
	}

	@Override
	@Async("arrangeThreadPool")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void syncForChangePeriods(Schedule schedule, LocalDate date) {
		QueryWrapper<ScheduleObject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("schedule_id", schedule.getId());
		queryWrapper.ge("schedule_date", date);
		List<ScheduleObject> scheduleObjectList = scheduleObjectService.list(queryWrapper);
		if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
			// 当天需要同步大数据
			List<ScheduleObject> bigDataListUpdate = new ArrayList<>();
			// 获取该班次每周情况
			List<Integer> schedulePriods = scheduleService.getSchedulePriods(schedule.getId());
			for (ScheduleObject scheduleObject : scheduleObjectList) {
				Integer temporary = scheduleObject.getTemporary();
				// 忽略临时排班
				if (temporary == null || temporary == ArrangeConstant.Temporary.FALSE) {
					Integer status = scheduleObject.getStatus();
					int dayOfWeek = scheduleObject.getScheduleDate().getDayOfWeek().getValue();
					if (status != schedulePriods.get(dayOfWeek - 1)) {
						scheduleObject.setStatus(schedulePriods.get(dayOfWeek - 1));
						ScheduleCache.delScheduleObject(scheduleObject.getEntityId(), scheduleObject.getEntityType(), scheduleObject.getScheduleDate());
						scheduleObjectService.updateById(scheduleObject);
					}
				}
				if (scheduleObject.getScheduleDate().equals(LocalDate.now())) {
					bigDataListUpdate.add(scheduleObject);
				}
			}
			if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
				scheduleObjectService.syncArrangeToBigData(bigDataListUpdate, BigDataHttpClient.OptFlag.EDIT, schedule);
			}
		}
	}

}
