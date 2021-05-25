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
package com.ai.apac.smartenv.device.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign接口类
 *
 * @author zhanglei25
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_DEVICE_NAME,
	fallback = IDeviceRelClientFallback.class
)
public interface IDeviceRelClient {

	String API_PREFIX = "/client";
	String REL_DEVICE = API_PREFIX + "/rel-device";
	String GET_ENTITY_REL = API_PREFIX + "/entity-rel-device";
	String GET_ENTITY_REL_BY_CATEGORY = API_PREFIX + "/entity-rel-by-category";
	String GET_TENANT_REL_DEVICE = API_PREFIX + "/tenant-rel-device";

	String REL_GET_BY_DEVICE_ID= API_PREFIX + "/device-rel-by-id";
	String RELS_GET_BY_DEVICE_IDS= API_PREFIX + "/device-rels-by-ids";
	String REL_GET_BY_DEVICE_COUNT = API_PREFIX + "/device-count";
	String BIND_DEVICE = API_PREFIX + "/bind-device";

	String GET_DEVICES_BY_ENTITY_LIST = API_PREFIX + "/devices-by-entity-list";

	String RELS_GET_BY_DEVICE_CODES = API_PREFIX + "/device-rels-by-codes";


	@GetMapping(GET_DEVICES_BY_ENTITY_LIST)
	R<List<DeviceInfo>> getDevicesByEntityList(@RequestParam("entityList") List<Long> entityList,@RequestParam("entityType") Long entitytype);

	/*
	 * @param entityId 实体ID，entityType 实体类型
	 * @return
	 */
	@GetMapping(BIND_DEVICE)
	R<Boolean> bindDevice(@RequestParam("entityType") String entityType,@RequestParam("entityId") Long entityId,@RequestParam("deviceIds") String deviceIds);

	@GetMapping(REL_DEVICE)
	R<List<DeviceRel>> getEntityRels(@RequestParam("entityId") Long entityId,@RequestParam("entityType") Long entityType);

	/**
	 * 根据实体获取设备绑定关系
	 * @param entityId
	 * @return
	 */
	@GetMapping(GET_ENTITY_REL)
	R<List<DeviceRel>> getEntityRels(@RequestParam("entityId") Long entityId);

	@GetMapping(GET_ENTITY_REL_BY_CATEGORY)
	R<List<Long>> getEntityRelsByCategory(@RequestParam("tenantId") String tenantId,@RequestParam("categoryId") String categoryId,@RequestParam("deviceStatus") String deviceStatus);

	@GetMapping(REL_GET_BY_DEVICE_ID)
	R<DeviceRel> getDeviceRelByDeviceId(@RequestParam("deviceId") Long deviceId);

	@GetMapping(RELS_GET_BY_DEVICE_IDS)
	R<List<DeviceRel>> getDeviceRelsByDeviceIds(@RequestParam("deviceIds") List<Long> deviceIds);

	@GetMapping(RELS_GET_BY_DEVICE_CODES)
	R<List<DeviceRel>> getDeviceRelsByDeviceCodes(@RequestParam("deviceCodes") List<String> deviceCodes,@RequestParam("tenantId") String tenantId);
	/**
	* 获取设施绑定终端数量
	*/
	@GetMapping(REL_GET_BY_DEVICE_COUNT)
	R<Map<Long,Long>> getDeviceCount(@RequestParam("entityIdList") List<Long> entityIdList, @RequestParam("entityType") Long entityType);

	/**
	 * 按租户获取所有设备与实体的绑定关系
	 * @return
	 */
	@GetMapping(GET_TENANT_REL_DEVICE)
	R<List<DeviceRel>> getTenantDeviceRel(@RequestParam("tenantId") String tenantId);
}
