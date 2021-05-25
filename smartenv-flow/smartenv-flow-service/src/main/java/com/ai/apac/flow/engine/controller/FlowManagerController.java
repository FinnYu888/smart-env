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

import com.ai.apac.flow.engine.constant.FlowEngineConstant;
import com.ai.apac.flow.engine.entity.FlowProcess;
import com.ai.apac.flow.engine.service.FlowService;

import com.ai.apac.smartenv.inventory.entity.ResOrder;
import com.ai.apac.smartenv.inventory.feign.IResOrderClient;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.support.Kv;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 流程管理接口
 *
 * @author Chill
 */
@RestController
@RequestMapping("manager")
@AllArgsConstructor
@Api(value = "流程管理接口", tags = "流程管理接口")
@PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
public class FlowManagerController {
	private IResOrderClient resOrderClient;
	private FlowService flowService;


	/**
	 * 分页
	 */
	@GetMapping("list")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "分页", notes = "传入流程类型")
	public R<IPage<FlowProcess>> list(@ApiParam("流程类型") String category, Query query) {
		IPage<FlowProcess> pages = flowService.selectProcessPage(Condition.getPage(query), category);
		return R.data(pages);
	}

	/**
	 * 变更流程状态
	 *
	 * @param state     状态
	 * @param processId 流程id
	 */
	@PostMapping("change-state")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "变更流程状态", notes = "传入state,processId")
	public R changeState(@RequestParam String state, @RequestParam String processId) {
		String msg = flowService.changeState(state, processId);
		return R.success(msg);
	}

	/**
	 * 删除部署流程
	 *
	 * @param deploymentIds 部署流程id集合
	 */
	@PostMapping("delete-deployment")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "删除部署流程", notes = "部署流程id集合")
	public R deleteDeployment(String deploymentIds) {
		return R.status(flowService.deleteDeployment(deploymentIds));
	}

	/**
	 * 检查流程文件格式
	 *
	 * @param file    流程文件
	 */
	@PostMapping("check-upload")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "上传部署流程文件", notes = "传入文件")
	public R checkUpload(@RequestParam MultipartFile file) {
		boolean temp = Objects.requireNonNull(file.getOriginalFilename()).endsWith(FlowEngineConstant.SUFFIX);
		return R.data(Kv.create().set("name", file.getOriginalFilename()).set("success", temp));
	}

	/**
	 * 上传部署流程文件
	 *
	 * @param files    流程文件
	 * @param category 类型
	 */
	@PostMapping("deploy-upload")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "上传部署流程文件", notes = "传入文件")
	public R deployUpload(@RequestParam List<MultipartFile> files, @RequestParam String category) {
		
		return R.status(flowService.deployUpload(files, category));
	}

}
