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
package com.ai.apac.smartenv.vehicle.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.enums.VehicleStatusEnum;
import com.ai.apac.smartenv.common.enums.WorkStatusEnum;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.dto.DeviceStatusCountDTO;
import com.ai.apac.smartenv.device.entity.*;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.device.feign.ISimClient;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BigDataRespDto;
import com.ai.apac.smartenv.omnic.dto.SummaryDataForVehicle;
import com.ai.apac.smartenv.omnic.dto.TrackPositionDto;
import com.ai.apac.smartenv.omnic.entity.OmnicVehicleInfo;
import com.ai.apac.smartenv.omnic.feign.IDataChangeEventClient;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.SimpleVehicleTrackInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleStatusStatDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleExt;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.mapper.VehicleInfoMapper;
import com.ai.apac.smartenv.vehicle.service.IVehicleExtService;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleNode;
import com.ai.apac.smartenv.vehicle.vo.VehicleVideoUrlVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleVideoVO;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleInfoWrapper;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.querydsl.QuerydslRepositoryInvokerAdapter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 车辆基本信息表 服务实现类
 *
 * @author Blade
 * @since 2020-01-16
 */
@Service
@Slf4j
@AllArgsConstructor
public class VehicleInfoServiceImpl extends BaseServiceImpl<VehicleInfoMapper, VehicleInfo> implements IVehicleInfoService {

    private IVehicleExtService vehicleExtService;
    private IScheduleClient scheduleClient;
    private ISysClient sysClient;
    private IPersonVehicleRelClient personVehicleRelClient;
    private IDeviceClient deviceClient;
    private IWorkareaRelClient workareaRelClient;
    private IWorkareaClient workareaClient;
    private IDeviceRelClient deviceRelClient;
    private IEntityCategoryClient categoryClient;
    private ISimClient simClient;
    private MongoTemplate mongoTemplate;

    private IPolymerizationClient polymerizationClient;
    private IDataChangeEventClient dataChangeEventClient;

    private IProjectClient projectClient;

    @Override
    public IPage<VehicleInfoVO> selectVehicleInfoPage(IPage<VehicleInfoVO> page, VehicleInfoVO vehicleInfo) {
        return page.setRecords(baseMapper.selectVehicleInfoPage(page, vehicleInfo));
    }

    /**
     * 根据部门ID查询车辆
     *
     * @param deptId
     * @return
     */
    @Override
    public List<VehicleInfo> getVehicleInfoByDeptId(Long deptId) {
        return baseMapper.selectList(new QueryWrapper<VehicleInfo>().eq("dept_id", deptId));
    }

    @Override
    public Integer updateVehicleInfoById(VehicleInfoVO vehicleInfo) {
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            vehicleInfo.setUpdateUser(user.getUserId());
        }
        vehicleInfo.setUpdateTime(DateUtil.now());
        // 删除缓存
        VehicleCache.delVehicle(null, vehicleInfo.getId());
        Integer update = baseMapper.updateVehicleInfoById(vehicleInfo);

