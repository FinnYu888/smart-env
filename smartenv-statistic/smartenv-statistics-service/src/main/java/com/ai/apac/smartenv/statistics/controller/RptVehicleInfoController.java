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
package com.ai.apac.smartenv.statistics.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ai.apac.smartenv.statistics.service.IRptVehicleInfoService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 *  控制器
 *
 * @author Blade
 * @since 2020-09-09
 */
@RestController
@AllArgsConstructor
@RequestMapping("/rptvehicleinfo")
@Api(value = "", tags = "接口")
public class RptVehicleInfoController extends BladeController {

	private IRptVehicleInfoService rptVehicleInfoService;

	@PostMapping("/syncVehicleInfo")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "", notes = "")
	public R syncVehicleInfo(@RequestParam("date") String date) {
		Boolean syncVehicleInfo = rptVehicleInfoService.syncVehicleInfo(date);
		return R.status(true);
	}

	
}
