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
package com.ai.apac.smartenv.gateway.config;


import com.ai.apac.smartenv.gateway.handler.ErrorExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * 异常处理配置类
 *
 * @author Chill
 */
@Configuration
@AutoConfigureBefore(ErrorWebFluxAutoConfiguration.class)
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
public class ErrorHandlerConfiguration {

	private final ServerProperties serverProperties;

	private final ApplicationContext applicationContext;

	private final ResourceProperties resourceProperties;

	private final List<ViewResolver> viewResolvers;

	private final ServerCodecConfigurer serverCodecConfigurer;

	public ErrorHandlerConfiguration(ServerProperties serverProperties,
									 ResourceProperties resourceProperties,
									 ObjectProvider<List<ViewResolver>> viewResolversProvider,
									 ServerCodecConfigurer serverCodecConfigurer,
									 ApplicationContext applicationContext) {
		this.serverProperties = serverProperties;
		this.applicationContext = applicationContext;
		this.resourceProperties = resourceProperties;
		this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
		this.serverCodecConfigurer = serverCodecConfigurer;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
		ErrorExceptionHandler exceptionHandler = new ErrorExceptionHandler(
			errorAttributes,
			this.resourceProperties,
			this.serverProperties.getError(),
			this.applicationContext);
		exceptionHandler.setViewResolvers(this.viewResolvers);
		exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
		exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
		return exceptionHandler;
	}

}
