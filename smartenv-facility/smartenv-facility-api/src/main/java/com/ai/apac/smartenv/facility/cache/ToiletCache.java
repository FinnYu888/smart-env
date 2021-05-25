package com.ai.apac.smartenv.facility.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.facility.feign.IToiletClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description 公厕相关缓存
 * @Date 2020/10/29 7:49 下午
 **/
public class ToiletCache {

    private static IToiletClient toiletClient;

    private static BladeRedis bladeRedis = null;

    public static IToiletClient getToiletClient() {
        if (toiletClient == null) {
            toiletClient = SpringUtil.getBean(IToiletClient.class);
        }
        return toiletClient;
    }

    public static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 获取租户所有未删除的公厕数量
     */
    public static Integer getToiletCount(String tenantId) {
        Integer count = SmartCache.hget(TOILET_COUNT_MAP, tenantId, () -> {
            R<Integer> result = getToiletClient().countAllToilet(tenantId);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return 0;
        });
        return count == null ? 0 : count;
    }

    /**
     * 删除租户所有未删除的公厕数量
     */
    public static void delToiletCountToday(String tenantId) {
        SmartCache.hdel(TOILET_COUNT_MAP, tenantId);
    }
}
