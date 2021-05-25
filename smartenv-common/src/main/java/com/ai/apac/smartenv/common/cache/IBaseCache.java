package com.ai.apac.smartenv.common.cache;

import java.io.Serializable;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/21 10:06 上午
 **/
public interface IBaseCache {

    /**
     * 根据租户加载数据到缓存中
     * @param tenantId
     */
    void reload(String tenantId);

    /**
     * 加载所有数据到缓存中
     */
    void reloadAll();
}
