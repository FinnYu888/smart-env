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
package com.ai.apac.smartenv.person.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.address.vo.PersonTrackModelVO;
import com.ai.apac.smartenv.address.vo.VehicleTrackModelVO;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.omnic.dto.ThirdSyncImportResultModel;
import com.ai.apac.smartenv.omnic.entity.AiMapping;
import com.ai.apac.smartenv.omnic.feign.IMappingClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.person.dto.PersonImportResultModel;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.service.IPersonAsyncService;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.vo.PersonImportResultVO;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.entity.Dept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import java.io.ByteArrayOutputStream;

import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
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
public class PersonAsyncServiceImpl implements IPersonAsyncService {

    private IPersonService personService;

    private IMappingClient mappingClient;

    private IOssClient ossClient;

    private IDeviceRelClient deviceRelClient;

    private IDeviceClient deviceClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean thirdPersonInfoAsync(List<List<String>> datasList, String tenantId, String actionType,Boolean isAsyn) {
        if(ObjectUtil.isEmpty(actionType)){
            /**
             * 对实时调用接口，我们支持新增或者更新，需求根据code是否存在来判断操作类型
             */
            AiMapping reqMapping = new AiMapping();
            reqMapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
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
                resultModel.setType(OmnicConstant.THIRD_INFO_TYPE.PERSON);
                resultModel.setStatus("1");
                Person person = new Person();
                //如果操作符是更新或者删除
                if (!OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                    AiMapping mapping = new AiMapping();
                    mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
                    mapping.setThirdCode(datas.get(0));
                    mapping.setTenantId(tenantId);
                    AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                    if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                        person = personService.getById(mappingRes.getSscpCode());
                        //如果根据第三方code没有找到人员，直接返回这次的结果
                        if (ObjectUtil.isEmpty(person)) {
                            if(!isAsyn){
                                throw new ServiceException("人员" + datas.get(0) + "不存在");
                            }
                            resultModel.setStatus("0");
                            resultModel.setReason("人员" + datas.get(0) + "不存在");
                            resultModelList.add(resultModel);
                            continue;
                        } else {
                            //不管是更新还是删除,先把设备先解绑了
                            Long personId = person.getId();
                            deviceClient.unbindDevice(personId, CommonConstant.ENTITY_TYPE.PERSON);
                            //如果是删除,把人删了。
                            if (OmnicConstant.ACTION_TYPE.DELETE.equals(actionType)) {
                                personService.removeById(personId);
                                mappingClient.delMapping(personId.toString(), Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
                                resultModelList.add(resultModel);
                                continue;
                            }
                        }
                    } else {
                        if(!isAsyn){
                            throw new ServiceException("人员" + datas.get(0) + "不存在");
                        }
                        resultModel.setStatus("0");
                        resultModel.setReason("人员" + datas.get(0) + "不存在");
                        resultModelList.add(resultModel);
                        continue;
                    }
                } else {
                    person.setTenantId(tenantId);
                    person.setWatchDeviceStatus(-1L);
                    person.setIsIncumbency(1);
                }
                String deviceIds = "";
                checkPersonInfo(resultModel, datas, person,isAsyn);
                if (resultModel.getStatus().equals("1")) {
                    personService.saveOrUpdate(person);
                    if (OmnicConstant.ACTION_TYPE.NEW.equals(actionType)) {
                        AiMapping mapping0 = new AiMapping();
                        mapping0.setTenantId(tenantId);
                        mapping0.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.PERSON));
                        mapping0.setSscpCode(person.getId().toString());
                        mapping0.setThirdCode(datas.get(0));
                        mappingClient.saveMappingCode(mapping0);
                    }
                    if (ObjectUtil.isNotEmpty(datas.get(9))) {
                        String[] thirdCodeList = datas.get(9).split("\\|");
                        if (thirdCodeList.length > 0) {
                            for (String thirdCode : thirdCodeList) {
                                AiMapping mapping = new AiMapping();
                                mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEVICE));
                                mapping.setThirdCode(thirdCode);
                                mapping.setTenantId(person.getTenantId());
                                AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
                                if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                                    deviceIds = deviceIds + mappingRes.getSscpCode() + ",";
                                }
                            }
                            deviceIds = deviceIds.substring(0, deviceIds.length() - 1);
                        }
                    }
                    if (ObjectUtil.isNotEmpty(deviceIds)) {
                        deviceRelClient.bindDevice(CommonConstant.ENTITY_TYPE.PERSON.toString(), person.getId(), deviceIds);
                    }
                }
            } catch (Exception ex) {
                if(!isAsyn){
                    throw new ServiceException("人员导入" + datas.get(0) + "失败");
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
        String data = ossClient.putBase64Stream(AddressConstant.BUCKET, "第三方" + tenantId + "人员导入结果.xlsx", stringData).getData();
        log.info("URL------------------" + data);
    }


    private void checkPersonInfo(ThirdSyncImportResultModel resultModel, List<String> datas, Person person,Boolean isAsyn) {
        if (ObjectUtil.isNotEmpty(datas.get(1))) {
            person.setPersonName(datas.get(1));
        } else {
            if(!isAsyn){
                throw new ServiceException("人员" + datas.get(0) + "不存在");
            }
            resultModel.setStatus("0");
            resultModel.setReason("人员姓名为空");
        }
        person.setPersonName(datas.get(1));
        person.setGender(1);
        if (datas.get(2).equals("女") || datas.get(2).equals("2")) {
            person.setGender(2);
        }

        if (ObjectUtil.isNotEmpty(datas.get(4))) {
            person.setIdCardType(1);
            person.setIdCard(datas.get(4));
        }
        if (ObjectUtil.isNotEmpty(datas.get(5))) {
            person.setMobileNumber(datas.get(5));
        }
        if (ObjectUtil.isNotEmpty(datas.get(6))) {
            person.setEmail(datas.get(6));
        }

        if (ObjectUtil.isNotEmpty(datas.get(7))) {
            AiMapping mapping = new AiMapping();
            mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.STATION));
            mapping.setThirdCode(datas.get(7));
            mapping.setTenantId(AuthUtil.getTenantId());
            AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
            if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                person.setPersonPositionId(Long.parseLong(mappingRes.getSscpCode()));
            }
        }

        if (ObjectUtil.isNotEmpty(datas.get(8))) {
            AiMapping mapping = new AiMapping();
            mapping.setCodeType(Integer.parseInt(OmnicConstant.THIRD_INFO_TYPE.DEPT));
            mapping.setThirdCode(datas.get(8));
            mapping.setTenantId(person.getTenantId());
            AiMapping mappingRes = mappingClient.getSscpCodeByThirdCode(mapping).getData();
            if (ObjectUtil.isNotEmpty(mappingRes) && ObjectUtil.isNotEmpty(mappingRes.getSscpCode())) {
                person.setPersonDeptId(Long.parseLong(mappingRes.getSscpCode()));
            }
        }

        if (ObjectUtil.isNotEmpty(datas.get(10))) {
            person.setEntryTime(TimeUtil.stringParseTimeStamp(datas.get(10)));
        }

    }


    @Async("importThreadPool")
