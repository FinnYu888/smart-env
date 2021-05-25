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

import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
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
import com.ai.apac.smartenv.device.entity.DeviceExt;
import com.ai.apac.smartenv.device.vo.DeviceExtVO;
import com.ai.apac.smartenv.device.wrapper.DeviceExtWrapper;
import com.ai.apac.smartenv.device.service.IDeviceExtService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 记录设备属性 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/deviceext")
@Api(value = "记录设备属性", tags = "记录设备属性接口")
public class DeviceExtController extends BladeController {

	private IDeviceExtService deviceExtService;

	private IEntityCategoryClient entityCategoryClient;


	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入deviceExt")
	public R<DeviceExtVO> detail(DeviceExt deviceExt) {
		DeviceExt detail = deviceExtService.getOne(Condition.getQueryWrapper(deviceExt));
		return R.data(DeviceExtWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 记录设备属性
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入deviceExt")
	public R<IPage<DeviceExtVO>> list(DeviceExt deviceExt, Query query) {
		IPage<DeviceExt> pages = deviceExtService.page(Condition.getPage(query), Condition.getQueryWrapper(deviceExt));
		return R.data(DeviceExtWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 记录设备属性
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入deviceExt")
	public R<IPage<DeviceExtVO>> page(DeviceExtVO deviceExt, Query query) {
		IPage<DeviceExtVO> pages = deviceExtService.selectDeviceExtPage(Condition.getPage(query), deviceExt);
		return R.data(pages);
	}

	/**
	 * 新增 记录设备属性
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入deviceExt")
	public R save(@Valid @RequestBody DeviceExt deviceExt) {
		return R.status(deviceExtService.save(deviceExt));
	}

	/**
	 * 修改 记录设备属性
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入deviceExt")
	public R update(@Valid @RequestBody DeviceExt deviceExt) {
		return R.status(deviceExtService.updateById(deviceExt));
	}

	/**
	 * 新增或修改 记录设备属性
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入deviceExt")
	public R submit(@Valid @RequestBody DeviceExt deviceExt) {
		return R.status(deviceExtService.saveOrUpdate(deviceExt));
	}

	
	/**
	 * 删除 记录设备属性
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deviceExtService.deleteLogic(Func.toLongList(ids)));
	}

	
}
