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

import com.ai.apac.smartenv.system.vo.UserProjectRoleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.vo.RoleVO;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IRoleService extends IService<Role> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param role
	 * @return
	 */
	IPage<RoleVO> selectRolePage(IPage<RoleVO> page, RoleVO role);

	/**
	 * 树形结构
	 *
	 * @param tenantId
	 * @return
	 */
	List<RoleVO> tree(String tenantId);

	/**
	 * 权限配置
	 *
	 * @param roleIds 角色id集合
	 * @param menuIds 菜单id集合
	 * @param dataScopeIds 数据权限id集合
	 * @param apiScopeIds 接口权限id集合
	 * @return 是否成功
	 */
	boolean grant(@NotEmpty List<Long> roleIds, List<Long> menuIds, List<Long> dataScopeIds, List<Long> apiScopeIds);

	/**
	 * 获取角色名
	 *
	 * @param roleIds
	 * @return
	 */
	List<String> getRoleNames(String roleIds);

	/**
	 * 获取角色名
	 *
	 * @param roleIds
	 * @return
	 */
	List<String> getRoleAliases(String roleIds);

	/**
	 * 提交
	 *
	 * @param role
	 * @return
	 */
	boolean submit(Role role);

	/**
	 * 逻辑删除
	 * @param roleIds
	 * @return
	 */
	boolean deleteLogic(String roleIds);

	/**
	 * 根据条件查询角色
	 * @param role
	 * @return
	 */
	List<Role> selectRole(Role role);

	/**
	 * 根据主键查询角色
	 * @param roleId
	 * @return
	 */
	Role getRoleById(Long roleId);

	/**
	 * 根据项目编码查询
	 * @param projectCode
	 * @return
	 */
	List<Role> listRoleByProjectCode(String projectCode);

	/**
	 * 根据用户ID查询关联的所有项目及关联的角色
	 * @param userId
	 * @return
	 */
	UserProjectRoleVO listUserProjectRole(Long userId);
}
