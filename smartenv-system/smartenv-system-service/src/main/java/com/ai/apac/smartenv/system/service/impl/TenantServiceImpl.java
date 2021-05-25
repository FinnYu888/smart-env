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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.feign.IAlarmRuleInfoClient;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.person.entity.PersonUserRel;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.person.feign.IPersonUserRelClient;
import com.ai.apac.smartenv.pushc.dto.EmailDTO;
import com.ai.apac.smartenv.pushc.feign.IPushcClient;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.MenuCache;
import com.ai.apac.smartenv.system.cache.RoleCache;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.*;
import com.ai.apac.smartenv.system.mapper.TenantMapper;
import com.ai.apac.smartenv.system.service.*;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.entity.UserInfo;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.vo.TenantVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import feign.QueryMap;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tenant.TenantId;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMIN;
import static com.ai.apac.smartenv.common.constant.SystemConstant.RoleAlias.ADMINISTRATOR;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Service
@AllArgsConstructor
public class TenantServiceImpl extends BaseServiceImpl<TenantMapper, Tenant> implements ITenantService {

    private final TenantId tenantId;
    private final IRoleService roleService;
    private final IMenuService menuService;
    private final IDeptService deptService;
    private final IRoleMenuService roleMenuService;
    private final IUserClient userClient;
    private final IPersonClient personClient;
    private final IPersonUserRelClient personUserRelClient;
    private final IAlarmRuleInfoClient alarmRuleInfoClient;
    private final IStationService stationService;
    private final IPushcClient pushcClient;

    /**
     * 新建默认租户角色所分配的菜单主节点
     */
    private final List<String> menuCodes = Arrays.asList(
            "desk", "flow", "work", "monitor", "resource", "role", "user", "dept", "dictbiz", "topmenu"
    );

    @Override
    public IPage<Tenant> selectTenantPage(IPage<Tenant> page, Tenant tenant) {
        return page.setRecords(baseMapper.selectTenantPage(page, tenant));
    }

