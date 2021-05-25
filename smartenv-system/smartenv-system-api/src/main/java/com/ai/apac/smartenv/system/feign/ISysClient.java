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

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.*;
import com.ai.apac.smartenv.system.vo.CityInfoVO;
import com.ai.apac.smartenv.system.vo.CityVO;
import org.apache.commons.collections4.Get;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
	fallback = ISysClientFallback.class
)
public interface ISysClient {

	String API_PREFIX = "/client";
	String MENU = API_PREFIX + "/menu";
	String DEPT = API_PREFIX + "/dept";
	String DEPT_NAME = API_PREFIX + "/dept-name";
	String DEPT_NAMES = API_PREFIX + "/dept-names";
	String DEPT_FULL_NAMES = API_PREFIX + "/dept-full-names";
	String DEPT_CHILD = API_PREFIX + "/dept-child";
	String DEPT_ALL_CHILD = API_PREFIX + "/dept-all-child";
	String ROLE = API_PREFIX + "/role";
	String ROLE_NAME = API_PREFIX + "/role-name";
	String ROLE_NAMES = API_PREFIX + "/role-names";
	String ROLE_ALIAS = API_PREFIX + "/role-alias";
	String ROLE_ALIASES = API_PREFIX + "/role-aliases";
	String GET_ALL_TENANT = API_PREFIX + "/all-tenant";
	String TENANT = API_PREFIX + "/tenant";
	String TENANT_ID = API_PREFIX + "/tenant-id";
	String PARAM = API_PREFIX + "/param";
	String PARAM_VALUE = API_PREFIX + "/param-value";
	String GET_ALL_DEPT = API_PREFIX + "/get-all-dept";
	String GET_TENANT_DEPT = API_PREFIX + "/get-dept-by-tenant";
	String GET_ALL_ROLE = API_PREFIX + "/get-all-role";
	String GET_TENANT_ROLE = API_PREFIX + "/get-role-tenant";
	String GET_TENANT_ADMIN_ROLE = API_PREFIX + "/get-admin-role-tenant";
	String REGION = API_PREFIX + "/region";
	String REGION_BY_TYPE = API_PREFIX + "/region-by-type";
	String REGION_BY_TYPE_FOR_BS = API_PREFIX + "/region-by-type-for_bs";
	String GET_ALL_MENU = API_PREFIX + "/all-menu";
	String GET_CITY_TREE =  API_PREFIX + "/city-tree";
	String GET_ALL_CITY =  API_PREFIX + "/all-city";
	String GET_ALL_ADMIN_CITY =  API_PREFIX + "/all-admin-city";
	String GET_CITY_BY_ID =  API_PREFIX + "/get-city-by-id";
	String GET_ADMIN_CITY_BY_ID =  API_PREFIX + "/get-admin-city-by-id";
	String GET_CITY_BY_NAME =  API_PREFIX + "/get-city-by-name";
	String GET_ADMIN_CITY_BY_NAME =  API_PREFIX + "/get-admin-city-by-name";
	String GET_ROLES_BY_MENUID =  API_PREFIX + "/get-roles-by-menuId";
	String GET_STATION_BY_TENANT =  API_PREFIX + "/get-station-by-tenant";
	String GET_STATION_BY_ID =  API_PREFIX + "/get-station-by-id";

	String GET_MINICREATE_TOKEN =  API_PREFIX + "/get-miniCreate-token";

	String GET_ADMIN_CITY_BY_PARENT_CITY = API_PREFIX + "/getAdminCityByParentCity";

	String GET_ROLE_MENU_BY_ROLE = API_PREFIX + "/getRoleMenuByRoleId";

	/**
	 * 获取菜单
	 *
	 * @param id 主键
	 * @return Menu
	 */
	@GetMapping(MENU)
	R<Menu> getMenu(Long id);

	@GetMapping(GET_MINICREATE_TOKEN)
	R<String> getMiniCreateToken(@RequestParam("init") Boolean init);

	@GetMapping(GET_ROLES_BY_MENUID)
	R<List<RoleMenu>> getRolesByMenuId(@RequestParam("id") Long id);

	/**
	 * 获取所有菜单信息
	 * @return
	 */
	@GetMapping(GET_ALL_MENU)
	R<List<Menu>> getAllMenu();

	/**
	 * 获取部门
	 *
	 * @param id 主键
	 * @return Dept
	 */
	@GetMapping(DEPT)
	R<Dept> getDept(@RequestParam("id") Long id);

	/**
	 * 获取部门名
	 *
	 * @param id 主键
	 * @return 部门名
	 */
	@GetMapping(DEPT_NAME)
	R<String> getDeptName(@RequestParam("id") Long id);

	/**
	 * 获取角色
	 *
	 * @param id 主键
	 * @return Role
	 */
	@GetMapping(ROLE)
	R<Role> getRole(@RequestParam("id") Long id);

	/**
	 * 获取角色名
	 *
	 * @param id 主键
	 * @return 角色名
	 */
	@GetMapping(ROLE_NAME)
	R<String> getRoleName(@RequestParam("id") Long id);

	/**
	 * 获取角色别名
	 *
	 * @param id 主键
	 * @return 角色别名
	 */
	@GetMapping(ROLE_ALIAS)
	R<String> getRoleAlias(@RequestParam("id") Long id);

	/**
	 * 获取部门名
	 *
	 * @param deptIds 主键
	 * @return
	 */
	@GetMapping(DEPT_NAMES)
	R<List<String>> getDeptNames(@RequestParam("deptIds") String deptIds);

	/**
	 * 获取部门全名
	 *
	 * @param deptIds 主键
	 * @return
	 */
	@GetMapping(DEPT_FULL_NAMES)
	R<List<String>> getDeptFullNames(@RequestParam("deptIds") String deptIds);

