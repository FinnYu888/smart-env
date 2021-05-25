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
package com.ai.apac.smartenv.vehicle.controller;

import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.system.cache.*;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.vo.EntityCategoryVO;
import com.ai.apac.smartenv.vehicle.cache.VehicleCategoryCache;
import com.ai.apac.smartenv.vehicle.dto.VehicleBasicInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.SimpleVehicleTrackInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.service.IVehicleCategoryService;
import com.ai.apac.smartenv.vehicle.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.vo.DictVO;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoImportResultModel;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoExcelModel;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.wrapper.VehicleInfoWrapper;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.ai.apac.smartenv.vehicle.service.IVehicleAsyncService;
import com.ai.apac.smartenv.vehicle.service.IVehicleExtService;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;


/**
 * 车辆基本信息表 控制器
 *
 * @author Blade
 * @since 2020-01-16
 */
@RestController
@AllArgsConstructor
@RequestMapping("/vehicleinfo")
@Api(value = "车辆基本信息表", tags = "车辆基本信息表接口")
@Slf4j
public class VehicleInfoController extends BladeController {

    private IVehicleInfoService vehicleInfoService;
    private IVehicleExtService vehicleExtService;
    private IPersonVehicleRelClient personVehicleRelClient;
    private IOssClient ossClient;
    private ISysClient sysClient;
    private IDictClient dictClient;
    private IDeviceRelClient deviceRelClient;
    private IDeviceClient deviceClient;
    private IEntityCategoryClient categoryClient;
    private BladeRedisCache bladeRedisCache;
    private IScheduleClient scheduleClient;
    private IVehicleAsyncService vehicleAsyncService;
    private IEntityCategoryClient entityCategoryClient;
    private IProjectClient projectClient;
    private IVehicleCategoryService vehicleCategoryService;


    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入vehicleInfo")
    @ApiLog(value = "查询车辆详情")
    public R<VehicleInfoVO> detail(VehicleInfo vehicleInfo) {
        VehicleInfo detail = VehicleCache.getVehicleById(null, vehicleInfo.getId());
        VehicleInfoVO entityVO = VehicleInfoWrapper.build().entityVO(detail);
        entityVO = getVehicleAllInfoByVO(entityVO, true, true, true);
        return R.data(entityVO);
    }

    /**
     * 分页 车辆基本信息表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入vehicleInfo")
    @ApiLog(value = "查询车辆列表")
    public R<IPage<VehicleInfoVO>> list(VehicleInfo vehicleInfo, Query query, String vehicleState,
                                        @RequestParam(name = "deviceStatus", required = false) String deviceStatus,
                                        @RequestParam(name = "isBindTerminal", required = false) String isBindTerminal) {
        //vehicleInfo.setIsUsed(vehicleState);
        String seq = "";
        Boolean isExclude = true;
        List<Long> excludeIdList = new ArrayList<Long>();
//        if(ObjectUtil.isNotEmpty(deviceStatus)){
//            if(DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus.toString())){
//                //查绑定了ACC设备的实体ID
//                seq = "SELECT DISTINCT entity_id FROM ai_device_rel WHERE IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + " AND device_id IN (SELECT id FROM ai_device_info WHERE entity_category_id = " + DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE + " AND IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + ")";
//            }else{
//                isExclude = false;
//                //绑定了ACC设备且设备状态为deviceStatus的实体ID
//                seq = "SELECT DISTINCT entity_id FROM ai_device_rel WHERE IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + " AND device_id IN (SELECT id FROM ai_device_info WHERE entity_category_id = " + DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE + " AND device_status = " + deviceStatus + " AND IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + ")";
//            }
//        }
        IPage<VehicleInfoVO> pageVO = vehicleInfoService.selectVehicleInfoVOPage(vehicleInfo, query, deviceStatus, isBindTerminal, vehicleState);
//        IPage<VehicleInfoVO> pageVO = VehicleInfoWrapper.build().pageVO(pages);
        List<VehicleInfoVO> records = pageVO.getRecords();
        // 迭代获取其它信息
        getVehicleAllInfoByVO4List(records);
        return R.data(pageVO);

    }

    /**
     * 查询要绑定车辆信息
     */
    @GetMapping("/pageForPerson")
    @ApiOperationSupport(order = 8)
    @ApiLog(value = "查询可绑定车辆列表")
    @ApiOperation(value = "查询要绑定车辆信息", notes = "传入person，vehicleId")
    public R<IPage<VehicleInfoVO>> pageForPerson(VehicleInfoVO vehicle, Query query, Long personId) {
        IPage<VehicleInfo> pages = vehicleInfoService.pageForPerson(vehicle, query, personId);
        IPage<VehicleInfoVO> pageVO = VehicleInfoWrapper.build().pageVO(pages);
        List<VehicleInfoVO> records = pageVO.getRecords();
        records.forEach(record -> {
            record = getVehicleAllInfoByVO(record, true, true, false);
        });
        return R.data(pageVO);
    }

