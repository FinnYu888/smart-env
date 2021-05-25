package com.ai.apac.smartenv.device.cache;

import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.device.feign.IDeviceRelClient;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.smartenv.cache.util.SmartCache;
import jodd.util.StringUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/17 9:29 下午
 **/
public class DeviceRelCache {

    private static IDeviceClient deviceClient = null;
    private static IDeviceRelClient deviceRelClient = null;
    private static BladeRedisCache bladeRedisCache = null;
    private static BladeRedis bladeRedis;

//    private static

    public static IDeviceClient getDeviceClient() {
        if (deviceClient == null) {
            deviceClient = SpringUtil.getBean(IDeviceClient.class);
        }
        return deviceClient;
    }

    public static IDeviceRelClient getDeviceRelClient() {
        if (deviceRelClient == null) {
            deviceRelClient = SpringUtil.getBean(IDeviceRelClient.class);
        }
        return deviceRelClient;
    }

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    /**
     * 重新加载所有设备与实体的关联关系
     */
    public static void reload() {
        List<Tenant> tenantList = TenantCache.getAllTenant();
        tenantList.stream().forEach(tenant -> {
            reload(tenant.getTenantId());
        });
    }

    /**
     * 按租户加载设备与实体的关联关系
     *
     * @param tenantId
     */
    public static void reload(String tenantId) {
        String cacheName = DEVICE_REL_TENANT_MAP + StringPool.COLON + tenantId;
        SmartCache.clear(cacheName);
        R<List<DeviceRel>> relResult = getDeviceRelClient().getTenantDeviceRel(tenantId);
        if (relResult != null && relResult.getData() != null) {
            List<DeviceRel> deviceRelList = relResult.getData();
            Map<Long, List<DeviceRel>> entityRelMap = deviceRelList.stream()
                    .collect(Collectors.groupingByConcurrent(DeviceRel::getEntityId));
            entityRelMap.keySet().stream().forEach(entityId -> {
                List<DeviceRel> relList = entityRelMap.get(entityId);
                SmartCache.hset(cacheName, entityId, relList);
            });
        }
    }

//    /**
//     * 按业务实体重新加载
//     *
//     * @param entityId
//     */
//    public static void reloadByEntityId(Long entityId) {
//        R<List<DeviceRel>> relResult = getDeviceRelClient().getEntityRels(entityId);
//        if (relResult != null && relResult.getData() != null) {
//            List<DeviceRel> deviceRelList = relResult.getData();
//            if (deviceRelList.size() > 0) {
//                String tenantId = deviceRelList.get(0).getTenantId();
//                String cacheName = DEVICE_REL_TENANT_MAP + StringPool.COLON + tenantId;
//                Map<Long, List<DeviceRel>> entityRelMap = getBladeRedisCache().get(cacheName);
//                if (entityRelMap == null) {
//                    entityRelMap = new HashMap<Long, List<DeviceRel>>();
//                }
//                entityRelMap.put(entityId, deviceRelList);
//                getBladeRedisCache().set(cacheName, entityRelMap);
//            }
//        }
//    }

    /**
     * 根据实体ID,实体类型获取绑定关系
     *
     * @param entityId
     * @param tenantId
     */
    public static List<DeviceRel> getDeviceRel(Long entityId, String tenantId) {
        if (entityId == null) {
            return null;
        }
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        String cacheName = DEVICE_REL_TENANT_MAP + StringPool.COLON + tenantId;
        return SmartCache.hget(cacheName, entityId, () -> {
            R<List<DeviceRel>> result = getDeviceRelClient().getEntityRels(entityId);
            return result.getData();
        });
    }

    /**
     * 删除实体关联关系
     *
     * @param entityId
     * @param tenantId
     */
    public static void deleteDeviceRel(Long entityId, String tenantId) {
        String cacheName = DEVICE_REL_TENANT_MAP + StringPool.COLON + tenantId;
        SmartCache.hdel(cacheName, entityId);
    }

    /**
     * 根据设备ID获取绑定关系
     *
     * @param deviceId
     */
    public static DeviceRel getDeviceRel(Long deviceId) {
        if (deviceId == null) {
            return null;
        }
        return SmartCache.hget(DEVICE_REL_MAP, deviceId, () -> {
            R<DeviceRel> result = getDeviceRelClient().getDeviceRelByDeviceId(deviceId);
            return result.getData();
        });
    }
}
