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
package com.ai.apac.smartenv.auth.utils;

import lombok.SneakyThrows;
import org.springblade.core.launch.constant.TokenConstant;
import org.springblade.core.tool.utils.Charsets;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;

import java.util.Base64;
import java.util.Calendar;

/**
 * 认证工具类
 *
 * @author Chill
 */
public class TokenUtil {

	public final static String AVATAR = TokenConstant.AVATAR;
	public final static String ACCOUNT = TokenConstant.ACCOUNT;
	public final static String USER_NAME = TokenConstant.USER_NAME;
	public final static String NICK_NAME = TokenConstant.NICK_NAME;
	public final static String REAL_NAME = TokenConstant.REAL_NAME;
	public final static String USER_ID = TokenConstant.USER_ID;
	public final static String DEPT_ID = TokenConstant.DEPT_ID;
	public final static String ROLE_ID = TokenConstant.ROLE_ID;
	public final static String ROLE_NAME = TokenConstant.ROLE_NAME;
	public final static String ROLE_GROUP = TokenConstant.ROLE_GROUP;
	public final static String TENANT_ID = TokenConstant.TENANT_ID;
	public final static String TENANT_NAME = "tenant_name";
	public final static String PLATFORM_NAME = "platform_name";
	public final static String TENANT_CITY = "city_id";
	public final static String TENANT_CITY_NAME = "city_name";
	public final static String TENANT_CITY_LON = "city_lon";
	public final static String TENANT_CITY_LAT = "city_lat";
	public final static String PROJECT_LIST = "project_list";
	public final static String CLIENT_ID = TokenConstant.CLIENT_ID;
	public final static String LICENSE = TokenConstant.LICENSE;
	public final static String LICENSE_NAME = "Powered By AsiaInfo";


	public final static String TENANT_HEADER_KEY = "Tenant-Id";
	public final static String TENANT_PARAM_KEY = "tenant_id";
	public final static String DEFAULT_TENANT_ID = "000000";
	public final static String TENANT_NOT_FOUND = "租户ID未找到";
	public final static String USER_TYPE_HEADER_KEY = "User-Type";
	public final static String DEFAULT_USER_TYPE = "web";
	public final static String USER_NOT_FOUND = "用户名或密码错误";
	public final static String USER_HAS_NO_ROLE = "未获得用户的角色信息";
	public final static String USER_STATUS_LOCKED_ERROR = "帐号已锁定,请联系管理员";
	public final static String USER_STATUS_CANCELLED_ERROR = "帐号已注销,请联系管理员";
	public final static String USER_HAS_NO_TENANT = "未获得用户的租户信息";
	public final static String USER_HAS_NO_TENANT_PERMISSION = "项目授权已过期,请联系管理员";
	public final static String TENANT_LOCKED = "项目状态已锁定,请联系管理员";
	public final static String HEADER_KEY = "Authorization";
	public final static String HEADER_PREFIX = "Basic ";
	public final static String DEFAULT_AVATAR = "";
	public final static String SWITCH_PROJECT = "switchProject";


	/**
	 * 解码
	 */
	@SneakyThrows
	public static String[] extractAndDecodeHeader() {
		String header = WebUtil.getRequest().getHeader(TokenUtil.HEADER_KEY);
		if (header == null || !header.startsWith(TokenUtil.HEADER_PREFIX)) {
			throw new UnapprovedClientAuthenticationException("请求头中无client信息");
		}

		byte[] base64Token = header.substring(6).getBytes(Charsets.UTF_8_NAME);

		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException var7) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, Charsets.UTF_8_NAME);
		int index = token.indexOf(StringPool.COLON);
		if (index == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		} else {
			return new String[]{token.substring(0, index), token.substring(index + 1)};
		}
	}

	/**
	 * 获取请求头中的客户端id
	 */
	public static String getClientIdFromHeader() {
		String[] tokens = extractAndDecodeHeader();
		return tokens[0];
	}

	/**
	 * 获取token过期时间(次日凌晨3点)
	 *
	 * @return expire
	 */
	public static int getTokenValiditySecond() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (int) (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
	}

	/**
	 * 获取refreshToken过期时间
	 *
	 * @return expire
	 */
	public static int getRefreshTokenValiditySeconds() {
		return 60 * 60 * 24 * 15;
	}

}
