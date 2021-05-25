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
package com.ai.apac.smartenv.person.service.impl;

import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.vo.PersonUserRelVO;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.person.mapper.PersonUserRelMapper;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.service.IPersonUserRelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 员工用户关联表 服务实现类
 *
 * @author Blade
 * @since 2020-03-31
 */
@Service
public class PersonUserRelServiceImpl extends BaseServiceImpl<PersonUserRelMapper, PersonUserRel> implements IPersonUserRelService {

	@Autowired
	private IUserClient userClient;
	@Autowired
	@Lazy
	private IPersonService personService;
	
	@Override
	public IPage<PersonUserRelVO> selectPersonUserRelPage(IPage<PersonUserRelVO> page, PersonUserRelVO personUserRel) {
		return page.setRecords(baseMapper.selectPersonUserRelPage(page, personUserRel));
	}

	@Override
	public PersonUserRel getRelByUserOrPerson(Long userId, Long personId) {
		PersonUserRel personUserRel = new PersonUserRel();
		if (userId == null && personId == null) {
			return null;
		}
		if (userId != null) {
			personUserRel.setUserId(userId);
		}
		if (personId != null) {
			personUserRel.setPersonId(personId);
		}
		List<PersonUserRel> relList = list(Condition.getQueryWrapper(personUserRel));
		if (relList == null || relList.isEmpty()) {
			return null;
		}
		return relList.get(0);
	}

	@Override
	public Boolean savePersonUserRel(PersonUserRel personUserRel) {
		validateRel(personUserRel);
		checkExistRel(personUserRel);
		checkDept(personUserRel);
		PersonUserRelCache.delRel(personUserRel);
		save(personUserRel);
		return true;
	}

	/*
	 * 参数规则校验
	 */
	private void validateRel(@Valid PersonUserRel personUserRel) {
		Set<ConstraintViolation<@Valid PersonUserRel>> validateSet = Validation.buildDefaultValidatorFactory()
				.getValidator().validate(personUserRel, new Class[0]);
		if (validateSet != null && !validateSet.isEmpty()) {
			String messages = validateSet.stream().map(ConstraintViolation::getMessage)
					.reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
			throw new ServiceException(messages);
		}
	}
	
	/*
	 * 校验是否已绑定
	 */
	private void checkExistRel(PersonUserRel personUserRel) {
		Long personId = personUserRel.getPersonId();
		PersonUserRel rel = PersonUserRelCache.getRelByPersonId(personId);
		if (rel != null && rel.getId() != null) {
			throw new ServiceException("该人员已存在操作员");
		}
		Long userId = personUserRel.getUserId();
		rel = PersonUserRelCache.getRelByUserId(userId);
		if (rel != null && rel.getId() != null) {
			throw new ServiceException("该操作员已存在人员");
		}
	}
	
	/*
	 * 校验是否同部门
	 */
	private void checkDept(PersonUserRel personUserRel) {
//		Person person = PersonCache.getPersonById(null, personUserRel.getPersonId());// 新增人员，表和缓存不是一个事务，缓存没有数据，直接查表
		Person person = personService.getById(personUserRel.getPersonId());
		User user = UserCache.getUser(personUserRel.getUserId());
		if (person == null || person.getId() == null) {
			throw new ServiceException("该人员不存在");
		}
		if (user == null || user.getId() == null) {
			throw new ServiceException("该操作员不存在");
		}
		if (!String.valueOf(person.getPersonDeptId()).equals(user.getDeptId())) {
			throw new ServiceException("操作员和人员部门不同，不能绑定");
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
	public Boolean deletePersonUserRel(List<Long> idList) {
		idList.forEach(id -> {
			PersonUserRel rel = PersonUserRelCache.getRelByRelId(id);
			PersonUserRelCache.delRel(rel);
			deleteLogic(Arrays.asList(id));
		});
		return true;
	}

	@Override
	public void unbindUser(Long personId) {
		PersonUserRel rel = PersonUserRelCache.getRelByPersonId(personId);
		PersonUserRelCache.delRel(rel);
		deleteLogic(Arrays.asList(rel.getId()));
	}

	@Override
	public List<PersonVO> listUserForPerson(Long personDeptId, Long personId) {
		if (personDeptId == null || personDeptId <= 0) {
			throw new ServiceException("需要输入所属部门");
		}
		// 根据部门查询
		List<User> userList = userClient.userInfoByDeptId(personDeptId).getData();
		Iterator<User> userIterator = userList.iterator();
		// 过滤已绑定的操作员
		while (userIterator.hasNext()) {
			User user = userIterator.next();
			Long userId = user.getId();
			PersonUserRel personUserRel = PersonUserRelCache.getRelByUserId(userId);
			// 不排除当前人员当前绑定的操作员
			if (personUserRel != null && personUserRel.getId() != null && !personUserRel.getPersonId().equals(personId)) {
				userIterator.remove();
			}
		}
		List<PersonVO> relList = new ArrayList<>();
		if (!userList.isEmpty()) {
			userList.forEach(user -> {
				PersonVO personVO = new PersonVO();
				personVO.setAccount(user.getAccount());
				personVO.setUserId(user.getId());
				personVO.setRoleId(user.getRoleId());
				personVO.setRoleName(RoleCache.roleNames(user.getRoleId()));
				relList.add(personVO);
			});
		}
		return relList;
	}

	@Override
	public List<Person> listPersonForUser(Long userId) {
		List<Person> personList = new ArrayList<>();
		List<PersonUserRel> personUserRelList = this.list(new QueryWrapper<PersonUserRel>().eq("user_id",userId));
		for (PersonUserRel personUserRel : personUserRelList) {
			Person person = personService.getById(personUserRel.getPersonId());
			personList.add(person);
		}
		return personList;
	}
}