	/**
	 * 获取子部门ID
	 *
	 * @param deptId
	 * @return
	 */
	@GetMapping(DEPT_CHILD)
	R<List<Dept>> getDeptChild(@RequestParam("deptId") Long deptId);

	/**
	 * 获取所有子部门ID
	 *
	 * @param deptId
	 * @return
	 */
	@GetMapping(DEPT_ALL_CHILD)
	R<List<Long>> getAllChildDepts(@RequestParam("deptId") Long deptId);

	/**
	 * 获取角色名
	 *
	 * @param roleIds 主键
	 * @return
	 */
	@GetMapping(ROLE_NAMES)
	R<List<String>> getRoleNames(@RequestParam("roleIds") String roleIds);

	/**
	 * 获取角色别名
	 *
	 * @param roleIds 主键
	 * @return 角色别名
	 */
	@GetMapping(ROLE_ALIASES)
	R<List<String>> getRoleAliases(@RequestParam("roleIds") String roleIds);

	/**
	 * 获取租户
	 *
	 * @param id 主键
	 * @return Tenant
	 */
	@GetMapping(TENANT)
	R<Tenant> getTenant(@RequestParam("id") Long id);

	/**
	 * 获取租户
	 *
	 * @param tenantId 租户id
	 * @return Tenant
	 */
	@GetMapping(TENANT_ID)
	R<Tenant> getTenant(@RequestParam("tenantId") String tenantId);

	/**
	 * 获取参数
	 *
	 * @param id 主键
	 * @return Param
	 */
	@GetMapping(PARAM)
	R<Param> getParam(@RequestParam("id") Long id);

	/**
	 * 获取参数配置
	 *
	 * @param paramKey 参数key
	 * @return String
	 */
	@GetMapping(PARAM_VALUE)
	R<String> getParamValue(@RequestParam("paramKey") String paramKey);

	@GetMapping(GET_ALL_DEPT)
	R<List<Dept>> getAllDept();

	@GetMapping(GET_TENANT_DEPT)
	R<List<Dept>> getTenantDept(@RequestParam String tenantId);

	@GetMapping(GET_ALL_ROLE)
	R<List<Role>> getAllRole();

	@GetMapping(GET_TENANT_ROLE)
	R<List<Role>> getTenantRole(@RequestParam String tenantId);


	@GetMapping(GET_TENANT_ADMIN_ROLE)
	R<Role> getTenantAdminRole(@RequestParam("tenantId") String tenantId);
	/**
	 * 获取区域
	 *
	 * @param id 主键
	 * @return Region
	 */
	@GetMapping(REGION)
	R<Region> getRegion(@RequestParam("id") Long id);

	@GetMapping(REGION_BY_TYPE)
	R<List<Region>> getRegionByType(@RequestParam("type") String regionType,@RequestParam("tenantId") String tenantId);

	@GetMapping(REGION_BY_TYPE_FOR_BS)
	R<List<Region>> getRegionForBS(@RequestParam("regionType") String regionType,@RequestParam("tenantId") String tenantId);

	/**
	 * 获取所有租户信息
	 * @return
	 */
	@GetMapping(GET_ALL_TENANT)
	R<List<Tenant>> getAllTenant();

	/**
	 * 获取所有城市信息
	 * @return
	 */
	@GetMapping(GET_ALL_CITY)
	R<List<City>> getAllCity();

	/**
	 * 获取所有行政区域城市信息
	 * @return
	 */
	@GetMapping(GET_ALL_ADMIN_CITY)
	R<List<AdministrativeCity>> getAllAdministrativeCity();

	/**
	 * 根据城市ID获取城市信息
	 * @param cityId
	 * @return
	 */
	@GetMapping(GET_CITY_BY_ID)
	R<City> getCityById(Long cityId);

	/**
	 * 根据城市ID获取行政区域城市信息
	 * @param cityId
	 * @return
	 */
	@GetMapping(GET_ADMIN_CITY_BY_ID)
	R<AdministrativeCity> getAdminCityById(@RequestParam("cityId") Long cityId);

	/**
	 * 获取城市树结构
	 * @return
	 */
	@GetMapping(GET_CITY_TREE)
	R<List<CityVO>> getCityTree();

	/**
	 * 根据城市名称模糊查询城市信息
	 * @param cityName
	 * @return
	 */
	@GetMapping(GET_CITY_BY_NAME)
	R<List<City>> getCityByName(@RequestParam("cityName") String cityName);

	@GetMapping(GET_ADMIN_CITY_BY_NAME)
	R<List<AdministrativeCity>> getAdminCityByName(@RequestParam("cityName") String cityName);

	/**
	 * 根据租户ID查询岗位信息
	 * @param tenantId
	 * @return
	 */
	@GetMapping(GET_STATION_BY_TENANT)
	R<List<Station>> getStationByTenant(@RequestParam("tenantId") String tenantId);

	/**
	 * 根据主键查询岗位信息
	 * @param stationId
	 * @return
	 */
	@GetMapping(GET_STATION_BY_ID)
	R<Station> getStationById(@RequestParam("stationId") Long stationId);

	/**
	 * 根据父城市ID查询下级城市
	 * @param parentCityId
	 * @return
	 */
	@GetMapping(GET_ADMIN_CITY_BY_PARENT_CITY)
	R<List<AdministrativeCity>> getAdminCityByParent(@RequestParam("parentCityId") Long parentCityId);

	/**
	 * 根据角色ID查询角色菜单
	 * @param roleId
	 * @return
	 */
	@GetMapping(GET_ROLE_MENU_BY_ROLE)
	R<List<RoleMenu>> getRoleMenuByRoleId(@RequestParam("roleId") Long roleId);
}
