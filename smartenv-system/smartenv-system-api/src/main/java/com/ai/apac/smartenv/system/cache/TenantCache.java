package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.TENANT_MAP;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/23 12:33 下午
 **/
public class TenantCache {

    private static ISysClient sysClient;

    private static ISysClient getSysClient() {
        if (sysClient == null) {
            sysClient = SpringUtil.getBean(ISysClient.class);
        }
        return sysClient;
    }

    /**
     * 加载所有租户数据到缓存中
     */
    public static void reload() {
        //删除key
        SmartCache.clear(TENANT_MAP);
        R<List<Tenant>> result = getSysClient().getAllTenant();
        if (result != null && result.getData() != null) {
            List<Tenant> tenantList = result.getData();
            tenantList.stream().forEach(tenant -> {
                SmartCache.hset(TENANT_MAP, tenant.getTenantId(), tenant);
            });
        }
    }

    /**
     * 根据租户ID获取租户信息
     *
     * @param tenantId
     * @return
     */
    public static Tenant getTenantById(String tenantId) {
        return SmartCache.hget(TENANT_MAP, tenantId, () -> {
            R<Tenant> result = getSysClient().getTenant(tenantId);
            return result.getData();
        });
    }

    /**
     * 根据租户ID获取租户名称
     * @param tenantId
     * @return
     */
    public static String getTenantName(String tenantId){
        Tenant tenant = getTenantById(tenantId);
        if(tenant != null){
            return tenant.getTenantName();
        }
        return null;
    }

    /**
     * 更新或新增数据
     * @param tenant
     */
    public static void saveOrUpdateTenant(Tenant tenant) {
        if (tenant != null) {
            SmartCache.hset(TENANT_MAP, tenant.getTenantId(), tenant);
        }
    }

    /**
     * 从缓存中删除数据
     * @param tenantId
     */
    public static void deleteTenant(String tenantId){
        SmartCache.hdel(TENANT_MAP,tenantId);
    }

    /**
     * 获取所有租户信息
     * @return
     */
    public static List<Tenant> getAllTenant(){
        List<Tenant> tenantList = SmartCache.getHVals(TENANT_MAP);
        return tenantList;
    }
}
