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
package com.ai.apac.smartenv.workarea.controller;

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
import com.ai.apac.smartenv.workarea.entity.WorkareaPathway;
import com.ai.apac.smartenv.workarea.vo.WorkareaPathwayVO;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaPathwayWrapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaPathwayService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2021-01-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("/workareapathway")
@Api(value = "工作区域主要途经点", tags = "工作区域主要途经点信息")
public class WorkareaPathwayController extends BladeController {

	private IWorkareaPathwayService workareaPathwayService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入workareaPathway")
	public R<WorkareaPathwayVO> detail(WorkareaPathway workareaPathway) {
		WorkareaPathway detail = workareaPathwayService.getOne(Condition.getQueryWrapper(workareaPathway));
		return R.data(WorkareaPathwayWrapper.build().entityVO(detail));
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入workareaPathway")
	public R<IPage<WorkareaPathwayVO>> list(WorkareaPathway workareaPathway, Query query) {
		IPage<WorkareaPathway> pages = workareaPathwayService.page(Condition.getPage(query), Condition.getQueryWrapper(workareaPathway));
		return R.data(WorkareaPathwayWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入workareaPathway")
	public R<IPage<WorkareaPathwayVO>> page(WorkareaPathwayVO workareaPathway, Query query) {
		IPage<WorkareaPathwayVO> pages = workareaPathwayService.selectWorkareaPathwayPage(Condition.getPage(query), workareaPathway);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入workareaPathway")
	public R save(@Valid @RequestBody WorkareaPathway workareaPathway) {
		return R.status(workareaPathwayService.save(workareaPathway));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入workareaPathway")
	public R update(@Valid @RequestBody WorkareaPathway workareaPathway) {
		return R.status(workareaPathwayService.updateById(workareaPathway));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入workareaPathway")
	public R submit(@Valid @RequestBody WorkareaPathway workareaPathway) {
		return R.status(workareaPathwayService.saveOrUpdate(workareaPathway));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(workareaPathwayService.deleteLogic(Func.toLongList(ids)));
	}


}
