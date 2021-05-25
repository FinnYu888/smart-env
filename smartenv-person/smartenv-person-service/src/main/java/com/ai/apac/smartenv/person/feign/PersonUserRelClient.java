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

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.service.IPersonUserRelService;
import com.ai.apac.smartenv.person.service.IPersonVehicleRelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class PersonUserRelClient implements IPersonUserRelClient {

	private IPersonUserRelService personUserRelService;
//	private IPersonService personService;
	private IPersonVehicleRelService personVehicleRelService;
	
	@Override
	@GetMapping(GET_REL_BY_USER_ID)
	public R<PersonUserRel> getRelByUserId(Long userId) {
		return R.data(personUserRelService.getRelByUserOrPerson(userId, null));
	}

	@Override
	@GetMapping(GET_REL_BY_PERSON_ID)
	public R<PersonUserRel> getRelByPersonId(Long personId) {
		return R.data(personUserRelService.getRelByUserOrPerson(null, personId));
	}

	@Override
	@GetMapping(GET_REL_BY_ID)
	public R<PersonUserRel> getRelById(Long relId) {
		return R.data(personUserRelService.getById(relId));
	}

	@Override
	@PostMapping(CREATE_REL)
	public R<PersonUserRel> createPersonUserRel(@RequestBody PersonUserRel personUserRel) {
		personUserRelService.save(personUserRel);
		return R.data(personUserRel);
	}

	/**
	 * 根据租户查询员工和帐号的绑定关系
	 *
	 * @param tenantId
	 * @return
	 */
	@GetMapping(GET_REL_BY_TENANT_ID)
	@Override
	public R<List<PersonUserRel>> getRelByTenant(@RequestParam("tenantId") String tenantId) {
		List<PersonUserRel> list = personUserRelService.list(new LambdaQueryWrapper<PersonUserRel>().eq(PersonUserRel::getTenantId,tenantId));
		return R.data(list);
	}

	@Override
	@PostMapping(GET_VEHICLE_BY_USER_ID)
	public R<List<Long>> getVehicleByUserId(@RequestParam("userId")Long userId) {
		return R.data(personVehicleRelService.getRelVehiclesByuserId(userId));
	}
}
