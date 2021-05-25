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

import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleStatusStatDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.service.IVehicleAsyncService;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆服务Feign实现类
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
public class VehicleClient implements IVehicleClient {

    private IVehicleInfoService service;

    private IVehicleAsyncService vehicleAsyncService;

    @Override
    public R<Integer> countVehicleByTenantId(String tenantId, String deviceStatus) {
        QueryWrapper<VehicleInfo> wrapper = new QueryWrapper<VehicleInfo>();
        wrapper.lambda().eq(VehicleInfo::getTenantId,tenantId).eq(VehicleInfo::getIsUsed,VehicleConstant.VehicleState.IN_USED);
        if(ObjectUtil.isNotEmpty(deviceStatus)){
            wrapper.lambda().eq(VehicleInfo::getAccDeviceStatus,Long.parseLong(deviceStatus));
        }
        return R.data(service.count(wrapper));
    }

    @Override
    public R<Boolean> vehicleInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(vehicleAsyncService.thirdVehicleInfoAsync(datasList,tenantId,actionType,true));
    }

    @Override
    @GetMapping(VEHICLE_INFO_BY_ID)
    public R<VehicleInfo> vehicleInfoById(Long vehicleId) {
        return R.data(service.getById(vehicleId));
    }


    /**
     * 根据部门获取车辆信息
     *
     * @param deptId id
     * @return
     */
    @Override
    @GetMapping(VEHICLE_INFO_BY_DEPT_ID)
    public R<List<VehicleInfo>> vehicleInfoByDeptId(Long deptId) {
        return R.data(service.getVehicleInfoByDeptId(deptId));
    }

    @Override
    @GetMapping(GET_TENANT_VEHICLE)
    public R<List<VehicleInfo>> getVehicleByTenant(String tenantId) {
        return R.data(service.list(new LambdaQueryWrapper<VehicleInfo>().eq(VehicleInfo::getTenantId, tenantId)));
    }

    /**
     * 根据部门获取车辆数量
     *
     * @param deptId id
     * @return
     */
    @Override
    @GetMapping(VEHICLE_COUNT_BY_DEPT_ID)
    public R<Integer> vehicleCountByDeptId(Long deptId) {
        Integer count = service.count(new LambdaQueryWrapper<VehicleInfo>().eq(VehicleInfo::getDeptId, deptId));
        return R.data(count);
    }

    /**
     * 获取租户状态为正常的车辆数量
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_TENANT_VEHICLE_COUNT)
    public R<Integer> getNormalVehicleCountByTenant(String tenantId) {
        Integer count = service.count(new LambdaQueryWrapper<VehicleInfo>().eq(VehicleInfo::getTenantId, tenantId)
                .ne(VehicleInfo::getAccDeviceStatus, VehicleConstant.VehicleState.UN_USED)
                .eq(VehicleInfo::getIsDeleted, 0));
        return R.data(count);
    }

    /**
     * 实时获取当天车辆出勤统计
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_VEHICLE_STATUS_STAT)
    public R<VehicleStatusStatDTO> getVehicleStatusStatToday(@RequestParam String tenantId) {
        return R.data(service.getVehicleStatusStatToday(tenantId));
    }

    @Override
    @GetMapping(LIST_VEHICLE)
    public R<List<VehicleInfo>> listVehicle(VehicleInfoVO vehicleInfo) {
        List<VehicleInfo> list = service.listAll(vehicleInfo);
        return R.data(list);
    }

    @Override
    @PostMapping(LIST_VEHICLE_BY_WRAPPER)
    public R<List<VehicleInfo>> listVehicleByCondition(@RequestBody VehicleInfoDTO vehicleInfoDTO) {

        //车辆查询条件
        QueryWrapper<VehicleInfo> wrapper = Condition.getQueryWrapper(vehicleInfoDTO);
        if (vehicleInfoDTO != null && CollectionUtil.isNotEmpty(vehicleInfoDTO.getVehicleIds())) {
            wrapper.in("id", vehicleInfoDTO.getVehicleIds());
        }
        return R.data(service.list(wrapper));
    }


    @Override
    @GetMapping(GET_VEHICLE_BY_ID)
    public R<VehicleInfo> getVehicleInfoById(Long vehicleId) {
        return R.data(service.getById(vehicleId));
    }


    @Override
    @PostMapping(TREE_BY_DEPT)
    public R<List<VehicleNode>> treeByDept(String nodeName, String tenantId, String entityIdStr) {
        return R.data(service.treeByDept(nodeName, tenantId, Func.toLongList(entityIdStr)));
    }

    @Override
    @GetMapping(UPDATE_VEHICLE_BY_ID)
    public R<Integer> updateVehicleAccstateById(Long accState, Long vehicleId) {
        return R.data(service.updateVehicleAccstateById(accState, vehicleId));
    }

    @Override
    @GetMapping(UPDATE_VEHICLE_STATE_BYID)
    public R updateVehicleStateById(Integer vehicleState, Long vehicleId) {
        service.updateVehicleStateById(vehicleState, vehicleId);
        return R.status(true);
    }

    /**
     * 实时获取当天车辆设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_VEHICLE_DEVICE_STATUS_STAT)
    public R<VehicleDeviceStatusCountDTO> getVehicleDeviceStatusStat(@RequestParam String tenantId) {
        return R.data(service.getVehicleDeviceStatusCount(tenantId));
    }

    /**
     * 批量实时获取当天车辆设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<VehicleDeviceStatusCountDTO>> listVehicleDeviceStatusStat(String tenantId) {
        return R.data(service.listVehicleDeviceStatusCount(tenantId));
    }
}
