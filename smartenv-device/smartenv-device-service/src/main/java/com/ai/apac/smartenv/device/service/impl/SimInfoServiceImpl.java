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

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.dto.SimInfoDTO;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.SimInfo;
import com.ai.apac.smartenv.device.entity.SimRel;
import com.ai.apac.smartenv.device.feign.ISimClient;
import com.ai.apac.smartenv.device.mapper.DeviceInfoMapper;
import com.ai.apac.smartenv.device.service.IDeviceInfoService;
import com.ai.apac.smartenv.device.service.ISimRelService;
import com.ai.apac.smartenv.device.vo.SimInfoVO;
import com.ai.apac.smartenv.device.mapper.SimInfoMapper;
import com.ai.apac.smartenv.device.service.ISimInfoService;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.AuthClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * SIM卡信息 服务实现类
 *
 * @author Blade
 * @since 2020-05-08
 */
@Service
@AllArgsConstructor
public class SimInfoServiceImpl extends BaseServiceImpl<SimInfoMapper, SimInfo> implements ISimInfoService {

    private ISimRelService simRelService;

    private ISimClient simClient;

    private DeviceInfoMapper deviceInfoService;
    
    @Override
    public IPage<SimInfo> selectSimInfoPage(Query query, SimInfo simInfo) {
        QueryWrapper<SimInfo> queryWrapper = new QueryWrapper<SimInfo>();

        if (StringUtil.isNotBlank(simInfo.getSimType())) {
            queryWrapper.lambda().eq(SimInfo::getSimType, simInfo.getSimType());
        }
        if (StringUtil.isNotBlank(simInfo.getSimCode())) {
            queryWrapper.lambda().like(SimInfo::getSimCode, simInfo.getSimCode());
        }

        if (StringUtil.isNotBlank(simInfo.getSimNumber())) {
            queryWrapper.lambda().like(SimInfo::getSimNumber, simInfo.getSimNumber());
        }
        return baseMapper.selectPage(Condition.getPage(query), queryWrapper);
    }

    private String getSimCode2(){
        QueryWrapper<SimInfo> wrapper = new QueryWrapper<SimInfo>();
        String num = RandomUtil.randomNumbers(12);
        wrapper.lambda().eq(SimInfo::getSimCode2,num);
        if(this.list(wrapper).size() > 0 ){
            return getSimCode2();
        }else{
            return num;
        }
    }

    @Override
    public Boolean saveOrUpdateSimInfo(SimInfoDTO simInfoDTO) {
        if (checkSimInfo(simInfoDTO)) {
            SimInfo simInfo = Objects.requireNonNull(BeanUtil.copy(simInfoDTO, SimInfo.class));
            if(ObjectUtil.isEmpty(simInfo.getId())){
                simInfo.setSimCode2(getSimCode2());
            }
            this.saveOrUpdate(simInfo);
//            if (StringUtil.isNotBlank(simInfoDTO.getDeviceId())) {
//                String deviceId = simInfoDTO.getDeviceId();
//                SimRel simRel = new SimRel();
//                simRel.setDeviceId(deviceId);
//                simRel.setSimId(simInfo.getId());
//                SimRel simRel1 = simRelService.selectSimRelBySimId(simInfo.getId());
//                if (null == simRel1 || null == simRel1.getId()) {
//                    simRelService.saveOrUpdate(simRel);
//                } else if (null != simRel1 && null != simRel1.getId() & !simRel1.getDeviceId().equals(deviceId)) {
//                    simRel.setId(simRel1.getId());
//                    simRelService.saveOrUpdate(simRel);
//                }
//            } else {
//                QueryWrapper<SimRel> queryWrapper2 = new QueryWrapper<SimRel>();
//                queryWrapper2.lambda().eq(SimRel::getSimId, simInfo.getId());
//                simRelService.remove(queryWrapper2);
//            }
        }
        return true;
    }

    @Override
    public Boolean removeSimInfo(String ids) {
        List<Long> idList = Func.toLongList(ids);
        this.removeByIds(idList);
        QueryWrapper<SimRel> queryWrapper2 = new QueryWrapper<SimRel>();
        queryWrapper2.lambda().in(SimRel::getSimId, idList);
        simRelService.remove(queryWrapper2);
        return true;
    }

