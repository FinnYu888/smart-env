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

import com.ai.apac.smartenv.assessment.service.IKpiTplDefService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.cache.AssessmentCache;
import com.ai.apac.smartenv.assessment.entity.KpiCatalog;
import com.ai.apac.smartenv.assessment.entity.KpiDef;
import com.ai.apac.smartenv.assessment.vo.KpiDefVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiDefWrapper;
import com.ai.apac.smartenv.assessment.service.IKpiDefService;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;

/**
 * 考核指标定义表 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpidef")
@Api(value = "考核指标定义表", tags = "考核指标定义表接口")
public class KpiDefController extends BladeController {

	private IKpiDefService kpiDefService;

	private IKpiTplDefService kpiTplDefService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入kpiDef")
	@ApiLog(value = "查询考核指标定义详情")
	public R<KpiDefVO> detail(KpiDef kpiDef) {
		KpiDef detail = AssessmentCache.getKpiDefById(kpiDef.getId());
		KpiDefVO kpiDefVO = KpiDefWrapper.build().entityVO(detail);
		kpiDefVO = getKpiDefAllInfoByVO(kpiDefVO);
		return R.data(kpiDefVO);
	}


	/**
	 * 分页 考核指标定义表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "查询考核指标定义列表")
	@ApiOperation(value = "分页", notes = "传入kpiDef")
	public R<IPage<KpiDefVO>> list(KpiDef kpiDef, Query query) {
		IPage<KpiDef> pages = kpiDefService.page(kpiDef, query);
		IPage<KpiDefVO> pageVO = KpiDefWrapper.build().pageVO(pages);
		List<KpiDefVO> records = pageVO.getRecords();
		records.forEach(record -> {
			record = getKpiDefAllInfoByVO(record);
		});
		return R.data(pageVO);
	}



	@GetMapping(value = {"/noPageList"})
	@ApiOperationSupport(order = 2)
	@ApiLog(value = "不分页查询考核指标")
	@ApiOperation(value = "不分页查询", notes = "传入kpiDef")
	public R<List<KpiDefVO>> noPageList(KpiDef kpiDef){
		String kpiDefName=kpiDef.getKpiName();
		KpiDef queryDef = BeanUtil.copy(kpiDef, KpiDef.class);
		queryDef.setKpiName(null);
		queryDef.setTenantId(getUser().getTenantId());
		queryDef.setIsDeleted(0);
		QueryWrapper<KpiDef> wrapper=new QueryWrapper<>(queryDef);
		if (StringUtil.isNotBlank(kpiDefName)){
			wrapper.like("kpi_name",kpiDefName);
		}
		List<KpiDef> list = kpiDefService.list(wrapper);
		List<KpiDefVO> kpiDefVOS = KpiDefWrapper.build().listVO(list);
		kpiDefVOS.forEach(record -> {
			record = getKpiDefAllInfoByVO(record);
		});
		return R.data(kpiDefVOS);
	}


	/**
	 * 自定义分页 考核指标定义表
	 */
	/*@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入kpiDef")
	public R<IPage<KpiDefVO>> page(KpiDefVO kpiDef, Query query) {
		IPage<KpiDefVO> pages = kpiDefService.selectKpiDefPage(Condition.getPage(query), kpiDef);
		return R.data(pages);
	}*/

	/**
	 * 新增 考核指标定义表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入kpiDef")
	@ApiLog(value = "新增考核指标定义")
	public R save(@RequestBody KpiDef kpiDef) {
		// 验证入参
		validateKpiDef(kpiDef);
		boolean save = kpiDefService.saveKpiDef(kpiDef);
		return R.status(save);
	}


	/**
	 * 修改 考核指标定义表
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入kpiDef")
	@ApiLog(value = "修改考核指标定义")
	public R update(@RequestBody KpiDef kpiDef) {
		// 验证入参
		validateKpiDef(kpiDef);
		boolean update = kpiDefService.updateKpiDefById(kpiDef);
        return R.status(update);
	}

	/**
	 * 新增或修改 考核指标定义表
	 */
	/*@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入kpiDef")
	public R submit(@Valid @RequestBody KpiDef kpiDef) {
		return R.status(kpiDefService.saveOrUpdate(kpiDef));
	}*/

	
	/**
	 * 删除 考核指标定义表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "删除考核指标定义")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Long> idList = Func.toLongList(ids);
		kpiTplDefService.verifyKpiIsUsed(idList);
		boolean remove = kpiDefService.removeKpiDef(idList);
        return R.status(remove);
	}

	private void validateKpiDef(@Valid KpiDef kpiDef) {
		Set<ConstraintViolation<@Valid KpiDef>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator().validate(kpiDef, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}
	}
	
	private KpiDefVO getKpiDefAllInfoByVO(KpiDefVO kpiDefVO) {
		Long kpiCatalogId = kpiDefVO.getKpiCatalog();
		KpiCatalog kpiCatalog = AssessmentCache.getKpiCatalogById(kpiCatalogId);
		if (kpiCatalog != null && StringUtils.isNotBlank(kpiCatalog.getCatalogName())) {
			kpiDefVO.setKpiCatalogName(kpiCatalog.getCatalogName());
		}
		return kpiDefVO;
	}
}
