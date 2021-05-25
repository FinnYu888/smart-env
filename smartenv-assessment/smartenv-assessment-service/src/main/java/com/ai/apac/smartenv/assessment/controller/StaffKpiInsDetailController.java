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

import com.ai.apac.smartenv.assessment.vo.KpiTargetLostPointsStaVO;
import com.ai.apac.smartenv.assessment.vo.KpiTargetLostPointsVO;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.assessment.entity.StaffKpiInsDetail;
import com.ai.apac.smartenv.assessment.vo.StaffKpiInsDetailVO;
import com.ai.apac.smartenv.assessment.wrapper.StaffKpiInsDetailWrapper;
import com.ai.apac.smartenv.assessment.service.IStaffKpiInsDetailService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.List;

/**
 * 员工考核实例明细 控制器
 *
 * @author Blade
 * @since 2020-02-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/staffkpiinsdetail")
@Api(value = "员工考核实例明细", tags = "员工考核实例明细接口")
public class StaffKpiInsDetailController extends BladeController {

	private IStaffKpiInsDetailService staffKpiInsDetailService;

	private MongoTemplate mongoTemplate;


	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入staffKpiInsDetail")
	public R<StaffKpiInsDetailVO> detail(StaffKpiInsDetail staffKpiInsDetail) {
		StaffKpiInsDetail detail = staffKpiInsDetailService.getOne(Condition.getQueryWrapper(staffKpiInsDetail));
		return R.data(StaffKpiInsDetailWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 员工考核实例明细
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入staffKpiInsDetail")
	public R<IPage<StaffKpiInsDetailVO>> list(StaffKpiInsDetail staffKpiInsDetail, Query query) {
		IPage<StaffKpiInsDetail> pages = staffKpiInsDetailService.page(Condition.getPage(query), Condition.getQueryWrapper(staffKpiInsDetail));
		return R.data(StaffKpiInsDetailWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 员工考核实例明细
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入staffKpiInsDetail")
	public R<IPage<StaffKpiInsDetailVO>> page(StaffKpiInsDetailVO staffKpiInsDetail, Query query) {
		IPage<StaffKpiInsDetailVO> pages = staffKpiInsDetailService.selectStaffKpiInsDetailPage(Condition.getPage(query), staffKpiInsDetail);
		return R.data(pages);
	}

	/**
	 * 新增 员工考核实例明细
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入staffKpiInsDetail")
	public R save(@Valid @RequestBody StaffKpiInsDetail staffKpiInsDetail) {
		return R.status(staffKpiInsDetailService.save(staffKpiInsDetail));
	}

	/**
	 * 修改 员工考核实例明细
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入staffKpiInsDetail")
	public R update(@Valid @RequestBody StaffKpiInsDetail staffKpiInsDetail) {
		return R.status(staffKpiInsDetailService.updateById(staffKpiInsDetail));
	}

	/**
	 * 新增或修改 员工考核实例明细
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入staffKpiInsDetail")
	public R submit(@Valid @RequestBody StaffKpiInsDetail staffKpiInsDetail) {
		return R.status(staffKpiInsDetailService.saveOrUpdate(staffKpiInsDetail));
	}

	
	/**
	 * 删除 员工考核实例明细
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(staffKpiInsDetailService.deleteLogic(Func.toLongList(ids)));
	}


	/**
	 * 考核指标失分统计
	 */
	@PostMapping("/lostPoints")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "近N天考核指标扣分统计", notes = "")
	public R<KpiTargetLostPointsStaVO> kpiTargetLostPointsCount() {
		org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
		query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
		return R.data(mongoTemplate.findOne(query, KpiTargetLostPointsStaVO.class,"KpiTargetLostPointsSta"));
	}

}
