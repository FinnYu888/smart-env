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
package com.ai.apac.smartenv.system.config;


import com.ai.apac.smartenv.system.handler.ApiScopePermissionHandler;
import com.ai.apac.smartenv.system.handler.DataScopeModelHandler;
import lombok.AllArgsConstructor;
import org.springblade.core.datascope.handler.ScopeModelHandler;
import org.springblade.core.secure.config.RegistryConfiguration;
import org.springblade.core.secure.handler.IPermissionHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共封装包配置类
 *
 * @author Chill
 */
@Configuration
@AllArgsConstructor
@AutoConfigureBefore(RegistryConfiguration.class)
public class ScopeConfiguration {

	@Bean
	public ScopeModelHandler scopeModelHandler() {
		return new DataScopeModelHandler();
	}

	@Bean
	public IPermissionHandler permissionHandler() {
		return new ApiScopePermissionHandler();
	}

}
