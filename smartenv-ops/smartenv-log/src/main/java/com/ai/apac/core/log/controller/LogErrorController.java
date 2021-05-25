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


import com.ai.apac.core.log.service.ILogErrorService;
import com.ai.apac.core.log.dto.LogErrorQueryDTO;
import com.ai.apac.core.log.vo.LogErrorVO;
import com.ai.apac.core.log.wrapper.LogErrorWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.model.LogError;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/errorLog")
@Api(value = "异常日志", tags = "异常日志")
public class LogErrorController extends BladeController {

	private ILogErrorService errorLogService;

	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询单条异常日志信息", notes = "查询单条异常日志信息")
	@GetMapping("/detail")
	public R<LogErrorVO> detail(LogError logError) {
		LogError entity = errorLogService.getOne(Condition.getQueryWrapper(logError));
		return R.data(LogErrorWrapper.build().entityVO(entity));
	}

	/**
	 * 查询多条(分页)
	 */
	@ApiOperationSupport(order = 2)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "serviceId", value = "服务ID", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "serverHost", value = "服务host", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "serverIp", value = "服务IP", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "createBy", value = "操作员帐号", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "exceptionName", value = "异常名称", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "env", value = "系统环境名", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "string")
	})
	@ApiOperation(value = "分页查询异常日志信息", notes = "分页查询异常日志信息")
	@GetMapping("/list")
	public R<IPage<LogErrorVO>> list(@ApiIgnore LogErrorQueryDTO logErrorQueryDTO, @ApiIgnore Query query) {
		Integer current = query.getCurrent();
		Integer size = query.getSize();
		logErrorQueryDTO.setCurrent(current == null ? 1 : current);
		logErrorQueryDTO.setSize(size == null ? 10 : size);
		IPage<LogError> pages = new Page<>(logErrorQueryDTO.getCurrent(), logErrorQueryDTO.getSize(), 0);
		Integer count = errorLogService.countLogErrorByCondition(logErrorQueryDTO);
		if (count != null && count > 0) {
			List<LogError> logErrors = errorLogService.listLogErrorByCondition(logErrorQueryDTO);
			pages.setRecords(logErrors);
			pages.setTotal(count.longValue());
		}
		return R.data(LogErrorWrapper.build().pageVO(pages));
	}

}
