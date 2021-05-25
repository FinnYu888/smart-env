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
package com.ai.apac.smartenv.system.user.feign;

import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.entity.UserInfo;
import com.ai.apac.smartenv.system.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务Feign实现类
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
public class UserClient implements IUserClient {

    private IUserService service;

    @Override
    @GetMapping(USER_INFO_BY_ID)
    public R<User> userInfoById(@RequestParam("userId") Long userId) {
        return R.data(service.getById(userId));
    }

    @Override
    @GetMapping(USER_INFO)
    public R<UserInfo> userInfo(@RequestParam("tenantId") String tenantId, @RequestParam("account") String account) {
        return R.data(service.userInfo(tenantId, account));
    }

    /**
     * 根据帐户获取用户信息
     *
     * @param account 账号
     * @return
     */
    @Override
    public R<UserInfo> getUserInfoByAccount(@RequestParam("account") String account) {
        return R.data(service.getUserInfoByAccount(account));
    }

    @Override
    @PostMapping(SAVE_USER)
    public R<Boolean> saveUser(@RequestBody User user) {
        return R.data(service.submit(user));
    }

    /**
     * 更新用户
     *
     * @param user 用户实体
     * @return
     */
    @Override
    @PostMapping(UPDATE_USER)
    public R<Boolean> updateUser(@RequestBody User user) {
        return R.data(service.updateUser(user));
    }

    /**
     * 根据部门获取用户信息
     *
     * @param deptId id
     * @return
     */
    @Override
    @GetMapping(USER_INFO_BY_DEPT_ID)
    public R<List<User>> userInfoByDeptId(@RequestParam("deptId") Long deptId) {
        return R.data(service.getUserByDeptId(deptId));
    }

    /**
     * 获取所有用户信息
     *
     * @return
     */
    @Override
    @GetMapping(GET_ALL_USER)
    public R<List<User>> getAllUser() {
        return R.data(service.getAllUser());
    }

    /**
     * 获取指定租户的所有用户信息
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(GET_TENANT_USER)
    public R<List<User>> getTenantUser(@RequestParam("tenantId")  String tenantId) {
        return R.data(service.getUserByTenant(tenantId));
    }

    @Override
    @GetMapping(GET_ROLE_USER)
    public R<List<User>> getRoleUser(@RequestParam("roleId")  String roleId,@RequestParam("tenantId") String tenantId) {
        return R.data(service.getUserByRole(roleId, tenantId));
    }

    /**
     * 根据帐户获取用户信息
     *
     * @param account 用户id
     * @return
     */
    @Override
    @GetMapping(USER_BY_ACCOUNT)
    public R<User> userByAcct(@RequestParam("account") String account) {
        List<User> list = service.list(new LambdaQueryWrapper<User>().eq(User::getAccount, account));
        if (list == null || list.size() == 0) {
            return R.data(null);
        }
        return R.data(list.get(0));
    }

}
