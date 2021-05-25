package com.ai.apac.smartenv.device.cache;

import com.ai.apac.smartenv.common.constant.DeviceConstant;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.device.feign.IDeviceClient;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/3 3:45 下午
 **/
public class DeviceCache {

    private static IDeviceClient deviceClient = null;

    private static BladeRedis bladeRedis = null;

    public static IDeviceClient getDeviceClient() {
        if (deviceClient == null) {
            deviceClient = SpringUtil.getBean(IDeviceClient.class);
        }
        return deviceClient;
    }

    public static BladeRedis getBladeRedisCache() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 加载所有设备的数据
     */
    public static void reload() {
        //获取所有租户
        List<Tenant> allTenant = TenantCache.getAllTenant();
        allTenant.stream().forEach(tenant -> {
            reload(tenant.getTenantId());
        });
    }

    /**
     * 按租户加载设备的数据
     */
    public static void reload(String tenantId) {
        R<List<DeviceInfo>> result = getDeviceClient().getTenantDevice(tenantId);
        if (result != null && result.getData() != null) {
            List<DeviceInfo> deviceInfos = result.getData();
            String deviceIdCache = DEVICE_ID_MAP + StringPool.COLON + tenantId;
            String deviceCodeCache = DEVICE_CODE_MAP + StringPool.COLON + tenantId;
            SmartCache.clear(deviceIdCache, deviceCodeCache);
            deviceInfos.stream().forEach(deviceInfo -> {
                SmartCache.hset(deviceIdCache, deviceInfo.getId(), deviceInfo);
                SmartCache.hset(deviceCodeCache, deviceInfo.getDeviceCode(), deviceInfo);
                SmartCache.hset(ALL_DEVICE_ID_MAP, deviceInfo.getId(), deviceInfo.getTenantId());
                SmartCache.hset(ALL_DEVICE_CODE_MAP, deviceInfo.getDeviceCode(), deviceInfo);
            });
        }
    }

    /**
     * 根据租户ID和主键查询设备信息
     *
     * @param tenantId
     * @param deviceId
     * @return
     */
    public static DeviceInfo getDeviceById(String tenantId, Long deviceId) {
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        String cacheName = DEVICE_ID_MAP + StringPool.COLON + tenantId;
        return SmartCache.hget(cacheName, deviceId, () -> {
            R<DeviceInfo> result = getDeviceClient().getDeviceById(String.valueOf(deviceId));
            return result.getData();
        });
    }

    /**
     * 根据租户ID和设备编号查询设备信息
     *
     * @param tenantId
     * @param deviceCode
     * @return
     */
    public static DeviceInfo getDeviceByCode(String tenantId, String deviceCode) {
        String cacheName = DEVICE_CODE_MAP + StringPool.COLON + tenantId;
        return SmartCache.hget(cacheName, deviceCode, () -> {
            R<List<DeviceInfo>> result = getDeviceClient().getDeviceByCode(deviceCode);
            if (result != null && result.getData() != null && result.getData().size() > 0) {
                return result.getData().get(0);
            } else {
                return null;
            }
        });
    }

    /**
     * 根据设备编号查询设备信息
     *
     * @param deviceCode
     * @return
     */
    public static DeviceInfo getDeviceByCode(String deviceCode) {
        return SmartCache.hget(ALL_DEVICE_CODE_MAP, deviceCode, () -> {
            R<List<DeviceInfo>> result = getDeviceClient().getDeviceByCode(deviceCode);
            if (result != null && result.getData() != null && result.getData().size() > 0) {
                return result.getData().get(0);
            } else {
                return null;
            }
        });
    }

    /**
     * 根据设备编号查询设备信息
     *
     * @param deviceId
     * @return
     */
    public static DeviceInfo getDeviceById(Long deviceId) {
        return SmartCache.hget(ALL_DEVICE_ID_MAP, deviceId, () -> {
            R<DeviceInfo> result = getDeviceClient().getDeviceById(String.valueOf(deviceId));
            if (result != null && result.getData() != null) {
                return result.getData();
            } else {
                return null;
            }
        });
    }

