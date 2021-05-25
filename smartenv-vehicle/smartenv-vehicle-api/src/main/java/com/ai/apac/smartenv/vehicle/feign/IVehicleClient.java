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
package com.ai.apac.smartenv.vehicle.feign;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleStatusStatDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * User Feign接口类
 *
 * @author yupf3
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_VEHICLE_NAME
)
public interface IVehicleClient {

	String API_PREFIX = "/client";
	String VEHICLE_INFO_BY_ID = API_PREFIX + "/vehicle-info-by-id";
	String LIST_VEHICLE = API_PREFIX + "/list-vehicle";
	String VEHICLE_INFO_BY_DEPT_ID = API_PREFIX + "/vehicle-info-by-deptId";
	String VEHICLE_COUNT_BY_DEPT_ID = API_PREFIX + "/vehicle-count-by-deptId";
	String GET_TENANT_VEHICLE = API_PREFIX + "/get-tenant-vehicle";
	String GET_TENANT_VEHICLE_COUNT = API_PREFIX + "/get-tenant-vehicle-count";
	String GET_VEHICLE_STATUS_STAT = API_PREFIX + "/get-vehicleStatus";
	String GET_VEHICLE_DEVICE_STATUS_STAT = API_PREFIX + "/get-vehicleDeviceStatus";
	String LIST_VEHICLE_DEVICE_STATUS_STAT = API_PREFIX + "/list-vehicleDeviceStatus";
	String GET_VEHICLE_BY_ID = API_PREFIX + "/get-vehicle-by-id";
	String LIST_VEHICLE_BY_WRAPPER = API_PREFIX + "/list-vehicle-by-wrapper";
	String TREE_BY_DEPT = API_PREFIX + "/tree-by-dept";
	String UPDATE_VEHICLE_BY_ID = API_PREFIX + "/update-vehicle-by-id";
	String UPDATE_VEHICLE_STATE_BYID = API_PREFIX + "/update-vehicle-state-byid";

	String VEHICLE_INFO_ASYNC = API_PREFIX + "/vehicle-info-async";
	String COUNT_VEHICLE_BY_TENANTIDS = API_PREFIX + "/count-vehicle-by-tenantIds";


	@PostMapping(COUNT_VEHICLE_BY_TENANTIDS)
	R<Integer> countVehicleByTenantId(@RequestBody String tenantId,@RequestParam("deviceStatus") String deviceStatus);


	@PostMapping(VEHICLE_INFO_ASYNC)
	R<Boolean> vehicleInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType);

	/**
	 * 获取车辆信息
	 *
	 * @param vehicleId 车辆id
	 * @return
	 */
	@GetMapping(VEHICLE_INFO_BY_ID)
	R<VehicleInfo> vehicleInfoById(@RequestParam("vehicleId") Long vehicleId);

	@GetMapping(LIST_VEHICLE)
	R<List<VehicleInfo>> listVehicle(VehicleInfoVO vehicle);

	/**
	 * 根据部门获取车辆信息
	 *
	 * @param deptId id
	 * @return
	 */
	@GetMapping(VEHICLE_INFO_BY_DEPT_ID)
	R<List<VehicleInfo>> vehicleInfoByDeptId(@RequestParam("deptId") Long deptId);

	/**
	 * 根据部门获取车辆数量
	 *
	 * @param deptId id
	 * @return
	 */
	@GetMapping(VEHICLE_COUNT_BY_DEPT_ID)
	R<Integer> vehicleCountByDeptId(@RequestParam("deptId") Long deptId);

	/**
	 * 
	 * @Function: IVehicleClient::getVehicleByTenant
	 * @Description: 根据租户获取车辆信息列表
	 * @param tenantId
	 * @return
	 * @version: v1.0.0
	 * @author: zhaoaj
	 * @date: 2020年2月26日 下午2:20:07 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 */
	@GetMapping(GET_TENANT_VEHICLE)
	R<List<VehicleInfo>> getVehicleByTenant(@RequestParam("tenantId") String tenantId);

	/**
	 * 获取租户状态为正常的车辆数量
	 * @param tenantId
	 * @return
	 */
	@GetMapping(GET_TENANT_VEHICLE_COUNT)
	R<Integer> getNormalVehicleCountByTenant(@RequestParam("tenantId") String tenantId);

	/**
	 * 实时获取当天车辆出勤统计
	 * @param tenantId
	 * @return
	 */
	@GetMapping(GET_VEHICLE_STATUS_STAT)
	R<VehicleStatusStatDTO> getVehicleStatusStatToday(@RequestParam("tenantId") String tenantId);

	/**
	 * 实时获取当天车辆设备状态统计
	 * @param tenantId
	 * @return
	 */
	@GetMapping(GET_VEHICLE_DEVICE_STATUS_STAT)
	R<VehicleDeviceStatusCountDTO> getVehicleDeviceStatusStat(@RequestParam("tenantId") String tenantId);

	/**
	 * 批量实时获取当天车辆设备状态统计
	 * @param tenantId
	 * @return
	 */
	@GetMapping(LIST_VEHICLE_DEVICE_STATUS_STAT)
	R<List<VehicleDeviceStatusCountDTO>> listVehicleDeviceStatusStat(@RequestParam("tenantId") String tenantId);


	@PostMapping(LIST_VEHICLE_BY_WRAPPER)
	R<List<VehicleInfo>> listVehicleByCondition(@RequestBody VehicleInfoDTO vehicleInfoDTO);

	@GetMapping(GET_VEHICLE_BY_ID)
	R<VehicleInfo> getVehicleInfoById(@RequestParam("vehicleId")  Long vehicleId);

	@PostMapping(TREE_BY_DEPT)
	R<List<VehicleNode>> treeByDept(@RequestParam("nodeName") String nodeName, @RequestParam("tenantId") String tenantId, @RequestParam("entityIdStr") String entityIdStr);

	@GetMapping(UPDATE_VEHICLE_BY_ID)
	R<Integer> updateVehicleAccstateById(@RequestParam("accState")Long accState,@RequestParam("vehicleId") Long vehicleId);
	/**
	* 更新车辆状态
	* @author 66578
	*/
	@GetMapping(UPDATE_VEHICLE_STATE_BYID)
	R updateVehicleStateById(@RequestParam("vehicleState")Integer vehicleState,@RequestParam("vehicleId") Long vehicleId);
}
