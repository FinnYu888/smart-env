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
package com.ai.apac.smartenv.system.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ai.apac.smartenv.common.constant.CommonConstant;

import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.*;

import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.feign.IPushcClient;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.cache.SysCache;
import com.ai.apac.smartenv.system.entity.AccountProjectRel;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.feign.IProjectClient;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.entity.UserDept;
import com.ai.apac.smartenv.system.user.entity.UserInfo;
import com.ai.apac.smartenv.system.user.mapper.UserMapper;
import com.ai.apac.smartenv.system.user.service.IUserDeptService;
import com.ai.apac.smartenv.system.user.service.IUserService;
import com.ai.apac.smartenv.system.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.utils.SecureUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {

    private IUserDeptService userDeptService;

    private IPersonUserRelClient personUserRelClient;

    private IPushcClient pushcClient;

    private IPersonClient personClient;

    private IProjectClient projectClient;

    /**
     * 根据用户ID设置该用户的角色组
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public void setRoleGroup(Long userId) {
        User user = getUserById(userId);
        if (user != null) {
            String roleIds = user.getRoleId();
            if (StringUtils.isBlank(roleIds)) {
                user.setRoleGroup(USER);
            } else {
                String roleGroup = this.getRoleGroup(roleIds);
                user.setRoleGroup(roleGroup);
            }
            baseMapper.updateById(user);
        }
    }

    /**
     * 根据角色id获取角色组名称
     *
     * @param roleIds
     * @return
     */
    private String getRoleGroup(String roleIds) {
        List<Long> roleIdList = Func.toLongList(roleIds);
        List<String> roleAliasList = new ArrayList<String>();
        roleIdList.stream().forEach(roleId -> {
            Role role = RoleCache.getRole(String.valueOf(roleId));
            if (role != null) {
                roleAliasList.add(role.getRoleAlias());
            }
        });
        if (roleAliasList.contains(ADMINISTRATOR)) {
            return ADMINISTRATOR;
        } else if (roleAliasList.contains(ADMIN)) {
            return ADMIN;
        } else {
            return USER;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean submit(User user) {
        boolean result = false;
        user.setName(CommonUtil.getNickName(user.getRealName()));
        if (null == user.getId()) {//ID为空代表新增
            user.setPassword(DigestUtil.encrypt(CommonConstant.DEFAULT_PASSWORD));
            Integer cnt = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getAccount, user.getAccount()));
            if (cnt > 0 && Func.isEmpty(user.getId())) {
                throw new ServiceException("登录帐户不能重复!");
            }
            result = save(user) && submitUserDept(user);
        } else {
            //调用更新服务
            result = updateById(user) && submitUserDept(user);
        }
        //更新角色组
        setRoleGroup(user.getId());

        //根据角色所关联的项目将用户与项目关联起来
        String roleIds = user.getRoleId();
        List<String> roleIdList = Func.toStrList(roleIds);
        List<String> projectCodeList = roleIdList.stream().map(roleId -> {
            Role role = RoleCache.getRole(roleId);
            if (role != null && role.getId() != null) {
                return role.getTenantId();
            }
            return null;
        }).filter(projectCode -> StringUtils.isNotEmpty(projectCode)).collect(Collectors.toList());
        if (result) {
            user = getById(user.getId());
            UserCache.saveOrUpdateUser(user);
        }
        projectCodeList = projectCodeList.stream().distinct().collect(Collectors.toList());
        if (CollUtil.isNotEmpty(projectCodeList)) {
            projectClient.addAccountProjectRel(user.getId(), projectCodeList);
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean updateUser(User user) {
        user.setPassword(null);
        boolean result = updateById(user) && submitUserDept(user);
        //更新角色组
        setRoleGroup(user.getId());
        if (result) {
            UserCache.saveOrUpdateUser(getById(user.getId()));
        }
        return result;
    }

    /**
     * 校验帐号是否重复
     *
     * @param account
     * @return
     */
    @Override
    public boolean validAccount(String account) {
        Integer cnt = baseMapper.selectCount(Wrappers.<User>query().lambda().eq(User::getAccount, account));
        if (cnt > 0) {
            return false;
        }
        return true;
//        UserInfo userInfo = getUserInfoByAccount(account);
//        return userInfo != null && userInfo.getUser() != null && userInfo.getUser().getId() != null;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    private boolean submitUserDept(User user) {
        if (StringUtils.isBlank(user.getDeptId())) {
            return true;
        }
        List<Long> deptIdList = Func.toLongList(user.getDeptId());
        List<UserDept> userDeptList = new ArrayList<>();
        deptIdList.forEach(deptId -> {
            UserDept userDept = new UserDept();
            userDept.setUserId(user.getId());
            userDept.setDeptId(deptId);
            userDeptList.add(userDept);
        });
        userDeptService.remove(Wrappers.<UserDept>query().lambda().eq(UserDept::getUserId, user.getId()));
        return userDeptService.saveBatch(userDeptList);
    }

    @Override
    public IPage<User> selectUserPage(IPage<User> page, User user, Long deptId, String tenantId) {
        List<Long> deptIdList = SysCache.getDeptChildIds(deptId);
        return page.setRecords(baseMapper.selectUserPage(page, user, deptIdList, tenantId));
    }

    /**
     * 自定义分页
     *
     * @param page
     * @param user
     * @param deptId
     * @param roleGroupList
     * @param tenantId
     * @return
     */
    @Override
    public IPage<UserVO> selectUserPageByCond(IPage<UserVO> page, User user, Long deptId,
                                              List<String> roleGroupList, String tenantId) {
        List<Long> deptIdList = SysCache.getDeptChildIds(deptId);
        List<UserVO> result = baseMapper.selectUserPageByCond(page, user, deptIdList, roleGroupList, tenantId);
        result.stream().forEach(userVO -> {
            userVO.setRoleName(RoleCache.roleNames(user.getRoleId()));
            userVO.setSexName(DictCache.getValue("sex", Func.toInt(userVO.getSex())));
            userVO.setStatusName(DictCache.getValue("user_status", Func.toInt(userVO.getStatus())));
            if (StringUtils.isNotBlank(userVO.getRoleId())) {
                String[] roleIdList = Func.toStrArray(userVO.getRoleId());
                userVO.setRoleIdList(roleIdList);
                userVO.setRoleName(RoleCache.roleNames(userVO.getRoleId()));
            }
        });
        return page.setRecords(result);
    }

    @Override
    public UserInfo userInfo(String tenantId, String account) {
        UserInfo userInfo = new UserInfo();
        User user = baseMapper.getUser(tenantId, account);
        userInfo.setUser(user);
        if (Func.isNotEmpty(user)) {
            List<String> roleAlias = SysCache.getRoleAliases(user.getRoleId());
            userInfo.setRoles(roleAlias);
        }
        return userInfo;
    }

    /**
     * 用户信息
     *
     * @param account account也支持手机号
     * @return
     */
    @Override
    public UserInfo getUserInfoByAccount(String account) {
        UserInfo userInfo = new UserInfo();
        User user = baseMapper.getUserByAccount(account);
        if (user == null) {
            //直接根据登录帐号没有查询到数据就根据手机号来查
            R<Person> personResult = personClient.getPersonByMobile(account);
            if (personResult == null || personResult.getData() == null || personResult.getData().getId() == null) {
                throw new ServiceException("登录帐号不存在");
            }
            R<PersonUserRel> personUserRelResult = personUserRelClient.getRelByPersonId(personResult.getData().getId());
            if (personUserRelResult == null || personUserRelResult.getData() == null) {
                throw new ServiceException("登录帐号不存在");
            } else {
                Long userId = personUserRelResult.getData().getUserId();
                user = baseMapper.selectById(userId);
            }
        }
        userInfo.setUser(user);
        if (Func.isNotEmpty(user)) {
            List<String> roleAlias = SysCache.getRoleAliases(user.getRoleId());
            userInfo.setRoles(roleAlias);
        }
        return userInfo;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean grant(String userIds, String roleIds) {
        User user = new User();
        user.setRoleId(roleIds);
        List<Long> userIdList = Func.toLongList(userIds);
        boolean result = this.update(user, Wrappers.<User>update().lambda().in(User::getId, userIdList));
        userIdList.stream().forEach(userId -> {
            User userTmp = getById(user.getId());
            if (userTmp != null) {
                UserCache.saveOrUpdateUser(userTmp);
            }
        });
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean resetPassword(String userIds) {
        User user = new User();
        user.setPassword(DigestUtil.encrypt(CommonConstant.DEFAULT_PASSWORD));
        user.setUpdateTime(DateUtil.now());
        List<Long> userIdList = Func.toLongList(userIds);
        boolean result = this.update(user, Wrappers.<User>update().lambda().in(User::getId, userIdList));
        userIdList.stream().forEach(userId -> {
            User userTmp = getById(userId);
            if (userTmp != null) {
                UserCache.saveOrUpdateUser(userTmp);

                //给用户发送邮件
                EmailDTO emailDTO = new EmailDTO();
                emailDTO.setReceiver(userTmp.getEmail());
                emailDTO.setSubject("重置密码");
                emailDTO.setContent(StrUtil.format("您的登录密码是{}", CommonConstant.DEFAULT_PASSWORD));
                pushcClient.sendEmail(emailDTO);
            }
        });


        return result;
    }

    /**
     * 初始化密码
     *
     * @param userIds
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean resetPassword(String[] userIds) {
        if (userIds != null && userIds.length > 0) {
            String userIdStr = Func.join(userIds);
            this.resetPassword(userIdStr);
        }
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean updatePassword(Long userId, String oldPassword, String newPassword, String newPassword1) {
        User user = getById(userId);
        if (!newPassword.equals(newPassword1)) {
            throw new ServiceException("请输入正确的确认密码!");
        }
        //旧密码和新密码都进行base64解密
        String oldPwd = Base64Util.decode(oldPassword);
        String newPwd = Base64Util.decode(newPassword);
        if (!user.getPassword().equals(DigestUtil.encrypt(oldPwd))) {
            throw new ServiceException("原密码不正确!");
        }
        boolean result = this.update(Wrappers.<User>update().lambda().set(User::getPassword, DigestUtil.encrypt(newPwd)).eq(User::getId, userId));
        if (result) {
            user = getById(userId);
            UserCache.saveOrUpdateUser(user);
        }

        //发送邮件
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setSubject("修改密码成功");
        emailDTO.setReceiver(user.getEmail());
        emailDTO.setContent("您的密码修改成功。");
        pushcClient.sendEmail(emailDTO);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean removeUser(String userIds) {
        if (Func.contains(Func.toLongArray(userIds), SecureUtil.getUserId())) {
            throw new ApiException("不能删除本账号!");
        }
        List<Long> userIdList = Func.toLongList(userIds);
        boolean result = deleteLogic(userIdList);
        userIdList.stream().forEach(userId -> {
            User user = getById(userId);
            if (user != null) {
                UserCache.saveOrUpdateUser(user);
            }
        });
        return result;
    }

    /**
     * 根据部门ID查询用户
     *
     * @param deptId
     * @return
     */
    @Override
    public List<User> getUserByDeptId(Long deptId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("dept_id", deptId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据部门ID查询未与员工绑定的用户帐号
     *
     * @param tenantId
     * @param deptId
     * @return
     */
    @Override
    public List<User> getUnBindUserByDeptId(String tenantId, Long deptId) {
        //查询该租户下的员工与帐号绑定关系
        R<List<PersonUserRel>> personUserRelListResult = personUserRelClient.getRelByTenant(tenantId);
        List<Long> userIdList = null;
        if (personUserRelListResult != null && personUserRelListResult.isSuccess()) {
            List<PersonUserRel> relList = personUserRelListResult.getData();
            if (CollUtil.isNotEmpty(relList)) {
                userIdList = relList.stream().map(PersonUserRel::getUserId).collect(Collectors.toList());
            }
        } else {
            userIdList = new ArrayList<Long>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("dept_id", deptId);
        queryWrapper.notIn("id", userIdList);
        queryWrapper.eq("tenant_id", tenantId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据查询条件查询,忽略逻辑删除属性
     *
     * @param user
     * @param tenantId
     * @return
     */
    @Override
    public List<User> selectUserByCond(User user, String tenantId) {
        List<User> result = baseMapper.selectUserByCond(user, tenantId);
        return result;
    }

    /**
     * 根据租户查询操作员用户
     *
     * @param tenantId
     * @return
     */
    @Override
    public List<User> getUserByTenant(String tenantId) {
        return selectUserByCond(new User(), tenantId);
    }

    @Override
    public List<User> getUserByRole(String roleId, String tenantId) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        wrapper.lambda().eq(User::getRoleId, roleId).eq(User::getTenantId, tenantId);
        return this.list(wrapper);
    }

    /**
     * 获取所有操作员用户
     *
     * @return
     */
    @Override
    public List<User> getAllUser() {
        return selectUserByCond(new User(), null);
    }

    /**
     * 根据用户ID查询用户信息,忽略逻辑删除属性
     *
     * @param userId
     * @return
     */
    @Override
    public User getUserById(Long userId) {
        User user = new User();
        user.setId(userId);
        List<User> result = baseMapper.selectUserByCond(user, null);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    /**
     * 重新设置所有用户的角色组,只是为了清理数据用,不在前端提供菜单
     */
    @Override
    public void resetAllUserRoleGroup() {
        //获取当前系统所有用户
        List<User> allUser = getAllUser();
        allUser.stream().forEach(user -> {
            setRoleGroup(user.getId());
        });
    }
}
