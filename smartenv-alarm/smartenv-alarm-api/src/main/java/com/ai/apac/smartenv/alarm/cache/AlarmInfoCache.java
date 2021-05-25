package com.ai.apac.smartenv.alarm.cache;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

/**
 * @author qianlong
 * @description 告警信息缓存
 * @Date 2020/11/2 10:46 下午
 **/
public class AlarmInfoCache {

    private static BladeRedis bladeRedis = null;

    private static IAlarmInfoClient alarmInfoClient = null;

    public static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    public static IAlarmInfoClient getAlarmInfoClient() {
        if (alarmInfoClient == null) {
            alarmInfoClient = SpringUtil.getBean(IAlarmInfoClient.class);
        }
        return alarmInfoClient;
    }

    /**
     * 获取当日未处理告警数量
     *
     * @param tenantId
     * @return
     */
    public static Integer getUnHandleAlarmCountToday(String tenantId) {
        Integer count = SmartCache.hget(UN_HANDLE_ALARM_COUNT_MAP, tenantId, () -> {
            R<Integer> result = getAlarmInfoClient().countAlarmInfoAmount(tenantId);
            if (result.isSuccess() && result.getData() == null) {
                return result.getData();
            }
            return 0;
        });
        return count == null ? 0 : count;
    }

    /**
     * 删除当日未处理告警数量
     */
    public static void delUnHandleAlarmCountToday(String tenantId) {
        SmartCache.hdel(UN_HANDLE_ALARM_COUNT_MAP, tenantId);
    }

    /**
     * 获取告警统计数据
     *
     * @param tenantId
     * @return
     */
    public static AlarmAmountVO getSummaryAlarmAmount(String tenantId) {
        AlarmAmountVO alarmAmountVO = SmartCache.hget(SUMMARY_ALARM_COUNT_MAP, tenantId, () -> {
            AlarmAmountVO result = getAlarmInfoClient().countAllRuleAlarmAmount(tenantId).getData();
            return result;
        });
        return alarmAmountVO;
    }

    /**
     * 删除当日告警数量综合统计
     */
    public static void delSummaryAlarmCount(String tenantId) {
        SmartCache.hdel(SUMMARY_ALARM_COUNT_MAP, tenantId);
    }

    /**
     * 获取业务实体当日未处理告警数量
     *
     * @param entityId
     * @param entityType
     * @return
     */
    public static Integer getUnHandledAlarmCountToday(Long entityId, Long entityType) {
        String key = ENTITY_UNHANDLED_ALARM_COUNT + StringPool.COLON + entityType + StringPool.COLON + entityId;
        Integer count = bladeRedis.get(key);
        if (count == null || count.equals(0)) {
            R<Integer> result = alarmInfoClient.countNoHandleAlarmInfoByEntity(entityId, entityType);
            if (result.isSuccess() && result.getData() != null) {
                count = result.getData();
                //设置缓存,10分钟过期
                bladeRedis.setEx(key, count, ExpirationTime.EXPIRATION_TIME_10MIN);
            }
        }
        return count;
    }

    /**
     * 设置业务实体当日未处理告警数量
     * @param entityId
     * @param entityType
     * @param alarmCount
     */
    public static void setUnHandledAlarmCountToday(Long entityId, Long entityType, Integer alarmCount) {
        String key = ENTITY_UNHANDLED_ALARM_COUNT + StringPool.COLON + entityType + StringPool.COLON + entityId;
        bladeRedis.setEx(key, alarmCount, ExpirationTime.EXPIRATION_TIME_1HOURS);
    }
}
