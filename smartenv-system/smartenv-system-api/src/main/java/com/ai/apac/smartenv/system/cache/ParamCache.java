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

import com.ai.apac.smartenv.system.entity.Param;
import com.ai.apac.smartenv.system.feign.ISysClient;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import static org.springblade.core.cache.constant.CacheConstant.PARAM_CACHE;

/**
 * 参数缓存工具类
 *
 * @author Chill
 */
public class ParamCache {

    private static final String PARAM_ID = "param:id:";
    private static final String PARAM_VALUE = "param:value:";

    private static ISysClient sysClient;

    private static BladeRedis bladeRedis;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    private static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 获取参数实体
     *
     * @param id 主键
     * @return Param
     */
    public static Param getById(Long id) {
        return CacheUtil.get(PARAM_CACHE, PARAM_ID, id, () -> {
            R<Param> result = getSysClient().getParam(id);
            return result.getData();
        });
    }

    /**
     * 获取参数配置
     *
     * @param paramKey 参数值
     * @return String
     */
    public static String getValue(String paramKey) {
        return CacheUtil.get(PARAM_CACHE, PARAM_VALUE, paramKey, () -> {
            R<String> result = getSysClient().getParamValue(paramKey);
            return result.getData();
        });
    }

    /**
     * 根据Key从缓存中删除配置
     *
     * @param paramKey
     */
    public static void deleteParamByKey(String paramKey) {
        String key = PARAM_CACHE + StringPool.COLON + StringPool.COLON + PARAM_VALUE + paramKey;
        getBladeRedis().del(key);
    }

    /**
     * 根据Key从缓存中删除配置
     *
     * @param id
     */
    public static void deleteParamById(Long id) {
        String key = PARAM_CACHE + StringPool.COLON + StringPool.COLON + PARAM_ID + id;
        getBladeRedis().del(key);
    }
}
