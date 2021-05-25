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
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonVehicleRelClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.EntityCategoryCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.dto.VehicleInfoImportResultModel;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.service.IVehicleAsyncService;
import com.ai.apac.smartenv.vehicle.service.IVehicleInfoService;
import com.ai.apac.smartenv.vehicle.vo.VehicleImportResultVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;

import java.io.ByteArrayOutputStream;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

@Service
@Slf4j
@AllArgsConstructor
public class VehicleAsyncServiceImpl implements IVehicleAsyncService {

    private IMappingClient mappingClient;

    private IDeviceRelClient deviceRelClient;

    private IDeviceClient deviceClient;

    private IOssClient ossClient;


    private IVehicleInfoService vehicleInfoService;

    private IPersonVehicleRelClient personVehicleRelClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean thirdVehicleInfoAsync(List<List<String>> datasList, String tenantId, String actionType,Boolean isAsyn) {
        if(ObjectUtil.isEmpty(actionType)){
            /**
             * 对实时调用接口，我们支持新增或者更新，需求根据code是否存在来判断操作类型
             */
            AiMapping reqMapping = new AiMapping();
            reqMapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
            reqMapping.setThirdCode(datasList.get(0).get(0));
            reqMapping.setTenantId(tenantId);
            AiMapping resMapping = mappingClient.getSscpCodeByThirdCode(reqMapping).getData();
            if(ObjectUtil.isNotEmpty(resMapping) && ObjectUtil.isNotEmpty(resMapping.getSscpCode())){
                actionType = OmnicConstant.ACTION_TYPE.UPDATE;
            }else{
                actionType = OmnicConstant.ACTION_TYPE.NEW;
            }
        }
        List<ThirdSyncImportResultModel> resultModelList = new ArrayList<ThirdSyncImportResultModel>();
        for (List<String> datas : datasList) {
            ThirdSyncImportResultModel resultModel = new ThirdSyncImportResultModel();
            try {
                resultModel.setCode(datas.get(0));
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.VEHICLE);
                resultModel.setStatus("1");
                VehicleInfo vehicleInfo = new VehicleInfo();
                //如果操作符是更新或者删除
                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        vehicleInfo = vehicleInfoService.getById(mappingRes.getSscpCode());
                        //如果根据第三方code没有找到车辆，直接返回这次的结果
                        if (ObjectUtil.isEmpty(vehicleInfo)) {
                            if(!isAsyn){
                                continue;
                            }
                            resultModel.setStatus("0");
                            resultModel.setReason("车辆" + datas.get(0) + "不存在");
                            resultModelList.add(resultModel);
                            continue;
                        } else {
                            //对更新操作先把设备先解绑了
                            Long vehiceId = vehicleInfo.getId();
                            deviceClient.unbindDevice(vehiceId, CommonConstant.ENTITY_TYPE.VEHICLE);
                            //对更新操作先把驾驶员先解绑了
                            personVehicleRelClient.unbindPerson(vehiceId);
                            //如果是删除操作,把车删了。
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                vehicleInfoService.removeById(vehiceId);
                                mappingClient.delMapping(vehiceId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
                                resultModelList.add(resultModel);
                                continue;//这条数据就处理完成了，跳出循环
                            }
                        }
                    } else {
                        if(!isAsyn){
                            continue;
                        }
                        resultModel.setStatus("0");
                        resultModel.setReason("车辆" + datas.get(0) + "不存在");
                        resultModelList.add(resultModel);
                        continue;
                    }
                }else {
                    vehicleInfo.setTenantId(tenantId);
                    vehicleInfo.setAccDeviceStatus(-1L);
                    vehicleInfo.setIsUsed(VehicleConstant.VehicleState.IN_USED);
                    vehicleInfo.setDeptAddTime(TimeUtil.getDefaultSysDate());
                    vehicleInfo.setKindCode(VehicleConstant.KindCode.MOTOR);
                    vehicleInfo.setEntityCategoryId(VehicleConstant.MOTOR_CATEGORY.SSC);
                }
                String deviceIds = "";
                checkVehicleInfo(resultModel, datas, vehicleInfo,isAsyn);
                if (resultModel.getStatus().equals("1")) {
                    vehicleInfoService.saveOrUpdate(vehicleInfo);//新增或者更新车辆信息
                    if (OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping0 = new AiMapping();
                        mapping0.setTenantId(tenantId);
                        mapping0.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
                        mapping0.setSscpCode(vehicleInfo.getId().toString());
                        mapping0.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping0);
                    }

