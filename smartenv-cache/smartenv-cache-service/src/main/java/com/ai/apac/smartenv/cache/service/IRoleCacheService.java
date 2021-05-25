package com.ai.apac.smartenv.cache.service;

import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.common.cache.IBaseCache;

import java.util.List;

/**
 * @author qianlong
 * @Description 角色管理缓存服务
 * @Date 2020/2/21 10:01 上午
 **/
public interface IRoleCacheService extends IBaseCache {

    /**
     * 根据角色ID集合获取菜单
     *
     * @param roleIds
     * @return
     */
    List<Menu> getMenuByRole(String roleIds);

    /**
     * 根据主键查询角色名称
     * @param roleId
     * @return
     */
    String getRoleNameById(String roleId);

    /**
     * 根据主键集合查询角色名称集合
     * @param roleIds
     * @return
     */
    String getRoleNamesByIds(String roleIds);
}
