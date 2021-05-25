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
package com.ai.apac.smartenv.auth.constant;

/**
 * 授权校验常量
 *
 * @author Chill
 */
public interface AuthConstant {

	/**
	 * 密码加密规则
	 */
	String ENCRYPT = "{blade}";

	/**
	 * blade_client表字段
	 */
	String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, resource_ids, scope, authorized_grant_types, " +
		"web_server_redirect_uri, authorities, access_token_validity, " +
		"refresh_token_validity, additional_information, autoapprove";

	/**
	 * blade_client查询语句
	 */
	String BASE_STATEMENT = "select " + CLIENT_FIELDS + " from blade_client";

	/**
	 * blade_client查询排序
	 */
	String DEFAULT_FIND_STATEMENT = BASE_STATEMENT + " order by client_id";

	/**
	 * 查询client_id
	 */
	String DEFAULT_SELECT_STATEMENT = BASE_STATEMENT + " where client_id = ?";

}
