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
package com.ai.apac.smartenv.gateway;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Properties;

/**
 * 项目启动
 *
 * @author Chill
 */
@EnableHystrix
@EnableScheduling
@SpringCloudApplication
public class GateWayApplication {

	public static void main(String[] args) {
		BladeApplication.run(ApplicationConstant.APPLICATION_GATEWAY_NAME, GateWayApplication.class, args);
	}
}
