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


import com.ai.apac.core.log.service.ILogApiService;
import com.ai.apac.core.log.dto.LogApiQueryDTO;
import com.ai.apac.core.log.vo.LogApiVO;
import com.ai.apac.core.log.wrapper.LogApiWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.model.LogApi;
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
@RequestMapping("/apiLog")
@Api(value = "API日志", tags = "API日志")
public class LogApiController extends BladeController {

	private ILogApiService logService;

	/**
	 * 查询单条
	 */
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "查询单条API日志信息", notes = "查询单条API日志信息")
	@GetMapping("/detail")
	public R<LogApiVO> detail(LogApi log) {
		LogApi logApi = logService.getOne(Condition.getQueryWrapper(log));
		return R.data(LogApiWrapper.build().entityVO(logApi));
	}

	/**
	 * 查询多条(分页)
	 */
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页查询API日志信息", notes = "分页查询API日志信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "serviceId", value = "服务ID", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "serverHost", value = "服务host", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "serverIp", value = "服务IP", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "createBy", value = "操作员帐号", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "title", value = "日志名", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query", dataType = "string")
	})
	@GetMapping("/list")
	public R<IPage<LogApiVO>> list(@ApiIgnore LogApiQueryDTO logApiQueryDTO, @ApiIgnore Query query) {
		Integer current = query.getCurrent();
		Integer size = query.getSize();
		logApiQueryDTO.setCurrent(current == null ? 1 : current);
		logApiQueryDTO.setSize(size == null ? 10 : size);
		IPage<LogApi> pages = new Page<>(logApiQueryDTO.getCurrent(), logApiQueryDTO.getSize(), 0);
		Integer count = logService.countLogApiByCondition(logApiQueryDTO);
		if (count != null && count > 0) {
			List<LogApi> logErrors = logService.listLogApiByCondition(logApiQueryDTO);
			pages.setRecords(logErrors);
			pages.setTotal(count.longValue());
		}
		return R.data(LogApiWrapper.build().pageVO(pages));
	}

}
