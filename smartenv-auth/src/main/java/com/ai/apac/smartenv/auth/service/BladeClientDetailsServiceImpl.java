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
package com.ai.apac.smartenv.auth.service;

import lombok.SneakyThrows;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 客户端信息
 *
 * @author Chill
 */
@Component
public class BladeClientDetailsServiceImpl extends JdbcClientDetailsService {

	public BladeClientDetailsServiceImpl(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * 缓存客户端信息
	 *
	 * @param clientId 客户端id
	 */
	@Override
	@SneakyThrows
	public ClientDetails loadClientByClientId(String clientId) {
		return super.loadClientByClientId(clientId);
	}
}
