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
package com.ai.apac.core.log.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.model.LogUsual;
import com.ai.apac.core.log.service.ILogUsualService;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/usualLog")
@Api(value = "通用日志", tags = "通用日志")
public class LogUsualController extends BladeController {

	private ILogUsualService logService;

	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询单条通用日志信息", notes = "查询单条通用日志信息")
	@GetMapping("/detail")
	public R<LogUsual> detail(LogUsual log) {
		return R.data(logService.getOne(Condition.getQueryWrapper(log)));
	}

	/**
	 * 查询多条(分页)
	 */
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页查询通用日志信息", notes = "分页查询通用日志信息")
	@GetMapping("/list")
	public R<IPage<LogUsual>> list(@RequestParam Map<String, Object> log, Query query) {
		IPage<LogUsual> pages = logService.page(Condition.getPage(query), Condition.getQueryWrapper(log, LogUsual.class));
		return R.data(pages);
	}

}
