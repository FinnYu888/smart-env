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
package com.ai.apac.smartenv.person.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.dto.PersonStatusStatDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonVehicleRel;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.person.vo.PersonVO;

import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 *
 * @author zhanglei25
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_PERSON_NAME,
        fallback = IPersonClientFallback.class
)
public interface IPersonClient {

    String API_PREFIX = "/client";
    String PERSON = API_PREFIX + "/person";
    String LIST_PERSON = API_PREFIX + "/list-person";
    String PERSON_LIST = API_PREFIX + "/person-list";
    String GET_PERSON_BY_COND = API_PREFIX + "/get-person-by_cond";
    String GET_PERSON_BY_MOBILE = API_PREFIX + "/get-person-by-mobile";
    String GET_DEPT_PERSON_COUNT = API_PREFIX + "/get-dept-person-count";
    String GET_TENANT_PERSON = API_PREFIX + "/get-tenant-person";
    String GET_TENANT_PERSON_COUNT = API_PREFIX + "/get-tenant-person-count";
    String GET_ASSIGN_PERSON = API_PREFIX + "/get-assign-person";
    String GET_PERSON_STATUS_STAT = API_PREFIX + "/get-personStatus-stat";
    String GET_PERSON_DEVICE_STATUS_STAT = API_PREFIX + "/get-personDeviceStatus-stat";
    String LIST_PERSON_DEVICE_STATUS_STAT = API_PREFIX + "/list-personDeviceStatus-stat";
    String GET_PERSON_COUNT_BY_STATION = API_PREFIX + "/get-person-count-by-station";
    String CREATE_PERSON = API_PREFIX + "/create-person";
    String GET_ALL_DEPT_PERSON_COUNT = API_PREFIX + "/all-dept-person-count";
    String TREE_BY_DEPT = API_PREFIX + "/tree-by-dept";
    String UPDATE_PERSON = API_PREFIX + "/update-person";
    String UPDATE_PERSON_INFO = API_PREFIX + "/update-person-info";
    String GET_WECHAT_USER_BY_PERSON_ID = API_PREFIX  + "/getWechatUserByPersonId";
    String GET_PERSON_ACCOUNT = API_PREFIX  + "/getPersonAccount";

    String PERSON_INFO_ASYNC = API_PREFIX + "/person-info-async";

    String COUNT_PERSON_BY_TENANTID = API_PREFIX + "/count-person-by-tenantId";

    @PostMapping(COUNT_PERSON_BY_TENANTID)
    R<Integer> countPersonByTenantId(@RequestBody String tenantId,@RequestParam("deviceStatus") String deviceStatus);


    @PostMapping(PERSON_INFO_ASYNC)
    R<Boolean> personInfoAsync(@RequestBody List<List<String>> datasListStr,@RequestParam String tenantId,@RequestParam String actionType);

    /**
     * 获取员工信息
     *
     * @param id
     * @return
     */
    @GetMapping(PERSON)
    R<Person> getPerson(@RequestParam("id") Long id);

    @GetMapping(LIST_PERSON)
    R<List<Person>> listPerson(PersonVO person);

    /**
     * 根据指定的条件查询用户
     *
     * @param queryCond
     * @return
     */
    @GetMapping(GET_PERSON_BY_COND)
    R<List<Person>> getPersonByCond(Person queryCond);

    /**
     * 根据手机号码的条件查询唯一用户
     *
     * @param mobile
     * @return
     */
    @GetMapping(GET_PERSON_BY_MOBILE)
    R<Person> getPersonByMobile(@RequestParam("mobile") String mobile);

    @GetMapping(GET_ASSIGN_PERSON)
    R<List<Person>> getAssignPerson(@RequestParam("name") String name, @RequestParam("position") String position, @RequestParam("deptId") String deptId);


    /**
     * 获取员工信息列表
     *
     * @param deptId
     * @return
     */
    @GetMapping(PERSON_LIST)
    R<List<Person>> getPersonByDeptId(@RequestParam("deptId") Long deptId);

    /**
     * 根据部门获取员工数量
     *
     * @param deptId
     * @return
     */
    @GetMapping(GET_DEPT_PERSON_COUNT)
    R<Integer> getPersonCountByDeptId(@RequestParam("deptId") Long deptId);

    /**
     * 根据租户获取每个部门的在职员工数量
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_ALL_DEPT_PERSON_COUNT)
    R<List<DeptStaffCountDTO>> getDeptPersonCountByTenant(@RequestParam("tenantId") String tenantId);

    /**
     * 根据租户获取员工信息列表
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_TENANT_PERSON)
    R<List<Person>> getPersonByTenant(@RequestParam("tenantId") String tenantId);

    /**
     * 根据租户获取在职员工数量
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_TENANT_PERSON_COUNT)
    R<Integer> getPersonCountByTenant(@RequestParam("tenantId") String tenantId);

    /**
     * 实时根据租户获取今日出勤状态统计
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_PERSON_STATUS_STAT)
    R<PersonStatusStatDTO> getPersonStatusStat(@RequestParam("tenantId") String tenantId);

    /**
     * 实时根据租户获取今日人员设备状态统计
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_PERSON_DEVICE_STATUS_STAT)
    R<PersonDeviceStatusCountDTO> getPersonDeviceStatusStat(@RequestParam("tenantId") String tenantId);

    /**
     * 批量实时根据租户获取今日人员设备状态统计
     * @param tenantId
     * @return
     */
    @GetMapping(LIST_PERSON_DEVICE_STATUS_STAT)
    R<List<PersonDeviceStatusCountDTO>> listPersonDeviceStatusStat(@RequestParam("tenantId") String tenantId);

    /**
     * 根据岗位ID获取关联的员工数量
     *
     * @param stationId
     * @return
     */
    @GetMapping(GET_PERSON_COUNT_BY_STATION)
    R<Integer> getPersonCountByStation(@RequestParam("stationId") Long stationId);

    /**
     * 创建员工
     * @param person
     * @return
     */
    @PostMapping(CREATE_PERSON)
    R<Person> createPerson(@RequestBody Person person);

    /**
     * 创建员工
     * @param person
     * @return
     */
    @PostMapping(UPDATE_PERSON_INFO)
    R<Person> updatePerson(@RequestBody Person person);

    @PostMapping(TREE_BY_DEPT)
	R<List<PersonNode>> treeByDept(@RequestParam("nodeName") String nodeName, @RequestParam("tenantId") String tenantId, @RequestParam("entityIdStr") String entityIdStr);

    @GetMapping(UPDATE_PERSON)
    R<Integer> updatePersonWatchStateById(@RequestParam("state")Long state,@RequestParam("personId") Long personId);

    @GetMapping(GET_WECHAT_USER_BY_PERSON_ID)
    R<WeChatUser> getWechatUserByPersonId(@RequestParam("personId") Long personId);

    /**
     * 根据员工姓名查询员工帐号信息
     * @param personName
     * @return
     */
    @GetMapping(GET_PERSON_ACCOUNT)
    R<List<PersonAccountVO>> getPersonAccount(@RequestParam("personName") String personName);
}
