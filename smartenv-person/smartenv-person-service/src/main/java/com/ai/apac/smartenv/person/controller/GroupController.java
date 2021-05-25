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

import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.person.wrapper.PersonWrapper;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ai.apac.smartenv.person.entity.Group;
import com.ai.apac.smartenv.person.vo.GroupVO;
import com.ai.apac.smartenv.person.wrapper.GroupWrapper;
import com.ai.apac.smartenv.person.service.IGroupService;
import org.springblade.core.boot.ctrl.BladeController;

import java.util.ArrayList;
import java.util.List;

/**
 * 组信息表 控制器
 *
 * @author Blade
 * @since 2020-09-10
 */
@RestController
@AllArgsConstructor
@RequestMapping("/group")
@Api(value = "组信息表", tags = "组信息表接口")
public class GroupController extends BladeController {

	private IGroupService groupService;

	private IPersonService personService;

	/**
	 * 详情
	 */
	@GetMapping("")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入组ID")
	public R<GroupVO> detail(@RequestParam(name = "groupId", required = true) Long groupId,
							 @RequestParam(name = "memberName", required = false) String memberName) {
		GroupVO detail = groupService.getGroupDetails(groupId,memberName);
		return R.data(detail);
	}

	@PutMapping("/default")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "设置默认组", notes = "设置默认组")
	public R<Boolean> setGroupDefault(@RequestParam(name = "groupId", required = true) Long groupId) {
		return R.data(groupService.setGroupDefault(groupId));
	}


	/**
	 * 分页 组信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入group")
	public R<IPage<GroupVO>> page(GroupVO groupVO, Query query) {
		IPage<Group> pages = groupService.page(Condition.getPage(query), Condition.getQueryWrapper(groupVO));
		return R.data(GroupWrapper.build().pageVO(pages));
	}

	/**
	 * 分页 组信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "列表", notes = "传入group")
	public R<List<GroupVO>> list(GroupVO groupVO, Query query) {
		List<GroupVO> groupVOList = groupService.getGroupList(groupVO);

		return R.data(groupVOList);
	}


	/**
	 * 新增 组信息表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "新增", notes = "传入group")
	public R save(@Valid @RequestBody GroupVO groupVO) {
		return R.status(groupService.saveGroup(groupVO));
	}

	/**
	 * 修改 组信息表
	 */
	@PutMapping("")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "修改", notes = "传入group")
	public R update(@Valid @RequestBody GroupVO groupVO) {
		return R.status(groupService.updateGroup(groupVO));
	}

	
	/**
	 * 删除 组信息表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(groupService.delGroup(Func.toLongList(ids)));
	}



	/**
	 * 查询绑定驾驶员信息
	 */
	@GetMapping("/pageForGroup")
	@ApiOperationSupport(order = 8)
	@ApiLog(value = "查询可添加组的人员信息")
	@ApiOperation(value = "查询可添加组的人员信息", notes = "传入person，groupId")
	public R<IPage<PersonVO>> pageForGroup(PersonVO person, Query query, Long groupId) {
		IPage<Person> pages = personService.pageForGroup(person, query, groupId);
		IPage<PersonVO> pageVO = PersonWrapper.build().pageVO(pages);
		List<PersonVO> records = pageVO.getRecords();
		records.forEach(record -> {
			record = getPersonAllInfoByVO(record);
		});
		return R.data(pageVO);
	}



	private PersonVO getPersonAllInfoByVO(PersonVO personVO) {
		if (personVO == null || personVO.getId() == null) {
			return personVO;
		}
		// 部门名称
		Dept dept = DeptCache.getDept(personVO.getPersonDeptId());
		if (dept != null) {
			personVO.setPersonDeptName(dept.getFullName());
		}

		// 岗位
		if (personVO.getPersonPositionId() != null) {
			personVO.setPersonPositionName(StationCache.getStationName(personVO.getPersonPositionId()));
		}


		return personVO;
	}

	
}