    /**
     * 新增或更新缓存中数据
     *
     * @param deviceInfo
     */
    public static void saveOrUpdateDevice(DeviceInfo deviceInfo) {
        if (deviceInfo == null || StringUtil.isBlank(deviceInfo.getTenantId())) {
            return;
        }
        String tenantId = deviceInfo.getTenantId();
        String deviceIdCache = DEVICE_ID_MAP + StringPool.COLON + tenantId;
        String deviceCodeCache = DEVICE_CODE_MAP + StringPool.COLON + tenantId;
        SmartCache.hset(deviceIdCache, deviceInfo.getId(), deviceInfo);
        SmartCache.hset(deviceCodeCache, deviceInfo.getDeviceCode(), deviceInfo);
        SmartCache.hset(ALL_DEVICE_CODE_MAP, deviceInfo.getDeviceCode(), deviceInfo);
        SmartCache.hset(ALL_DEVICE_ID_MAP, deviceInfo.getId(), deviceInfo);
    }

    /**
     * 根据租户ID、设备ID从内存中删除某条记录
     *
     * @param deviceId
     */
    public static void delDevice(String tenantId, Long deviceId) {
        if (StringUtil.isBlank(tenantId) || deviceId == null) {
            return;
        }
        DeviceInfo deviceInfo = getDeviceById(tenantId, deviceId);
        delDeviceByEntity(deviceInfo);
    }

    /**
     * 根据设备编号删除数据
     *
     * @param deviceId
     */
    public static void delDeviceById(Long deviceId) {
        DeviceInfo deviceInfo = getDeviceById(deviceId);
        delDeviceByEntity(deviceInfo);
    }

    /**
     * 根据设备编号删除数据
     *
     * @param deviceCode
     */
    public static void delDeviceByCode(String deviceCode) {
        DeviceInfo deviceInfo = getDeviceByCode(deviceCode);
        delDeviceByEntity(deviceInfo);
    }

    /**
     * 根据业务实体从缓存中删除对象
     *
     * @param deviceInfo
     */
    public static void delDeviceByEntity(DeviceInfo deviceInfo) {
        if (deviceInfo != null && deviceInfo.getId() != null) {
            SmartCache.hdel(ALL_DEVICE_ID_MAP, deviceInfo.getId());
            SmartCache.hdel(ALL_DEVICE_CODE_MAP, deviceInfo.getDeviceCode());
            String deviceIdCache = DEVICE_ID_MAP + StringPool.COLON + deviceInfo.getTenantId();
            String deviceCodeCache = DEVICE_CODE_MAP + StringPool.COLON + deviceInfo.getTenantId();
            SmartCache.hdel(deviceIdCache, deviceInfo.getId());
            SmartCache.hdel(deviceCodeCache, deviceInfo.getDeviceCode());

            //同时还需要删除设备与业务实体的关联关系
            DeviceRelCache.deleteDeviceRel(deviceInfo.getId(), deviceInfo.getTenantId());
        }
    }

    /**
     * 根据设备ID列表批量删除
     * @param deviceIds
     */
    public static void batchDelDeviceByIds(List<Long> deviceIds){

    }

    /**
     * 获取设备开启状态
     *
     * @param entityId
     * @param entityCategoryId
     * @param tenantId
     * @return
     */
    public static String getDeviceStatus(Long entityId, Long entityCategoryId, String tenantId) {
        List<DeviceRel> deviceRelList = DeviceRelCache.getDeviceRel(entityId, tenantId);
        if (deviceRelList != null && deviceRelList.size() > 0) {
            for (DeviceRel deviceRel : deviceRelList) {
                DeviceInfo deviceInfo = getDeviceById(tenantId, deviceRel.getDeviceId());
                if (deviceInfo != null && ObjectUtil.isNotEmpty(deviceInfo.getEntityCategoryId()) && deviceInfo.getEntityCategoryId().equals(entityCategoryId)) {
                    return String.valueOf(deviceInfo.getDeviceStatus());
                }
            }
        }
        return DeviceConstant.DeviceStatus.NO;
    }
}
