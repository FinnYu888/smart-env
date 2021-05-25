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
import com.ai.apac.smartenv.system.entity.CommonParam;
import com.ai.apac.smartenv.system.vo.CommonParamVO;
import com.ai.apac.smartenv.system.wrapper.CommonParamWrapper;
import com.ai.apac.smartenv.system.service.ICommonParamService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 公共参数表 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/commonparam")
@Api(value = "公共参数表", tags = "公共参数表接口")
public class CommonParamController extends BladeController {

	private ICommonParamService commonParamService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入commonParam")
	public R<CommonParamVO> detail(CommonParam commonParam) {
		CommonParam detail = commonParamService.getOne(Condition.getQueryWrapper(commonParam));
		return R.data(CommonParamWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 公共参数表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入commonParam")
	public R<IPage<CommonParamVO>> list(CommonParam commonParam, Query query) {
		IPage<CommonParam> pages = commonParamService.page(Condition.getPage(query), Condition.getQueryWrapper(commonParam));
		return R.data(CommonParamWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 公共参数表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入commonParam")
	public R<IPage<CommonParamVO>> page(CommonParamVO commonParam, Query query) {
		IPage<CommonParamVO> pages = commonParamService.selectCommonParamPage(Condition.getPage(query), commonParam);
		return R.data(pages);
	}

	/**
	 * 新增 公共参数表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入commonParam")
	public R save(@Valid @RequestBody CommonParam commonParam) {
		return R.status(commonParamService.save(commonParam));
	}

	/**
	 * 修改 公共参数表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入commonParam")
	public R update(@Valid @RequestBody CommonParam commonParam) {
		return R.status(commonParamService.updateById(commonParam));
	}

	/**
	 * 新增或修改 公共参数表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入commonParam")
	public R submit(@Valid @RequestBody CommonParam commonParam) {
		return R.status(commonParamService.saveOrUpdate(commonParam));
	}

	
	/**
	 * 删除 公共参数表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(commonParamService.deleteLogic(Func.toLongList(ids)));
	}

	
}
