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
import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ai.apac.smartenv.arrange.entity.ScheduleWork;
import com.ai.apac.smartenv.arrange.vo.ScheduleWorkVO;
import com.ai.apac.smartenv.arrange.wrapper.ScheduleWorkWrapper;
import com.ai.apac.smartenv.arrange.service.IScheduleWorkService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 实际作业表 控制器
 *
 * @author Blade
 * @since 2020-03-17
 */
@RestController
@AllArgsConstructor
@RequestMapping("schedulework")
@Api(value = "实际作业表", tags = "实际作业表接口")
public class ScheduleWorkController extends BladeController {

	private IScheduleWorkService scheduleWorkService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入scheduleWork")
	public R<ScheduleWorkVO> detail(ScheduleWork scheduleWork) {
		ScheduleWork detail = scheduleWorkService.getOne(Condition.getQueryWrapper(scheduleWork));
		return R.data(ScheduleWorkWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 实际作业表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入scheduleWork")
	public R<IPage<ScheduleWorkVO>> list(ScheduleWork scheduleWork, Query query) {
		IPage<ScheduleWork> pages = scheduleWorkService.page(Condition.getPage(query), Condition.getQueryWrapper(scheduleWork));
		return R.data(ScheduleWorkWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 实际作业表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入scheduleWork")
	public R<IPage<ScheduleWorkVO>> page(ScheduleWorkVO scheduleWork, Query query) {
		IPage<ScheduleWorkVO> pages = scheduleWorkService.selectScheduleWorkPage(Condition.getPage(query), scheduleWork);
		return R.data(pages);
	}

	/**
	 * 新增 实际作业表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入scheduleWork")
	public R save(@Valid @RequestBody ScheduleWork scheduleWork) {
		return R.status(scheduleWorkService.save(scheduleWork));
	}

	/**
	 * 修改 实际作业表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入scheduleWork")
	public R update(@Valid @RequestBody ScheduleWork scheduleWork) {
		return R.status(scheduleWorkService.updateById(scheduleWork));
	}

	/**
	 * 新增或修改 实际作业表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入scheduleWork")
	public R submit(@Valid @RequestBody ScheduleWork scheduleWork) {
		return R.status(scheduleWorkService.saveOrUpdate(scheduleWork));
	}

	
	/**
	 * 删除 实际作业表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(scheduleWorkService.deleteLogic(Func.toLongList(ids)));
	}

	
}
