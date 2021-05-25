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
package com.ai.apac.smartenv.workarea.service.impl;

import com.ai.apac.smartenv.common.constant.AddressConstant;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.OmnicConstant;
import com.ai.apac.smartenv.common.constant.WorkAreaConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.workarea.cache.WorkareaCache;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.service.IWorkareaAsyncService;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import com.ai.apac.smartenv.workarea.service.IWorkareaNodeService;
import com.ai.apac.smartenv.workarea.service.IWorkareaRelService;

import cn.hutool.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Slf4j
public class WorkareaAsyncServiceImpl implements IWorkareaAsyncService {

    @Autowired
    private IDeviceRelClient deviceRelClient;

    @Autowired
    private IDeviceClient deviceClient;

    @Autowired
    private IWorkareaRelService workareaRelService;

    @Autowired
    @Lazy
    private IWorkareaInfoService workareaInfoService;

    @Autowired
    private IWorkareaNodeService workareaNodeService;

    @Autowired
    private IMappingClient mappingClient;

    @Autowired
    private IOssClient ossClient;


    @Override
    @Async("workareaThreadPool")
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    // 异步操作，取不到bladeUser信息
    public void bindOrUnbindAsync(List<WorkareaRel> workareaRelList, BladeUser bladeUser) {
        long entityType = 0L;
        long workAreaId = 0L;
        int count = workareaRelList.size();
        entityType = workareaRelList.get(0).getEntityType();
        workAreaId = workareaRelList.get(0).getWorkareaId();
        if (workareaRelList.get(0).getId() != null && workareaRelList.get(0).getId() != 0L) {
            count = -count;
        }
        List<String> deviceCodeList = new ArrayList<>();
        for (WorkareaRel workareaRel : workareaRelList) {
            R<List<DeviceRel>> deviceRels = deviceRelClient.getEntityRels(workareaRel.getEntityId(), workareaRel.getEntityType() == 1 ? 5 : workareaRel.getEntityType());
            if (deviceRels.getData() != null && deviceRels.getData().size() > 0) {
                for (DeviceRel datum : deviceRels.getData()) {
                    String devicecode = deviceClient.getDeviceById(String.valueOf(datum.getDeviceId())).getData().getDeviceCode();
                    deviceCodeList.add(devicecode);
                }
            }
            //调用大数据
            JSONObject param = new JSONObject();
            param.put("deviceId", deviceCodeList.toArray());
            param.put("areaId", workareaRel.getWorkareaId());
            if (workareaRel.getId() != null && workareaRel.getId() != 0L) { // 带入id是解绑删除或者编辑数据
                workareaRelService.removeById(workareaRel.getId());
                if (deviceCodeList.size() > 0) {
                    param.put("optFlag", BigDataHttpClient.OptFlag.REMOVE);
                    try {
                        BigDataHttpClient.postDataToBigData("/smartenv-api/sync/deviceAreaRel", param.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (entityType == 2L) {
                    //把这车上的驾驶员也解绑了
                    workareaInfoService.syncDriverWorkAreaRelAsync(workareaRel.getEntityId(), workareaRel.getWorkareaId(), "1", bladeUser);
                }
            } else { // 绑定新数据
                workareaRel.setCreateUser(bladeUser.getUserId());
                workareaRel.setUpdateUser(bladeUser.getUserId());
                workareaRel.setCreateDept(Long.parseLong(bladeUser.getDeptId()));
                workareaRel.setTenantId(bladeUser.getTenantId());
                workareaRelService.save(workareaRel);
                if (deviceCodeList.size() > 0) {
                    param.put("optFlag", BigDataHttpClient.OptFlag.ADD);
                    try {
                        BigDataHttpClient.postDataToBigData("/smartenv-api/sync/deviceAreaRel", param.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (entityType == 2L) {
                    //把这车上的驾驶员也绑了
                    workareaInfoService.syncDriverWorkAreaRelAsync(workareaRel.getEntityId(), workareaRel.getWorkareaId(), "2", bladeUser);
                }
            }
        }
        // 绑定解绑时需要更新绑定数量
        WorkareaInfo workareaInfo = workareaInfoService.getById(workAreaId);
        if (workareaInfo == null) {
            throw new ServiceException("No data by id:" + workAreaId);
        }
        if (WorkAreaConstant.WorkareaRelEntityType.PERSON.equals(String.valueOf(entityType))) { // 人员
            int personCount = count + (workareaInfo.getPersonCount() == null ? 0 : workareaInfo.getPersonCount().intValue());
            workareaInfo.setPersonCount((long) (Math.max(personCount, 0)));
        } else if (WorkAreaConstant.WorkareaRelEntityType.VEHICLE.equals(String.valueOf(entityType))) { // 车辆
            int vehicleCount = count + (workareaInfo.getVehicleCount() == null ? 0 : workareaInfo.getVehicleCount().intValue());
            workareaInfo.setVehicleCount((long) (Math.max(vehicleCount, 0)));
        }
        workareaInfoService.updateById(workareaInfo);

        for (WorkareaRel workareaRel : workareaRelList) {
            WorkareaCache.deleteAsyncEntity(workareaRel.getEntityId(), String.valueOf(workareaRel.getEntityType()));
        }
    }

    @Override
    public Boolean thirdWorkareaInfoAsync(List<List<String>> datasList, String tenantId,String actionType,Boolean isAsyn) {

        if(ObjectUtil.isEmpty(actionType)){
            /**
             * 对实时调用接口，我们支持新增或者更新，需求根据code是否存在来判断操作类型
             */
            AiMapping reqMapping = new AiMapping();
            reqMapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.WORKAREA));
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
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.WORKAREA);
                resultModel.setStatus("1");

                WorkareaInfo workareaInfo = new WorkareaInfo();
                //如果操作符是更新或者删除
                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.WORKAREA));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        workareaInfo = workareaInfoService.getById(mappingRes.getSscpCode());
                        //如果根据第三方code没有找到工作区域，直接返回这次的结果
                        if (ObjectUtil.isEmpty(workareaInfo)) {
                            if(!isAsyn){
                                throw new ServiceException("工作区域" + datas.get(0) + "不存在");
                            }
                            resultModel.setStatus("0");
                            resultModel.setReason("工作区域" + datas.get(0) + "不存在");
                            resultModelList.add(resultModel);
                            continue;
                        } else {
                            //不管是更新还是删除,先把工作区域的坐标点删了
                            Long workareaInfoId = workareaInfo.getId();
                            QueryWrapper<WorkareaNode> wrapper = new QueryWrapper<WorkareaNode>();
                            wrapper.lambda().eq(WorkareaNode::getWorkareaId,workareaInfoId);
                            workareaNodeService.remove(wrapper);
                            //不管是更新还是删除,先把工作区域关联实体关系删了
                            QueryWrapper<WorkareaRel> workareaRelQueryWrapper = new QueryWrapper<WorkareaRel>();
                            workareaRelQueryWrapper.lambda().eq(WorkareaRel::getWorkareaId,workareaInfoId);
                            workareaRelService.remove(workareaRelQueryWrapper);
                            //如果是删除操作,把工作区域基本信息删了。
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                workareaInfoService.removeById(workareaInfoId);
                                mappingClient.delMapping(workareaInfoId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.WORKAREA));
                                resultModelList.add(resultModel);
                                continue;
                            }
                        }
                    } else {
                        if(!isAsyn){
                            throw new ServiceException("工作区域" + datas.get(0) + "不存在");
                        }
                        resultModel.setStatus("0");
                        resultModel.setReason("工作区域" + datas.get(0) + "不存在");
                        resultModelList.add(resultModel);
                        continue;
                    }
                }else {
                    workareaInfo.setTenantId(tenantId);
                }
                List<WorkareaNode> workareaNodeList = new ArrayList<WorkareaNode>();
                checkWorkareaInfo(resultModel, datas, workareaInfo,isAsyn);
                if (resultModel.getStatus().equals("1")) {
                    workareaInfoService.saveOrUpdate(workareaInfo);
                    if(OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping0 = new AiMapping();
                        mapping0.setTenantId(tenantId);
                        mapping0.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.WORKAREA));
                        mapping0.setSscpCode(workareaInfo.getId().toString());
                        mapping0.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping0);
                    }

                    if (ObjectUtil.isNotEmpty(datas.get(3))) {
                        String[] nodeStrList = datas.get(3).split("\\|");
                        if (ObjectUtil.isNotEmpty(nodeStrList) && nodeStrList.length > 0) {
                            for (String nodeStr : nodeStrList) {
                                Long index = 1L;
                                String[] node = nodeStr.split(",");
                                if (ObjectUtil.isNotEmpty(node) && node.length == 2) {
                                    WorkareaNode workareaNode = new WorkareaNode();
                                    workareaNode.setNodeSeq(index);
                                    workareaNode.setLongitude(node[0]);
                                    workareaNode.setLatitudinal(node[1]);
                                    workareaNode.setWorkareaId(workareaInfo.getId());
                                    workareaNode.setTenantId(workareaInfo.getTenantId());
                                    index++;
                                    workareaNodeList.add(workareaNode);
                                }
                            }
                            workareaNodeService.saveBatch(workareaNodeList);
                        }
                    }

                    if (ObjectUtil.isNotEmpty(datas.get(4)) && ObjectUtil.isNotEmpty(datas.get(5)) ) {
                        List<WorkareaRel> workareaRelList = new ArrayList<WorkareaRel>();
                        String[] entityIdList = datas.get(5).split("\\|");
                        if (entityIdList.length > 0) {
                            for (String entityId : entityIdList) {
                                AiMapping mapping = new AiMapping();
                                if(datas.get(4).equals("1")){
                                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
                                }else{
                                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.VEHICLE));
                                }
                                mapping.setThirdCode(entityId);
                                mapping.setTenantId(workareaInfo.getTenantId());
                                AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                                if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                                    WorkareaRel workareaRel = new WorkareaRel();
                                    workareaRel.setEntityId(Long.parseLong(mappingRes.getSscpCode()));
                                    workareaRel.setWorkareaId(workareaInfo.getId());
                                    workareaRel.setEntityType(Long.parseLong(datas.get(4)));
                                    workareaRelList.add(workareaRel);
                                }
                            }
                        }
                        workareaRelService.saveBatch(workareaRelList);
                    }

                }
            } catch (Exception ex) {
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
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "第三方" + tenantId + "工作区域导入结果.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }

    private void checkWorkareaInfo(ThirdSyncImportResultModel resultModel, List<String> datas, WorkareaInfo workareaInfo,Boolean isAsyn) {
        if (ObjectUtil.isNotEmpty(datas.get(1))) {
            workareaInfo.setAreaName(datas.get(1));
        } else {
            if(!isAsyn){
                throw new ServiceException("工作区域/线路不能为空");
            }
            resultModel.setStatus("0");
            resultModel.setReason("工作区域/线路不能为空");
        }
        workareaInfo.setWorkAreaType(1L);
        if (ObjectUtil.isNotEmpty(datas.get(2))) {
            workareaInfo.setAreaType(Long.parseLong(datas.get(2)));
        }
    }

}
