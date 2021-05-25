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
package com.ai.apac.flow.engine.controller;

import com.ai.apac.smartenv.flow.entity.FlowTaskAllot;
import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.flow.engine.wrapper.FlowTaskAllotWrapper;

import com.ai.apac.smartenv.flow.vo.FlowTaskAllotVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
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
 * @since 2020-08-26
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-flow-task-allot/flowtaskallot")
@Api(value = "流程任务分配设置", tags = "流程任务分配设置")
public class FlowTaskAllotController extends BladeController {

	private IFlowTaskAllotService flowTaskAllotService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入flowTaskAllot")
	public R<FlowTaskAllotVO> detail(FlowTaskAllot flowTaskAllot) {
		FlowTaskAllot detail = flowTaskAllotService.getOne(Condition.getQueryWrapper(flowTaskAllot));
		return R.data(FlowTaskAllotWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入flowTaskAllot")
	public R<IPage<FlowTaskAllotVO>> list(FlowTaskAllot flowTaskAllot, Query query) {
		IPage<FlowTaskAllot> pages = flowTaskAllotService.page(Condition.getPage(query), Condition.getQueryWrapper(flowTaskAllot));
		return R.data(FlowTaskAllotWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入flowTaskAllot")
	public R<IPage<FlowTaskAllotVO>> page(FlowTaskAllotVO flowTaskAllot, Query query) {
		IPage<FlowTaskAllotVO> pages = flowTaskAllotService.selectFlowTaskAllotPage(Condition.getPage(query), flowTaskAllot);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入flowTaskAllot")
	public R save(@Valid @RequestBody FlowTaskAllot flowTaskAllot) {
		return R.status(flowTaskAllotService.save(flowTaskAllot));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入flowTaskAllot")
	public R update(@Valid @RequestBody FlowTaskAllot flowTaskAllot) {
		return R.status(flowTaskAllotService.updateById(flowTaskAllot));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入flowTaskAllot")
	public R submit(@Valid @RequestBody FlowTaskAllot flowTaskAllot) {
		return R.status(flowTaskAllotService.saveOrUpdate(flowTaskAllot));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(flowTaskAllotService.deleteLogic(Func.toLongList(ids)));
	}

	
}