        List<OmnicVehicleInfo> vehicleInfoList = new ArrayList<>();
        vehicleInfoList.add(BeanUtil.copy(vehicleInfo, OmnicVehicleInfo.class));
        return update;
    }

    @Override
    public Integer updateVehicleAccstateById(Long accState, Long vehicleId) {
        VehicleInfo vehicleInfo = baseMapper.selectById(vehicleId);
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            vehicleInfo.setUpdateUser(user.getUserId());
        }
        vehicleInfo.setUpdateTime(DateUtil.now());
        vehicleInfo.setAccDeviceStatus(accState);
        Integer update = baseMapper.updateById(vehicleInfo);

        //触发数据库变更事件
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.VEHICLE_ACC_STATUS_EVENT, AuthUtil.getTenantId(), vehicleInfo.getId()));
        return update;
    }

    @Override
    public void updateVehicleStateById(Integer vehicleState, Long vehicleId) {
        VehicleInfo vehicleInfo = baseMapper.selectById(vehicleId);
        vehicleInfo.setIsUsed(vehicleState);
        // 更新缓存中数据状态
        baseMapper.updateById(vehicleInfo);
        VehicleCache.saveOrUpdateVehicle(vehicleInfo);
    }

    @Override
    public IPage<VehicleInfo> page(VehicleInfoVO vehicleInfo, Query query, String deviceStatus) {
        QueryWrapper<VehicleInfo> queryWrapper = generateQueryWrapper(vehicleInfo);
        if (ObjectUtil.isNotEmpty(deviceStatus)) {
            List<Long> entityIdList = deviceRelClient.getEntityRelsByCategory(AuthUtil.getTenantId(), DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString(), deviceStatus).getData();
            if (ObjectUtil.isNotEmpty(entityIdList) && entityIdList.size() > 0) {
                if (DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus.toString())) {
                    queryWrapper.lambda().notIn(VehicleInfo::getId, entityIdList);
                } else {
                    queryWrapper.lambda().in(VehicleInfo::getId, entityIdList);
                }
            } else {
                if (!DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus.toString())) {
                    IPage<VehicleInfo> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
                    return emptyPage;
                }
            }
        }
        IPage<VehicleInfo> pages = page(Condition.getPage(query), queryWrapper);
        return pages;
    }

    @Override
    public IPage<VehicleInfoVO> selectVehicleInfoVOPage(VehicleInfo vehicleInfo, Query query, String deviceStatus, String isBindTerminal, String vehicleState) {
        QueryWrapper<VehicleInfo> queryWrapper = generateQueryWrapperForlist(vehicleInfo);
        if (ObjectUtil.isNotEmpty(deviceStatus)) {
//			List<Long> entityIdList = deviceRelClient.getEntityRelsByCategory(AuthUtil.getTenantId(),
//					DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE.toString(), deviceStatus).getData();
//            if (ObjectUtil.isNotEmpty(entityIdList) && entityIdList.size() > 0) {
//                if (DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus)) {
//                    queryWrapper.lambda().notIn(VehicleInfo::getId, entityIdList);
            queryWrapper.eq("info.acc_device_status", deviceStatus);
//                } else {
////                    queryWrapper.lambda().in(VehicleInfo::getId, entityIdList);
//                    queryWrapper.in("info.accDeviceStatus", entityIdList);
//                }
//            } else {
//                if (!DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus)) {
//                    IPage<VehicleInfoVO> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
//                    return emptyPage;
//                }
//            }
        }
        if (StringUtils.isNotEmpty(vehicleState)) {
            queryWrapper.in("info.is_used", Func.toIntList(vehicleState));
        }
        //由于以后绑定都以acc为准 所以以acc状态判定是否绑定了设备
        if (ObjectUtil.isNotEmpty(isBindTerminal)) {
            if (String.valueOf(VehicleConstant.VehicleRelBind.FALSE).equals(isBindTerminal)) {
                queryWrapper.eq("info.acc_device_status", -1);
            } else {
                queryWrapper.ne("info.acc_device_status", -1);
            }
//			List<Long> entityIdList = deviceRelClient.getEntityRelsByCategory(AuthUtil.getTenantId(), "", DeviceConstant.DeviceStatus.NO_DEV).getData();
//            if (ObjectUtil.isNotEmpty(entityIdList) && entityIdList.size() > 0) {
//                if (String.valueOf(VehicleConstant.VehicleRelBind.FALSE).equals(isBindTerminal)) {
//                    queryWrapper.notIn("info.id", entityIdList);
//                } else {
//                    queryWrapper.in("info.id", entityIdList);
//                }
//            } else {
//                if (String.valueOf(VehicleConstant.VehicleRelBind.TRUE).equals(isBindTerminal)) {
//                    IPage<VehicleInfoVO> emptyPage = new Page<>(query.getCurrent(), query.getSize(), 0);
//                    return emptyPage;
//                }
//            }
        }
        IPage<VehicleInfoVO> pages = Condition.getPage(query);
        List<VehicleInfoVO> vehicleInfos = baseMapper.selectVehicleInfoVOPage(Condition.getPage(query), queryWrapper);
        pages.setRecords(vehicleInfos);
        pages.setTotal(baseMapper.countVehicleInfoVOPage(queryWrapper));
        return pages;
    }


    private QueryWrapper<VehicleInfo> generateQueryWrapper(VehicleInfoVO vehicleInfo) {
        QueryWrapper<VehicleInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(vehicleInfo.getPlateNumber())) {
            queryWrapper.like("plate_number", vehicleInfo.getPlateNumber());
        }
        if (vehicleInfo.getKindCode() != null) {
            queryWrapper.eq("kind_code", vehicleInfo.getKindCode());
        }
        if (vehicleInfo.getEntityCategoryId() != null) {
            queryWrapper.eq("entity_category_id", vehicleInfo.getEntityCategoryId());
        }
        if (vehicleInfo.getDeptId() != null) {
            List<Long> deptIdList = sysClient.getAllChildDepts(vehicleInfo.getDeptId()).getData();
            if (deptIdList != null && !deptIdList.isEmpty()) {
                queryWrapper.in("dept_id", deptIdList);
            }
        }
        if (vehicleInfo.getIsUsed() != null) {
            queryWrapper.eq("is_used", vehicleInfo.getIsUsed());
        }
        if (StringUtils.isNotBlank(vehicleInfo.getIsUseds())) {
            queryWrapper.in("is_used", Func.toStrList(vehicleInfo.getIsUseds()));
        }
        if (StringUtils.isNotBlank(vehicleInfo.getTenantId())) {
            queryWrapper.eq("tenant_id", vehicleInfo.getTenantId());
        } else {
            BladeUser user = AuthUtil.getUser();
            if (user != null) {
                queryWrapper.eq("tenant_id", user.getTenantId());
            }
        }
        queryWrapper.orderByAsc("is_used").orderByAsc("plate_number");
        return queryWrapper;
    }

    private QueryWrapper<VehicleInfo> generateQueryWrapperForlist(VehicleInfo vehicleInfo) {
        QueryWrapper<VehicleInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(vehicleInfo.getPlateNumber())) {
            queryWrapper.like("plate_number", vehicleInfo.getPlateNumber());
        }
        if (vehicleInfo.getKindCode() != null) {
            queryWrapper.eq("kind_code", vehicleInfo.getKindCode());
        }
        if (vehicleInfo.getEntityCategoryId() != null) {
            queryWrapper.eq("entity_category_id", vehicleInfo.getEntityCategoryId());
        }
        if (vehicleInfo.getDeptId() != null) {
            List<Long> deptIdList = sysClient.getAllChildDepts(vehicleInfo.getDeptId()).getData();
            if (deptIdList != null && !deptIdList.isEmpty()) {
                queryWrapper.in("dept_id", deptIdList);
            }
        }
        if (vehicleInfo.getIsUsed() != null) {
			/*if (vehicleState == VehicleConstant.VehicleState.IN_USED) {
				queryWrapper.gt("dept_remove_time", LocalDate.now()).or().isNull("dept_remove_time");
			} else {
				queryWrapper.le("dept_remove_time", LocalDate.now());
			}*/
            queryWrapper.eq("is_used", vehicleInfo.getIsUsed());
        }
        if (StringUtils.isNotBlank(vehicleInfo.getTenantId())) {
            queryWrapper.eq("info.tenant_id", vehicleInfo.getTenantId());
        } else {
            BladeUser user = AuthUtil.getUser();
            if (user != null) {
                queryWrapper.eq("info.tenant_id", user.getTenantId());
            }
        }
        queryWrapper.eq("info.is_deleted", 0);
        queryWrapper.orderByAsc("is_used").orderByAsc("plate_number");
        return queryWrapper;
    }

    /*
     * 验证该号码是否已存在，对报废的车，车牌号可以再利用
     */
    private boolean checkPlateNumber(VehicleInfo vehicleInfo) {
        QueryWrapper<VehicleInfo> queryWrapper = new QueryWrapper<>();
        if (vehicleInfo.getId() != null) {
            queryWrapper.notIn("id", vehicleInfo.getId());
        }
        queryWrapper.lambda().eq(VehicleInfo::getPlateNumber, vehicleInfo.getPlateNumber());
        queryWrapper.lambda().ne(VehicleInfo::getIsUsed, VehicleConstant.VehicleState.UN_USED);
        List<VehicleInfo> list = list(queryWrapper);
        if (list != null && list.size() > 0) {
//			return false;
            throw new ServiceException("该车牌已存在");
        }
        return true;
    }

    @Override
    public boolean saveVehicleInfo(VehicleInfoVO vehicleInfoVO) {
        if (vehicleInfoVO.getIsUsed() == null) {
            vehicleInfoVO.setIsUsed(VehicleConstant.VehicleState.IN_USED);
        }
        // 验证入参
        validateVehicle(vehicleInfoVO);
        // 验证该号码是否已存在
        checkPlateNumber(vehicleInfoVO);
        // 校验是否报废
//        vehicleInfoVO.setIsUsed(checkVehicleState(vehicleInfoVO));

        boolean save = save(vehicleInfoVO);
        Long vehicleId = vehicleInfoVO.getId();
        // 保存照片
        if (StringUtil.isNotBlank(vehicleInfoVO.getVehiclePicName())) {
            vehicleExtService.savePicture(vehicleId, VehicleConstant.VehicleExtAttr.PIC_ATTR_ID,
                    VehicleConstant.VehicleExtAttr.PIC_ATTR_NAME, vehicleInfoVO.getVehiclePicName());
        }
        if (StringUtil.isNotBlank(vehicleInfoVO.getDrivingPicFirstName())) {
            vehicleExtService.savePicture(vehicleId, VehicleConstant.VehicleExtAttr.DRIVING_PIC_FIRST_ATTR_ID,
                    VehicleConstant.VehicleExtAttr.DRIVING_PIC_FIRST_ATTR_NAME, vehicleInfoVO.getDrivingPicFirstName());
        }
        if (StringUtil.isNotBlank(vehicleInfoVO.getDrivingPicSecondName())) {
            vehicleExtService.savePicture(vehicleId, VehicleConstant.VehicleExtAttr.DRIVING_PIC_SECOND_ATTR_ID,
                    VehicleConstant.VehicleExtAttr.DRIVING_PIC_SECOND_ATTR_NAME, vehicleInfoVO.getDrivingPicSecondName());
        }

//        List<OmnicVehicleInfo> vehicleInfoList=new ArrayList<>();
//        vehicleInfoList.add(BeanUtil.copy(vehicleInfoVO,OmnicVehicleInfo.class));
//        polymerizationClient.addOrUpdateVehicleList(vehicleInfoList);

//        List<Long> entityList = new ArrayList<>();
//        entityList.add(vehicleInfoVO.getId());
//        polymerizationClient.reloadVehicleInfo(entityList);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_VEHICLE_EVENT, vehicleInfoVO.getTenantId(), vehicleInfoVO.getId()));
        return save;
    }

    private void validateVehicle(@Valid VehicleInfo vehicleInfo) {
        Set<ConstraintViolation<@Valid VehicleInfo>> validateSet = Validation.buildDefaultValidatorFactory().getValidator()
                .validate(vehicleInfo, new Class[0]);
        if (validateSet != null && !validateSet.isEmpty()) {
            String messages = validateSet.stream().map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2).orElse("参数输入有误！");
            throw new ServiceException(messages);
        }
        if (vehicleInfo.getDeptRemoveTime() != null && vehicleInfo.getDeptAddTime().after(vehicleInfo.getDeptRemoveTime())) {
            throw new ServiceException("加入日期不能晚于退出日期");
        }
        if (StringUtils.isNotBlank(vehicleInfo.getFuelTankSize())) {
            String[] sizeList = vehicleInfo.getFuelTankSize().split(VehicleConstant.FULE_TANK_SIZE_SPLIT);
            if (sizeList.length != 3) {
                throw new ServiceException("油箱尺寸输入不正确");
            } else {
                for (String size : sizeList) {
                    try {
                        Double.parseDouble(size);
                    } catch (Exception e) {
                        throw new ServiceException("油箱尺寸输入不正确");
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(vehicleInfo.getBoundarySize())) {
            String[] sizeList = vehicleInfo.getBoundarySize().split(VehicleConstant.FULE_TANK_SIZE_SPLIT);
            if (sizeList.length != 3) {
                throw new ServiceException("外形大小输入不正确");
            } else {
                for (String size : sizeList) {
                    try {
                        Double.parseDouble(size);
                    } catch (Exception e) {
                        throw new ServiceException("外形大小输入不正确");
                    }
                }
            }
        }
    }

    // 车辆-报废状态不再跟退出日期相关联
	/*private Integer checkVehicleState(VehicleInfoVO vehicleInfoVO) {
	    if (vehicleInfoVO.getDeptRemoveTime() != null) {
	        LocalDate removeDate = vehicleInfoVO.getDeptRemoveTime().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
	        if (!removeDate.isAfter(LocalDate.now())) {
	            return VehicleConstant.VehicleState.UN_USED;
	        }
	    }
	    return VehicleConstant.VehicleState.IN_USED;
	}*/

    @Override
    public Integer updateVehicleInfo(VehicleInfoVO vehicleInfo) {
        String tenantId = AuthUtil.getTenantId();
        // 验证入参
        validateVehicle(vehicleInfo);
        // 先退出解绑, 报废状态不再跟退出日期相关联
//        vehicleInfo.setIsUsed(checkVehicleState(vehicleInfo));
        if (vehicleInfo.getIsUsed() != null && vehicleInfo.getIsUsed() == VehicleConstant.VehicleState.UN_USED) {
            removeVehicleAllRel(vehicleInfo);
            vehicleInfo.setAccDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO_DEV));
        }
        // 验证该号码是否已存在
        checkPlateNumber(vehicleInfo);
        // 是否需要同步大数据
        boolean syncDevice = checkSyncDevice(vehicleInfo, tenantId);
        // 更新
        Integer update = updateVehicleInfoById(vehicleInfo);
        if (update == 1) {
            // 修改照片
            VehicleExt vehicleExt = new VehicleExt();
            vehicleExt.setVehicleId(vehicleInfo.getId());
            // 车辆照片
            String attrValue = DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_VEHICLE);
            // 默认图片不保存
            if (StringUtils.isNotBlank(vehicleInfo.getVehiclePicName())
                    && !attrValue.equals(vehicleInfo.getVehiclePicName())) {
                vehicleExtService.updatePicture(vehicleInfo.getId(), vehicleInfo.getVehiclePicName(),
                        VehicleConstant.VehicleExtAttr.PIC_ATTR_ID, VehicleConstant.VehicleExtAttr.PIC_ATTR_NAME);
            }
            // 行驶证正页
            vehicleExtService.updatePicture(vehicleInfo.getId(), vehicleInfo.getDrivingPicFirstName(),
                    VehicleConstant.VehicleExtAttr.DRIVING_PIC_FIRST_ATTR_ID, VehicleConstant.VehicleExtAttr.DRIVING_PIC_FIRST_ATTR_NAME);
            // 行驶证副页
            vehicleExtService.updatePicture(vehicleInfo.getId(), vehicleInfo.getDrivingPicSecondName(),
                    VehicleConstant.VehicleExtAttr.DRIVING_PIC_SECOND_ATTR_ID, VehicleConstant.VehicleExtAttr.DRIVING_PIC_SECOND_ATTR_NAME);
            // 同步大数据
            if (syncDevice) {
                List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(vehicleInfo.getId(), tenantId);
                if (deviceRelList != null && !deviceRelList.isEmpty()) {
                    for (DeviceRel deviceRel : deviceRelList) {
                        DeviceInfo device = DeviceCache.getDeviceById(tenantId, deviceRel.getDeviceId());
                        if (device != null && StringUtil.isNotBlank(device.getDeviceCode())) {
                            syncDeviceRel(vehicleInfo, device.getDeviceCode(), BigDataHttpClient.OptFlag.EDIT);
                        }
                    }
                }
            }
        }

