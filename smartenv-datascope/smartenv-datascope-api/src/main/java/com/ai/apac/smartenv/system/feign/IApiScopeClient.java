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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 接口权限Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
	fallback = IApiScopeClientFallback.class
)
public interface IApiScopeClient {

	String API_PREFIX = "/client/api-scope";
	String PERMISSION_PATH = API_PREFIX + "/permission-path";
	String PERMISSION_CODE = API_PREFIX + "/permission-code";

	/**
	 * 获取接口权限地址
	 *
	 * @param roleId 角色id
	 * @return permissions
	 */
	@GetMapping(PERMISSION_PATH)
	List<String> permissionPath(@RequestParam("roleId") String roleId);

	/**
	 * 获取接口权限信息
	 *
	 * @param permission 权限编号
	 * @param roleId     角色id
	 * @return permissions
	 */
	@GetMapping(PERMISSION_CODE)
	List<String> permissionCode(@RequestParam("permission") String permission, @RequestParam("roleId") String roleId);

}
