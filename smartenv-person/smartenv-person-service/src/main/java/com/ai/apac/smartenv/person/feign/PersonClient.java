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

import cn.hutool.core.util.ArrayUtil;
import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.dto.PersonStatusStatDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.service.IPersonAsyncService;
import com.ai.apac.smartenv.person.service.IPersonService;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.person.vo.PersonVO;
import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统服务Feign实现类
 *
 * @author Chill
 */
//@ApiIgnore
@RestController
@AllArgsConstructor
public class PersonClient implements IPersonClient {

    private ISysClient sysClient;
    private IPersonService personService;
    private IPersonAsyncService personAsyncService;



    @Override
    @GetMapping(PERSON)
    public R<Person> getPerson(Long id) {
        return R.data(personService.getById(id));
    }

    @Override
    public R<Integer> countPersonByTenantId(String tenantId, String deviceStatus) {
        QueryWrapper<Person> wrapper = new QueryWrapper<Person>();
        wrapper.lambda().eq(Person::getTenantId,tenantId).eq(Person::getIsIncumbency,PersonConstant.IncumbencyStatus.IN);
        if(ObjectUtil.isNotEmpty(deviceStatus)){
            wrapper.lambda().eq(Person::getWatchDeviceStatus,Long.parseLong(deviceStatus));
        }
        return R.data(personService.count(wrapper));
    }