    private boolean checkSimInfo(SimInfoDTO simInfoDTO) {
        if (StringUtil.isBlank(simInfoDTO.getSimType())) {
            throw new ServiceException("SIM卡类型不能为空");
        }
        if (StringUtil.isBlank(simInfoDTO.getSimCode())) {
            throw new ServiceException("SIM卡号不能为空");
        }
        if (StringUtil.isBlank(simInfoDTO.getSimNumber())) {
            throw new ServiceException("SIM卡电话号码不能为空");
        }
        SimInfo simInfo1 = simClient.getSimBySimCode(simInfoDTO.getSimCode()).getData();
        if(ObjectUtil.isNotEmpty(simInfo1) && ObjectUtil.isNotEmpty(simInfo1.getId()) && !simInfo1.getId().toString().equals(simInfoDTO.getId() == null ? "" : simInfoDTO.getId().toString())){
                throw new ServiceException(StrUtil.format("SIM卡号[{}]不能重复", simInfoDTO.getSimCode()));
        }

        SimInfo simInfo2 = simClient.getSimBySimNumber(simInfoDTO.getSimNumber()).getData();
        if(ObjectUtil.isNotEmpty(simInfo2) && ObjectUtil.isNotEmpty(simInfo2.getId()) &&!simInfo2.getId().toString().equals(simInfoDTO.getId() == null ? "" : simInfoDTO.getId().toString())){
            throw new ServiceException(StrUtil.format("SIM卡号码[{}]不能重复", simInfoDTO.getSimNumber()));
        }

        if (!StringUtil.isBlank(simInfoDTO.getDeviceId())) {
            String deviceId = simInfoDTO.getDeviceId();

            QueryWrapper<SimRel> queryWrapper2 = new QueryWrapper<SimRel>();
            queryWrapper2.lambda().eq(SimRel::getDeviceId, deviceId);
            SimRel simRel = simRelService.getOne(queryWrapper2);
            if (null != simRel && null != simRel.getId() && !simRel.getSimId().toString().equals(simInfoDTO.getId() == null ? "" : simInfoDTO.getId().toString())) {
                throw new ServiceException(StrUtil.format("该设备[{}]已绑定SIM卡，不能重复绑定", deviceId));
            }
        }

        return true;
    }

    /**
     * 根据设备主键查询绑定的SIM卡信息
     *
     * @param deviceId
     * @return
     */
    @Override
    public SimInfo getSimByDeviceId(Long deviceId) {
        DeviceInfo deviceInfo = deviceInfoService.selectById(deviceId);
        if (deviceInfo == null) {
            return null;
        }
        SimRel simRel = simRelService.getOne(new LambdaQueryWrapper<SimRel>().eq(SimRel::getDeviceId,deviceId));
        if (simRel == null || simRel.getId() == null) {
            return null;
        }
        return baseMapper.selectById(simRel.getSimId());
    }

    /**
     * 根据设备CODE查询绑定的SIM卡信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    public SimInfo getSimByDeviceCode(String deviceCode) {
        DeviceInfo deviceInfo = DeviceCache.getDeviceByCode(AuthUtil.getTenantId(),deviceCode);
        if(!ObjectUtil.isNotEmpty(deviceInfo)){
            return null;
        }
        Long deviceId = deviceInfo.getId();
        SimRel simRel = simRelService.getOne(new LambdaQueryWrapper<SimRel>().eq(SimRel::getDeviceId, deviceId.toString()));
        if (simRel == null || simRel.getId() == null) {
            return null;
        }
        return baseMapper.selectById(simRel.getSimId());
    }

    @Override
    public List<SimInfo> listUnBindSim(SimInfo simInfo) {
        return baseMapper.listUnBindSimInfo(simInfo.getSimCode(),simInfo.getSimNumber(),simInfo.getSimType(), AuthUtil.getTenantId());
    }

    @Override
    public SimRel getSimInfoBySimCode2(String simCode2) {
        QueryWrapper<SimInfo> queryWrapper = new QueryWrapper<SimInfo>();
        queryWrapper.lambda().eq(SimInfo::getSimCode2, simCode2);
        SimInfo simInfo = this.getOne(queryWrapper);
        if (simInfo == null || simInfo.getId() == null) {
            return null;
        }
        return simRelService.selectSimRelBySimId(simInfo.getId());
    }
}
