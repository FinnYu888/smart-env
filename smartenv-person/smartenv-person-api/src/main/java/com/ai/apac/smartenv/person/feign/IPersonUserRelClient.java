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
package com.ai.apac.smartenv.person.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 *
 */
@FeignClient(value = ApplicationConstant.APPLICATION_PERSON_NAME, fallback = IPersonClientFallback.class)
public interface IPersonUserRelClient {

	String API_PREFIX = "/client";
	String GET_REL_BY_ID = API_PREFIX + "/get-rel-by-id";
	String GET_REL_BY_USER_ID = API_PREFIX + "/get-rel-by-user-id";
	String GET_REL_BY_PERSON_ID = API_PREFIX + "/get-rel-by-person-id";
	String GET_REL_BY_TENANT_ID = API_PREFIX + "/get-rel-by-tenant-id";
	String CREATE_REL = API_PREFIX + "/create_person_user_rel";
	String GET_VEHICLE_BY_USER_ID = API_PREFIX + "/get-Vehicle-by-user-id";
	
	@GetMapping(GET_REL_BY_USER_ID)
	R<PersonUserRel> getRelByUserId(@RequestParam("userId") Long userId);

	@GetMapping(GET_REL_BY_PERSON_ID)
	R<PersonUserRel> getRelByPersonId(@RequestParam("personId") Long personId);

	/**
	 * 根据租户查询员工和帐号的绑定关系
	 * @param tenantId
	 * @return
	 */
	@GetMapping(GET_REL_BY_TENANT_ID)
	R<List<PersonUserRel>> getRelByTenant(@RequestParam("tenantId") String tenantId);

	@GetMapping(GET_REL_BY_ID)
	R<PersonUserRel> getRelById(@RequestParam("relId") Long relId);

	@PostMapping(CREATE_REL)
	R<PersonUserRel> createPersonUserRel(@RequestBody PersonUserRel personUserRel);

	@PostMapping(GET_VEHICLE_BY_USER_ID)
	R<List<Long>> getVehicleByUserId(@RequestParam("userId")Long userId);
}
