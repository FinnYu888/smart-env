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
package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.DictBiz;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IDictBizClientFallback implements IDictBizClient {
	@Override
	public R<DictBiz> getById(Long id) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<String> getValue(String code, String dictKey) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<DictBiz>> getList(String code) {
		return R.fail("获取数据失败");
	}

	/**
	 * 获取所有字典表
	 *
	 * @return
	 */
	@Override
	public R<List<DictBiz>> getAllDict() {
		return R.fail("获取数据失败");
	}

	/**
	 * 获取指定租户的字典表
	 *
	 * @param tenantId
	 * @return
	 */
	@Override
	public R<List<DictBiz>> getTenantDict(String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<DictBiz>> getTenantCodeDict(String tenantId, String code) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<String> getTenantCodeDictValue(String tenantId, String code, String dictKey) {
		return R.fail("获取数据失败");
	}
}
