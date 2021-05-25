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
package com.ai.apac.smartenv.assessment.controller;

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
import com.ai.apac.smartenv.assessment.entity.KpiTargetDetail;
import com.ai.apac.smartenv.assessment.vo.KpiTargetDetailVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTargetDetailWrapper;
import com.ai.apac.smartenv.assessment.service.IKpiTargetDetailService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-03-02
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpitargetdetail")
@Api(value = "Target_detail", tags = "Target_detail")
public class KpiTargetDetailController extends BladeController {

	private IKpiTargetDetailService kpiTargetDetailService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入kpiTargetDetail")
	public R<KpiTargetDetailVO> detail(KpiTargetDetail kpiTargetDetail) {
		KpiTargetDetail detail = kpiTargetDetailService.getOne(Condition.getQueryWrapper(kpiTargetDetail));
		return R.data(KpiTargetDetailWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入kpiTargetDetail")
	public R<IPage<KpiTargetDetailVO>> list(KpiTargetDetail kpiTargetDetail, Query query) {
		IPage<KpiTargetDetail> pages = kpiTargetDetailService.page(Condition.getPage(query), Condition.getQueryWrapper(kpiTargetDetail));
		return R.data(KpiTargetDetailWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入kpiTargetDetail")
	public R<IPage<KpiTargetDetailVO>> page(KpiTargetDetailVO kpiTargetDetail, Query query) {
		IPage<KpiTargetDetailVO> pages = kpiTargetDetailService.selectKpiTargetDetailPage(Condition.getPage(query), kpiTargetDetail);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入kpiTargetDetail")
	public R save(@Valid @RequestBody KpiTargetDetail kpiTargetDetail) {
		return R.status(kpiTargetDetailService.save(kpiTargetDetail));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入kpiTargetDetail")
	public R update(@Valid @RequestBody KpiTargetDetail kpiTargetDetail) {
		return R.status(kpiTargetDetailService.updateById(kpiTargetDetail));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入kpiTargetDetail")
	public R submit(@Valid @RequestBody KpiTargetDetail kpiTargetDetail) {
		return R.status(kpiTargetDetailService.saveOrUpdate(kpiTargetDetail));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(kpiTargetDetailService.deleteLogic(Func.toLongList(ids)));
	}

	
}
