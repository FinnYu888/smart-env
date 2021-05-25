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
package com.ai.apac.smartenv.arrange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import com.ai.apac.smartenv.arrange.vo.ScheduleObjectTimeVO;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.arrange.wrapper.ScheduleObjectWrapper;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.omnic.entity.QScheduleObject;
import com.ai.apac.smartenv.omnic.vo.QScheduleObjectVO;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.service.IScheduleObjectService;

import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;

/**
 * 敏捷排班表 控制器
 *
 * @author Blade
 * @since 2020-02-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("scheduleobject")
@Api(value = "敏捷排班表", tags = "敏捷排班表接口")
public class ScheduleObjectController extends BladeController {

	private IScheduleObjectService scheduleObjectService;
	private IEntityCategoryClient categoryClient;
	private IPersonClient personClient;
	private IVehicleClient vehicleClient;
	
	/**
	 * 删除人或车考勤考勤
	 */
	@DeleteMapping("arrange")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "删除人或车考勤", notes = "传入QScheduleObjects")
	@ApiLog(value = "删除人或车考勤")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R removeArrange(@RequestBody List<QScheduleObject> qScheduleObjects) {
		return R.status(scheduleObjectService.removeArrange(qScheduleObjects));
	}
	/**
	 * 修改人或车考勤
	 */
	@PutMapping("arrange")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "修改人或车考勤", notes = "传入QScheduleObject")
	@ApiLog(value = "修改人或车考勤")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R updateArrange(@RequestBody QScheduleObjectVO qScheduleObject, BladeUser bladeUser) {
		scheduleObjectService.updateArrange(qScheduleObject, bladeUser);
		return R.status(true);
	}
	/**
	 * 车临时调班
	 */
	@PutMapping("vehicleScheduleObject")
	@ApiOperationSupport(order = 7)
	@ApiLog(value = "车临时调班")
	@ApiOperation(value = "车临时调班", notes = "传入ScheduleObjectVO")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R changeVehicleScheduleObject(@RequestBody ScheduleObjectVO scheduleObject) {
		scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
		scheduleObjectService.changeScheduleObject(scheduleObject);
		return R.status(true);
	}

	/**
	 * 人临时调班
	 */
	@PutMapping("personScheduleObject")
	@ApiOperationSupport(order = 7)
	@ApiLog(value = "人临时调班")
	@ApiOperation(value = "人临时调班", notes = "传入ScheduleObjectVO")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R changePersonScheduleObject(@RequestBody ScheduleObjectVO scheduleObject) {
		scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
		scheduleObjectService.changeScheduleObject(scheduleObject);
		return R.status(true);
	}


	/**
	 * 设置车辆考勤
	 */
	@PostMapping("/vehicleArrange")
	@ApiOperationSupport(order = 8)
	@ApiLog(value = "设置车辆考勤")
	@ApiOperation(value = "设置车辆考勤", notes = "传入scheduleObject")
	public R submitVehicleArrange(@RequestBody ScheduleObjectVO scheduleObject, BladeUser bladeUser) {
		scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
		scheduleObjectService.submitArrange(scheduleObject, bladeUser);
		return R.status(true);
	}

	/**
	 * 设置人员考勤
	 */
	@PostMapping("/personArrange")
	@ApiOperationSupport(order = 9)
	@ApiLog(value = "设置人员考勤")
	@ApiOperation(value = "设置人员考勤", notes = "传入scheduleObject")
	public R submitPersonArrange(@RequestBody ScheduleObjectVO scheduleObject, BladeUser bladeUser) {
		scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
		scheduleObjectService.submitArrange(scheduleObject, bladeUser);
		return R.status(true);
	}

	/**
	 * 校验车辆考勤
	 */
	@PostMapping("/checkVehicleArrange")
	@ApiOperationSupport(order = 8)
	@ApiLog(value = "校验车辆考勤")
	@ApiOperation(value = "校验车辆考勤", notes = "传入scheduleObject")
	public R checkVehicleArrange(@RequestBody ScheduleObjectVO scheduleObject) {
		scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
		return R.data(scheduleObjectService.checkArrange(scheduleObject));
	}
	
	/**
	 * 校验人员考勤
	 */
	@PostMapping("/checkpersonArrange")
	@ApiOperationSupport(order = 9)
	@ApiLog(value = "校验人员考勤")
	@ApiOperation(value = "校验人员考勤", notes = "传入scheduleObject")
	public R checkpersonArrange(@RequestBody ScheduleObjectVO scheduleObject) {
		scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
		return R.data(scheduleObjectService.checkArrange(scheduleObject));
	}
	

	/**
	 * 查询车辆考勤日历
	 */
	@GetMapping("/vehicleArrange")
	@ApiOperationSupport(order = 10)
	@ApiLog(value = "查询车辆考勤日历")
	@ApiOperation(value = "查询车辆考勤日历", notes = "传入车辆id,月份")
	public R getVehicleArrange(@RequestParam(required = true) Long entityId, @RequestParam(required = true) String month) {
		List<ScheduleObjectVO> list = scheduleObjectService.getArrange(entityId, month, ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
		return R.data(list);
	}
	/**
	 * 查询人员考勤日历
	 */
	@GetMapping("/personArrange")
	@ApiOperationSupport(order = 10)
	@ApiLog(value = "查询人员考勤日历")
	@ApiOperation(value = "查询人员考勤日历", notes = "传入人员id,月份")
	public R getPersonArrange(@RequestParam(required = true) Long entityId, @RequestParam(required = true) String month) {
		List<ScheduleObjectVO> list = scheduleObjectService.getArrange(entityId, month, ArrangeConstant.ScheduleObjectEntityType.PERSON);
		return R.data(list);
	}

	/**
	 * 查询车辆考勤列表
	 */
	@GetMapping("/listVehicleArrange")
	@ApiOperationSupport(order = 10)
	@ApiLog(value = "查询车辆考勤列表")
	@ApiOperation(value = "查询车辆考勤列表", notes = "")
	public R listVehicleArrange(QScheduleObjectVO qScheduleObject, Query query, BladeUser bladeUser) {
		qScheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
		qScheduleObject.setTenantId(bladeUser.getTenantId());
		return R.data(scheduleObjectService.listArrange(qScheduleObject, query, false));
	}

	/**
	 * 查询人员考勤列表
	 */
	@GetMapping("/listPersonArrange")
	@ApiOperationSupport(order = 10)
	@ApiLog(value = "查询人员考勤列表")
	@ApiOperation(value = "查询人员考勤列表", notes = "")
	public R listPersonArrange(QScheduleObjectVO qScheduleObject, Query query, BladeUser bladeUser) {
		qScheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
		qScheduleObject.setTenantId(bladeUser.getTenantId());
		return R.data(scheduleObjectService.listArrange(qScheduleObject, query, false));
	}

	/*
	 * 查询历史考勤列表
	 */
	@GetMapping("/listHistoryArrange")
	@ApiOperationSupport(order = 10)
	@ApiLog(value = "查询历史考勤列表")
	@ApiOperation(value = "查询历史考勤列表", notes = "")
	public R listHistoryArrange(QScheduleObjectVO qScheduleObject, Query query, BladeUser bladeUser) {
		qScheduleObject.setTenantId(bladeUser.getTenantId());
		return R.data(scheduleObjectService.listArrange(qScheduleObject, query, true));
	}

	/*
	 * 考勤数据同步
	 */
	@GetMapping("/sync/scheduleObject")
	@ApiOperationSupport(order = 10)
	@ApiLog(value = "考勤数据同步")
	@ApiOperation(value = "考勤数据同步", notes = "传入日期")
	public R syncScheduleObject(@ApiParam(value = "日期", required = true) @RequestParam String date) {
		LocalDate localDate = null;
		try {
			localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
		} catch (Exception e) {
			throw new ServiceException(date + ": 日期格式不正确，需要格式为yyyymmdd");
		}
		List<ScheduleObjectTimeVO> list = scheduleObjectService.listScheduleObjectTimeByDate(localDate);
		return R.data(list);
	}

	/*
	 * 排班记录查询，按日期
	 */
	@GetMapping("/listByDate")
	@ApiOperationSupport(order = 11)
	@ApiLog(value = "排班记录查询，按日期")
	@ApiOperation(value = "排班记录查询，按日期", notes = "")
	public R<IPage<ScheduleObjectVO>> listByDate(ScheduleObjectVO scheduleObject, Query query, BladeUser user) {
		LocalDate scheduleDate = scheduleObject.getScheduleDate();// 日期
		String entityType = scheduleObject.getEntityType();// 人员、车
		List<Long> entityIdList = getEntityIdListByEntityType(scheduleObject, user);
		// 实体id为空直接返回
		if (entityIdList == null || entityIdList.isEmpty()) {
			IPage<ScheduleObjectVO> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
			return R.data(emptyPage);
		}
		IPage<ScheduleObject> pages = scheduleObjectService.pageByDate(scheduleDate, entityType, user.getTenantId(), entityIdList, query);
		IPage<ScheduleObjectVO> pageVO = ScheduleObjectWrapper.build().pageVO(pages);
		List<ScheduleObjectVO> records = pageVO.getRecords();
		records.forEach(record -> {
			record = getScheduleObjectAllInfoByVO(record);
		});
		return R.data(pageVO);
	}
	
	/*
	 * 排班记录查询数量，按日期
	 */
	@GetMapping("/countByDate")
	@ApiOperationSupport(order = 12)
	@ApiLog(value = "排班记录查询数量，按日期")
	@ApiOperation(value = "排班记录查询数量，按日期", notes = "")
	public R<Integer> countByDate(ScheduleObjectVO scheduleObject, BladeUser user) {
		LocalDate scheduleDate = scheduleObject.getScheduleDate();// 日期
		String entityType = scheduleObject.getEntityType();// 人员、车
		List<Long> entityIdList = getEntityIdListByEntityType(scheduleObject, user);
		// 实体id为空直接返回
		if (entityIdList == null || entityIdList.isEmpty()) {
			return R.data(0);
		}
		Integer count = scheduleObjectService.countByDate(scheduleDate, entityType, user.getTenantId(), entityIdList);
		return R.data(count);
	}

	private List<Long> getEntityIdListByEntityType(ScheduleObjectVO scheduleObject, BladeUser user) {
		String entityType = scheduleObject.getEntityType();// 人员、车
		List<Long> entityIdList = new ArrayList<>();
		if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(entityType)) {
			PersonVO person = new PersonVO();
			person.setPersonDeptId(scheduleObject.getDeptId());
			person.setPersonName(scheduleObject.getPersonName());
			person.setTenantId(user.getTenantId());
			person.setPersonPositionId(scheduleObject.getPersonPositionId());
			person.setIsIncumbencys(PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
			List<Person> personList = personClient.listPerson(person).getData();
			if (personList != null) {
				personList.forEach(obj -> {
					entityIdList.add(obj.getId());
				});
			}
		} else if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(entityType)) {
			VehicleInfoVO vehicle = new VehicleInfoVO();
			vehicle.setTenantId(user.getTenantId());
			vehicle.setDeptId(scheduleObject.getDeptId());
			vehicle.setPlateNumber(scheduleObject.getPlateNumber());
			vehicle.setEntityCategoryId(scheduleObject.getEntityCategoryId());
			vehicle.setIsUsed(VehicleConstant.VehicleState.IN_USED);
			List<VehicleInfo> vehicleList = vehicleClient.listVehicle(vehicle).getData();
			if (vehicleList != null) {
				vehicleList.forEach(obj -> {
					entityIdList.add(obj.getId());
				});
			}
		}
		return entityIdList;
	}

	/*
	 * 未处理休息的考勤，入参目前都是需要出勤的
	 */
	private ScheduleObjectVO getScheduleObjectAllInfoByVO(ScheduleObjectVO record) {
		String tenantId = record.getTenantId();
		Long scheduleId = record.getScheduleId();
		Schedule schedule = ScheduleCache.getScheduleById(scheduleId);
		if (schedule != null) {
			record.setScheduleName(schedule.getScheduleName());
			record.setScheduleBeginTime(schedule.getScheduleBeginTime());
			record.setScheduleEndTime(schedule.getScheduleEndTime());
		}
		if (ArrangeConstant.ScheduleObjectEntityType.PERSON.equals(record.getEntityType())) {
			Long personId = record.getEntityId();
			Person person = PersonCache.getPersonById(tenantId, personId);
			if (person != null && person.getId() != null) {
				record.setPersonName(person.getPersonName() + "(" + person.getJobNumber() + ")");
				record.setDeptName(DeptCache.getDeptFullName(String.valueOf(person.getPersonDeptId())));
				record.setPersonPositionName(StationCache.getStationName(person.getPersonPositionId()));
			}
			
		} else if (ArrangeConstant.ScheduleObjectEntityType.VEHICLE.equals(record.getEntityType())) {
			Long vehicleId = record.getEntityId();
			VehicleInfo vehicle = VehicleCache.getVehicleById(tenantId, vehicleId);
			if (vehicle != null && vehicle.getId() != null) {
				record.setPlateNumber(vehicle.getPlateNumber());
				record.setDeptName(DeptCache.getDeptFullName(String.valueOf(vehicle.getDeptId())));
				record.setEntityCategoryName(categoryClient.getCategoryName(vehicle.getEntityCategoryId()).getData());
			}
		}
		return record;
	}
	
}
