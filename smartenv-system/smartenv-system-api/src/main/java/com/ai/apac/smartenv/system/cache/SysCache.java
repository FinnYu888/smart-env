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
package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.ai.apac.smartenv.system.entity.*;
import com.ai.apac.smartenv.system.feign.ISysClient;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springblade.core.cache.constant.CacheConstant.SYS_CACHE;

/**
 * 系统缓存
 *
 * @author Chill
 */
public class SysCache {
    private static final String MENU_ID = "menu:id:";
    private static final String DEPT_ID = "dept:id:";
    private static final String DEPT_NAME_ID = "deptName:id:";
    private static final String DEPT_NAMES_ID = "deptNames:id:";
    private static final String DEPT_FULL_NAMES_ID = "deptFullNames:id:";
    private static final String DEPT_CHILD_ID = "deptChild:id:";
    private static final String DEPT_CHILDIDS_ID = "deptChildIds:id:";
    private static final String ROLE_ID = "role:id:";
    private static final String ROLE_NAME_ID = "roleName:id:";
    private static final String ROLE_NAMES_ID = "roleNames:id:";
    private static final String ROLE_ALIAS_ID = "roleAlias:id:";
    private static final String ROLE_ALIASES_ID = "roleAliases:id:";
    private static final String TENANT_ID = "tenant:id:";
    private static final String TENANT_TENANT_ID = "tenant:tenantId:";
    private static final String REGION_ID = "region:id:";

    private static ISysClient sysClient;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    /**
     * 获取菜单
     *
     * @param id 主键
     * @return
     */
//    public static Menu getMenu(Long id) {
//        return CacheUtil.get(SYS_CACHE, MENU_ID, id, () -> {
//            R<Menu> result = getSysClient().getMenu(id);
//            return result.getData();
//        });
//    }

    /**
     * 获取部门
     *
     * @param id 主键
     * @return
     */
//    public static Dept getDept(Long id) {
//        return CacheUtil.get(SYS_CACHE, DEPT_ID, id, () -> {
//            R<Dept> result = getSysClient().getDept(id);
//            return result.getData();
//        });
//    }

    /**
     * 获取部门名
     *
     * @param id 主键
     * @return 部门名
     */
//    public static String getDeptName(Long id) {
//        return CacheUtil.get(SYS_CACHE, DEPT_NAME_ID, id, () -> {
//            R<String> result = getSysClient().getDeptName(id);
//            return result.getData();
//        });
//    }

    /**
     * 获取角色
     *
     * @param id 主键
     * @return Role
     */
//    public static Role getRole(Long id) {
//        return CacheUtil.get(SYS_CACHE, ROLE_ID, id, () -> {
//            R<Role> result = getSysClient().getRole(id);
//            return result.getData();
//        });
//    }

    /**
     * 获取角色名
     *
     * @param id 主键
     * @return 角色名
     */
//    public static String getRoleName(Long id) {
//        return CacheUtil.get(SYS_CACHE, ROLE_NAME_ID, id, () -> {
//            R<String> result = getSysClient().getRoleName(id);
//            return result.getData();
//        });
//    }

    /**
     * 获取角色别名
     *
     * @param id 主键
     * @return 角色别名
     */
//    public static String getRoleAlias(Long id) {
//        return CacheUtil.get(SYS_CACHE, ROLE_ALIAS_ID, id, () -> {
//            R<String> result = getSysClient().getRoleAlias(id);
//            return result.getData();
//        });
//    }


    /**
     * 获取部门名集合
     *
     * @param deptIds 主键集合
     * @return 部门名
     */
//    public static List<String> getDeptNames(String deptIds) {
//        return CacheUtil.get(SYS_CACHE, DEPT_NAMES_ID, deptIds, () -> {
//            R<List<String>> result = getSysClient().getDeptNames(deptIds);
//            return result.getData();
//        });
//    }

    /**
     * 获取部门全名集合
     *
     * @param deptIds 主键集合
     * @return 部门名
     */
//    public static List<String> getDeptFullNames(String deptIds) {
//        return CacheUtil.get(SYS_CACHE, DEPT_FULL_NAMES_ID, deptIds, () -> {
//            R<List<String>> result = getSysClient().getDeptFullNames(deptIds);
//            return result.getData();
//        });
//    }

    /**
     * 获取子部门集合
     *
     * @param deptId 主键
     * @return 子部门
     */
    public static List<Dept> getDeptChild(Long deptId) {
        return CacheUtil.get(SYS_CACHE, DEPT_CHILD_ID, deptId, () -> {
            R<List<Dept>> result = getSysClient().getDeptChild(deptId);
            return result.getData();
        });
    }

    /**
     * 获取子部门ID集合
     *
     * @param deptId 主键
     * @return 子部门ID
     */
    public static List<Long> getDeptChildIds(Long deptId) {
        if (deptId == null) {
            return null;
        }
        List<Long> deptIdList = CacheUtil.get(SYS_CACHE, DEPT_CHILDIDS_ID, deptId, List.class);
        if (deptIdList == null) {
            deptIdList = new ArrayList<>();
            List<Dept> deptChild = getDeptChild(deptId);
            if (deptChild != null) {
                List<Long> collect = deptChild.stream().map(Dept::getId).collect(Collectors.toList());
                deptIdList.addAll(collect);
            }
            deptIdList.add(deptId);
            CacheUtil.put(SYS_CACHE, DEPT_CHILDIDS_ID, deptId, deptIdList);
        }
        return deptIdList;
    }

    /**
     * 获取角色名集合
     *
     * @param roleIds 主键集合
     * @return 角色名
     */
    public static List<String> getRoleNames(String roleIds) {
        return CacheUtil.get(SYS_CACHE, ROLE_NAMES_ID, roleIds, () -> {
            R<List<String>> result = getSysClient().getRoleNames(roleIds);
            return result.getData();
        });
    }

    /**
     * 获取角色别名集合
     *
     * @param roleIds 主键集合
     * @return 角色别名
     */
    public static List<String> getRoleAliases(String roleIds) {
        return CacheUtil.get(SYS_CACHE, ROLE_ALIASES_ID, roleIds, () -> {
            R<List<String>> result = getSysClient().getRoleAliases(roleIds);
            return result.getData();
        });
    }

    /**
     * 获取租户
     *
     * @param id 主键
     * @return Tenant
     */
//    public static Tenant getTenant(Long id) {
//        return CacheUtil.get(SYS_CACHE, TENANT_ID, id, () -> {
//            R<Tenant> result = getSysClient().getTenant(id);
//            return result.getData();
//        });
//    }

    /**
     * 获取租户
     *
     * @param tenantId 租户id
     * @return Tenant
     */
//    public static Tenant getTenant(String tenantId) {
//        return CacheUtil.get(SYS_CACHE, TENANT_TENANT_ID, tenantId, () -> {
//            R<Tenant> result = getSysClient().getTenant(tenantId);
//            return result.getData();
//        });
//    }

    /**
     * 获取区域
     *
     * @param regionId
     * @return
     */
//    public static Region getRegion(Long regionId) {
//        return CacheUtil.get(SYS_CACHE, REGION_ID, regionId, () -> {
//            R<Region> result = getSysClient().getRegion(regionId);
//            return result.getData();
//        });
//    }
}
