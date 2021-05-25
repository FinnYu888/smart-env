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

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.device.entity.DeviceContact;
import com.ai.apac.smartenv.device.vo.DeviceContactVO;
import com.ai.apac.smartenv.device.wrapper.DeviceContactWrapper;
import com.ai.apac.smartenv.device.service.IDeviceContactService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 人员紧急联系人信息表 控制器
 *
 * @author Blade
 * @since 2020-02-26
 */
@RestController
@AllArgsConstructor
@RequestMapping("/devicecontroller")
@Api(value = "人员紧急联系人信息表", tags = "人员紧急联系人信息表接口")
public class DeviceContactController extends BladeController {

	private IDeviceContactService deviceContactService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入deviceContact")
	public R<DeviceContactVO> detail(DeviceContact deviceContact) {
		DeviceContact detail = deviceContactService.getOne(Condition.getQueryWrapper(deviceContact));
		return R.data(DeviceContactWrapper.build().entityVO(detail));
	}

	/**
	 * 新增或修改 人员紧急联系人信息表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入deviceContact")
	public R submit(@Valid @RequestBody DeviceContact deviceContact) {
		return R.status(deviceContactService.submitContactInfo(deviceContact));
	}

	
	/**
	 * 删除 人员紧急联系人信息表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R removeDeviceContact(@ApiParam(value = "设备ID", required = true) @RequestParam Long deviceId,@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deviceContactService.removeDeviceContact(deviceId,ids));
	}


	/**
	 * 紧急联系人优先级转换信息表
	 */
	@PutMapping("/transfer")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "优先级转换", notes = "传入ids")
	public R transferDeviceContact(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deviceContactService.transferDeviceContact(ids));
	}

	/**
	 * 删除 人员紧急联系人信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "查询设备紧急联系人列表", notes = "传入设备ID")
	public R<List<DeviceContactVO>> list(@ApiParam(value = "设备ID", required = true) @RequestParam Long id) {
		List<DeviceContactVO> deviceContactVOList = new ArrayList<DeviceContactVO>();
		DeviceContact req = new DeviceContact();
		req.setDeviceId(id);
		List<DeviceContact> deviceContactList = deviceContactService.listDeviceContact(req);
		if(deviceContactList.size() > 0){
			deviceContactList.forEach(deviceContact1 -> {
				deviceContactVOList.add(Objects.requireNonNull(BeanUtil.copy(deviceContact1, DeviceContactVO.class)));
			});
		}

		return R.data(deviceContactVOList);
	}

	
}
