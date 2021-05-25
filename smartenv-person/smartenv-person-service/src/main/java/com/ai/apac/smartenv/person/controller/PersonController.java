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
package com.ai.apac.smartenv.person.controller;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.arrange.vo.ScheduleObjectVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.*;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.cache.PersonUserRelCache;
import com.ai.apac.smartenv.person.dto.BodyBiologicalDTO;
import com.ai.apac.smartenv.person.dto.PersonExcelModel;
import com.ai.apac.smartenv.person.dto.PersonImportResultModel;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.service.IPersonAsyncService;
import com.ai.apac.smartenv.person.service.IPersonExtService;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonImportResultVO;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.person.wrapper.PersonWrapper;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictBizCache;
import com.ai.apac.smartenv.system.entity.Dept;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.vo.DictVO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.vo.VehicleDriverVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleImportResultVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleInfoVO;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;

/**
 * 人员信息表 控制器
 *
 * @author Blade
 * @since 2020-02-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/person")
@Api(value = "人员信息表", tags = "人员信息表接口")
@Slf4j
public class PersonController extends BladeController {

    private IPersonService personService;
    private IPersonExtService personExtService;
    private IOssClient ossClient;
    private BladeRedisCache bladeRedisCache;
    private IDictClient dictClient;
    private IDeviceRelClient deviceRelClient;
    private IDeviceClient deviceClient;
    private ISysClient sysClient;
    private IScheduleClient scheduleClient;
    private IPersonAsyncService personAsyncService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入person")
    @ApiLog(value = "查询人员详情")
    public R<PersonVO> detail(Person person) {
        Person detail = PersonCache.getPersonById(null, person.getId());
        PersonVO personVO = PersonWrapper.build().entityVO(detail);
        personVO = getPersonAllInfoByVO(personVO, true, true, true);
        return R.data(personVO);
    }

    /**
     * 分页 人员信息表
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入person")
    @ApiLog(value = "查询人员列表")
    public R<IPage<PersonVO>> list(PersonVO person, Query query,
                                   @RequestParam(name = "deviceStatus", required = false) String deviceStatus,
                                   @RequestParam(name = "isBindTerminal", required = false) String isBindTerminal) {

        String seq = "";
        List<Long> excludeIdList = new ArrayList<Long>();
//        if(ObjectUtil.isNotEmpty(deviceStatus)){
//            if(DeviceConstant.DeviceStatus.NO_DEV.equals(deviceStatus.toString())){
//                //绑定了手表设备的实体ID
//               seq = "SELECT DISTINCT entity_id FROM ai_device_rel WHERE IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + " AND device_id IN (SELECT id FROM ai_device_info WHERE entity_category_id = " + DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE + " AND IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + ")";
//            }else{
//                isExclude = false;
//                //绑定了手表设备且设备状态为deviceStatus的实体ID
//                seq = "SELECT DISTINCT entity_id FROM ai_device_rel WHERE IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + " AND device_id IN (SELECT id FROM ai_device_info WHERE entity_category_id = " + DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE + " AND device_status = " + deviceStatus + " AND IS_DELETED = 0 AND TENANT_ID = " + AuthUtil.getTenantId() + ")";
//            }
//        }
        IPage<Person> pages = personService.page(person, query, deviceStatus, isBindTerminal);
        IPage<PersonVO> pageVO = PersonWrapper.build().pageVO(pages);
        List<PersonVO> records = pageVO.getRecords();
        getPersonAllInfoByVO(records, true, true, true);


        return R.data(pageVO);
    }

    /**
     * 根据名称联想查询人员列表
     */
    @GetMapping("/personList")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "联想查询", notes = "传入personName")
    @ApiLog(value = "根据名称联想查询人员列表")
    public R<List<Person>> personList(String personName, BladeUser bladeUser) {
        List<Person> personList = personService.list(new QueryWrapper<Person>().eq("is_incumbency", 1).eq("tenant_id", bladeUser.getTenantId()).like("person_name", personName));
        return R.data(personList);
    }

    /**
     * 根据名称联想查询人员帐号信息
     */
    @GetMapping("/personAccountList")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "根据名称联想查询人员帐号信息", notes = "传入personName")
    public R<List<PersonAccountVO>> listPersonAccount(String personName){
        return R.data(PersonCache.getPersonAccount(personName));
    }

    /**
     * 新增 人员信息表
     */
    @PostMapping("")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入person")
    @ApiLog(value = "新增人员")
    public R save(@RequestBody PersonVO personVO, BladeUser bladeUser) {
        personVO.setTenantId(bladeUser.getTenantId());
        boolean save = personService.savePersonInfo(personVO);
        return R.status(save);
    }

    /**
     * 修改 人员信息表
     */
    @PutMapping("")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入person")
    @ApiLog(value = "修改人员")
    public R update(@RequestBody PersonVO person, BladeUser bladeUser) {
        personService.updatePersonInfo(person);
        return R.status(true);
    }

    /**
     * 删除 人员信息表
     */
    @DeleteMapping("")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "删除人员")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        return R.status(personService.removePerson(Func.toLongList(ids)));
    }

    /**
     * 查询绑定驾驶员信息
     */
    @GetMapping("/pageForVehicle")
    @ApiOperationSupport(order = 8)
    @ApiLog(value = "查询可绑定驾驶员信息")
    @ApiOperation(value = "查询绑定驾驶员信息", notes = "传入person，vehicleId")
    public R<IPage<PersonVO>> pageForVehicle(PersonVO person, Query query, Long vehicleId) {
        IPage<Person> pages = personService.pageForVehicle(person, query, vehicleId);
        IPage<PersonVO> pageVO = PersonWrapper.build().pageVO(pages);
        List<PersonVO> records = pageVO.getRecords();
        records.forEach(record -> {
            record = getPersonAllInfoByVO(record, false, false, false);
        });
        return R.data(pageVO);
    }

    /**
     * 获取数据字典
     */
    @GetMapping("/listBladeDict")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "获取数据字典", notes = "")
    public R listBladeDict(Dict dict) {
        List<Dict> dicts = DictCache.getList(dict.getCode());
        List<DictVO> dictVOs = new ArrayList<>();
        dicts.forEach(obj -> {
            DictVO vo = BeanUtil.copy(obj, DictVO.class);
            dictVOs.add(vo);
        });
        return R.data(dictVOs);
    }

    private PersonVO getPersonAllInfoByVO(PersonVO personVO, boolean needPicture, boolean needDevice, boolean needuser) {
        if (personVO == null || personVO.getId() == null) {
            return personVO;
        }
        // 部门名称
        Dept dept = DeptCache.getDept(personVO.getPersonDeptId());
        if (dept != null) {
            personVO.setPersonDeptName(dept.getFullName());
        }
        if (needPicture) {
            // 人员照片
            String image = personVO.getImage();
            if (StringUtils.isBlank(image)) {
                image = DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_PERSON);
            }
            personVO.setImageLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, image).getData());
            // 驾驶证正页
            personVO = personExtService.getPictures(personVO);
            if (StringUtils.isNotBlank(personVO.getDriverLicenseFirstName())) {
                personVO.setDriverLicenseFirstLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, personVO.getDriverLicenseFirstName()).getData());
            }
            if (StringUtils.isNotBlank(personVO.getDriverLicenseSecondName())) {
                personVO.setDriverLicenseSecondLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, personVO.getDriverLicenseSecondName()).getData());
            }
            // 身份证
            if (StringUtil.isNotBlank(personVO.getIdCardFront())) {
                personVO.setIdCardFrontLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, personVO.getIdCardFront()).getData());
            }
            if (StringUtil.isNotBlank(personVO.getIdCardBack())) {
                personVO.setIdCardBackLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, personVO.getIdCardBack()).getData());
            }
            // 银行卡
            if (StringUtil.isNotBlank(personVO.getBankCardFront())) {
                personVO.setBankCardFrontLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, personVO.getBankCardFront()).getData());
            }
            if (StringUtil.isNotBlank(personVO.getBankCardBack())) {
                personVO.setBankCardBackLink(ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, personVO.getBankCardBack()).getData());
            }
        }
        // 岗位
        if (personVO.getPersonPositionId() != null) {
            personVO.setPersonPositionName(StationCache.getStationName(personVO.getPersonPositionId()));
        }
        // 性别
        if (personVO.getGender() != null) {
            personVO.setGenderName(DictCache.getValue(PersonConstant.DictCode.GENDER, personVO.getGender()));
        }
        // 证件类型
        if (personVO.getIdCardType() != null) {
            personVO.setIdCardTypeName(DictCache.getValue(PersonConstant.DictCode.ID_CARD_TYPE, personVO.getIdCardType()));
        }
        // 政治面貌
        if (personVO.getPoliticalKind() != null) {
            personVO.setPoliticalKindName(DictCache.getValue(PersonConstant.DictCode.POLITICAL_KIND, personVO.getPoliticalKind()));
        }
        // 婚姻状况
        if (personVO.getMaritalStatus() != null) {
            personVO.setMaritalStatusName(DictCache.getValue(PersonConstant.DictCode.MARITAL_STATUS, personVO.getMaritalStatus()));
        }
        // 在职状态
        if (personVO.getIsIncumbency() != null) {
            personVO.setIsIncumbencyName(DictCache.getValue(PersonConstant.DictCode.INCUMBENCY_STATUS, personVO.getIsIncumbency()));
        }
        // 合同类型
        if (personVO.getContractType() != null) {
            personVO.setContractTypeName(DictCache.getValue(PersonConstant.DictCode.CONTRACT_TYPE, personVO.getContractType()));
        }
        // 学历
        if (StringUtils.isNotBlank(personVO.getEducation())) {
            personVO.setEducationName(DictCache.getValue(PersonConstant.DictCode.QUALIFICATION, personVO.getEducation()));
        }
        // 是否允许登录系统
        if (personVO.getIsUser() == null) {
            personVO.setIsUser(PersonConstant.IsUser.NO);
        }
        if (needuser) {
            // 人员关联操作员关联id
            PersonUserRel personUserRel = PersonUserRelCache.getRelByPersonId(personVO.getId());
            if (personUserRel == null || personUserRel.getId() == null) {
                personVO.setIsPersonUserRel(false);
            } else {
                personVO.setIsPersonUserRel(true);
                personVO.setPersonUserRelId(personUserRel.getId());
                User user = UserCache.getUser(personUserRel.getUserId());
                if (user != null && user.getId() != null) {
                    personVO.setUserId(user.getId());
                    personVO.setAccount(user.getAccount());
                    personVO.setRoleId(user.getRoleId());
                    personVO.setRoleName(RoleCache.roleNames(user.getRoleId()));
                }
            }
        }
        if (needDevice) {
            // 人员手表状态
            personVO.setWatchStatusId(DeviceConstant.DeviceStatus.NO_DEV);
            personVO.setWatchStatus("未绑定设备");
            List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(personVO.getId(), personVO.getTenantId());
            if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0) {
                personVO.setIsBindTerminal(PersonConstant.PersonRelBind.TRUE);
                List<DeviceRel> watchList = new ArrayList<>();
                for (DeviceRel deviceRel : deviceRelList) {
                    DeviceInfo deviceInfo = DeviceCache.getDeviceById(personVO.getTenantId(), deviceRel.getDeviceId());
                    if (deviceInfo != null && ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId()) && deviceInfo
                            .getEntityCategoryId().equals(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE)) {
                        watchList.add(deviceRel);
                    }
                }
                if (ObjectUtil.isNotEmpty(watchList) && watchList.size() > 0) {
                    String deviceStatus = DeviceCache.getDeviceStatus(personVO.getId(), DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE, AuthUtil.getTenantId());
                    personVO.setWatchStatusId(deviceStatus);
                    personVO.setWatchStatus(DictCache.getValue("device_status", deviceStatus));
                }
            } else {
                personVO.setIsBindTerminal(PersonConstant.PersonRelBind.FALSE);
            }
        }

        return personVO;
    }

    private List<PersonVO> getPersonAllInfoByVO(List<PersonVO> personVOs, boolean needPicture, boolean needDevice, boolean needuser) {
        //判空
        if (null == personVOs || personVOs.size() == 0) {
            return personVOs;
        }
        Map<String, String> picMap = new HashMap<>();
        //循环
        for (PersonVO personVO : personVOs) {
            // 部门名称
            Dept dept = DeptCache.getDept(personVO.getPersonDeptId());
            if (dept != null) {
                personVO.setPersonDeptName(dept.getFullName());
            }
            if (needPicture) {
                // 人员照片

                String image = personVO.getImage();
                if (StringUtils.isBlank(image)) {
                    image = DictCache.getValue(VehicleConstant.DICT_DEFAULT_IMAGE, VehicleConstant.DICT_DEFAULT_IMAGE_PERSON);
                }
                picMap.put(personVO.getId().toString(), image);
            }
            // 岗位
            if (personVO.getPersonPositionId() != null) {
                personVO.setPersonPositionName(StationCache.getStationName(personVO.getPersonPositionId()));
            }
            // 性别
            if (personVO.getGender() != null) {
                personVO.setGenderName(DictCache.getValue(PersonConstant.DictCode.GENDER, personVO.getGender()));
            }
            // 证件类型
            if (personVO.getIdCardType() != null) {
                personVO.setIdCardTypeName(DictCache.getValue(PersonConstant.DictCode.ID_CARD_TYPE, personVO.getIdCardType()));
            }
            // 政治面貌
            if (personVO.getPoliticalKind() != null) {
                personVO.setPoliticalKindName(DictCache.getValue(PersonConstant.DictCode.POLITICAL_KIND, personVO.getPoliticalKind()));
            }
            // 婚姻状况
            if (personVO.getMaritalStatus() != null) {
                personVO.setMaritalStatusName(DictCache.getValue(PersonConstant.DictCode.MARITAL_STATUS, personVO.getMaritalStatus()));
            }
            // 在职状态
            if (personVO.getIsIncumbency() != null) {
                personVO.setIsIncumbencyName(DictCache.getValue(PersonConstant.DictCode.INCUMBENCY_STATUS, personVO.getIsIncumbency()));
            }
            // 合同类型
            if (personVO.getContractType() != null) {
                personVO.setContractTypeName(DictCache.getValue(PersonConstant.DictCode.CONTRACT_TYPE, personVO.getContractType()));
            }
            // 学历
            if (StringUtils.isNotBlank(personVO.getEducation())) {
                personVO.setEducationName(DictCache.getValue(PersonConstant.DictCode.QUALIFICATION, personVO.getEducation()));
            }
            // 是否允许登录系统
            if (personVO.getIsUser() == null) {
                personVO.setIsUser(PersonConstant.IsUser.NO);
            }
            if (needuser) {
                // 人员关联操作员关联id
                PersonUserRel personUserRel = PersonUserRelCache.getRelByPersonId(personVO.getId());
                if (personUserRel == null || personUserRel.getId() == null) {
                    personVO.setIsPersonUserRel(false);
                } else {
                    personVO.setIsPersonUserRel(true);
                    personVO.setPersonUserRelId(personUserRel.getId());
                    User user = UserCache.getUser(personUserRel.getUserId());
                    if (user != null && user.getId() != null) {
                        personVO.setUserId(user.getId());
                        personVO.setAccount(user.getAccount());
                        personVO.setRoleId(user.getRoleId());
                        personVO.setRoleName(RoleCache.roleNames(user.getRoleId()));
                    }
                }
            }
            if (needDevice) {
                // 人员手表状态
                if (DeviceConstant.DeviceStatus.NO_DEV.equals(personVO.getWatchDeviceStatus().toString())) {
                    personVO.setWatchStatusId(personVO.getWatchDeviceStatus().toString());
                    personVO.setWatchStatus("未绑定设备");
                    personVO.setIsBindTerminal(PersonConstant.PersonRelBind.FALSE);
                } else {
                    personVO.setWatchStatusId(personVO.getWatchDeviceStatus().toString());
                    personVO.setWatchStatus(DictCache.getValue("device_status", String.valueOf(personVO.getWatchDeviceStatus())));
                    personVO.setIsBindTerminal(PersonConstant.PersonRelBind.TRUE);
                }

//
//
//                List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(personVO.getId(), personVO.getTenantId());
//                if (ObjectUtil.isNotEmpty(deviceRelList) && deviceRelList.size() > 0 ) {
//                	personVO.setIsBindTerminal(PersonConstant.PersonRelBind.TRUE);
//                	List<DeviceRel> watchList = new ArrayList<>();
//                	for (DeviceRel deviceRel : deviceRelList) {
//                		DeviceInfo deviceInfo = DeviceCache.getDeviceById(personVO.getTenantId(), deviceRel.getDeviceId());
//    					if (deviceInfo != null && ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId()) && deviceInfo
//    							.getEntityCategoryId().equals(DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE)) {
//    						watchList.add(deviceRel);
//                        }
//                	}
//                	if(ObjectUtil.isNotEmpty(watchList) && watchList.size() > 0 ){
//                		String deviceStatus  = DeviceCache.getDeviceStatus(personVO.getId(),DeviceConstant.DeviceCategory.PERSON_WATCH_DEVICE,AuthUtil.getTenantId());
//                		personVO.setWatchStatusId(deviceStatus);
//                		personVO.setWatchStatus(DictCache.getValue("device_status",deviceStatus));
//                	}
//				} else {
//					personVO.setIsBindTerminal(PersonConstant.PersonRelBind.FALSE);
//				}
            }
        }
        //获取所有图片信息
        R<Map<String, String>> retunpics = ossClient.getObjectLinks(VehicleConstant.OSS_BUCKET_NAME, picMap);
        if (null != retunpics && !"400".equals(retunpics.getCode())) {
            picMap = retunpics.getData();
        }

        for (PersonVO personVO : personVOs) {
            Long personId = personVO.getId();
            if (null != picMap) {
                if (picMap.containsKey(personId.toString())) {
                    personVO.setImageLink(picMap.get(personId.toString()));
                }
            }
        }
        return personVOs;
    }

    @GetMapping("/tree")
    @ApiOperationSupport(order = 2)
    @ApiLog(value = "根据部门查询人员树")
    @ApiOperation(value = "查询人员树", notes = "查询人员树")
    public R<Collection<PersonNode>> getPersonTree(BladeUser bladeUser) {
        return R.data(personService.getPersonTree(2, bladeUser.getTenantId()));
//        Person person = new Person();
//        person.setTenantId(getUser().getTenantId());
//        person.setIsIncumbency(1);
//        List<Person> list = personService.list(Condition.getQueryWrapper(person));
//        Map<Long, PersonNode> parentMap = new HashMap<>();
//        Map<Long, PersonNode> categoryType = new HashMap<>();
//        for (Person vo : list) {
//            PersonNode node = new PersonNode();
//            node.setNodeName(vo.getPersonName());
//            node.setJobNumber(vo.getJobNumber());
//            node.setId(vo.getId());
//            node.setShowFlag(false);
//
//            //目前先写死，等排班做好
//            node.setStatus(1);
//            node.setIsLastNode(true);
//
//            Boolean isNeedWork = scheduleClient.checkNowNeedWork(vo.getId(), ArrangeConstant.ScheduleObjectEntityType.PERSON).getData();
//            Long accStatus = 99L;
//            DeviceInfo deviceInfo = deviceClient.getByEntityAndCategory(vo.getId(), CommonConstant.PERSON_ACC_CATEGORY_ID).getData();
//            if (deviceInfo != null && deviceInfo.getId() != null) {
//
//                accStatus = deviceInfo.getDeviceStatus();
//
//            }
//            if (isNeedWork != null && !isNeedWork) {
//                node.setStatus(3);
//            } else if (isNeedWork != null && isNeedWork && accStatus == 0) {
//                node.setStatus(1);
//            } else {
//                node.setStatus(2);
//            }
//
//
////			vo.setHaveAlarm(0);
//            node.setStatusName(DictCache.getValue(VehicleConstant.WORK_STATUS_KEY, node.getStatus().toString()));
//            Long parentId = vo.getPersonDeptId();
//            PersonNode parentNode = parentMap.get(parentId);
//            if (parentNode == null) {
//                parentNode = new PersonNode();
//                parentNode.setId(parentId);
//                parentNode.setIsLastNode(false);
//                parentMap.put(parentId, parentNode);
//                List<PersonNode> vehicleNodeList = new ArrayList<>();
//                parentNode.setSubNodes(vehicleNodeList);
//                String name = sysClient.getDeptName(vo.getPersonDeptId()).getData();
//                parentNode.setNodeName(name);
//            }
//            parentNode.getSubNodes().add(node);
//        }
//        Collection<PersonNode> values = parentMap.values();
//        return R.data(values);
    }

    /**
     * 根据部门查询人员树，全加载
     */
    @GetMapping("/treeByDept")
    @ApiOperationSupport(order = 11)
    @ApiLog(value = "根据部门查询人员树，全加载")
    @ApiOperation(value = "根据部门查询人员树，全加载", notes = "")
    public R<List<PersonNode>> treeByDept(String nodeName, BladeUser user, Person person) {
        List<PersonNode> nodeList = personService.treeByDept(nodeName, user.getTenantId(), person, null);
        return R.data(nodeList);
    }

    /**
     * @param person
     * @throws Exception
     * @Function: PersonController::exportPersonInfo
     * @Description: 导出excel
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月19日 下午2:25:01
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping("/exportPersonInfo")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导出人员信息")
    @ApiOperation(value = "导出excel", notes = "传入person")
    public void exportPersonInfo(PersonVO person, BladeUser bladeUser) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        List<Person> list = personService.listAll(person);

        if (list == null || list.size() == 0) {
            throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
        }
        Map<Long, Dept> deptMap = DeptCache.getDeptName();
        Map<Long, Station> stationMap = StationCache.getStation(bladeUser.getTenantId());
        List<PersonExcelModel> modelList = new ArrayList<>();
        list.forEach(personObj -> {
            PersonVO personVO = PersonWrapper.build().entityVO(personObj);
            PersonExcelModel personExcelModel = new PersonExcelModel();
            personExcelModel.setPresonName(personVO.getPersonName());
            personExcelModel.setJobNumber(personVO.getJobNumber());
            Station station = stationMap.get(personVO.getPersonPositionId());
            if (null != station) {
                personExcelModel.setPersonPosition(station.getStationName());
            }
            Dept dept = deptMap.get(personVO.getPersonDeptId());
            if (null != dept) {
                personExcelModel.setDeptName(dept.getDeptName());
            }

            personExcelModel.setMobileNumber(String.valueOf(personVO.getMobileNumber()));
            if (null != person.getGender()) {
                if (PersonConstant.Gender.MAN.equals(person.getGender())) {
                    personExcelModel.setGender(PersonConstant.Gender.MAN_NAME);
                } else if (PersonConstant.Gender.WOMAN.equals(person.getGender())) {
                    personExcelModel.setGender(PersonConstant.Gender.WOMAN_NAME);
                }

            }

            if (personVO.getEntryTime() != null) {
                personExcelModel.setEntryDate(DateUtil.format(personVO.getEntryTime(), DateUtil.PATTERN_DATE));
            }
            modelList.add(personExcelModel);
        });
        OutputStream out = null;
        try {
            response.reset(); // 清除buffer缓存
            // 指定下载的文件名
            String fileName = "人员信息导出";
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, PersonExcelModel.class);
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
     * @return
     * @throws Exception
     * @Function: PersonController::importPersonInfo
     * @Description: 导入excel
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月19日 下午2:58:45
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @SuppressWarnings("finally")
    @PostMapping("/importPersonInfo")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入人员信息")
    @ApiOperation(value = "导入excel", notes = "传入excel")
    public R<PersonImportResultVO> importPersonInfo(@RequestParam("file") MultipartFile excel, BladeUser bladeUser) throws Exception {
        String tenantId = bladeUser.getTenantId();
        PersonImportResultVO result = new PersonImportResultVO();
        int successCount = 0;
        int failCount = 0;
        List<PersonImportResultModel> failRecords = new ArrayList<>();
        List<PersonImportResultModel> allRecords = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(excel.getInputStream());
            List<Object> datas = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
            if (datas == null || datas.isEmpty()) {
                throw new ServiceException(getExceptionMsg(VehicleConstant.VehicleException.KEY_NO_RECORDS));
            }
            // 获取该租户下所有岗位
            HashMap<String, Long> stationMap = getStationMap(tenantId);
            // 导入
            List<Future<PersonImportResultVO>> futures = new ArrayList<Future<PersonImportResultVO>>();
            for (Object object : datas) {
                Future<PersonImportResultVO> vehicleImportResult = personAsyncService.importPersonInfo(object, bladeUser, stationMap);
                futures.add(vehicleImportResult);
            }
            for (Future<PersonImportResultVO> future : futures) {
                PersonImportResultVO personImportResultVO = future.get();
                successCount += personImportResultVO.getSuccessCount();
                failCount += personImportResultVO.getFailCount();
                failRecords.addAll(personImportResultVO.getFailRecords());
                allRecords.addAll(personImportResultVO.getAllRecords());
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
                String key = CacheNames.PERSON_IMPORT + ":" + DateUtil.now().getTime();
                bladeRedisCache.setEx(key, allRecords, 3600L);
                result.setFileKey(key);
            }
        }
        return R.data(result);
    }

    private HashMap<String, Long> getStationMap(String tenantId) {
        List<Station> stationList = sysClient.getStationByTenant(tenantId).getData();
        HashMap<String, Long> stationMap = new HashMap<>();
        if (stationList != null) {
            for (Station station : stationList) {
                stationMap.put(station.getStationName(), station.getId());
            }
        }
        return stationMap;
    }

    @GetMapping("/importPersonInfoModel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入人员模板下载")
    @ApiOperation(value = "导入模板下载", notes = "")
    public R importPersonInfoModel() throws Exception {
        String name = DictCache.getValue(VehicleConstant.DICT_IMPORT_EXCEL_MODEL, VehicleConstant.DICT_IMPORT_EXCEL_MODEL_PERSON);
        String link = ossClient.getObjectLink(VehicleConstant.OSS_BUCKET_NAME, name).getData();
        return R.data(link);
    }

    @GetMapping("/importResultExcel")
    @ApiOperationSupport(order = 9)
    @ApiLog(value = "导入人员结果下载")
    @ApiOperation(value = "导入结果下载", notes = "")
    public void getImportResultExcel(String key) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        Object object = bladeRedisCache.get(key);
        List<PersonImportResultModel> modelList = new ArrayList<>();
        for (Object o : (List<?>) object) {
            PersonImportResultModel model = BeanUtil.copy(o, PersonImportResultModel.class);
            modelList.add(model);
        }
        OutputStream out = null;
        try {
            response.reset(); // 清除buffer缓存
            String fileName = "人员信息导入结果";
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, PersonImportResultModel.class);
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

    @GetMapping("/listArrangeByWeek")
    @ApiOperationSupport(order = 11)
    @ApiLog(value = "排班记录查询，按人")
    @ApiOperation(value = "排班记录查询，按人", notes = "")
    public R<IPage<PersonVO>> listArrangeByWeek(PersonVO person, Query query, BladeUser user, String monday) {
        LocalDate mondayDate = LocalDate.parse(monday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String entityType = ArrangeConstant.ScheduleObjectEntityType.PERSON;// 人员
        // 根据条件查询人员
        person.setTenantId(user.getTenantId());
        person.setIsIncumbencys(PersonConstant.IncumbencyStatus.IN_AND_TEMPORARY);
        IPage<Person> pages = personService.page(person, query, null, null);
        IPage<PersonVO> pageVO = PersonWrapper.build().pageVO(pages);
        List<PersonVO> records = pageVO.getRecords();
        records.forEach(record -> {
            record = getPersonAllInfoByVO(record, false, false, false);
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

    private String getExceptionMsg(String key) {
        String msg = DictBizCache.getValue(PersonConstant.PersonException.CODE, key);
        if (StringUtils.isBlank(msg)) {
            msg = key;
        }
        return msg;
    }

    @GetMapping("/bodyBiologicalInfo/{personId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "month", value = "查询月份(格式为yyyyMM)", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "personId", value = "人员主键", paramType = "path", dataType = "long")
    })
    @ApiOperationSupport(order = 12)
    @ApiLog(value = "查询人体生物节律")
    @ApiOperation(value = "查询人体生物节律", notes = "查询人体生物节律")
    public R<List<BodyBiologicalDTO>> getBodyBiologicalInfo(@PathVariable Long personId, @RequestParam String month) {
        return R.data(personService.getBodyBiologicalInfo(personId,month));
    }
}
