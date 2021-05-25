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
import com.ai.apac.smartenv.system.service.*;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import com.ai.apac.smartenv.system.wrapper.AdministrativeCityWrapper;
import com.ai.apac.smartenv.system.wrapper.CityWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 系统服务Feign实现类
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class SysClient implements ISysClient {

    private IDeptService deptService;

    private ITokenService tokenService;

    private IRoleService roleService;

    private IMenuService menuService;

    private IRoleMenuService roleMenuService;

    private ITenantService tenantService;

    private IParamService paramService;

    private IRegionService regionService;

    private ICityService cityService;

    private IStationService stationService;

    private IAdministrativeCityService administrativeCityService;

    @Override
    @GetMapping(MENU)
    public R<Menu> getMenu(Long id) {
        return R.data(menuService.getById(id));
    }

    @Override
    public R<String> getMiniCreateToken(Boolean init) {
        return R.data(tokenService.getMiniCreateToken(init));
    }

    @Override
    @GetMapping(GET_ROLES_BY_MENUID)
    public R<List<RoleMenu>> getRolesByMenuId(Long id) {
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<RoleMenu>();
        wrapper.lambda().eq(RoleMenu::getMenuId, id);
        return R.data(roleMenuService.list(wrapper));
    }


    /**
     * 获取所有菜单信息
     *
     * @return
     */
    @Override
    public R<List<Menu>> getAllMenu() {
        LambdaQueryWrapper queryWrapper = Wrappers.<Menu>query().lambda().orderByAsc(Menu::getSort);
        return R.data(menuService.list(queryWrapper));
    }

    @Override
    @GetMapping(DEPT)
    public R<Dept> getDept(Long id) {
        return R.data(deptService.getById(id));
    }


    @Override
    @GetMapping(DEPT_NAME)
    public R<String> getDeptName(Long id) {
        Dept dept = deptService.getById(id);
        if (dept != null) {
            return R.data(dept.getDeptName());
        } else {
            return R.data(null);
        }
    }

    /**
     * 获取部门全名
     *
     * @param deptIds 主键
     * @return
     */
    @Override
    public R<List<String>> getDeptFullNames(String deptIds) {
        return R.data(deptService.getDeptFullNames(deptIds));
    }

    @Override
    @GetMapping(ROLE)
    public R<Role> getRole(Long id) {
        return R.data(roleService.getById(id));
    }

    @Override
    @GetMapping(ROLE_NAME)
    public R<String> getRoleName(Long id) {
        return R.data(roleService.getById(id).getRoleName());
    }

    @Override
    @GetMapping(ROLE_ALIAS)
    public R<String> getRoleAlias(Long id) {
        return R.data(roleService.getById(id).getRoleAlias());
    }

    @Override
    @GetMapping(DEPT_NAMES)
    public R<List<String>> getDeptNames(String deptIds) {
        return R.data(deptService.getDeptNames(deptIds));
    }

    @Override
    @GetMapping(DEPT_CHILD)
    public R<List<Dept>> getDeptChild(Long deptId) {
        return R.data(deptService.getDeptChild(deptId));
    }

    @Override
    @GetMapping(DEPT_ALL_CHILD)
    public R<List<Long>> getAllChildDepts(Long deptId) {
        return R.data(deptService.getAllChildDepts(deptId, new ArrayList<>()));
    }

    @Override
    @GetMapping(ROLE_NAMES)
    public R<List<String>> getRoleNames(String roleIds) {
        return R.data(roleService.getRoleNames(roleIds));
    }

    @Override
    @GetMapping(ROLE_ALIASES)
    public R<List<String>> getRoleAliases(String roleIds) {
        return R.data(roleService.getRoleAliases(roleIds));
    }

    @Override
    @GetMapping(TENANT)
    public R<Tenant> getTenant(Long id) {
        return R.data(tenantService.getById(id));
    }

    @Override
    @GetMapping(TENANT_ID)
    public R<Tenant> getTenant(String tenantId) {
        return R.data(tenantService.getByTenantId(tenantId));
    }

    @Override
    @GetMapping(PARAM)
    public R<Param> getParam(Long id) {
        return R.data(paramService.getById(id));
    }

    @Override
    @GetMapping(PARAM_VALUE)
    public R<String> getParamValue(String paramKey) {
        return R.data(paramService.getValue(paramKey));
    }

    @Override
    @GetMapping(GET_ALL_DEPT)
    public R<List<Dept>> getAllDept() {
        //这种写法是为了带上租户
        Dept dept = new Dept();
        List<Dept> list = deptService.selectDept(dept);
        return R.data(list);
    }

    @Override
    @GetMapping(GET_TENANT_DEPT)
    public R<List<Dept>> getTenantDept(String tenantId) {
        Dept dept = new Dept();
        dept.setTenantId(tenantId);
        List<Dept> list = deptService.selectDept(dept);
        return R.data(list);
    }

    @Override
    @GetMapping(GET_ALL_ROLE)
    public R<List<Role>> getAllRole() {
        List<Role> list = roleService.selectRole(new Role());
        return R.data(list);
    }

    @Override
    @GetMapping(GET_TENANT_ROLE)
    public R<List<Role>> getTenantRole(String tenantId) {
        List<Role> list = roleService.list(Condition.getQueryWrapper(new HashMap<String, Object>(), Role.class).lambda()
                .eq(Role::getTenantId, tenantId));
        return R.data(list);
    }

    @Override
    @GetMapping(GET_TENANT_ADMIN_ROLE)
    public R<Role> getTenantAdminRole(String tenantId) {
        String roleAlias = "admin";
        if ("000000".equals(tenantId)) {
            roleAlias = "administrator";
        }
        Role role = new Role();
        List<Role> list = roleService.list(Condition.getQueryWrapper(new HashMap<String, Object>(), Role.class).lambda()
                .eq(Role::getTenantId, tenantId).eq(Role::getRoleAlias, roleAlias));
        if (list.size() > 0) {
            role = list.get(0);
        }
        return R.data(role);
    }

    /**
     * 获取区域
     *
     * @param id 主键
     * @return Region
     */
    @Override
    public R<Region> getRegion(Long id) {
        return R.data(regionService.getById(id));
    }

    @Override
    public R<List<Region>> getRegionByType(String regionType, String tenantId) {
        LambdaQueryWrapper queryWrapper = Wrappers.<Region>query().lambda().
                eq(Region::getRegionType,regionType).
                eq(Region::getTenantId,tenantId);
        return R.data(regionService.list(queryWrapper));
    }

    @Override
    @GetMapping(REGION_BY_TYPE_FOR_BS)
    public R<List<Region>> getRegionForBS(String regionType, String tenantId) {
        return R.data(regionService.queryBusiRegionListForBS(regionType,tenantId));
    }

    /**
     * 获取所有租户信息
     *
     * @return
     */
    @Override
    public R<List<Tenant>> getAllTenant() {
        return R.data(tenantService.getAllTenant());
    }

    /**
     * 获取所有城市信息
     *
     * @return
     */
    @Override
    public R<List<City>> getAllCity() {
        return R.data(cityService.list());
    }

    /**
     * 获取所有行政区域城市信息
     *
     * @return
     */
    @Override
    public R<List<AdministrativeCity>> getAllAdministrativeCity() {
        return R.data(administrativeCityService.getAllCity());
    }

    /**
     * 获取城市树结构
     *
     * @return
     */
    @Override
    @GetMapping(GET_CITY_TREE)
    public R<List<CityVO>> getCityTree() {
        return R.data(CityWrapper.build().listTree(cityService.list()));
    }

    /**
     * 根据城市ID获取城市信息
     *
     * @param cityId
     * @return
     */
    @Override
    @GetMapping(GET_CITY_BY_ID)
    public R<City> getCityById(Long cityId) {
        return R.data(cityService.getById(cityId));
    }

    /**
     * 根据城市ID获取行政区域城市信息
     *
     * @param cityId
     * @return
     */
    @Override
    @GetMapping(GET_ADMIN_CITY_BY_ID)
    public R<AdministrativeCity> getAdminCityById(Long cityId) {
        return R.data(administrativeCityService.getById(cityId));
    }

    /**
     * 根据城市名称模糊查询城市信息
     *
     * @param cityName
     * @return
     */
    @Override
    @GetMapping(GET_CITY_BY_NAME)
    public R<List<City>> getCityByName(@RequestParam("cityName") String cityName) {
        List<City> cityList = cityService.list(new LambdaQueryWrapper<City>().like(City::getCityName, cityName));
        return R.data(cityList);
    }

    @Override
    @GetMapping(GET_ADMIN_CITY_BY_NAME)
    public R<List<AdministrativeCity>> getAdminCityByName(@RequestParam("cityName") String cityName) {
        List<AdministrativeCity> cityList = administrativeCityService.list(new LambdaQueryWrapper<AdministrativeCity>().like(AdministrativeCity::getCityName,cityName));
        return R.data(cityList);
    }

    /**
     * 根据租户ID查询岗位信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<List<Station>> getStationByTenant(@RequestParam("tenantId") String tenantId) {
        List<Station> stationList = stationService.list(new LambdaQueryWrapper<Station>().eq(Station::getTenantId, tenantId));
        return R.data(stationList);
    }

    /**
     * 根据主键查询岗位信息
     *
     * @param stationId
     * @return
     */
    @Override
    public R<Station> getStationById(@RequestParam("stationId") Long stationId) {
        return R.data(stationService.getById(stationId));
    }

    /**
     * 根据父城市ID查询下级城市
     *
     * @param parentCityId
     * @return
     */
    @Override
    @GetMapping(GET_ADMIN_CITY_BY_PARENT_CITY)
    public R<List<AdministrativeCity>> getAdminCityByParent(@RequestParam("parentCityId") Long parentCityId) {
        List<AdministrativeCity> list = administrativeCityService.list(new LambdaQueryWrapper<AdministrativeCity>().eq(AdministrativeCity::getParentId,parentCityId));
        return R.data(list);
    }

    /**
     * 根据角色ID查询角色菜单
     *
     * @param roleId
     * @return
     */
    @Override
    @GetMapping(GET_ROLE_MENU_BY_ROLE)
    public R<List<RoleMenu>> getRoleMenuByRoleId(@RequestParam("roleId") Long roleId) {
        List<RoleMenu> list = roleMenuService.list(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId,roleId));
        return R.data(list);
    }
}
