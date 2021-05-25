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
package com.ai.apac.smartenv.facility.controller;

import com.ai.apac.smartenv.facility.entity.FacilityExt;
import com.ai.apac.smartenv.facility.service.IFacilityExtService;
import com.ai.apac.smartenv.facility.vo.FacilityExtVO;
import com.ai.apac.smartenv.facility.wrapper.FacilityExtWrapper;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/facilityext")
@Api(value = "设施扩展信息接口", tags = "设施扩展信息接口")
public class FacilityExtController extends BladeController {

	private IFacilityExtService facilityExtService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入facilityExt")
	public R<FacilityExtVO> detail(FacilityExt facilityExt) {
		FacilityExt detail = facilityExtService.getOne(Condition.getQueryWrapper(facilityExt));
		return R.data(FacilityExtWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入facilityExt")
	public R<IPage<FacilityExtVO>> list(FacilityExt facilityExt, Query query) {
		IPage<FacilityExt> pages = facilityExtService.page(Condition.getPage(query), Condition.getQueryWrapper(facilityExt));
		return R.data(FacilityExtWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入facilityExt")
	public R<IPage<FacilityExtVO>> page(FacilityExtVO facilityExt, Query query) {
		IPage<FacilityExtVO> pages = facilityExtService.selectFacilityExtPage(Condition.getPage(query), facilityExt);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入facilityExt")
	public R save(@Valid @RequestBody FacilityExt facilityExt) {
		return R.status(facilityExtService.save(facilityExt));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入facilityExt")
	public R update(@Valid @RequestBody FacilityExt facilityExt) {
		return R.status(facilityExtService.updateById(facilityExt));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入facilityExt")
	public R submit(@Valid @RequestBody FacilityExt facilityExt) {
		return R.status(facilityExtService.saveOrUpdate(facilityExt));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(facilityExtService.deleteLogic(Func.toLongList(ids)));
	}

	
}
