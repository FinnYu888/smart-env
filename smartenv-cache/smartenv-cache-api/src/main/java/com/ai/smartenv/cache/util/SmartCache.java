package com.ai.smartenv.cache.util;

import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/23 7:56 上午
 **/
public class SmartCache {

    private static BladeRedis bladeRedisCache = SpringUtil.getBean(BladeRedis.class);

    /**
     * HashMap设置
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public static void hset(String cacheName, Object key, Object value) {
        bladeRedisCache.hSet(cacheName, key, value);
    }

    /**
     * HashMap设置
     *
     * @param cacheName
     * @param key
     * @param value
     * @param seconds
     */
    public static void hset(String cacheName, Object key, Object value, long seconds) {
        bladeRedisCache.hSet(cacheName, key, value);
        bladeRedisCache.expire(cacheName, seconds);
    }

    /**
     * 根据缓存key名称,获取hashmap集合值
     *
     * @param cacheName
     * @return
     */
    public static List getHVals(String cacheName) {
        return bladeRedisCache.hVals(cacheName);
    }

    /**
     * 从HashMap获取值
     *
     * @param cacheName
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T hget(String cacheName, Object key) {
        if (Func.hasEmpty(cacheName, key)) {
            return null;
        }
        return bladeRedisCache.hGet(cacheName, key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName   缓存名
     * @param key         缓存键值
     * @param valueLoader 重载对象
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T hget(String cacheName, Object key, Callable<T> valueLoader) {
        if (Func.hasEmpty(cacheName, key)) {
            return null;
        }
        try {
            T objectValue = hget(cacheName, key);
            if (objectValue == null) {
                T call = valueLoader.call();
                if (Func.isNotEmpty(call)) {
                    bladeRedisCache.hSet(cacheName, key, call);
                    objectValue = call;
                }
            } else {
                return objectValue;
            }
            return objectValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取缓存,同时设置缓存key的失效时间
     *
     * @param cacheName   缓存名
     * @param key         缓存键值
     * @param valueLoader 重载对象
     * @param seconds     超时时间,单位是秒
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T hget(String cacheName, Object key, Callable<T> valueLoader, long seconds) {
        if (Func.hasEmpty(cacheName, key)) {
            return null;
        }
        try {
            T objectValue = hget(cacheName, key);
            if (objectValue == null) {
                T call = valueLoader.call();
                if (Func.isNotEmpty(call)) {
                    bladeRedisCache.hSet(cacheName, key, call);
                    bladeRedisCache.setEx(cacheName, key, seconds);
                    objectValue = call;
                }
            } else {
                return objectValue;
            }
            return objectValue;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 清除缓存
     *
     * @param cacheName
     * @param key
     */
    public static void hdel(String cacheName, Object key) {
        bladeRedisCache.hDel(cacheName, key);
    }

    /**
     * 清除缓存
     *
     * @param cacheName
     */
    public static void clear(String... cacheName) {
        bladeRedisCache.del(cacheName);
    }
}
