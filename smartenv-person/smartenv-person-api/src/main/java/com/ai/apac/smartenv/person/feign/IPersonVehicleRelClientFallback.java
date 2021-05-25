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
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component

public class IPersonVehicleRelClientFallback implements IPersonVehicleRelClient {

    @Override
    public R<List<PersonVehicleRel>> getPersonVehicleRels(Long vehicleId) {
        return R.fail("获取数据失败");
    }
	@Override
	public R<Map<Long, List<PersonVehicleRel>>> getPersonsVehicleRels(List<Long> vehicleIds) {
		return R.fail("获取数据失败");
	}

    @Override
    public R<List<Person>> listDriverByVehicleId(Long vehicleId) {
    	return R.fail("获取数据失败");
    }

    @Override
    public R<Person> getDriverByVehicleId(Long vehicleId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<PersonVehicleRel>> getVehicleByPersonId(Long personId) {
        return R.fail("获取数据失败");
    }

	@Override
	public R<Boolean> unbindPerson(Long vehicleId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> unbindVehicle(Long personId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Boolean> bindVehicle(String jobNumber, String vehicleId,String tenantId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<Person> getCurrentDriver(Long vehicleId) {
		return R.fail("获取数据失败");
	}

	@Override
	public R<List<PersonVehicleRel>> listAll() {
		return R.fail("获取数据失败");
	}

}
