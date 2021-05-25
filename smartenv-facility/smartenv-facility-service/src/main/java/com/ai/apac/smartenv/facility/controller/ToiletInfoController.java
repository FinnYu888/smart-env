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
import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import com.ai.apac.smartenv.facility.wrapper.ToiletInfoWrapper;
import com.ai.apac.smartenv.facility.service.IToiletInfoService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-09-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/toiletinfo")
@Api(value = "公厕信息详情", tags = "公厕信息详情")
public class ToiletInfoController extends BladeController {

	private IToiletInfoService toiletInfoService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "公厕详情", notes = "传入toiletInfo")
	public R<ToiletInfoVO> detail(ToiletInfo toiletInfo) {
		return R.data(toiletInfoService.getToiletDetailsById(toiletInfo.getId()));
	}

	@GetMapping("/view")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "公厕基本信息", notes = "传入toiletInfo")
	public R<ToiletInfoVO> view(ToiletInfo toiletInfo) {
		return R.data(toiletInfoService.getToiletViewById(toiletInfo.getId()));
	}


	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入toiletInfo")
	public R<IPage<ToiletInfoVO>> list(ToiletInfo toiletInfo, Query query) {
		IPage<ToiletInfo> pages = toiletInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(toiletInfo));
		return R.data(ToiletInfoWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入toiletInfo")
	public R<IPage<ToiletInfoVO>> page(ToiletInfoVO toiletInfo, Query query) {
		IPage<ToiletInfoVO> pages = toiletInfoService.selectToiletInfoPage(Condition.getPage(query), toiletInfo);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入toiletInfo")
	public R save(@Valid @RequestBody ToiletInfo toiletInfo) {
		return R.status(toiletInfoService.save(toiletInfo));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入toiletInfo")
	public R update(@Valid @RequestBody ToiletInfo toiletInfo) {
		return R.status(toiletInfoService.updateById(toiletInfo));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入toiletInfo")
	public R submit(@Valid @RequestBody ToiletInfo toiletInfo) {
		return R.status(toiletInfoService.saveOrUpdate(toiletInfo));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(toiletInfoService.deleteLogic(Func.toLongList(ids)));
	}

	
}