//	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    @Override
    public Future<PersonImportResultVO> importPersonInfo(Object object, BladeUser user, HashMap<String, Long> stationMap) throws InterruptedException {
        PersonImportResultVO result = new PersonImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<PersonImportResultModel> failRecords = new ArrayList<>();
        List<PersonImportResultModel> allRecords = new ArrayList<>();
        PersonImportResultModel currentModel = new PersonImportResultModel();
        try {
            // 获取每行数据
            List<String> params = new ArrayList<>();
            for (Object o : (List<?>) object) {
                params.add(String.class.cast(o));
            }
            // 导入结果对象
            if (params.size() > PersonConstant.ExcelImportIndex.PERSON_NAME) {
                currentModel.setPersonName(params.get(PersonConstant.ExcelImportIndex.PERSON_NAME));
            }
            if (params.size() > PersonConstant.ExcelImportIndex.JOB_NUMBER) {
                currentModel.setJobNumber(params.get(PersonConstant.ExcelImportIndex.JOB_NUMBER));
            }
            if (params.size() > PersonConstant.ExcelImportIndex.PERSON_DEPT_ID) {
                currentModel.setPersonDeptId(params.get(PersonConstant.ExcelImportIndex.PERSON_DEPT_ID));
            }
            if (params.size() > PersonConstant.ExcelImportIndex.ID_CARD) {
                currentModel.setIdCard(params.get(PersonConstant.ExcelImportIndex.ID_CARD));
            }
            if (params.size() > PersonConstant.ExcelImportIndex.MOBILE_NUMBER) {
                currentModel.setMobileNumber(params.get(PersonConstant.ExcelImportIndex.MOBILE_NUMBER));
            }
//            if (params.size() > PersonConstant.ExcelImportIndex.EMAIL) {
//                currentModel.setEmail(params.get(PersonConstant.ExcelImportIndex.EMAIL));
//            }
            if (params.size() > PersonConstant.ExcelImportIndex.ENTRY_TIME) {
                currentModel.setEntryTime(params.get(PersonConstant.ExcelImportIndex.ENTRY_TIME));
            }
            if (params.size() > PersonConstant.ExcelImportIndex.GENDER) {
                currentModel.setGender(params.get(PersonConstant.ExcelImportIndex.GENDER));
            }
            if (params.size() > PersonConstant.ExcelImportIndex.PERSON_POSITION_NAME) {
                currentModel.setPersonPositionName(params.get(PersonConstant.ExcelImportIndex.PERSON_POSITION_NAME));
            }
            // 校验数据
            verifyParamForImport(currentModel, stationMap);
            Dept dept = DeptCache.getDept(Long.parseLong(currentModel.getPersonDeptId()));
            if (dept == null || dept.getId() == null
                    || (user != null && !user.getTenantId().equals(dept.getTenantId()))) {
                throw new ServiceException("部门编号不正确");
            }
            // 保存
            PersonVO person = new PersonVO();
            person.setPersonName(currentModel.getPersonName());
            person.setJobNumber(currentModel.getJobNumber());
            person.setPersonDeptId(Long.parseLong(currentModel.getPersonDeptId()));
            person.setIdCard(currentModel.getIdCard());
            person.setMobileNumber(currentModel.getMobileNumber());
//            person.setEmail(currentModel.getEmail());
            person.setEntryTime(DateUtil.parse(currentModel.getEntryTime(), DateUtil.PATTERN_DATE));
            if (StringUtils.isNotBlank(currentModel.getGender())) {
                person.setGender(Integer.parseInt(currentModel.getGender()));
            }
            if (StringUtils.isNotBlank(currentModel.getPersonPositionName())) {
                person.setPersonPositionId(stationMap.get(currentModel.getPersonPositionName()));
            }
            person.setIdCardType(1);
            person.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
            person.setIsImport(true);
            person.setCreateUser(user.getUserId());
            person.setUpdateUser(user.getUserId());
            person.setCreateDept(Long.parseLong(user.getDeptId()));
            person.setTenantId(user.getTenantId());
            personService.savePersonInfo(person);
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
        return new AsyncResult<PersonImportResultVO>(result);
    }

    private void verifyParamForImport(PersonImportResultModel currentModel, HashMap<String, Long> stationMap) {
        // 部门
        if (StringUtils.isBlank(currentModel.getPersonDeptId())) {
            throw new ServiceException("需要输入所属部门");
        }
        try {
            Long.parseLong(currentModel.getPersonDeptId());
        } catch (Exception e) {
            throw new ServiceException("所属部门编号格式不正确");
        }
        Dept dept = DeptCache.getDept(Long.parseLong(currentModel.getPersonDeptId()));
        if (dept == null || dept.getId() == null || dept.getId() <= 0) {
            throw new ServiceException("所属部门编号格式不正确");
        }
        // 手机号
		/*if (StringUtils.isBlank(currentModel.getMobileNumber())) {
		    throw new ServiceException(getExceptionMsg(PersonConstant.PersonException.KEY_NEED_MOBILE_NUMBER));
		}
		try {
		    Long.parseLong(currentModel.getMobileNumber());
		} catch (Exception e) {
		    throw new ServiceException(getExceptionMsg(PersonConstant.PersonException.KEY_MOBILE_NUMBER_WRONG));
		}*/
        // 入职日期
        if (StringUtils.isBlank(currentModel.getEntryTime())) {
            throw new ServiceException("需要输入入职日期");
        }
        try {
            DateUtil.parse(currentModel.getEntryTime(), DateUtil.PATTERN_DATE);
        } catch (Exception e) {
            throw new ServiceException("入职日期格式不正确");
        }
        // 性别
        if (StringUtils.isNotBlank(currentModel.getGender())) {
            try {
                Integer.parseInt(currentModel.getGender());
            } catch (Exception e) {
                throw new ServiceException("性别输入错误");
            }
            String genderName = DictCache.getValue(PersonConstant.DictCode.GENDER, Integer.parseInt(currentModel.getGender()));
            if (genderName == null || "".equals(genderName)) {
                throw new ServiceException("性别输入错误");
            }
        }
        // 岗位
        if (StringUtils.isNotBlank(currentModel.getPersonPositionName())) {
            Long personPositionId = stationMap.get(currentModel.getPersonPositionName());
            if (personPositionId == null || personPositionId <= 0) {
                throw new ServiceException("岗位输入错误");
            }
        }
    }

    /**
     * 将人员设备数据同步刷新到Mongo中
     *
     * @param projectCode
     */
    @Override
    public void syncPersonDeviceStatusToMongo(String projectCode) {
        List<Person> personList = personService.list(new LambdaQueryWrapper<Person>()
                .eq(Person::getTenantId, projectCode));
        HashMap<Long, Long> personDeviceStatusMap = new HashMap<Long, Long>();
        if (CollUtil.isNotEmpty(personList)) {
            for (Person person : personList) {
                personDeviceStatusMap.put(person.getId(), person.getWatchDeviceStatus());
            }
            org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query();
            query.addCriteria(Criteria.where("tenantId").is(projectCode));
            List<BasicPersonDTO> basicPersonList = mongoTemplate.find(query, BasicPersonDTO.class);
            if (CollUtil.isNotEmpty(basicPersonList)) {
                basicPersonList.parallelStream().forEach(basicPersonInfo -> {
                    org.springframework.data.mongodb.core.query.Query updateQuery = new org.springframework.data.mongodb.core.query.Query();
                    updateQuery.addCriteria(Criteria.where("id").is(basicPersonInfo.getId()));
                    Update update = Update.update("deviceStatus", personDeviceStatusMap.get(basicPersonInfo.getId()));
                    mongoTemplate.updateFirst(updateQuery, update, BasicPersonDTO.class);
                });
            }
        }
    }
}
