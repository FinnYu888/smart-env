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
package com.ai.apac.smartenv.person.feign;

import com.ai.apac.smartenv.person.entity.PersonUserRel;

import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IPersonUserRelClientFallback implements IPersonUserRelClient {

	@Override
	public R<PersonUserRel> getRelById(Long relId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<PersonUserRel> getRelByUserId(Long userId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<PersonUserRel> getRelByPersonId(Long personId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<PersonUserRel> createPersonUserRel(PersonUserRel personUserRel) {
		return R.fail("获取数据失败");
	}

	/**
	 * 根据租户查询员工和帐号的绑定关系
	 *
	 * @param tenantId
	 * @return
	 */
	@Override
	public R<List<PersonUserRel>> getRelByTenant(String tenantId) {
		return R.fail("获取数据失败");
	}
	@Override
	public R<List<Long>> getVehicleByUserId(@RequestParam("userId")Long userId){
		return R.fail("获取数据失败");
	}
}
