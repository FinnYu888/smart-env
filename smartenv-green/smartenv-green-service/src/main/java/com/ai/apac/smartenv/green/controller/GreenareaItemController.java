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
package com.ai.apac.smartenv.green.controller;

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
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ai.apac.smartenv.green.entity.GreenareaItem;
import com.ai.apac.smartenv.green.vo.GreenareaItemVO;
import com.ai.apac.smartenv.green.wrapper.GreenareaItemWrapper;
import com.ai.apac.smartenv.green.service.IGreenareaItemService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 绿化养护项信息 控制器
 *
 * @author Blade
 * @since 2020-07-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/greenareaitem")
@Api(value = "绿化养护项信息", tags = "绿化养护项信息接口")
public class GreenareaItemController extends BladeController {

	private IGreenareaItemService greenareaItemService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入greenareaItem")
	public R<GreenareaItemVO> detail(GreenareaItem greenareaItem) {
		GreenareaItem detail = greenareaItemService.getOne(Condition.getQueryWrapper(greenareaItem));
		return R.data(GreenareaItemWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 绿化养护项信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入greenareaItem")
	public R<IPage<GreenareaItemVO>> list(GreenareaItem greenareaItem, Query query) {
		IPage<GreenareaItem> pages = greenareaItemService.page(Condition.getPage(query), Condition.getQueryWrapper(greenareaItem));
		return R.data(GreenareaItemWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 绿化养护项信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入greenareaItem")
	public R<IPage<GreenareaItemVO>> page(GreenareaItemVO greenareaItem, Query query) {
		IPage<GreenareaItemVO> pages = greenareaItemService.selectGreenareaItemPage(Condition.getPage(query), greenareaItem);
		return R.data(pages);
	}

	/**
	 * 新增 绿化养护项信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入greenareaItem")
	public R save(@Valid @RequestBody GreenareaItem greenareaItem) {
		return R.status(greenareaItemService.save(greenareaItem));
	}

	/**
	 * 修改 绿化养护项信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入greenareaItem")
	public R update(@Valid @RequestBody GreenareaItem greenareaItem) {
		return R.status(greenareaItemService.updateById(greenareaItem));
	}

	/**
	 * 新增或修改 绿化养护项信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入greenareaItem")
	public R submit(@Valid @RequestBody GreenareaItem greenareaItem) {
		return R.status(greenareaItemService.saveOrUpdate(greenareaItem));
	}

	
	/**
	 * 删除 绿化养护项信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(greenareaItemService.deleteLogic(Func.toLongList(ids)));
	}

	
}
