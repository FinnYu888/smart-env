package com.ai.apac.smartenv.facility.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description 中转站相关缓存
 * @Date 2020/10/29 7:49 下午
 **/
public class FacilityCache {

    private static BladeRedis bladeRedis = null;
    private static IFacilityClient facilityClient = null;

    public static IFacilityClient getFacilityClient() {
        if (facilityClient == null) {
            facilityClient = SpringUtil.getBean(IFacilityClient.class);
        }
        return facilityClient;
    }

    public static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 获取租户所有未删除的中转站数量
     */
    public static Integer getFacilityCount(String tenantId) {
        Integer count = SmartCache.hget(FACILITY_COUNT_MAP, tenantId, () -> {
            R<Integer> result = getFacilityClient().countAllFacility(tenantId);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return 0;
        });
        return count == null ? 0 : count;
    }

    /**
     * 删除租户所有未删除的中转站数量
     */
    public static void delFacilityCountToday(String tenantId) {
        SmartCache.hdel(FACILITY_COUNT_MAP, tenantId);
    }
}
