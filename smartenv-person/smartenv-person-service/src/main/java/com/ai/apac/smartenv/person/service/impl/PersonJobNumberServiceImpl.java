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

import com.ai.apac.smartenv.person.entity.PersonJobNumber;
import com.ai.apac.smartenv.person.vo.PersonJobNumberVO;
import com.ai.apac.smartenv.person.mapper.PersonJobNumberMapper;
import com.ai.apac.smartenv.person.service.IPersonJobNumberService;

import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-08-19
 */
@Service
public class PersonJobNumberServiceImpl extends BaseServiceImpl<PersonJobNumberMapper, PersonJobNumber> implements IPersonJobNumberService {

	@Override
	public IPage<PersonJobNumberVO> selectPersonJobNumberPage(IPage<PersonJobNumberVO> page, PersonJobNumberVO personJobNumber) {
		return page.setRecords(baseMapper.selectPersonJobNumberPage(page, personJobNumber));
	}

	@Override
	public synchronized String getNextNumber(String tenantId) {
		if (StringUtil.isBlank(tenantId)) {
			throw new ServiceException("生成员工工号失败");
		}
		Integer update = baseMapper.updateNextNumber(tenantId);
		if (update == 0) {
			// 更新不成功，创建该租户的序列
			PersonJobNumber personJobNumber = new PersonJobNumber();
			personJobNumber.setCurrentNumber(1);
			personJobNumber.setFigures(5);
			personJobNumber.setPrefix(null);
			personJobNumber.setTenantId(tenantId);
			save(personJobNumber);
		}
		QueryWrapper<PersonJobNumber> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("tenant_id", tenantId);
		queryWrapper.last("limit 1");
		PersonJobNumber current = getOne(queryWrapper);
		String currentNumber = String.format("%0" + current.getFigures() + "d", current.getCurrentNumber());
		if (StringUtil.isNotBlank(current.getPrefix())) {
			currentNumber = current.getPrefix() + currentNumber;
		}
		return currentNumber;
	}

}
