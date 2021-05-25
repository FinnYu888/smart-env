package com.ai.apac.smartenv.cache.service.impl;

import com.ai.apac.smartenv.cache.service.IRoleCacheService;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.system.entity.Menu;
import com.ai.apac.smartenv.system.entity.Role;
import com.ai.apac.smartenv.system.feign.ISysClient;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/21 10:10 上午
 **/
@Service
public class RoleCacheServiceImpl implements IRoleCacheService {

    @Autowired
    private ISysClient sysClient;

    @Autowired
    private BladeRedisCache bladeRedisCache;

    @Override
    public void reload(String tenantId) {
        R<List<Role>> result = sysClient.getTenantRole(tenantId);
        if (result.isSuccess() && result.getData() != null) {
            //先删除缓存key,重新加载
            List<Role> roleList = result.getData();
            roleList.stream().forEach(role ->{
                bladeRedisCache.hSet(CacheNames.ROLE_MAP,role.getId(),role);
                bladeRedisCache.hSet(CacheNames.ROLE_NAME_MAP, role.getId(), role.getRoleName());
            });
        }
    }

    /**
     * 根据角色ID集合获取菜单
     *
     * @param roleIds
     * @return
     */
    @Override
    public List<Menu> getMenuByRole(String roleIds) {
        return null;
    }

    /**
     * 根据主键查询角色名称
     *
     * @param roleId
     * @return
     */
    @Override
    public String getRoleNameById(String roleId) {
        return null;
    }

    /**
     * 根据主键集合查询角色名称集合
     *
     * @param roleIds
     * @return
     */
    @Override
    public String getRoleNamesByIds(String roleIds) {
        return null;
    }

    /**
     * 加载所有数据到缓存中
     */
    @Override
    public void reloadAll() {

    }
}
