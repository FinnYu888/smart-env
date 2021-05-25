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

import java.util.List;

import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleRel;
import com.ai.apac.smartenv.alarm.vo.AlarmRuleRelVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmRuleRelWrapper;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleInfoService;
import com.ai.apac.smartenv.alarm.service.IAlarmRuleRelService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 告警规则关联表 控制器
 *
 * @author Blade
 * @since 2020-02-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarmrulerel")
@Api(value = "告警规则关联表", tags = "告警规则关联表接口")
public class AlarmRuleRelController extends BladeController {

	private IAlarmRuleRelService alarmRuleRelService;
	private IAlarmRuleInfoService alarmRuleInfoService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入alarmRuleRel")
	public R<AlarmRuleRelVO> detail(AlarmRuleRel alarmRuleRel) {
		AlarmRuleRel detail = alarmRuleRelService.getOne(Condition.getQueryWrapper(alarmRuleRel));
		return R.data(AlarmRuleRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 告警规则关联表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入alarmRuleRel")
	public R<IPage<AlarmRuleRelVO>> list(AlarmRuleRel alarmRuleRel, Query query) {
		IPage<AlarmRuleRel> pages = alarmRuleRelService.page(Condition.getPage(query), Condition.getQueryWrapper(alarmRuleRel));
		return R.data(AlarmRuleRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 告警规则关联表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入alarmRuleRel")
	public R<IPage<AlarmRuleRelVO>> page(AlarmRuleRelVO alarmRuleRel, Query query) {
		IPage<AlarmRuleRelVO> pages = alarmRuleRelService.selectAlarmRuleRelPage(Condition.getPage(query), alarmRuleRel);
		return R.data(pages);
	}

	/**
	 * 新增 告警规则关联表
	 */
	@PostMapping
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入alarmRuleRel")
	public R save(@Valid @RequestBody AlarmRuleRel alarmRuleRel) {
		return R.status(alarmRuleRelService.save(alarmRuleRel));
	}

	/**
	 * 修改 告警规则关联表
	 */
	@PutMapping
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入alarmRuleRel")
	public R update(@Valid @RequestBody AlarmRuleRel alarmRuleRel) {
		return R.status(alarmRuleRelService.updateById(alarmRuleRel));
	}

	/**
	 * 新增或修改 告警规则关联表
	 */
/*	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入alarmRuleRel")
	public R submit(@Valid @RequestBody AlarmRuleRel alarmRuleRel) {
		return R.status(alarmRuleRelService.saveOrUpdate(alarmRuleRel));
	}*/

	
	/**
	 * 删除 告警规则关联表
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(alarmRuleRelService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 绑定告警规则
	 */
	@PostMapping("bindAlarmRule")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "绑定告警规则", notes = "")
	public R bindAlarmRule(@RequestParam Long vehicleId, String ruleIds) {
		List<Long> ruleIdList = Func.toLongList(ruleIds);
		ruleIdList.forEach(ruleId -> {
			AlarmRuleInfo ruleInfo = alarmRuleInfoService.getById(ruleId);
			if (ruleInfo == null) {
				return;
			}
			AlarmRuleRel alarmRuleRel = new AlarmRuleRel();
			alarmRuleRel.setAlarmRuleId(ruleId);
			alarmRuleRel.setEntityId(vehicleId);
			alarmRuleRel.setEntityType(VehicleConstant.ALARM_RULE_REL_TYPE_VEHICLE);
			List<AlarmRuleRel> list = alarmRuleRelService.list(Condition.getQueryWrapper(alarmRuleRel));
			if (list != null && list.size() > 0) {
				return;
			}
			alarmRuleRelService.save(alarmRuleRel);
		});
		return R.status(true);
	}
}