                    if (ObjectUtil.isNotEmpty(datas.get(5))) {
                        String[] thirdCodeList = datas.get(5).split("\\|");
                        if (thirdCodeList.length > 0) {
                            for (String thirdCode : thirdCodeList) {
                                AiMapping mapping = new AiMapping();
                                mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
                                mapping.setThirdCode(thirdCode);
                                mapping.setTenantId(vehicleInfo.getTenantId());
                                AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                                if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                                    deviceIds = deviceIds + mappingRes.getSscpCode() + ",";
                                }
                            }
                            //再补充个ACC设备
                            DeviceInfo deviceInfo = new DeviceInfo();
                            deviceInfo.setDeviceCode(thirdCodeList[0] + "ACC");
                            deviceInfo.setDeviceName(thirdCodeList[0] + "ACC");
                            deviceInfo.setEntityCategoryId(DeviceConstant.DeviceCategory.VEHICLE_ACC_DEVICE);
                            deviceInfo.setDeviceStatus(Long.parseLong(DeviceConstant.DeviceStatus.NO));
                            deviceInfo.setTenantId(vehicleInfo.getTenantId());
                            deviceInfo = deviceClient.saveDeviceInfo(deviceInfo).getData();
                            deviceIds = deviceIds + deviceInfo.getId();
                        }
                    }
                    if (ObjectUtil.isNotEmpty(deviceIds)) {
                        deviceRelClient.bindDevice(CommonConstant.ENTITY_TYPE.VEHICLE.toString(), vehicleInfo.getId(), deviceIds);
                    }

                    if (ObjectUtil.isNotEmpty(datas.get(6))) {
                        AiMapping mapping = new AiMapping();
                        mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
                        mapping.setThirdCode(datas.get(5));
                        mapping.setTenantId(AuthUtil.getTenantId());
                        AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                        if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                            personVehicleRelClient.bindVehicle(mappingRes.getSscpCode(), vehicleInfo.getId().toString(), tenantId);
                        }
                    }
                }
            } catch (Exception ex) {
                if(!isAsyn){
                    throw new ServiceException("车辆导入失败");
                }
                resultModel.setStatus("0");
                resultModel.setReason(ex.getMessage());
            }
            log.info("resultModel-----------------" + resultModel.toString());
            resultModelList.add(resultModel);
        }

        if(isAsyn){
            saveResultModel(resultModelList,tenantId);
        }

        return true;
    }



    private void saveResultModel(List<ThirdSyncImportResultModel> resultModelList,String tenantId){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ExcelWriter writer = new ExcelWriter(byteArrayOutputStream, ExcelTypeEnum.XLSX);
        Sheet sheet1 = new Sheet(1, 0, ThirdSyncImportResultModel.class);

        sheet1.setSheetName("sheet1");
        writer.write(resultModelList, sheet1);
        writer.finish();

        byte[] bytes = byteArrayOutputStream.toByteArray();
        String stringData = Base64Utils.encodeToString(bytes);
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "第三方" + tenantId + "车辆导入结果.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }


    private void checkVehicleInfo(ThirdSyncImportResultModel resultModel, List<String> datas, VehicleInfo vehicleInfo,Boolean isAsyn) {
        if (ObjectUtil.isNotEmpty(datas.get(1))) {
            vehicleInfo.setPlateNumber(datas.get(1));
        } else {
            if(!isAsyn){
                throw new ServiceException("车牌号码" + datas.get(0) + "不能为空");
            }
            resultModel.setStatus("0");
            resultModel.setReason("车牌号不能为空");
        }

        if (ObjectUtil.isNotEmpty(datas.get(3))) {
            vehicleInfo.setBrand(datas.get(3));
        }


        if (ObjectUtil.isNotEmpty(datas.get(4))) {
            AiMapping mapping = new AiMapping();
            mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
            mapping.setThirdCode(datas.get(4));
            mapping.setTenantId(vehicleInfo.getTenantId());
            AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
            if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                vehicleInfo.setDeptId(Long.parseLong(mappingRes.getSscpCode()));
            }
        }


    }


    @Async("importThreadPool")
