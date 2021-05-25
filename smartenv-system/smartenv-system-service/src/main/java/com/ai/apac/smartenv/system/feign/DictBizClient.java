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
import com.ai.apac.smartenv.system.service.IDictBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * 字典服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class DictBizClient implements IDictBizClient {

	private IDictBizService service;

	@Override
	@GetMapping(GET_BY_ID)
	public R<DictBiz> getById(Long id) {
		return R.data(service.getById(id));
	}

	@Override
	@GetMapping(GET_VALUE)
	public R<String> getValue(String code, String dictKey) {
		return R.data(service.getValue(code, dictKey));
	}

	@Override
	@GetMapping(GET_LIST)
	public R<List<DictBiz>> getList(String code) {
		return R.data(service.getList(code));
	}

	/**
	 * 获取所有字典表
	 *
	 * @return
	 */
	@Override
	public R<List<DictBiz>> getAllDict() {
		return R.data(service.list());
	}

	/**
	 * 获取指定租户的字典表
	 *
	 * @param tenantId
	 * @return
	 */
	@Override
	public R<List<DictBiz>> getTenantDict(String tenantId) {
		return R.data(service.list(new LambdaQueryWrapper<DictBiz>().eq(DictBiz::getTenantId,tenantId)));
	}

	@Override
	public R<List<DictBiz>> getTenantCodeDict(String tenantId, String code) {
		QueryWrapper<DictBiz> wrapper = new QueryWrapper<DictBiz>();
		wrapper.eq("code",code);
		wrapper.eq("tenant_Id", tenantId);
		wrapper.gt("parent_id",0);
		List<DictBiz> list = service.list(wrapper);
		return R.data(list);
	}

	@Override
	public R<String> getTenantCodeDictValue(String tenantId, String code, String dictKey) {
		QueryWrapper<DictBiz> wrapper = new QueryWrapper<DictBiz>();
		wrapper.eq("code",code);
		wrapper.eq("dict_key",dictKey);
		wrapper.eq("tenant_Id", tenantId);
		wrapper.gt("parent_id",0);
		DictBiz dictBiz = service.getOne(wrapper);

		return R.data(dictBiz.getDictValue());
	}
}
