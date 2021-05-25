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
package com.ai.apac.smartenv.workarea.controller;

import com.ai.apac.smartenv.address.util.CoordsTypeConvertUtil;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.vo.WorkareaInfoVO;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaInfoWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.vo.WorkareaNodeVO;
import com.ai.apac.smartenv.workarea.wrapper.WorkareaNodeWrapper;
import com.ai.apac.smartenv.workarea.service.IWorkareaNodeService;
import org.springblade.core.boot.ctrl.BladeController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作区域节点信息 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/workareanode")
@Api(value = "工作区域节点信息", tags = "工作区域节点信息接口")
public class WorkareaNodeController extends BladeController {

	private IWorkareaNodeService workareaNodeService;



	private CoordsTypeConvertUtil coordsTypeConvertUtil;


	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入workareaNode")
	@ApiLog("查详情")
	public R<WorkareaNodeVO> detail(WorkareaNode workareaNode) {
		WorkareaNode detail = workareaNodeService.getOne(Condition.getQueryWrapper(workareaNode));
		return R.data(WorkareaNodeWrapper.build().entityVO(detail));
	}

	/**
	 * 工作区域节点信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入workareaNode")
	@ApiLog("分页查询工作区域节点信息")
	public R<List<WorkareaNodeVO>> list(WorkareaNode workareaNode, BladeUser user) throws IOException {
		String tenantId = user.getTenantId();
		return R.data(workareaNodeService.queryWorkareaNodeVOListByParam(workareaNode,tenantId));
	}


	/**
	 * 自定义分页 工作区域节点信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入workareaNode")
	@ApiLog("默认分页查询")
	public R<IPage<WorkareaNodeVO>> page(WorkareaNodeVO workareaNode, Query query) {
		IPage<WorkareaNodeVO> pages = workareaNodeService.selectWorkareaNodePage(Condition.getPage(query), workareaNode);
		coordsTypeConvertUtil.toWebConvert(pages.getRecords());
		return R.data(pages);
	}

	/**
	 * 新增 工作区域节点信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入workareaNode")
	@ApiLog("默认保存方法")
	public R save(@Valid @RequestBody WorkareaNode workareaNode) {
		return R.status(workareaNodeService.save(workareaNode));
	}

	/**
	 * 修改 工作区域节点信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入workareaNode")
	@ApiLog("默认更新方法")
	public R update(@Valid @RequestBody WorkareaNode workareaNode) {
		return R.status(workareaNodeService.updateById(workareaNode));
	}

	/**
	 * 新增或修改 工作区域节点信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入workareaNode")
	@ApiLog("默认submit方法")
	public R submit(@Valid @RequestBody WorkareaNode workareaNode) {
		return R.status(workareaNodeService.saveOrUpdate(workareaNode));
	}

	
	/**
	 * 删除 工作区域节点信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog("默认删除方法")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(workareaNodeService.deleteLogic(Func.toLongList(ids)));
	}

	
}
