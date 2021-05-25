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

import com.ai.apac.smartenv.system.entity.CharSpec;
import com.ai.apac.smartenv.system.service.ICharSpecService;
import com.ai.apac.smartenv.system.vo.CharSpecVO;
import com.ai.apac.smartenv.system.wrapper.CharSpecWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 扩展属性表 控制器
 *
 * @author Blade
 * @since 2020-02-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/charspec")
@Api(value = "扩展属性表", tags = "扩展属性表接口")
public class CharSpecController extends BladeController {

	private ICharSpecService charSpecService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入charSpec")
	public R<CharSpecVO> detail(CharSpec charSpec) {
		CharSpec detail = charSpecService.getOne(Condition.getQueryWrapper(charSpec));
		return R.data(CharSpecWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 扩展属性表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入charSpec")
	public R<IPage<CharSpecVO>> list(CharSpec charSpec, Query query) {
            IPage<CharSpec> pages = charSpecService.page(Condition.getPage(query), Condition.getQueryWrapper(charSpec));
		return R.data(CharSpecWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 扩展属性表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入charSpec")
	public R<IPage<CharSpecVO>> page(CharSpecVO charSpec, Query query) {
		IPage<CharSpecVO> pages = charSpecService.selectCharSpecPage(Condition.getPage(query), charSpec);
		return R.data(pages);
	}

	/**
	 * 新增 扩展属性表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入charSpec")
	public R save(@Valid @RequestBody CharSpec charSpec) {
		return R.status(charSpecService.save(charSpec));
	}

	/**
	 * 修改 扩展属性表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入charSpec")
	public R update(@Valid @RequestBody CharSpec charSpec) {
		return R.status(charSpecService.updateById(charSpec));
	}

	/**
	 * 新增或修改 扩展属性表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入charSpec")
	public R submit(@Valid @RequestBody CharSpec charSpec) {
		return R.status(charSpecService.saveOrUpdate(charSpec));
	}

	
	/**
	 * 删除 扩展属性表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(charSpecService.deleteLogic(Func.toLongList(ids)));
	}

	
}
