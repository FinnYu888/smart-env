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

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.utils.OkhttpUtil;
import com.ai.apac.smartenv.system.entity.Dict;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.springblade.core.cache.constant.CacheConstant.DICT_CACHE;

/**
 * 字典缓存工具类
 *
 * @author Chill
 */
public class DictCache {

    private static final String DICT_ID = "dict:id:";
    private static final String DICT_VALUE = "dict:value:";
    private static final String DICT_LIST = "dict:list:";
    private static final String THIRD_TOKEN = "third:token:";

    private static IDictClient dictClient;

    private static IDictClient getDictClient() {
        if (dictClient == null) {
            dictClient = SpringUtil.getBean(IDictClient.class);
        }
        return dictClient;
    }

    public static void reload() {
        R<List<Dict>> result = getDictClient().getAllDict();
        if (result != null && result.getData() != null) {
            List<Dict> allDict = result.getData();
            HashMap<String, HashMap<String, String>> dictMap = new HashMap<String, HashMap<String, String>>();
            allDict.stream().forEach(dict -> {
                if (dictMap.get(dict.getCode()) != null) {
                    HashMap<String, String> dictKeyMap = dictMap.get(dict.getCode());
                    dictKeyMap.put(dict.getDictKey(), dict.getDictValue());
                    dictMap.put(dict.getCode(), dictKeyMap);
                } else {
                    HashMap<String, String> dictKeyMap = new HashMap<String, String>();
                    dictKeyMap.put(dict.getDictKey(), dict.getDictValue());
                    dictMap.put(dict.getCode(), dictKeyMap);
                }
            });
            if (dictMap.size() > 0) {
                dictMap.forEach((k, v) -> {
                    SmartCache.hset(CacheNames.DICT_MAP, k, v);
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
    public static Dict getById(Long id) {
        return CacheUtil.get(DICT_CACHE, DICT_ID, id, () -> {
            R<Dict> result = getDictClient().getById(id);
            return result.getData();
        });
    }

    /**
     * 获取字典值
     *
     * @param code    字典编号
     * @param dictKey Integer型字典键
     * @return
     */
    public static String getValue(String code, Integer dictKey) {
        if (dictKey == null || dictKey == -1) {
            return "";
        }
        HashMap<String, String> dictMap = SmartCache.hget(CacheNames.DICT_MAP, code);
        if (dictMap != null) {
            String value = dictMap.get(String.valueOf(dictKey));
            return value;
        } else {
            return CacheUtil.get(DICT_CACHE, DICT_VALUE + code + StringPool.COLON, String.valueOf(dictKey), () -> {
                R<String> result = getDictClient().getValue(code, String.valueOf(dictKey));
                return result.getData();
            });
        }
    }

    /**
     * 获取字典值
     *
     * @param code    字典编号
     * @param dictKey String型字典键
     * @return
     */
    public static String getValue(String code, String dictKey) {
        if (StringUtils.isBlank(dictKey) || dictKey.equals("-1")) {
            return "";
        }
        HashMap<String, String> dictMap = SmartCache.hget(CacheNames.DICT_MAP, code);
        if (dictMap != null) {
            String value = dictMap.get(dictKey);
            return value;
        } else {
            return CacheUtil.get(DICT_CACHE, DICT_VALUE + code + StringPool.COLON, dictKey, () -> {
                R<String> result = getDictClient().getValue(code, dictKey);
                return result.getData();
            });
        }
    }

    /**
     * TODO 直接插数据库
     * @param code
     * @return
     */
    public static List<Dict> listDictByCode(String code) {
        R<List<Dict>> result = getDictClient().getList(code);
        return result.getData();
    }

    /**
     * 获取字典集合
     *
     * @param code 字典编号
     * @return
     */
    public static List<Dict> getList(String code) {
        return CacheUtil.get(DICT_CACHE, DICT_LIST, code, () -> {
            R<List<Dict>> result = getDictClient().getList(code);
            return result.getData();
        });
    }


}
