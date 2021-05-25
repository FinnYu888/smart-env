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

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ai.apac.smartenv.arrange.entity.ScheduleHoliday;
import com.ai.apac.smartenv.arrange.vo.ScheduleHolidayVO;
import com.ai.apac.smartenv.arrange.wrapper.ScheduleHolidayWrapper;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.arrange.service.IScheduleHolidayService;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 排班节假日表 控制器
 *
 * @author Blade
 * @since 2020-02-11
 */
@RestController
@AllArgsConstructor
@RequestMapping("scheduleholiday")
@Api(value = "排班节假日表", tags = "排班节假日表接口")
public class ScheduleHolidayController extends BladeController {

	private IScheduleHolidayService scheduleHolidayService;
	private IDictClient dictClient;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入scheduleHoliday")
	public R<ScheduleHolidayVO> detail(ScheduleHoliday scheduleHoliday) {
		ScheduleHoliday detail = scheduleHolidayService.getOne(Condition.getQueryWrapper(scheduleHoliday));
		return R.data(ScheduleHolidayWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 排班节假日表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入scheduleHoliday")
	public R<IPage<ScheduleHolidayVO>> list(ScheduleHoliday scheduleHoliday, Query query) {
		IPage<ScheduleHoliday> pages = scheduleHolidayService.page(Condition.getPage(query), Condition.getQueryWrapper(scheduleHoliday));
		IPage<ScheduleHolidayVO> pageVO = ScheduleHolidayWrapper.build().pageVO(pages);
		List<ScheduleHolidayVO> records = pageVO.getRecords();
		records.forEach(record -> {
			record = getAllScheduleInfoByVO(record);
		});
		pageVO.setRecords(records);
		return R.data(pageVO);
	}


	private ScheduleHolidayVO getAllScheduleInfoByVO(ScheduleHolidayVO record) {
		// 节假日类型
		if (StringUtils.isNotBlank(record.getHolidayType())) {
			record.setHolidayTypeName(dictClient.getValue(ArrangeConstant.DICT_HOLIDAY_TYPE, record.getHolidayType()).getData());
		}
		// 假期时间
		if (StringUtils.isNotBlank(record.getHolidayType())) {
			if (ArrangeConstant.HolidayType.ASSIGN.equals(record.getHolidayType())) {
				record.setHolidayTime(record.getHolidayBeginDate().toString() + ArrangeConstant.DATE_SEPARATION
						+ record.getHolidayEndDate().toString());
			} else if (ArrangeConstant.HolidayType.PERIOD.equals(record.getHolidayType())) {
				String holidayPeriod = record.getHolidayPeriod();
				String[] holidayPeriods = holidayPeriod.split(",");
				List<String> holidayPeriodList = new ArrayList<>();
				for (int i = 0; i < holidayPeriods.length; i++) {
					if (ArrangeConstant.TureOrFalse.STR_TRUE.equals(holidayPeriods[i])) {
						switch (i) {
						case 0:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.MONDAY);
							break;
						case 1:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.TUESDAY);
							break;
						case 2:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.WEDNESDAY);
							break;
						case 3:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.THURSDAY);
							break;
						case 4:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.FRIDAY);
							break;
						case 5:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.SATURDAY);
							break;
						case 6:
							holidayPeriodList.add(ArrangeConstant.SchedulePeriod.SUNDAY);
							break;
						}
					}
				}
				record.setHolidayTime(StringUtils.strip(holidayPeriodList.toString(), "[]"));
			}
		}
		return record;
	}

	/**
	 * 自定义分页 排班节假日表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入scheduleHoliday")
	public R<IPage<ScheduleHolidayVO>> page(ScheduleHolidayVO scheduleHoliday, Query query) {
		IPage<ScheduleHolidayVO> pages = scheduleHolidayService.selectScheduleHolidayPage(Condition.getPage(query), scheduleHoliday);
		return R.data(pages);
	}

	/**
	 * 新增 排班节假日表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入scheduleHoliday")
	public R save(@Valid @RequestBody ScheduleHoliday scheduleHoliday) {
		return R.status(scheduleHolidayService.save(scheduleHoliday));
	}

	/**
	 * 修改 排班节假日表
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入scheduleHoliday")
	public R update(@Valid @RequestBody ScheduleHoliday scheduleHoliday) {
		return R.status(scheduleHolidayService.updateById(scheduleHoliday));
	}

	/**
	 * 新增或修改 排班节假日表
	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入scheduleHoliday")
//	public R submit(@Valid @RequestBody ScheduleHoliday scheduleHoliday) {
//		return R.status(scheduleHolidayService.saveOrUpdate(scheduleHoliday));
//	}

	
	/**
	 * 删除 排班节假日表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(scheduleHolidayService.deleteLogic(Func.toLongList(ids)));
	}

	
}
