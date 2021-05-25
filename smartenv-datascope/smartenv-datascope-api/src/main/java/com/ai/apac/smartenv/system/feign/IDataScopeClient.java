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
import org.springblade.core.datascope.model.DataScopeModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 数据权限Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
	fallback = IDataScopeClientFallback.class
)
public interface IDataScopeClient {

	String API_PREFIX = "/client/data-scope";
	String GET_DATA_SCOPE_BY_MAPPER = API_PREFIX + "/by-mapper";
	String GET_DATA_SCOPE_BY_CODE = API_PREFIX + "/by-code";
	String GET_DEPT_ANCESTORS = API_PREFIX + "/dept-ancestors";

	/**
	 * 获取数据权限
	 *
	 * @param mapperId 数据权限mapperId
	 * @param roleId   用户角色集合
	 * @return DataScopeModel
	 */
	@GetMapping(GET_DATA_SCOPE_BY_MAPPER)
	DataScopeModel getDataScopeByMapper(@RequestParam("mapperId") String mapperId, @RequestParam("roleId") String roleId);

	/**
	 * 获取数据权限
	 *
	 * @param code 数据权限资源编号
	 * @return DataScopeModel
	 */
	@GetMapping(GET_DATA_SCOPE_BY_CODE)
	DataScopeModel getDataScopeByCode(@RequestParam("code") String code);

	/**
	 * 获取部门子级
	 *
	 * @param deptId 部门id
	 * @return deptIds
	 */
	@GetMapping(GET_DEPT_ANCESTORS)
	List<Long> getDeptAncestors(@RequestParam("deptId") Long deptId);


}
