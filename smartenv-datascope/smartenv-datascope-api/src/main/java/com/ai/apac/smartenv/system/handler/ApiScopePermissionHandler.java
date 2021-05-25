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
package com.ai.apac.smartenv.system.handler;

import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.handler.IPermissionHandler;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.ai.apac.smartenv.system.cache.ApiScopeCache.permissionCode;
import static com.ai.apac.smartenv.system.cache.ApiScopeCache.permissionPath;

/**
 * 接口权限校验类
 *
 * @author Chill
 */
public class ApiScopePermissionHandler implements IPermissionHandler {

	@Override
	public boolean permissionAll() {
		HttpServletRequest request = WebUtil.getRequest();
		BladeUser user = SecureUtil.getUser();
		if (request == null || user == null) {
			return false;
		}
		String uri = request.getRequestURI();
		List<String> paths = permissionPath(user.getRoleId());
		if (paths == null || paths.size() == 0) {
			return false;
		}
		return paths.stream().anyMatch(uri::contains);
	}

	@Override
	public boolean hasPermission(String permission) {
		HttpServletRequest request = WebUtil.getRequest();
		BladeUser user = SecureUtil.getUser();
		if (request == null || user == null) {
			return false;
		}
		List<String> codes = permissionCode(permission, user.getRoleId());
		return codes != null && codes.size() != 0;
	}

}
