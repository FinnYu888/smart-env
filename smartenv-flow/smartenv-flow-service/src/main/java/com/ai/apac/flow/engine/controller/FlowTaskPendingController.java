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

import com.ai.apac.smartenv.flow.entity.FlowTaskPending;
import com.ai.apac.flow.engine.service.IFlowTaskPendingService;
import com.ai.apac.flow.engine.wrapper.FlowTaskPendingWrapper;
import com.ai.apac.smartenv.flow.vo.FlowTaskPendingVO;
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
@RequestMapping("blade-flow-task-allot/flowtaskpending")
@Api(value = "流程任务待处理接口", tags = "流程任务待处理接口")
public class FlowTaskPendingController extends BladeController {

	private IFlowTaskPendingService flowTaskPendingService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入flowTaskPending")
	public R<FlowTaskPendingVO> detail(FlowTaskPending flowTaskPending) {
		FlowTaskPending detail = flowTaskPendingService.getOne(Condition.getQueryWrapper(flowTaskPending));
		return R.data(FlowTaskPendingWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入flowTaskPending")
	public R<IPage<FlowTaskPendingVO>> list(FlowTaskPending flowTaskPending, Query query) {
		IPage<FlowTaskPending> pages = flowTaskPendingService.page(Condition.getPage(query), Condition.getQueryWrapper(flowTaskPending));
		return R.data(FlowTaskPendingWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入flowTaskPending")
	public R<IPage<FlowTaskPendingVO>> page(FlowTaskPendingVO flowTaskPending, Query query) {
		IPage<FlowTaskPendingVO> pages = flowTaskPendingService.selectFlowTaskPendingPage(Condition.getPage(query), flowTaskPending);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入flowTaskPending")
	public R save(@Valid @RequestBody FlowTaskPending flowTaskPending) {
		return R.status(flowTaskPendingService.save(flowTaskPending));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入flowTaskPending")
	public R update(@Valid @RequestBody FlowTaskPending flowTaskPending) {
		return R.status(flowTaskPendingService.updateById(flowTaskPending));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入flowTaskPending")
	public R submit(@Valid @RequestBody FlowTaskPending flowTaskPending) {
		return R.status(flowTaskPendingService.saveOrUpdate(flowTaskPending));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(flowTaskPendingService.deleteLogic(Func.toLongList(ids)));
	}

	
}
