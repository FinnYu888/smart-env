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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.entity.KpiTplCatalog;
import com.ai.apac.smartenv.assessment.vo.KpiTplCatalogVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTplCatalogWrapper;
import com.ai.apac.smartenv.assessment.service.IKpiTplCatalogService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-29
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpitplcatalog")
@Api(value = "考核模板分类", tags = "考核模板分类")
public class KpiTplCatalogController extends BladeController {

	private IKpiTplCatalogService kpiTplCatalogService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiLog(value = "查询考核模板分类详情")
	@ApiOperation(value = "详情", notes = "传入kpiTplCatalog")
	public R<KpiTplCatalogVO> detail(KpiTplCatalog kpiTplCatalog) {
		KpiTplCatalog detail = kpiTplCatalogService.getOne(Condition.getQueryWrapper(kpiTplCatalog));
		return R.data(KpiTplCatalogWrapper.build().entityVO(detail));
	}

	@GetMapping("/list")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "列表", notes = "传入kpiTplCatalog")
	@ApiLog(value = "查询考核模板分类列表")
	public R<List<KpiTplCatalogVO>> list(KpiTplCatalog kpiTplCatalog) {
		List<KpiTplCatalog> list = kpiTplCatalogService.list(Condition.getQueryWrapper(kpiTplCatalog));
		return R.data(KpiTplCatalogWrapper.build().listVO(list));
	}



	/**
	 * 自定义分页
	 */
	@GetMapping("")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入kpiTplCatalog")
	@ApiLog(value = "分页查询考核模板分类详情")
	public R<IPage<KpiTplCatalogVO>> page(KpiTplCatalogVO kpiTplCatalog, Query query) {

		String catalogName = kpiTplCatalog.getCatalogName();
		kpiTplCatalog.setCatalogName(null);

		QueryWrapper<KpiTplCatalog> wrapper=Condition.getQueryWrapper(kpiTplCatalog);
		if (catalogName!=null){
			wrapper.like("catalog_name",catalogName);
		}

		IPage<KpiTplCatalog> page = kpiTplCatalogService.page(Condition.getPage(query), wrapper);
		return R.data(KpiTplCatalogWrapper.build().pageVO(page));
	}
	/**
	 * 新增
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入kpiTplCatalog")
	@ApiLog(value = "新增考核模板分类")
	public R save(@Valid @RequestBody KpiTplCatalog kpiTplCatalog) {
		return R.status(kpiTplCatalogService.save(kpiTplCatalog));
	}


	/**
	 * 删除
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "删除考核模板分类")
	public R remove(@RequestParam String ids) {
		return R.status(kpiTplCatalogService.deleteLogic(Func.toLongList(ids)));
	}

	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入catalog")
	public R update(@Valid @RequestBody KpiTplCatalog catalog) {
		return R.status(kpiTplCatalogService.updateById(catalog));
	}

	
}
