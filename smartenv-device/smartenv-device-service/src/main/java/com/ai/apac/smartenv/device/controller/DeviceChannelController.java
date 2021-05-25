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

import com.ai.apac.smartenv.device.entity.DeviceChannel;
import com.ai.apac.smartenv.device.service.IDeviceChannelService;
import com.ai.apac.smartenv.device.vo.DeviceChannelVO;
import com.ai.apac.smartenv.device.wrapper.DeviceChannelWrapper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 录像设备通道信息 控制器
 *
 * @author Blade
 * @since 2020-02-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/devicechannel")
@Api(value = "录像设备通道信息", tags = "录像设备通道信息接口")
public class DeviceChannelController extends BladeController {

	private IDeviceChannelService deviceChannelService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入deviceChannel")
	public R<DeviceChannelVO> detail(DeviceChannel deviceChannel) {
		DeviceChannel detail = deviceChannelService.getOne(Condition.getQueryWrapper(deviceChannel));
		return R.data(DeviceChannelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 录像设备通道信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入deviceChannel")
	public R<IPage<DeviceChannelVO>> list(DeviceChannel deviceChannel, Query query) {
		IPage<DeviceChannel> pages = deviceChannelService.page(Condition.getPage(query), Condition.getQueryWrapper(deviceChannel));
		return R.data(DeviceChannelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 录像设备通道信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入deviceChannel")
	public R<IPage<DeviceChannelVO>> page(DeviceChannelVO deviceChannel, Query query) {
		IPage<DeviceChannelVO> pages = deviceChannelService.selectDeviceChannelPage(Condition.getPage(query), deviceChannel);
		return R.data(pages);
	}

	/**
	 * 新增 录像设备通道信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入deviceChannel")
	public R save(@Valid @RequestBody DeviceChannel deviceChannel) {
		return R.status(deviceChannelService.save(deviceChannel));
	}

	/**
	 * 修改 录像设备通道信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入deviceChannel")
	public R update(@Valid @RequestBody DeviceChannel deviceChannel) {
		return R.status(deviceChannelService.updateById(deviceChannel));
	}

	/**
	 * 新增或修改 录像设备通道信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入deviceChannel")
	public R submit(@Valid @RequestBody DeviceChannel deviceChannel) {
		return R.status(deviceChannelService.saveOrUpdate(deviceChannel));
	}

	
	/**
	 * 删除 录像设备通道信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deviceChannelService.deleteLogic(Func.toLongList(ids)));
	}

	
}