//        List<Long> vehicleIdList = new ArrayList<>();
//        vehicleIdList.add(vehicleInfo.getId());
//        polymerizationClient.reloadVehicleInfo(vehicleIdList);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_VEHICLE_EVENT, tenantId, vehicleInfo.getId()));

//        List<OmnicVehicleInfo> vehicleInfoList=new ArrayList<>();
//        vehicleInfoList.add(BeanUtil.copy(vehicleInfo,OmnicVehicleInfo.class));
//        polymerizationClient.addOrUpdateVehicleList(vehicleInfoList);

        return update;
    }

    private void syncDeviceRel(VehicleInfoVO vehicleInfo, String deviceCode, String optFlag) {
        Long category = VehicleConstant.CategoryBigDataMap.get(vehicleInfo.getEntityCategoryId());
        if (category == null) {
            category = 0L;
        }
        JSONObject param = new JSONObject();
        param.put("deviceId", deviceCode);
        param.put("brand", vehicleInfo.getBrand());
        param.put("category", category);
        param.put("model", vehicleInfo.getVehicleModel());
        param.put("plateNum", vehicleInfo.getPlateNumber());
        param.put("tonnage", vehicleInfo.getTonnage());
        param.put("vehicleKind", vehicleInfo.getKindCode());
        param.put("isDeleted", vehicleInfo.getIsDeleted());
        param.put("status", vehicleInfo.getStatus());
        param.put("tenantId", vehicleInfo.getTenantId());
        param.put("id", vehicleInfo.getId());
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
        param.put("optFlag", optFlag);
        String url = BigDataHttpClient.syncDeviceVehicleRel;
        try {
            BigDataHttpClient.postDataToBigData(url, param.toString());
        } catch (IOException e) {
            throw new ServiceException("同步大数据失败");
        }
    }

    private boolean checkSyncDevice(VehicleInfoVO vehicleInfo, String tenantId) {
        VehicleInfo oldVehicle = VehicleCache.getVehicleById(tenantId, vehicleInfo.getId());
        String oldFuelTankSize = oldVehicle.getFuelTankSize() == null ? "" : oldVehicle.getFuelTankSize();
        String newFuelTankSize = vehicleInfo.getFuelTankSize() == null ? "" : vehicleInfo.getFuelTankSize();
        BigDecimal oldFuelCapacity = oldVehicle.getFuelCapacity() == null ? new BigDecimal(0) : oldVehicle.getFuelCapacity();
        BigDecimal newFuelCapacity = vehicleInfo.getFuelCapacity() == null ? new BigDecimal(0) : vehicleInfo.getFuelCapacity();
        if (!oldFuelTankSize.equals(newFuelTankSize) || !oldFuelCapacity.equals(newFuelCapacity)) {
            return true;
        }
        return false;
    }

    private void removeVehicleAllRel(VehicleInfoVO vehicleInfo) {
		/*LocalDate removeDate = vehicleInfo.getDeptRemoveTime().toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
		if (!removeDate.isAfter(LocalDate.now())) {*/
        // 解绑终端
        deviceClient.unbindDevice(vehicleInfo.getId(), CommonConstant.ENTITY_TYPE.VEHICLE);
        // 解绑路线
        workareaRelClient.unbindWorkarea(vehicleInfo.getId(), VehicleConstant.WORKAREA_REL_VEHICLE);
        // 解绑考勤
        scheduleClient.unbindSchedule(vehicleInfo.getId(), ArrangeConstant.ScheduleObjectEntityType.VEHICLE);
        // 解绑驾驶员
        personVehicleRelClient.unbindPerson(vehicleInfo.getId());
//		}

//        List<Long> vehicleIdList = new ArrayList<>();
//        vehicleIdList.add(vehicleInfo.getId());
//        polymerizationClient.reloadVehicleInfo(vehicleIdList);
        dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.NEW_OR_UPDATE_VEHICLE_EVENT, AuthUtil.getTenantId(), vehicleInfo.getId()));
    }

    @Override
    public List<VehicleInfo> listAll(VehicleInfoVO vehicleInfo) {
        QueryWrapper<VehicleInfo> queryWrapper = generateQueryWrapper(vehicleInfo);
        List<VehicleInfo> list = list(queryWrapper);
        return list;
    }

    @Override
    public int countAll(VehicleInfoVO vehicleInfo) {
        QueryWrapper<VehicleInfo> queryWrapper = generateQueryWrapper(vehicleInfo);
        return count(queryWrapper);
    }

    @Override
    public IPage<VehicleInfo> pageForPerson(VehicleInfoVO vehicle, Query query, Long personId) {
        vehicle.setIsUsed(VehicleConstant.VehicleState.IN_USED);
        QueryWrapper<VehicleInfo> queryWrapper = generateQueryWrapper(vehicle);
        // 查询人员已绑定的车辆
        List<PersonVehicleRel> vehicles = personVehicleRelClient.getVehicleByPersonId(personId).getData();
        List<Long> filterVehicleIdList = new ArrayList<>();
        if (vehicles != null) {
            vehicles.forEach(rel -> {
                filterVehicleIdList.add(rel.getVehicleId());
            });
        }
        // 已绑定人的非机动车
		/*List<PersonVehicleRel> relList = personVehicleRelClient.listAll().getData();
		relList.forEach(rel -> {
		    VehicleInfo filterVehicle = VehicleCache.getVehicleById(null, rel.getVehicleId());
		    if (vehicle != null && VehicleConstant.KindCode.NON_MOTOR.equals(vehicle.getKindCode())) {
		        filterVehicleIdList.add(filterVehicle.getId());
		    }
		});*/
        if (!filterVehicleIdList.isEmpty()) {
            queryWrapper.notIn("id", filterVehicleIdList);
        }
        IPage<VehicleInfo> pages = page(Condition.getPage(query), queryWrapper);
        return pages;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean removeVehicle(List<Long> idList) {
        idList.forEach(id -> {
            VehicleInfo vehicle = VehicleCache.getVehicleById(null, id);
            if (vehicle == null) {
                throw new ServiceException("没有记录");
            }
            String plateNumber = vehicle.getPlateNumber();
            // 校验是否绑定终端
            List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(id, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
            if (deviceRelList != null && !deviceRelList.isEmpty()) {
                throw new ServiceException(plateNumber + ", " + "已绑定终端，不允许删除");
            }
            // 校验是否绑定区域/路线
            List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(id, VehicleConstant.WORKAREA_REL_VEHICLE).getData();
            if (workareaRelList != null && !workareaRelList.isEmpty()) {
                throw new ServiceException(plateNumber + ", " + "已绑定路线或区域，不允许删除");
            }
            // 校验是否绑定考勤
            List<ScheduleObject> scheduleObjectList = scheduleClient.listUnfinishScheduleByEntity(id, ArrangeConstant.ScheduleObjectEntityType.VEHICLE).getData();
            if (scheduleObjectList != null && !scheduleObjectList.isEmpty()) {
                throw new ServiceException(plateNumber + ", " + "已绑定考勤，不允许删除");
            }
            // 校验是否绑定驾驶员
            List<PersonVehicleRel> personList = personVehicleRelClient.getPersonVehicleRels(id).getData();
            if (personList != null && !personList.isEmpty()) {
                throw new ServiceException(plateNumber + ", " + "已绑定驾驶员，不允许删除");
            }
            // 逻辑删除
            deleteLogic(Arrays.asList(id));
            // 照片

            vehicleExtService.removeVehicleAttr(id);

//            polymerizationClient.reloadVehicleInfo(idList);
            dataChangeEventClient.doDbEvent(new BaseDbEventDTO<Long>(DbEventConstant.EventType.REMOVE_VEHICLE_EVENT, AuthUtil.getTenantId(), id));
        });
        return true;
    }

    /**
     * 获取车辆树
     *
     * @param treeType 1-按车辆类型分组，2-按部门分组
     * @param tenantId 租户ID
     * @return
     */
    @Override
    public List<VehicleNode> getVehicleTree(Integer treeType, String tenantId) {
        List<VehicleNode> vehicleNodeList = null;

        //获取今日车辆排班列表
//        VehicleStatusStatDTO vehicleStatusStat = getVehicleStatusStatToday(tenantId);
//        List<BasicVehicleInfoDTO> allVehicle = new ArrayList<BasicVehicleInfoDTO>();
//        allVehicle.addAll(vehicleStatusStat.getWorkingList());
//        allVehicle.addAll(vehicleStatusStat.getDepartureList());
//        allVehicle.addAll(vehicleStatusStat.getSitBackList());
        org.springframework.data.mongodb.core.query.Query query = org.springframework.data.mongodb.core.query.Query.query(Criteria.where("tenantId").is(tenantId)
                .and("gpsDeviceCode").ne(null)
        );
        List<BasicVehicleInfoDTO> allVehicle = mongoTemplate.find(query, BasicVehicleInfoDTO.class);


        log.info("当前租户所有车辆数量:{}", allVehicle.size());
        List<VehicleNode> vehicleTypeTree = new ArrayList<VehicleNode>();
        if (treeType == 1) {
            Map<Long, List<BasicVehicleInfoDTO>> vehicleMap = allVehicle.stream()
                    .collect(Collectors.groupingBy(BasicVehicleInfoDTO::getKindCode));
            log.info("按车辆类型分组结果:{}", JSON.toJSONString(vehicleMap));
            vehicleNodeList = vehicleMap.keySet().stream().map(kindCode -> {
                VehicleNode vehicleNode = new VehicleNode();
                vehicleNode.setId(kindCode);
                String entityCategoryName = VehicleCategoryCache.getCategoryNameByCode(kindCode.toString(), tenantId);

                //String kindName = EntityCategoryCache.getCategoryNameById(kindCode);
                vehicleNode.setNodeName(entityCategoryName);
                vehicleNode.setIsLastNode(false);
                List<BasicVehicleInfoDTO> vehicleInfos = vehicleMap.get(kindCode);
                List<VehicleNode> subTree = this.buildSubTree(vehicleInfos);
                vehicleNode.setSubNodes(subTree);
                vehicleTypeTree.add(vehicleNode);
                return vehicleNode;
            }).collect(Collectors.toList());
        } else {
            Map<Long, List<BasicVehicleInfoDTO>> vehicleMap = allVehicle.stream()
                    .collect(Collectors.groupingByConcurrent(BasicVehicleInfoDTO::getDeptId));
            log.info("按部门分组结果:{}", JSON.toJSONString(vehicleMap));
            vehicleNodeList = vehicleMap.keySet().stream().map(deptId -> {
                VehicleNode vehicleNode = new VehicleNode();
                vehicleNode.setId(deptId);
                String deptName = DeptCache.getDeptName(String.valueOf(deptId));
                vehicleNode.setNodeName(deptName);
                vehicleNode.setIsLastNode(false);
                List<BasicVehicleInfoDTO> vehicleInfos = vehicleMap.get(deptId);
                List<VehicleNode> subTree = this.buildSubTree(vehicleInfos);
                vehicleNode.setSubNodes(subTree);
                vehicleTypeTree.add(vehicleNode);
                return vehicleNode;
            }).collect(Collectors.toList());
        }

        return vehicleNodeList;
    }

    private List<VehicleNode> buildSubTree(List<BasicVehicleInfoDTO> vehicleInfos) {
        List<VehicleNode> subTree = vehicleInfos.stream().map(vehicleInfo -> {
            VehicleNode subNode = new VehicleNode();
            subNode.setId(vehicleInfo.getId());
            subNode.setNodeName(vehicleInfo.getPlateNumber());
            subNode.setSubNodes(null);
            subNode.setShowFlag(false);
            subNode.setIsLastNode(true);
            subNode.setStatus(vehicleInfo.getWorkStatus());
            subNode.setStatusName(vehicleInfo.getWorkStatusName());
            subNode.setVehicleType(vehicleInfo.getVehicleTypeName());
            return subNode;
        }).collect(Collectors.toList());
        return subTree;
    }

    /**
     * 获取当天车辆出勤状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public VehicleStatusStatDTO getVehicleStatusStatToday(String tenantId) {
        //工作中车辆
        List<BasicVehicleInfoDTO> workingList = new ArrayList<BasicVehicleInfoDTO>();
        //脱岗车辆
        List<BasicVehicleInfoDTO> departureList = new ArrayList<BasicVehicleInfoDTO>();
        //休息中车辆
        List<BasicVehicleInfoDTO> sitBackList = new ArrayList<BasicVehicleInfoDTO>();
        //查询当天排班表,应该有哪些车出勤
        R<List<ScheduleObject>> scheduleResult = scheduleClient.listVehicleForNow(tenantId);
        List<Long> scheduleVehicleIdList = new ArrayList<Long>();
        HashMap<Long, VehicleInfo> scheduleVehicleMap = new HashMap<Long, VehicleInfo>();
        if (scheduleResult != null && scheduleResult.getData() != null) {
            List<ScheduleObject> scheduleList = scheduleResult.getData();
            Date now = new Date();
            if (scheduleList != null && scheduleList.size() > 0) {
                log.info("今日应出勤车辆:{}", JSON.toJSONString(scheduleList));
                scheduleList.stream().forEach(scheduleObject -> {
                    Long vehicleId = scheduleObject.getEntityId();
                    VehicleInfo vehicleInfo = VehicleCache.getVehicleById(tenantId, vehicleId);
                    if (vehicleInfo != null && vehicleInfo.getId() != null && vehicleInfo.getIsUsed() == 1) {
                        scheduleVehicleIdList.add(vehicleId);
                        scheduleVehicleMap.put(vehicleId, vehicleInfo);
                        BasicVehicleInfoDTO vehicleInfoDTO = BeanUtil.copy(vehicleInfo, BasicVehicleInfoDTO.class);
                        String entityCategoryName = VehicleCategoryCache.getCategoryNameByCode(vehicleInfoDTO.getKindCode().toString(), tenantId);

                        //String kindName = EntityCategoryCache.getCategoryNameById(vehicleInfoDTO.getKindCode());
                        vehicleInfoDTO.setVehicleTypeName(entityCategoryName);
                        DeviceInfo deviceInfo = deviceClient.getByEntityAndCategory(vehicleId, DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE).getData();

                        R<Boolean> isNeedWorkResult = scheduleClient.checkNeedWork(scheduleObject.getEntityId(), scheduleObject.getEntityType(), now);
                        if (isNeedWorkResult != null && isNeedWorkResult.getData() != null) {
                            if (isNeedWorkResult.getData()) {
                                if (ObjectUtil.isNotEmpty(deviceInfo) && ObjectUtil.isNotEmpty(deviceInfo.getDeviceStatus()) && deviceInfo.getDeviceStatus().equals(0L)) {
                                    //需要出勤且ACC为开
                                    vehicleInfoDTO.setWorkStatus(VehicleConstant.VehicleStatus.ONLINE);
                                    vehicleInfoDTO.setWorkStatusName(VehicleStatusEnum.getDescByValue(vehicleInfoDTO.getWorkStatus()));
                                    workingList.add(vehicleInfoDTO);
                                } else {
                                    //没有绑定ACC、ACC关闭、ACC异常关闭都视为离岗
                                    vehicleInfoDTO.setWorkStatus(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM);
                                    vehicleInfoDTO.setWorkStatusName(VehicleStatusEnum.getDescByValue(vehicleInfoDTO.getWorkStatus()));
                                    departureList.add(vehicleInfoDTO);
                                }
                            }
                        } else {
                            vehicleInfoDTO.setWorkStatus(VehicleConstant.VehicleStatus.OFF_ONLINE);
                            vehicleInfoDTO.setWorkStatusName(VehicleStatusEnum.getDescByValue(vehicleInfoDTO.getWorkStatus()));
                            sitBackList.add(vehicleInfoDTO);
                        }
                    }
                });
            }
        }
        List<VehicleInfo> vehicleInfoList = getUsedVehicleByTenant(tenantId);
        for (VehicleInfo vehicleInfo : vehicleInfoList) {
            if (scheduleVehicleMap.get(vehicleInfo.getId()) == null) {
                BasicVehicleInfoDTO vehicleInfoDTO = BeanUtil.copy(vehicleInfo, BasicVehicleInfoDTO.class);
                vehicleInfoDTO.setWorkStatus(VehicleConstant.VehicleStatus.OFF_ONLINE);
                vehicleInfoDTO.setWorkStatusName(VehicleStatusEnum.getDescByValue(vehicleInfoDTO.getWorkStatus()));
                sitBackList.add(vehicleInfoDTO);
            }
        }

        VehicleStatusStatDTO statusStatDTO = new VehicleStatusStatDTO();
        statusStatDTO.setWorkingList(workingList);
        statusStatDTO.setDepartureList(departureList);
        statusStatDTO.setSitBackList(sitBackList);

        log.info("今日车辆出勤统计:{}", JSON.toJSONString(statusStatDTO));
        return statusStatDTO;
    }

    @Override
    public List<VehicleInfo> getUsedVehicleByTenant(String tenantId) {
        VehicleInfoVO vehicleInfo = new VehicleInfoVO();
        vehicleInfo.setTenantId(tenantId);
        vehicleInfo.setIsUsed(VehicleConstant.VehicleState.IN_USED);
        return listAll(vehicleInfo);
    }

    @Override
    public int getTotalNormalVehicleCount(String tenantId) {
        VehicleInfoVO vehicleInfo = new VehicleInfoVO();
        vehicleInfo.setTenantId(tenantId);
        vehicleInfo.setIsUsed(VehicleConstant.VehicleState.IN_USED);
        return countAll(vehicleInfo);
    }

    @Override
    public List<VehicleNode> treeByDept(String nodeName, String tenantId, List<Long> invalidEntityIdList) {
        List<Dept> allDepts = sysClient.getAllDept().getData();
        Iterator<Dept> it = allDepts.iterator();
        while (it.hasNext()) {
            Dept dept = it.next();
            if (dept.getId().equals(0L) || dept.getId().equals(dept.getParentId())
                    || (StringUtils.isNotBlank(tenantId) && !tenantId.equals(dept.getTenantId()))
                    || dept.getIsDeleted() == null || dept.getIsDeleted() == 1) {
                it.remove();
            }
        }
        List<VehicleNode> nodeList = getChildNodeByDept(nodeName, 0L, allDepts, tenantId, invalidEntityIdList);
        return nodeList;
    }

    private List<VehicleNode> getChildNodeByDept(String nodeName, Long parentId, List<Dept> allDepts, String tenantId, List<Long> invalidEntityIdList) {
        List<VehicleNode> nodeList = new ArrayList<>();
        for (Dept dept : allDepts) {
            if (parentId.equals(dept.getParentId())) {
                VehicleNode node = new VehicleNode();
                node.setId(dept.getId());
                node.setIsVehicle(false);
                node.setNodeName(dept.getFullName());
                node.setIsValid(true);
                List<VehicleNode> subNodes = getChildNodeByDept(nodeName, dept.getId(), allDepts, tenantId, invalidEntityIdList);
                // 查询车辆
                List<VehicleInfo> list = getVehicleInfoByDeptId(dept.getId());
                List<VehicleInfoVO> listVO = VehicleInfoWrapper.build().listVO(list);
                for (VehicleInfoVO obj : listVO) {
                    // 报废
                    if (obj.getIsUsed() != null && obj.getIsUsed() != VehicleConstant.VehicleState.IN_USED) {
                        continue;
                    }
                    VehicleNode vehicle = new VehicleNode();
                    vehicle.setId(obj.getId());
                    vehicle.setIsVehicle(true);
                    String entityCategoryName = VehicleCategoryCache.getCategoryNameByCode(obj.getEntityCategoryId().toString(), tenantId);
                    vehicle.setNodeName(obj.getPlateNumber() + "(" + entityCategoryName + ")");
                    vehicle.setIsLastNode(true);
                    vehicle.setIsValid(true);
                    if (invalidEntityIdList != null && invalidEntityIdList.contains(obj.getId())) {
                        vehicle.setIsValid(false);
                    }
                    if (StringUtil.isBlank(nodeName) || (StringUtil.isNotBlank(nodeName) && vehicle.getNodeName().contains(nodeName))) {
                        subNodes.add(vehicle);
                    }
                }
                if (StringUtil.isNotBlank(nodeName) && subNodes.isEmpty()) {
                    continue;
                }
                if (subNodes.isEmpty()) {
                    node.setIsLastNode(true);
                }
                node.setSubNodes(subNodes);
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    /**
     * 根据车辆ID获取历史视频播放地址
     *
     * @param vehicleId 车辆ID
     * @param channelNo 通道编号
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public VehicleVideoVO getHistoryVideoUrl(Long vehicleId, String channelNo, String startTime, String endTime) {
        VehicleVideoVO vehicleVideoVO = new VehicleVideoVO();
        vehicleVideoVO.setVerhicleId(String.valueOf(vehicleId));
        List<VehicleVideoUrlVO> videoUrlVOList = null;
        //获取车辆信息
        VehicleInfo vehicleInfo = baseMapper.selectById(vehicleId);
        if (vehicleInfo == null) {
            throw new ServiceException("车辆不存在,无法播放视频");
        }
        //TODO 如果没有传通道编号,则一次性查询所有通道的url返回给前端,目前只允许每次只能查一个通道
        if (StringUtils.isEmpty(channelNo) || channelNo.equals("0")) {
            throw new ServiceException("车辆不存在,无法播放视频");
        }

        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (CollUtil.isNotEmpty(deviceRelList)) {
            videoUrlVOList = new ArrayList<VehicleVideoUrlVO>();
            for (DeviceRel deviceRel : deviceRelList) {
                Long deviceId = deviceRel.getDeviceId();
                DeviceInfo deviceInfo = DeviceCache.getDeviceById(deviceRel.getTenantId(), deviceId);
                //TODO 目前只和深圳点创科技集成
                if (deviceInfo != null && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())
                        && (deviceInfo.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE)
                        || deviceInfo.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE))) {
                    VehicleVideoUrlVO vehicleVideoUrlVO = callMiniCreate(deviceId, channelNo, startTime, endTime);
                    videoUrlVOList.add(vehicleVideoUrlVO);
                }
            }
        }
        vehicleVideoVO.setVehicleVideoUrlVOList(videoUrlVOList);
        return vehicleVideoVO;
    }

    @Override
    public VehicleVideoVO getVehicleVideosLive(Long vehicleId, String device, String channel) {
        VehicleVideoVO vehicleVideoVO = new VehicleVideoVO();
        List<VehicleVideoUrlVO> vehicleVideoUrlVOList = new ArrayList<VehicleVideoUrlVO>();
        vehicleVideoVO.setVerhicleId(vehicleId.toString());
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
            String token = sysClient.getMiniCreateToken(false).getData();
            if (!StringUtil.isNotBlank(token)) {
                throw new ServiceException("获取点创科技Token失败");
            }
            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_LIVE_VIDEO_KEY);

            if (StringUtil.isNotBlank(device) && StringUtil.isNotBlank(channel)) {
                //播放指定某个设备某个渠道
                DeviceInfo deviceInfo = deviceClient.getDeviceById(device).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                    List<DeviceExt> deviceExtList = deviceClient.getExtInfoByParam(Long.parseLong(device), DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID).getData();
                    String guid = "";
                    if (ObjectUtil.isNotEmpty(deviceExtList) && deviceExtList.size() > 0) {
                        guid = deviceExtList.get(0).getAttrValue();
                    }
                    SimInfo simInfo = simClient.getSimByDeviceId(Long.parseLong(device)).getData();
                    if (StringUtil.isNotBlank(guid) && ObjectUtil.isNotEmpty(simInfo)) {
                        VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                        vehicleVideoUrlVO.setDeviceId(device);
                        vehicleVideoUrlVO.setChannelSeq(channel);
                        String uri = value.split(" ")[1];
                        String simCode2 = simInfo.getSimCode2();
                        uri = StrUtil.format(uri, token, guid, simCode2, channel);
                        String resStr = null;
                        try {
                            log.info("OkhttpUtil Start ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                            resStr = OkhttpUtil.getSync(uri).body().string();
                        } catch (IOException e) {
                            log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                            throw new ServiceException("获取通道[" + channel + "]实时视频地址失败");
                        }
                        log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                        if (ObjectUtil.isNotEmpty(resStr)) {
                            cn.hutool.json.JSONObject res = JSONUtil.parseObj(resStr);
                            int videoErrCode = res.getInt("videoErrCode");
                            vehicleVideoUrlVO.setVideoErrCode(videoErrCode);
                            if (0 == videoErrCode) {
                                vehicleVideoUrlVO.setUrl(res.getStr("Url"));
                            }
                            if (1 == videoErrCode) {
                                sysClient.getMiniCreateToken(true).getData();
                            }
                        }
                        vehicleVideoUrlVOList.add(vehicleVideoUrlVO);

                    }


                }
            } else {
                //播放全部设备全部渠道
                for (DeviceRel deviceRel : deviceRelList) {
                    Long deviceId = deviceRel.getDeviceId();
                    DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                    if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {

                        List<DeviceChannel> deviceChannelList = deviceClient.getChannelInfoByDeviceId(deviceId).getData();
                        if (ObjectUtil.isNotEmpty(deviceChannelList) && deviceChannelList.size() > 0) {
                            List<DeviceExt> deviceExtList = deviceClient.getExtInfoByParam(deviceId, DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID).getData();
                            String guid = "";
                            if (ObjectUtil.isNotEmpty(deviceExtList) && deviceExtList.size() > 0) {
                                guid = deviceExtList.get(0).getAttrValue();
                            }
                            SimInfo simInfo = simClient.getSimByDeviceId(deviceId).getData();
                            if (StringUtil.isNotBlank(guid) && ObjectUtil.isNotEmpty(simInfo)) {
                                for (DeviceChannel deviceChannel : deviceChannelList) {
                                    String channelSeq = deviceChannel.getChannelSeq();
                                    VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                                    vehicleVideoUrlVO.setDeviceId(deviceId.toString());
                                    vehicleVideoUrlVO.setChannelSeq(channelSeq);
                                    String uri = value.split(" ")[1];
                                    String simCode2 = simInfo.getSimCode2();
                                    uri = StrUtil.format(uri, token, guid, simCode2, channelSeq);
                                    String resStr = null;
                                    try {
                                        log.info("OkhttpUtil Start ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                                        resStr = OkhttpUtil.getSync(uri).body().string();
                                    } catch (IOException e) {
                                        log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                                        throw new ServiceException("获取通道[" + channelSeq + "]实时视频地址失败");
                                    }
                                    log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                                    if (ObjectUtil.isNotEmpty(resStr)) {
                                        cn.hutool.json.JSONObject res = JSONUtil.parseObj(resStr);
                                        int videoErrCode = res.getInt("videoErrCode");
                                        vehicleVideoUrlVO.setVideoErrCode(videoErrCode);
                                        if (0 == videoErrCode) {
                                            vehicleVideoUrlVO.setUrl(res.getStr("Url"));
                                        }
                                        if (1 == videoErrCode) {
                                            sysClient.getMiniCreateToken(true).getData();
                                        }
                                    }
                                    vehicleVideoUrlVOList.add(vehicleVideoUrlVO);
                                }
                            }

                        }
                    }
                }
            }
            vehicleVideoVO.setVehicleVideoUrlVOList(vehicleVideoUrlVOList);
        } else {
            throw new ServiceException("该车辆尚未绑定监控设备");
        }
        return vehicleVideoVO;
    }

    @Override
    public VehicleVideoVO getVehicleVideosHistory(Long vehicleId, String device, String channel, String startTime, String endTime, Boolean isTransfer) {
        VehicleInfo vehicleInfo = this.getById(vehicleId);
        VehicleVideoVO vehicleVideoVO = new VehicleVideoVO();
        List<VehicleVideoUrlVO> vehicleVideoUrlVOList = new ArrayList<VehicleVideoUrlVO>();
        vehicleVideoVO.setVerhicleId(vehicleId.toString());
        List<DeviceRel> deviceRelList = deviceRelClient.getEntityRels(vehicleId, CommonConstant.ENTITY_TYPE.VEHICLE).getData();
        if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
            String token = sysClient.getMiniCreateToken(false).getData();
            if (!StringUtil.isNotBlank(token)) {
                throw new ServiceException("获取点创科技Token失败");
            }
            String value = DictCache.getValue(CommonConstant.DICT_THIRD_PATH, CommonConstant.DICT_THIRD_KEY.MINICREATE_HISTORY_VIDEO_KEY);

            if (StringUtil.isNotBlank(device) && StringUtil.isNotBlank(channel)) {
                DeviceInfo deviceInfo = deviceClient.getDeviceById(device).getData();
                if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                    List<DeviceExt> deviceExtList = deviceClient.getExtInfoByParam(Long.parseLong(device), DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID).getData();
                    String guid = "";
                    if (ObjectUtil.isNotEmpty(deviceExtList) && deviceExtList.size() > 0) {
                        guid = deviceExtList.get(0).getAttrValue();
                    }
                    SimInfo simInfo = simClient.getSimByDeviceId(Long.parseLong(device)).getData();
                    if (StringUtil.isNotBlank(guid) && ObjectUtil.isNotEmpty(simInfo)) {
                        VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                        vehicleVideoUrlVO.setDeviceId(device);
                        vehicleVideoUrlVO.setChannelSeq(channel);
                        String uri = value.split(" ")[1];
                        String simCode2 = simInfo.getSimCode2();
                        uri = StrUtil.format(uri, guid, simCode2, channel, startTime, endTime, token);
                        String resStr = null;
                        try {
                            log.info("OkhttpUtil Start ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                            resStr = OkhttpUtil.getSync(uri).body().string();
                        } catch (IOException e) {
                            log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                            throw new ServiceException("获取通道[" + channel + "]历史视频地址失败");
                        }
                        log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                        if (ObjectUtil.isNotEmpty(resStr)) {
                            cn.hutool.json.JSONObject res = JSONUtil.parseObj(resStr);
                            int videoErrCode = res.getInt("videoErrCode");
                            vehicleVideoUrlVO.setVideoErrCode(videoErrCode);
                            if (0 == videoErrCode) {
                                String videoUri = res.getStr("Url");
                                if (ObjectUtil.isNotEmpty(isTransfer) && isTransfer) {
                                    videoUri = videoUri.replace("m3u8", "flv");
                                }
                                vehicleVideoUrlVO.setUrl(videoUri);
                            }
                            if (1 == videoErrCode) {
                                sysClient.getMiniCreateToken(true).getData();
                            }
                        }
                        vehicleVideoUrlVOList.add(vehicleVideoUrlVO);

                    }
                }
            } else {
                for (DeviceRel deviceRel : deviceRelList) {
                    Long deviceId = deviceRel.getDeviceId();
                    DeviceInfo deviceInfo = deviceClient.getDeviceById(deviceId.toString()).getData();
                    if (ObjectUtil.isNotEmpty(deviceInfo) && DeviceConstant.DeviceFactory.MINICREATE.equals(deviceInfo.getDeviceFactory())) {
                        List<DeviceChannel> deviceChannelList = deviceClient.getChannelInfoByDeviceId(deviceId).getData();
                        if (ObjectUtil.isNotEmpty(deviceChannelList) && deviceChannelList.size() > 0) {
                            List<DeviceExt> deviceExtList = deviceClient.getExtInfoByParam(deviceId, DeviceConstant.DeviceExtAttrId.miniCreateDeviceGUID).getData();
                            String guid = "";
                            if (ObjectUtil.isNotEmpty(deviceExtList) && deviceExtList.size() > 0) {
                                guid = deviceExtList.get(0).getAttrValue();
                            }
                            SimInfo simInfo = simClient.getSimByDeviceId(deviceId).getData();
                            if (StringUtil.isNotBlank(guid) && ObjectUtil.isNotEmpty(simInfo)) {
                                for (DeviceChannel deviceChannel : deviceChannelList) {
                                    String channelSeq = deviceChannel.getChannelSeq();
                                    VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
                                    vehicleVideoUrlVO.setDeviceId(deviceId.toString());
                                    vehicleVideoUrlVO.setChannelSeq(channelSeq);
                                    String uri = value.split(" ")[1];
                                    String simCode2 = simInfo.getSimCode2();
                                    uri = StrUtil.format(uri, guid, simCode2, channelSeq, startTime, endTime, token);
                                    String resStr = null;
                                    try {
                                        log.info("OkhttpUtil Start ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                                        resStr = OkhttpUtil.getSync(uri).body().string();
                                    } catch (IOException e) {
                                        log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                                        throw new ServiceException("获取通道[" + channelSeq + "]历史视频地址失败");
                                    }
                                    log.info("OkhttpUtil End ----- " + TimeUtil.getYYYYMMDDHHMMSS(new Date()));
                                    if (ObjectUtil.isNotEmpty(resStr)) {
                                        cn.hutool.json.JSONObject res = JSONUtil.parseObj(resStr);
                                        int videoErrCode = res.getInt("videoErrCode");
                                        vehicleVideoUrlVO.setVideoErrCode(videoErrCode);
                                        if (0 == videoErrCode) {
                                            String videoUri = res.getStr("Url");
                                            if (ObjectUtil.isNotEmpty(isTransfer) && isTransfer) {
                                                videoUri = videoUri.replace("m3u8", "flv");
                                            }
                                            vehicleVideoUrlVO.setUrl(videoUri);
                                        }
                                        if (1 == videoErrCode) {
                                            sysClient.getMiniCreateToken(true).getData();
                                        }
                                    }
                                    vehicleVideoUrlVOList.add(vehicleVideoUrlVO);
                                }
                            }

                        }
                    }
                }
            }
            vehicleVideoVO.setVehicleVideoUrlVOList(vehicleVideoUrlVOList);
        } else {
            throw new ServiceException("该车辆尚未绑定监控设备");
        }
        return vehicleVideoVO;
    }

    /**
     * 请求深圳点创科技获取url
     *
     * @return
     */
    private VehicleVideoUrlVO callMiniCreate(Long deviceId, String channelNo, String startTime, String endTime) {
        VehicleVideoUrlVO vehicleVideoUrlVO = new VehicleVideoUrlVO();
        vehicleVideoUrlVO.setDeviceId(String.valueOf(deviceId));
        vehicleVideoUrlVO.setChannelSeq(channelNo);
        SimInfo simInfo = simClient.getSimByDeviceId(deviceId).getData();
        if (simInfo == null || StringUtils.isEmpty(simInfo.getSimCode2())) {
            throw new ServiceException("该设备未绑定SIM卡,无法播放视频");
        }

        String token = sysClient.getMiniCreateToken(false).getData();
//        String token = "CF51DCIJIBVLLMQWV3EGMCM0CQEDDFJ1";
        if (!StringUtil.isNotBlank(token)) {
            throw new ServiceException("获取点创科技Token失败");
        }

        try {
            String requestUrl = "http://47.96.66.82:5000" + "/Api/PlayVideo/PlayBack/Start";
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("SimNo", simInfo.getSimCode2());
            params.put("ChannelId", channelNo);
            params.put("AVType", 0);
            params.put("BitStream", 1);
            params.put("MemoryType", 1);
            params.put("PlayBackType", 1);
            params.put("PlayBackTimes", 0);
            params.put("StartTime", startTime);
            params.put("EndTime", endTime);
            params.put("Token", token);
            params.put("Version", "1.0.1");
            HttpResponse resp = HttpUtil.createRequest(Method.GET, requestUrl)
                    .contentType(ContentType.FORM_URLENCODED.toString())
                    .form(params)
                    .execute();
            if (resp.getStatus() != 200) {
                System.out.println(resp.body());
                throw new ServiceException(resp.body());
            }
            String resBody = resp.body();
            if (StringUtils.isNotBlank(resBody)) {
                JSONObject result = JSON.parseObject(resBody);
                Integer videoErrCode = result.getInteger("videoErrCode");
                vehicleVideoUrlVO.setVideoErrCode(videoErrCode);
                if (videoErrCode == 0) {
                    vehicleVideoUrlVO.setUrl(result.getString("Url"));
                }
            }
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return vehicleVideoUrlVO;
    }

    @Override
    public void reSyncDevices() {
        List<VehicleInfo> vehicleList = list();
        for (VehicleInfo vehicleInfo : vehicleList) {
            List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(vehicleInfo.getId(), vehicleInfo.getTenantId());
            if (deviceRelList != null && !deviceRelList.isEmpty()) {
                for (DeviceRel deviceRel : deviceRelList) {
                    DeviceInfo device = DeviceCache.getDeviceById(vehicleInfo.getTenantId(), deviceRel.getDeviceId());
                    if (device != null && StringUtil.isNotBlank(device.getDeviceCode())) {
                        VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();
                        BeanUtil.copy(vehicleInfo, vehicleInfoVO);
                        syncDeviceRel(vehicleInfoVO, device.getDeviceCode(), BigDataHttpClient.OptFlag.EDIT);
                    }
                }
            }
        }
    }

    @Override
    public Long getVehicleRealWorkingArea(Long vehicleId) {
        Long vehicleRealWorkingArea = 0l;

        List<WorkareaRel> workareaRelList = workareaRelClient.getByEntityIdAndType(vehicleId, VehicleConstant.WORKAREA_REL_VEHICLE).getData();
        if (ObjectUtil.isEmpty(workareaRelList)) {
            return vehicleRealWorkingArea;
        }

        Long workareaId = workareaRelList.get(0).getWorkareaId();
        WorkareaInfo workareaInfo = workareaClient.getWorkInfoById(workareaId).getData();
        if (ObjectUtil.isEmpty(workareaInfo)) {
            return vehicleRealWorkingArea;
        }
        String width = workareaInfo.getWidth();
        if (ObjectUtil.isEmpty(width)) {
            return vehicleRealWorkingArea;
        }

        List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(vehicleId, AuthUtil.getTenantId());
        if (deviceRelList != null && !deviceRelList.isEmpty()) {
            for (DeviceRel deviceRel : deviceRelList) {
                DeviceInfo device = DeviceCache.getDeviceById(AuthUtil.getTenantId(), deviceRel.getDeviceId());
                if (device != null && StringUtil.isNotBlank(device.getDeviceCode())) {
                    if (DeviceConstant.DeviceFactory.MINICREATE.equals(device.getDeviceFactory())
                            && (DeviceConstant.DeviceCategory.VEHICLE_NVR_MONITOR_DEVICE.equals(device.getEntityCategoryId()) ||
                            DeviceConstant.DeviceCategory.VEHICLE_CVR_MONITOR_DEVICE.equals(device.getEntityCategoryId()))) {
                        //调用大数据
                        Map<String, Object> param = new HashMap<String, Object>();

                        param.put("deviceId", device.getId());
                        try {
                            JSONObject res = JSON.parseObject(BigDataHttpClient.getBigDataBody(BigDataHttpClient.track, param));
                            if (res.getInteger("code") == 0) {
                                String realWorkLength = res.getJSONObject("data").getJSONObject("statistics").getString("total_distance_work");
                                if (ObjectUtil.isNotEmpty(realWorkLength)) {
                                    vehicleRealWorkingArea = Long.parseLong(realWorkLength) * Long.parseLong(width);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new ServiceException("调用大数据获取车辆实时工作路线星级长度失败");
                        }
                    }
                }
            }
        }
        return vehicleRealWorkingArea;
    }

    /**
     * 获取车辆工作状态今日实时数据统计
     *
     * @param tenantId 租户ID
     * @return
     */
    @Override
    public SummaryDataForVehicle getSummaryDataForVehicleToday(String tenantId) {
        org.springframework.data.mongodb.core.query.Query query = org.springframework.data.mongodb.core.query.Query.query(Criteria.where("tenantId").is(tenantId)
                .and("gpsDeviceCode").ne(null)
        );
        List<BasicVehicleInfoDTO> allVehicle = mongoTemplate.find(query, BasicVehicleInfoDTO.class);

        return null;
    }

    /**
     * 获取车辆设备状态实时统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public VehicleDeviceStatusCountDTO getVehicleDeviceStatusCount(String tenantId) {
        QueryWrapper<VehicleInfo> queryWrapper = new QueryWrapper<VehicleInfo>();
        queryWrapper.select("acc_device_status", "count(id) as count")
                .groupBy("acc_device_status")
                .eq("tenant_id", tenantId)
                .eq("is_used", VehicleConstant.VehicleState.IN_USED);
        List<Map<String, Object>> result = baseMapper.selectMaps(queryWrapper);
        VehicleDeviceStatusCountDTO vehicleDeviceStatusCountDTO = new VehicleDeviceStatusCountDTO();
        DeviceStatusCountDTO deviceStatusCountDTO = new DeviceStatusCountDTO();
        if (CollUtil.isNotEmpty(result)) {
            result.stream().forEach(deviceStatusResult -> {
                Integer accDeviceStatus = Integer.valueOf(deviceStatusResult.get("acc_device_status").toString());
                Integer count = Integer.valueOf(deviceStatusResult.get("count").toString());
                switch (accDeviceStatus.toString()) {
                    case DeviceConstant.DeviceStatus.NO:
                        deviceStatusCountDTO.setNoSingleCount(count);
                        break;
                    case DeviceConstant.DeviceStatus.NO_DEV:
                        deviceStatusCountDTO.setUnBindCount(count);
                        break;
                    case DeviceConstant.DeviceStatus.OFF:
                        deviceStatusCountDTO.setOffCount(count);
                        break;
                    case DeviceConstant.DeviceStatus.ON:
                        deviceStatusCountDTO.setNormalCount(count);
                        break;
                    default:
                        deviceStatusCountDTO.setErrorOffCount(count);
                }
            });
            vehicleDeviceStatusCountDTO.setVehicleCount(deviceStatusCountDTO.getAllStatusCount());
            vehicleDeviceStatusCountDTO.setOnVehicleCount(deviceStatusCountDTO.getNormalCount());
            vehicleDeviceStatusCountDTO.setOffVehicleCount(deviceStatusCountDTO.getOffCount() + deviceStatusCountDTO.getErrorOffCount());
            vehicleDeviceStatusCountDTO.setNodVehicleCount(deviceStatusCountDTO.getUnBindCount() + deviceStatusCountDTO.getNoSingleCount());
        }
        vehicleDeviceStatusCountDTO.setProjectCode(tenantId);
        return vehicleDeviceStatusCountDTO;
    }

    /**
     * 批量获取车辆设备状态实时统计
     *
     * @param projectCode
     * @return
     */
    @Override
    public List<VehicleDeviceStatusCountDTO> listVehicleDeviceStatusCount(String projectCode) {
        if (StringUtils.isEmpty(projectCode)) {
            throw new ServiceException("项目编号不能为空");
        }
        List<String> projectCodeList = Func.toStrList(projectCode);
        if (CollUtil.isNotEmpty(projectCodeList)) {
            List<VehicleDeviceStatusCountDTO> list = projectCodeList.parallelStream().map(projectCodeStr ->{
                return this.getVehicleDeviceStatusCount(projectCodeStr);
            }).collect(Collectors.toList());
            return list;
        }
        return null;
    }

    @Override
    @Async
    public Future<IPage<VehicleInfo>> pageByCompanyId(Query query, String companyId) {
        QueryWrapper<VehicleInfo> queryWrapper = new QueryWrapper<VehicleInfo>();
        queryWrapper.lambda().eq(VehicleInfo::getIsUsed, VehicleConstant.VehicleState.IN_USED);
        List<Project> projectList = projectClient.getProjectByAdcode(130900L).getData();
        if (cn.hutool.core.util.ObjectUtil.isNotEmpty(projectList) && projectList.size() > 0) {
            List<String> projectCodeList = projectList.stream().map(Project::getProjectCode).collect(Collectors.toList());
            queryWrapper.lambda().in(VehicleInfo::getTenantId, projectCodeList);
        } else {
            queryWrapper.lambda().eq(VehicleInfo::getTenantId, "-1");
        }
        IPage<VehicleInfo> pages = page(Condition.getPage(query), queryWrapper);
        return new AsyncResult(pages);
    }

    /**
     * 根据车牌号获取车辆轨迹数据
     *
     * @param plateNumber
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<SimpleVehicleTrackInfoDTO> getVehicleTrackInfoByPlateNumber(String plateNumber, String beginTime, String endTime) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("plateNumber").is(plateNumber));
        List<BasicVehicleInfoDTO> vehicleList = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        List<SimpleVehicleTrackInfoDTO> vehicleTrackInfoDTOList = new ArrayList<SimpleVehicleTrackInfoDTO>();
        if (CollUtil.isNotEmpty(vehicleList)) {
            BasicVehicleInfoDTO vehicleInfoDTO = vehicleList.get(0);
            SimpleVehicleTrackInfoDTO simpleVehicleTrackInfoDTO = this.getSimpleVehicleTrackInfo(vehicleInfoDTO, beginTime, endTime);
            vehicleTrackInfoDTOList.add(simpleVehicleTrackInfoDTO);
            return vehicleTrackInfoDTOList;
        }
        return null;
    }

    /**
     * 根据项目编号获取车辆轨迹数据
     *
     * @param projectCode
     * @param statDate
     * @return
     */
    @Override
    public List<SimpleVehicleTrackInfoDTO> getVehicleTrackInfoByProjectCode(String projectCode, String statDate) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("projectCode").is(projectCode).and("statDate").is(statDate));
        List<SimpleVehicleTrackInfoDTO> vehicleList = mongoTemplate.find(query, SimpleVehicleTrackInfoDTO.class);
        return vehicleList;
    }

    /**
     * 根据项目编号、日期统计车辆行驶汇总数据
     *
     * @param projectCode
     * @param statDate
     */
    @Override
    @Async
    public void statVehicleTrackInfoByProjectCode(String projectCode, String statDate) {
        String beginTime = statDate + "000000";
        String endTime = statDate + "235959";
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
        query.addCriteria(Criteria.where("tenantId").is(projectCode));
        List<BasicVehicleInfoDTO> vehicleList = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
        log.info("车辆聚合数据共有{}条记录", vehicleList.size());
        vehicleList = vehicleList.stream().distinct().collect(Collectors.toList());
        log.info("车辆聚合数据去重后共有{}条记录", vehicleList.size());
        if (CollUtil.isNotEmpty(vehicleList)) {
            log.info("从大数据侧查询[{}]轨迹开始,共有{}辆车需要查询", statDate, vehicleList.size());
            long startTime = System.currentTimeMillis();
            List<SimpleVehicleTrackInfoDTO> vehicleTrackInfoDTOList = vehicleList.stream().map(vehicleInfoDTO -> {
                log.debug("统计车辆[{}]:", vehicleInfoDTO.getPlateNumber());
                return this.getSimpleVehicleTrackInfo(vehicleInfoDTO, beginTime, endTime);
            }).collect(Collectors.toList());
            long endTiime = System.currentTimeMillis();
            Double duration = (endTiime - startTime) / 1000.00;
            log.info("从大数据侧查询[{}]轨迹结束,共有:{}条记录,耗时{}秒", statDate, vehicleTrackInfoDTOList.size(), duration);
            vehicleTrackInfoDTOList = vehicleTrackInfoDTOList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            log.info("去除空元素后共有:{}条记录", vehicleTrackInfoDTOList.size());
            vehicleTrackInfoDTOList = vehicleTrackInfoDTOList.stream().distinct().collect(Collectors.toList());
            log.info("去除主键相同元素后共有:{}条记录", vehicleTrackInfoDTOList.size());

            //保存到mongo数据库中,先删除旧数据
            org.springframework.data.mongodb.core.query.Query trackQuery = new org.springframework.data.mongodb.core.query.Query();
            String queryDate = statDate.substring(0, 4) + "-" + statDate.substring(4, 6) + "-" + statDate.substring(6, 8);
            trackQuery.addCriteria(Criteria.where("statDate").is(queryDate).and("projectCode").is(projectCode));
            mongoTemplate.remove(trackQuery, SimpleVehicleTrackInfoDTO.class);
            if (CollUtil.isNotEmpty(vehicleTrackInfoDTOList)) {
//                vehicleTrackInfoDTOList.parallelStream().forEach(simpleVehicleTrackInfoDTO -> {
//                    if(simpleVehicleTrackInfoDTO != null && simpleVehicleTrackInfoDTO.getId() != null){
//                        mongoTemplate.save(simpleVehicleTrackInfoDTO);
//                    }
//                });
                mongoTemplate.insert(vehicleTrackInfoDTOList, SimpleVehicleTrackInfoDTO.class);
                log.info("数据统计结束[{}]共插入{}条记录", statDate, vehicleTrackInfoDTOList.size());
            } else {
                log.error("未从大数据侧查询到任何数据[项目:{}]", projectCode);
            }
        }
    }

    /**
     * 根据车辆数据查询轨迹
     *
     * @param vehicleInfoDTO
     * @param beginTime
     * @param endTime
     * @return
     */
    private SimpleVehicleTrackInfoDTO getSimpleVehicleTrackInfo(BasicVehicleInfoDTO vehicleInfoDTO, String beginTime, String endTime) {
        if (StringUtils.isNotEmpty(vehicleInfoDTO.getGpsDeviceCode())) {
            Map<String, Object> params = new HashMap<>();
            params.put("deviceId", vehicleInfoDTO.getGpsDeviceCode());
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
            SimpleVehicleTrackInfoDTO vehicleTrackInfoDTO = null;
            long startTimeD = 0L;
            long duration = 0L;
            try {
                String requestUrl = BigDataHttpClient.getBigdataAddr().concat(BigDataHttpClient.track.substring(1));
                startTimeD = System.currentTimeMillis();
                //设置超时时间30秒
                HttpRequest httpRequest = HttpRequest.get(requestUrl).form(params).timeout(30000);
                String res = httpRequest.execute().body();
//                String res = HttpUtil.get(requestUrl, params);
                BigDataRespDto bigDataBody = JSONUtil.toBean(res, BigDataRespDto.class);
//                BigDataRespDto bigDataBody = BigDataHttpClient.getBigDataBodyToObjcet(BigDataHttpClient.track, params, BigDataRespDto.class);
                vehicleTrackInfoDTO = this.buildSimpleVehicleTrackInfoDTO(vehicleInfoDTO, beginTime, endTime);
                List<TrackPositionDto> data = bigDataBody.getData();
                if (CollectionUtil.isNotEmpty(data)) {
                    TrackPositionDto trackPositionDto = data.get(0);
                    TrackPositionDto.Statistics statisticsInfo = trackPositionDto.getStatistics();
                    if (statisticsInfo != null) {
                        vehicleTrackInfoDTO.setTotalDistance(statisticsInfo.getTotalDistance());
                        vehicleTrackInfoDTO.setTotalWorkDistance(statisticsInfo.getTotalDistanceWork());
                        vehicleTrackInfoDTO.setAvgSpeed(statisticsInfo.getAvgSpeed());
                        vehicleTrackInfoDTO.setMaxSpeed(statisticsInfo.getMaxSpeed());
                        vehicleTrackInfoDTO.setTotalCount(statisticsInfo.getTotalCount());
                    }
                }
                return vehicleTrackInfoDTO;
            } catch (Exception e) {
                vehicleTrackInfoDTO = this.buildSimpleVehicleTrackInfoDTO(vehicleInfoDTO, beginTime, endTime);
                long endTimeD = System.currentTimeMillis();
                duration = endTimeD - startTimeD;
                log.error("从大数据侧获取车辆轨迹数据失败[车牌号:{}][{}],耗时{}秒:", vehicleInfoDTO.getPlateNumber(), JSON.toJSON(params), Double.valueOf(duration / 1000.00), e.getMessage());
            } finally {
                return vehicleTrackInfoDTO;
            }
        }
        return null;
    }

    /**
     * 构造DTO对象
     *
     * @param vehicleInfoDTO
     * @param beginTime
     * @param endTime
     * @return
     */
    private SimpleVehicleTrackInfoDTO buildSimpleVehicleTrackInfoDTO(BasicVehicleInfoDTO vehicleInfoDTO, String beginTime, String endTime) {
        SimpleVehicleTrackInfoDTO vehicleTrackInfoDTO = new SimpleVehicleTrackInfoDTO();
        vehicleTrackInfoDTO.setId(IdUtil.objectId());
        vehicleTrackInfoDTO.setVehicleId(String.valueOf(vehicleInfoDTO.getId()));
        vehicleTrackInfoDTO.setPlateNumber(vehicleInfoDTO.getPlateNumber());
        vehicleTrackInfoDTO.setGpsDeviceCode(vehicleInfoDTO.getGpsDeviceCode());
        vehicleTrackInfoDTO.setProjectCode(vehicleInfoDTO.getTenantId());
        vehicleTrackInfoDTO.setProjectName(ProjectCache.getProjectNameByCode(vehicleInfoDTO.getTenantId()));
        vehicleTrackInfoDTO.setBeginTime(beginTime);
        vehicleTrackInfoDTO.setEndTime(endTime);
        vehicleTrackInfoDTO.setStatDate(beginTime.substring(0, 4) + "-" + beginTime.substring(4, 6) + "-" + beginTime.substring(6, 8));
        vehicleTrackInfoDTO.setTotalCount("0");
        vehicleTrackInfoDTO.setTotalDistance("0");
        vehicleTrackInfoDTO.setTotalWorkDistance("0");
        vehicleTrackInfoDTO.setAvgSpeed("0.0");
        vehicleTrackInfoDTO.setMaxSpeed("0.0");
        return vehicleTrackInfoDTO;
    }
}
