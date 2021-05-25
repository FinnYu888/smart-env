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
package com.ai.apac.flow.engine.controller;

import com.ai.apac.flow.engine.entity.FlowExecution;
import com.ai.apac.flow.engine.service.FlowService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiOperation;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;

import org.springframework.web.bind.annotation.*;

/**
 * 流程状态控制器
 *
 * @author Chill
 */
@RestController
@RequestMapping("follow")
@AllArgsConstructor
@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
public class FlowFollowController {

	private FlowService flowService;

	/**
	 * 流程状态列表
	 */
	@GetMapping("list")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "分页", notes = "传入notice")
	public R<IPage<FlowExecution>> list(Query query, @ApiParam(value = "流程实例id") String processInstanceId, @ApiParam(value = "流程key") String processDefinitionKey) {
		IPage<FlowExecution> pages = flowService.selectFollowPage(Condition.getPage(query), processInstanceId, processDefinitionKey);
		return R.data(pages);
	}

	/**
	 * 删除流程实例
	 */
	@PostMapping("delete-process-instance")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "删除", notes = "传入主键集合")
	public R deleteProcessInstance(@ApiParam(value = "流程实例id") @RequestParam String processInstanceId, @ApiParam(value = "删除原因") @RequestParam String deleteReason) {
		boolean temp = flowService.deleteProcessInstance(processInstanceId, deleteReason);
		return R.status(temp);
	}

}
