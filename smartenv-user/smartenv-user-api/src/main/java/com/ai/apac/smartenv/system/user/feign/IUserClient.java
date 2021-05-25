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
package com.ai.apac.smartenv.system.user.feign;


import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.entity.UserInfo;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_USER_NAME
)
public interface IUserClient {

	String API_PREFIX = "/client";
	String USER_INFO = API_PREFIX + "/user-info";
	String USER_INFO_BY_ID = API_PREFIX + "/user-info-by-id";
	String USER_BY_ACCOUNT = API_PREFIX + "/user-by-acct";
	String USER_INFO_BY_DEPT_ID = API_PREFIX + "/user-info-by-deptId";
	String USER_INFO_BY_ACCOUNT = API_PREFIX + "/user-info-by-acct";
	String SAVE_USER = API_PREFIX + "/save-user";
	String UPDATE_USER = API_PREFIX + "/update-user";
	String GET_ALL_USER = API_PREFIX + "/all-user";
	String GET_TENANT_USER = API_PREFIX + "/user-by-tenant";
	String GET_ROLE_USER = API_PREFIX + "/user-by-role";
	/**
	 * 获取用户信息
	 *
	 * @param userId 用户id
	 * @return
	 */
	@GetMapping(USER_INFO_BY_ID)
	R<User> userInfoById(@RequestParam("userId") Long userId);

	/**
	 * 根据部门获取用户信息
	 *
	 * @param deptId id
	 * @return
	 */
	@GetMapping(USER_INFO_BY_DEPT_ID)
	R<List<User>> userInfoByDeptId(@RequestParam("deptId") Long deptId);

	/**
	 * 获取用户信息
	 *
	 * @param tenantId 租户ID
	 * @param account  账号
	 * @return
	 */
	@GetMapping(USER_INFO)
	R<UserInfo> userInfo(@RequestParam("tenantId") String tenantId, @RequestParam("account") String account);

	/**
	 * 根据帐户获取用户信息
	 *
	 * @param account  账号
	 * @return
	 */
	@GetMapping(USER_INFO_BY_ACCOUNT)
	R<UserInfo> getUserInfoByAccount(@RequestParam("account") String account);

	/**
	 * 新建用户
	 *
	 * @param user 用户实体
	 * @return
	 */
	@PostMapping(SAVE_USER)
	R<Boolean> saveUser(@RequestBody User user);

	/**
	 * 更新用户
	 *
	 * @param user 用户实体
	 * @return
	 */
	@PostMapping(UPDATE_USER)
	R<Boolean> updateUser(@RequestBody User user);

	/**
	 * 获取所有用户信息
	 * @return
	 */
	@GetMapping(GET_ALL_USER)
	R<List<User>> getAllUser();

	/**
	 * 获取指定租户的所有用户信息
	 * @return
	 */
	@GetMapping(GET_TENANT_USER)
	R<List<User>> getTenantUser(@RequestParam("tenantId") String tenantId);

	@GetMapping(GET_ROLE_USER)
	R<List<User>> getRoleUser(@RequestParam("roleId")  String roleId,@RequestParam("tenantId") String tenantId);

	/**
	 * 根据帐户获取用户信息
	 *
	 * @param account 用户帐号
	 * @return
	 */
	@GetMapping(USER_BY_ACCOUNT)
	R<User> userByAcct(@RequestParam("account") String account);
}
