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
public class IDeviceRelClientFallback implements IDeviceRelClient {

    @Override
    public R<List<DeviceInfo>> getDevicesByEntityList(List<Long> entityList, Long entitytype) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Boolean> bindDevice(String entityType, Long entityId, String deviceIds) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceRel>> getEntityRels(Long entityId,Long entityType) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据实体获取设备绑定关系
     *
     * @param entityId
     * @return
     */
    @Override
    public R<List<DeviceRel>> getEntityRels(Long entityId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Long>> getEntityRelsByCategory(String tenantId,String categoryId,String deviceStatus) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<DeviceRel> getDeviceRelByDeviceId(Long deviceId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceRel>> getDeviceRelsByDeviceIds(List<Long> deviceIds) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<DeviceRel>> getDeviceRelsByDeviceCodes(List<String> deviceCodes, String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Map<Long, Long>> getDeviceCount(List<Long> entityIdList, Long entityType) {
        return R.fail("获取数据失败");
    }

    //    @Override
//    public R<List<DeviceRel>> getByDeviceId(Long deviceId, Long entityType) {
//            return R.fail("获取数据失败");
//    }


    /**
     * 按租户获取所有设备与实体的绑定关系
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<DeviceRel>> getTenantDeviceRel(String tenantId) {
        return R.fail("获取数据失败");
    }
}