//	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    @Override
    public Future<VehicleImportResultVO> importVehicleInfo(Object object, BladeUser user) throws InterruptedException {
        VehicleImportResultVO result = new VehicleImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<VehicleInfoImportResultModel> failRecords = new ArrayList<>();
        List<VehicleInfoImportResultModel> allRecords = new ArrayList<>();
        VehicleInfoImportResultModel currentModel = new VehicleInfoImportResultModel();
        try {
            // 获取每行数据
            List<String> params = new ArrayList<>();
            for (Object o : (List<?>) object) {
                params.add(String.class.cast(o));
            }
            // 导入结果对象
            currentModel = new VehicleInfoImportResultModel();
            if (params.size() > VehicleConstant.ExcelImportIndex.PLATE_NUMBER) {
                currentModel.setPlateNumber(params.get(VehicleConstant.ExcelImportIndex.PLATE_NUMBER));
            }
            if (params.size() > VehicleConstant.ExcelImportIndex.ENTITY_CATEGORY_ID) {
                currentModel.setEntityCategoryId(params.get(VehicleConstant.ExcelImportIndex.ENTITY_CATEGORY_ID));
            }
            if (params.size() > VehicleConstant.ExcelImportIndex.DEPT_ID) {
                currentModel.setDeptId(params.get(VehicleConstant.ExcelImportIndex.DEPT_ID));
            }
            if (params.size() > VehicleConstant.ExcelImportIndex.DEPT_ADD_TIME) {
                currentModel.setDeptAddTime(params.get(VehicleConstant.ExcelImportIndex.DEPT_ADD_TIME));
            }
            // 校验数据
            verifyParamForImport(currentModel);
            Dept dept = DeptCache.getDept(Long.parseLong(currentModel.getDeptId()));
            if (dept == null || dept.getId() == null
                    || (user != null && !user.getTenantId().equals(dept.getTenantId()))) {
                throw new ServiceException("部门编号不正确");
            }
            // 保存
            VehicleInfoVO vehicleInfo = new VehicleInfoVO();
            vehicleInfo.setPlateNumber(currentModel.getPlateNumber());
            long entityCategoryId = Long.parseLong(currentModel.getEntityCategoryId());
            vehicleInfo.setEntityCategoryId(entityCategoryId);
            EntityCategory entityCategory = EntityCategoryCache.getCategoryById(entityCategoryId);
            if (entityCategory == null || entityCategory.getParentCategoryId() == null) {
                throw new ServiceException("车辆类型编码格式不正确");
            }
            vehicleInfo.setKindCode(entityCategory.getParentCategoryId());
            vehicleInfo.setDeptId(Long.parseLong(currentModel.getDeptId()));
            vehicleInfo.setDeptAddTime(DateUtil.parse(currentModel.getDeptAddTime(), DateUtil.PATTERN_DATE));
            vehicleInfo.setIsUsed(VehicleConstant.VehicleState.IN_USED);
            vehicleInfo.setCreateUser(user.getUserId());
            vehicleInfo.setUpdateUser(user.getUserId());
            vehicleInfo.setCreateDept(Long.parseLong(user.getDeptId()));
            vehicleInfo.setTenantId(user.getTenantId());
            vehicleInfoService.saveVehicleInfo(vehicleInfo);
            // 保存成功
            successCount++;
            currentModel.setStatus("成功");
            allRecords.add(currentModel);
        } catch (Exception e) {
            failCount++;
            currentModel.setStatus("失败");
            currentModel.setReason(e.getMessage());
            failRecords.add(currentModel);
            allRecords.add(currentModel);
        }
        result.setSuccessCount(successCount);
        result.setFailCount(failCount);
        result.setFailRecords(failRecords);
        result.setAllRecords(allRecords);
        return new AsyncResult<VehicleImportResultVO>(result);
    }


    private void verifyParamForImport(VehicleInfoImportResultModel currentModel) {
        if (StringUtils.isBlank(currentModel.getPlateNumber())) {
            throw new ServiceException("需要输入车牌号码");
        }
        if (StringUtils.isBlank(currentModel.getEntityCategoryId())) {
            throw new ServiceException("需要输入车辆类型");
        }
        try {
            Long.parseLong(currentModel.getEntityCategoryId());
        } catch (Exception e) {
            throw new ServiceException("车辆类型编码格式不正确");
        }
        if (StringUtils.isBlank(currentModel.getDeptId())) {
            throw new ServiceException("需要输入所属部门");
        }
        try {
            Long.parseLong(currentModel.getDeptId());
        } catch (Exception e) {
            throw new ServiceException("部门编码格式不正确");
        }
        if (StringUtils.isBlank(currentModel.getDeptAddTime())) {
            throw new ServiceException("需要输入加入日期");
        }
        try {
            DateUtil.parse(currentModel.getDeptAddTime(), DateUtil.PATTERN_DATE);
        } catch (Exception e) {
            throw new ServiceException("加入日期格式不正确");
        }
    }

    /**
     * 根据项目编号将目前在册车辆的设备状态刷新到MongoDB中去
     *
     * @param projectCode
     */
    @Override
    public void syncVehicleDeviceStatusToMongo(String projectCode) {
        List<VehicleInfo> vehicleInfoList = vehicleInfoService.list(new LambdaQueryWrapper<VehicleInfo>()
                .eq(VehicleInfo::getTenantId, projectCode)
                .eq(VehicleInfo::getIsUsed, VehicleConstant.VehicleState.IN_USED));
        HashMap<Long, Long> vehicleDeviceStatusMap = new HashMap<Long, Long>();
        if (CollUtil.isNotEmpty(vehicleInfoList)) {
            for (VehicleInfo vehicleInfo : vehicleInfoList) {
                vehicleDeviceStatusMap.put(vehicleInfo.getId(), vehicleInfo.getAccDeviceStatus());
            }
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("tenantId").is(projectCode));
            List<BasicVehicleInfoDTO> basicVehicleInfoList = mongoTemplate.find(query, BasicVehicleInfoDTO.class);
            if (CollUtil.isNotEmpty(basicVehicleInfoList)) {
                basicVehicleInfoList.parallelStream().forEach(basicVehicleInfo -> {
                    org.springframework.data.mongodb.core.query.Query updateQuery = new org.springframework.data.mongodb.core.query.Query();
                    updateQuery.addCriteria(Criteria.where("id").is(basicVehicleInfo.getId()));
                    Update update = Update.update("deviceStatus", vehicleDeviceStatusMap.get(basicVehicleInfo.getId()));
                    mongoTemplate.updateFirst(updateQuery, update, BasicVehicleInfoDTO.class);
                });
            }
        }
    }

}