    /**
     * @param vehicleInfoVO
     * @param needPicture   是否需要查询图片
     * @param needDriver    是否需要查询驾驶员
     * @param needDevice
     * @return
     * @Function: VehicleInfoController::getVehicleAllInfoByVO
     * @Description: 补全车辆信息
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月12日 上午11:34:10
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    private VehicleInfoVO getVehicleAllInfoByVO(VehicleInfoVO vehicleInfoVO, boolean needPicture, boolean needDriver, boolean needDevice) {
        if (vehicleInfoVO == null || vehicleInfoVO.getId() == null) {
            return vehicleInfoVO;
        }
        Long vehicleId = vehicleInfoVO.getId();
        if (needPicture) {
            vehicleInfoVO = vehicleExtService.getPictures(vehicleInfoVO);
            if (StringUtils.isNotBlank(vehicleInfoVO.getVehiclePicName())) {
                vehicleInfoVO.setVehiclePicLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, vehicleInfoVO.getVehiclePicName()).getData());
            }
            if (StringUtils.isNotBlank(vehicleInfoVO.getDrivingPicFirstName())) {
                vehicleInfoVO.setDrivingPicFirstLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, vehicleInfoVO.getDrivingPicFirstName()).getData());
            }
            if (StringUtils.isNotBlank(vehicleInfoVO.getDrivingPicSecondName())) {
                vehicleInfoVO.setDrivingPicSecondLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, vehicleInfoVO.getDrivingPicSecondName()).getData());
            }
        }
        if (needDriver) {
            // 获取驾驶员
            List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(vehicleId).getData();
            if (personVehicleRelList.size() > 0) {
                List<VehicleDriverVO> vehicleDriverVOList = new ArrayList<VehicleDriverVO>();
                personVehicleRelList.forEach(personVehicleRel -> {
                    Person person = PersonCache.getPersonById(null, personVehicleRel.getPersonId());
                    if (person != null && person.getId() != null && person.getId() > 0) {
                        VehicleDriverVO vehicleDriverVO = BeanUtil.copy(person, VehicleDriverVO.class);
                        vehicleDriverVO.setId(personVehicleRel.getId());
                        vehicleDriverVOList.add(vehicleDriverVO);
                    }
                });
                vehicleInfoVO.setVehicleDriverVO(vehicleDriverVOList);
            }
        }
        // 车辆大类
        if (vehicleInfoVO.getKindCode() != null) {
            String categoryName = VehicleCategoryCache.getCategoryNameByCode(vehicleInfoVO.getKindCode().toString(),AuthUtil.getTenantId());
            //String categoryName = EntityCategoryCache.getCategoryNameById(vehicleInfoVO.getKindCode());
            vehicleInfoVO.setKindCodeName(categoryName);
        }
        if (needDevice) {
            // 车辆ACC状态
            vehicleInfoVO.setAccStatusId(DeviceConstant.DeviceStatus.NO_DEV);
            vehicleInfoVO.setAccStatus("未绑定设备");
            List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(vehicleInfoVO.getId(), vehicleInfoVO.getTenantId());
            if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
                vehicleInfoVO.setIsBindTerminal(VehicleConstant.VehicleRelBind.TRUE);
                List<DeviceRel> accList = new ArrayList<>();
                for (DeviceRel deviceRel : deviceRelList) {
                    DeviceInfo deviceInfo = DeviceCache.getDeviceById(vehicleInfoVO.getTenantId(), deviceRel.getDeviceId());
                    if (deviceInfo != null && ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId()) && deviceInfo
                            .getEntityCategoryId().equals(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE)) {
                        accList.add(deviceRel);
                    }
                }
                if (ObjectUtil.isNotEmpty(accList) && accList.size() > 0) {
                    String deviceStatus = DeviceCache.getDeviceStatus(vehicleInfoVO.getId(), DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE, AuthUtil.getTenantId());
                    vehicleInfoVO.setAccStatusId(deviceStatus);
                    vehicleInfoVO.setAccStatus(DictCache.getValue("device_status", deviceStatus));
                }
            } else {
                vehicleInfoVO.setIsBindTerminal(VehicleConstant.VehicleRelBind.FALSE);
            }
        }
        // 车辆小类
        if (vehicleInfoVO.getEntityCategoryId() != null) {
            String entityCategoryName = VehicleCategoryCache.getCategoryNameByCode(vehicleInfoVO.getEntityCategoryId().toString(), AuthUtil.getTenantId());

           // String categoryName = EntityCategoryCache.getCategoryNameById(vehicleInfoVO.getEntityCategoryId());
            vehicleInfoVO.setEntityCategoryName(entityCategoryName);
        }
        // 燃油类型
        if (StringUtils.isNotBlank(vehicleInfoVO.getFuelType())) {
            vehicleInfoVO.setFuelTypeName(DictCache.getValue(VehicleConstant.DICT_FUEL_TYPE, vehicleInfoVO.getFuelType()));
            if (StringUtils.isNotBlank(vehicleInfoVO.getRoz())) {
                vehicleInfoVO.setRozName(DictCache.getValue(VehicleConstant.DICT_FUEL_TYPE + "_" + vehicleInfoVO.getFuelType(), vehicleInfoVO.getRoz()));
            }
        }
        // 部门名称
        Dept dept = DeptCache.getDept(vehicleInfoVO.getDeptId());
        if (dept != null) {
            vehicleInfoVO.setDeptName(dept.getFullName());
        }
        // 车辆状态
        vehicleInfoVO.setVehicleStateName(DictCache.getValue(VehicleConstant.DICT_VEHICLE_STATE, vehicleInfoVO.getIsUsed()));
        return vehicleInfoVO;
    }

    private List<VehicleInfoVO> getVehicleAllInfoByVO4List(List<VehicleInfoVO> vehicleInfoVOS) {
        if (vehicleInfoVOS == null || vehicleInfoVOS.size() == 0) {
            return vehicleInfoVOS;
        }
        Map<String, String> picMap = new HashMap<>();
        Map<Long, List<PersonVehicleRel>> driverMap = new HashMap<>();
        List<Long> vehicleIds = new ArrayList<>();
        for (VehicleInfoVO vehicleInfoVO : vehicleInfoVOS) {
            Long vehicleId = vehicleInfoVO.getId();
            if (null != vehicleInfoVO.getVehiclePicName()) {
                picMap.put(vehicleId.toString(), vehicleInfoVO.getVehiclePicName());
            } else {
                vehicleInfoVO.setVehiclePicName(DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_VEHICLE));
                picMap.put(vehicleId.toString(), vehicleInfoVO.getVehiclePicName());
            }
            vehicleIds.add(vehicleId);

            // 获取驾驶员
//                List<PersonVehicleRel> personVehicleRelList = personVehicleRelClient.getPersonVehicleRels(vehicleId).getData();
            // 车辆大类
            if (vehicleInfoVO.getKindCode() != null) {
                String categoryName = VehicleCategoryCache.getCategoryNameByCode(vehicleInfoVO.getKindCode().toString(),AuthUtil.getTenantId());
                vehicleInfoVO.setKindCodeName(categoryName);
            }
            // 车辆ACC状态
            if (DeviceConstant.DeviceStatus.NO_DEV.equals(vehicleInfoVO.getAccDeviceStatus().toString())) {
                vehicleInfoVO.setIsBindTerminal(VehicleConstant.VehicleRelBind.FALSE);
                vehicleInfoVO.setAccStatusId(vehicleInfoVO.getAccDeviceStatus().toString());
                vehicleInfoVO.setAccStatus("未绑定设备");
            } else {
                vehicleInfoVO.setIsBindTerminal(VehicleConstant.VehicleRelBind.TRUE);
                vehicleInfoVO.setAccStatusId(String.valueOf(vehicleInfoVO.getAccDeviceStatus()));
                vehicleInfoVO.setAccStatus(DictCache.getValue("device_status", String.valueOf(vehicleInfoVO.getAccDeviceStatus())));
            }
//
//            vehicleInfoVO.setAccStatusId(DeviceConstant.DeviceStatus.NO_DEV);
//            vehicleInfoVO.setAccStatus("未绑定设备");
//            List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(vehicleInfoVO.getId(), vehicleInfoVO.getTenantId());
//            if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0 ) {
//            	vehicleInfoVO.setIsBindTerminal(VehicleConstant.VehicleRelBind.TRUE);
//            	List<DeviceRel> accList = new ArrayList<>();
//            	for (DeviceRel deviceRel : deviceRelList) {
//            		DeviceInfo deviceInfo = DeviceCache.getDeviceById(vehicleInfoVO.getTenantId(), deviceRel.getDeviceId());
//					if (deviceInfo != null && ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId()) && deviceInfo
//							.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE)) {
//                        accList.add(deviceRel);
//                    }
//            	}
//            	if(ObjectUtil.isNotEmpty(accList) && accList.size() > 0 ){
//            		String deviceStatus  = DeviceCache.getDeviceStatus(vehicleInfoVO.getId(),DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE,AuthUtil.getTenantId());
//            		vehicleInfoVO.setAccStatusId(deviceStatus);
//            		vehicleInfoVO.setAccStatus(DictCache.getValue("device_status",deviceStatus));
//            	}
//			} else {
//				vehicleInfoVO.setIsBindTerminal(VehicleConstant.VehicleRelBind.FALSE);
//			}
            // 车辆小类
            if (vehicleInfoVO.getEntityCategoryId() != null) {
                String categoryName = VehicleCategoryCache.getCategoryNameByCode(vehicleInfoVO.getEntityCategoryId().toString(),AuthUtil.getTenantId());
                vehicleInfoVO.setEntityCategoryName(categoryName);
            }
            // 燃油类型
            if (StringUtils.isNotBlank(vehicleInfoVO.getFuelType())) {
                vehicleInfoVO.setFuelTypeName(DictCache.getValue(VehicleConstant.DICT_FUEL_TYPE, vehicleInfoVO.getFuelType()));
                if (StringUtils.isNotBlank(vehicleInfoVO.getRoz())) {
                    vehicleInfoVO.setRozName(DictCache.getValue(VehicleConstant.DICT_FUEL_TYPE + "_" + vehicleInfoVO.getFuelType(), vehicleInfoVO.getRoz()));
                }
            }
            // 部门名称
            Dept dept = DeptCache.getDept(vehicleInfoVO.getDeptId());
            if (dept != null) {
                vehicleInfoVO.setDeptName(dept.getFullName());
            }
            // 车辆状态
            vehicleInfoVO.setVehicleStateName(DictCache.getValue(VehicleConstant.DICT_VEHICLE_STATE, vehicleInfoVO.getIsUsed()));

        }
        //获取所有图片的map
        R<Map<String, String>> retunpics = ossClient.getObjectLinks(VehicleConstant.OSS_BUCKET_NAME, picMap);
        if (null != retunpics && !"400".equals(retunpics.getCode())) {
            picMap = retunpics.getData();
        }
        //获取所有驾驶员的信息
        R<Map<Long, List<PersonVehicleRel>>> retunDrives = personVehicleRelClient.getPersonsVehicleRels(vehicleIds);
        if (null != retunDrives && !"400".equals(retunDrives.getCode())) {
            driverMap = retunDrives.getData();
        }
        for (VehicleInfoVO vehicleInfoVO : vehicleInfoVOS) {
            Long vehicleId = vehicleInfoVO.getId();
            if (null != picMap) {
                if (picMap.containsKey(vehicleId.toString())) {
                    vehicleInfoVO.setVehiclePicLink(picMap.get(vehicleId.toString()));
                }
            }

            if (null != driverMap) {
                if (driverMap.containsKey(vehicleId)) {
                    List<PersonVehicleRel> personVehicleRelList = driverMap.get(vehicleId);
                    if (personVehicleRelList.size() > 0) {
                        List<VehicleDriverVO> vehicleDriverVOList = new ArrayList<VehicleDriverVO>();
                        personVehicleRelList.forEach(personVehicleRel -> {
                            Person person = PersonCache.getPersonById(null, personVehicleRel.getPersonId());
                            if (person != null && person.getId() != null && person.getId() > 0) {
                                VehicleDriverVO vehicleDriverVO = BeanUtil.copy(person, VehicleDriverVO.class);
                                vehicleDriverVO.setId(personVehicleRel.getId());
                                vehicleDriverVOList.add(vehicleDriverVO);
                            }
                        });
                        vehicleInfoVO.setVehicleDriverVO(vehicleDriverVOList);
                    }
                }
            }
        }

        return vehicleInfoVOS;
    }

    @GetMapping("/tree/{type}")
    @ApiOperationSupport(order = 2)
//    @ApiLog(value = "根据类型查询车辆树")
    @ApiOperation(value = "根据类型查询车辆树", notes = "传入查询类型。1车辆类型，2为部门")
    public R<Collection<VehicleNode>> getVehicleTree(@PathVariable Integer type, BladeUser bladeUser) {
        return R.data(vehicleInfoService.getVehicleTree(type, bladeUser.getTenantId()));
//        VehicleInfo vehicleInfo = new VehicleInfo();
//        vehicleInfo.setTenantId(getUser().getTenantId());
//        vehicleInfo.setIsDeleted(0);
//        vehicleInfo.setIsUsed(1);
//        QueryWrapper<VehicleInfo> queryWrapper = Condition.getQueryWrapper(vehicleInfo);
//        queryWrapper.gt("dept_remove_time", new Timestamp(System.currentTimeMillis()));
//        List<VehicleInfo> list = vehicleInfoService.list(Condition.getQueryWrapper(vehicleInfo));
//        List<VehicleInfoVO> vehicleInfoVOS = VehicleInfoWrapper.build().listVO(list);
//
//
//        Map<Long, VehicleNode> parentMap = new HashMap<>();
//        Map<Long, VehicleNode> categoryType = new HashMap<>();
//        for (VehicleInfoVO vo : vehicleInfoVOS) {
//            VehicleNode node = new VehicleNode();
//            node.setNodeName(vo.getPlateNumber());
//            node.setId(vo.getId());
//            node.setShowFlag(false);
//            Boolean isNeedWork = scheduleClient.checkNowNeedWork(vo.getId(), ArrangeConstant.ScheduleObjectEntityType.VEHICLE).getData();
//            Long accStatus = 99L;
//            R<DeviceInfo> accDevice = deviceClient.getByEntityAndCategory(vo.getId(), CommonConstant.VEHICLE_ACC_CATEGORY_ID);
//            if (accDevice.getData() != null && accDevice.getData().getId() != null) {
//                accStatus = accDevice.getData().getDeviceStatus();
//            }
//            if (!isNeedWork) {
//                node.setStatus(3);
//            } else if (isNeedWork && accStatus == 0) {
//                node.setStatus(1);
//            } else {
//                node.setStatus(2);
//            }
//
//            String work_status = dictClient.getValue("work_status", node.getStatus().toString()).getData();
//            node.setStatusName(work_status);
//            node.setIsLastNode(true);
//            node.setStatusName(DictCache.getValue(VehicleConstant.WORK_STATUS_KEY, node.getStatus().toString()));
//            Long parentId = null;
//            if (type.equals(1)) {
//                parentId = vo.getEntityCategoryId();
//            } else {
//                parentId = vo.getDeptId();
//            }
//            VehicleNode parentNode = parentMap.get(parentId);
//            if (parentNode == null) {
//                parentNode = new VehicleNode();
//                parentNode.setId(parentId);
//                parentNode.setIsLastNode(false);
//                parentMap.put(parentId, parentNode);
//                List<VehicleNode> vehicleNodeList = new ArrayList<>();
//                parentNode.setSubNodes(vehicleNodeList);
//                String name = null;
//                if (type.equals(1)) {
//                    name = categoryClient.getCategoryName(vo.getEntityCategoryId()).getData();
//                } else {
//                    name = sysClient.getDeptName(vo.getDeptId()).getData();
//                }
//                parentNode.setNodeName(name);
//            }
//            if (type.equals(1)) {
//                node.setVehicleType(parentNode.getNodeName());
//            } else {
//                VehicleNode parentType = categoryType.get(vo.getEntityCategoryId());
//                if (parentType == null) {
//                    parentType = new VehicleNode();
//                    String data = categoryClient.getCategoryName(vo.getEntityCategoryId()).getData();
//                    parentType.setNodeName(data);
//                    parentType.setId(vo.getEntityCategoryId());
//                    categoryType.put(vo.getEntityCategoryId(), parentType);
//                }
//                node.setVehicleType(parentType.getNodeName());
//            }
//            parentNode.getSubNodes().add(node);
//        }
//        Collection<VehicleNode> values = parentMap.values();
//        return R.data(values);
    }

    /**
     * 新增 车辆基本信息表
     */
    @PostMapping("")
    @ApiOperationSupport(order = 4)
    @ApiLog(value = "新增车辆")
    @ApiOperation(value = "新增", notes = "传入vehicleInfo")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public R save(@RequestBody VehicleInfoVO vehicleInfoVO) {
        // 保存信息
        boolean save = vehicleInfoService.saveVehicleInfo(vehicleInfoVO);
        return R.status(save);
    }

