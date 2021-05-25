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
import com.ai.apac.smartenv.person.entity.GroupMember;
import com.ai.apac.smartenv.person.vo.GroupMemberVO;
import com.ai.apac.smartenv.person.wrapper.GroupMemberWrapper;
import com.ai.apac.smartenv.person.service.IGroupMemberService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.List;

/**
 * 组成员信息表 控制器
 *
 * @author Blade
 * @since 2020-09-10
 */
@RestController
@AllArgsConstructor
@RequestMapping("/groupmember")
@Api(value = "组成员信息表", tags = "组成员信息表接口")
public class GroupMemberController extends BladeController {

	private IGroupMemberService groupMemberService;

	/**
	 * 新增 组成员信息表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "新增", notes = "传入groupMember")
	public R addGroupMembers(@ApiParam(value = "组ID", required = true) @RequestParam Long groupId,
				  @ApiParam(value = "成员IDs", required = true) @RequestParam String memberIds) {
		return R.status(groupMemberService.addGroupMembers(groupId,memberIds));
	}

	/**
	 * 删除 组成员信息表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R delGroupMembers(@ApiParam(value = "组ID", required = true) @RequestParam Long groupId,
					@ApiParam(value = "成员IDs", required = true) @RequestParam String memberIds) {
		return R.status(groupMemberService.delGroupMembers(groupId,memberIds));
	}

	
}
