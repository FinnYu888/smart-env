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
package com.ai.apac.smartenv.vehicle.controller;

import com.ai.apac.smartenv.vehicle.entity.VehicleWorkType;
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
import com.ai.apac.smartenv.vehicle.vo.VehicleWorkTypeVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleWorkTypeWrapper;
import com.ai.apac.smartenv.vehicle.service.IVehicleWorkTypeService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 车辆作业类型表 控制器
 *
 * @author Blade
 * @since 2021-01-12
 */
@RestController
@AllArgsConstructor
@RequestMapping("blade-vehicle_work_type/vehicleworktype")
@Api(value = "车辆作业类型表", tags = "车辆作业类型表接口")
public class VehicleWorkTypeController extends BladeController {

	private IVehicleWorkTypeService vehicleWorkTypeService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入vehicleWorkType")
	public R<VehicleWorkTypeVO> detail(VehicleWorkType vehicleWorkType) {
		VehicleWorkType detail = vehicleWorkTypeService.getOne(Condition.getQueryWrapper(vehicleWorkType));
		return R.data(VehicleWorkTypeWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 车辆作业类型表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入vehicleWorkType")
	public R<IPage<VehicleWorkTypeVO>> list(VehicleWorkType vehicleWorkType, Query query) {
		IPage<VehicleWorkType> pages = vehicleWorkTypeService.page(Condition.getPage(query), Condition.getQueryWrapper(vehicleWorkType));
		return R.data(VehicleWorkTypeWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 车辆作业类型表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入vehicleWorkType")
	public R<IPage<VehicleWorkTypeVO>> page(VehicleWorkTypeVO vehicleWorkType, Query query) {
		IPage<VehicleWorkTypeVO> pages = vehicleWorkTypeService.selectVehicleWorkTypePage(Condition.getPage(query), vehicleWorkType);
		return R.data(pages);
	}

	/**
	 * 新增 车辆作业类型表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入vehicleWorkType")
	public R save(@Valid @RequestBody VehicleWorkType vehicleWorkType) {
		return R.status(vehicleWorkTypeService.save(vehicleWorkType));
	}

	/**
	 * 修改 车辆作业类型表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入vehicleWorkType")
	public R update(@Valid @RequestBody VehicleWorkType vehicleWorkType) {
		return R.status(vehicleWorkTypeService.updateById(vehicleWorkType));
	}

	/**
	 * 新增或修改 车辆作业类型表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入vehicleWorkType")
	public R submit(@Valid @RequestBody VehicleWorkType vehicleWorkType) {
		return R.status(vehicleWorkTypeService.saveOrUpdate(vehicleWorkType));
	}


	/**
	 * 删除 车辆作业类型表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(vehicleWorkTypeService.deleteLogic(Func.toLongList(ids)));
	}


}
