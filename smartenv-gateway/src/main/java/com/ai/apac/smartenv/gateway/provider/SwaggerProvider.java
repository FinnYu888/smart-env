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
package com.ai.apac.smartenv.gateway.provider;

import com.ai.apac.smartenv.gateway.props.RouteProperties;
import com.ai.apac.smartenv.gateway.props.RouteResource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合接口文档注册
 *
 * @author Chill
 */
@Primary
@Component
@AllArgsConstructor
public class SwaggerProvider implements SwaggerResourcesProvider {
	private static final String API_URI = "/v2/api-docs-ext";

	private RouteProperties routeProperties;

	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<>();
		List<RouteResource> routeResources = routeProperties.getResources();
		routeResources.forEach(routeResource -> resources.add(swaggerResource(routeResource)));
		return resources;
	}

	private SwaggerResource swaggerResource(RouteResource routeResource) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(routeResource.getName());
		swaggerResource.setLocation(routeResource.getLocation().concat(API_URI));
		swaggerResource.setSwaggerVersion(routeResource.getVersion());
		return swaggerResource;
	}

}
