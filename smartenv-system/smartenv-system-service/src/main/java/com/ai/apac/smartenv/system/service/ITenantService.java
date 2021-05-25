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
package com.ai.apac.smartenv.system.service;

import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.vo.TenantVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import com.ai.apac.smartenv.system.entity.Tenant;

import java.util.List;

/**
 * 服务类
 *
 * @author Chill
 */
public interface ITenantService extends BaseService<Tenant> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param tenant
	 * @return
	 */
	IPage<Tenant> selectTenantPage(IPage<Tenant> page, Tenant tenant);

	/**
	 * 根据租户编号获取实体
	 *
	 * @param tenantId
	 * @return
	 */
	Tenant getByTenantId(String tenantId);

	/**
	 * 新增
	 *
	 * @param tenant
	 * @return
	 */
	boolean saveTenant(Tenant tenant);

	/**
	 * 校验租户名称是否唯一,唯一返回true,否则返回false
	 * @param tenantName
	 * @return
	 */
	boolean validTenantName(String tenantName);

	/**
	 * 修改租户状态
	 * @param tenantId
	 * @param newStatus
	 * @return
	 */
	boolean changeTenantStatus(Long tenantId,Integer newStatus);

	/**
	 * 获取所有租户数据
	 * @return
	 */
	List<Tenant> getAllTenant();

	/**
	 * 根据租户主键获取管理角色信息
	 * @param tenantId
	 * @return
	 */
	Role getTenantAdminRole(String tenantId);

	String getTenantId(List<String> codes);
}
