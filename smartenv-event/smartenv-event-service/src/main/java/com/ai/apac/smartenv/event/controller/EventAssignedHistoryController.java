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

import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import com.ai.apac.smartenv.event.service.IEventAssignedHistoryService;
import com.ai.apac.smartenv.event.service.IEventInfoService;
import com.ai.apac.smartenv.event.vo.EventAssignedAllVO;
import com.ai.apac.smartenv.event.vo.EventAssignedHistoryVO;
import com.ai.apac.smartenv.event.wrapper.EventAssignedHistoryWrapper;
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
 *  控制器
 *
 * @author Blade
 * @since 2020-03-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eventassignedhistory")
@Api(value = "", tags = "事件指派历史记录接口")
public class EventAssignedHistoryController extends BladeController {

	private IEventAssignedHistoryService eventAssignedHistoryService;
	private IEventInfoService eventInfoService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入eventAssignedHistory")
	@ApiLog("查详情")
	public R<EventAssignedHistoryVO> detail(EventAssignedHistory eventAssignedHistory) {
		EventAssignedHistory detail = eventAssignedHistoryService.getOne(Condition.getQueryWrapper(eventAssignedHistory));
		return R.data(EventAssignedHistoryWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入eventAssignedHistory")
	@ApiLog("分页查询列表信息")
	public R<IPage<EventAssignedHistoryVO>> list(EventAssignedHistory eventAssignedHistory, Query query) {
		IPage<EventAssignedHistory> pages = eventAssignedHistoryService.page(Condition.getPage(query), Condition.getQueryWrapper(eventAssignedHistory));
		return R.data(EventAssignedHistoryWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入eventAssignedHistory")
	@ApiLog("默认分页查询")
	public R<IPage<EventAssignedHistoryVO>> page(EventAssignedHistoryVO eventAssignedHistory, Query query) {
		IPage<EventAssignedHistoryVO> pages = eventAssignedHistoryService.selectEventAssignedHistoryPage(Condition.getPage(query), eventAssignedHistory);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入eventAssignedHistory")
	@ApiLog("默认保存方法")
	public R save(@Valid @RequestBody EventAssignedHistory eventAssignedHistory) {
		return R.status(eventAssignedHistoryService.save(eventAssignedHistory));
	}

	/**
	 * 重新指派
	 */
	@PostMapping("/reassign")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "重新指派", notes = "传入eventAssignedHistory")
	@ApiLog("重新指派")
	public R reassign(@Valid @RequestBody EventAssignedHistory eventAssignedHistory) {
		return R.status(eventInfoService.reassign(eventAssignedHistory));
	}

	/**
	 * 检查
	 */
	@PostMapping("/eventCheck")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "检查", notes = "传入eventAssignedHistory")
	@ApiLog("事件检查")
	public R eventCheck(@Valid @RequestBody EventAssignedAllVO eventAssignedHistory) {
		return R.status(eventInfoService.eventCheck(eventAssignedHistory));
	}

	/**
	 * 事件整改
	 */
	@PostMapping("/rectification")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "事件整改", notes = "传入eventAssignedHistory")
	@ApiLog("事件整改")
	public R rectification(@Valid @RequestBody EventAssignedAllVO eventAssignedHistory) {
		return R.status(eventInfoService.rectification(eventAssignedHistory));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入eventAssignedHistory")
	@ApiLog("默认更新方法")
	public R update(@Valid @RequestBody EventAssignedHistory eventAssignedHistory) {
		return R.status(eventAssignedHistoryService.updateById(eventAssignedHistory));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入eventAssignedHistory")
	@ApiLog("默认submit方法")
	public R submit(@Valid @RequestBody EventAssignedHistory eventAssignedHistory) {
		return R.status(eventAssignedHistoryService.saveOrUpdate(eventAssignedHistory));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog("默认删除方法")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(eventAssignedHistoryService.deleteLogic(Func.toLongList(ids)));
	}

	
}
