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
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign接口类
 *
 * @author zhanglei25
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_PERSON_NAME,
	fallback = IPersonVehicleRelClientFallback.class
)
public interface IPersonVehicleRelClient {

	String API_PREFIX = "/client";
	String LIST_ALL = API_PREFIX + "/list-all";
	String REL_PERSONS = API_PREFIX + "/rel-persons";
	String REL_PERSON = API_PREFIX + "/rel-person";
	String REL_DRIVER = API_PREFIX + "/rel-deriver";
	String REL_VEHICLE = API_PREFIX + "/rel-vehicle";
	String LIST_DRIVER = API_PREFIX + "/list-deriver";
	String UNBIND_PERSON = API_PREFIX + "/unbind-person";
	String UNBIND_VEHICLE = API_PREFIX + "/unbind-vehicle";
	String BIND_VEHICLE = API_PREFIX + "/bind-vehicle";
	String CURRENT_DRIVER = API_PREFIX + "/current-driver";


	/**
	 * 获取驾驶员关联列表
	 *
	 * @param vehicleIds 车ID
	 * @return
	 */
	@PostMapping(REL_PERSONS)
	R<Map<Long, List<PersonVehicleRel>>> getPersonsVehicleRels(@RequestBody List<Long> vehicleIds);
	/**
	 * 获取驾驶员关联列表
	 *
	 * @param vehicleId 车ID
	 * @return
	 */
	@GetMapping(REL_PERSON)
	R<List<PersonVehicleRel>> getPersonVehicleRels(@RequestParam("vehicleId") Long vehicleId);
	/**
	 * 获取驾驶员列表
	 *
	 * @param vehicleId 车ID
	 * @return
	 */
	@GetMapping(LIST_DRIVER)
	R<List<Person>> listDriverByVehicleId(@RequestParam("vehicleId") Long vehicleId);


	/**
	 * 获取第一个驾驶员
	 *
	 * @param vehicleId 车ID
	 * @return
	 */
	@GetMapping(REL_DRIVER)
	R<Person> getDriverByVehicleId(@RequestParam("vehicleId") Long vehicleId);


	/**
	 * 获取人驾驶的所有车
	 *
	 * @param personId 人ID
	 * @return
	 */
	@GetMapping(REL_VEHICLE)
	R<List<PersonVehicleRel>> getVehicleByPersonId(@RequestParam("personId") Long personId);

	/**
	 * 
	 * @Function: IPersonVehicleRelClient::unbindPerson
	 * @Description: 解绑车辆驾驶员
	 * @param vehicleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午2:54:12 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	@PostMapping(UNBIND_PERSON)
	R<Boolean> unbindPerson(@RequestParam("vehicleId") Long vehicleId);

	/**
	 * 
	 * @Function: IPersonVehicleRelClient::unbindVehicle
	 * @Description: 解绑人员车辆
	 * @param personId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午2:54:26 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	@PostMapping(UNBIND_VEHICLE)
	R<Boolean> unbindVehicle(@RequestParam("personId") Long personId);

	@PostMapping(BIND_VEHICLE)
	R<Boolean> bindVehicle(@RequestParam("personId") String personId,@RequestParam("vehicleId") String vehicleId,@RequestParam("tenantId") String tenantId);


	/**
	 * 
	 * @Function: IPersonVehicleRelClient::getCurrentDriver
	 * @Description: 车辆今天排班的驾驶员
	 * @param vehicleId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年3月3日 下午2:54:42 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	@GetMapping(CURRENT_DRIVER)
	R<Person> getCurrentDriver(@RequestParam("vehicleId") Long vehicleId);

	@GetMapping(LIST_ALL)
	R<List<PersonVehicleRel>> listAll();

}
