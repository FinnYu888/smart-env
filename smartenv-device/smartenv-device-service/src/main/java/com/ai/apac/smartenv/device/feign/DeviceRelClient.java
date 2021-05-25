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

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 驾驶员 服务Feign实现类
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class DeviceRelClient implements IDeviceRelClient {

    private IDeviceRelService deviceRelService;

    private IDeviceInfoService deviceInfoService;


    @Override
    public R<List<DeviceInfo>> getDevicesByEntityList(List<Long> entityList, Long entitytype) {
        List<DeviceRel> deviceRelList = deviceRelService.listDeviceRelsByEntity(entityList,entitytype,null);
        if(ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() >0){
            List<String> ids = new ArrayList<String>();
            deviceRelList.forEach(deviceRel -> {
                ids.add(deviceRel.getDeviceId().toString());
            });
            return R.data(deviceInfoService.listDevicesByParam(ids,null));
        }
        return R.data(null);
    }

    @Override
    public R<Boolean> bindDevice(String entityType, Long entityId, String deviceIds) {
        return R.data(deviceInfoService.bindDevice(entityType,entityId,deviceIds));
    }

    @Override
    @GetMapping(REL_DEVICE)
    public R<List<DeviceRel>> getEntityRels(Long entityId,Long entityType) {
        return R.data(deviceRelService.getDeviceRelsByEntity(entityId,entityType));
    }

    /**
     * 根据实体获取设备绑定关系
     *
     * @param entityId
     * @return
     */
    @Override
    @GetMapping(GET_ENTITY_REL)
    public R<List<DeviceRel>> getEntityRels(Long entityId) {
        List<DeviceRel> list = deviceRelService.list(new LambdaQueryWrapper<DeviceRel>().eq(DeviceRel::getEntityId,entityId));
        return R.data(list);
    }

    @Override
    @GetMapping(GET_ENTITY_REL_BY_CATEGORY)
    public R<List<Long>> getEntityRelsByCategory(String tenantId,String categoryId,String deviceStatus) {
        return R.data(deviceRelService.getEntityRelsByCategory(tenantId,categoryId,deviceStatus));
    }

    @Override
    @GetMapping(REL_GET_BY_DEVICE_ID)
    public R<DeviceRel> getDeviceRelByDeviceId(@RequestParam("deviceId") Long deviceId) {
        DeviceRel deviceRel = new DeviceRel();
        if (deviceId == null) {
            return R.data(deviceRel);
        }
        deviceRel.setDeviceId(deviceId);
        return R.data(deviceRelService.getOne(Condition.getQueryWrapper(deviceRel)));
    }

    @Override
    public R<List<DeviceRel>> getDeviceRelsByDeviceIds(List<Long> deviceIds) {
        QueryWrapper<DeviceRel> deviceRelQueryWrapper = new QueryWrapper<DeviceRel>();
        deviceRelQueryWrapper.lambda().in(DeviceRel::getDeviceId,deviceIds);
        return R.data(deviceRelService.list(deviceRelQueryWrapper));
    }

    @Override
    public R<List<DeviceRel>> getDeviceRelsByDeviceCodes(List<String> deviceCodes, String tenantId) {
        QueryWrapper<DeviceInfo> deviceInfoQueryWrapper = new QueryWrapper<DeviceInfo>();
        deviceInfoQueryWrapper.lambda().in(DeviceInfo::getDeviceCode,deviceCodes);
        deviceInfoQueryWrapper.lambda().eq(DeviceInfo::getTenantId,tenantId);
        List<DeviceInfo> deviceInfoList =  deviceInfoService.list(deviceInfoQueryWrapper);
        if(ObjectUtil.isNotEmpty(deviceInfoList) && deviceInfoList.size() > 0){
            List<Long> deviceIdList = deviceInfoList.stream().map(deviceInfo -> deviceInfo.getId()).collect(Collectors.toList());
            return this.getDeviceRelsByDeviceIds(deviceIdList);
        }else{
            return R.data(null);
        }

    }

    @Override
    @GetMapping(REL_GET_BY_DEVICE_COUNT)
    public R<Map<Long,Long>> getDeviceCount(List<Long> entityIdList, Long entityType) {
        return R.data(deviceRelService.getDeviceCount(entityIdList,entityType));
    }

//    @Override
//    @GetMapping(REL_GET_BY_DEVICEID)
//    public R<List<DeviceRel>> getByDeviceId(Long deviceId, Long entityType) {
//        DeviceRel deviceRel=new DeviceRel();

//        deviceRel.setDeviceId(deviceId);
//        deviceRel.setEntityType(entityType.toString());
//        return R.data(deviceRelService.list(Condition.getQueryWrapper(deviceRel)));
//    }


    /**
     * 按租户获取所有设备与实体的绑定关系
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_TENANT_REL_DEVICE)
    public R<List<DeviceRel>> getTenantDeviceRel(@RequestParam("tenantId") String tenantId) {
        List<DeviceRel> list = deviceRelService.list(new LambdaQueryWrapper<DeviceRel>().eq(DeviceRel::getTenantId,tenantId));
        return R.data(list);
    }
}
