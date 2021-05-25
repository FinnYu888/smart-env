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

import com.ai.apac.smartenv.security.entity.TrainingObject;
import com.ai.apac.smartenv.security.service.ITrainingObjectService;
import com.ai.apac.smartenv.security.vo.TrainingObjectVO;
import com.ai.apac.smartenv.security.wrapper.TrainingObjectWrapper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-08-20
 */
@RestController
@AllArgsConstructor
@RequestMapping("/trainingobject")
@Api(value = "", tags = "培训对象表接口")
public class TrainingObjectController extends BladeController {

	private ITrainingObjectService trainingObjectService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入trainingObject")
	public R<TrainingObjectVO> detail(TrainingObject trainingObject) {
		TrainingObject detail = trainingObjectService.getOne(Condition.getQueryWrapper(trainingObject));
		return R.data(TrainingObjectWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入trainingObject")
	public R<IPage<TrainingObjectVO>> list(TrainingObject trainingObject, Query query) {
		IPage<TrainingObject> pages = trainingObjectService.page(Condition.getPage(query), Condition.getQueryWrapper(trainingObject));
		return R.data(TrainingObjectWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入trainingObject")
	public R<IPage<TrainingObjectVO>> page(TrainingObjectVO trainingObject, Query query) {
		IPage<TrainingObjectVO> pages = trainingObjectService.selectTrainingObjectPage(Condition.getPage(query), trainingObject);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入trainingObject")
	public R save(@Valid @RequestBody TrainingObject trainingObject) {
		return R.status(trainingObjectService.save(trainingObject));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入trainingObject")
	public R update(@Valid @RequestBody TrainingObject trainingObject) {
		return R.status(trainingObjectService.updateById(trainingObject));
	}

	/**
	 * 删除 
	 */
	@DeleteMapping
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(trainingObjectService.deleteLogic(Func.toLongList(ids)));
	}

	
}
