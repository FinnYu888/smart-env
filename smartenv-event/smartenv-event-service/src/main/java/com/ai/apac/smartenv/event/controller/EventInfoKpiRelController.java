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
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.vo.EventInfoKpiRelVO;
import com.ai.apac.smartenv.event.wrapper.EventInfoKpiRelWrapper;
import com.ai.apac.smartenv.event.service.IEventInfoKpiRelService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 事件指标与事件信息关联表 控制器
 *
 * @author Blade
 * @since 2020-12-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-event_info_kpi_rel/eventinfokpirel")
@Api(value = "事件指标与事件信息关联表", tags = "事件指标与事件信息关联表接口")
public class EventInfoKpiRelController extends BladeController {

	private IEventInfoKpiRelService eventInfoKpiRelService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入eventInfoKpiRel")
	public R<EventInfoKpiRelVO> detail(EventInfoKpiRel eventInfoKpiRel) {
		EventInfoKpiRel detail = eventInfoKpiRelService.getOne(Condition.getQueryWrapper(eventInfoKpiRel));
		return R.data(EventInfoKpiRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 事件指标与事件信息关联表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入eventInfoKpiRel")
	public R<IPage<EventInfoKpiRelVO>> list(EventInfoKpiRel eventInfoKpiRel, Query query) {
		IPage<EventInfoKpiRel> pages = eventInfoKpiRelService.page(Condition.getPage(query), Condition.getQueryWrapper(eventInfoKpiRel));
		return R.data(EventInfoKpiRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 事件指标与事件信息关联表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入eventInfoKpiRel")
	public R<IPage<EventInfoKpiRelVO>> page(EventInfoKpiRelVO eventInfoKpiRel, Query query) {
		IPage<EventInfoKpiRelVO> pages = eventInfoKpiRelService.selectEventInfoKpiRelPage(Condition.getPage(query), eventInfoKpiRel);
		return R.data(pages);
	}

	/**
	 * 新增 事件指标与事件信息关联表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入eventInfoKpiRel")
	public R save(@Valid @RequestBody EventInfoKpiRel eventInfoKpiRel) {
		return R.status(eventInfoKpiRelService.save(eventInfoKpiRel));
	}

	/**
	 * 修改 事件指标与事件信息关联表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入eventInfoKpiRel")
	public R update(@Valid @RequestBody EventInfoKpiRel eventInfoKpiRel) {
		return R.status(eventInfoKpiRelService.updateById(eventInfoKpiRel));
	}

	/**
	 * 新增或修改 事件指标与事件信息关联表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入eventInfoKpiRel")
	public R submit(@Valid @RequestBody EventInfoKpiRel eventInfoKpiRel) {
		return R.status(eventInfoKpiRelService.saveOrUpdate(eventInfoKpiRel));
	}


	/**
	 * 删除 事件指标与事件信息关联表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(eventInfoKpiRelService.deleteLogic(Func.toLongList(ids)));
	}


}