    private String getExceptionMsg(String key) {
        String msg = DictBizCache.getValue(VehicleConstant.VehicleException.CODE, key);
        if (StringUtils.isBlank(msg)) {
            msg = key;
        }
        return msg;
    }

    /**
     * 修改 车辆基本信息表
     */
    @PutMapping("")
    @ApiOperationSupport(order = 5)
    @ApiLog(value = "修改车辆")
    @ApiOperation(value = "修改", notes = "传入vehicleInfo")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public R update(@Valid @RequestBody VehicleInfoVO vehicleInfo) {
        vehicleInfoService.updateVehicleInfo(vehicleInfo);
        return R.status(true);
    }

    /**
     * 删除 车辆基本信息表
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "删除车辆")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        boolean delete = vehicleInfoService.removeVehicle(Func.toLongList(ids));
        return R.status(delete);
    }

    /**
     * 获取车辆类型
     */
    @GetMapping("/getVehicleTypeDic")
    @ApiOperationSupport(order = 8)
    @ApiLog(value = "获取车辆类型")
    @ApiOperation(value = "获取车辆类型", notes = "")
    public R getVehicleTypeDic(@RequestParam(name = "categoryCode", required = false) String categoryCode,
                               @RequestParam(name = "parentCategoryId", required = false) String parentCategoryId) {
        /**
         * 以上参数定义是为了适配前端不需要改造定义的。categoryCode表示查询第几级的车辆类型，parentCategoryId实际表示表示上级车辆类型的code。
         */
        QueryWrapper<VehicleCategory> vehicleCategoryQueryWrapper = new QueryWrapper<VehicleCategory>();
        if (StringUtil.isBlank(categoryCode) || VehicleConstant.DICT_VEHICLE_CODE_1.equals(categoryCode)) {
            // 默询大类
            vehicleCategoryQueryWrapper.lambda().eq(VehicleCategory::getParentCategoryId,0);
            return R.data(vehicleCategoryService.list(vehicleCategoryQueryWrapper));
        }else if (VehicleConstant.DICT_VEHICLE_CODE_2.equals(categoryCode) && ObjectUtil.isNotEmpty(parentCategoryId)) {
            vehicleCategoryQueryWrapper.lambda().eq(VehicleCategory::getCategoryCode,parentCategoryId);
            // 车辆小类
            List<VehicleCategory> vehicleCategories = vehicleCategoryService.list(vehicleCategoryQueryWrapper);
            if(ObjectUtil.isNotEmpty(vehicleCategories)){
                vehicleCategoryQueryWrapper.clear();
                vehicleCategoryQueryWrapper.lambda().eq(VehicleCategory::getParentCategoryId,vehicleCategories.get(0).getId());
                return R.data(vehicleCategoryService.list(vehicleCategoryQueryWrapper));
            }
        }
        return R.data(null);
    }

