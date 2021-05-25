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
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.dto.mongo.GreenScreenDeviceDTO;
import com.ai.apac.smartenv.device.dto.third.DeviceInfoParamDTO;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.mapper.DeviceInfoMapper;
import com.ai.apac.smartenv.device.service.*;
import com.ai.apac.smartenv.device.vo.DeviceInfoVO;
import com.ai.apac.smartenv.device.vo.DeviceViewVO;
import com.ai.apac.smartenv.device.vo.PersonDeviceVO;
import com.ai.apac.smartenv.device.vo.VehicleDeviceVO;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 记录设备信息 服务实现类
 *
 * @author Blade
 * @since 2020-02-14
 */
@Service
@AllArgsConstructor
public class DeviceInfoServiceImpl extends BaseServiceImpl<DeviceInfoMapper, DeviceInfo> implements IDeviceInfoService {

    private IDeviceChannelService deviceChannelService;

    private IDeviceExtService deviceExtService;

    private IDeviceRelService deviceRelService;

    private IEntityCategoryClient entityCategoryClient;

    private IWorkareaRelClient workareaRelClient;

    private IPersonClient personClient;

    private ISysClient sysClient;

    private IVehicleClient vehicleClient;

    private ISimRelService simRelService;

    private ISimInfoService simInfoService;

    private MongoTemplate mongoTemplate;

    private IPolymerizationClient polymerizationClient;

    private IDataChangeEventClient dataChangeEventClient;




    @Override
    public boolean saveOrUpdateDeviceInfo(DeviceInfo entity) {
        boolean res = saveOrUpdate(entity);
        DeviceCache.delDeviceByEntity(entity);
        return res;
    }

    @Override
    public boolean updateVehicleDeviceInfo(VehicleDeviceVO vehicleDeviceVO) throws IOException {
        DeviceInfo deviceInfo = Objects.requireNonNull(BeanUtil.copy(vehicleDeviceVO, DeviceInfo.class));
        deviceInfo.setDeviceType(deviceInfo.getDeviceType().toUpperCase());
        boolean save = saveOrUpdateDeviceInfo(deviceInfo);

        Long id = vehicleDeviceVO.getId();

        //新增或更新SIM
        QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
        simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, id);
        SimRel simRel = simRelService.getOne(simRelQueryWrapper);
        String simOpr = "";
        String simCode2 = "";
        if (StringUtil.isBlank(vehicleDeviceVO.getSimId())) {
            if (ObjectUtil.isNotEmpty(simRel)) {
                simRelService.remove(simRelQueryWrapper);
                simOpr = "1";//删除sim卡
                simCode2 = simInfoService.getById(simRel.getSimId()).getSimCode2();
            }
        } else {
            if (ObjectUtil.isNotEmpty(simRel)) {
                if (!vehicleDeviceVO.getSimId().equals(simRel.getSimId())) {
                    simRel.setSimId(Long.parseLong(vehicleDeviceVO.getSimId()));
                    simRelService.updateById(simRel);
                    simOpr = "2";//更新sim卡
                } else {
                    //更新设备的其他信息不涉及sim卡（有可能什么信息都没更新）
                    simOpr = "4";//
                }
                simCode2 = simInfoService.getById(vehicleDeviceVO.getSimId()).getSimCode2();
            } else {
                SimRel simRel1 = new SimRel();
                simRel1.setSimId(Long.parseLong(vehicleDeviceVO.getSimId()));
                simRel1.setDeviceId(id.toString());
                simRelService.save(simRel1);
                simOpr = "3";//新增sim卡
                simCode2 = simInfoService.getById(vehicleDeviceVO.getSimId()).getSimCode2();
            }
        }

