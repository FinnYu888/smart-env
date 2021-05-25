package com.ai.apac.smartenv.alarm.cache;

import com.ai.apac.smartenv.alarm.entity.AlarmRuleInfo;
import com.ai.apac.smartenv.alarm.feign.IAlarmRuleInfoClient;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.ALARM_RULE_INFO_MAP;

public class AlarmRuleInfoCache {
    private static IAlarmRuleInfoClient alarmRuleInfoClient = null;

    private static BladeRedisCache bladeRedisCache = null;

    private static IOssClient ossClient = null;

    public static IAlarmRuleInfoClient getAlarmRuleInfoClient() {
        if (alarmRuleInfoClient == null) {
            alarmRuleInfoClient = SpringUtil.getBean(IAlarmRuleInfoClient.class);
        }
        return alarmRuleInfoClient;
    }

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
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
        R<List<AlarmRuleInfo>> result = getAlarmRuleInfoClient().listAlarmRuleInfoByTenant(tenantId);
        if (result != null && result.getData() != null) {
            List<AlarmRuleInfo> alarmRuleInfoList = result.getData();
            String cacheName = ALARM_RULE_INFO_MAP + StringPool.COLON + tenantId;
            alarmRuleInfoList.stream().forEach(alarmRuleInfo -> {
                SmartCache.hset(cacheName, alarmRuleInfo.getId(), alarmRuleInfo);
            });
        }
    }

    /**
     * 根据告警规则ID获取告警规则信息
     *
     * @param tenantId
     * @param alarmRuleInfoId
     * @return
     */
    public static AlarmRuleInfo getAlarmRuleInfoById(String tenantId, Long alarmRuleInfoId) {
        if (StringUtil.isBlank(tenantId)) {
            tenantId = getDefaultTenantId();
        }
        String cacheName = ALARM_RULE_INFO_MAP + StringPool.COLON + tenantId;
        AlarmRuleInfo alarmRuleInfo = SmartCache.hget(cacheName, alarmRuleInfoId);
        if (alarmRuleInfo == null) {
            alarmRuleInfo = getAlarmRuleInfoClient().getAlarmRuleInfoById(alarmRuleInfoId).getData();
            saveOrUpdateAlarm(alarmRuleInfo);
        }
        return alarmRuleInfo;
    }

    /**
     * 更新内存中数据
     *
     * @param alarmRuleInfo
     */
    public static void saveOrUpdateAlarm(AlarmRuleInfo alarmRuleInfo) {
        if (alarmRuleInfo == null || alarmRuleInfo.getId() == null) {
            return;
        }
        if (StringUtil.isBlank(alarmRuleInfo.getTenantId())) {
            alarmRuleInfo.setTenantId(getDefaultTenantId());
        }
        String tenantId = alarmRuleInfo.getTenantId();
        String cacheName = ALARM_RULE_INFO_MAP + StringPool.COLON + tenantId;
        SmartCache.hset(cacheName, alarmRuleInfo.getId(), alarmRuleInfo);
    }

    /**
     * 从内存中删除某条记录
     *
     * @param tenantId
     * @param alarmRuleInfoId
     */
    public static void delAlarmRuleInfo(String tenantId, Long alarmRuleInfoId) {
        if (alarmRuleInfoId == null) {
            return;
        }
        if (StringUtil.isBlank(tenantId)) {
            tenantId = getDefaultTenantId();
        }
        String cacheName = ALARM_RULE_INFO_MAP + StringPool.COLON + tenantId;
        SmartCache.hdel(cacheName, alarmRuleInfoId);
    }

    private static String getDefaultTenantId() {
        String tenantId = TenantConstant.DEFAULT_TENANT_ID;
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            tenantId = user.getTenantId();
        }
        return tenantId;
    }
}
