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
package com.ai.apac.smartenv.device.controller;

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
import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.vo.SimRelVO;
import com.ai.apac.smartenv.device.wrapper.SimRelWrapper;
import com.ai.apac.smartenv.device.service.ISimRelService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-05-08
 */
@RestController
@AllArgsConstructor
@RequestMapping("/simRel")
@Api(value = "SIM卡关联设备信息", tags = "SIM卡关联设备信息")
public class SimRelController extends BladeController {

	private ISimRelService simRelService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入simRel")
	public R<SimRelVO> detail(SimRel simRel) {
		SimRel detail = simRelService.getOne(Condition.getQueryWrapper(simRel));
		return R.data(SimRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入simRel")
	public R<IPage<SimRelVO>> list(SimRel simRel, Query query) {
		IPage<SimRel> pages = simRelService.page(Condition.getPage(query), Condition.getQueryWrapper(simRel));
		return R.data(SimRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入simRel")
	public R<IPage<SimRelVO>> page(SimRelVO simRel, Query query) {
		IPage<SimRelVO> pages = simRelService.selectSimRelPage(Condition.getPage(query), simRel);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入simRel")
	public R save(@Valid @RequestBody SimRel simRel) {
		return R.status(simRelService.save(simRel));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入simRel")
	public R update(@Valid @RequestBody SimRel simRel) {
		return R.status(simRelService.updateById(simRel));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入simRel")
	public R submit(@Valid @RequestBody SimRel simRel) {
		return R.status(simRelService.saveOrUpdate(simRel));
	}



	
}
