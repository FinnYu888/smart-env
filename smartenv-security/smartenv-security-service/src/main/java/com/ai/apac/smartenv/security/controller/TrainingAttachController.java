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
package com.ai.apac.smartenv.security.controller;

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
import com.ai.apac.smartenv.security.entity.TrainingAttach;
import com.ai.apac.smartenv.security.vo.TrainingAttachVO;
import com.ai.apac.smartenv.security.wrapper.TrainingAttachWrapper;
import com.ai.apac.smartenv.security.service.ITrainingAttachService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 培训记录附件表 控制器
 *
 * @author Blade
 * @since 2020-08-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/trainingattach")
@Api(value = "培训记录附件表", tags = "培训记录附件表接口")
public class TrainingAttachController extends BladeController {

	private ITrainingAttachService trainingAttachService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入trainingAttach")
	public R<TrainingAttachVO> detail(TrainingAttach trainingAttach) {
		TrainingAttach detail = trainingAttachService.getOne(Condition.getQueryWrapper(trainingAttach));
		return R.data(TrainingAttachWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 培训记录附件表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入trainingAttach")
	public R<IPage<TrainingAttachVO>> list(TrainingAttach trainingAttach, Query query) {
		IPage<TrainingAttach> pages = trainingAttachService.page(Condition.getPage(query), Condition.getQueryWrapper(trainingAttach));
		return R.data(TrainingAttachWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 培训记录附件表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入trainingAttach")
	public R<IPage<TrainingAttachVO>> page(TrainingAttachVO trainingAttach, Query query) {
		IPage<TrainingAttachVO> pages = trainingAttachService.selectTrainingAttachPage(Condition.getPage(query), trainingAttach);
		return R.data(pages);
	}

	/**
	 * 新增 培训记录附件表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入trainingAttach")
	public R save(@Valid @RequestBody TrainingAttach trainingAttach) {
		return R.status(trainingAttachService.save(trainingAttach));
	}

	/**
	 * 修改 培训记录附件表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入trainingAttach")
	public R update(@Valid @RequestBody TrainingAttach trainingAttach) {
		return R.status(trainingAttachService.updateById(trainingAttach));
	}

	/**
	 * 删除 培训记录附件表
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(trainingAttachService.deleteLogic(Func.toLongList(ids)));
	}

	
}
