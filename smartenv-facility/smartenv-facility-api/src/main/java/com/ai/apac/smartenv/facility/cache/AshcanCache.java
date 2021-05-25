package com.ai.apac.smartenv.facility.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.facility.dto.AshcanImportResultModel;
import com.ai.apac.smartenv.facility.entity.AshcanInfo;
import com.ai.apac.smartenv.facility.feign.IAshcanClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

public class AshcanCache {

    private static IAshcanClient ashcanClient = null;

    private static BladeRedis bladeRedis = null;

    public static IAshcanClient getAshcanClient() {
        if (ashcanClient == null) {
            ashcanClient = SpringUtil.getBean(IAshcanClient.class);
        }
        return ashcanClient;
    }

    public static BladeRedis getBladeRedisCache() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    /**
     * 根据人员ID获取垃圾桶信息
     *
     * @param ashcanId
     * @return
     */
    public static AshcanInfo getAshcanById(Long ashcanId) {
        String cacheName = ASHCAN_MAP;
        AshcanInfo ashcan = SmartCache.hget(cacheName, String.valueOf(ashcanId));
        if (ashcan == null) {
            ashcan = getAshcanClient().getAshcan(ashcanId).getData();
            saveOrUpdateAshcan(ashcan);
        }
        return ashcan;
    }

    /**
     * 更新内存中数据
     *
     * @param ashcan
     */
    public static void saveOrUpdateAshcan(AshcanInfo ashcan) {
        if (ashcan == null || ashcan.getId() == null || StringUtil.isBlank(ashcan.getAshcanCode())) {
            return;
        }
        if (StringUtil.isBlank(ashcan.getTenantId())) {
            ashcan.setTenantId(getDefaultTenantId());
        }
        String cacheName = ASHCAN_MAP;
        SmartCache.hset(cacheName, ashcan.getId(), ashcan);
    }

    /**
     * 从内存中删除某条记录
     *
     * @param ashcanId
     */
    public static void delAshcan(Long ashcanId) {
        if (ashcanId == null) {
            return;
        }
        String cacheName = ASHCAN_MAP;
        SmartCache.hdel(cacheName, ashcanId);
    }

    private static String getDefaultTenantId() {
        String tenantId = TenantConstant.DEFAULT_TENANT_ID;
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            tenantId = user.getTenantId();
        }
        return tenantId;
    }

    public static String getAshcanQrCodeById(Long ashcanId) {
        String cacheName = ASHCAN_QR_CODE_MAP;
        return SmartCache.hget(cacheName, String.valueOf(ashcanId));
    }

    public static void saveAshcanQrCode(Long ashcanId, String base64) {
        String cacheName = ASHCAN_QR_CODE_MAP;
        SmartCache.hset(cacheName, ashcanId, base64, 3600);
    }

    public static void saveImportRecords(List<AshcanImportResultModel> allRecords) {
        String cacheName = CacheNames.ASHCAN_IMPORT + ":" + DateUtil.now().getTime();
        SmartCache.hset(cacheName, DateUtil.now().getTime(), allRecords, 3600);
    }

    public static Object getImportRecords(String key) {
        String cacheName = CacheNames.ASHCAN_IMPORT + ":" + key;
        Object object = SmartCache.hget(cacheName, DateUtil.now().getTime());
        return object;
    }

    /**
     * 获取租户所有未删除的垃圾桶数量
     */
    public static Integer getAshcanCount(String tenantId) {
        Integer count = SmartCache.hget(ASHCAN_COUNT_MAP, tenantId, () -> {
            R<Integer> result = getAshcanClient().countAshcanInfo(tenantId);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return 0;
        });
        return count == null ? 0 : count;
    }

    /**
     * 删除租户所有未删除的垃圾桶数量
     */
    public static void delAshcanCount(String tenantId) {
        SmartCache.hdel(ASHCAN_COUNT_MAP, tenantId);
    }
}
