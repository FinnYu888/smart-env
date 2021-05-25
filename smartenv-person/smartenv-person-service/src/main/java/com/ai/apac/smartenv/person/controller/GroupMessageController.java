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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.person.entity.GroupMessage;
import com.ai.apac.smartenv.person.vo.GroupMessageVO;
import com.ai.apac.smartenv.person.wrapper.GroupMessageWrapper;
import com.ai.apac.smartenv.person.service.IGroupMessageService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.List;

/**
 * 组消息表 控制器
 *
 * @author Blade
 * @since 2020-09-10
 */
@RestController
@AllArgsConstructor
@RequestMapping("/groupmessage")
@Api(value = "组消息表", tags = "组消息表接口")
public class GroupMessageController extends BladeController {

	private IGroupMessageService groupMessageService;

	/**
	 * 详情
	 */
	@GetMapping("")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入groupMessage")
	public R<GroupMessageVO> detail(GroupMessage groupMessage) {
		GroupMessage detail = groupMessageService.getOne(Condition.getQueryWrapper(groupMessage));
		return R.data(GroupMessageWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 组消息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入groupMessage")
	public R<IPage<GroupMessageVO>> page(GroupMessage groupMessage, Query query) {
		IPage<GroupMessage> pages = groupMessageService.page(Condition.getPage(query), Condition.getQueryWrapper(groupMessage).lambda().orderByDesc(GroupMessage::getCreateTime));
		return R.data(GroupMessageWrapper.build().pageVO(pages));
	}

	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "列表", notes = "传入groupMessage")
	public R<List<GroupMessageVO>> list(GroupMessage groupMessage) {
		QueryWrapper<GroupMessage> wrapper = new QueryWrapper<GroupMessage>();
		if(ObjectUtil.isNotEmpty(groupMessage.getMessageInfo())){
			wrapper.lambda().like(GroupMessage::getMessageInfo,groupMessage.getMessageInfo());
		}
		wrapper.lambda().orderByDesc(GroupMessage::getCreateTime);
		List<GroupMessage> messageList = groupMessageService.list(wrapper);
		return R.data(GroupMessageWrapper.build().listVO(messageList));
	}


	/**
	 * 新增或修改 组消息表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "发送Message", notes = "发送Message")
	public R submit(@Valid @RequestBody GroupMessageVO groupMessageVO) {
		return R.status(groupMessageService.submitMessage(groupMessageVO));
	}

	
}