    /**
     * @param vehicleInfo
     * @throws Exception
     * @Function: VehicleInfoController::exportVehicleInfo
     * @Description: 导出excel
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月10日 上午11:53:41
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping("/exportVehicleInfo")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导出车辆信息")
    @ApiOperation(value = "导出excel", notes = "传入vehicleInfo")
    public void exportVehicleInfo(VehicleInfoVO vehicleInfo, Integer vehicleState) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        vehicleInfo.setIsUsed(vehicleState);
        List<VehicleInfo> list = vehicleInfoService.listAll(vehicleInfo);
        List<VehicleInfoExcelModel> modelList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
        }
        list.forEach(vehicle -> {
            VehicleInfoVO vehicleInfoVO = getVehicleAllInfoByVO(VehicleInfoWrapper.build().entityVO(vehicle), false, true, false);
            VehicleInfoExcelModel vehicleInfoExcelModel = new VehicleInfoExcelModel();
            vehicleInfoExcelModel.setEntityCategoryName(vehicleInfoVO.getEntityCategoryName());
            vehicleInfoExcelModel.setPlateNumber(vehicleInfoVO.getPlateNumber());
            vehicleInfoExcelModel.setDeptName(vehicleInfoVO.getDeptName());
            List<VehicleDriverVO> vehicleDriverVOs = vehicleInfoVO.getVehicleDriverVO();
            if (vehicleDriverVOs != null && !vehicleDriverVOs.isEmpty()) {
                vehicleInfoExcelModel.setPersonName(vehicleDriverVOs.get(0).getPersonName());
                vehicleInfoExcelModel.setMobileNumber(String.valueOf(vehicleDriverVOs.get(0).getMobileNumber()));
            }
            modelList.add(vehicleInfoExcelModel);
        });
        OutputStream out = null;
        try {
            response.reset(); // 清除buffer缓存
            // 指定下载的文件名
            String fileName = "车辆基本信息导出";
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, VehicleInfoExcelModel.class);
            sheet1.setSheetName("sheet1");
            writer.write(modelList, sheet1);
            writer.finish();

        } catch (IOException e) {
            throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
            }
        }
    }

    @SuppressWarnings("finally")
    @PostMapping("/importVehicleInfo")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入车辆信息")
    @ApiOperation(value = "导入excel", notes = "传入excel")
    public R<VehicleImportResultVO> importVehicleInfo(@RequestParam("file") MultipartFile excel) throws Exception {
        VehicleImportResultVO result = new VehicleImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<VehicleInfoImportResultModel> failRecords = new ArrayList<>();
        List<VehicleInfoImportResultModel> allRecords = new ArrayList<>();
        InputStream inputStream = null;
        BladeUser user = AuthUtil.getUser();
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
            List<Object> datas = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
            if (datas == null || datas.isEmpty()) {
                throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
            }
            List<Future<VehicleImportResultVO>> futures = new ArrayList<Future<VehicleImportResultVO>>();
            // 如果连续多条记录车牌相同，有可能会车牌重复
            for (Object data : datas) {
                Future<VehicleImportResultVO> vehicleImportResult = vehicleAsyncService.importVehicleInfo(data, user);
                futures.add(vehicleImportResult);
            }
            for (Future<VehicleImportResultVO> future : futures) {
                VehicleImportResultVO vehicleImportResultVO = future.get();
                successCount += vehicleImportResultVO.getSuccessCount();
                failCount += vehicleImportResultVO.getFailCount();
                failRecords.addAll(vehicleImportResultVO.getFailRecords());
                allRecords.addAll(vehicleImportResultVO.getAllRecords());

            }
        } catch (Exception e) {
            log.error("Excel操作异常" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            result.setSuccessCount(successCount);
            result.setFailCount(failCount);
            result.setFailRecords(failRecords);

            if (failCount > 0) {
                String key = CacheNames.VEHICLE_IMPORT + ":" + DateUtil.now().getTime();
                bladeRedisCache.setEx(key, allRecords, 3600L);
                result.setFileKey(key);
            }
        }
        return R.data(result);
    }

    @GetMapping("/importVehicleInfoModel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入车辆模板下载")
    @ApiOperation(value = "导入模板下载", notes = "")
    public R importVehicleInfoModel() throws Exception {
        String name = DictCache.getValue(VehicleConstant.DICT_IMPORT_EXCEL_MODEL, VehicleConstant.DICT_IMPORT_EXCEL_MODEL_VEHICLE);
        String link = ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, name).getData();
        return R.data(link);
    }

    @GetMapping("/importResultExcel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入车辆结果下载")
    @ApiOperation(value = "导入结果下载", notes = "")
    public void getImportResultExcel(String key) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        Object object = bladeRedisCache.get(key);
        List<VehicleInfoImportResultModel> modelList = new ArrayList<>();
        for (Object o : (List<?>) object) {
            VehicleInfoImportResultModel model = BeanUtil.copy(o, VehicleInfoImportResultModel.class);
            modelList.add(model);
        }
        OutputStream out = null;
        try {
            response.reset(); // 清除buffer缓存
            String fileName = "车辆基本信息导入结果";
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, VehicleInfoImportResultModel.class);
            sheet1.setSheetName("sheet1");
            writer.write(modelList, sheet1);
            writer.finish();
        } catch (IOException e) {
            throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
            }
        }
    }

    /**
     * 获取数据字典
     */
    @GetMapping("/listBladeDict")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "获取数据字典", notes = "")
    public R listBladeDict(Dict dict) {
        List<Dict> dicts = DictCache.getList(dict.getCode());
        List<DictVO> dictVOs = new ArrayList<>();
        dicts.forEach(obj -> {
            DictVO vo = BeanUtil.copy(obj, DictVO.class);
            dictVOs.add(vo);
        });
        List<DictVO> sortCollect = dictVOs.stream().sorted(Comparator.comparing(DictVO::getSort)).collect(Collectors.toList());
        return R.data(sortCollect);
    }

    /**
     * 根据部门查询车辆树，全加载
     */
    @GetMapping("/treeByDept")
    @ApiOperationSupport(order = 11)
    @ApiLog(value = "根据部门查询车辆树，全加载")
    @ApiOperation(value = "根据部门查询车辆树，全加载", notes = "")
    public R<List<VehicleNode>> treeByDept(String nodeName, BladeUser user) {
        List<VehicleNode> nodeList = vehicleInfoService.treeByDept(nodeName, user.getTenantId(), new ArrayList<>());
        return R.data(nodeList);
    }

    @GetMapping("/listArrangeByWeek")
    @ApiOperationSupport(order = 11)
    @ApiLog(value = "排班记录查询，按车")
    @ApiOperation(value = "排班记录查询，按车", notes = "")
    public R<IPage<VehicleInfoVO>> listArrangeByWeek(VehicleInfoVO vehicleInfo, Query query, BladeUser user, String monday) {
        LocalDate mondayDate = LocalDate.parse(monday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String entityType = ArrangeConstant.ScheduleObjectEntityType.VEHICLE;// 车辆
        // 根据条件查询人员
        vehicleInfo.setTenantId(user.getTenantId());
        vehicleInfo.setIsUsed(VehicleConstant.VehicleState.IN_USED);
        IPage<VehicleInfo> pages = vehicleInfoService.page(vehicleInfo, query, null);
        IPage<VehicleInfoVO> pageVO = VehicleInfoWrapper.build().pageVO(pages);
        List<VehicleInfoVO> records = pageVO.getRecords();
        records.forEach(record -> {
            record = getVehicleAllInfoByVO(record, false, false, false);
            List<List<ScheduleObjectVO>> scheduleObjectWeekList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                List<ScheduleObjectVO> scheduleObjects = new ArrayList<>();
                // 查询一周七天排班记录
                List<ScheduleObject> scheduleObjectList = ScheduleCache.getScheduleObjectByEntityAndDate(record.getId(), entityType, mondayDate.plusDays(i));
                if (scheduleObjectList == null || scheduleObjectList.isEmpty() || scheduleObjectList.get(0).getId() == null) {
                    scheduleObjectWeekList.add(scheduleObjects);
                } else {
                    boolean needWork = false;
                    boolean needBreak = false;
                    for (ScheduleObject scheduleObject : scheduleObjectList) {
                        ScheduleObjectVO scheduleObjectVO = BeanUtil.copy(scheduleObject, ScheduleObjectVO.class);
                        if (scheduleObjectVO.getStatus() == 0) {
                            if (needBreak) {
                                continue;
                            }
                            scheduleObjectVO.setScheduleName("休息");
                            needBreak = true;
                        } else {
                            needWork = true;
                            Schedule schedule = ScheduleCache.getScheduleById(scheduleObjectVO.getScheduleId());
                            scheduleObjectVO.setScheduleName(schedule.getScheduleName());
                            scheduleObjectVO.setScheduleBeginTime(schedule.getScheduleBeginTime());
                            scheduleObjectVO.setScheduleEndTime(schedule.getScheduleEndTime());
                        }
                        scheduleObjects.add(scheduleObjectVO);
                    }
                    // 过滤多余的休息班次
                    Iterator<ScheduleObjectVO> iterator = scheduleObjects.iterator();
                    while (iterator.hasNext()) {
                        ScheduleObjectVO next = iterator.next();
                        if (needWork && next.getScheduleBeginTime() == null && next.getScheduleEndTime() == null) {
                            iterator.remove();
                        }
                    }
                    scheduleObjects.sort(Comparator.comparing(ScheduleObjectVO::getScheduleBeginTime));
                    scheduleObjectWeekList.add(scheduleObjects);
                }
            }
            record.setScheduleObjectList(scheduleObjectWeekList);
        });
        return R.data(pageVO);
    }

    /**
     * 分页 车辆基本信息表
     */
    @GetMapping("/queryByName")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "按车牌号查寻", notes = "传入车牌号")
    @ApiLog(value = "根据车牌号查询车辆信息")
    public R<List<RefuelVehicleInfoVO>> queryByName(@RequestParam(name = "plateNumber") String plateNumber) {
        VehicleInfoVO vehicleInfo = new VehicleInfoVO();
        vehicleInfo.setPlateNumber(plateNumber);
        List<VehicleInfo> listVehicle = vehicleInfoService.listAll(vehicleInfo);
        List<RefuelVehicleInfoVO> arealist = BeanUtil.copyProperties(listVehicle, RefuelVehicleInfoVO.class);
        return R.data(arealist);
    }

    /**
     * 重新向大数据同步车辆信息
     *
     * @return
     */
    @PostMapping("/reSyncDevices")
    @ApiOperationSupport(order = 14)
    @ApiOperation(value = "重新向大数据同步车辆信息", notes = "传入vehicleInfo")
    @ApiLog(value = "重新向大数据同步车辆信息")
    public R<List<VehicleMaintOrderVO>> reSyncDevices() {
        vehicleInfoService.reSyncDevices();
        return R.status(true);
    }


    /**
     * 查询公司下所有的车辆
     */
    @GetMapping("/pageByCompany")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "查询公司下所有的车辆", notes = "")
    public R<IPage<VehicleBasicInfoDTO>> pageByCompanyId(Query query, @RequestParam("companyId") String companyId) throws ExecutionException, InterruptedException {
        Future<IPage<VehicleInfo>> pagesFuture = vehicleInfoService.pageByCompanyId(query, companyId);
        IPage<VehicleInfo> page = pagesFuture.get();
        List<VehicleInfo> vehicleInfoList = page.getRecords();
        IPage<VehicleBasicInfoDTO> res = new Page<VehicleBasicInfoDTO>(query.getCurrent(), query.getSize(), page.getTotal());

        List<VehicleBasicInfoDTO> vehicleBasicInfoDTOList = new ArrayList<VehicleBasicInfoDTO>();
        vehicleInfoList.forEach(record -> {
            VehicleBasicInfoDTO vehicleBasicInfoDTO = new VehicleBasicInfoDTO();
            vehicleBasicInfoDTO.setTenantId(record.getTenantId());
            Project project = projectClient.getProjectById(record.getTenantId()).getData();
            if (ObjectUtil.isNotEmpty(project) && ObjectUtil.isNotEmpty(project.getProjectName())) {
                vehicleBasicInfoDTO.setTenantName(project.getProjectName());
            }
            vehicleBasicInfoDTO.setEntityCategoryId(record.getEntityCategoryId().toString());
            vehicleBasicInfoDTO.setEntityCategoryName(entityCategoryClient.getCategoryName(record.getEntityCategoryId()).getData());
            vehicleBasicInfoDTO.setPlateNumber(record.getPlateNumber());
            vehicleBasicInfoDTOList.add(vehicleBasicInfoDTO);
        });
        res.setRecords(vehicleBasicInfoDTOList);
        return R.data(res);
    }

    /**
     * 根据车牌号查询车辆轨迹数据
     */
    @GetMapping("/trackInfo")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "查询车辆轨迹数据", notes = "车牌号或项目编号至少填一个")
    public R<List<SimpleVehicleTrackInfoDTO>> getSimpleVehicleTrackInfo(@RequestParam(required = false) String plateNumber, @RequestParam(required = false) String projectCode, @RequestParam String statDate) {
        if (StringUtils.isEmpty(plateNumber) && StringUtils.isEmpty(projectCode)) {
            throw new ServiceException("车牌号或项目编码不能全为空");
        }
        String beginTime = statDate + "000000";
        String endTime = statDate + "235959";
        if (StringUtils.isNotEmpty(plateNumber)) {
            return R.data(vehicleInfoService.getVehicleTrackInfoByPlateNumber(plateNumber, beginTime, endTime));
        }
        if (StringUtils.isNotEmpty(projectCode)) {
            String queryDate = statDate.substring(0, 4) + "-" + statDate.substring(4, 6) + "-" + statDate.substring(6, 8);
            return R.data(vehicleInfoService.getVehicleTrackInfoByProjectCode(projectCode, queryDate));
        }
        return R.data(null);
    }

    /**
     * 根据项目编码、日期统计车辆轨迹数据
     */
    @PostMapping("/statTrackInfo")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "统计车辆轨迹数据", notes = "根据项目编码、日期统计车辆轨迹数据")
    public R statSimpleVehicleTrackInfo(@RequestParam String projectCode, @RequestParam String statDate) {
        if (StringUtils.isEmpty(statDate) && StringUtils.isEmpty(projectCode)) {
            throw new ServiceException("统计日期和项目编码不能为空");
        }
        vehicleInfoService.statVehicleTrackInfoByProjectCode(projectCode, statDate);
        return R.success("数据正在统计中,请稍候查询结果");
    }
}