    @Override
    public Tenant getByTenantId(String tenantId) {
        return getOne(Wrappers.<Tenant>query().lambda().eq(Tenant::getTenantId, tenantId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {ServiceException.class, Exception.class})
    public boolean saveTenant(Tenant tenant) {
        boolean result = false;
        String adminAccount = tenant.getAdminAccount();
        if (StringUtils.isNotBlank(adminAccount)) {
            if (adminAccount.length() > 45) {
                throw new ServiceException("管理员帐号长度不能超过45个字符");
            }
        }
        String tenantName = tenant.getTenantName();
        if (StringUtils.isNotBlank(tenantName)) {
            if (tenantName.length() > 45) {
                throw new ServiceException("租户名称长度不能超过50个字符");
            }
        }
        String address = tenant.getAddress();
        if (StringUtils.isNotBlank(address)) {
            if (address.length() > 255) {
                throw new ServiceException("地址长度不能超过255个字符");
            }
        }
        if (Func.isEmpty(tenant.getId())) {
            //校验租户管理员帐号名是否重复
            R<User> userInfo = userClient.userByAcct(tenant.getAdminAccount());
            if (userInfo != null && userInfo.getData() != null && userInfo.getData().getId() != null) {
                throw new ServiceException("该管理员帐号在系统中已存在！");
            }
            List<Tenant> tenants = baseMapper.selectList(Wrappers.<Tenant>query().lambda().eq(Tenant::getIsDeleted, BladeConstant.DB_NOT_DELETED));
            tenants.stream().forEach(tenantObj -> {
                if (tenant.getTenantName().equals(tenantObj.getTenantName())) {
                    throw new ServiceException("该租户名称在系统中已存在！");
                }
            });
            List<String> codes = tenants.stream().map(Tenant::getTenantId).collect(Collectors.toList());
            String tenantId = getTenantId(codes);
            tenant.setTenantId(tenantId);
            tenant.setAccountNumber(-1);
            tenant.setLinkman(tenant.getAdminAccount());
            // 新建租户对应的默认角色
            Role role = new Role();
            role.setTenantId(tenantId);
            role.setParentId(BladeConstant.TOP_PARENT_ID);
            role.setRoleName("管理员");
            role.setRoleAlias("admin");
            role.setSort(2);
            role.setIsDeleted(0);
            roleService.save(role);
            //刷新新角色数据到缓存中去
            RoleCache.saveOrUpdateRole(role);
            // 新建租户对应的角色菜单权限
            LinkedList<Menu> userMenus = new LinkedList<>();
//			List<Menu> menus = getMenus(menuCodes, userMenus);
            List<Menu> menus = MenuCache.getAllWebMenu();
            menus.addAll(MenuCache.getAllAppMenu());
            List<RoleMenu> roleMenus = new ArrayList<>();
            menus.forEach(menu -> {
                if (!menu.getCode().startsWith("tenant")) {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setMenuId(menu.getId());
                    roleMenu.setRoleId(role.getId());
                    roleMenus.add(roleMenu);
                }
            });
            roleMenuService.saveBatch(roleMenus);
            // 新建租户对应的默认部门
            Dept dept = new Dept();
            dept.setTenantId(tenantId);
            dept.setParentId(BladeConstant.TOP_PARENT_ID);
            dept.setAncestors(String.valueOf(BladeConstant.TOP_PARENT_ID));
            dept.setDeptName("管理部");
            dept.setFullName("管理部");
            dept.setDeptCategory(1);
            dept.setSort(2);
            dept.setIsDeleted(0);
            deptService.save(dept);
            //刷新新部门数据到缓存中去
            DeptCache.saveOrUpdateDept(dept);
            // 新建租户对应的默认管理用户
            User user = new User();
            user.setTenantId(tenantId);
            user.setName(tenant.getLinkman());
            user.setRealName(tenant.getLinkman());
            user.setAccount(tenant.getAdminAccount());
            user.setPassword("123456");
            user.setEmail(tenant.getEmail());
            user.setRoleId(String.valueOf(role.getId()));
            user.setDeptId(String.valueOf(dept.getId()));
            user.setBirthday(new Date());
            user.setSex(1);
            user.setPhone(tenant.getContactNumber());
            user.setIsDeleted(0);
            result = super.saveOrUpdate(tenant);
            R<Boolean> userResult = userClient.saveUser(user);
            if (!userResult.isSuccess() && !userResult.getMsg().equals("登录帐户不能重复!")) {
                throw new ServiceException(userResult.getMsg());
            }

            //创建默认岗位
            Station defaultStation = new Station();
            defaultStation.setParentId(0L);
            defaultStation.setStationName("管理员");
            //岗位级别默认为管理级
            defaultStation.setStationLevel(4);
            defaultStation.setTenantId(tenantId);
            stationService.createStation(defaultStation);

            //新建租户默认员工,并和操作员帐号绑定
            Person adminPerson = new Person();
            adminPerson.setPersonName(user.getAccount());
            adminPerson.setEntryTime(user.getCreateTime());
            //租户管理员工号默认000001
            adminPerson.setJobNumber("000001");
            adminPerson.setPersonDeptId(dept.getId());
            adminPerson.setIsIncumbency(PersonConstant.IncumbencyStatus.IN);
            adminPerson.setTenantId(tenantId);
            adminPerson.setEmail(tenant.getEmail());
            adminPerson.setMobileNumber(tenant.getContactNumber());
            adminPerson.setPersonPositionId(defaultStation.getId());
            adminPerson.setEntryTime(new Date());
            adminPerson.setIsUser(PersonConstant.IsUser.YES);
            //性别默认为男
            adminPerson.setGender(1);
            R<Person> createPersonResult = personClient.createPerson(adminPerson);
            if (createPersonResult == null || !createPersonResult.isSuccess()) {
                throw new ServiceException("创建默认员工失败:" + createPersonResult.getMsg());
            } else {
                R<UserInfo> newUserInfo = userClient.getUserInfoByAccount(tenant.getAdminAccount());
                if (newUserInfo == null || newUserInfo.getData() == null && newUserInfo.getData().getUser().getId() == null) {
                    throw new ServiceException("创建默认登录帐号失败");
                }
                Person person = createPersonResult.getData();
                User newUser = newUserInfo.getData().getUser();
                PersonUserRel personUserRel = new PersonUserRel();
                personUserRel.setPersonId(person.getId());
                personUserRel.setUserId(newUser.getId());
                personUserRel.setTenantId(tenantId);
                R<PersonUserRel> createRelResult = personUserRelClient.createPersonUserRel(personUserRel);
                if (createRelResult == null || !createRelResult.isSuccess()) {
                    throw new ServiceException("绑定员工和帐号失败:" + createRelResult.getMsg());
                }
            }


            //TODO 复制告警规则
            AlarmRuleInfo alarmRuleInfo = new AlarmRuleInfo();
            alarmRuleInfo.setTenantId(tenantId);
            R copyResult = alarmRuleInfoClient.copyDefaultAlarmRule4SpecifiedTenantOrRuleId(alarmRuleInfo);
            if (copyResult == null || !copyResult.isSuccess()) {
                throw new ServiceException("复制告警规则失败");
            }

            //发送开通租户邮件
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setSubject("您的登录帐号");
            emailDTO.setReceiver(tenant.getEmail());
            emailDTO.setContent(StrUtil.format("您的登录帐号是{},登录密码是{}。", user.getAccount(), user.getPassword()));
            pushcClient.sendEmail(emailDTO);

        } else {
            //获取关联的操作员帐号信息
            R<UserInfo> adminInfoResult = userClient.getUserInfoByAccount(tenant.getAdminAccount());
            if (adminInfoResult != null && adminInfoResult.getData() != null) {
                User adminUser = adminInfoResult.getData().getUser();
                adminUser.setEmail(tenant.getEmail());
                adminUser.setPhone(tenant.getContactNumber());
                userClient.saveUser(adminUser);
            }
            result = super.saveOrUpdate(tenant);
        }
        if (result) {
            TenantCache.saveOrUpdateTenant(tenant);
        }
        return result;
    }

    @Override
    public String getTenantId(List<String> codes) {
        String code = tenantId.generate();
        if (codes.contains(code)) {
            return getTenantId(codes);
        }
        return code;
    }

    private List<Menu> getMenus(List<String> codes, LinkedList<Menu> menus) {
        codes.forEach(code -> {
            Menu menu = menuService.getOne(Wrappers.<Menu>query().lambda().eq(Menu::getCode, code).eq(Menu::getIsDeleted, BladeConstant.DB_NOT_DELETED));
            menus.add(menu);
            recursion(menu.getId(), menus);
        });
        return menus;
    }

    private void recursion(Long parentId, LinkedList<Menu> menus) {
        List<Menu> menuList = menuService.list(Wrappers.<Menu>query().lambda().eq(Menu::getParentId, parentId).eq(Menu::getIsDeleted, BladeConstant.DB_NOT_DELETED));
        menus.addAll(menuList);
        menuList.forEach(menu -> recursion(menu.getId(), menus));
    }

    /**
     * 校验租户名称是否唯一,唯一返回true,否则返回false
     *
     * @param tenantName
     * @return
     */
    @Override
    public boolean validTenantName(String tenantName) {
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<Tenant>();
        queryWrapper.eq("tenant_name", tenantName);
        List<Tenant> tenantList = baseMapper.selectList(queryWrapper);
        return CollectionUtil.isNotEmpty(tenantList);
    }

    /**
     * 修改租户状态
     *
     * @param tenantId
     * @param newStatus
     * @return
     */
    @Override
    public boolean changeTenantStatus(Long tenantId, Integer newStatus) {
        Tenant tenant = this.getById(tenantId);
        if (tenant == null) {
            throw new ServiceException("租户不存在");
        }
        tenant.setStatus(newStatus);
        if (super.updateById(tenant)) {
            TenantCache.saveOrUpdateTenant(tenant);
            return true;
        } else
            return false;
    }

    /**
     * 获取所有租户数据
     *
     * @return
     */
    @Override
    public List<Tenant> getAllTenant() {
        return baseMapper.selectList(new QueryWrapper<Tenant>());
    }

    /**
     * 根据租户主键获取管理角色信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public Role getTenantAdminRole(String tenantId) {
        Tenant tenant = getByTenantId(tenantId);
        String adminAccount = tenant.getAdminAccount();
        if (StringUtils.isBlank(adminAccount)) {
            throw new ServiceException("该租户没有设置管理员帐号");
        }
        User user = UserCache.getUserByAcct(adminAccount);
        if (user == null) {
            throw new ServiceException("该租户的管理员帐号已注销");
        }
//        String roleIds = user.getRoleId();
//        if (StringUtils.isBlank(roleIds)) {
//            throw new ServiceException("该租户的管理员帐号没有与任何角色绑定");
//        }
        List<Role> roleList = RoleCache.getRoleByTenant(tenantId);
        if (CollUtil.isEmpty(roleList)) {
            throw new ServiceException("该租户的管理员帐号没有与任何角色绑定");
        }
        List<Role> adminRoleList = roleList.stream().filter(role -> role.getRoleAlias().equalsIgnoreCase(ADMINISTRATOR) || role.getRoleAlias().equalsIgnoreCase(ADMIN)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(adminRoleList)){
            return adminRoleList.get(0);
        }
        return null;
    }

}
