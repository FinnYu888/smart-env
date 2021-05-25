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
package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.entity.*;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign失败配置
 *
 * @author Chill
 */
@Component
public class ISysClientFallback implements ISysClient {

    @Override
    public R<Menu> getMenu(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getMiniCreateToken(Boolean init) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<RoleMenu>> getRolesByMenuId(Long id) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取所有菜单信息
     *
     * @return
     */
    @Override
    public R<List<Menu>> getAllMenu() {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Dept> getDept(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getDeptName(Long id) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取部门全名
     *
     * @param deptIds 主键
     * @return
     */
    @Override
    public R<List<String>> getDeptFullNames(String deptIds) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Role> getRole(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getRoleName(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getRoleAlias(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<String>> getDeptNames(String deptIds) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Dept>> getDeptChild(Long deptId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<String>> getRoleNames(String roleIds) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<String>> getRoleAliases(String roleIds) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Tenant> getTenant(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Tenant> getTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Param> getParam(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<String> getParamValue(String paramKey) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Dept>> getAllDept() {
        return R.fail("获取数据失败");
    }

    /**
     * 获取区域
     *
     * @param id 主键
     * @return Region
     */
    @Override
    public R<Region> getRegion(Long id) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Region>> getRegionByType(String regionType, String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public  R<List<Region>> getRegionForBS(String regionType,String tenantId){
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Role>> getAllRole() {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Role>> getTenantRole(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<Role> getTenantAdminRole(String tenantId) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<Dept>> getTenantDept(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取所有租户信息
     *
     * @return
     */
    @Override
    public R<List<Tenant>> getAllTenant() {
        return R.fail("获取数据失败");
    }

    /**
     * 获取所有城市信息
     *
     * @return
     */
    @Override
    public R<List<City>> getAllCity() {
        return R.fail("获取数据失败");
    }

    /**
     * 获取所有行政区域城市信息
     *
     * @return
     */
    @Override
    public R<List<AdministrativeCity>> getAllAdministrativeCity() {
        return R.fail("获取数据失败");
    }

    /**
     * 获取城市树结构
     *
     * @return
     */
    @Override
    public R<List<CityVO>> getCityTree() {
        return R.fail("获取数据失败");
    }

    /**
     * 根据城市ID获取城市信息
     *
     * @param cityId
     * @return
     */
    @Override
    public R<City> getCityById(Long cityId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据城市ID获取行政区域城市信息
     *
     * @param cityId
     * @return
     */
    @Override
    public R<AdministrativeCity> getAdminCityById(Long cityId) {
        return R.fail("获取数据失败");
    }

    @Override
	public R<List<Long>> getAllChildDepts(Long deptId) {
		return R.fail("获取数据失败");
	}

    @Override
    public R<List<City>> getCityByName(String cityName) {
        return R.fail("获取数据失败");
    }

    @Override
    public R<List<AdministrativeCity>> getAdminCityByName(String cityName) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据租户ID查询岗位信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<Station>> getStationByTenant(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据主键查询岗位信息
     *
     * @param stationId
     * @return
     */
    @Override
    public R<Station> getStationById(Long stationId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据父城市ID查询下级城市
     *
     * @param parentCityId
     * @return
     */
    @Override
    public R<List<AdministrativeCity>> getAdminCityByParent(Long parentCityId) {
        return R.fail("获取数据失败");
    }

    /**
     * 根据角色ID查询角色菜单
     *
     * @param roleId
     * @return
     */
    @Override
    public R<List<RoleMenu>> getRoleMenuByRoleId(Long roleId) {
        return R.fail("获取数据失败");
    }
}
