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

import cn.hutool.core.util.ObjectUtil;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.service.IPersonVehicleRelService;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 驾驶员 服务Feign实现类
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class PersonVehicleRelClient implements IPersonVehicleRelClient {

    private IPersonVehicleRelService personVehicleRelService;
    private IPersonService personService;
    private IScheduleClient scheduleClient;


    @Override
    @GetMapping(REL_PERSON)
    public R<List<PersonVehicleRel>> getPersonVehicleRels(Long vehicleId) {
        return R.data(personVehicleRelService.getPersonByVehicle(vehicleId));
    }

	@Override
	@PostMapping(REL_PERSONS)
	public R<Map<Long, List<PersonVehicleRel>>> getPersonsVehicleRels(List<Long> vehicleIds) {
		Map<Long, List<PersonVehicleRel>> drives = new HashMap<>();
		for(Long vehicleId : vehicleIds){
			List<PersonVehicleRel> rels = personVehicleRelService.getPersonByVehicle(vehicleId);
			if(null!=rels&&rels.size()>0){
				drives.put(vehicleId,rels);
			}
		}

		return R.data(drives);
	}

    @Override
    @GetMapping(LIST_DRIVER)
    public R<List<Person>> listDriverByVehicleId(Long vehicleId) {
    	List<Person> personList = new ArrayList<>();
    	List<PersonVehicleRel> personRelList = personVehicleRelService.getPersonByVehicle(vehicleId);
    	if (personRelList == null || personRelList.isEmpty()) {
			return R.data(personList);
		}
    	personRelList.forEach(personRel -> {
    		personList.add(PersonCache.getPersonById(null, personRel.getPersonId()));
		});
    	return R.data(personList);
    }

    @Override
    @GetMapping(REL_DRIVER)
	public R<Person> getDriverByVehicleId(Long vehicleId) {
		List<PersonVehicleRel> personVehicleRelList = personVehicleRelService.getPersonByVehicle(vehicleId);
		if (personVehicleRelList != null && !personVehicleRelList.isEmpty()) {
			return R.data(PersonCache.getPersonById(null, personVehicleRelList.get(0).getPersonId()));
		} else {
			return R.data(null);
		}
	}

    @Override
    @GetMapping(REL_VEHICLE)
    public R<List<PersonVehicleRel>> getVehicleByPersonId(Long personId) {
        return R.data(personVehicleRelService.getVehicleByPersonId(personId));
    }

	@Override
	@PostMapping(UNBIND_PERSON)
	public R<Boolean> unbindPerson(Long vehicleId) {
		return R.data(personVehicleRelService.unbindPerson(vehicleId));
	}

	@Override
	@PostMapping(UNBIND_VEHICLE)
	public R<Boolean> unbindVehicle(Long personId) {
		return R.data(personVehicleRelService.unbindVehicle(personId));
	}

	@Override
	public R<Boolean> bindVehicle(String personId, String vehicleId,String tenantId) {
    	Person person  = PersonCache.getPersonById(tenantId,Long.parseLong(personId));
    	PersonVehicleRel personVehicleRel = new PersonVehicleRel();
		personVehicleRel.setPersonId(Long.parseLong(personId));
		personVehicleRel.setPersonName(person.getPersonName());
		personVehicleRel.setVehicleId(Long.parseLong(vehicleId));
		personVehicleRelService.save(personVehicleRel);
		return R.data(true);
	}

	@Override
	@GetMapping(CURRENT_DRIVER)
	public R<Person> getCurrentDriver(Long vehicleId) {
		List<PersonVehicleRel> personVehicleRelList = personVehicleRelService.getPersonByVehicle(vehicleId);
		if (personVehicleRelList == null || personVehicleRelList.isEmpty()) {
			return R.data(null);
		}
		if (personVehicleRelList.size() == 1) {
			return R.data(PersonCache.getPersonById(null, personVehicleRelList.get(0).getPersonId()));
		}
		Person person = PersonCache.getPersonById(null, personVehicleRelList.get(0).getPersonId());// 默认最新的一个
		for (PersonVehicleRel personVehicleRel : personVehicleRelList) {
			Long personId = personVehicleRel.getPersonId();
			if (scheduleClient.checkTodayNeedWork(personId, ArrangeConstant.ScheduleObjectEntityType.PERSON).getData()) {
				person = PersonCache.getPersonById(null, personId);
				break;
			}
		}
		return R.data(person);
	}

	@Override
	public R<List<PersonVehicleRel>> listAll() {
		return R.data(personVehicleRelService.list());
	}
}
