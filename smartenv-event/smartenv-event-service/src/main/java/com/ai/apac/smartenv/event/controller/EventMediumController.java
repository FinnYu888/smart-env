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
package com.ai.apac.smartenv.event.controller;

import com.ai.apac.smartenv.event.entity.EventMedium;
import com.ai.apac.smartenv.event.service.IEventMediumService;
import com.ai.apac.smartenv.event.vo.EventMediumVO;
import com.ai.apac.smartenv.event.wrapper.EventMediumWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 事件基本信息表 控制器
 *
 * @author Blade
 * @since 2020-02-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eventmedium")
@Api(value = "事件基本信息表", tags = "事件媒介信息表接口")
public class EventMediumController extends BladeController {

	private IEventMediumService eventMediumService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "eventMedium")
	@ApiLog("查详情")
	public R<EventMediumVO> detail(EventMedium eventMedium) {
		EventMedium detail = eventMediumService.getOne(Condition.getQueryWrapper(eventMedium));
		return R.data(EventMediumWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 事件基本信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入eventMedium")
	@ApiLog("分页查询事件列表")
	public R<IPage<EventMediumVO>> list(EventMedium eventMedium, Query query) {
		IPage<EventMedium> pages = eventMediumService.page(Condition.getPage(query), Condition.getQueryWrapper(eventMedium));
		return R.data(EventMediumWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 事件基本信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入EventMedium")
	@ApiLog("默认分页查询方法")
	public R<IPage<EventMediumVO>> page(EventMediumVO eventMedium, Query query) {
		IPage<EventMediumVO> pages = eventMediumService.selectEventMediumPage(Condition.getPage(query), eventMedium);
		return R.data(pages);
	}

	/**
	 * 新增 事件基本信息表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入EventMedium")
	@ApiLog("默认保存方法")
	public R save(@Valid @RequestBody EventMedium eventMedium) {
		return R.status(eventMediumService.save(eventMedium));
	}

	/**
	 * 修改 事件基本信息表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入EventMedium")
	@ApiLog("默认更新方法")
	public R update(@Valid @RequestBody EventMedium eventMedium) {
		return R.status(eventMediumService.updateById(eventMedium));
	}

	/**
	 * 新增或修改 事件基本信息表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入EventMedium")
	@ApiLog("默认submit方法")
	public R submit(@Valid @RequestBody EventMedium eventMedium) {
		return R.status(eventMediumService.saveOrUpdate(eventMedium));
	}

	
	/**
	 * 删除 事件基本信息表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog("默认删除方法")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(eventMediumService.deleteLogic(Func.toLongList(ids)));
	}

	
}