    @Override
    public R<Boolean> personInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(personAsyncService.thirdPersonInfoAsync(datasList,tenantId,actionType,true));
    }

    @Override
    public R<List<Person>> getAssignPerson(String name, String position, String deptId) {
        QueryWrapper<Person> queryWrapper = new QueryWrapper<Person>();
        if (ObjectUtil.isNotEmpty(position)) {
            queryWrapper.lambda().eq(Person::getPersonPositionId, position);
        }
        if (ObjectUtil.isNotEmpty(deptId)) {
            queryWrapper.lambda().eq(Person::getPersonDeptId, deptId);
        }
        if (ObjectUtil.isNotEmpty(name)) {
            queryWrapper.lambda().like(Person::getPersonName, name);
        }
        queryWrapper.lambda().eq(Person::getIsIncumbency,PersonConstant.IncumbencyStatus.IN);
        return R.data(personService.list(queryWrapper));
    }

    @Override
    public R<List<Person>> getPersonByDeptId(Long deptId) {
        return R.data(personService.getPersonByDeptId(deptId));
    }

    /**
     * 根据租户获取员工信息列表
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_TENANT_PERSON)
    public R<List<Person>> getPersonByTenant(String tenantId) {
        return R.data(personService.list(new LambdaQueryWrapper<Person>().eq(Person::getTenantId, tenantId)));
    }

    /**
     * 根据手机号码的条件查询唯一用户
     *
     * @param mobile
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_BY_MOBILE)
    public R<Person> getPersonByMobile(String mobile) {
        return R.data(personService.getOne(new LambdaQueryWrapper<Person>().eq(Person::getMobileNumber, mobile)));
    }

    /**
     * 根据部门获取员工数量
     *
     * @param deptId
     * @return
     */
    @Override
    @GetMapping(GET_DEPT_PERSON_COUNT)
    public R<Integer> getPersonCountByDeptId(Long deptId) {
        Integer count = personService.count(new QueryWrapper<Person>().eq("person_dept_id", deptId));
        return R.data(count);
    }

    /**
     * 实时根据租户获取今日出勤状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_STATUS_STAT)
    public R<PersonStatusStatDTO> getPersonStatusStat(@RequestParam String tenantId) {
        return R.data(personService.getPersonStatusStatToday(tenantId));
    }

    /**
     * 实时根据租户获取今日人员设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_DEVICE_STATUS_STAT)
    public R<PersonDeviceStatusCountDTO> getPersonDeviceStatusStat(@RequestParam String tenantId) {
        return R.data(personService.getPersonDeviceStatusCount(tenantId));
    }

    /**
     * 批量实时根据租户获取今日人员设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(LIST_PERSON_DEVICE_STATUS_STAT)
    public R<List<PersonDeviceStatusCountDTO>> listPersonDeviceStatusStat(@RequestParam String tenantId) {
        return R.data(personService.listPersonDeviceStatusCount(tenantId));
    }

    @Override
    @GetMapping(LIST_PERSON)
    public R<List<Person>> listPerson(PersonVO person) {
        Person copy = BeanUtil.copy(person, Person.class);
        QueryWrapper<Person> queryWrapper = Condition.getQueryWrapper(copy);
        if (CollectionUtil.isNotEmpty(person.getPersonIdList())) {
            queryWrapper.in("id", person.getPersonIdList());
        }
        List<Person> personList = personService.list(queryWrapper);
        return R.data(personList);
    }

    /**
     * 根据指定的条件查询用户
     *
     * @param queryCond
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_BY_COND)
    public R<List<Person>> getPersonByCond(Person queryCond) {
        return R.data(personService.listPersonByCond(queryCond));
    }

    /**
     * 根据岗位ID获取关联的员工数量
     *
     * @param stationId
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_COUNT_BY_STATION)
    public R<Integer> getPersonCountByStation(@RequestParam("stationId") Long stationId) {
        return R.data(personService.count(new LambdaQueryWrapper<Person>().eq(Person::getPersonPositionId, stationId)));
    }

    /**
     * 创建员工
     *
     * @param person
     * @return
     */
    @Override
    @PostMapping(CREATE_PERSON)
    public R<Person> createPerson(@RequestBody Person person) {
        personService.save(person);
        PersonCache.saveOrUpdatePerson(person);
        return R.data(person);
    }

    /**
     * 创建员工
     *
     * @param person
     * @return
     */
    @Override
    @PostMapping(UPDATE_PERSON_INFO)
    public R<Person> updatePerson(Person person) {
        personService.updateById(person);
        PersonCache.saveOrUpdatePerson(person);
        return R.data(person);
    }

    /**
     * 根据租户获取每个部门的在职员工数量
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_ALL_DEPT_PERSON_COUNT)
    public R<List<DeptStaffCountDTO>> getDeptPersonCountByTenant(@RequestParam("tenantId") String tenantId) {
        List<DeptStaffCountDTO> list = personService.getDeptStaffCount(tenantId);
        return R.data(list);
    }

    @Override
    @PostMapping(TREE_BY_DEPT)
    public R<List<PersonNode>> treeByDept(String nodeName, String tenantId, String entityIdStr) {
        return R.data(personService.treeByDept(nodeName, tenantId, null, Func.toLongList(entityIdStr)));
    }

    @Override
    @GetMapping(UPDATE_PERSON)
    public R<Integer> updatePersonWatchStateById(@RequestParam("state") Long state, @RequestParam("personId") Long personId) {
        return R.data(personService.updatePersonWatchStateById(state, personId));
    }

    @Override
    @GetMapping(GET_WECHAT_USER_BY_PERSON_ID)
    public R<WeChatUser> getWechatUserByPersonId(@RequestParam("personId") Long personId) {
        return R.data(personService.getWechatUserByPersonId(personId));
    }

    /**
     * 根据租户获取在职员工数量
     *
     * @param tenantId
     * @return
     */
    @GetMapping(GET_TENANT_PERSON_COUNT)
    @Override
    public R<Integer> getPersonCountByTenant(String tenantId) {
        return R.data(personService.count(new LambdaQueryWrapper<Person>().eq(Person::getTenantId, tenantId)
                .eq(Person::getIsIncumbency, PersonConstant.IncumbencyStatus.IN)
                .eq(Person::getIsDeleted, 0)));
    }

    /**
     * 根据员工姓名查询员工帐号信息
     *
     * @param personName
     * @return
     */
    @GetMapping(GET_PERSON_ACCOUNT)
    @Override
    public R<List<PersonAccountVO>> getPersonAccount(@RequestParam("personName") String personName) {
        return R.data(personService.listPersonAccount(personName));
    }
}
