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
package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.facility.entity.AshcanInfo;

import com.ai.apac.smartenv.facility.vo.AshcanInfoVO;
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
public class IAshcanClientFallback implements IAshcanClient {

	@Override
	public R<AshcanInfo> getAshcan(Long id) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<AshcanInfoVO> getAshcanVO(Long id) {
		return R.fail("获取数据失败");
	}
	
	@Override
	public R<List<AshcanInfo>> listAshcanInfoByid(Long id) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<AshcanInfo>> listAshcanInfoAll() {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Integer> countAshcanInfo(String tenantId) {
		return R.fail("获取数据失败");
	}

}
