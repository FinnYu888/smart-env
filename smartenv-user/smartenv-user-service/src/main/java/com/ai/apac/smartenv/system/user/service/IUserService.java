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
package com.ai.apac.smartenv.system.user.service;


import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.entity.UserInfo;
import com.ai.apac.smartenv.system.user.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

import java.util.List;

/**
 * 服务类
 *
 * @author Chill
 */
public interface IUserService extends BaseService<User> {

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    boolean submit(User user);

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    boolean updateUser(User user);

    /**
     * 校验帐号是否重复
     *
     * @param account
     * @return
     */
    boolean validAccount(String account);

    /**
     * 自定义分页
     *
     * @param page
     * @param user
     * @return
     */
    IPage<User> selectUserPage(IPage<User> page, User user, Long deptId, String tenantId);

    /**
     * 自定义分页
     *
     * @param page
     * @param user
     * @return
     */
    IPage<UserVO> selectUserPageByCond(IPage<UserVO> page, User user, Long deptId, List<String> roleGroupList, String tenantId);

    /**
     * 根据查询条件查询,忽略逻辑删除属性
     *
     * @param user
     * @param tenantId
     * @return
     */
    List<User> selectUserByCond(User user, String tenantId);

    /**
     * 用户信息
     *
     * @param tenantId
     * @param account
     * @return
     */
    UserInfo userInfo(String tenantId, String account);

    /**
     * 根据帐号获取用户信息
     *
     * @param account
     * @return
     */
    UserInfo getUserInfoByAccount(String account);

    /**
     * 给用户设置角色
     *
     * @param userIds
     * @param roleIds
     * @return
     */
    boolean grant(String userIds, String roleIds);

    /**
     * 初始化密码
     *
     * @param userIds
     * @return
     */
    boolean resetPassword(String userIds);

    /**
     * 初始化密码
     *
     * @param userIds
     * @return
     */
    boolean resetPassword(String[] userIds);

    /**
     * 修改密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param newPassword1
     * @return
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword, String newPassword1);

    /**
     * 删除用户
     *
     * @param userIds
     * @return
     */
    boolean removeUser(String userIds);

    /**
     * 根据部门ID查询用户
     *
     * @param deptId
     * @return
     */
    List<User> getUserByDeptId(Long deptId);

    /**
     * 根据部门ID查询未与员工绑定的用户帐号
     *
     * @param tenantId
     * @param deptId
     * @return
     */
    List<User> getUnBindUserByDeptId(String tenantId, Long deptId);

    /**
     * 根据租户查询操作员用户
     *
     * @return
     */
    List<User> getUserByTenant(String tenantId);

    List<User> getUserByRole(String roleId, String tenantId);

    /**
     * 获取所有操作员用户
     *
     * @return
     */
    List<User> getAllUser();

    /**
     * 根据用户ID查询用户信息,忽略逻辑删除属性
     *
     * @param userId
     * @return
     */
    User getUserById(Long userId);

    /**
     * 设置用户的角色组
     *
     * @param userId
     */
    void setRoleGroup(Long userId);

    /**
     * 重新设置所有用户的角色组,只是为了清理数据用,不在前端提供菜单
     */
    void resetAllUserRoleGroup();
}
