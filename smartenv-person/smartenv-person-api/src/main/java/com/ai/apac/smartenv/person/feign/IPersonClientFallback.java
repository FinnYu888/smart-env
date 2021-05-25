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

import com.ai.apac.smartenv.person.dto.DeptStaffCountDTO;
import com.ai.apac.smartenv.person.dto.PersonDeviceStatusCountDTO;
import com.ai.apac.smartenv.person.dto.PersonStatusStatDTO;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.vo.PersonAccountVO;
import com.ai.apac.smartenv.person.vo.PersonNode;
import com.ai.apac.smartenv.person.vo.PersonVO;

import com.ai.apac.smartenv.wechat.entity.WeChatUser;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class IPersonClientFallback implements IPersonClient {

    @Override
    public R<Person> getPerson(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Integer> countPersonByTenantId(String tenantId, String deviceStatus) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Boolean> personInfoAsync(@RequestBody List<List<String>> datasListStr, @RequestParam String tenantId, @RequestParam String actionTyoe) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Person>> getAssignPerson(String name, String position, String deptId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Person>> getPersonByDeptId(Long deptId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据指定的条件查询用户
     *
     * @param queryCond
     * @return
     */
    @Override
    public R<List<Person>> getPersonByCond(Person queryCond) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据租户获取员工信息列表
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<Person>> getPersonByTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据部门获取员工数量
     *
     * @param deptId
     * @return
     */
    @Override
    public R<Integer> getPersonCountByDeptId(Long deptId) {
        return R.fail("获取数据失败");
    }

    /**
     * 实时根据租户获取今日出勤状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<PersonStatusStatDTO> getPersonStatusStat(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Person>> listPerson(PersonVO person) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据岗位ID获取关联的员工数量
     *
     * @param stationId
     * @return
     */
    @Override
    public R<Integer> getPersonCountByStation(Long stationId) {
        return R.fail("获取数据失败");
    }

    /**
     * 创建员工
     *
     * @param person
     * @return
     */
    @Override
    public R<Person> createPerson(Person person) {
        return R.fail("获取数据失败");
    }

    /**
     * 创建员工
     *
     * @param person
     * @return
     */
    @Override
    public R<Person> updatePerson(Person person) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据租户获取每个部门的在职员工数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<DeptStaffCountDTO>> getDeptPersonCountByTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

	@Override
	public R<List<PersonNode>> treeByDept(String nodeName, String tenantId, String entityIdStr) {
		return R.fail("获取数据失败");
	}
    @Override
    public R<Integer> updatePersonWatchStateById(Long state, Long personId){
        return R.fail("获取数据失败");
    }

    /**
     * 根据手机号码的条件查询唯一用户
     *
     * @param mobile
     * @return
     */
    @Override
    public R<Person> getPersonByMobile(String mobile) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据personId查询微信用户信息
     *
     * @param personId
     * @return
     */
    @Override
    public R<WeChatUser> getWechatUserByPersonId(Long personId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据租户获取在职员工数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<Integer> getPersonCountByTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 实时根据租户获取今日人员设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<PersonDeviceStatusCountDTO> getPersonDeviceStatusStat(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据员工姓名查询员工帐号信息
     *
     * @param personName
     * @return
     */
    @Override
    public R<List<PersonAccountVO>> getPersonAccount(String personName) {
        return R.fail("获取数据失败");
    }

    /**
     * 批量实时根据租户获取今日人员设备状态统计
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<PersonDeviceStatusCountDTO>> listPersonDeviceStatusStat(String tenantId) {
        return R.fail("获取数据失败");
    }
}
