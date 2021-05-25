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

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.ai.apac.smartenv.device.vo.DeviceRelVO;
import com.ai.apac.smartenv.device.wrapper.DeviceRelWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/devicerel")
@Api(value = "终端绑定相关接口", tags = "终端绑定相关接口")
public class DeviceRelController extends BladeController {

	private IDeviceRelService deviceRelService;
	private IDeviceInfoService deviceInfoService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入deviceRel")
	public R<DeviceRelVO> detail(DeviceRel deviceRel) {
		DeviceRel detail = deviceRelService.getOne(Condition.getQueryWrapper(deviceRel));
		return R.data(DeviceRelWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入deviceRel")
	public R<IPage<DeviceRelVO>> list(DeviceRel deviceRel, Query query) {
		IPage<DeviceRel> pages = deviceRelService.page(Condition.getPage(query), Condition.getQueryWrapper(deviceRel));
		return R.data(DeviceRelWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入deviceRel")
	public R<IPage<DeviceRelVO>> page(DeviceRelVO deviceRel, Query query) {
		IPage<DeviceRelVO> pages = deviceRelService.selectDeviceRelPage(Condition.getPage(query), deviceRel);
		return R.data(pages);
	}

	/**
	 * 新增 
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入deviceRel")
	public R save(@Valid @RequestBody DeviceRel deviceRel) {
		return R.status(deviceRelService.save(deviceRel));
	}

	/**
	 * 修改 
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入deviceRel")
	public R update(@Valid @RequestBody DeviceRel deviceRel) {
		return R.status(deviceRelService.updateById(deviceRel));
	}

	/**
	 * 新增或修改 
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入deviceRel")
	public R submit(@Valid @RequestBody DeviceRel deviceRel) {
		return R.status(deviceRelService.saveOrUpdate(deviceRel));
	}

	
	/**
	 * 删除 
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(deviceRelService.deleteLogic(Func.toLongList(ids)));
	}

	/**
	 * 车辆绑定终端
	 */
	@PostMapping("bindDevice")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "车辆绑定终端", notes = "")
	public R bindDevice(@RequestParam Long vehicleId, String deviceIds) {
		
		/*List<Long> deviceIdList = Func.toLongList(deviceIds);
		deviceIdList.forEach(deviceId -> {
			DeviceInfo deviceInfo = deviceInfoService.getById(deviceId);
			if (deviceInfo == null) {
				return;
			}
			DeviceRel deviceRel = new DeviceRel();
			deviceRel.setDeviceId(deviceId);
			deviceRel.setEntityId(vehicleId);
			deviceRel.setEntityType(VehicleConstant.DEVICE_REL_TYPE_VEHICLE);
			List<DeviceRel> list = deviceRelService.list(Condition.getQueryWrapper(deviceRel));
			if (list != null && list.size() > 0) {
				return;
			}
			deviceRelService.save(deviceRel);
		});*/
		// 翟道鑫注释，爱总看到了看下代码是否正确。 entityType类型有待统一  
		return R.status(deviceInfoService.bindDevice(VehicleConstant.DEVICE_REL_TYPE_VEHICLE, vehicleId, deviceIds));
	}

	/**
	 * 通用绑定终端接口
	 */
	@PostMapping("/bind")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "通用绑定终端接口", notes = "")
	@ApiLog(value = "通用绑定终端接口")
	public R deviceBind(@RequestParam String entityType, @RequestParam Long entityId, @RequestParam String deviceIds) {
		//1.绑定
		boolean b = deviceInfoService.bindDevice(entityType, entityId, deviceIds);
		return R.status(b);
	}

    /**
     * 通用解绑终端接口
     */
    @PostMapping("/unbind")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "通用解绑终端接口", notes = "")
	@ApiLog(value = "通用解绑终端接口")
    public R DeleteDeviceBind(@RequestParam String entityType, @RequestParam Long entityId, @RequestParam String deviceIds) {
		//1.解绑
		boolean b = deviceInfoService.deleteBindDevice(entityType, entityId, deviceIds);
        return R.status(b);
    }

	@PostMapping("/syncDeviceRel")
	@ApiOperationSupport(order = 10)
	@ApiOperation(value = "全量更新车辆绑定设备信息给大数据", notes = "")
	@ApiLog(value = "全量更新车辆绑定设备信息给大数据")
	public R updateAllDeviceRel() {
		//1.解绑
		boolean b = deviceInfoService.updateAllDeviceRel();
		return R.status(b);
	}

}
