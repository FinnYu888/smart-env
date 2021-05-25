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

import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.utils.Func;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springblade.core.secure.constant.PermissionConstant.permissionAllStatement;
import static org.springblade.core.secure.constant.PermissionConstant.permissionStatement;

/**
 * 接口权限Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class ApiScopeClient implements IApiScopeClient {

	private final JdbcTemplate jdbcTemplate;

	@Override
	@GetMapping(PERMISSION_PATH)
	public List<String> permissionPath(String roleId) {
		List<Long> roleIds = Func.toLongList(roleId);
		return jdbcTemplate.queryForList(permissionAllStatement(roleIds.size()), roleIds.toArray(), String.class);
	}

	@Override
	@GetMapping(PERMISSION_CODE)
	public List<String> permissionCode(String permission, String roleId) {
		List<Object> args = new ArrayList<>(Collections.singletonList(permission));
		List<Long> roleIds = Func.toLongList(roleId);
		args.addAll(roleIds);
		return jdbcTemplate.queryForList(permissionStatement(roleIds.size()), args.toArray(), String.class);
	}

}
