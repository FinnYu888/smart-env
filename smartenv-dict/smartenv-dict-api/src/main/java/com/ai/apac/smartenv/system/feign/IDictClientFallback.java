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

import com.ai.apac.smartenv.system.entity.Dict;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IDictClientFallback implements IDictClient {
	@Override
	public R<Dict> getById(Long id) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<String> getValue(String code, String dictKey) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<Dict>> getList(String code) {
		return R.fail("获取数据失败");
	}

    /**
     * 获取字典表中所有数据
     *
     * @return
     */
    @Override
    public R<List<Dict>> getAllDict() {
		return R.fail("获取数据失败");
    }

	@Override
	public R<Map<String, Object>> getMap(String code) {
		return R.data(new HashMap<>());
	}
}
