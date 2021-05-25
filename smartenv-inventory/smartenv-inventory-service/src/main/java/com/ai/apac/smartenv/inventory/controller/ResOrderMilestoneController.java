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
package com.ai.apac.smartenv.inventory.controller;

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
import com.ai.apac.smartenv.inventory.entity.ResOrderMilestone;
import com.ai.apac.smartenv.inventory.vo.ResOrderMilestoneVO;
import com.ai.apac.smartenv.inventory.wrapper.ResOrderMilestoneWrapper;
import com.ai.apac.smartenv.inventory.service.IResOrderMilestoneService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("resordermilestone")
@Api(value = "订单里程碑接口", tags = "订单里程碑接口")
public class ResOrderMilestoneController extends BladeController {

	private IResOrderMilestoneService resOrderMilestoneService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resOrderMilestone")
	public R<ResOrderMilestoneVO> detail(ResOrderMilestone resOrderMilestone) {
		ResOrderMilestone detail = resOrderMilestoneService.getOne(Condition.getQueryWrapper(resOrderMilestone));
		return R.data(ResOrderMilestoneWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入resOrderMilestone")
	public R<IPage<ResOrderMilestoneVO>> list(ResOrderMilestone resOrderMilestone, Query query) {
		IPage<ResOrderMilestone> pages = resOrderMilestoneService.page(Condition.getPage(query), Condition.getQueryWrapper(resOrderMilestone));
		return R.data(ResOrderMilestoneWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入resOrderMilestone")
	public R<IPage<ResOrderMilestoneVO>> page(ResOrderMilestoneVO resOrderMilestone, Query query) {
		IPage<ResOrderMilestoneVO> pages = resOrderMilestoneService.selectResOrderMilestonePage(Condition.getPage(query), resOrderMilestone);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resOrderMilestone")
	public R save(@Valid @RequestBody ResOrderMilestone resOrderMilestone) {
		return R.status(resOrderMilestoneService.save(resOrderMilestone));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resOrderMilestone")
	public R update(@Valid @RequestBody ResOrderMilestone resOrderMilestone) {
		return R.status(resOrderMilestoneService.updateOrderMilestoneByCond(resOrderMilestone));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入resOrderMilestone")
	public R submit(@Valid @RequestBody ResOrderMilestone resOrderMilestone) {
		return R.status(resOrderMilestoneService.saveOrUpdate(resOrderMilestone));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(resOrderMilestoneService.deleteLogic(Func.toLongList(ids)));
	}

	
}
