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

import com.ai.apac.smartenv.assessment.dto.KpiTargetDTO;
import com.ai.apac.smartenv.assessment.dto.KpiTargetQueryDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
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
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.entity.KpiTarget;
import com.ai.apac.smartenv.assessment.vo.KpiTargetVO;
import com.ai.apac.smartenv.assessment.wrapper.KpiTargetWrapper;
import com.ai.apac.smartenv.assessment.service.IKpiTargetService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-03-02
 */
@RestController
@AllArgsConstructor
@RequestMapping("/kpitarget")
@Api(value = "kpi_target", tags = "指定考核目标")
public class KpiTargetController extends BladeController {

	private IKpiTargetService kpiTargetService;

	private IPersonClient personClient;


	@GetMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "查询考核目标详情", notes = "传入target_id")
	@Transactional(rollbackFor = Exception.class)
	@ApiLog(value = "根据ID获取考核目标详细信息")
	public R<KpiTargetVO> getTargetDetail(@ApiParam(value = "主键", required = true) @RequestParam String id) {
		KpiTargetVO kpiTargetVO = kpiTargetService.getAllKpiTargetDetail(id);
		Person person = personClient.getPerson(kpiTargetVO.getGraderId()).getData();
		kpiTargetVO.setGraderName(ObjectUtil.isNotEmpty(person)?person.getPersonName():"");
		return R.data(kpiTargetVO);
	}



	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入kpiTarget")
	@ApiLog(value = "考核目标分页列表")
	public R<IPage<KpiTargetVO>> page(KpiTargetQueryDTO kpiTargetQueryDTO, Query query) {
		IPage<KpiTargetVO> pages = kpiTargetService.selectKpiTargetPage(Condition.getPage(query), kpiTargetQueryDTO);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入kpiTarget")
	@ApiLog(value = "新增考核目标")
	public R saveKpiTarget(@Valid @RequestBody KpiTargetDTO kpiTargetDTO) {
		return R.status(kpiTargetService.saveOrUpdateKpiTarget(kpiTargetDTO));
	}

	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "逻辑删除考核目标")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(kpiTargetService.deleteKpiTarget(Func.toLongList(ids)));
	}

	
}
