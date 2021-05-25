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
package com.ai.apac.smartenv.alarm.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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
import com.ai.apac.smartenv.alarm.entity.AlarmRuleExt;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleExtVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleExtWrapper;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleExtService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 告警规则参数表 控制器
 *
 * @author Blade
 * @since 2020-02-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("alarmruleext")
@Api(value = "告警规则参数表", tags = "告警规则参数表接口")
public class AlarmRuleExtController extends BladeController {

	private IAlarmRuleExtService alarmRuleExtService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入alarmRuleExt")
	public R<AlarmRuleExtVO> detail(AlarmRuleExt alarmRuleExt) {
		AlarmRuleExt detail = alarmRuleExtService.getOne(Condition.getQueryWrapper(alarmRuleExt));
		return R.data(AlarmRuleExtWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 告警规则参数表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入alarmRuleExt")
	public R<IPage<AlarmRuleExtVO>> list(AlarmRuleExt alarmRuleExt, Query query) {
		IPage<AlarmRuleExt> pages = alarmRuleExtService.page(Condition.getPage(query), Condition.getQueryWrapper(alarmRuleExt));
		return R.data(AlarmRuleExtWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 告警规则参数表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入alarmRuleExt")
	public R<IPage<AlarmRuleExtVO>> page(AlarmRuleExtVO alarmRuleExt, Query query) {
		IPage<AlarmRuleExtVO> pages = alarmRuleExtService.selectAlarmRuleExtPage(Condition.getPage(query), alarmRuleExt);
		return R.data(pages);
	}

	/**
	 * 新增 告警规则参数表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入alarmRuleExt")
	public R save(@Valid @RequestBody AlarmRuleExt alarmRuleExt) {
		return R.status(alarmRuleExtService.save(alarmRuleExt));
	}

	/**
	 * 修改 告警规则参数表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入alarmRuleExt")
	public R update(@Valid @RequestBody AlarmRuleExt alarmRuleExt) {
		return R.status(alarmRuleExtService.updateById(alarmRuleExt));
	}

	/**
	 * 新增或修改 告警规则参数表
	 */
/*	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入alarmRuleExt")
	public R submit(@Valid @RequestBody AlarmRuleExt alarmRuleExt) {
		return R.status(alarmRuleExtService.saveOrUpdate(alarmRuleExt));
	}*/

	
	/**
	 * 删除 告警规则参数表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(alarmRuleExtService.deleteLogic(Func.toLongList(ids)));
	}

	
}
