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
package com.ai.apac.smartenv.system.user.cache;

import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.system.user.feign.IUserClient;
import com.ai.apac.smartenv.system.user.vo.UserVO;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.USER_ACCT_MAP;
import static com.ai.apac.smartenv.common.cache.CacheNames.USER_ID_MAP;
import static org.springblade.core.cache.constant.CacheConstant.USER_CACHE;
import static org.springblade.core.launch.constant.FlowConstant.TASK_USR_PREFIX;

/**
 * 系统缓存
 *
 * @author Chill
 */
public class UserCache {
    private static final String USER_CACHE_ID = "user:id:";

    private static IUserClient userClient;

    private static BladeRedisCache bladeRedisCache;

    private static IUserClient getUserClient() {
        if (userClient == null) {
            userClient = SpringUtil.getBean(IUserClient.class);
        }
        return userClient;
    }

    private static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    /**
     * 加载所有用户数据到缓存中
     */
    public static void reload() {
        //删除key
        getBladeRedisCache().del(USER_ID_MAP, USER_ACCT_MAP);
        R<List<User>> result = getUserClient().getAllUser();
        reloadData(result);
    }

    /**
     * 加载指定租户用户数据到缓存中
     */
    public static void reload(String tenantId) {
        R<List<User>> result = getUserClient().getTenantUser(tenantId);
        reloadData(result);
    }

    private static void reloadData(R<List<User>> result) {
        if (result.isSuccess() && result.getData() != null) {
            List<User> userList = result.getData();
            userList.stream().forEach(user -> {
                getBladeRedisCache().hSet(USER_ID_MAP, user.getId(), user);
                getBladeRedisCache().hSet(USER_ACCT_MAP, user.getAccount(), user);
            });
        }
    }

    /**
     * 根据任务用户id获取用户信息
     *
     * @param taskUserId 任务用户id
     * @return
     */
    public static User getUserByTaskUser(String taskUserId) {
        Long userId = Func.toLong(StringUtil.removePrefix(taskUserId, TASK_USR_PREFIX));
        return getUser(userId);
    }

    /**
     * 获取用户名
     *
     * @param userId 用户id
     * @return
     */
    public static User getUser(Long userId) {
        return SmartCache.hget(USER_ID_MAP, userId, () -> {
            R<User> result = getUserClient().userInfoById(userId);
            return result.getData();
        });
//        return CacheUtil.get(USER_CACHE, USER_CACHE_ID, userId, () -> {
//            R<User> result = getUserClient().userInfoById(userId);
//            return result.getData();
//        });
    }

    /**
     * 根据登录帐号获取用户信息
     *
     * @param account 用户帐号
     * @return
     */
    public static User getUserByAcct(String account) {
        return SmartCache.hget(USER_ACCT_MAP, account, () -> {
            R<User> result = getUserClient().userByAcct(account);
            return result.getData();
        });
    }

    /**
     * 根据帐号获取用户名称
     * @param account
     * @return
     */
    public static String getUserName(String account){
        User user = getUserByAcct(account);
        if(user != null){
            return user.getName();
        }
        return null;
    }

    /**
     * 新增或更新用户
     *
     * @param user
     */
    public static void saveOrUpdateUser(User user) {
        if (user != null) {
            SmartCache.hset(USER_ID_MAP, user.getId(), user);
            SmartCache.hset(USER_ACCT_MAP, user.getAccount(), user);
        }
    }

    /**
     * 批量更新用户数据
     *
     * @param userList
     */
    public static void batchSaveOrUpdateUser(List<User> userList) {
        if (userList != null && userList.size() > 0) {
            userList.stream().forEach(user -> {
                saveOrUpdateUser(user);
            });
        }
    }
}
