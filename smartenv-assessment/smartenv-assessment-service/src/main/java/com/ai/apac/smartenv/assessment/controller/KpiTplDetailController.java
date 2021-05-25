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
import com.ai.apac.smartenv.assessment.entity.KpiTplDetail;
import com.ai.apac.smartenv.assessment.vo.KpiTplDetailVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTplDetailWrapper;
import com.ai.apac.smartenv.assessment.service.IKpiTplDetailService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 考核模板明细 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpitpldetail")
@Api(value = "考核模板明细", tags = "考核模板明细接口")
public class KpiTplDetailController extends BladeController {

	private IKpiTplDetailService kpiTplDetailService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入kpiTplDetail")
	public R<KpiTplDetailVO> detail(KpiTplDetail kpiTplDetail) {
		KpiTplDetail detail = kpiTplDetailService.getOne(Condition.getQueryWrapper(kpiTplDetail));
		return R.data(KpiTplDetailWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 考核模板明细
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入kpiTplDetail")
	public R<IPage<KpiTplDetailVO>> list(KpiTplDetail kpiTplDetail, Query query) {
		IPage<KpiTplDetail> pages = kpiTplDetailService.page(Condition.getPage(query), Condition.getQueryWrapper(kpiTplDetail));
		return R.data(KpiTplDetailWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 考核模板明细
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入kpiTplDetail")
	public R<IPage<KpiTplDetailVO>> page(KpiTplDetailVO kpiTplDetail, Query query) {
		IPage<KpiTplDetailVO> pages = kpiTplDetailService.selectKpiTplDetailPage(Condition.getPage(query), kpiTplDetail);
		return R.data(pages);
	}

	/**
	 * 新增 考核模板明细
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入kpiTplDetail")
	public R save(@Valid @RequestBody KpiTplDetail kpiTplDetail) {
		return R.status(kpiTplDetailService.save(kpiTplDetail));
	}

	/**
	 * 修改 考核模板明细
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入kpiTplDetail")
	public R update(@Valid @RequestBody KpiTplDetail kpiTplDetail) {
		return R.status(kpiTplDetailService.updateById(kpiTplDetail));
	}

	/**
	 * 新增或修改 考核模板明细
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入kpiTplDetail")
	public R submit(@Valid @RequestBody KpiTplDetail kpiTplDetail) {
		return R.status(kpiTplDetailService.saveOrUpdate(kpiTplDetail));
	}

	
	/**
	 * 删除 考核模板明细
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(kpiTplDetailService.deleteLogic(Func.toLongList(ids)));
	}

	
}
