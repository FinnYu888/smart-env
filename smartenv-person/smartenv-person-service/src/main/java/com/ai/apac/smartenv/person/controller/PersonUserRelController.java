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

import com.ai.apac.smartenv.system.entity.Dept;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.vo.PersonUserRelVO;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.person.wrapper.PersonUserRelWrapper;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.person.service.IPersonUserRelService;

import java.util.List;

import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;

/**
 * 员工用户关联表 控制器
 *
 * @author Blade
 * @since 2020-03-31
 */
@RestController
@AllArgsConstructor
@RequestMapping("personuserrel")
@Api(value = "员工用户关联表", tags = "员工用户关联表接口")
public class PersonUserRelController extends BladeController {

	private IPersonUserRelService personUserRelService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入personUserRel")
	@ApiLog(value = "员工用户关联表详情")
	public R<PersonUserRelVO> detail(PersonUserRel personUserRel) {
		PersonUserRel detail = PersonUserRelCache.getRelByRelId(personUserRel.getId());
		PersonUserRelVO personUserRelVO = PersonUserRelWrapper.build().entityVO(detail);
		personUserRelVO = getRelAllInfoByVO(personUserRelVO);
		return R.data(personUserRelVO);
	}

	/**
	 * 分页 员工用户关联表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入personUserRel")
	@ApiLog(value = "分页 员工用户关联表")
	public R<IPage<PersonUserRelVO>> list(PersonUserRel personUserRel, Query query) {
		IPage<PersonUserRel> pages = personUserRelService.page(Condition.getPage(query), Condition.getQueryWrapper(personUserRel));
		IPage<PersonUserRelVO> pageVO = PersonUserRelWrapper.build().pageVO(pages);
        List<PersonUserRelVO> records = pageVO.getRecords();
        records.forEach(record -> {
            record = getRelAllInfoByVO(record);
        });
		return R.data(pageVO);
	}

	/**
	 * 新增 员工用户关联表
	 */
	@PostMapping("")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入personUserRel")
	@ApiLog(value = "新增员工用户关联")
	public R<?> save(@RequestBody PersonUserRel personUserRel) {
		return R.status(personUserRelService.savePersonUserRel(personUserRel));
	}

	/**
	 * 修改 员工用户关联表
	 */
	/*@PutMapping("")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入personUserRel")
	public R<?> update(@Valid @RequestBody PersonUserRel personUserRel) {
		return R.status(personUserRelService.updateById(personUserRel));
	}*/

	/**
	 * 删除 员工用户关联表
	 */
	@DeleteMapping("")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@ApiLog(value = "解绑员工用户关联")
	public R<?> remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(personUserRelService.deletePersonUserRel(Func.toLongList(ids)));
	}

	/**
	 * 查询可绑定的操作员
	 */
	@GetMapping("/listUserForPerson")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "查询可绑定的操作员", notes = "")
	@ApiLog(value = "查询可绑定的操作员")
	public List<PersonVO> listUserForPerson(@RequestParam Long personDeptId, @RequestParam Long personId) {
		List<PersonVO> list = personUserRelService.listUserForPerson(personDeptId, personId);
		return list;
	}


	/**
	 * 查询操作员绑定的员工
	 */
	@GetMapping("/listPersonForUser")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "查询操作员绑定的员工", notes = "")
	@ApiLog(value = "查询操作员绑定的员工")
	public List<Person> listPersonForUser(BladeUser user) {
		List<Person> list = personUserRelService.listPersonForUser(user.getUserId());
		return list;
	}
	
	private PersonUserRelVO getRelAllInfoByVO(PersonUserRelVO personUserRelVO) {
		Long personId = personUserRelVO.getPersonId();
		Person person = PersonCache.getPersonById(null, personId);
		if (person != null) {
			personUserRelVO.setPersonName(person.getPersonName());
			personUserRelVO.setJobNumber(person.getJobNumber());
			Long personDeptId = person.getPersonDeptId();
			Dept dept = DeptCache.getDept(personDeptId);
			if (dept != null) {
				personUserRelVO.setDeptName(dept.getFullName());
			}
		}
		User user = UserCache.getUser(personUserRelVO.getUserId());
		if (user != null && user.getId() != null) {
			personUserRelVO.setAccount(user.getAccount());
			personUserRelVO.setRoleName(RoleCache.roleNames(user.getRoleId()));
		}
		return personUserRelVO;
	}
	
}
