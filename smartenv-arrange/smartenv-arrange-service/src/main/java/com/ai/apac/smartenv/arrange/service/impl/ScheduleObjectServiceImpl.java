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
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectTimeVO;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.arrange.vo.ScheduleVO;
import com.ai.apac.smartenv.arrange.wrapper.ScheduleObjectWrapper;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.feign.IArrangeClient;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.arrange.mapper.ScheduleObjectMapper;
import com.ai.apac.smartenv.arrange.service.IArrangeAsyncService;
import com.ai.apac.smartenv.arrange.service.IScheduleObjectService;
import com.ai.apac.smartenv.arrange.service.IScheduleService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 敏捷排班表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
@AllArgsConstructor
@Slf4j
public class ScheduleObjectServiceImpl extends BaseServiceImpl<ScheduleObjectMapper, ScheduleObject> implements IScheduleObjectService {

	private IScheduleService scheduleService;
	@Autowired
	private IArrangeClient arrangeClient;
	private IEntityCategoryClient categoryClient;
	private ISysClient sysClient;
	private IArrangeAsyncService arrangeAsyncService;

	@Autowired
	private IDataChangeEventClient dataChangeEventClient;

	@Override
	public IPage<ScheduleObjectVO> selectScheduleObjectPage(IPage<ScheduleObjectVO> page, ScheduleObjectVO scheduleObject) {
		return page.setRecords(baseMapper.selectScheduleObjectPage(page, scheduleObject));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void refreshArrange(Long entityId, String entityType) {
		ScheduleObject qObject = new ScheduleObject();
		qObject.setEntityId(entityId);
		qObject.setEntityType(entityType);
		QueryWrapper<ScheduleObject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("entity_id", entityId);
		queryWrapper.eq("entity_type", entityType);
		queryWrapper.ge("schedule_end_date", LocalDate.now());
		List<ScheduleObject> list = list(queryWrapper.orderByAsc("schedule_date"));
		List<List<ScheduleObject>> allList = new ArrayList<>();
		List<ScheduleObject> subList = new ArrayList<>();

		for (ScheduleObject object : list) {
			Long scheduleId = object.getScheduleId();
			LocalDate scheduleDate = object.getScheduleDate();
			if (subList.isEmpty()) {
				subList.add(object);
				continue;
			}
			// 天数不连续，优先级1
			if (!scheduleDate.minusDays(1).equals(subList.get(subList.size() - 1).getScheduleDate())) {
				allList.add(subList);
				subList = new ArrayList<>();
				subList.add(object);
				continue;
			}
			// 月份不同，优先级2
			if (scheduleDate.getMonthValue() != subList.get(subList.size() - 1).getScheduleDate().getMonthValue()) {
				allList.add(subList);
				subList = new ArrayList<>();
				subList.add(object);
				continue;
			}
			// 班次不同，优先级3
			if (subList.get(subList.size() - 1).getScheduleId() == null) {
				if (scheduleId != null) {
					allList.add(subList);
					subList = new ArrayList<>();
					subList.add(object);
					continue;
				}
			} else {
				if (!subList.get(subList.size() - 1).getScheduleId().equals(scheduleId)) {
					allList.add(subList);
					subList = new ArrayList<>();
					subList.add(object);
					continue;
				}
			}
			subList.add(object);
		}
		allList.add(subList);
		for (List<ScheduleObject> sub : allList) {
			if (sub != null && !sub.isEmpty()) {
				LocalDate beginDate = sub.get(0).getScheduleDate();
				LocalDate endDate = sub.get(sub.size() - 1).getScheduleDate();
				sub.forEach(obj -> {
					if (!beginDate.equals(obj.getScheduleBeginDate())
							|| !endDate.equals(obj.getScheduleEndDate())) {
						obj.setScheduleBeginDate(beginDate);
						obj.setScheduleEndDate(endDate);
						ScheduleCache.delScheduleObject(obj.getEntityId(), obj.getEntityType(), obj.getScheduleDate());
						updateById(obj);
					}
				});
			}
		}

	}

	/*@Override
	public void deleteLogicSameObject(ScheduleObjectVO scheduleObject, LocalDate everyday) {
		ScheduleObject qryObj = new ScheduleObject();
	//		qryObj.setScheduleId(scheduleObject.getScheduleId());// 目前规定一天一班
		qryObj.setEntityId(scheduleObject.getEntityId());
		qryObj.setEntityType(scheduleObject.getEntityType());
		qryObj.setScheduleDate(everyday);
		List<ScheduleObject> existObjects = list(Condition.getQueryWrapper(qryObj));
		List<Long> existIds = new ArrayList<>();
		existObjects.forEach(existObject -> {
			existIds.add(existObject.getId());
		});
		if (existIds.size() > 0) {
			deleteLogic(existIds);
		}
	}*/

	@Override
	public List<ScheduleObject> listUnfinishScheduleByEntity(Long entityId, String entityType) {
		QueryWrapper<ScheduleObject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("entity_id", entityId);
		queryWrapper.eq("entity_type", entityType);
		queryWrapper.ge("schedule_date", DateUtil.formatDate(DateUtil.now()));
		return list(queryWrapper);
	}

	@Override
	public Boolean checkNeedWork(Long entityId, String entityType, LocalDateTime checkTime) {
		LocalDate nowDate = null;
		LocalTime nowTime = null;
		if (checkTime == null) {
			nowDate = LocalDate.now();
			nowTime = LocalTime.now();
		} else {
			nowDate = checkTime.toLocalDate();
			nowTime = checkTime.toLocalTime();
		}
		List<ScheduleObject> scheduleObjectList = ScheduleCache.getScheduleObjectByEntityAndDate(entityId, entityType, nowDate);
		// 当天无考勤
		if (scheduleObjectList == null || scheduleObjectList.isEmpty() || scheduleObjectList.get(0).getScheduleId() == null) {
			return false;
		}
		boolean needWork = false;
		for (ScheduleObject scheduleObject : scheduleObjectList) {
			if (scheduleObject.getScheduleId() == null) {
				needWork = false;
				continue;
			}
			// 当天休息考勤
			if (scheduleObject.getStatus() != null && scheduleObject.getStatus() == ArrangeConstant.TureOrFalse.INT_FALSE) {
				needWork = false;
				continue;
			}
			// 不存在的班次
			Long scheduleId = scheduleObject.getScheduleId();
			Schedule schedule = ScheduleCache.getScheduleById(scheduleId);
			if (schedule == null || schedule.getId() == null) {
				needWork = false;
				continue;
			}
			// 未上班，和下班后
			LocalTime scheduleBeginTime = dateToLocalTime(schedule.getScheduleBeginTime());
			LocalTime scheduleEndTime = dateToLocalTime(schedule.getScheduleEndTime());
			if (nowTime.isBefore(scheduleBeginTime) || nowTime.isAfter(scheduleEndTime)) {
				needWork = false;
				continue;
			}
			// 休息时间内
			if (schedule.getBreaksBeginTime() != null && schedule.getBreaksEndTime() != null) {
				LocalTime breaksBeginTime = dateToLocalTime(schedule.getBreaksBeginTime());
				LocalTime breaksEndTime = dateToLocalTime(schedule.getBreaksEndTime());
				if (!nowTime.isBefore(breaksBeginTime) && !nowTime.isAfter(breaksEndTime)) {
					needWork = false;
					continue;
				}
			}
			needWork = true;
			break;
		}
		return needWork;
	}

	private LocalTime dateToLocalTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalTime localTime = localDateTime.toLocalTime();
		return localTime;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public boolean removeArrange(List<QScheduleObject> qScheduleObjects) {
		verifyParamForRemoveArrange(qScheduleObjects);
		// 需要同步大数据的排班
		List<ScheduleObject> bigDataListRemove = new ArrayList<>();
		qScheduleObjects.forEach(obj -> {
			ScheduleObjectVO scheduleObjectVO = BeanUtil.copy(obj, ScheduleObjectVO.class);
			List<ScheduleObject> scheduleObjects = listAllByVO(scheduleObjectVO);
			scheduleObjects.forEach(scheduleObject -> {
				ScheduleCache.delScheduleObject(scheduleObject.getEntityId(), scheduleObject.getEntityType(),
						scheduleObject.getScheduleDate());
				deleteLogic(Arrays.asList(scheduleObject.getId()));
				if (scheduleObject.getScheduleDate().equals(LocalDate.now())) {
					bigDataListRemove.add(scheduleObject);
				}
			});
		});
		// 同步大数据
		if (!bigDataListRemove.isEmpty()) {
			syncArrangeToBigData(bigDataListRemove, BigDataHttpClient.OptFlag.REMOVE, null);
		}

		dataChangeEventClient.doDbEvent(new BaseDbEventDTO(DbEventConstant.EventType.ARRANGE_EVENT, AuthUtil.getTenantId(), Func.join(qScheduleObjects.stream().map(QScheduleObject::getEntityId).collect(Collectors.toList()))));

		return true;
	}

	private void verifyParamForRemoveArrange(List<QScheduleObject> qScheduleObjects) {
		qScheduleObjects.forEach(obj -> {
			if (obj.getScheduleId() == null) {
				throw new ServiceException("需要输入班次编号");
			}
			if (obj.getEntityId() == null) {
				throw new ServiceException("需要输入实体对象编号");
			}
			if (StringUtils.isBlank(obj.getEntityType())) {
				throw new ServiceException("需要输入实体对象类型");
			}
			if (obj.getScheduleBeginDate() == null) {
				throw new ServiceException("需要输入开始日期");
			}
			if (obj.getScheduleEndDate() == null) {
				throw new ServiceException("需要输入结束日期");
			}
			/*
			 * if (obj.getScheduleBeginDate().isBefore(LocalDate.now()) ||
			 * obj.getScheduleBeginDate().equals(LocalDate.now())) { throw new
			 * ServiceException("不允许删除今天及之前考勤"); }
			 */
		});
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void submitArrange(ScheduleObjectVO scheduleObject, BladeUser bladeUser) {
		// 验证参数
		verifyParamForSubmit(scheduleObject);
		checkEntityStatus(scheduleObject.getEntityType(), scheduleObject.getEntityIds());
		List<Long> entityIds = scheduleObject.getEntityIds();
		for (Long entityId : entityIds) {
			String asyncEntity = ScheduleCache.getAsyncEntity(entityId, scheduleObject.getEntityType());
			if (asyncEntity != null) {
				if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(scheduleObject.getEntityType())) {
					Person person = PersonCache.getPersonById(bladeUser.getTenantId(), entityId);
					throw new ServiceException(person.getPersonName() + ", 等等......人员考勤设置处理中，请稍后重试");
				} else if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(scheduleObject.getEntityType())) {
					VehicleInfo vehicleInfo = VehicleCache.getVehicleById(bladeUser.getTenantId(), entityId);
					throw new ServiceException(vehicleInfo.getPlateNumber() + ", 等等......车辆考勤设置处理中，请稍后重试");
				}
			}
		}
		for (Long entityId : entityIds) {
			ScheduleCache.putAsyncEntity(entityId, scheduleObject.getEntityType());
		}
		// 异步执行
		arrangeAsyncService.submitArrangeAsync(scheduleObject, bladeUser);


	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public int checkArrange(ScheduleObjectVO scheduleObject) {
		// 验证参数
		verifyParamForSubmit(scheduleObject);
		checkEntityStatus(scheduleObject.getEntityType(), scheduleObject.getEntityIds());
		// 校验期间是否已有排班
		HashSet<Long> entitySet = checkScheduleObjectSet(scheduleObject);
		return entitySet.size();
	}

	@Override
	public HashSet<Long> checkScheduleObjectSet(ScheduleObjectVO scheduleObject) {
		Long scheduleId = scheduleObject.getScheduleId();
		scheduleObject.setScheduleId(null);// 忽略班次id
		List<ScheduleObject> list = listAllByVO(scheduleObject);
		HashSet<Long> entitySet = new HashSet<>();
		if (list != null) {
			for (ScheduleObject object : list) {
				entitySet.add(object.getEntityId());
			}
		}
		scheduleObject.setScheduleId(scheduleId);// 方法里会改成null，重新赋值
		return entitySet;
	}

	private void verifyParamForSubmit(ScheduleObjectVO scheduleObject) {
		List<Long> scheduleIds = scheduleObject.getScheduleIds();
		if (scheduleIds == null || scheduleIds.isEmpty()) {
			// 需要输入班次编号
			throw new ServiceException("需要输入班次编号");
		} else {
			List<Schedule> schedules = new ArrayList<>();
			for (Long scheduleId : scheduleIds) {
				if (scheduleId.equals(ArrangeConstant.BREAK_SCHEDULE_ID)) {
					continue;
				}
				Schedule schedule = ScheduleCache.getScheduleById(scheduleId);
				if (schedule == null || schedule.getId() == null || schedule.getId() <= 0) {
					throw new ServiceException(scheduleId + ":没有这个班次");
				}
				schedules.add(ScheduleCache.getScheduleById(scheduleId));
			}
			boolean checkOverlap = checkOverlap(schedules);
			if (checkOverlap) {
				throw new ServiceException("多个班次时间不能重叠");
			}
		}
		List<Long> entityIds = scheduleObject.getEntityIds();
		if (entityIds == null || entityIds.size() == 0) {
			// 需要输入实体对象编号
			throw new ServiceException("需要输入实体对象编号");
		}
		if (StringUtils.isBlank(scheduleObject.getEntityType())) {
			// 需要输入实体对象类型
			throw new ServiceException("需要输入实体对象类型");
		}
		if (null == scheduleObject.getScheduleBeginDate()) {
			// 需要输入开始日期
			throw new ServiceException("需要输入开始日期");
		}
		if (null == scheduleObject.getScheduleEndDate()) {
			// 需要输入结束日期
			throw new ServiceException("需要输入结束日期");
		}
		if (scheduleObject.getScheduleBeginDate().isAfter(scheduleObject.getScheduleEndDate())) {
			// 开始日期不能晚于结束日期
			throw new ServiceException("开始日期不能晚于结束日期");
		}
	}

	private boolean checkOverlap(List<Schedule> schedules) {
		schedules.sort(Comparator.comparing(Schedule::getScheduleBeginTime));
		boolean flag = false;// 是否重叠标识
		for (int i = 0; i < schedules.size(); i++) {
			if (i > 0) {
				// 跳过第一个时间段不做判断
				Date scheduleBeginTime = schedules.get(i).getScheduleBeginTime();
				for (int j = 0; j < schedules.size(); j++) {
					// 如果当前遍历的i开始时间小于j中某个时间段的结束时间那么则有重叠，反之没有重叠
					// 这里比较时需要排除i本身以及i之后的时间段，因为已经排序了所以只比较自己之前(不包括自己)的时间段
					if (j == i || j > i) {
						continue;
					}
					Date scheduleEndTime = schedules.get(j).getScheduleEndTime();
					int compare = scheduleBeginTime.compareTo(scheduleEndTime);
					if (compare < 0) {
						flag = true;
						break;// 只要存在一个重叠则可退出内循环
					}
				}
			}
			// 当标识已经认为重叠了则可退出外循环
			if (flag) {
				break;
			}
		}
		return flag;
	}

	private String checkScheduleObject(ScheduleObjectVO scheduleObject) {
		Long scheduleId = scheduleObject.getScheduleId();
		scheduleObject.setScheduleId(null);// 一天只能一个排班
		String entityType = scheduleObject.getEntityType();
		List<Long> entityIds = scheduleObject.getEntityIds();
		StringBuilder message = new StringBuilder();
		for (Long entityId : entityIds) {
			scheduleObject.setEntityId(entityId);
			List<ScheduleObject> list = listAllByVO(scheduleObject);
			if (list != null && !list.isEmpty()) {
				if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(entityType)) {
					VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, entityId);
					message.append(vehicleInfo.getPlateNumber() + ": ");
				} else {
					Person person = PersonCache.getPersonById(null, entityId);
					message.append(person.getPersonName() + ": ");
				}
				if (list.size() == 1) {
					message.append(list.get(0).getScheduleDate().toString()).append(".  ");
				} else {
					message.append(list.get(0).getScheduleDate().toString() + "~" + list.get(list.size() - 1).getScheduleDate().toString()).append(".  ");
				}
			}
		}
		scheduleObject.setScheduleId(scheduleId);// 方法里会改成null，重新赋值
		return message.toString();
	}

	@Override
	public List<LocalDate> getBetweenDate(LocalDate scheduleBeginTime, LocalDate scheduleEndTime) {
		List<LocalDate> list = new ArrayList<>();
		long distance = ChronoUnit.DAYS.between(scheduleBeginTime, scheduleEndTime);
		if (distance < 0) {
			return list;
		}
		if (scheduleBeginTime.equals(scheduleEndTime)) {
			list.add(scheduleBeginTime);
			return list;
		}
		Stream.iterate(scheduleBeginTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> list.add(f));
		return list;
    }

	@Override
	public List<ScheduleObjectVO> getArrange(Long entityId, String month, String entityType) {
		month += "-01";
		// 当月一号
		LocalDate firstDayOfMonth = LocalDate.parse(month, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		// 一号周几
		int dayOfMonth = firstDayOfMonth.getDayOfWeek().getValue();
		/*1   -7
		2	-1
		3	-2
		4	-3
		5	-4
		6	-5
		7	-6*/
		LocalDate startDate = null;
		if (dayOfMonth == 1) {
			startDate = firstDayOfMonth.minusDays(7);
		} else {
			startDate = firstDayOfMonth.minusDays(dayOfMonth - 1);
		}
		// 查询
		List<ScheduleObjectVO> calendarList = new ArrayList<>();
		List<ScheduleObjectVO> oneDayTimeList = new ArrayList<>();
		List<ScheduleVO> schedules = new ArrayList<>();
		ScheduleObjectVO oneDaySchedule = new ScheduleObjectVO();
		int count = -1;
		LocalDate currentDate = null;
		while(count < 41) {// 日历一页展示42天
			oneDayTimeList = new ArrayList<>();
			oneDaySchedule = new ScheduleObjectVO();
			schedules = new ArrayList<>();
			count++;
			currentDate = startDate.plusDays(count);
			List<ScheduleObject> scheduleObjectList = ScheduleCache.getScheduleObjectByEntityAndDate(entityId, entityType, currentDate);
			if (scheduleObjectList == null || scheduleObjectList.isEmpty()) {
				oneDaySchedule.setScheduleDate(currentDate);
				calendarList.add(oneDaySchedule);
				continue;
			}
			boolean needWork = false;
			boolean needBreak = false;
			for (ScheduleObject scheduleObject : scheduleObjectList) {
				ScheduleObjectVO scheduleObjectVO = ScheduleObjectWrapper.build().entityVO(scheduleObject);
				/*if (scheduleObjectVO == null || scheduleObjectVO.getId() == null) {
					scheduleObjectVO = new ScheduleObjectVO();
					scheduleObjectVO.setScheduleDate(currentDate);
					oneDayTimeList.add(scheduleObjectVO);
					continue;
				}*/
				if (scheduleObjectVO.getStatus() == 0) {
					if (needBreak) {
                		continue;
                	}
					scheduleObjectVO.setScheduleName("休息");
					oneDayTimeList.add(scheduleObjectVO);
					needBreak = true;
					continue;
				}
				Long scheduleId = scheduleObjectVO.getScheduleId();
				Schedule schedule = ScheduleCache.getScheduleById(scheduleId);
				if (schedule == null || schedule.getId() == null) {
					oneDayTimeList.add(scheduleObjectVO);
					continue;
				}
				needWork = true;
				// 班次时间
				scheduleObjectVO.setScheduleTime(scheduleService.buildScheduleTime(schedule));
				scheduleObjectVO.setScheduleName(schedule.getScheduleName());;
				oneDayTimeList.add(scheduleObjectVO);
			}
			// 过滤多余的休息班次
			Iterator<ScheduleObjectVO> iterator = oneDayTimeList.iterator();
			while (iterator.hasNext()) {
				ScheduleObjectVO next = iterator.next();
				if (needWork && next.getScheduleTime() == null) {
					iterator.remove();
				}
			}
			oneDayTimeList.sort(Comparator.comparing(ScheduleObjectVO::getScheduleTime));
			// 构造每天排班
			oneDaySchedule.setStatus(oneDayTimeList.get(0).getStatus());
			oneDaySchedule.setScheduleDate(oneDayTimeList.get(0).getScheduleDate());
			for (ScheduleObjectVO scheduleObjectVO : oneDayTimeList) {
				if (StringUtil.isBlank(scheduleObjectVO.getScheduleName())) {
					continue;
				}
				ScheduleVO schedule = new ScheduleVO();
				schedule.setScheduleName(scheduleObjectVO.getScheduleName());
				schedule.setScheduleTime(scheduleObjectVO.getScheduleTime());
				schedules.add(schedule);
			}
			oneDaySchedule.setSchedules(schedules);
			calendarList.add(oneDaySchedule);
		}
		return calendarList;
	}

	/*private ScheduleObject getScheduleObject(List<ScheduleObject> scheduleObjectList, LocalDate currentDate) {
		ScheduleObject scheduleObject = new ScheduleObject();
		scheduleObject.setScheduleDate(currentDate);
		scheduleObject.setStatus(ArrangeConstant.TureOrFalse.INT_FALSE);
		if (scheduleObjectList == null || scheduleObjectList.isEmpty()) {
			return scheduleObject;
		}
		for (ScheduleObject so : scheduleObjectList) {
			if (so.getScheduleDate().equals(currentDate)) {
				return so;
			}
		}
		return scheduleObject;
	}*/

	@Override
	public List<ScheduleObject> listAllByVO(ScheduleObjectVO scheduleObject) {
		QueryWrapper<ScheduleObject> queryWrapper = new QueryWrapper<>();
		if (scheduleObject.getEntityId() != null) {
			queryWrapper.eq("entity_id", scheduleObject.getEntityId());
		} else {
			if (scheduleObject.getEntityIds() != null && !scheduleObject.getEntityIds().isEmpty()) {
				queryWrapper.in("entity_id", scheduleObject.getEntityIds());
			}
		}
		if (StringUtils.isNotBlank(scheduleObject.getEntityType())) {
			queryWrapper.eq("entity_type", scheduleObject.getEntityType());
		}
		if (scheduleObject.getScheduleBeginDate() != null) {
			queryWrapper.ge("schedule_date", scheduleObject.getScheduleBeginDate());
		}
		if (scheduleObject.getScheduleEndDate() != null) {
			queryWrapper.le("schedule_date", scheduleObject.getScheduleEndDate());
		}
		if (scheduleObject.getScheduleDate() != null) {
			queryWrapper.eq("schedule_date", scheduleObject.getScheduleDate());
		}
		if (scheduleObject.getScheduleId() != null) {
			queryWrapper.eq("schedule_id", scheduleObject.getScheduleId());
		}
		queryWrapper.orderByAsc("schedule_date");
		queryWrapper.orderByDesc("update_time");
//			queryWrapper.eq("status", 1);
		return list(queryWrapper);
	}

	/*
	 * 临时调班
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void changeScheduleObject(ScheduleObjectVO scheduleObject) {
		// 校验参数
		verifyParamForChange(scheduleObject);
		checkEntityStatus(scheduleObject.getEntityType(), Arrays.asList(scheduleObject.getEntityId()));
		LocalDate scheduleBeginDate = scheduleObject.getScheduleBeginDate();
		LocalDate scheduleEndDate = scheduleObject.getScheduleEndDate();
		List<Long> scheduleIds = scheduleObject.getScheduleIds();
		// 查询对象
		ScheduleObjectVO qryScheduleObject = new ScheduleObjectVO();
		qryScheduleObject.setEntityId(scheduleObject.getEntityId());
		qryScheduleObject.setEntityType(scheduleObject.getEntityType());
		// 所有天
		List<LocalDate> dateList = getBetweenDate(scheduleBeginDate, scheduleEndDate);
		// 需要同步大数据的排班
		List<ScheduleObject> bigDataListAdd = new ArrayList<>();
		List<ScheduleObject> bigDataListRemove = new ArrayList<>();
		List<ScheduleObject> removeScheduleObjectList = new ArrayList<>();
		List<ScheduleObject> newScheduleObjectList = new ArrayList<>();
		dateList.forEach(date -> {
			qryScheduleObject.setScheduleDate(date);
			List<ScheduleObject> oldScheduleObjectList = listAllByVO(qryScheduleObject);
			// 删除
			if (oldScheduleObjectList != null && !oldScheduleObjectList.isEmpty()) {
				for (ScheduleObject oldScheduleObject : oldScheduleObjectList) {
					removeScheduleObjectList.add(oldScheduleObject);
					bigDataListRemove.add(oldScheduleObject);
				}
			}
			// 新增
			for (Long scheduleId : scheduleIds) {
				ScheduleObject newScheduleObject = new ScheduleObject();
				newScheduleObject.setEntityType(scheduleObject.getEntityType());
				newScheduleObject.setEntityId(scheduleObject.getEntityId());
				newScheduleObject.setScheduleDate(date);
				newScheduleObject.setScheduleBeginDate(scheduleBeginDate);
				newScheduleObject.setScheduleEndDate(scheduleEndDate);
				newScheduleObject.setScheduleId(scheduleId);
				if (scheduleId.equals(ArrangeConstant.BREAK_SCHEDULE_ID)) {
					// 休息
					newScheduleObject.setStatus(ArrangeConstant.TureOrFalse.INT_FALSE);
				} else {
					// 工作
					newScheduleObject.setStatus(ArrangeConstant.TureOrFalse.INT_TRUE);
				}
				newScheduleObject.setTemporary(ArrangeConstant.Temporary.TRUE);
				ScheduleCache.delScheduleObject(newScheduleObject.getEntityId(), newScheduleObject.getEntityType(), date);
				newScheduleObjectList.add(newScheduleObject);
				if (date.equals(LocalDate.now())) {
					bigDataListAdd.add(newScheduleObject);
				}
			}
		});
		// remove
		if (!removeScheduleObjectList.isEmpty()) {
			for (ScheduleObject removeScheduleObject : removeScheduleObjectList) {
				ScheduleCache.delScheduleObject(removeScheduleObject.getEntityId(), removeScheduleObject.getEntityType(), removeScheduleObject.getScheduleDate());
 				deleteLogic(Arrays.asList(removeScheduleObject.getId()));
			}
		}
		// save
		saveBatch(newScheduleObjectList);
		// 同步大数据
		if (!bigDataListRemove.isEmpty()) {
			syncArrangeToBigData(bigDataListRemove, BigDataHttpClient.OptFlag.REMOVE, null);
		}
		if (!bigDataListAdd.isEmpty()) {
			syncArrangeToBigData(bigDataListAdd, BigDataHttpClient.OptFlag.ADD, null);
		}

		dataChangeEventClient.doDbEvent(new BaseDbEventDTO(DbEventConstant.EventType.ARRANGE_EVENT, AuthUtil.getTenantId(),null));

	}

	private void checkEntityStatus(String entityType, List<Long> entityIds) {
		if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(entityType)) {
			entityIds.forEach(entityId -> {
				VehicleInfo vehicleInfo = VehicleCache.getVehicleById(null, entityId);
				if (vehicleInfo == null || vehicleInfo.getIsUsed() != VehicleConstant.VehicleState.IN_USED) {
					throw new ServiceException("车辆非在用");
				}
			});
		} else if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(entityType)) {
			entityIds.forEach(entityId -> {
				Person person = PersonCache.getPersonById(null, entityId);
				if (person == null || person.getIsIncumbency() == PersonConstant.IncumbencyStatus.UN) {
					throw new ServiceException("人员已离职");
				}
			});
		}
	}

	/*
	 * 校验临时调班参数
	 */
	private void verifyParamForChange(ScheduleObjectVO scheduleObject) {
		List<Long> scheduleIds = scheduleObject.getScheduleIds();
		if (scheduleIds == null || scheduleIds.isEmpty()) {
			// 需要输入班次编号
			throw new ServiceException("需要输入班次编号");
		} else {
			List<Schedule> schedules = new ArrayList<>();
			for (Long scheduleId : scheduleIds) {
				if (scheduleId.equals(ArrangeConstant.BREAK_SCHEDULE_ID)) {
					continue;
				}
				Schedule schedule = ScheduleCache.getScheduleById(scheduleId);
				if (schedule == null || schedule.getId() == null || schedule.getId() <= 0) {
					throw new ServiceException(scheduleId + ":没有这个班次");
				}
				schedules.add(ScheduleCache.getScheduleById(scheduleId));
			}
			boolean checkOverlap = checkOverlap(schedules);
			if (checkOverlap) {
				throw new ServiceException("多个班次时间不能重叠");
			}
		}
		if (scheduleObject.getScheduleBeginDate() == null) {
			throw new ServiceException("需要调班开始日期");
		}
		if (scheduleObject.getScheduleEndDate() == null) {
			throw new ServiceException("需要调班结束日期");
		}
		if (scheduleObject.getEntityId() == null) {
			throw new ServiceException("需要调班对象编码");
		}
		if (StringUtils.isBlank(scheduleObject.getEntityType())) {
			throw new ServiceException("需要调班对象类型");
		}
		if (scheduleObject.getScheduleBeginDate().isBefore(LocalDate.now())) {
			throw new ServiceException("临时调班不能早于当天");
		}
	}

	@Override
	public void syncArrangeToBigData(List<ScheduleObject> bigDataList, String optFlag, Schedule schedule) {
		Boolean needGetSchedule = false;
		if (schedule == null) {
			needGetSchedule = true;
		}
		String url = BigDataHttpClient.syncArrangeToBigData;
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		for (ScheduleObject scheduleObject : bigDataList) {
			if (needGetSchedule) {
				schedule = ScheduleCache.getScheduleById(scheduleObject.getScheduleId());
			}
			if (schedule != null && schedule.getId() != null && schedule.getId() > 0) {
				JSONObject param = new JSONObject();
				param.put("scheduleObjectId", scheduleObject.getId());
				param.put("entityId", scheduleObject.getEntityId());
				param.put("entityType", scheduleObject.getEntityType());
				param.put("scheduleDate", scheduleObject.getScheduleDate().toString());
				param.put("status", scheduleObject.getStatus());
				if (schedule.getScheduleBeginTime() != null) {
					param.put("scheduleBeginTime", formatter.format(schedule.getScheduleBeginTime()));
				}
				if (schedule.getScheduleEndTime() != null) {
					param.put("scheduleEndTime", formatter.format(schedule.getScheduleEndTime()));
				}
				if (schedule.getBreaksBeginTime() != null) {
					param.put("breaksBeginTime", formatter.format(schedule.getBreaksBeginTime()));
				}
				if (schedule.getBreaksEndTime() != null) {
					param.put("breaksEndTime", formatter.format(schedule.getBreaksEndTime()));
				}
				param.put("optFlag", optFlag);
				try {
					BigDataHttpClient.postDataToBigData(url, param.toString());
				} catch (IOException e) {
//					throw new ServiceException("同步大数据失败");
					log.error("同步大数据失败||" + e.getMessage() + "||" + param.toString());
				}
			}
		}

	}

	@Override
	public List<ScheduleObjectTimeVO> listScheduleObjectTimeByDate(LocalDate localDate) {
		List<ScheduleObjectTimeVO> timeList = new ArrayList<>();
		ScheduleObject qScheduleObject = new ScheduleObject();
		qScheduleObject.setScheduleDate(localDate);
		List<ScheduleObject> scheduleObjectList = list(Condition.getQueryWrapper(qScheduleObject));
		if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
			for (ScheduleObject scheduleObject : scheduleObjectList) {
				ScheduleObjectTimeVO vo = new ScheduleObjectTimeVO();
				vo.setScheduleObjectId(scheduleObject.getId());
				vo.setEntityId(scheduleObject.getEntityId());
				vo.setEntityType(scheduleObject.getEntityType());
				vo.setScheduleDate(scheduleObject.getScheduleDate().toString());
				vo.setStatus(scheduleObject.getStatus());
				Long scheduleId = scheduleObject.getScheduleId();
				Schedule schedule = ScheduleCache.getScheduleById(scheduleId);
				if (schedule != null && schedule.getId() != null && schedule.getId() > 0) {
					vo.setScheduleBeginTime(schedule.getScheduleBeginTime());
					vo.setScheduleEndTime(schedule.getScheduleEndTime());
					vo.setBreaksBeginTime(schedule.getBreaksBeginTime());
					vo.setBreaksEndTime(schedule.getBreaksEndTime());
					timeList.add(vo);
				}
			}
		}
		return timeList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public Boolean unbindSchedule(Long entityId, String entityType) {
		List<ScheduleObject> scheduleObjectList = listUnfinishScheduleByEntity(entityId, entityType);
		if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
			scheduleObjectList.forEach(obj -> {
				ScheduleCache.delScheduleObject(entityId, entityType, obj.getScheduleDate());
				deleteLogic(Arrays.asList(obj.getId()));
			});
		}
		return true;
	}

	@Override
	public Integer countForToday(String entityType, String tenantId, List<Long> entityIdList) {
		return count(generateWrapper(LocalDate.now(), entityType, tenantId, entityIdList));
	}

	private QueryWrapper<ScheduleObject> generateWrapper(LocalDate date, String entityType, String tenantId, List<Long> entityIdList) {
		ScheduleObject scheduleObject = new ScheduleObject();
		scheduleObject.setEntityType(entityType);
		scheduleObject.setScheduleDate(date);
		scheduleObject.setStatus(ArrangeConstant.TureOrFalse.INT_TRUE);// 应出勤
		if (StringUtils.isNotBlank(tenantId)) {
			scheduleObject.setTenantId(tenantId);
		}
		QueryWrapper<ScheduleObject> queryWrapper = Condition.getQueryWrapper(scheduleObject);
		// 查询所有在用班次
		List<Schedule> schedules = scheduleService.list();
		List<Long> scheduleIds = new ArrayList<>();
		if (schedules != null) {
			schedules.forEach(schedule -> {
				if (schedule != null && schedule.getId() != null) {
					scheduleIds.add(schedule.getId());
				}
			});
		}
		if (!scheduleIds.isEmpty()) {
			queryWrapper.in("schedule_id", scheduleIds);
		}
		if (entityIdList != null && !entityIdList.isEmpty()) {
			queryWrapper.in("entity_id", entityIdList);
		}
		return queryWrapper;
	}

	@Override
	public List<ScheduleObject> listForTime(Date time,String entityType, String tenantId, List<Long> entityIdList) {
		LocalDate localDate = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		ScheduleObject scheduleObject = new ScheduleObject();
		if(StringUtil.isNotBlank(entityType)){
			scheduleObject.setEntityType(entityType);
		}
		scheduleObject.setScheduleDate(localDate);
		scheduleObject.setStatus(ArrangeConstant.TureOrFalse.INT_TRUE);// 应出勤
		if (StringUtils.isNotBlank(tenantId)) {
			scheduleObject.setTenantId(tenantId);
		}
		QueryWrapper<ScheduleObject> queryWrapper = Condition.getQueryWrapper(scheduleObject);
		// 查询所有在用班次
		LocalTime localTime = dateToLocalTime(time);
		List<Schedule> schedules = scheduleService.list();
		List<Long> scheduleIds = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(schedules)) {
			schedules.forEach(schedule -> {
				LocalTime scheduleBeginTime = dateToLocalTime(schedule.getScheduleBeginTime());
				LocalTime scheduleEndTime = dateToLocalTime(schedule.getScheduleEndTime());
				if (localTime.isBefore(scheduleBeginTime) || localTime.isAfter(scheduleEndTime)) {
					return;
				}
				// 休息时间内
				if (schedule.getBreaksBeginTime() != null && schedule.getBreaksEndTime() != null) {
					LocalTime breaksBeginTime = dateToLocalTime(schedule.getBreaksBeginTime());
					LocalTime breaksEndTime = dateToLocalTime(schedule.getBreaksEndTime());
					if (!localTime.isBefore(breaksBeginTime) && !localTime.isAfter(breaksEndTime)) {
						return;
					}
				}
				scheduleIds.add(schedule.getId());
			});
		}
		List<ScheduleObject> list=new ArrayList<>();
		if (CollectionUtil.isEmpty(scheduleIds)){
			return list;
		}

		queryWrapper.in("schedule_id", scheduleIds);
		if (entityIdList != null && !entityIdList.isEmpty()) {
			queryWrapper.in("entity_id", entityIdList);
		}

		list = list(queryWrapper);
		return list;
	}


	@Override
	public List<ScheduleObject> listForToday(String entityType, String tenantId, List<Long> entityIdList) {
		return list(generateWrapper(LocalDate.now(), entityType, tenantId, entityIdList));
	}

	@Override
	public IPage<ScheduleObject> pageByDate(LocalDate scheduleDate, String entityType, String tenantId,
			List<Long> entityIdList, Query query) {
		return page(Condition.getPage(query), generateWrapper(scheduleDate, entityType, tenantId, entityIdList));
	}

	/*
	 * 应出勤数量
	 */
	@Override
	public Integer countByDate(LocalDate scheduleDate, String entityType, String tenantId, List<Long> entityIdList) {
		return baseMapper.countByDate(scheduleDate, entityType, tenantId, entityIdList);
	}

	@Override
	public Boolean checkTodayNeedWork(Long entityId, String entityType) {
		List<ScheduleObject> scheduleObjectList = ScheduleCache.getScheduleObjectByEntityAndDate(entityId, entityType, LocalDate.now());
		if (scheduleObjectList == null || scheduleObjectList.isEmpty()) {
			return false;
		}
		boolean needWork = false;
		for (ScheduleObject scheduleObject : scheduleObjectList) {
			if (scheduleObject.getId() == null || scheduleObject.getStatus() == null
					|| scheduleObject.getScheduleId() == null
					|| scheduleObject.getStatus() == ArrangeConstant.TureOrFalse.INT_FALSE) {
				needWork = false;
				continue;
			}
			// 已删除班次
			Schedule schedule = ScheduleCache.getScheduleById(scheduleObject.getScheduleId());
			if (schedule == null || schedule.getId() == null) {
				needWork = false;
				continue;
			}
			needWork = true;
			break;
		}
		return needWork;
	}

	@Override
	public IPage<QScheduleObjectVO> listArrange(QScheduleObjectVO qScheduleObject, Query query, boolean isHistory) {
		Long deptId = qScheduleObject.getDeptId();
		String name = qScheduleObject.getName();
		List<Long> deptIdList = null;
		if (deptId != null) {
			deptIdList = sysClient.getAllChildDepts(deptId).getData();
		}
		if (isHistory) {
			qScheduleObject.setPersonDeptIds(deptIdList);
			qScheduleObject.setPersonName(name);
			qScheduleObject.setVehicleDeptIds(deptIdList);
			qScheduleObject.setPlateNumber(name);
		} else {
			if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(qScheduleObject.getEntityType())) {
				qScheduleObject.setVehicleDeptIds(deptIdList);
				qScheduleObject.setPlateNumber(name);
			} else if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(qScheduleObject.getEntityType())) {
				qScheduleObject.setPersonDeptIds(deptIdList);
				qScheduleObject.setPersonName(name);
			}
		}
		// 处理排班周期条件
		qScheduleObject = initSchedulePeriod(qScheduleObject);
		qScheduleObject = initQuery(query, qScheduleObject);
		qScheduleObject.setHistoryFlag(isHistory);
		// 开始查询
