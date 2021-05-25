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


import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.DictBiz;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
	fallback = IDictBizClientFallback.class
)
public interface IDictBizClient {

	String API_PREFIX = "/client";
	String GET_BY_ID = API_PREFIX + "/dict-biz/get-by-id";
	String GET_VALUE = API_PREFIX + "/dict-biz/get-value";
	String GET_LIST = API_PREFIX + "/dict-biz/get-list";
	String GET_ALL_DICT = API_PREFIX + "/dict-biz/get-all";
	String GET_TENANT_DICT = API_PREFIX + "/dict-biz/get-by-tenant";
	String GET_TENANT_CODE_DICT = API_PREFIX + "/dict-biz/get-by-tenantCode";
	String GET_TENANT_CODE_DICT_VALUE = API_PREFIX + "/dict-biz/get-by-tenantCode-value";

	/**
	 * 获取字典实体
	 *
	 * @param id 主键
	 * @return
	 */
	@GetMapping(GET_BY_ID)
	R<DictBiz> getById(@RequestParam("id") Long id);

	/**
	 * 获取字典表对应值
	 *
	 * @param code    字典编号
	 * @param dictKey 字典序号
	 * @return
	 */
	@GetMapping(GET_VALUE)
	R<String> getValue(@RequestParam("code") String code, @RequestParam("dictKey") String dictKey);

	/**
	 * 获取字典表
	 *
	 * @param code 字典编号
	 * @return
	 */
	@GetMapping(GET_LIST)
	R<List<DictBiz>> getList(@RequestParam("code") String code);

	/**
	 * 获取所有字典表
	 *
	 * @return
	 */
	@GetMapping(GET_ALL_DICT)
	R<List<DictBiz>> getAllDict();

	/**
	 * 获取指定租户的字典表
	 *
	 * @return
	 */
	@GetMapping(GET_TENANT_DICT)
	R<List<DictBiz>> getTenantDict(@RequestParam("tenantId") String tenantId);
	/**
	 * 获取指定租户的字典表，字典编号
	 *
	 * @return
	 */
	@GetMapping(GET_TENANT_CODE_DICT)
	R<List<DictBiz>> getTenantCodeDict(@RequestParam("tenantId") String tenantId,@RequestParam("code") String code);
	/**
	 * 获取指定租户的字典表，字典编号
	 *
	 * @return
	 */
	@GetMapping(GET_TENANT_CODE_DICT_VALUE)
	R<String> getTenantCodeDictValue(@RequestParam("tenantId") String tenantId,@RequestParam("code") String code,@RequestParam("dictKey") String dictKey);
}
