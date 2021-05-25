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
import com.ai.apac.smartenv.facility.entity.ToiletQuota;
import com.ai.apac.smartenv.facility.vo.ToiletQuotaVO;
import com.ai.apac.smartenv.facility.wrapper.ToiletQuotaWrapper;
import com.ai.apac.smartenv.facility.service.IToiletQuotaService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-09-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/toiletquota")
@Api(value = "公厕配额详情", tags = "公厕配额详情")
public class ToiletQuotaController extends BladeController {

	private IToiletQuotaService toiletQuotaService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入toiletQuota")
	public R<ToiletQuotaVO> detail(ToiletQuota toiletQuota) {
		ToiletQuota detail = toiletQuotaService.getOne(Condition.getQueryWrapper(toiletQuota));
		return R.data(ToiletQuotaWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入toiletQuota")
	public R<IPage<ToiletQuotaVO>> list(ToiletQuota toiletQuota, Query query) {
		IPage<ToiletQuota> pages = toiletQuotaService.page(Condition.getPage(query), Condition.getQueryWrapper(toiletQuota));
		return R.data(ToiletQuotaWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入toiletQuota")
	public R<IPage<ToiletQuotaVO>> page(ToiletQuotaVO toiletQuota, Query query) {
		IPage<ToiletQuotaVO> pages = toiletQuotaService.selectToiletQuotaPage(Condition.getPage(query), toiletQuota);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入toiletQuota")
	public R save(@Valid @RequestBody ToiletQuota toiletQuota) {
		return R.status(toiletQuotaService.save(toiletQuota));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入toiletQuota")
	public R update(@Valid @RequestBody ToiletQuota toiletQuota) {
		return R.status(toiletQuotaService.updateById(toiletQuota));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入toiletQuota")
	public R submit(@Valid @RequestBody ToiletQuota toiletQuota) {
		return R.status(toiletQuotaService.saveOrUpdate(toiletQuota));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(toiletQuotaService.deleteLogic(Func.toLongList(ids)));
	}

	
}
