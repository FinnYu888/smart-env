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
package com.ai.apac.smartenv.person.controller;

import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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
import com.ai.apac.smartenv.person.entity.PersonAttendInfo;
import com.ai.apac.smartenv.person.vo.PersonAttendInfoVO;
import com.ai.apac.smartenv.person.wrapper.PersonAttendInfoWrapper;
import com.ai.apac.smartenv.person.service.IPersonAttendInfoService;
import org.springblade.core.boot.ctrl.BladeController;

/**
 * 人员考勤信息表 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/personattendinfo")
@Api(value = "人员考勤信息表", tags = "人员考勤信息表接口")
public class PersonAttendInfoController extends BladeController {

	private IPersonAttendInfoService personAttendInfoService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入personAttendInfo")
	public R<PersonAttendInfoVO> detail(PersonAttendInfo personAttendInfo) {
		PersonAttendInfo detail = personAttendInfoService.getOne(Condition.getQueryWrapper(personAttendInfo));
		return R.data(PersonAttendInfoWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 人员考勤信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入personAttendInfo")
	public R<IPage<PersonAttendInfoVO>> list(PersonAttendInfo personAttendInfo, Query query) {
		IPage<PersonAttendInfo> pages = personAttendInfoService.page(Condition.getPage(query), Condition.getQueryWrapper(personAttendInfo));
		return R.data(PersonAttendInfoWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 人员考勤信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入personAttendInfo")
	public R<IPage<PersonAttendInfoVO>> page(PersonAttendInfoVO personAttendInfo, Query query) {
		IPage<PersonAttendInfoVO> pages = personAttendInfoService.selectPersonAttendInfoPage(Condition.getPage(query), personAttendInfo);
		return R.data(pages);
	}

	/**
	 * 新增 人员考勤信息表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入personAttendInfo")
	public R save(@Valid @RequestBody PersonAttendInfo personAttendInfo) {
		return R.status(personAttendInfoService.save(personAttendInfo));
	}

	/**
	 * 修改 人员考勤信息表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入personAttendInfo")
	public R update(@Valid @RequestBody PersonAttendInfo personAttendInfo) {
		return R.status(personAttendInfoService.updateById(personAttendInfo));
	}

	/**
	 * 新增或修改 人员考勤信息表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入personAttendInfo")
	public R submit(@Valid @RequestBody PersonAttendInfo personAttendInfo) {
		return R.status(personAttendInfoService.saveOrUpdate(personAttendInfo));
	}

	
	/**
	 * 删除 人员考勤信息表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(personAttendInfoService.deleteLogic(Func.toLongList(ids)));
	}

	
}
