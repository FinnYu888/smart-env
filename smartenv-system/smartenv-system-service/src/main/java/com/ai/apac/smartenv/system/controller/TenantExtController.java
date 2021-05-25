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
package com.ai.apac.smartenv.system.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.system.entity.TenantExt;
import com.ai.apac.smartenv.system.vo.TenantExtVO;
import com.ai.apac.smartenv.system.wrapper.TenantExtWrapper;
import com.ai.apac.smartenv.system.service.ITenantExtService;
import org.springblade.core.boot.ctrl.BladeController;

import javax.validation.Valid;

/**
 * 租户扩展表 控制器
 *
 * @author Blade
 * @since 2020-07-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/tenantext")
@Api(value = "租户扩展表", tags = "租户扩展表接口")
public class TenantExtController extends BladeController {

	private ITenantExtService tenantExtService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入tenantExt")
	public R<TenantExtVO> detail(TenantExt tenantExt) {
		TenantExt detail = tenantExtService.getOne(Condition.getQueryWrapper(tenantExt));
		return R.data(TenantExtWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 租户扩展表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入tenantExt")
	public R<IPage<TenantExtVO>> list(TenantExt tenantExt, Query query) {
		IPage<TenantExt> pages = tenantExtService.page(Condition.getPage(query), Condition.getQueryWrapper(tenantExt));
		return R.data(TenantExtWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 租户扩展表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入tenantExt")
	public R<IPage<TenantExtVO>> page(TenantExtVO tenantExt, Query query) {
		IPage<TenantExtVO> pages = tenantExtService.selectTenantExtPage(Condition.getPage(query), tenantExt);
		return R.data(pages);
	}

	/**
	 * 新增 租户扩展表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入tenantExt")
	public R save(@Valid @RequestBody TenantExt tenantExt) {
		return R.status(tenantExtService.save(tenantExt));
	}

	/**
	 * 修改 租户扩展表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入tenantExt")
	public R update(@Valid @RequestBody TenantExt tenantExt) {
		return R.status(tenantExtService.updateById(tenantExt));
	}

	/**
	 * 新增或修改 租户扩展表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入tenantExt")
	public R submit(@Valid @RequestBody TenantExt tenantExt) {
		return R.status(tenantExtService.saveOrUpdate(tenantExt));
	}

	
	/**
	 * 删除 租户扩展表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@RequestParam String ids) {
		return R.status(tenantExtService.deleteLogic(Func.toLongList(ids)));
	}

	
}
