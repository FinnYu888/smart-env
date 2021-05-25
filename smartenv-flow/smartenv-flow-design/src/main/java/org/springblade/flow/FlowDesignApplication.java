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
package org.springblade.flow;

import org.flowable.ui.common.conf.DevelopmentConfiguration;
import org.flowable.ui.common.rest.idm.remote.RemoteAccountResource;
import org.mybatis.spring.annotation.MapperScan;
import org.springblade.core.launch.BladeApplication;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.flow.constant.FlowableConstant;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * FlowDesign启动器
 *
 * @author Chill
 */
@SpringBootApplication(
	exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class,
		LiquibaseAutoConfiguration.class
	}
)
@ComponentScan(
	basePackages = {AppConstant.BASE_PACKAGES, FlowableConstant.FLOWABLE_BASE_PACKAGES},
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RemoteAccountResource.class, DevelopmentConfiguration.class})
)
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv.flow"})
@MapperScan({"org.springblade.**.mapper.**", "com.ai.apac.smartenv.**.mapper.**"})
@SpringCloudApplication
public class FlowDesignApplication {

	public static void main(String[] args) {
		BladeApplication.run(AppConstant.APPLICATION_FLOWDESIGN_NAME, FlowDesignApplication.class, args);
	}

}

