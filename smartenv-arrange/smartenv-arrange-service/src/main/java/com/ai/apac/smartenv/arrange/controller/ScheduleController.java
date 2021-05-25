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

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.service.IArrangeAsyncService;
import com.ai.apac.smartenv.arrange.service.IScheduleObjectService;
import com.ai.apac.smartenv.arrange.service.IScheduleService;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.arrange.vo.ScheduleVO;
import com.ai.apac.smartenv.arrange.wrapper.ScheduleWrapper;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.vo.DictVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 排班表 控制器
 *
 * @author Blade
 * @since 2020-02-11
 */
@RestController
@AllArgsConstructor
@RequestMapping("schedule")
@Api(value = "排班表", tags = "排班表接口")
public class ScheduleController extends BladeController {

	private IScheduleService scheduleService;
	private IScheduleObjectService scheduleObjectService;
	private IArrangeAsyncService arrangeAsyncService;

	private IPersonClient personClient;
	private IVehicleClient vehicleClient;
	private IMappingClient mappingClient;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入schedule")
	@ApiLog(value = "查询班次详情")
	public R<ScheduleVO> detail(Schedule schedule) {
		Schedule detail = ScheduleCache.getScheduleById(schedule.getId());
		return R.data(ScheduleWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 排班表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入schedule")
	@ApiLog(value = "查询班次列表")
	public R<IPage<ScheduleVO>> list(ScheduleVO schedule, Query query) {
		IPage<Schedule> pages = scheduleService.page(schedule, query);
		IPage<ScheduleVO> pageVOs = ScheduleWrapper.build().pageVO(pages);
		List<ScheduleVO> scheduleVOs = pageVOs.getRecords();
		scheduleVOs.forEach(scheduleVO -> {
			scheduleVO = getAllScheduleInfoByVO(scheduleVO);
		});
		pageVOs.setRecords(scheduleVOs);
		return R.data(pageVOs);
	}
	
	@GetMapping("/listAll")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "查询所有", notes = "传入schedule")
	@ApiLog(value = "查询所有班次")
	public R<List<ScheduleVO>> listAll(ScheduleVO schedule, String needBreakTime) {
		List<Schedule> list = scheduleService.listAll(schedule);
		List<ScheduleVO> scheduleVOs = ScheduleWrapper.build().listVO(list);
		scheduleVOs.forEach(scheduleVO -> {
			scheduleVO = getAllScheduleInfoByVO(scheduleVO);
		});
		/*if (ArrangeConstant.TureOrFalse.STR_TRUE.equals(needBreakTime)) {
			// 休息班次
			ScheduleVO breakSchedule = new ScheduleVO();
			breakSchedule.setId(ArrangeConstant.BREAK_SCHEDULE_ID);
			breakSchedule.setScheduleName(ArrangeConstant.BREAK_SCHEDULE_NAME);
			scheduleVOs.add(0, breakSchedule);
		}*/
		return R.data(scheduleVOs);
	}

	private ScheduleVO getAllScheduleInfoByVO(ScheduleVO scheduleVO) {
		// 排班周期
		List<String> schedulePeriodList = scheduleService.getSchedulePeriod(scheduleVO);
		if (schedulePeriodList.size() > 0) {
			scheduleVO.setSchedulePeriod(StringUtils.strip(schedulePeriodList.toString(), "[]"));
		}
		// 排班类型
		if (StringUtils.isNotBlank(scheduleVO.getScheduleType())) {
			scheduleVO.setScheduleTypeName(DictCache.getValue(ArrangeConstant.DICT_SCHEDULE_TYPE, scheduleVO.getScheduleType()));
		}
		// 班次时间
		String scheduleTime = scheduleService.buildScheduleTime(scheduleVO);
		scheduleVO.setScheduleTime(scheduleTime);
		return scheduleVO;
	}

	/**
	 * 自定义分页 排班表
	 */
	/*@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入schedule")
	public R<IPage<ScheduleVO>> page(ScheduleVO schedule, Query query) {
		IPage<ScheduleVO> pages = scheduleService.selectSchedulePage(Condition.getPage(query), schedule);
		return R.data(pages);
	}*/

	/**
	 * 新增 排班表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入schedule")
	@ApiLog(value = "新增班次")
	public R save(@RequestBody Schedule schedule) {
		// 验证参数
		validateSchedule(schedule);
		boolean save = scheduleService.saveSchedule(schedule);
		return R.status(save);
	}


	@PostMapping("/sync/qiaoyin")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "侨银排班数据同步", notes = "侨银排班数据同步")
	public R<Boolean> syncQiaoyinSchedule(@RequestParam("file") MultipartFile excel, BladeUser bladeUser) throws IOException, ParseException {
		String tenantId = AuthUtil.getTenantId();
		InputStream inputStream1 = new BufferedInputStream(excel.getInputStream());
		List<Object> datas = EasyExcelFactory.read(inputStream1, new Sheet(1, 1));
		int index = 0;
		for (Object object : datas) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");//注意月份是MM
			index ++;
			List<String> params = new ArrayList<>();
			for (Object o : (List<?>) object) {
				params.add(String.class.cast(o));
			}
			String type = params.get(0);
			String id = params.get(1);
			String scheduleStr = params.get(2);
			if(!"NULL".equals(scheduleStr)){
				String[] schedules = scheduleStr.split("\\|");
				Schedule schedule = new Schedule();

				schedule.setScheduleName("排班"+index);
				schedule.setScheduleType("1");
				schedule.setScheduleMonday(1);
				schedule.setScheduleTuesday(1);
				schedule.setScheduleWednesday(1);
				schedule.setScheduleThursday(1);
				schedule.setScheduleFriday(1);
				schedule.setScheduleSaturday(1);
				schedule.setScheduleSunday(1);
				if(schedules.length == 2) {
					schedule.setScheduleBeginTime(simpleDateFormat.parse(schedules[0]));
					schedule.setScheduleEndTime(simpleDateFormat.parse(schedules[1]));
				}
				if(schedules.length == 4) {
					schedule.setScheduleBeginTime(simpleDateFormat.parse(schedules[0]));
					schedule.setScheduleEndTime(simpleDateFormat.parse(schedules[3]));
					schedule.setBreaksBeginTime(simpleDateFormat.parse(schedules[1]));
					schedule.setBreaksEndTime(simpleDateFormat.parse(schedules[2]));
				}
				Long scheduleId = scheduleService.syncSchedule(schedule);
				if(!"NULL".equals(id)){
					ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
					List<Long> entityIdList = new ArrayList<Long>();
					if("1".equals(type)){
						Person person  = new Person();
						person.setIdCard(id);
						person.setTenantId(AuthUtil.getTenantId());
						person.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
						List<Person> res  = personClient.getPersonByCond(person).getData();
						if(ObjectUtil.isNotEmpty(res) && res.size() > 0){
							scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
							entityIdList.add(res.get(0).getId());
						}else{
							continue;
						}
					}else{
						VehicleInfoVO vehicleInfo  = new VehicleInfoVO();
						vehicleInfo.setPlateNumber(id);
						vehicleInfo.setTenantId(AuthUtil.getTenantId());
						List<VehicleInfo> res  = vehicleClient.listVehicle(vehicleInfo).getData();
						if(ObjectUtil.isNotEmpty(res) && res.size() > 0){
							scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
							entityIdList.add(res.get(0).getId());
						}else{
							continue;
						}
					}
					List<Long> scheduleIdList = new ArrayList<Long>();
					scheduleIdList.add(scheduleId);
					scheduleObject.setEntityIds(entityIdList);
					scheduleObject.setScheduleIds(scheduleIdList);
					scheduleObject.setScheduleBeginDate(LocalDate.parse("2021-01-04"));
					scheduleObject.setScheduleEndDate(LocalDate.parse("2021-12-31"));
					scheduleObjectService.submitArrange(scheduleObject, bladeUser);
				}
			}
		}
		return R.data(true);

	}

	@PostMapping("/sync/longma")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "龙马排班数据同步", notes = "龙马排班数据同步")
	public R<Boolean> syncLongmaSchedule(@RequestParam("file") MultipartFile excel, BladeUser bladeUser) throws IOException, ParseException {
		String tenantId = AuthUtil.getTenantId();
		InputStream inputStream1 = new BufferedInputStream(excel.getInputStream());
		List<Object> datas = EasyExcelFactory.read(inputStream1, new Sheet(1, 1));
		int index = 0;
		for (Object object : datas) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");//注意月份是MM
			index ++;
			List<String> params = new ArrayList<>();
			for (Object o : (List<?>) object) {
				params.add(String.class.cast(o));
			}
			String entityId = params.get(0);
			String scheduleIds = params.get(1);
				String[] schedules = scheduleIds.split("\\|");
				Schedule schedule = new Schedule();

				schedule.setScheduleName("排班"+index);
				schedule.setScheduleType("1");
				schedule.setScheduleMonday(1);
				schedule.setScheduleTuesday(1);
				schedule.setScheduleWednesday(1);
				schedule.setScheduleThursday(1);
				schedule.setScheduleFriday(1);
				schedule.setScheduleSaturday(1);
				schedule.setScheduleSunday(1);
				schedule.setScheduleBeginTime(simpleDateFormat.parse(schedules[0]));
				schedule.setScheduleEndTime(simpleDateFormat.parse(schedules[1]));

				Long scheduleId = scheduleService.syncSchedule(schedule);
			AiMapping mapping = new AiMapping();
			mapping.setTenantId(AuthUtil.getTenantId());
			mapping.setThirdCode(entityId);
			AiMapping mapping1 = mappingClient.getSscpCodeByThirdCode(mapping).getData();
			if(ObjectUtil.isNotEmpty(mapping1) && ObjectUtil.isNotEmpty(mapping1.getSscpCode())){

					ScheduleObjectVO scheduleObject = new ScheduleObjectVO();
					List<Long> entityIdList = new ArrayList<Long>();
				entityIdList.add(Long.parseLong(mapping1.getSscpCode()));
					if(mapping1.getCodeType() == 1){
						scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.PERSON);
					}else if(mapping1.getCodeType() == 2){
						scheduleObject.setEntityType(ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
					}
					List<Long> scheduleIdList = new ArrayList<Long>();
					scheduleIdList.add(scheduleId);
					scheduleObject.setEntityIds(entityIdList);
					scheduleObject.setScheduleIds(scheduleIdList);
					scheduleObject.setScheduleBeginDate(LocalDate.parse("2021-01-04"));
					scheduleObject.setScheduleEndDate(LocalDate.parse("2021-12-31"));
					scheduleObjectService.submitArrange(scheduleObject, bladeUser);
			}
		}
		return R.data(true);

	}


	
	private void validateSchedule(@Valid Schedule schedule) {
		Set<ConstraintViolation<@Valid Schedule>> validateSet = Validation.buildDefaultValidatorFactory().getValidator()
				.validate(schedule, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}
		if (schedule.getScheduleBeginTime().after(schedule.getScheduleEndTime())) {
			throw new ServiceException(getExceptionMsg(ArrangeConstant.ArrangeException.KEY_TIME_BEGIN_AFTER_END));
		}
		if (schedule.getBreaksBeginTime() != null && schedule.getBreaksEndTime() == null) {
			throw new ServiceException(getExceptionMsg(ArrangeConstant.ArrangeException.KEY_NEED_BREAKS_END_TIME));
		}
		if (schedule.getBreaksBeginTime() == null && schedule.getBreaksEndTime() != null) {
			throw new ServiceException(getExceptionMsg(ArrangeConstant.ArrangeException.KEY_NEED_BREAKS_BEGIN_TIME));
		}
		if (schedule.getBreaksBeginTime() != null && schedule.getBreaksEndTime() != null
				&& schedule.getBreaksBeginTime().after(schedule.getBreaksEndTime())) {
			throw new ServiceException(getExceptionMsg(ArrangeConstant.ArrangeException.KEY_TIME_BEGIN_AFTER_END));
		}
		if (schedule.getBreaksBeginTime() != null
				&& schedule.getBreaksBeginTime().before(schedule.getScheduleBeginTime())) {
			throw new ServiceException(getExceptionMsg(ArrangeConstant.ArrangeException.KEY_TIME_BREAKS_OUT_OF_SCOPE));
		}
		if (schedule.getBreaksEndTime() != null && schedule.getBreaksEndTime().after(schedule.getScheduleEndTime())) {
			throw new ServiceException(getExceptionMsg(ArrangeConstant.ArrangeException.KEY_TIME_BREAKS_OUT_OF_SCOPE));
		}
	}

	/**
	 * 修改 排班表
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiLog(value = "修改班次")
	@ApiOperation(value = "修改", notes = "传入schedule")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R update(@RequestBody Schedule schedule) {
		// 验证参数
		validateSchedule(schedule);
		Integer updateFlag = scheduleService.updateByScheduleById(schedule);
		if (updateFlag == ArrangeConstant.UPDATE_SCHEDULE_FLAG.UPDATE_TIME) {
			arrangeAsyncService.syncForChangeSchedule(schedule, LocalDate.now());
		} else if (updateFlag == ArrangeConstant.UPDATE_SCHEDULE_FLAG.UPDATE_PERIODS) {
			arrangeAsyncService.syncForChangePeriods(schedule, LocalDate.now());
		}
		return R.status(true);
	}

	/**
	 * 新增或修改 排班表
	 */
	/*
	 * @PostMapping("/submit")
	 * 
	 * @ApiOperationSupport(order = 6)
	 * 
	 * @ApiOperation(value = "新增或修改", notes = "传入schedule") public R
	 * submit(@Valid @RequestBody Schedule schedule) { return
	 * R.status(scheduleService.saveOrUpdate(schedule)); }
	 */

	
	/**
	 * 删除 排班表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiLog(value = "删除班次")
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Long> idList = Func.toLongList(ids);
		boolean removeSchedule = scheduleService.removeSchedule(idList);
		return R.status(removeSchedule);
	}

	/**
	 * 获取数据字典
	 */
	@GetMapping("/listBladeDict")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "获取数据字典", notes = "")
	public R listBladeDict(Dict dict) {
		List<Dict> dicts = DictCache.getList(dict.getCode());
		List<DictVO> dictVOs = new ArrayList<>();
		dicts.forEach(obj -> {
			DictVO vo = BeanUtil.copy(obj, DictVO.class);
			dictVOs.add(vo);
		});
		return R.data(dictVOs);
	}
	
	private String getExceptionMsg(String key) {
		String msg = DictBizCache.getValue(ArrangeConstant.ArrangeException.CODE, key);
		if (StringUtils.isBlank(msg)) {
			msg = key;
		}
		return msg;
	}
}
