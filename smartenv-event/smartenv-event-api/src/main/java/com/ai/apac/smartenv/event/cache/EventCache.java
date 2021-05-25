package com.ai.apac.smartenv.event.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;

public class EventCache {

    private static IEventInfoClient eventInfoClient = null;


    private static BladeRedis bladeRedis = null;

    private static RedisConnectionFactory redisConnectionFactory;

    private static RedisTemplate redisTemplate;

    public static IEventInfoClient getEventInfoClient() {
        if (eventInfoClient == null) {
            eventInfoClient = SpringUtil.getBean(IEventInfoClient.class);
        }
        return eventInfoClient;
    }

    public static BladeRedis getBladeRedis() {
        if (bladeRedis == null) {
            bladeRedis = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedis;
    }

    public static RedisConnectionFactory getRedisConnectionFactory() {
        if (redisConnectionFactory == null) {
            redisConnectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);
        }
        return redisConnectionFactory;
    }

    public static RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = SpringUtil.getBean("redisTemplate");
        }
        return redisTemplate;
    }

    /**
     * 加载所有数据到缓存
     */
    public static void reloadEventKpiCatalog() {
        R<List<EventKpiCatalog>> result = getEventInfoClient().listAllEventKpiCatalog();
        if (result != null && result.getData() != null) {
            List<EventKpiCatalog> eventKpiCatalogs = result.getData();
            String cacheName = CacheNames.EVENT_KPI_CATALOG_MAP;
            eventKpiCatalogs.stream().forEach(eventkpiCatalog -> {
                SmartCache.hset(cacheName, eventkpiCatalog.getId(), eventkpiCatalog);
            });
        }
    }

    public static void reloadEventKpiDef() {
        R<List<EventKpiDef>> result = getEventInfoClient().listAllEventKpiDef();
        if (result != null && result.getData() != null) {
            List<EventKpiDef> eventKpiDefs = result.getData();
            String cacheName = CacheNames.EVENT_KPI_CATALOG_MAP;
            eventKpiDefs.stream().forEach(eventKpiDef -> {
                SmartCache.hset(cacheName, eventKpiDef.getId(), eventKpiDef);
            });
        }
    }

    public static void reloadEventKpiRel() {
        R<List<EventInfoKpiRel>> result = getEventInfoClient().listAllEventInfoKpiRel();
        if (result != null && result.getData() != null) {
            List<EventInfoKpiRel> eventInfoKpiRelList = result.getData();
            String cacheName = CacheNames.EVENT_KPI_REL_MAP;
            Map<Long, List<EventInfoKpiRel>> collect = eventInfoKpiRelList.stream().collect(Collectors.groupingBy(EventInfoKpiRel::getEventInfoId));
            collect.entrySet().forEach(eventRel -> {
                Long key = eventRel.getKey();
                List<EventInfoKpiRel> value = eventRel.getValue();
                SmartCache.hset(cacheName, key, value);
            });
//            eventInfoKpiRelList.stream().forEach(eventInfoKpiRel -> {
//                SmartCache.hset(cacheName, eventInfoKpiRel.getEventInfoId(), eventInfoKpiRel);
//            });
        }
    }


    public static List<EventInfoKpiRel> getEventInfoRelByEventId(Long eventInfoId) {
        if (eventInfoId == null) {
            return null;
        }
        String cacheName = CacheNames.EVENT_KPI_REL_MAP;
        List<EventInfoKpiRel> eventInfoKpiRels = SmartCache.hget(cacheName, String.valueOf(eventInfoId));
        if (eventInfoKpiRels == null) {
            List<EventInfoKpiRel> data = getEventInfoClient().listAllEventInfoKpiRelByEventId(eventInfoId).getData();
            SmartCache.hset(cacheName, eventInfoId, data);
        }

        return eventInfoKpiRels;
    }


    /**
     * 获取考核指标信息
     * * @return
     */
    public static EventKpiCatalog getEventKpiCatalogById(Long eventkpiCatalogId) {
        if (eventkpiCatalogId == null) {
            return null;
        }
        String cacheName = CacheNames.EVENT_KPI_CATALOG_MAP;
        EventKpiCatalog eventKpiCatalog = SmartCache.hget(cacheName, String.valueOf(eventkpiCatalogId));
        if (eventKpiCatalog == null) {
            eventKpiCatalog = getEventInfoClient().getEventKpiCatalogById(eventkpiCatalogId).getData();
            saveOrUpdateEventKpiCatalog(eventKpiCatalog);
        }

        return eventKpiCatalog;
    }

    public static EventKpiDef getEventKpiDefById(Long eventkpiDefId) {
        if (eventkpiDefId == null) {
            return null;
        }
        String cacheName = CacheNames.EVENT_KPI_DEF_MAP;
        EventKpiDef eventKpiDef = SmartCache.hget(cacheName, String.valueOf(eventkpiDefId));
        if (eventKpiDef == null) {
            eventKpiDef = getEventInfoClient().getEventKpiDefById(eventkpiDefId).getData();
            saveOrUpdateEventKpiDef(eventKpiDef);
        }
        return eventKpiDef;
    }

    /**
     * 更新内存中数据
     **/
    public static void saveOrUpdateEventKpiCatalog(EventKpiCatalog eventKpiCatalog) {
        if (eventKpiCatalog == null || eventKpiCatalog.getId() == null || StringUtil.isBlank(eventKpiCatalog.getCatalogName())) {
            return;
        }
        if (StringUtil.isBlank(eventKpiCatalog.getTenantId())) {
            eventKpiCatalog.setTenantId(getDefaultTenantId());
        }
        String cacheName = CacheNames.EVENT_KPI_CATALOG_MAP;
        SmartCache.hset(cacheName, eventKpiCatalog.getId(), eventKpiCatalog);
    }

    public static void saveOrUpdateEventKpiDef(EventKpiDef eventKpiDef) {
        if (eventKpiDef == null || eventKpiDef.getId() == null || StringUtil.isBlank(eventKpiDef.getEventKpiName())) {
            return;
        }
        if (StringUtil.isBlank(eventKpiDef.getTenantId())) {
            eventKpiDef.setTenantId(getDefaultTenantId());
        }
        String cacheName = CacheNames.EVENT_KPI_DEF_MAP;
        SmartCache.hset(cacheName, eventKpiDef.getId(), eventKpiDef);
    }

    /**
     * 从内存中删除某条记录
     */
    public static void delEventKpiCatalog(Long eventkpiCatalogId) {
        if (eventkpiCatalogId == null) {
            return;
        }
        String cacheName = CacheNames.EVENT_KPI_CATALOG_MAP;
        SmartCache.hdel(cacheName, eventkpiCatalogId);
    }

    public static void delEventKpiDef(Long eventkpiDefId) {
        if (eventkpiDefId == null) {
            return;
        }
        String cacheName = CacheNames.EVENT_KPI_DEF_MAP;
        SmartCache.hdel(cacheName, eventkpiDefId);
    }

    public static void delAllEventKpiDef() {
        String cacheName = CacheNames.EVENT_KPI_DEF_MAP;
        SmartCache.clear(cacheName);
    }

    public static void delEventKpiTplDef(Long eventkpiTplDefId) {
        if (eventkpiTplDefId == null) {
            return;
        }
        String cacheName = CacheNames.EVENT_KPI_TPL_DEF_MAP;
        SmartCache.hdel(cacheName, eventkpiTplDefId);
    }

    public static void delAllEventKpiTplDef() {
        String cacheName = CacheNames.EVENT_KPI_TPL_DEF_MAP;
        SmartCache.clear(cacheName);
    }


    private static String getDefaultTenantId() {
        String tenantId = TenantConstant.DEFAULT_TENANT_ID;
        BladeUser user = AuthUtil.getUser();
        if (user != null) {
            tenantId = user.getTenantId();
        }
        return tenantId;
    }

    /**
     * 获取今日事件总数
     *
     * @param tenantId
     * @return
     */
    public static Integer getEventCountToday(String tenantId) {
        Integer count = SmartCache.hget(EVENT_COUNT_MAP, tenantId, () -> {
            return getEventInfoClient().countEventDaily(tenantId).getData();
        });
        return count == null ? 0 : count;
    }

    /**
     * 删除今日事件总数
     */
    public static void delEventCountToday(String tenantId) {
        SmartCache.hdel(EVENT_COUNT_MAP, tenantId);
    }
}
