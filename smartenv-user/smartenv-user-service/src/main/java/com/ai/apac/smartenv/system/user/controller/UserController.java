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
package com.ai.apac.smartenv.system.user.controller;


import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.service.IUserService;
import com.ai.apac.smartenv.system.user.vo.UpdatePasswordVO;
import com.ai.apac.smartenv.system.user.vo.UserVO;
import com.ai.apac.smartenv.system.user.wrapper.UserWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.*;
import static org.springblade.core.cache.constant.CacheConstant.USER_CACHE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@RequestMapping
@AllArgsConstructor
@Api(value = "用户管理", tags = "用户管理")
public class UserController {

    private IUserService userService;

    /**
     * 查询单条
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查看详情", notes = "传入id")
    @GetMapping("/detail")
    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "查询操作员信息")
    public R<UserVO> detail(User user) {
        User detail = userService.getOne(Condition.getQueryWrapper(user));
        if (detail != null) {
            return R.data(UserWrapper.build().entityVO(detail));
        } else {
            return R.success("用户不存在");
        }
    }

    /**
     * 查询单条
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "根据ID查看详情", notes = "传入id")
    @GetMapping("/info")
    @ApiLog(value = "根据操作员ID查询信息")
    public R<UserVO> info(@RequestParam Long userId, BladeUser user) {
        User detail = userService.getById(userId);
        if (detail != null) {
            return R.data(UserWrapper.build().entityVO(detail));
        } else {
            return R.success("用户不存在");
        }
    }

    /**
     * 用户列表
     */
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "realName", value = "姓名", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入account和realName")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "分页查询操作员列表")
    public R<IPage<UserVO>> list(@ApiIgnore @RequestParam Map<String, Object> user, Query query, BladeUser bladeUser) {
        QueryWrapper<User> queryWrapper = Condition.getQueryWrapper(user, User.class);
        IPage<User> pages = userService.page(Condition.getPage(query), queryWrapper.lambda().eq(User::getTenantId, bladeUser.getTenantId()));
        return R.data(UserWrapper.build().pageVO(pages));
    }

    /**
     * 自定义用户列表
     */
    @GetMapping("/page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "realName", value = "姓名", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "列表", notes = "传入account和realName")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "自定义分页查询操作员列表")
    public R<IPage<UserVO>> page(@ApiIgnore User user, Query query, Long deptId, BladeUser bladeUser) {
        String userRoleGroup = bladeUser.getRoleGroup();
        List<String> roleGroupList = new ArrayList<String>();
        if (StringUtils.isNotBlank(userRoleGroup)) {
            if (userRoleGroup.equalsIgnoreCase(ADMINISTRATOR)) {
                roleGroupList = null;
            } else if (userRoleGroup.equalsIgnoreCase(ADMIN)) {
                roleGroupList.add(ADMIN);
                roleGroupList.add(USER);
            } else {
                roleGroupList.add(USER);
            }
        }
        IPage<UserVO> pages = userService.selectUserPageByCond(Condition.getPage(query), user, deptId, roleGroupList, bladeUser.getTenantId());
        return R.data(pages);
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增或修改", notes = "传入User")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "新增/修改操作员")
    public R submit(@Valid @RequestBody UserVO userVO, BladeUser bladeUser) {
        //取当前操作员的租户ID作为新帐号的租户ID
        userVO.setTenantId(bladeUser.getTenantId());
        User user = new User();
        BeanUtil.copy(userVO, user);
        String[] roleIdList = userVO.getRoleIdList();
        if (roleIdList != null && roleIdList.length > 0) {
            user.setRoleId(Func.join(roleIdList));
        }
        CacheUtil.clear(USER_CACHE);
        return R.status(userService.submit(user));
    }

    /**
     * 校验帐户登录名是否重复
     */
    @GetMapping("/validAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号名", paramType = "query", dataType = "string")
    })
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "校验帐户登录名是否重复", notes = "校验帐户登录名是否重复,重复返回true,否则返回false")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    public R validAccount(@RequestParam String account, BladeUser bladeUser) {
        boolean result = userService.validAccount(account);
        if (!result) {
            return R.data(true, "帐号已重复");
        } else {
            return R.data(false, "帐号不重复");
        }
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入User")
    @ApiLog(value = "修改操作员")
    public R update(@Valid @RequestBody User user) {
        CacheUtil.clear(USER_CACHE);
        return R.status(userService.updateUser(user));
    }

    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "删除", notes = "传入id集合")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "删除操作员")
    public R remove(@RequestParam String ids) {
        CacheUtil.clear(USER_CACHE);
        return R.status(userService.removeUser(ids));
    }

    /**
     * 设置菜单权限
     *
     * @param userIds
     * @param roleIds
     * @return
     */
    @PostMapping("/grant")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "权限设置", notes = "传入roleId集合以及userId集合")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "操作员授权")
    public R grant(@ApiParam(value = "userId集合", required = true) @RequestParam String userIds,
                   @ApiParam(value = "roleId集合", required = true) @RequestParam String roleIds) {
        boolean temp = userService.grant(userIds, roleIds);
        return R.status(temp);
    }

