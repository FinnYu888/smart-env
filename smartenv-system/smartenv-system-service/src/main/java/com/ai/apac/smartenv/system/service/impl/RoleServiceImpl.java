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
package com.ai.apac.smartenv.system.service.impl;

import com.ai.apac.smartenv.system.cache.ProjectCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.entity.Project;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.entity.RoleMenu;
import com.ai.apac.smartenv.system.entity.RoleScope;
import com.ai.apac.smartenv.system.mapper.ProjectMapper;
import com.ai.apac.smartenv.system.mapper.RoleMapper;
import com.ai.apac.smartenv.system.mapper.RoleMenuMapper;
import com.ai.apac.smartenv.system.service.IRoleMenuService;
import com.ai.apac.smartenv.system.service.IRoleScopeService;
import com.ai.apac.smartenv.system.service.IRoleService;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.ProjectRoleVO;
import com.ai.apac.smartenv.system.vo.RoleVO;
import com.ai.apac.smartenv.system.vo.UserProjectRoleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.node.ForestNodeMerger;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.constant.CommonConstant.API_SCOPE_CATEGORY;
import static com.ai.apac.smartenv.common.constant.CommonConstant.DATA_SCOPE_CATEGORY;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@Validated
@AllArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private IRoleMenuService roleMenuService;
    private IRoleScopeService roleScopeService;
    private RoleMenuMapper roleMenuMapper;
    private ProjectMapper projectMapper;

    @Autowired
    private IUserClient userClient;

    @Override
    public IPage<RoleVO> selectRolePage(IPage<RoleVO> page, RoleVO role) {
        return page.setRecords(baseMapper.selectRolePage(page, role));
    }

    @Override
    public List<RoleVO> tree(String tenantId) {
        String userRole = SecureUtil.getUserRole();
        String excludeRole = null;
        if (!CollectionUtil.contains(Func.toStrArray(userRole), RoleConstant.ADMIN) && !CollectionUtil.contains(Func.toStrArray(userRole), RoleConstant.ADMINISTRATOR)) {
            excludeRole = RoleConstant.ADMINISTRATOR;
        }
        return ForestNodeMerger.merge(baseMapper.tree(tenantId, excludeRole));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean grant(@NotEmpty List<Long> roleIds, List<Long> menuIds, List<Long> dataScopeIds, List<Long> apiScopeIds) {
        // 删除角色配置的菜单集合
        roleMenuService.remove(Wrappers.<RoleMenu>update().lambda().in(RoleMenu::getRoleId, roleIds));
        // 组装配置
        List<RoleMenu> roleMenus = new ArrayList<>();
        roleIds.forEach(roleId -> menuIds.forEach(menuId -> {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        }));
        // 新增配置
        roleMenuService.saveBatch(roleMenus);

        // 删除角色配置的数据权限集合
        roleScopeService.remove(Wrappers.<RoleScope>update().lambda().eq(RoleScope::getScopeCategory, DATA_SCOPE_CATEGORY).in(RoleScope::getRoleId, roleIds));
        // 组装配置
        List<RoleScope> roleDataScopes = new ArrayList<>();
        roleIds.forEach(roleId -> dataScopeIds.forEach(scopeId -> {
            RoleScope roleScope = new RoleScope();
            roleScope.setScopeCategory(DATA_SCOPE_CATEGORY);
            roleScope.setRoleId(roleId);
            roleScope.setScopeId(scopeId);
            roleDataScopes.add(roleScope);
        }));
        // 新增配置
        roleScopeService.saveBatch(roleDataScopes);

        // 删除角色配置的接口权限集合
        roleScopeService.remove(Wrappers.<RoleScope>update().lambda().eq(RoleScope::getScopeCategory, API_SCOPE_CATEGORY).in(RoleScope::getRoleId, roleIds));
        // 组装配置
        List<RoleScope> roleApiScopes = new ArrayList<>();
        roleIds.forEach(roleId -> apiScopeIds.forEach(scopeId -> {
            RoleScope roleScope = new RoleScope();
            roleScope.setScopeCategory(API_SCOPE_CATEGORY);
            roleScope.setScopeId(scopeId);
            roleScope.setRoleId(roleId);
            roleApiScopes.add(roleScope);
        }));
        // 新增配置
        roleScopeService.saveBatch(roleApiScopes);

        return true;
    }

    @Override
    public List<String> getRoleNames(String roleIds) {
        return baseMapper.getRoleNames(Func.toLongArray(roleIds));
    }

    @Override
    public List<String> getRoleAliases(String roleIds) {
        return baseMapper.getRoleAliases(Func.toLongArray(roleIds));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean submit(Role role) {
        if (!AuthUtil.isAdministrator()) {
            if (Func.toStr(role.getRoleAlias()).equals(RoleConstant.ADMINISTRATOR)) {
                throw new ServiceException("无权限创建超管角色！");
            }
        }
        if (role.getId() == null && StringUtils.isBlank(role.getRoleAlias())) {
            role.setRoleAlias("user");
        }
        if (Func.isEmpty(role.getId())) {
            role.setTenantId(AuthUtil.getTenantId());
        }
        if (Func.isEmpty(role.getParentId())) {
            role.setParentId(BladeConstant.TOP_PARENT_ID);
        }
        role.setIsDeleted(BladeConstant.DB_NOT_DELETED);
        boolean result = saveOrUpdate(role);
        RoleCache.saveOrUpdateRole(role);
        return result;
    }

    /**
     * 逻辑删除
     *
     * @param roleIds
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean deleteLogic(String roleIds) {
        if (StringUtils.isNotBlank(roleIds)) {
            String[] roleIdList = Func.toStrArray(roleIds);
            for (String roleId : roleIdList) {
                Role role = getRoleById(Long.valueOf(roleId));
                if (role != null) {
                    //判断该角色是否与菜单关联,如果关联则不能被删除
                    QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<RoleMenu>();
                    queryWrapper.eq("role_id", roleId);
                    List<RoleMenu> roleMenuList = roleMenuMapper.selectList(queryWrapper);
                    if (CollectionUtil.isNotEmpty(roleMenuList)) {
                        throw new ServiceException(role.getRoleName() + "已使用,不能删除");
                    }
                    removeById(roleId);
                    RoleCache.saveOrUpdateRole(role);
                }
            }
        }
        return true;
    }

    /**
     * 根据条件查询角色
     *
     * @param role
     * @return
     */
    @Override
    public List<Role> selectRole(Role role) {
        return baseMapper.selectRole(role);
    }

    /**
     * 根据主键查询角色
     *
     * @param roleId
     * @return
     */
    @Override
    public Role getRoleById(Long roleId) {
        Role role = new Role();
        role.setId(roleId);
        List<Role> roleList = baseMapper.selectRole(role);
        if (roleList == null || roleList.size() == 0) {
            return null;
        }
        return roleList.get(0);
    }

    /**
     * 根据项目编码查询
     *
     * @param projectCode
     * @return
     */
    @Override
    public List<Role> listRoleByProjectCode(String projectCode) {
        return baseMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getTenantId, projectCode));
    }

    /**
     * 根据用户ID查询关联的所有项目及关联的角色
     *
     * @param userId
     * @return
     */
    @Override
    public UserProjectRoleVO listUserProjectRole(Long userId) {
//        User user = UserCache.getUser(userId);
        R<User> userInfoResult = userClient.userInfoById(userId);
        if (userInfoResult.isSuccess() && userInfoResult.getData() != null) {
            User user = userInfoResult.getData();
            String roleIds = user.getRoleId();
            if (StringUtils.isBlank(roleIds)) {
                return null;
            }
            List<Long> roleIdList = Func.toLongList(roleIds);
            List<Role> userRoleList = roleIdList.stream().map(roleId -> {
                return RoleCache.getRole(String.valueOf(roleId));
            }).filter(role -> role != null && role.getId() != null).collect(Collectors.toList());

            Map<String, List<Role>> userProjectIdRoleMap = new LinkedHashMap<String, List<Role>>();
            userProjectIdRoleMap = userRoleList.stream().collect(Collectors.groupingBy(Role::getTenantId));
//            LinkedHashMap<Project, List<Role>> userProjectRoleMap = new LinkedHashMap<Project, List<Role>>();
            List<ProjectRoleVO> userProjectRoleList = new ArrayList<ProjectRoleVO>();
            userProjectIdRoleMap.entrySet().stream().forEach(userProjectIdRole -> {
                String projectCode = userProjectIdRole.getKey();
                Project project = ProjectCache.getProjectByCode(projectCode);
                if (project == null || project.getId() == null) {
                    return;
                }
                List<Role> selectedRoleList = userProjectIdRole.getValue();
                ProjectRoleVO projectRoleVO = new ProjectRoleVO();
                projectRoleVO.setProjectId(project.getId());
                projectRoleVO.setProjectCode(project.getProjectCode());
                projectRoleVO.setProjectName(project.getProjectName());
                projectRoleVO.setRoleList(selectedRoleList);
                userProjectRoleList.add(projectRoleVO);
            });
            List<Project> allProject = projectMapper.selectList(new QueryWrapper<Project>());
            UserProjectRoleVO userProjectRoleVO = new UserProjectRoleVO();
            userProjectRoleVO.setUserId(userId);
            userProjectRoleVO.setUserProjectRoleList(userProjectRoleList);
            userProjectRoleVO.setAllProjectList(allProject);
            return userProjectRoleVO;
        }
        return null;
    }
}
