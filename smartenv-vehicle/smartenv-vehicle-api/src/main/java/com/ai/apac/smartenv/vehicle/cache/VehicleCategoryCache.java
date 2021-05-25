package com.ai.apac.smartenv.vehicle.cache;

import com.ai.apac.smartenv.system.entity.Station;
import com.ai.apac.smartenv.vehicle.entity.VehicleCategory;
import com.ai.apac.smartenv.vehicle.entity.VehicleWorkType;
import com.ai.apac.smartenv.vehicle.feign.IVehicleCategoryClient;
import com.ai.apac.smartenv.vehicle.vo.VehicleCategoryVO;
import com.ai.apac.smartenv.vehicle.vo.VehicleWorkTypeVO;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @description 车辆类型缓存
 * @Date 2020/3/18 5:55 下午
 **/
public class VehicleCategoryCache {

    public static BladeRedisCache bladeRedisCache = null;

    public static IVehicleCategoryClient vehicleCategoryClient = null;

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    public static IVehicleCategoryClient getVehicleCategoryClient() {
        if (vehicleCategoryClient == null) {
            vehicleCategoryClient = SpringUtil.getBean(IVehicleCategoryClient.class);
        }
        return vehicleCategoryClient;
    }

    public static void reload() {
        SmartCache.clear(VEHICLE_CATEGORY_MAP);
        List<VehicleCategoryVO> categoryVOS = getVehicleCategoryClient().getAllCategory().getData();
        if (CollectionUtil.isNotEmpty(categoryVOS)) {
            categoryVOS.forEach(vehicleCategoryVO -> {
                SmartCache.hset(VEHICLE_CATEGORY_MAP, vehicleCategoryVO.getId(), vehicleCategoryVO);
                String tenantVehicle = VEHICLE_CATEGORY_MAP + StringPool.COLON + vehicleCategoryVO.getTenantId();
                SmartCache.hset(tenantVehicle, vehicleCategoryVO.getCategoryCode(), vehicleCategoryVO);
            });
        }
    }

    /**
     * 根据主键ID获取categoryName
     *
     * @param id
     * @return
     */
    public static String getCategoryNameById(Long id) {
        VehicleCategory VehicleCategory = SmartCache.hget(VEHICLE_CATEGORY_MAP, id, () -> {
            return getVehicleCategoryClient().getCategory(id).getData();
        });
        if (VehicleCategory != null) {
            return VehicleCategory.getCategoryName();
        }
        return null;
    }


    public static String getCategoryNameByCode(String code,String tenantId) {
        String tenantVehicle = VEHICLE_CATEGORY_MAP + StringPool.COLON + tenantId;

        VehicleCategory VehicleCategory = SmartCache.hget(tenantVehicle,code, () -> {
            return getVehicleCategoryClient().getCategoryByCode(code,tenantId).getData();
        });
        if (VehicleCategory != null) {
            return VehicleCategory.getCategoryName();
        }
        return null;
    }

    /**
     * 根据主键ID获取category
     *
     * @param id
     * @return
     */
    public static VehicleCategory getCategoryById(Long id) {
        VehicleCategory VehicleCategory = SmartCache.hget(VEHICLE_CATEGORY_MAP, id, () -> {
            return getVehicleCategoryClient().getCategory(id).getData();
        });
        return VehicleCategory;
    }

    /**
     * 取租户下所有车辆类型（不区分级联关系）
     *
     * @param tenantId
     * @return
     * @author zhaidx
     */
    public static List<VehicleCategoryVO> listCategoryByTenantId(String tenantId) {
        String tenantVehicle = VEHICLE_CATEGORY_MAP + StringPool.COLON + tenantId;
        List<VehicleCategoryVO> categoryList = SmartCache.getHVals(tenantVehicle);
        if (CollectionUtil.isEmpty(categoryList)) {
            categoryList = getVehicleCategoryClient().listVehicleCategoryByTenantId(tenantId).getData();
        }
        if (CollectionUtil.isNotEmpty(categoryList) && categoryList.size() > 0 ) {
            return categoryList;
        }
        return null;
    }


    public static void reloadVehicleWorkType() {
        String key = VEHICLE_WORK_TYPE_MAP;
        List<VehicleWorkTypeVO> data = getVehicleCategoryClient().listVehicleWorkType().getData();
        if (CollectionUtil.isNotEmpty(data)) {
            data.forEach(vehicleWorkTypeVO -> {
                SmartCache.hset(key, vehicleWorkTypeVO.getVehicleCategoryCode(), vehicleWorkTypeVO);
            });
        }

    }

    public static VehicleWorkType getWorkTypeByCategoryId(String categoryCode) {
        String key = VEHICLE_WORK_TYPE_MAP;
        VehicleWorkTypeVO hget = SmartCache.hget(key, categoryCode, () -> getVehicleCategoryClient().getVehicleWorkTypeByCode(categoryCode).getData());
        return hget;
    }

    public static void saveOrUpdateVehicleCategory(VehicleCategoryVO vehicleCategoryVO) {
        SmartCache.hset(VEHICLE_CATEGORY_MAP, vehicleCategoryVO.getId(), vehicleCategoryVO);
        String tenantVehicle = VEHICLE_CATEGORY_MAP + StringPool.COLON + vehicleCategoryVO.getTenantId();
        SmartCache.hset(tenantVehicle, vehicleCategoryVO.getCategoryCode(), vehicleCategoryVO);
    }

    public static void deleteVehicleCategory(Long vehicleCategoryId) {
        VehicleCategory vehicleCategory = SmartCache.hget(VEHICLE_CATEGORY_MAP, vehicleCategoryId, () -> {
            return getVehicleCategoryClient().getCategory(vehicleCategoryId).getData();
        });
        SmartCache.hdel(VEHICLE_CATEGORY_MAP, vehicleCategoryId);
        String tenantVehicle = VEHICLE_CATEGORY_MAP + StringPool.COLON + vehicleCategory.getTenantId();
        if(ObjectUtil.isNotEmpty(vehicleCategory.getCategoryCode())){
            SmartCache.hdel(tenantVehicle, vehicleCategory.getCategoryCode());
        }
    }

}