//    @PostMapping("/reset-password")
//    @ApiOperationSupport(order = 8)
//    @ApiOperation(value = "初始化密码", notes = "传入userId集合")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
//    public R resetPassword(@ApiParam(value = "userId集合", required = true) @RequestParam String userIds) {
//        boolean temp = userService.resetPassword(userIds);
//        return R.status(temp);
//    }

    @PostMapping("/reset-password")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "初始化密码", notes = "传入userId集合")
//    @PreAuth(RoleConstant.HAS_ROLE_ADMIN)
    @ApiLog(value = "重置操作员密码")
    public R batchResetPassword(@ApiParam(value = "userId集合", required = true) @RequestBody String[] userIds) {
        boolean temp = userService.resetPassword(userIds);
        return R.status(temp);
    }

    /**
     * 修改密码
     *
     * @param updatePasswordVO
     * @return
     */
    @PostMapping("/update-password")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "修改密码", notes = "传入密码")
    @ApiLog(value = "修改操作员密码")
    public R updatePassword(BladeUser user, @RequestBody UpdatePasswordVO updatePasswordVO) {
        boolean temp = userService.updatePassword(user.getUserId(), updatePasswordVO.getOldPassword(),
                updatePasswordVO.getNewPassword(), updatePasswordVO.getNewPassword1());
        return R.status(temp);
    }

    /**
     * 用户列表
     *
     * @param user
     * @return
     */
    @GetMapping("/user-list")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "用户列表", notes = "传入user")
    @ApiLog(value = "查询操作员员列表")
    public R<List<User>> userList(User user) {
        List<User> list = userService.list(Condition.getQueryWrapper(user));
        return R.data(list);
    }

    /**
     * 重新设置所有用户的角色组
     *
     * @return
     */
    @PutMapping("/set-user-role-group")
    @ApiOperationSupport(order = 11)
    @ApiOperation(value = "重新设置所有用户的角色组", notes = "重新设置所有用户的角色组")
    @PreAuth(RoleConstant.HAS_ROLE_ADMINISTRATOR)
    public R resetAllUserRoleGroup() {
        userService.resetAllUserRoleGroup();
        return R.status(true);
    }

    /**
     * 根据部门查询未被绑定过员工的帐号
     *
     * @return
     */
    @GetMapping("/unbindUsers")
    @ApiOperationSupport(order = 12)
    @ApiOperation(value = "根据部门查询未被绑定过员工的帐号", notes = "根据部门查询未被绑定过员工的帐号")
    public R<List<User>> getUnBindUserList(@RequestParam Long deptId, BladeUser bladeUser) {
        List<User> userList = userService.getUnBindUserByDeptId(bladeUser.getTenantId(),deptId);
        return R.data(userList);
    }
}
