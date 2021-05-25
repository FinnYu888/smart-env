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

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.smartenv.alarm.dto.AlarmInformArrayDTO;
import com.ai.apac.smartenv.alarm.dto.AlarmInformDTO;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.alarm.entity.AlarmInform;
import com.ai.apac.smartenv.alarm.vo.AlarmInformVO;
import com.ai.apac.smartenv.alarm.wrapper.AlarmInformWrapper;
import com.ai.apac.smartenv.alarm.service.IAlarmInformService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.Collection;
import java.util.List;

/**
 * 告警通知方式配置表 控制器
 *
 * @author Blade
 * @since 2020-12-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alarminform")
@Api(value = "告警通知方式配置表", tags = "告警通知方式配置表接口")
public class AlarmInformController extends BladeController {

	private IAlarmInformService alarmInformService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入alarmInform")
	public R<AlarmInformVO> detail(AlarmInform alarmInform) {
		AlarmInform detail = alarmInformService.getOne(Condition.getQueryWrapper(alarmInform));
		return R.data(AlarmInformWrapper.build().entityVO(detail));
	}

	/**
	 * 不分页 告警通知方式配置表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "不分页", notes = "传入alarmInform")
	public R<List<AlarmInformVO>> list(AlarmInform alarmInform) {
		List<AlarmInform> list = alarmInformService.list(Condition.getQueryWrapper(alarmInform));
		return R.data(AlarmInformWrapper.build().listVO(list));
	}


	/**
	 * 自定义分页 告警通知方式配置表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入alarmInform")
	public R<IPage<AlarmInformVO>> page(AlarmInformVO alarmInform, Query query) {
		IPage<AlarmInformVO> pages = alarmInformService.selectAlarmInformPage(Condition.getPage(query), alarmInform);
		return R.data(pages);
	}

	/**
	 * 新增 告警通知方式配置表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入alarmInform")
	public R save(@RequestBody AlarmInformArrayDTO alarmInformArrayDTO) {
		List<AlarmInformDTO> informTypeList = alarmInformArrayDTO.getInformTypeList();
		if (CollectionUtil.isEmpty(informTypeList)) {
			throw new ServiceException("告警通知方式配置数据为空！");
		}
		for (AlarmInformDTO alarmInformDTO : informTypeList) {
			alarmInformService.saveOrUpdateAlarmInform(alarmInformDTO);
		}
		return R.status(true);
	}

	/**
	 * 修改 告警通知方式配置表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入alarmInform")
	public R update(@Valid @RequestBody AlarmInform alarmInform) {
		return R.status(alarmInformService.updateById(alarmInform));
	}

//	/**
//	 * 新增或修改 告警通知方式配置表
//	 */
//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入alarmInform")
//	public R submit(@Valid @RequestBody AlarmInform alarmInform) {
//		return R.status(alarmInformService.saveOrUpdate(alarmInform));
//	}

	
	/**
	 * 删除 告警通知方式配置表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(alarmInformService.deleteLogic(Func.toLongList(ids)));
	}

	
}
