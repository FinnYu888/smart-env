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
import com.ai.apac.smartenv.system.entity.DictBiz;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.IDictBizClient;
import com.ai.apac.smartenv.system.vo.DictBizVO;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;

/**
 * 业务字典缓存工具类
 *
 * @author Chill
 */
public class DictBizCache {

    private static final String DICT_ID = "dictBiz:id:";
    private static final String DICT_VALUE = "dictBiz:value:";
    private static final String DICT_LIST = "dictBiz:list:";

    private static IDictBizClient dictClient;

    private static IDictBizClient getDictClient() {
        if (dictClient == null) {
            dictClient = SpringUtil.getBean(IDictBizClient.class);
        }
        return dictClient;
    }

    /**
     * 加载所有数据到缓存
     */
    public static void reload() {
        //先获取所有租户数据
        List<Tenant> allTenant = TenantCache.getAllTenant();
        //按租户分组获取业务字典数据
        allTenant.stream().forEach(tenant -> {
            reload(tenant.getTenantId());
        });
    }

    /**
     * 按租户加载数据到缓存
     *
     * @param tenantId
     */
    public static void reload(String tenantId) {
        R<List<DictBiz>> result = getDictClient().getTenantDict(tenantId);
        if (result != null && result.getData() != null) {
            List<DictBiz> dictList = result.getData();
            SmartCache.clear(CacheNames.BIZ_DICT_MAP + ":" + tenantId);
            Map<String, HashMap<String, String>> bizDictMap = new HashMap<String, HashMap<String, String>>();
            dictList.stream().forEach(dict -> {
                if (bizDictMap.get(dict.getCode()) != null) {
                    HashMap<String, String> dictKeyMap = bizDictMap.get(dict.getCode());
                    dictKeyMap.put(dict.getDictKey(), dict.getDictValue());
                    bizDictMap.put(dict.getCode(), dictKeyMap);
                } else {
                    HashMap<String, String> dictKeyMap = new HashMap<String, String>();
                    dictKeyMap.put(dict.getDictKey(), dict.getDictValue());
                    bizDictMap.put(dict.getCode(), dictKeyMap);
                }
            });
            String cacheName = CacheNames.BIZ_DICT_MAP + StringPool.COLON + tenantId;
            if (bizDictMap.size() > 0) {
                bizDictMap.forEach((k, v) -> {
                    SmartCache.hset(cacheName, k, v);
                });
            }
        }
    }

    /**
     * 获取字典实体
     *
     * @param id 主键
     * @return
     */
    public static DictBiz getById(Long id) {
        return CacheUtil.get(DICT_CACHE, DICT_ID, id, () -> {
            R<DictBiz> result = getDictClient().getById(id);
            return result.getData();
        });
    }

    /**
     * 获取字典值
     *
     * @param tenantId 租户编号
     * @param code     字典编号
     * @param dictKey  Integer型字典键
     * @return
     */
    public static String getValue(String tenantId, String code, Integer dictKey) {
        return getValue(tenantId,code,String.valueOf(dictKey));
    }

    /**
     * 获取字典值
     *
     * @param tenantId 租户编号
     * @param code     字典编号
     * @param dictKey  Integer型字典键
     * @return
     */
    public static String getValue(String tenantId, String code, String dictKey) {
        String cacheName = CacheNames.BIZ_DICT_MAP + StringPool.COLON + tenantId;
        HashMap<String, String> dictMap = SmartCache.hget(cacheName, code);
        return dictMap.get(String.valueOf(dictKey));
    }

    /**
     * 获取字典值
     *
     * @param code    字典编号
     * @param dictKey Integer型字典键
     * @return
     */
    public static String getValue(String code, Integer dictKey) {
        return CacheUtil.get(DICT_CACHE, DICT_VALUE + code + StringPool.COLON, String.valueOf(dictKey), () -> {
            R<String> result = getDictClient().getValue(code, String.valueOf(dictKey));
            return result.getData();
        });
    }

    /**
     * 获取字典值
     *
     * @param code    字典编号
     * @param dictKey String型字典键
     * @return
     */
    public static String getValue(String code, String dictKey) {
        return CacheUtil.get(DICT_CACHE, DICT_VALUE + code + StringPool.COLON, dictKey, () -> {
            R<String> result = getDictClient().getValue(code, dictKey);
            return result.getData();
        });
    }

    /**
     * 获取字典集合
     *
     * @param code 字典编号
     * @return
     */
    public static List<DictBiz> getList(String code) {
        return CacheUtil.get(DICT_CACHE, DICT_LIST, code, () -> {
            R<List<DictBiz>> result = getDictClient().getList(code);
            return result.getData();
        });
    }

    /**
     * 获取字典集合
     * @param tenantId 租户
     * @param code 字典编号
     * @return
     */
    public static List<DictBiz> getList(String tenantId, String code) {
        String cacheName = CacheNames.BIZ_DICT_MAP + StringPool.COLON + tenantId;
        HashMap<String, String> dictBizMap = SmartCache.hget(cacheName, code);
        if (dictBizMap != null) {
            return dictBizMap.entrySet().stream().filter(e -> !e.getKey().equals("-1"))
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .map(e -> {
                        DictBiz dictBiz = new DictBiz();
                        dictBiz.setDictKey(e.getKey());
                        dictBiz.setDictValue(e.getValue());
                        return dictBiz;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

}
