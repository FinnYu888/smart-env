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
package com.ai.apac.smartenv.alarm.controller;

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
import com.ai.apac.smartenv.alarm.entity.MinicreateAttach;
import com.ai.apac.smartenv.alarm.vo.MinicreateAttachVO;
import com.ai.apac.smartenv.alarm.wrapper.MinicreateAttachWrapper;
import com.ai.apac.smartenv.alarm.service.IMinicreateAttachService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 点创主动告警附件表 控制器
 *
 * @author Blade
 * @since 2020-09-10
 */
@RestController
@AllArgsConstructor
@RequestMapping("/minicreateattach")
@Api(value = "点创主动告警附件表", tags = "点创主动告警附件表接口")
public class MinicreateAttachController extends BladeController {

	private IMinicreateAttachService minicreateAttachService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入minicreateAttach")
	public R<MinicreateAttachVO> detail(MinicreateAttach minicreateAttach) {
		MinicreateAttach detail = minicreateAttachService.getOne(Condition.getQueryWrapper(minicreateAttach));
		return R.data(MinicreateAttachWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 点创主动告警附件表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入minicreateAttach")
	public R<IPage<MinicreateAttachVO>> list(MinicreateAttach minicreateAttach, Query query) {
		IPage<MinicreateAttach> pages = minicreateAttachService.page(Condition.getPage(query), Condition.getQueryWrapper(minicreateAttach));
		return R.data(MinicreateAttachWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 点创主动告警附件表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入minicreateAttach")
	public R<IPage<MinicreateAttachVO>> page(MinicreateAttachVO minicreateAttach, Query query) {
		IPage<MinicreateAttachVO> pages = minicreateAttachService.selectMinicreateAttachPage(Condition.getPage(query), minicreateAttach);
		return R.data(pages);
	}

	/**
	 * 新增 点创主动告警附件表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入minicreateAttach")
	public R save(@Valid @RequestBody MinicreateAttach minicreateAttach) {
		return R.status(minicreateAttachService.save(minicreateAttach));
	}

	/**
	 * 修改 点创主动告警附件表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入minicreateAttach")
	public R update(@Valid @RequestBody MinicreateAttach minicreateAttach) {
		return R.status(minicreateAttachService.updateById(minicreateAttach));
	}

	/**
	 * 新增或修改 点创主动告警附件表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入minicreateAttach")
	public R submit(@Valid @RequestBody MinicreateAttach minicreateAttach) {
		return R.status(minicreateAttachService.saveOrUpdate(minicreateAttach));
	}

	
	/**
	 * 删除 点创主动告警附件表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(minicreateAttachService.deleteLogic(Func.toLongList(ids)));
	}

	
}
