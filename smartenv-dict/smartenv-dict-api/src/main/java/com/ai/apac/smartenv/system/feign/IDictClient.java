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
import com.ai.apac.smartenv.system.entity.Dict;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
	fallback = IDictClientFallback.class
)
public interface IDictClient {

	String API_PREFIX = "/client";
	String GET_BY_ID = API_PREFIX + "/dict/get-by-id";
	String GET_VALUE = API_PREFIX + "/dict/get-value";
	String GET_LIST = API_PREFIX + "/dict/get-list";
	String GET_ALL_DICT = API_PREFIX + "/dict/get-all";
	String GET_MAP_DICT = API_PREFIX + "dict/get-map";
	/**
	 * 获取字典实体
	 *
	 * @param id 主键
	 * @return
	 */
	@GetMapping(GET_BY_ID)
	R<Dict> getById(@RequestParam("id") Long id);

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
	R<List<Dict>> getList(@RequestParam("code") String code);

	/**
	 * 获取字典表中所有数据
	 * @return
	 */
	@GetMapping(GET_ALL_DICT)
	R<List<Dict>> getAllDict();
	/**
	 * 获取字典表
	 *
	 * @param code 字典编号
	 * @return
	 */
	@GetMapping(GET_MAP_DICT)
	R<Map<String,Object>>  getMap(@RequestParam("code") String code);

}
