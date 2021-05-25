package com.ai.apac.smartenv.inventory.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.inventory.feign.IResSpecClient;
import com.ai.apac.smartenv.inventory.feign.IResTypeClient;
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.smartenv.cache.util.SmartCache;
import org.apache.commons.collections4.CollectionUtils;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.List;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.INVENTORY_RES_SPEC;
import static com.ai.apac.smartenv.common.cache.CacheNames.INVENTORY_RES_TYPE_RES_SPEC_NAME;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: InventoryCache
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/7/17
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/7/17     zhaidx           v1.0.0               修改原因
 */
public class InventoryCache {

    private static IResTypeClient resTypeClient = null;
    private static IResSpecClient resSpecClient = null;

    private static BladeRedisCache bladeRedisCache = null;

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    private static IResTypeClient getResTypeClient() {
        if (resTypeClient == null) {
            resTypeClient = SpringUtil.getBean(IResTypeClient.class);
        }
        return resTypeClient;
    }

    private static IResSpecClient getResSpecClient() {
        if (resSpecClient == null) {
            resSpecClient = SpringUtil.getBean(IResSpecClient.class);
        }
        return resSpecClient;
    }

    /**
     * 加载所有用户数据到缓存
     */
    public static void reload() {
        SmartCache.clear(CacheNames.INVENTORY_MAP);
        //先获取租户数据
        List<Tenant> allTenant = TenantCache.getAllTenant();
        allTenant.stream().forEach(tenant -> {
            reloadResTypeResSpecNameStrings(tenant.getTenantId());
            relaodResSpecByTenantId(tenant.getTenantId());
        });
    }
    
    /**
     * 加载物资类型/物资规格名字组合数据到缓存
     *
     * @param tenantId
     */
    public static void reloadResTypeResSpecNameStrings(String tenantId) {
        List<String> typeSpecNameIdStrings = getResTypeClient().listResTypeResSpecNameIdStrings(tenantId).getData();
        if (CollectionUtils.isNotEmpty(typeSpecNameIdStrings)) {
            List<String> typeSpecNames = typeSpecNameIdStrings.stream().map(str -> str.split(StringPool.COLON)[0]).collect(Collectors.toList());
            SmartCache.hset(INVENTORY_RES_TYPE_RES_SPEC_NAME, tenantId, typeSpecNames);
            
            String cacheName = INVENTORY_RES_TYPE_RES_SPEC_NAME + StringPool.COLON + tenantId;
            typeSpecNameIdStrings.forEach(str -> {
                String[] nameIdArray = str.split(StringPool.COLON);
                SmartCache.hset(cacheName, nameIdArray[0], nameIdArray[1]);
            });
        }
    }

    /**
     * 根据租户ID加载物资规格
     * @param tenantId
     */
    public static void relaodResSpecByTenantId(String tenantId) {
        List<ResSpecVO> resSpecVOS = getResSpecClient().listSpecByTenant(tenantId).getData();
        if (CollectionUtils.isNotEmpty(resSpecVOS)) {
            String cacheName = INVENTORY_RES_SPEC + StringPool.COLON + tenantId;
            resSpecVOS.forEach(resSpecVO -> {
                SmartCache.hset(cacheName, resSpecVO.getId(), resSpecVO);
            });
        }
    }

    /**
     * 查租户下所有物资类型/物资规格名称组合
     * @param tenantId
     * @return
     */
    public static List<String> listTypeSpecNamesByTenant(String tenantId) {
        return SmartCache.hget(INVENTORY_RES_TYPE_RES_SPEC_NAME, tenantId);
    }
    
    /**
     * 根据物资类型/物资规格名称组合查物资规格Id
     * @param tenantId
     * @param typeSpecName
     * @return
     */
    public static String getSpecIdByTenantAndName(String tenantId, String typeSpecName) {
        String cacheName = INVENTORY_RES_TYPE_RES_SPEC_NAME + StringPool.COLON + tenantId;
        return SmartCache.hget(cacheName, typeSpecName);
    }
}
