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

import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleStatusStatDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IVehicleClientFallback implements IVehicleClient {


    @Override
    public R<Integer> countVehicleByTenantId(String tenantId, String deviceStatus) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Boolean> vehicleInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<VehicleInfo> vehicleInfoById(Long vehicleId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<VehicleInfo>> vehicleInfoByDeptId(Long deptId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<VehicleInfo>> getVehicleByTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据部门获取车辆数量
     *
     * @param deptId id
     * @return
     */
    @Override
    public R<Integer> vehicleCountByDeptId(Long deptId) {
        return R.fail("获取数据失败");
    }

    /**
     * 实时获取当天车辆出勤统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<VehicleStatusStatDTO> getVehicleStatusStatToday(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<VehicleInfo>> listVehicleByCondition(VehicleInfoDTO vehicleInfoDTO) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取租户状态为正常的车辆数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<Integer> getNormalVehicleCountByTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<VehicleInfo> getVehicleInfoById(Long vehicleId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<VehicleInfo>> listVehicle(VehicleInfoVO vehicle) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<VehicleNode>> treeByDept(String nodeName, String tenantId, String entityIdStr) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Integer> updateVehicleAccstateById(Long accState, Long vehicleId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R updateVehicleStateById(Integer vehicleState, Long vehicleId) {
        return R.fail("获取数据失败");
    }

    /**
     * 实时获取当天车辆设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<VehicleDeviceStatusCountDTO> getVehicleDeviceStatusStat(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 批量实时获取当天车辆设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<VehicleDeviceStatusCountDTO>> listVehicleDeviceStatusStat(String tenantId) {
        return R.fail("获取数据失败");
    }
}
