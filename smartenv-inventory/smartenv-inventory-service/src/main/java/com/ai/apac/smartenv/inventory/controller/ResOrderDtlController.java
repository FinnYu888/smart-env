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
import com.ai.apac.smartenv.inventory.entity.ResOrderDtl;
import com.ai.apac.smartenv.inventory.vo.ResOrderDtlVO;
import com.ai.apac.smartenv.inventory.wrapper.ResOrderDtlWrapper;
import com.ai.apac.smartenv.inventory.service.IResOrderDtlService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-02-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-res_order_dtl/resorderdtl")
@Api(value = "", tags = "接口")
public class ResOrderDtlController extends BladeController {

	private IResOrderDtlService resOrderDtlService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入resOrderDtl")
	public R<ResOrderDtlVO> detail(ResOrderDtl resOrderDtl) {
		ResOrderDtl detail = resOrderDtlService.getOne(Condition.getQueryWrapper(resOrderDtl));
		return R.data(ResOrderDtlWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入resOrderDtl")
	public R<IPage<ResOrderDtlVO>> list(ResOrderDtl resOrderDtl, Query query) {
		IPage<ResOrderDtl> pages = resOrderDtlService.page(Condition.getPage(query), Condition.getQueryWrapper(resOrderDtl));
		return R.data(ResOrderDtlWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入resOrderDtl")
	public R<IPage<ResOrderDtlVO>> page(ResOrderDtlVO resOrderDtl, Query query) {
		IPage<ResOrderDtlVO> pages = resOrderDtlService.selectResOrderDtlPage(Condition.getPage(query), resOrderDtl);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入resOrderDtl")
	public R save(@Valid @RequestBody ResOrderDtl resOrderDtl) {
		return R.status(resOrderDtlService.save(resOrderDtl));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入resOrderDtl")
	public R update(@Valid @RequestBody ResOrderDtl resOrderDtl) {
		return R.status(resOrderDtlService.updateById(resOrderDtl));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入resOrderDtl")
	public R submit(@Valid @RequestBody ResOrderDtl resOrderDtl) {
		return R.status(resOrderDtlService.saveOrUpdate(resOrderDtl));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(resOrderDtlService.deleteLogic(Func.toLongList(ids)));
	}

	
}