//				List<QScheduleObject> list = scheduleObjectService.listArrange(qScheduleObject, start, query.getSize(), isHistory);
//				Integer count = scheduleObjectService.listArrange(qScheduleObject, -1, -1, isHistory).size();
		List<QScheduleObject> list = arrangeClient.listArrange(qScheduleObject).getData();
		Integer count = arrangeClient.countArrange(qScheduleObject).getData();
		List<QScheduleObjectVO> voList = new ArrayList<>();
		list.forEach(obj -> {
			QScheduleObjectVO qScheduleVO = BeanUtil.copy(obj, QScheduleObjectVO.class);
			qScheduleVO.setArrangeDate(qScheduleVO.getScheduleBeginDate() + ArrangeConstant.DATE_SEPARATION + qScheduleVO.getScheduleEndDate());
			// 排班周期
			List<String> schedulePeriodList = scheduleService.getSchedulePeriod(BeanUtil.copy(qScheduleVO, ScheduleVO.class));
			if (schedulePeriodList.size() > 0) {
				qScheduleVO.setSchedulePeriod(StringUtils.strip(schedulePeriodList.toString(), "[]"));
			}
			// 班次时间
			Date scheduleBeginTime = qScheduleVO.getScheduleBeginTime();
			Date scheduleEndTime = qScheduleVO.getScheduleEndTime();
			Date breaksBeginTime = qScheduleVO.getBreaksBeginTime();
			Date breaksEndTime = qScheduleVO.getBreaksEndTime();
			StringBuilder scheduleTime = new StringBuilder();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			if (scheduleBeginTime != null && scheduleEndTime != null) {
				scheduleTime.append(formatter.format(scheduleBeginTime) + ArrangeConstant.DATE_SEPARATION + formatter.format(scheduleEndTime));
			}
			if (breaksBeginTime != null && breaksEndTime != null) {
				scheduleTime.append("(休息时间:" + formatter.format(breaksBeginTime) + ArrangeConstant.DATE_SEPARATION + formatter.format(breaksEndTime) + ")");
			}
			qScheduleVO.setScheduleTime(scheduleTime.toString());
			// 车辆小类
	        if (qScheduleVO.getEntityCategoryId() != null && qScheduleVO.getEntityCategoryId() > 0)  {
//	            String categoryName = categoryClient.getCategoryName(qScheduleVO.getEntityCategoryId()).getData();
	        	String categoryName = VehicleCategoryCache.getCategoryNameByCode(qScheduleVO.getEntityCategoryId().toString(),AuthUtil.getTenantId());
				qScheduleVO.setPlateNumber(qScheduleVO.getPlateNumber() + "(" + categoryName + ")");
	        }
	        // 人员工号
	        if (StringUtils.isNotBlank(qScheduleVO.getJobNumber())) {
	        	qScheduleVO.setPersonName(qScheduleVO.getPersonName() + "(" + qScheduleVO.getJobNumber() + ")");
	        }
	        if (isHistory) {
				qScheduleVO.setName(StringUtils.isNotBlank(qScheduleVO.getPlateNumber()) ? qScheduleVO.getPlateNumber() : qScheduleVO.getPersonName());
				qScheduleVO.setDeptName(StringUtils.isNotBlank(qScheduleVO.getVehicleDeptName()) ? qScheduleVO.getVehicleDeptName() : qScheduleVO.getPersonDeptName());
			}
	        if (qScheduleVO.getScheduleId() == -1 && StringUtils.isBlank(qScheduleVO.getScheduleName()) || qScheduleVO.getScheduleId().equals(ArrangeConstant.BREAK_SCHEDULE_ID)) {
	        	qScheduleVO.setScheduleName(ArrangeConstant.BREAK_SCHEDULE_NAME);
			}
			voList.add(qScheduleVO);
		});
		// 构造page对象
		IPage<QScheduleObjectVO> iPage = new Page<>(query.getCurrent(), query.getSize(), true);
		iPage.setTotal(count);
		iPage.setRecords(voList);
		return iPage;
	}

	private QScheduleObjectVO initSchedulePeriod(QScheduleObjectVO qScheduleObject) {
		String schedulePeriod = qScheduleObject.getSchedulePeriod();
		if (StringUtils.isNotBlank(schedulePeriod)) {
			String[] schedulePeriods = schedulePeriod.split(",");
			if (schedulePeriods != null && schedulePeriods.length > 0) {
				qScheduleObject.setScheduleMonday(Integer.parseInt(schedulePeriods[0]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 1) {
				qScheduleObject.setScheduleTuesday(Integer.parseInt(schedulePeriods[1]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 2) {
				qScheduleObject.setScheduleWednesday(Integer.parseInt(schedulePeriods[2]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 3) {
				qScheduleObject.setScheduleThursday(Integer.parseInt(schedulePeriods[3]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 4) {
				qScheduleObject.setScheduleFriday(Integer.parseInt(schedulePeriods[4]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 5) {
				qScheduleObject.setScheduleSaturday(Integer.parseInt(schedulePeriods[5]));
			}
			if (schedulePeriods != null && schedulePeriods.length > 6) {
				qScheduleObject.setScheduleSunday(Integer.parseInt(schedulePeriods[6]));
			}
		}
		return qScheduleObject;
	}

	private QScheduleObjectVO initQuery(Query query, QScheduleObjectVO qScheduleObject) {
		if (query.getCurrent() == null) {
			query.setCurrent(0);
		}
		if (query.getSize() == null) {
			query.setSize(0);
		}
		int start = (query.getCurrent() - 1) * query.getSize();
		qScheduleObject.setStart(start);
		qScheduleObject.setSize(query.getSize());
		return qScheduleObject;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public void updateArrange(QScheduleObjectVO qScheduleObject, BladeUser bladeUser) {
		if (qScheduleObject.getScheduleId().equals(qScheduleObject.getNewScheduleId())
				&& qScheduleObject.getScheduleBeginDate().equals(qScheduleObject.getNewScheduleBeginDate())
				&& qScheduleObject.getScheduleEndDate().equals(qScheduleObject.getNewScheduleEndDate())) {
			throw new ServiceException("排班信息没有更新");
		}
		checkEntityStatus(qScheduleObject.getEntityType(), Arrays.asList(qScheduleObject.getEntityId()));
		Long oldScheduleId = qScheduleObject.getScheduleId();
		Long newScheduleId = qScheduleObject.getNewScheduleId();

		// 旧考勤数据
		ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
		scheduleObject.setEntityId(qScheduleObject.getEntityId());
		scheduleObject.setEntityType(qScheduleObject.getEntityType());
		scheduleObject.setScheduleBeginDate(qScheduleObject.getScheduleBeginDate());
		scheduleObject.setScheduleEndDate(qScheduleObject.getScheduleEndDate());
		scheduleObject.setScheduleId(oldScheduleId);
		List<ScheduleObject> oldScheduleObjectList = listAllByVO(scheduleObject);
		HashMap<Long, LocalDate> oldScheduleObjectMap = new HashMap<>();
		for (ScheduleObject oldScheduleObject : oldScheduleObjectList) {
			oldScheduleObjectMap.put(oldScheduleObject.getId(), oldScheduleObject.getScheduleDate());
		}

		// 查询新排班日期范围内是否有数据
		scheduleObject = new ScheduleObjectVO();
		scheduleObject.setEntityId(qScheduleObject.getEntityId());
		scheduleObject.setEntityType(qScheduleObject.getEntityType());
		scheduleObject.setScheduleBeginDate(qScheduleObject.getNewScheduleBeginDate());
		scheduleObject.setScheduleEndDate(qScheduleObject.getNewScheduleEndDate());
		List<ScheduleObject> oldScheduleObjectList2 = listAllByVO(scheduleObject);
		for (ScheduleObject oldScheduleObject : oldScheduleObjectList2) {
			// 排除当前需要修改的排班和休息
			if (oldScheduleObjectMap.containsKey(oldScheduleObject.getId())
					|| oldScheduleObject.getStatus().equals(ArrangeConstant.TureOrFalse.INT_FALSE)) {
				continue;
			}
			// 判断新的排班时间是否与旧的排班时间冲突
			List<Schedule> schedules = new ArrayList<>();
			schedules.add(ScheduleCache.getScheduleById(newScheduleId));
			schedules.add(ScheduleCache.getScheduleById(oldScheduleObject.getScheduleId()));
			boolean checkOverlap = checkOverlap(schedules);
			if (checkOverlap) {
				throw new ServiceException("与" + oldScheduleObject.getScheduleBeginDate().toString() + "~" + oldScheduleObject.getScheduleEndDate().toString()
						+ "的" + ScheduleCache.getScheduleById(oldScheduleObject.getScheduleId()).getScheduleName() + "时间冲突，不可修改");
			}
		}
		// 删除旧排班日期范围内数据
		removeArrange(Arrays.asList(qScheduleObject));
		qScheduleObject.setScheduleId(ArrangeConstant.BREAK_SCHEDULE_ID);
		qScheduleObject.setScheduleBeginDate(qScheduleObject.getNewScheduleBeginDate());
		qScheduleObject.setScheduleEndDate(qScheduleObject.getNewScheduleEndDate());
		removeArrange(Arrays.asList(qScheduleObject));
		// 新增数据
		Long scheduleId = qScheduleObject.getNewScheduleId();
		// 获取该班次每周情况
		List<Integer> schedulePriods = scheduleService.getSchedulePriods(scheduleId);
		// 获取日期范围所有日期
		List<LocalDate> betweenDates = getBetweenDate(qScheduleObject.getNewScheduleBeginDate(), qScheduleObject.getNewScheduleEndDate());
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
		// 需要同步大数据的排班
		List<ScheduleObject> bigDataListAdd = new ArrayList<>();
		List<ScheduleObject> newObjectList = new ArrayList<>();
		// 循环插表
		for (LocalDate everyday : betweenDates) {
			// 新增
			ScheduleObject newScheduleObject = new ScheduleObject();
			newScheduleObject.setScheduleId(qScheduleObject.getNewScheduleId());
			newScheduleObject.setEntityId(qScheduleObject.getEntityId());
			newScheduleObject.setEntityType(qScheduleObject.getEntityType());
			newScheduleObject.setScheduleDate(everyday);
			newScheduleObject.setScheduleBeginDate(qScheduleObject.getNewScheduleBeginDate());
			newScheduleObject.setScheduleEndDate(qScheduleObject.getNewScheduleEndDate());
			newScheduleObject.setStatus(dateStatusMap.get(everyday));
			newScheduleObject.setTemporary(ArrangeConstant.Temporary.FALSE);
			ScheduleCache.delScheduleObject(qScheduleObject.getEntityId(), qScheduleObject.getEntityType(), everyday);
			newObjectList.add(newScheduleObject);
			if (everyday.equals(LocalDate.now())) {
				bigDataListAdd.add(newScheduleObject);
			}
		}
		saveBatch(newObjectList);
		// 同步大数据
		if (!bigDataListAdd.isEmpty()) {
			syncArrangeToBigData(bigDataListAdd, BigDataHttpClient.OptFlag.ADD, null);
		}

		dataChangeEventClient.doDbEvent(new BaseDbEventDTO(DbEventConstant.EventType.ARRANGE_EVENT, AuthUtil.getTenantId(),Func.join(newObjectList.stream().map(ScheduleObject::getEntityId).collect(Collectors.toList()))));

	}

	@Override
	public ScheduleObject getByIdWithDel(Long scheduleObjectId) {
		return baseMapper.getByIdWithDel(scheduleObjectId);
	}
}