        DeviceExt deviceExt_ = new DeviceExt();
        deviceExt_.setDeviceId(id);
        deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.VEHICLE_DEVICE_ICCID);
        List<DeviceExt> deviceExtList_ = deviceExtService.getExtInfoByParam(deviceExt_);
        if (StringUtil.isNotBlank(vehicleDeviceVO.getAuthCode())) {
            //新增或更新ICCID
            if (deviceExtList_.size() > 0) {
                deviceExt_.setId(deviceExtList_.get(0).getId());
            }
            deviceExt_.setAttrName("鉴权码");
            deviceExt_.setAttrValue(vehicleDeviceVO.getAuthCode());
            deviceExt_.setAttrDisplayValue(vehicleDeviceVO.getAuthCode());
            deviceExtService.saveOrUpdate(deviceExt_);
        } else {
            //删除ICCID
            if (deviceExtList_.size() > 0) {
                deviceExtService.removeById(deviceExtList_.get(0).getId());
            }
        }

        List<DeviceChannel> deviceChannels = deviceChannelService.getChannelInfoByDeviceId(id);
        List<DeviceChannel> currentDeviceChannels = vehicleDeviceVO.getDeviceChannelList();
        if (null != deviceChannels && deviceChannels.size() > 0) {
            List<Long> channelIds = new ArrayList<Long>();
            deviceChannels.forEach(deviceChannel -> {
                channelIds.add(deviceChannel.getId());
            });
            if (null != vehicleDeviceVO.getDeviceChannelList() && vehicleDeviceVO.getDeviceChannelList().size() > 0) {

                //数据库有，页面有
                //0.过滤数据库有的，但是页面上没有了
                currentDeviceChannels.forEach(deviceChannelVO -> {
                    channelIds.remove(deviceChannelVO.getId());
                });
                //1.把数据库有的，但是页面上没有了的删了
                if (channelIds.size() > 0) {
                    deviceChannelService.removeByIds(channelIds);
                }
                //2.再把页面上的数据新增或更新
                currentDeviceChannels.forEach(deviceChannelVO -> {
                    deviceChannelVO.setDeviceId(id.toString());
                    deviceChannelService.saveOrUpdate(deviceChannelVO);
                });
            } else {
                //数据库有，页面没有-> 所有删除
                deviceChannelService.removeByIds(channelIds);
            }
        } else if (null != currentDeviceChannels && currentDeviceChannels.size() > 0) {
            //数据库没有，页面有-> 所有新增
            currentDeviceChannels.forEach(deviceChannelVO -> {
                deviceChannelVO.setDeviceId(id.toString());
                deviceChannelService.save(deviceChannelVO);
            });
        }

        if (ObjectUtil.isNotEmpty(vehicleDeviceVO.getCoordId())) {
            DeviceExt deviceExt__ = new DeviceExt();
            deviceExt__.setDeviceId(id);
            deviceExt__.setAttrId(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM);
            List<DeviceExt> deviceExtList1 = deviceExtService.getExtInfoByParam(deviceExt__);
            if (deviceExtList1.size() > 0) {
                if (!vehicleDeviceVO.getCoordId().equals(deviceExtList1.get(0).getAttrValueId().toString())) {
                    deviceExt__.setAttrValueId(Long.parseLong(vehicleDeviceVO.getCoordId()));
                    deviceExt__.setAttrName("坐标系");
                    deviceExt__.setAttrValue(vehicleDeviceVO.getCoordValue());
                    deviceExt__.setAttrDisplayValue(vehicleDeviceVO.getCoordValueName());
                    deviceExt__.setId(deviceExtList1.get(0).getId());
                    deviceExtService.updateById(deviceExt__);
                }
            } else {
                deviceExt__.setAttrValueId(Long.parseLong(vehicleDeviceVO.getCoordId()));
                deviceExt__.setAttrName("坐标系");
                deviceExt__.setAttrValue(vehicleDeviceVO.getCoordValue());
                deviceExt__.setAttrDisplayValue(vehicleDeviceVO.getCoordValueName());
                deviceExtService.save(deviceExt__);
            }
        }

        //如果保存成功 & 设备厂家是点创科技 那么同步设备信息给点创科技
        if (save && (DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE.equals(vehicleDeviceVO.getEntityCategoryId()) || DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE.equals(vehicleDeviceVO.getEntityCategoryId())) && DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleDeviceVO.getDeviceFactory()) && StringUtil.isNotBlank(simOpr)) {
            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_UPDATE_DEVICE_KEY);//默认更新
            DeviceInfoParamDTO deviceInfoParamDTO = new DeviceInfoParamDTO();
            QueryWrapper<DeviceExt> queryWrapper = new QueryWrapper();
            queryWrapper.lambda().eq(DeviceExt::getDeviceId, id);
            queryWrapper.lambda().eq(DeviceExt::getAttrId, DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID);
            DeviceExt deviceExt = deviceExtService.getOne(queryWrapper);
            String token = sysClient.getMiniCreateToken(false).getData();
            if (!StringUtil.isNotBlank(token)) {
                throw new ServiceException("获取点创科技Token失败");
            }
            if ("1".equals(simOpr)) {
                //删除sim卡
                value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_DELETE_DEVICE_KEY);
                deviceInfoParamDTO.setId(deviceExt.getAttrValue());
            }
            if ("2".equals(simOpr) || "4".equals(simOpr)) {
                //2:更新sim卡，4:sim卡不变，但deviceCode有可能更新了
                deviceInfoParamDTO.setDeviceID(vehicleDeviceVO.getDeviceCode());
                deviceInfoParamDTO.setSimNo(simCode2);
                deviceInfoParamDTO.setId(deviceExt.getAttrValue());
            }
            if ("3".equals(simOpr)) {
                //新增sim卡
                value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_ADD_DEVICE_KEY);
                deviceInfoParamDTO.setDeviceID(vehicleDeviceVO.getDeviceCode());
                deviceInfoParamDTO.setSimNo(simCode2);
            }

            com.alibaba.fastjson.JSONObject req = new com.alibaba.fastjson.JSONObject();
            req.put("Token", token);
            req.put("deviceInfoParam", deviceInfoParamDTO);
            String resStr = OkhttpUtil.postSyncJson(value.split(" ")[1], req.toJSONString()).body().string();
            if (ObjectUtil.isNotEmpty(resStr)) {
                JSONObject res = JSONUtil.parseObj(resStr);
                if (res.getInt("Result").equals(DeviceConstant.miniCreateResultCode.success)) {
                    if ("1".equals(simOpr)) {
                        deviceExtService.removeById(deviceExt.getId());
                    }
                    if ("3".equals(simOpr)) {
                        JSONArray deviceInfoParams = res.getJSONArray("deviceInfoParams");
                        String guid = deviceInfoParams.getJSONObject(0).getStr("id");
                        DeviceExt deviceExt1 = new DeviceExt();
                        deviceExt1.setDeviceId(id);
                        deviceExt1.setAttrId(DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID);
                        deviceExt1.setAttrName(DeviceConstant.DeviceExtAttrName.miniCreateDeviceGUID);
                        deviceExt1.setAttrValue(guid);
                        deviceExt1.setAttrDisplayValue(guid);
                        deviceExtService.save(deviceExt1);
                    }
                } else {
                    throw new ServiceException(StrUtil.format(DeviceConstant.miniCreateErrInfo.failInfo, res.getInt("Result")));
                }
            }
        }

        return save;
    }

    @Override
    public boolean saveVehicleDeviceInfo(VehicleDeviceVO vehicleDeviceVO) throws IOException {
        DeviceInfo deviceInfo = Objects.requireNonNull(BeanUtil.copy(vehicleDeviceVO, DeviceInfo.class));
        deviceInfo.setDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO));
        deviceInfo.setDeviceType(deviceInfo.getDeviceType().toUpperCase());

        boolean save = saveOrUpdateDeviceInfo(deviceInfo);

        Long id = deviceInfo.getId();

        //保存SIM卡号
        if (StringUtil.isNotBlank(vehicleDeviceVO.getSimId())) {

            SimRel simRel = new SimRel();
            simRel.setSimId(Long.parseLong(vehicleDeviceVO.getSimId()));
            simRel.setDeviceId(id.toString());
            save = simRelService.save(simRel);
        }

        //保存ICCID
        if (StringUtil.isNotBlank(vehicleDeviceVO.getAuthCode())) {
            DeviceExt deviceExt_ = new DeviceExt();
            deviceExt_.setDeviceId(id);
            deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.VEHICLE_DEVICE_ICCID);
            deviceExt_.setAttrName("鉴权码");
            deviceExt_.setAttrValue(vehicleDeviceVO.getAuthCode());
            deviceExt_.setAttrDisplayValue(vehicleDeviceVO.getAuthCode());
            deviceExtService.save(deviceExt_);
        }

        //保存坐标系
        if (StringUtil.isNotBlank(vehicleDeviceVO.getCoordId())) {
            DeviceExt deviceExt_ = new DeviceExt();
            deviceExt_.setDeviceId(id);
            deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM);
            deviceExt_.setAttrName("坐标系");
            deviceExt_.setAttrValueId(Long.parseLong(vehicleDeviceVO.getCoordId()));
            deviceExt_.setAttrValue(vehicleDeviceVO.getCoordValue());
            deviceExt_.setAttrDisplayValue(vehicleDeviceVO.getCoordValue());
            deviceExtService.save(deviceExt_);
        }

        if (null != vehicleDeviceVO.getDeviceChannelList() && vehicleDeviceVO.getDeviceChannelList().size() > 0) {
            vehicleDeviceVO.getDeviceChannelList().forEach(deviceChannel -> {
                deviceChannel.setDeviceId(id.toString());
                deviceChannelService.save(deviceChannel);
            });
        }

        //如果保存成功 & 设备厂家是点创科技 那么同步设备信息给点创科技
        if (save && (DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE.equals(vehicleDeviceVO.getEntityCategoryId()) || DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE.equals(vehicleDeviceVO.getEntityCategoryId())) && DeviceConstant.DeviceFactory.MINICREATE.equals(vehicleDeviceVO.getDeviceFactory()) && StringUtil.isNotBlank(vehicleDeviceVO.getSimId())) {
            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_ADD_DEVICE_KEY);
            DeviceInfoParamDTO deviceInfoParamDTO = new DeviceInfoParamDTO();
            deviceInfoParamDTO.setDeviceID(vehicleDeviceVO.getDeviceCode());
            String simCode2 = simInfoService.getById(vehicleDeviceVO.getSimId()).getSimCode2();
            deviceInfoParamDTO.setSimNo(simCode2);
            com.alibaba.fastjson.JSONObject req = new com.alibaba.fastjson.JSONObject();
            String token = sysClient.getMiniCreateToken(false).getData();
            if (!StringUtil.isNotBlank(token)) {
                throw new ServiceException("获取点创科技Token失败");
            }
            req.put("Token", token);
            req.put("deviceInfoParam", deviceInfoParamDTO);
            String resStr = OkhttpUtil.postSyncJson(value.split(" ")[1], req.toJSONString()).body().string();
            if (ObjectUtil.isNotEmpty(resStr)) {
                JSONObject res = JSONUtil.parseObj(resStr);
                if (DeviceConstant.miniCreateResultCode.success != res.getInt("Result")) {
                    throw new ServiceException(StrUtil.format(DeviceConstant.miniCreateErrInfo.failInfo, res.getInt("Result")));
                }
                JSONArray deviceInfoParams = res.getJSONArray("deviceInfoParams");
                String guid = deviceInfoParams.getJSONObject(0).getStr("id");
                DeviceExt deviceExt1 = new DeviceExt();
                deviceExt1.setDeviceId(id);
                deviceExt1.setAttrId(DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID);
                deviceExt1.setAttrName(DeviceConstant.DeviceExtAttrName.miniCreateDeviceGUID);
                deviceExt1.setAttrValue(guid);
                deviceExt1.setAttrDisplayValue(guid);
                deviceExtService.save(deviceExt1);
            }
        }
        return save;
    }


    @Override
    public boolean updatePersonDeviceInfo(PersonDeviceVO personDeviceVO) throws IOException {
        DeviceInfo deviceInfo = Objects.requireNonNull(BeanUtil.copy(personDeviceVO, DeviceInfo.class));
        deviceInfo.setDeviceType(deviceInfo.getDeviceType().toUpperCase());
        boolean save = this.saveOrUpdateDeviceInfo(deviceInfo);

        Long id = personDeviceVO.getId();

        QueryWrapper<SimRel> simRelQueryWrapper = new QueryWrapper<SimRel>();
        simRelQueryWrapper.lambda().eq(SimRel::getDeviceId, id);
        if (StringUtil.isBlank(personDeviceVO.getSimId())) {
            simRelService.remove(simRelQueryWrapper);
        } else {
            SimRel simRel = simRelService.getOne(simRelQueryWrapper);
            if (ObjectUtil.isNotEmpty(simRel)) {
                if (!personDeviceVO.getSimId().equals(simRel.getSimId())) {
                    simRel.setSimId(Long.parseLong(personDeviceVO.getSimId()));
                    simRelService.updateById(simRel);
                }
            } else {
                SimRel simRel1 = new SimRel();
                simRel1.setSimId(Long.parseLong(personDeviceVO.getSimId()));
                simRel1.setDeviceId(id.toString());
                simRelService.save(simRel1);
            }
        }
        DeviceExt deviceExt_ = new DeviceExt();
        deviceExt_.setDeviceId(id);
        deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.PERSON_DEVICE_ICCID);
        List<DeviceExt> deviceExtList_ = deviceExtService.getExtInfoByParam(deviceExt_);
        if (StringUtil.isNotBlank(personDeviceVO.getAuthCode())) {
            //新增或更新ICCID
            if (deviceExtList_.size() > 0) {
                deviceExt_.setId(deviceExtList_.get(0).getId());
            }
            deviceExt_.setAttrName("鉴权码");
            deviceExt_.setAttrValue(personDeviceVO.getAuthCode());
            deviceExt_.setAttrDisplayValue(personDeviceVO.getAuthCode());
            deviceExtService.saveOrUpdate(deviceExt_);
        } else {
            //删除ICCID
            if (deviceExtList_.size() > 0) {
                deviceExtService.removeById(deviceExtList_.get(0).getId());
            }
        }

        if (ObjectUtil.isNotEmpty(personDeviceVO.getCoordId())) {
            DeviceExt deviceExt__ = new DeviceExt();
            deviceExt__.setDeviceId(id);
            deviceExt__.setAttrId(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM);
            List<DeviceExt> deviceExtList1 = deviceExtService.getExtInfoByParam(deviceExt__);
            if (deviceExtList1.size() > 0) {
                if (!personDeviceVO.getCoordId().equals(deviceExtList1.get(0).getAttrValueId().toString())) {
                    deviceExt__.setAttrValueId(Long.parseLong(personDeviceVO.getCoordId()));
                    deviceExt__.setAttrName("坐标系");
                    deviceExt__.setAttrValue(personDeviceVO.getCoordValue());
                    deviceExt__.setAttrDisplayValue(personDeviceVO.getCoordValueName());
                    deviceExt__.setId(deviceExtList1.get(0).getId());
                    deviceExtService.updateById(deviceExt__);
                }
            } else {
                deviceExt__.setAttrValueId(Long.parseLong(personDeviceVO.getCoordId()));
                deviceExt__.setAttrName("坐标系");
                deviceExt__.setAttrValue(personDeviceVO.getCoordValue());
                deviceExt__.setAttrDisplayValue(personDeviceVO.getCoordValueName());
                deviceExtService.save(deviceExt__);
            }
        }

        return save;
    }

    @Override
    public boolean savePersonDeviceInfo(PersonDeviceVO personDeviceVO) throws IOException {

        DeviceInfo deviceInfo = Objects.requireNonNull(BeanUtil.copy(personDeviceVO, DeviceInfo.class));
        deviceInfo.setDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO));
        deviceInfo.setDeviceType(deviceInfo.getDeviceType().toUpperCase());
        boolean save = this.saveOrUpdateDeviceInfo(deviceInfo);

        Long id = deviceInfo.getId();

        if (StringUtil.isNotBlank(personDeviceVO.getSimId())) {
            SimRel simRel = new SimRel();
            simRel.setSimId(Long.parseLong(personDeviceVO.getSimId()));
            simRel.setDeviceId(id.toString());
            simRelService.save(simRel);
        }

        //保存ICCID
        if (StringUtil.isNotBlank(personDeviceVO.getAuthCode())) {
            DeviceExt deviceExt_ = new DeviceExt();
            deviceExt_.setDeviceId(id);
            deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.PERSON_DEVICE_ICCID);
            deviceExt_.setAttrName("鉴权码");
            deviceExt_.setAttrValue(personDeviceVO.getAuthCode());
            deviceExt_.setAttrDisplayValue(personDeviceVO.getAuthCode());
            deviceExtService.save(deviceExt_);
        }

        //保存坐标系
        if (StringUtil.isNotBlank(personDeviceVO.getCoordId())) {
            DeviceExt deviceExt_ = new DeviceExt();
            deviceExt_.setDeviceId(id);
            deviceExt_.setAttrId(DeviceConstant.DeviceCharSpec.COORDS_SYSTEM);
            deviceExt_.setAttrName("坐标系");
            deviceExt_.setAttrValueId(Long.parseLong(personDeviceVO.getCoordId()));
            deviceExt_.setAttrValue(personDeviceVO.getCoordValue());
            deviceExt_.setAttrDisplayValue(personDeviceVO.getCoordValueName());
            deviceExtService.save(deviceExt_);
        }

        return save;
    }

    @Override
    public boolean batchRemove(List<Long> idList) {
        if (idList.size() > 0) {
            idList.forEach(id -> {
                DeviceInfo deviceInfo = getById(id);
                List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(id);
                if (deviceRelList.size() > 0) {
                    throw new ServiceException("该设备已绑定，不能删除");
                }
                removeById(id);
                QueryWrapper<DeviceExt> queryWrapper = new QueryWrapper();
                queryWrapper.lambda().eq(DeviceExt::getDeviceId, id);
                queryWrapper.lambda().eq(DeviceExt::getAttrId, DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID);
                DeviceExt deviceExt = deviceExtService.getOne(queryWrapper);
                String guid = "";
                if (ObjectUtil.isNotEmpty(deviceExt) && StringUtil.isNotBlank(deviceExt.getAttrValue())) {
                    guid = deviceExt.getAttrValue();
                }
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("device_id", id);
                deviceExtService.removeByMap(param);
                deviceChannelService.removeByMap(param);
                simRelService.removeByMap(param);
                DeviceCache.saveOrUpdateDevice(deviceInfo);

                if (DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory()) && StringUtil.isNotBlank(guid)) {
                    String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_DELETE_DEVICE_KEY);
                    DeviceInfoParamDTO deviceInfoParamDTO = new DeviceInfoParamDTO();
                    deviceInfoParamDTO.setId(guid);
                    com.alibaba.fastjson.JSONObject req = new com.alibaba.fastjson.JSONObject();
                    String token = sysClient.getMiniCreateToken(false).getData();
                    if (!StringUtil.isNotBlank(token)) {
                        throw new ServiceException("获取点创科技Token失败");
                    }
                    req.put("Token", token);
                    req.put("deviceInfoParam", deviceInfoParamDTO);
                    String resStr = null;
                    try {
                        resStr = OkhttpUtil.postSyncJson(value.split(" ")[1], req.toJSONString()).body().string();
                    } catch (IOException e) {
                        throw new ServiceException("调用远程接口失败");
                    }
                    if (ObjectUtil.isNotEmpty(resStr)) {
                        JSONObject res = JSONUtil.parseObj(resStr);
                        if (DeviceConstant.miniCreateResultCode.success != res.getInt("Result")) {
                            throw new ServiceException(StrUtil.format(DeviceConstant.miniCreateErrInfo.failInfo, res.getInt("Result")));
                        }
                    }
                }
                DeviceCache.delDeviceByEntity(deviceInfo);
            });
        } else {
            throw new ServiceException("参数异常");
        }
        return true;
    }

    @Override
    public IPage<DeviceInfoVO> selectDeviceInfoPage(IPage<DeviceInfoVO> page, DeviceInfoVO deviceInfo) {

        return page.setRecords(baseMapper.selectDeviceInfoPage(page, deviceInfo));
    }

    @Override
    public IPage<DeviceInfo> pageDevices(DeviceInfo deviceInfo, Query query, String tag, String simCode) {
        /**重要说明
         * 1.deviceInfo里的entityCategoryId前端必须传
         * 查人的设备信息：entityCategoryId = 1227854530373226501 或子ID
         * 查车的设备信息：entityCategoryId = 1227854530373226500 或子ID
         * 查设施的设备信息：entityCategoryId = 1227854530373226502 或子ID
         */

        if (null == deviceInfo.getEntityCategoryId() || 0 == deviceInfo.getEntityCategoryId()) {
            throw new ServiceException("请输入终端类型!");
        }
        List<Long> ids = new ArrayList<Long>();
        ids.add(deviceInfo.getEntityCategoryId());

        List<Long> entityCategoryIdList = entityCategoryClient.getSubCategoryIdByParentCategoryId(deviceInfo.getEntityCategoryId()).getData();

        if (entityCategoryIdList.size() > 0) {
            ids.addAll(entityCategoryIdList);
        }
        QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();

        queryWrapper.lambda().in(DeviceInfo::getEntityCategoryId, ids);

        if (StringUtil.isNotBlank(deviceInfo.getDeviceName())) {
            queryWrapper.lambda().like(DeviceInfo::getDeviceName, deviceInfo.getDeviceName());
        }
        if (StringUtil.isNotBlank(deviceInfo.getDeviceFactory())) {
            queryWrapper.lambda().eq(DeviceInfo::getDeviceFactory, deviceInfo.getDeviceFactory());
        }
        if (StringUtil.isNotBlank(deviceInfo.getDeviceCode())) {
            queryWrapper.lambda().eq(DeviceInfo::getDeviceCode, deviceInfo.getDeviceCode());
        }

        if (StringUtil.isNotBlank(simCode)) {
            queryWrapper.lambda().inSql(DeviceInfo::getId, "SELECT device_id FROM ai_sim_rel WHERE sim_id IN (SELECT id FROM ai_sim_info WHERE sim_code LIKE '%" + simCode + "%')");
        }


        if (StringUtil.isNotBlank(tag) && "1".equals(tag)) {
            //只查未绑定的设备
            BladeUser user = AuthUtil.getUser();
            queryWrapper.lambda().notExists("SELECT * FROM  ai_device_rel where ai_device_info.id = ai_device_rel.device_id AND ai_device_rel.is_deleted = 0 AND ai_device_rel.tenant_id = " + user.getTenantId());
        }


        return baseMapper.selectPage(Condition.getPage(query), queryWrapper);

    }

    //由于改成聚合查询重新写查询条件与方法
    @Override
    public IPage<VehicleDeviceVO> pageDevices4Query(DeviceInfo deviceInfo, Query query, String tag, String simCode) {
        /**重要说明
         * 1.deviceInfo里的entityCategoryId前端必须传
         * 查人的设备信息：entityCategoryId = 1227854530373226501 或子ID
         * 查车的设备信息：entityCategoryId = 1227854530373226500 或子ID
         * 查设施的设备信息：entityCategoryId = 1227854530373226502 或子ID
         */

        if (null == deviceInfo.getEntityCategoryId() || 0 == deviceInfo.getEntityCategoryId()) {
            throw new ServiceException("请输入终端类型!");
        }
        List<Long> ids = new ArrayList<Long>();
        ids.add(deviceInfo.getEntityCategoryId());

        List<Long> entityCategoryIdList = entityCategoryClient.getSubCategoryIdByParentCategoryId(deviceInfo.getEntityCategoryId()).getData();

        if (entityCategoryIdList.size() > 0) {
            ids.addAll(entityCategoryIdList);
        }
        QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();

        queryWrapper.in("a.entity_category_id", ids);

        if (StringUtil.isNotBlank(deviceInfo.getDeviceName())) {
            queryWrapper.like("a.device_name", deviceInfo.getDeviceName());
        }
        if (StringUtil.isNotBlank(deviceInfo.getDeviceFactory())) {
            queryWrapper.eq("a.device_factory", deviceInfo.getDeviceFactory());
        }
        if (StringUtil.isNotBlank(deviceInfo.getDeviceCode())) {
            queryWrapper.eq("a.device_code", deviceInfo.getDeviceCode());
        }

        if (StringUtil.isNotBlank(simCode)) {
            queryWrapper.like("c.sim_code", simCode);
        }


        if (StringUtil.isNotBlank(tag) && "1".equals(tag)) {
            //只查未绑定的设备
            BladeUser user = AuthUtil.getUser();
            queryWrapper.isNull("b.entity_id");
            //    queryWrapper.eq("b.tenant_id",user.getTenantId());
        }
        queryWrapper.eq("a.is_deleted", 0);
        IPage<VehicleDeviceVO> pages = Condition.getPage(query);
        List<VehicleDeviceVO> vehicleDeviceVOs = baseMapper.selectDeviceInfoVOPage(Condition.getPage(query), queryWrapper);
        pages.setRecords(vehicleDeviceVOs);
        pages.setTotal(baseMapper.countDeviceInfoVOPage(queryWrapper));
        return pages;

    }


    /**
     * 设施关联终端信息
     *
     * @param entityCategoryId 设备实体类型
     * @param facilityId
     * @return TODO 固定的entityCategoryId后期稳定之后可以改造为通过接口查询获得
     */
    @Override
    public List<DeviceViewVO> listFacilityDevice(Long entityCategoryId, Long facilityId) {
        // 设施监控, 设施传感器
        List<Long> facilityTerminals = Stream.of(1225410941508714505L, 1225410941508714506L, 1227854530373226504L, 1227854530373226505L).collect(Collectors.toList());
        if (entityCategoryId == null || !facilityTerminals.contains(entityCategoryId)) {
            throw new ServiceException("设施设备类型为空或者不正确!");
        }
        List<Long> deviceEntityCategoryIds = new ArrayList<>();
        // 设施传感器有配置子类型
        if (entityCategoryId.equals(1225410941508714506L)) {
            deviceEntityCategoryIds.add(1227854530373226504L); // 称重传感器
            deviceEntityCategoryIds.add(1227854530373226505L); // 臭味传感器
        } else {
            deviceEntityCategoryIds.add(1225410941508714505L); // 设施监控
        }
        List<DeviceViewVO> viewRecords = new ArrayList<>();
        List<Long> deviceIds = new ArrayList<>();
        List<Long> entityIds = new ArrayList<>();
        // 如果传了facilityId却没有查到绑定设备，则直接返回空
        entityIds.add(facilityId);
        List<DeviceRel> deviceRelList = deviceRelService.listDeviceRelsByEntity(entityIds, CommonConstant.ENTITY_TYPE.FACILITY, null);
        if (CollectionUtil.isNotEmpty(deviceRelList)) {
            deviceRelList.forEach(deviceRel -> deviceIds.add(deviceRel.getDeviceId()));
        } else {
            return viewRecords;
        }
        List<DeviceInfo> deviceInfoList = this.list(
                Condition.getQueryWrapper(new DeviceInfo()).in("entity_category_id", deviceEntityCategoryIds).in("id", deviceIds));
        deviceInfoList.forEach(record -> {
            SimInfo simInfo = simInfoService.getSimByDeviceId(record.getId());
            DeviceViewVO deviceViewVO = Objects.requireNonNull(BeanUtil.copy(record, DeviceViewVO.class));
            if (ObjectUtil.isNotEmpty(simInfo)) {
                deviceViewVO.setSimId(simInfo.getId().toString());
                deviceViewVO.setSimCode(simInfo.getSimCode());
                deviceViewVO.setSimNumber(simInfo.getSimNumber());
            }
            deviceViewVO.setEntityCategoryName(entityCategoryClient.getCategoryName(entityCategoryId).getData());
            List<DeviceChannel> deviceChannels = deviceChannelService.getChannelInfoByDeviceId(record.getId());
            deviceViewVO.setDeviceChannelList(deviceChannels);
            List<DeviceExt> deviceExts = deviceExtService.getExtInfoByDeviceId(record.getId());
            deviceViewVO.setDeviceExtList(deviceExts);
            viewRecords.add(deviceViewVO);
        });
        return viewRecords;
    }

    @Override
    public List<DeviceInfo> listDevicesByParam(List<String> ids, Long categoryId) {
        QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();
        if (null != ids && ids.size() > 0) {
            queryWrapper.lambda().in(DeviceInfo::getId, ids);
        }
        if (null != categoryId && categoryId > 0l) {
            queryWrapper.lambda().eq(DeviceInfo::getEntityCategoryId, categoryId);
        }
//
//        if (ObjectUtil.isNotEmpty(deviceStatus)) {
//            queryWrapper.lambda().eq(DeviceInfo::getDeviceStatus, deviceStatus);
//        }
//
//        if (ObjectUtil.isNotEmpty(tenantId)) {
//            queryWrapper.lambda().eq(DeviceInfo::getTenantId, tenantId);
//        }

        List<DeviceInfo> relList = baseMapper.selectList(queryWrapper);
        return relList;
    }

    @Override
    public List<DeviceInfo> listDevicesByParam(DeviceInfo deviceInfo) {
        QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();
        if (StringUtil.isNotBlank(deviceInfo.getDeviceName())) {
            queryWrapper.eq("device_name", deviceInfo.getDeviceName());
        }
        if (StringUtil.isNotBlank(deviceInfo.getDeviceCode())) {
            queryWrapper.eq("device_code", deviceInfo.getDeviceCode());
        }
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 人员，车辆，设施绑定终端
     *
     * @param entityId
     * @param deviceIds
     */
    @Override
    public boolean bindDevice(String entityType, Long entityId, String deviceIds) {
        List<Long> deviceIdList = Func.toLongList(deviceIds);
        deviceIdList.forEach(deviceId -> {
            DeviceInfo deviceInfo = this.getById(deviceId);
            String tenantId = deviceInfo.getTenantId();
            if (deviceInfo == null) {
                throw new ServiceException("设备信息不存在");
            }
            if (checkBind(deviceInfo, entityId, entityType)) {
                DeviceRel deviceRel = new DeviceRel();
                deviceRel.setDeviceId(deviceId);
                deviceRel.setEntityId(entityId);
                deviceRel.setEntityType(entityType);
                deviceRel.setTenantId(tenantId);
                List<DeviceRel> list = deviceRelService.list(Condition.getQueryWrapper(deviceRel));
                if (list != null && list.size() > 0) {
                    return;
                }
                deviceRelService.saveOrUpdateDeviceRel(deviceRel);

                if (CommonConstant.ENTITY_TYPE.GREEN.toString().equals(entityType)) {
                    DeviceInfo deviceInfo1 = this.getById(deviceId);
                    GreenScreenDeviceDTO greenScreenDeviceDTO = new GreenScreenDeviceDTO();
                    greenScreenDeviceDTO.setDeviceCode(deviceInfo1.getDeviceCode());
                    greenScreenDeviceDTO.setDeviceId(deviceId.toString());
                    greenScreenDeviceDTO.setTenantId(AuthUtil.getTenantId());
                    greenScreenDeviceDTO.setGreenAreaId(entityId.toString());
                    mongoTemplate.save(greenScreenDeviceDTO, "GreenScreen_DevicesData");
                }

                if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(entityType) || CommonConstant.ENTITY_TYPE.PERSON.toString().equals(entityType)) {
                    List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(entityId, Long.valueOf(entityType)).getData();
                    if (workareaRelList != null && workareaRelList.size() > 0) {
                        List<String> deviceList = new ArrayList<>();
                        for (WorkareaRel workareaRel : workareaRelList) {
                            //调用大数据
                            JSONObject param = new JSONObject();

                            deviceList.add(deviceInfo.getDeviceCode());
                            if (deviceList.size() > 0) {
                                param.put("deviceId", deviceList.toArray());
                                param.put("areaId", workareaRel.getWorkareaId());
                                param.put("optFlag", BigDataHttpClient.OptFlag.ADD);
                                try {
                                    BigDataHttpClient.postDataToBigData("/smartenv-api/sync/deviceAreaRel", param.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //如果设备是ACC或者是手表,更新车辆或人员表相关状态
                    if (DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())) {
                        vehicleClient.updateVehicleAccstateById(deviceInfo.getDeviceStatus(), entityId);
                    } else if (DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())) {
                        personClient.updatePersonWatchStateById(deviceInfo.getDeviceStatus(), entityId);
                    }else if (DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())) {
                        personClient.updatePersonWatchStateById(deviceInfo.getDeviceStatus(), entityId);
                    }

                }

                String reStr = syncDeviceRel(entityType, entityId, deviceInfo.getDeviceCode(), BigDataHttpClient.OptFlag.ADD);
                log.debug("reStr-------------" + reStr);
                if (StringUtil.isBlank(reStr) || !JSONUtil.isJson(reStr) || !ObjectUtil.isNotEmpty(JSONUtil.parseObj(reStr).get("code")) || !JSONUtil.parseObj(reStr).get("code").equals(0)) {
                    throw new ServiceException("同步大数据失败");
                }
            }
            DeviceRelCache.deleteDeviceRel(deviceInfo.getId(), tenantId);
        });

        if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(Long.parseLong(entityType))) {
//            polymerizationClient.reloadVehicleInfo(entityList);
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_VEHICLE_EVENT, AuthUtil.getTenantId(), entityId));
        } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(Long.parseLong(entityType))) {
//            polymerizationClient.reloadPersonInfo(entityList);
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT, AuthUtil.getTenantId(), entityId));
        }

        return true;
    }

    private Boolean checkBind(DeviceInfo deviceInfo, Long entityId, String entityType) {
        if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(entityType)) {
            if (!ObjectUtil.isNotEmpty(vehicleClient.vehicleInfoById(entityId).getData().getId())) {
                throw new ServiceException("所选车辆信息不存在");
            }
        }
        if (CommonConstant.ENTITY_TYPE.PERSON.toString().equals(entityType)) {
            if (!ObjectUtil.isNotEmpty(personClient.getPerson(entityId).getData().getId())) {
                throw new ServiceException("所选员工信息不存在");
            }
        }
        if (!CommonConstant.ENTITY_TYPE.FACILITY.equals(entityType) && !CommonConstant.ENTITY_TYPE.GREEN.equals(entityType)) {
            //获取所有的车辆监控类型，人员监控类型
            List<DeviceInfo> deviceInfoList = listBindedDevice(entityId, entityType, AuthUtil.getTenantId());
            if (deviceInfoList.size() > 0) {
                deviceInfoList.forEach(deviceInfo1 -> {
                    //目前的规则是同一个实体只能绑定一种类型的设备，对于手环和手表，只能2选1.
                    if ((deviceInfo1.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE)
                            && deviceInfo.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE)) ||
                            (deviceInfo.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE)
                                    && deviceInfo1.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE))) {
                        //如果已经绑了手环或手表 就不能再绑了
                        throw new ServiceException("手环和手表只能绑定其一，不能重复绑定");
                    } else if (deviceInfo1.getEntityCategoryId().equals(deviceInfo.getEntityCategoryId())) {
                        EntityCategory entityCategory = entityCategoryClient.getCategory(deviceInfo.getEntityCategoryId()).getData();
                        if (!entityCategory.getParentCategoryId().equals(DeviceConstant.DeviceCategory.VEHICLE_MONITOR_DEVICE)) {
                            //同一个实体只能绑定一种类型的设备 车辆监控设备、绿化监控设备除外
                            throw new ServiceException("同种类型设备，限定只能绑定一个，不能重复绑定");
                        }
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean deleteBindDevice(String entityType, Long entityId, String deviceIds) {
        List<Long> deviceIdList = new ArrayList<>();
        if (StringUtil.isNotBlank(deviceIds)) {
            deviceIdList = Func.toLongList(deviceIds);
        } else {
            DeviceRel deviceRel = new DeviceRel();
            deviceRel.setEntityId(entityId);
            deviceRel.setEntityType(entityType);
            List<DeviceRel> list = deviceRelService.list(Condition.getQueryWrapper(deviceRel));
            List<Long> tempList = new ArrayList<>();
            list.forEach(dr -> tempList.add(dr.getDeviceId()));
            deviceIdList.addAll(tempList);
        }
        deviceIdList.forEach(deviceId -> {
            DeviceInfo deviceInfo = this.getById(deviceId);
            if (deviceInfo == null) {
                return;
            }
            DeviceRel deviceRel = new DeviceRel();
            deviceRel.setDeviceId(deviceId);
            deviceRel.setEntityId(entityId);
            deviceRel.setEntityType(entityType);
            deviceRelService.remove(Condition.getQueryWrapper(deviceRel));

            if (CommonConstant.ENTITY_TYPE.GREEN.toString().equals(entityType)) {
                org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
                query.addCriteria(Criteria.where("tenantId").is(AuthUtil.getTenantId()));
                query.addCriteria(Criteria.where("deviceId").is(deviceId.toString()));
                query.addCriteria(Criteria.where("greenAreaId").is(entityId.toString()));
                mongoTemplate.findAndRemove(query, GreenScreenDeviceDTO.class, "GreenScreen_DevicesData");
            }

            if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(entityType) || CommonConstant.ENTITY_TYPE.PERSON.toString().equals(entityType)) {
                List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(entityId, Long.valueOf(entityType)).getData();
                if (workareaRelList != null && workareaRelList.size() > 0) {
                    List<String> deviceList = new ArrayList<>();
                    for (WorkareaRel workareaRel : workareaRelList) {
                        //调用大数据
                        JSONObject param = new JSONObject();

                        deviceList.add(deviceInfo.getDeviceCode());
                        if (deviceList.size() > 0) {
                            param.put("deviceId", deviceList.toArray());
                            param.put("areaId", workareaRel.getWorkareaId());
                            param.put("optFlag", BigDataHttpClient.OptFlag.REMOVE);
                            try {
                                BigDataHttpClient.postDataToBigData("/smartenv-api/sync/deviceAreaRel", param.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                //如果设备是ACC或者是手表,更新车辆或人员表相关状态 因为是接触绑定 所以状态默认负1
                if (DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())) {
                    vehicleClient.updateVehicleAccstateById(-1l, entityId);
                } else if (DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())) {
                    personClient.updatePersonWatchStateById(-1l, entityId);
                }
            }
            String reStr = syncDeviceRel(entityType, entityId, deviceInfo.getDeviceCode(), BigDataHttpClient.OptFlag.REMOVE);


            if (StringUtil.isBlank(reStr) || !JSONUtil.isJson(reStr) || !ObjectUtil.isNotEmpty(JSONUtil.parseObj(reStr).get("code")) || !JSONUtil.parseObj(reStr).get("code").equals(0)) {
                log.error("reStr-----------" + reStr);
                throw new ServiceException("同步大数据失败");
            }


            DeviceRelCache.deleteDeviceRel(deviceInfo.getId(), deviceInfo.getTenantId());
        });

        if (CommonConstant.ENTITY_TYPE.VEHICLE.equals(Long.parseLong(entityType))) {
//            polymerizationClient.reloadVehicleInfo(entityList);
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_VEHICLE_EVENT, AuthUtil.getTenantId(), entityId));
        } else if (CommonConstant.ENTITY_TYPE.PERSON.equals(Long.parseLong(entityType))) {
//            polymerizationClient.reloadPersonInfo(entityList);
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT, AuthUtil.getTenantId(), entityId));
        }

        return true;
    }

    @Override
    public Boolean updateAllDeviceRel() {
        QueryWrapper<DeviceRel> wrapper = new QueryWrapper<DeviceRel>();
        wrapper.lambda().eq(DeviceRel::getEntityType,2);
        List<DeviceRel> deviceRelList = deviceRelService.list(wrapper);
        for(DeviceRel deviceRel:deviceRelList){
            syncDeviceRel("2",deviceRel.getEntityId(),deviceRel.getDeviceId().toString(),"2");
        }
        return true;
    }

    @Override
    public String syncDeviceRel(String entityType, Long entityId, String deviceId, String optFlag) {
        JSONObject param = new JSONObject();
        String url = "";
        if (CommonConstant.ENTITY_TYPE.PERSON.toString().equals(entityType)) {
            param = generateDevicePersonRelSync(entityId, deviceId);
            param.put("optFlag", optFlag);
            url = BigDataHttpClient.syncDevicePersonRel;
        }

        if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(entityType)) {
            param = generateDeviceVehicleRelSync(entityId, deviceId);
            param.put("optFlag", optFlag);
            url = BigDataHttpClient.syncDeviceVehicleRel;
        }
        // TODO 设施没说同步，先跳过
        if (StringUtil.isBlank(url)) {
            param.put("code", 0);
            return param.toString();
        }
        log.debug("param--------------------->" + param.toString());

        try {
            String res = BigDataHttpClient.postDataToBigData(url, param.toString());
            return res;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ServiceException("同步大数据失败");
        }
    }

    @Override
    public String syncDeviceCode(String oldCode, String newCode, String type) {
        Map<String, Object> param = new HashMap<String, Object>();
        String url = BigDataHttpClient.syncDeviceCode;
        param.put("oldCode", oldCode);
        param.put("newCode", newCode);
        param.put("type", type);
        log.debug("param--------------------->" + param.toString());
        try {
            String res = BigDataHttpClient.getBigDataBody(url, param);
            return res;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ServiceException("同步大数据失败");
        }
    }

    private JSONObject generateDevicePersonRelSync(Long entityId, String deviceId) {
//		Person person = personClient.getPerson(entityId).getData();
        Person person = PersonCache.getPersonById(AuthUtil.getTenantId(), entityId);
        JSONObject param = new JSONObject();
        param.put("deviceId", deviceId)
                .put("deptId", person.getPersonDeptId())
                .put("email", person.getEmail())
                .put("gender", person.getGender())
                .put("jobNumber", person.getJobNumber())
                .put("mobileNum", person.getMobileNumber())
                .put("personName", person.getPersonName())
                .put("position", person.getPersonPositionId())
                .put("isDeleted", person.getIsDeleted())
                .put("status", person.getStatus())
                .put("tenantId", person.getTenantId())
                .put("wechat", person.getWechatId())
                .put("id", person.getId());
        return param;
    }

    private JSONObject generateDeviceVehicleRelSync(Long entityId, String deviceId) {
//		VehicleInfo vehicleInfo = vehicleClient.vehicleInfoById(entityId).getData();
        VehicleInfo vehicleInfo = VehicleCache.getVehicleById(AuthUtil.getTenantId(), entityId);
        String category = "";
        if (ObjectUtil.isNotEmpty(vehicleInfo.getEntityCategoryId())) {
            category = vehicleInfo.getEntityCategoryId()+"";
        }else{
            category = "0";
        }
        JSONObject param = new JSONObject();
        param.put("deviceId", deviceId)
                .put("brand", vehicleInfo.getBrand())
                .put("category", category)
                .put("model", vehicleInfo.getVehicleModel())
                .put("plateNum", vehicleInfo.getPlateNumber())
                .put("tonnage", vehicleInfo.getTonnage())
                .put("vehicleKind", vehicleInfo.getKindCode())
                .put("isDeleted", vehicleInfo.getIsDeleted())
                .put("status", vehicleInfo.getStatus())
                .put("tenantId", vehicleInfo.getTenantId())
                .put("id", vehicleInfo.getId());
        // 油箱尺寸
        String fuelTankSize = vehicleInfo.getFuelTankSize();
        if (StringUtil.isNotBlank(fuelTankSize)) {
            String fuelTankLength = "";
            String fuelTankWidth = "";
            String fuelTankHeight = "";
            String[] fuelTankSizeList = fuelTankSize.split(VehicleConstant.FULE_TANK_SIZE_SPLIT);
            if (fuelTankSizeList.length >= 1) {
                fuelTankLength = fuelTankSizeList[0];
            }
            if (fuelTankSizeList.length >= 2) {
                fuelTankWidth = fuelTankSizeList[1];
            }
            if (fuelTankSizeList.length >= 3) {
                fuelTankHeight = fuelTankSizeList[2];
            }
            param.put("fuelTankLength", Math.round(Double.parseDouble(fuelTankLength)));
            param.put("fuelTankWidth", Math.round(Double.parseDouble(fuelTankWidth)));
            param.put("fuelTankHeight", Math.round(Double.parseDouble(fuelTankHeight)));
        }
        if (vehicleInfo.getFuelCapacity() != null) {
            param.put("fuelCapacity", vehicleInfo.getFuelCapacity().intValue());
        }
        return param;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public Boolean unbindDevice(Long entityId, Long entityType) {
        List<DeviceRel> deviceRels = deviceRelService.getDeviceRelsByEntity(entityId, entityType);
        if (deviceRels != null && !deviceRels.isEmpty()) {
            List<String> deviceIdList = new ArrayList<>();
            deviceRels.forEach(deviceRel -> {
                deviceIdList.add(String.valueOf(deviceRel.getDeviceId()));
            });
            // 1.解绑
            boolean b = deleteBindDevice(String.valueOf(entityType), entityId,
                    StringUtils.strip(deviceIdList.toString(), "[]").replaceAll(" ", ""));
            if (b) {
                // 2.解绑成功同步给大数据
                deviceIdList.forEach(deviceId -> {
                    DeviceInfo deviceInfo = this.getById(deviceId);
                    String reStr = syncDeviceRel(String.valueOf(entityType), entityId, deviceInfo.getDeviceCode(), BigDataHttpClient.OptFlag.REMOVE);
                    if (StringUtil.isBlank(reStr) || !JSONUtil.parseObj(reStr).get("code").equals(0)) {
                        throw new ServiceException("同步大数据失败");
                    }
                });
            }

            DeviceRelCache.deleteDeviceRel(entityId, deviceRels.get(0).getTenantId());
        }
        return true;
    }

    /**
     * 自定义列表查询
     *
     * @param deviceInfo
     * @return
     */
    @Override
    public List<DeviceInfo> selectDeviceList(DeviceInfo deviceInfo) {
        return baseMapper.selectDeviceInfoList(deviceInfo);
    }

    @Override
    public List<DeviceInfo> listBindedDevice(Long entityId, String entityType, String tenantId) {
        return baseMapper.listBindedDevice(entityId, entityType, tenantId);
    }

    @Override
    public DeviceInfo getDeviceInfoByCode(String code) {
        QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();
        queryWrapper.lambda().eq(DeviceInfo::getDeviceCode, code);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<DevicePersonInfo> getByEntityAndCategoryList(List<Long> scheduleVehicleIdList, Long personWatchDevice) {
        if (null != scheduleVehicleIdList && scheduleVehicleIdList.size() > 0) {
            return baseMapper.listDeviceEntity(scheduleVehicleIdList, personWatchDevice);
        }
        return null;
    }


    @Override
    public List<DevicePersonInfo> listDeviceByCategoryId(Long entityCategoryId) {
        return baseMapper.listDeviceByCategoryId(entityCategoryId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean updateDeviceStatus(String code, Long status) {
        //bladeLogger.info("sync device status",code+"|"+status);
        if(DeviceConstant.BigDataDeviceStatus.ON.equals(status)){
            status = Long.parseLong(DeviceConstant.DeviceStatus.ON);
        }else if(DeviceConstant.BigDataDeviceStatus.OFF.equals(status)){
            status = Long.parseLong(DeviceConstant.DeviceStatus.OFF);
        }else{
            throw new ServiceException("状态["+status+"]未定义");
        }

        QueryWrapper<DeviceInfo> queryWrapper = new QueryWrapper<DeviceInfo>();
        queryWrapper.lambda().eq(DeviceInfo::getDeviceCode,code);
        DeviceInfo deviceInfo =  getOne(queryWrapper);
        if(ObjectUtil.isEmpty(deviceInfo) || ObjectUtil.isEmpty(deviceInfo.getId())){
            throw new ServiceException("编号["+code+"]的设备不存在");
        }


        //ACC设备都是虚拟设备一律过滤
        if(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.equals(deviceInfo.getEntityCategoryId())){
            throw new ServiceException("编号["+code+"]的设备为虚拟设备不需要更新状态");
        }

        //除了点创的监控设备，其他设备都是无上报信息功能的，一律过滤
        if((DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE.equals(deviceInfo.getEntityCategoryId())||
                DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE.equals(deviceInfo.getEntityCategoryId())) && !DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())){
            throw new ServiceException("编号["+code+"]的设备为虚拟设备不需要更新状态");
        }

        //对点创厂家，GPS定位设备是虚拟设备，，一律过滤
        if (DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory()) && DeviceConstant.DeviceCategory.VEHICLE_GPS_DEVICE.equals(deviceInfo.getEntityCategoryId())) {
            throw new ServiceException("编号["+code+"]的设备为虚拟设备不需要更新状态");
        }



        //Version2 根据入参code查询同一实体下的ACC设备或手表设备来更新状态
        //1.入参code本身就是ACC设备或手表设备
        if(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())||
                DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())||
                DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())){
            if(!deviceInfo.getDeviceStatus().equals(status)){
                deviceInfo.setDeviceStatus(status);
                deviceInfo.setUpdateTime(DateUtil.now());
                updateById(deviceInfo);
                DeviceCache.saveOrUpdateDevice(deviceInfo);
                //如果设备是ACC或者是手表,更新车辆或人员表相关状态
                if(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())){
                    List<DeviceRel> deviceRels = deviceRelService.getDeviceRelByDeviceId(deviceInfo.getId());
                    if(null!=deviceRels&&deviceRels.size()>0){
                        for (DeviceRel deviceRel : deviceRels) {
                            vehicleClient.updateVehicleAccstateById(status,deviceRel.getEntityId());
                        }
                    }
                }else if(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())||
                        DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())){
                    List<DeviceRel> deviceRels = deviceRelService.getDeviceRelByDeviceId(deviceInfo.getId());
                    if(null!=deviceRels&&deviceRels.size()>0) {
                        for (DeviceRel deviceRel : deviceRels) {
                            personClient.updatePersonWatchStateById(status, deviceRel.getEntityId());
                        }
                    }
                }
            }
        }else {
            List<DeviceRel> deviceRelList = deviceRelService.getDeviceRelByDeviceId(deviceInfo.getId());
            if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
                DeviceRel deviceRel = deviceRelList.get(0);
                List<DeviceRel> deviceRelList2 = deviceRelService.getDeviceRelsByEntity(deviceRel.getEntityId(),Long.parseLong(deviceRel.getEntityType()));
                if(ObjectUtil.isNotEmpty(deviceRelList2) && deviceRelList2.size()>0){
                    for(DeviceRel deviceRel2:deviceRelList2){
                        DeviceInfo deviceInfo2 = getById(deviceRel2.getDeviceId());
                        if(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString().equals(deviceInfo2.getEntityCategoryId().toString())||
                                DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString().equals(deviceInfo2.getEntityCategoryId().toString())||
                                DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())) {
                            if(!deviceInfo2.getDeviceStatus().equals(status)){
                                deviceInfo2.setDeviceStatus(status);
                                deviceInfo2.setUpdateTime(DateUtil.now());
                                updateById(deviceInfo2);
                                DeviceCache.saveOrUpdateDevice(deviceInfo2);
                                //如果设备是ACC或者是手表,更新车辆或人员表相关状态
                                if(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString().equals(deviceInfo2.getEntityCategoryId().toString())){
                                    List<DeviceRel> deviceRels = deviceRelService.getDeviceRelByDeviceId(deviceInfo.getId());
                                    if(null!=deviceRels&&deviceRels.size()>0){
                                        for (DeviceRel dRel : deviceRels) {
                                            vehicleClient.updateVehicleAccstateById(status,dRel.getEntityId());
                                        }
                                    }

                                }else if(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE.toString().equals(deviceInfo2.getEntityCategoryId().toString())||
                                        DeviceConstant.DeviceCategory.PERSON_BAND_DEVICE.toString().equals(deviceInfo.getEntityCategoryId().toString())){
                                    List<DeviceRel> deviceRels = deviceRelService.getDeviceRelByDeviceId(deviceInfo.getId());
                                    if(null!=deviceRels&&deviceRels.size()>0){
                                        for (DeviceRel dRel : deviceRels) {
                                            personClient.updatePersonWatchStateById(status,dRel.getEntityId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //触发websocket推送事件
        BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO = new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.DEVICE_STATUS_EVENT, deviceInfo.getTenantId(), null, String.valueOf(deviceInfo.getDeviceCode()));
        dataChangeEventClient.doWebsocketEvent(baseWsMonitorEventDTO);


        return true;
    }
}
