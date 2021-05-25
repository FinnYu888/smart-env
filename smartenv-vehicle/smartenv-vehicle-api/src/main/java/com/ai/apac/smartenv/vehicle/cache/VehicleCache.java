package com.ai.apac.smartenv.vehicle.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.enums.VehicleStatusImgEnum;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.vehicle.dto.VehicleDeviceStatusCountDTO;
import com.ai.apac.smartenv.vehicle.entity.VehicleInfo;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.smartenv.cache.util.SmartCache;

import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/26 9:35 上午
 **/
public class VehicleCache {

    private static IVehicleClient vehicleClient = null;

    private static BladeRedis bladeRedisCache = null;

    private static IOssClient ossClient = null;

    public static IVehicleClient getVehicleClient() {
        if (vehicleClient == null) {
            vehicleClient = SpringUtil.getBean(IVehicleClient.class);
        }
        return vehicleClient;
    }

    public static BladeRedis getBladeRedis() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedisCache;
    }

    public static IOssClient getOssClient() {
        if (ossClient == null) {
            ossClient = SpringUtil.getBean(IOssClient.class);
        }
        return ossClient;
    }

    public static void reload() {
        //先获取租户数据
        List<Tenant> allTenant = TenantCache.getAllTenant();
        allTenant.stream().forEach(tenant -> {
            reload(tenant.getTenantId());
        });
    }

    public static void reload(String tenantId) {
        R<List<VehicleInfo>> result = getVehicleClient().getVehicleByTenant(tenantId);
        if (result != null && result.getData() != null) {
            List<VehicleInfo> vehicleList = result.getData();
            String cacheName = VEHICLE_MAP + StringPool.COLON + tenantId;
            SmartCache.clear(cacheName, VEHICLE_TENANT_MAP);
            vehicleList.stream().forEach(vehicle -> {
                SmartCache.hset(cacheName, vehicle.getId(), vehicle);
                SmartCache.hset(VEHICLE_TENANT_MAP, vehicle.getId(), vehicle.getTenantId());
            });
        }
    }

    /**
     * 根据车辆ID获取车辆信息
     *
     * @param tenantId
     * @param vehicleId
     * @return
     */
    public static VehicleInfo getVehicleById(String tenantId, Long vehicleId) {
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        VehicleInfo vehicle = null;
        if (StringUtil.isBlank(tenantId)) {
            vehicle = getVehicleClient().vehicleInfoById(vehicleId).getData();
        } else {
            String cacheName = VEHICLE_MAP + StringPool.COLON + tenantId;
            vehicle = SmartCache.hget(cacheName, vehicleId);
            if (vehicle == null) {
                vehicle = getVehicleClient().vehicleInfoById(vehicleId).getData();
                saveOrUpdateVehicle(vehicle);
            }
        }
        return vehicle;
    }

    /**
     * 根据租户获取车辆
     *
     * @param tenantId
     * @return
     */
    public static List<VehicleInfo> getUsedVehicleByTenant(String tenantId) {
        String cacheName = VEHICLE_MAP + StringPool.COLON + tenantId;
        return SmartCache.hget(cacheName, tenantId, () -> {
            return getVehicleClient().getVehicleByTenant(tenantId).getData();
        });
    }

    /**
     * 更新内存中数据
     *
     * @param vehicle
     */
    public static void saveOrUpdateVehicle(VehicleInfo vehicle) {
        if (vehicle == null || vehicle.getId() == null || StringUtil.isBlank(vehicle.getPlateNumber())) {
            return;
        }
        if (StringUtil.isBlank(vehicle.getTenantId())) {
            vehicle.setTenantId(AuthUtil.getTenantId());
        }
        String tenantId = vehicle.getTenantId();
        String cacheName = VEHICLE_MAP + StringPool.COLON + tenantId;
        SmartCache.hset(cacheName, vehicle.getId(), vehicle);
    }

    /**
     * 从内存中删除某条记录
     *
     * @param tenantId
     * @param vehicleId
     */
    public static void delVehicle(String tenantId, Long vehicleId) {
        if (vehicleId == null) {
            return;
        }
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        String cacheName = VEHICLE_MAP + StringPool.COLON + tenantId;
        SmartCache.hdel(cacheName, vehicleId);
        delNormalVehicleCount(tenantId);
    }

    /**
     * 用于车辆监控时根据用户状态获取对应的图标地址
     *
     * @param status
     * @return
     */
    public static String getVehicleStatusImg(Integer status) {
        String cacheName = VEHICLE_STATUS_IMG + StringPool.COLON + status;
        String imgLink = getBladeRedis().get(cacheName);
        if (StringUtil.isBlank(imgLink)) {
            String imgName = VehicleStatusImgEnum.getDescByValue(status);
            R<String> shareLink = getOssClient().getObjectLink(VehicleConstant.BUCKET, imgName);
            if (shareLink != null && shareLink.getData() != null) {
                getBladeRedis().setEx(cacheName, shareLink.getData(), CacheNames.ExpirationTime.EXPIRATION_TIME_24HOURS);
                return shareLink.getData();
            }
            return null;
        } else {
            return imgLink;
        }
    }

    /**
     * 获取当前租户正常状态的车辆数量
     *
     * @param tenantId
     * @return
     */
    public static Integer getNormalVehicleCount(String tenantId) {
        Integer count = SmartCache.hget(VEHICLE_COUNT_MAP, tenantId, () -> {
            return getVehicleClient().getNormalVehicleCountByTenant(tenantId).getData();
        });
        return count;
    }

    /**
     * 删除租户在用车辆数量
     *
     * @param tenantId
     */
    public static void delNormalVehicleCount(String tenantId) {
        SmartCache.hdel(VEHICLE_COUNT_MAP, tenantId);
    }

    /**
     * 获取当前租户车辆ACC状态统计
     *
     * @param tenantId
     * @return
     */
    public static VehicleDeviceStatusCountDTO getVehicleDeviceStatusCount(String tenantId) {
        VehicleDeviceStatusCountDTO result = SmartCache.hget(VEHICLE_ACC_STATUS_MAP, tenantId, () -> {
            return getVehicleClient().getVehicleDeviceStatusStat(tenantId).getData();
        });
        return result;
    }

    /**
     * 删除当前租户车辆ACC状态统计
     *
     * @param tenantId
     * @return
     */
    public static void delVehicleDeviceStatusCount(String tenantId) {
        SmartCache.hdel(VEHICLE_ACC_STATUS_MAP, tenantId);
    }

	/*private static String getDefaultTenantId() {
	    String tenantId = TenantConstant.DEFAULT_TENANT_ID;
	    BladeUser user = AuthUtil.getUser();
	    if (user != null) {
	        tenantId = user.getTenantId();
	    }
	    return tenantId;
	}*/
}
