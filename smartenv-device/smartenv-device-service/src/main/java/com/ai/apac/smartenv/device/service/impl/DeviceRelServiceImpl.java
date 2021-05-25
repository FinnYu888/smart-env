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
package com.ai.apac.smartenv.device.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.mapper.DeviceRelMapper;
import com.ai.apac.smartenv.device.service.IDeviceRelService;
import com.ai.apac.smartenv.device.vo.DeviceRelVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
public class DeviceRelServiceImpl extends BaseServiceImpl<DeviceRelMapper, DeviceRel> implements IDeviceRelService {

    @Override
    public IPage<DeviceRelVO> selectDeviceRelPage(IPage<DeviceRelVO> page, DeviceRelVO deviceRel) {
        return page.setRecords(baseMapper.selectDeviceRelPage(page, deviceRel));
    }

    @Override
    public void saveOrUpdateDeviceRel(DeviceRel deviceRel) {
        QueryWrapper<DeviceRel> deviceRelQueryWrapper = new QueryWrapper<DeviceRel>();
        deviceRelQueryWrapper.lambda().eq(DeviceRel::getDeviceId,deviceRel.getDeviceId());
        deviceRelQueryWrapper.lambda().eq(DeviceRel::getEntityId,deviceRel.getEntityId());
        DeviceRel deviceRel1 = baseMapper.selectOne(deviceRelQueryWrapper);
        if(!ObjectUtils.isEmpty(deviceRel1)){
            deviceRel.setId(deviceRel1.getId());
        }
        saveOrUpdate(deviceRel);
    }

    @Override
    public List<DeviceRel> getDeviceRelsByEntity(Long entityId, Long entityType) {
        QueryWrapper<DeviceRel> queryWrapper = new QueryWrapper<DeviceRel>();
        queryWrapper.eq("entity_id",entityId).eq("entity_type",entityType);
        List<DeviceRel> relList = baseMapper.selectList(queryWrapper);
        return relList;
    }

    @Override
    public List<DeviceRel> getDeviceRelByDeviceId(Long deviceId) {
        QueryWrapper<DeviceRel> queryWrapper = new QueryWrapper<DeviceRel>();
        queryWrapper.eq("device_id",deviceId);
        List<DeviceRel> relList = baseMapper.selectList(queryWrapper);
        return relList;
    }

    public List<DeviceRel> listForBinding(DeviceInfo deviceInfo, Query query, Long vehicleId) {
        Integer current = query.getCurrent();
        Integer size = query.getSize();
        if (current == null) {
            current = 0;
        }
        if (size == null) {
            size = 0;
        }
        return baseMapper.listForBinding(deviceInfo, (current - 1) * size, size, vehicleId);
    }

    /**
     * 根据实体Id或者实体类型或者实体分类查终端
     * @param entityIdList
     * @param entityType
     * @param entityCategoryIdList
     * @return
     */
    @Override
    public List<DeviceRel> listDeviceRelsByEntity(List<Long> entityIdList, Long entityType, List<Long> entityCategoryIdList) {
        QueryWrapper<DeviceRel> queryWrapper = new QueryWrapper<>();
        List<DeviceRel> relList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(entityIdList)) {
            queryWrapper.in("entity_id", entityIdList);
        }
        if (CollectionUtil.isNotEmpty(entityCategoryIdList)) {
            queryWrapper.in("entity_category_id", entityCategoryIdList);
        }
        if (entityType != null) {
            queryWrapper.eq("entity_type", entityType);
        }
        if (queryWrapper != null) {
            relList = baseMapper.selectList(queryWrapper);
        }
        return relList;
    }

    @Override
    public List<Long> getEntityRelsByCategory(String tenantId, String categoryId, String deviceStatus) {
        List<Long> entityIdList = new ArrayList<Long>();
        if(DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus)){
            deviceStatus = null;
        }
        if (StringUtil.isBlank(categoryId)) {
        	categoryId = null;
		}
        List<DeviceRel> deviceRelList = baseMapper.getEntityRelsByCategory(tenantId,categoryId,deviceStatus);
        if(!ObjectUtils.isEmpty(deviceRelList) && deviceRelList.size() > 0){
            for(DeviceRel deviceRel:deviceRelList){
                entityIdList.add(deviceRel.getEntityId());
            }
        }
        return entityIdList;
    }

    @Override
    public Map<Long, Long> getDeviceCount(List<Long> entityIdList, Long entityType) {
        QueryWrapper<DeviceRel> queryWrapper = new QueryWrapper<DeviceRel>();
        queryWrapper.select("entity_id,COUNT(1) AS id");
        queryWrapper.in("entity_id",entityIdList).eq("entity_type",entityType);
        queryWrapper.groupBy("entity_id");
        List<DeviceRel> relList = baseMapper.selectList(queryWrapper);
        Map<Long,Long> deviceMap = new HashMap<>();
        if (null != relList && relList.size()>0&& null != relList.get(0)) {
            relList.forEach(deviceRel -> {
                deviceMap.put(deviceRel.getEntityId(),deviceRel.getId());
            });
        }
        return  deviceMap;
    }


    @Override
    public List<DeviceRel> listForEntityAndTime(Long entityId,Long entityType,  Long entityCategoryId, Timestamp startTime, Timestamp endTime) {
        return baseMapper.listForEntityAndTime(entityId,entityType,entityCategoryId,startTime,endTime);
    }

    @Override
    public List<DeviceRel> getModifyList(Long entityId,Long entityType, Long entityCategoryId, Timestamp startTime) {
        return baseMapper.getModifyList(entityId,entityType,entityCategoryId,startTime);
    }

}
