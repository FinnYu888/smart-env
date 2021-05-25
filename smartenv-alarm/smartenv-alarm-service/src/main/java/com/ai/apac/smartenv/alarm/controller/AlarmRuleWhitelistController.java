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

import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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
import com.ai.apac.smartenv.alarm.entity.AlarmRuleWhitelist;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleWhitelistVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleWhitelistWrapper;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleWhitelistService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 告警白名单表 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarmrulewhitelist")
@Api(value = "告警白名单表", tags = "告警白名单表接口")
public class AlarmRuleWhitelistController extends BladeController {

	private IAlarmRuleWhitelistService alarmRuleWhitelistService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入alarmRuleWhitelist")
	public R<AlarmRuleWhitelistVO> detail(AlarmRuleWhitelist alarmRuleWhitelist) {
		AlarmRuleWhitelist detail = alarmRuleWhitelistService.getOne(Condition.getQueryWrapper(alarmRuleWhitelist));
		return R.data(AlarmRuleWhitelistWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 告警白名单表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入alarmRuleWhitelist")
	public R<IPage<AlarmRuleWhitelistVO>> list(AlarmRuleWhitelist alarmRuleWhitelist, Query query) {
		IPage<AlarmRuleWhitelist> pages = alarmRuleWhitelistService.page(Condition.getPage(query), Condition.getQueryWrapper(alarmRuleWhitelist));
		return R.data(AlarmRuleWhitelistWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 告警白名单表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入alarmRuleWhitelist")
	public R<IPage<AlarmRuleWhitelistVO>> page(AlarmRuleWhitelistVO alarmRuleWhitelist, Query query) {
		IPage<AlarmRuleWhitelistVO> pages = alarmRuleWhitelistService.selectAlarmRuleWhitelistPage(Condition.getPage(query), alarmRuleWhitelist);
		return R.data(pages);
	}

	/**
	 * 新增 告警白名单表
	 */
	@PostMapping
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入alarmRuleWhitelist")
	public R save(@Valid @RequestBody AlarmRuleWhitelist alarmRuleWhitelist) {
		return R.status(alarmRuleWhitelistService.save(alarmRuleWhitelist));
	}

	/**
	 * 修改 告警白名单表
	 */
	@PutMapping
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入alarmRuleWhitelist")
	public R update(@Valid @RequestBody AlarmRuleWhitelist alarmRuleWhitelist) {
		return R.status(alarmRuleWhitelistService.updateById(alarmRuleWhitelist));
	}

	/**
	 * 新增或修改 告警白名单表
	 */
/*	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入alarmRuleWhitelist")
	public R submit(@Valid @RequestBody AlarmRuleWhitelist alarmRuleWhitelist) {
		return R.status(alarmRuleWhitelistService.saveOrUpdate(alarmRuleWhitelist));
	}*/

	
	/**
	 * 删除 告警白名单表
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(alarmRuleWhitelistService.deleteLogic(Func.toLongList(ids)));
	}

	
}
