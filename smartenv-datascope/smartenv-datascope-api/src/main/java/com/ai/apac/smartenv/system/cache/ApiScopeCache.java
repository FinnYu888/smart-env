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
package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.system.feign.IApiScopeClient;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.List;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 接口权限缓存
 *
 * @author Chill
 */
public class ApiScopeCache {

	private static final String SCOPE_CACHE_CODE = "apiScope:code:";

	private static IApiScopeClient apiScopeClient;

	private static IApiScopeClient getApiScopeClient() {
		if (apiScopeClient == null) {
			apiScopeClient = SpringUtil.getBean(IApiScopeClient.class);
		}
		return apiScopeClient;
	}

	/**
	 * 获取接口权限地址
	 *
	 * @param roleId 角色id
	 * @return permissions
	 */
	public static List<String> permissionPath(String roleId) {
		List<String> permissions = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CODE, roleId, List.class);
		if (permissions == null) {
			permissions = getApiScopeClient().permissionPath(roleId);
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_CODE, roleId, permissions);
		}
		return permissions;
	}

	/**
	 * 获取接口权限信息
	 *
	 * @param permission 权限编号
	 * @param roleId     角色id
	 * @return permissions
	 */
	public static List<String> permissionCode(String permission, String roleId) {
		List<String> permissions = CacheUtil.get(SYS_CACHE, SCOPE_CACHE_CODE, permission + StringPool.COLON + roleId, List.class);
		if (permissions == null) {
			permissions = getApiScopeClient().permissionCode(permission, roleId);
			CacheUtil.put(SYS_CACHE, SCOPE_CACHE_CODE, permission + StringPool.COLON + roleId, permissions);
		}
		return permissions;
	}

}
