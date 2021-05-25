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
package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.service.IRoleService;
import com.ai.apac.smartenv.system.vo.GrantVO;
import com.ai.apac.smartenv.system.vo.RoleVO;
import com.ai.apac.smartenv.system.vo.UserProjectRoleVO;
import com.ai.apac.smartenv.system.wrapper.RoleWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springblade.core.tool.utils.Func;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/role")
@Api(value = "角色", tags = "角色")
//@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
public class RoleController extends BladeController {

    private IRoleService roleService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入role")
    @ApiLog(value = "查询角色详情")
    public R<RoleVO> detail(Role role) {
        Role detail = roleService.getOne(Condition.getQueryWrapper(role));
        return R.data(RoleWrapper.build().entityVO(detail));
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "参数名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "roleAlias", value = "角色别名", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "列表", notes = "传入role")
    @ApiLog(value = "查询角色列表")
    public R<List<INode>> list(@ApiIgnore @RequestParam Map<String, Object> role, BladeUser bladeUser) {
        QueryWrapper<Role> queryWrapper = Condition.getQueryWrapper(role, Role.class);
//		List<Role> list = roleService.list((!bladeUser.getTenantId().equals(BladeConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Role::getTenantId, bladeUser.getTenantId()) : queryWrapper);
        List<Role> list = roleService.list(queryWrapper.lambda().eq(Role::getTenantId, bladeUser.getTenantId()));
        /**
         * 对查询结果进行过滤
         * 用户有administrator角色,能看到租户下所有角色
         * 用户只有admin角色,只能看到admin和user类别的角色
         * 用户只有user角色,那就只能看到user类别的角色
         */
        String roleIds = bladeUser.getRoleId();
        List<Long> roleIdList = Func.toLongList(roleIds);
        List<String> roleAliasList = new ArrayList<String>();
        if (roleIdList != null && roleIdList.size() > 0) {
            roleIdList.stream().forEach(roleId -> {
                Role roleInfo = RoleCache.getRole(String.valueOf(roleId));
                if (roleInfo != null) {
                    roleAliasList.add(roleInfo.getRoleAlias());
                }
            });
        }
        if (roleAliasList.contains(SystemConstant.RoleAlias.ADMINISTRATOR)) {
            return R.data(RoleWrapper.build().listNodeVO(list));
        } else if (roleAliasList.contains(SystemConstant.RoleAlias.ADMIN)) {
            list = list.stream()
                    .filter(roleInfo -> !roleInfo.getRoleAlias().equalsIgnoreCase(SystemConstant.RoleAlias.ADMINISTRATOR))
                    .collect(Collectors.toList());
        } else {
            list = list.stream()
                    .filter(roleInfo -> !roleInfo.getRoleAlias().equalsIgnoreCase(SystemConstant.RoleAlias.ADMINISTRATOR)
                            && !roleInfo.getRoleAlias().equalsIgnoreCase(SystemConstant.RoleAlias.ADMIN))
                    .collect(Collectors.toList());
        }
        return R.data(RoleWrapper.build().listNodeVO(list));
    }

    /**
     * 获取角色树形结构
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "树形结构", notes = "树形结构")
    @ApiLog(value = "查询角色树")
    public R<List<RoleVO>> tree(String tenantId, BladeUser bladeUser) {
        List<RoleVO> tree = roleService.tree(Func.toStrWithEmpty(tenantId, bladeUser.getTenantId()));
        return R.data(tree);
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增或修改", notes = "传入role")
    @ApiLog(value = "新增/修改角色")
    public R submit(@Valid @RequestBody Role role) {
        CacheUtil.clear(SYS_CACHE);
        return R.status(roleService.submit(role));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    @ApiLog(value = "删除角色")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        CacheUtil.clear(SYS_CACHE);
        return R.status(roleService.deleteLogic(ids));
    }

    /**
     * 设置角色权限
     */
    @PostMapping("/grant")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "权限设置", notes = "传入roleId集合以及menuId集合")
    @ApiLog(value = "设备角色权限")
    public R grant(@RequestBody GrantVO grantVO) {
        CacheUtil.clear(SYS_CACHE);
        boolean temp = roleService.grant(grantVO.getRoleIds(), grantVO.getMenuIds(), grantVO.getDataScopeIds(), grantVO.getApiScopeIds());
        return R.status(temp);
    }

    /**
     * 根据项目编码查询角色
     *
     * @param projectCode
     * @return
     */
    @GetMapping("/{projectCode}/roleList")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据项目编码查询角色", notes = "根据项目编码查询角色")
    public R<List<Role>> listProjectRole(@PathVariable("projectCode") String projectCode) {
        return R.data(roleService.list(new LambdaQueryWrapper<Role>().eq(Role::getTenantId, projectCode)));
    }

    /**
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/projectRoleList")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "查询用户项目角色", notes = "查询用户项目角色")
    public R<UserProjectRoleVO> listUserProjectRole(@PathVariable("userId") Long userId){
        return R.data(roleService.listUserProjectRole(userId));
    }

}
