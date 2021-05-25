package com.ai.apac.smartenv.workarea.cache;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.ai.apac.smartenv.workarea.feign.IWorkareaClient;
import com.ai.apac.smartenv.workarea.feign.IWorkareaRelClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.*;
import static com.ai.apac.smartenv.common.cache.CacheNames.ALL_DEVICE_CODE_MAP;

public class WorkareaCache {

    private static BladeRedisCache bladeRedisCache = null;

    private static IWorkareaClient workareaClient = null;

    private static IWorkareaRelClient workareaRelClient = null;


    private static RedisConnectionFactory redisConnectionFactory;

    private static RedisTemplate redisTemplate;

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    public static IWorkareaClient getWorkareaClient() {
        if (workareaClient == null) {
            workareaClient = SpringUtil.getBean(IWorkareaClient.class);
        }
        return workareaClient;
    }


    public static RedisConnectionFactory getRedisConnectionFactory(){
        if (redisConnectionFactory == null) {
            redisConnectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);
        }
        return redisConnectionFactory;
    }

    public static RedisTemplate getRedisTemplate(){
        if (redisTemplate == null) {
            redisTemplate = SpringUtil.getBean("redisTemplate");
        }
        return redisTemplate;
    }


    /**
     * 按租户加载设备的数据
     */
    public static void reload(String tenantId) {

        R<List<WorkareaInfo>> result = getWorkareaClient().getWorkareaInfoByTenantId(tenantId);
        if (result != null && result.getData() != null) {
            List<WorkareaInfo> workareaInfos = result.getData();
            String workAreaIdCache = WORKAREA_ID_MAP + StringPool.COLON + tenantId;
            SmartCache.clear(workAreaIdCache);
            workareaInfos.stream().forEach(workareaInfo -> {
                SmartCache.hset(workAreaIdCache, workareaInfo.getId(), workareaInfo);
            });
        }

    }

    /**
     * 根据租户ID和主键查询工作区域/线路信息
     *
     * @param tenantId
     * @param workareaId
     * @return
     */
    public static WorkareaInfo getWorkareaById(String tenantId, Long workareaId) {
        if (StringUtil.isBlank(tenantId)) {
            tenantId = AuthUtil.getTenantId();
        }
        String cacheName = WORKAREA_ID_MAP + StringPool.COLON + tenantId;
        return SmartCache.hget(cacheName, workareaId, () -> {
            R<WorkareaInfo> result = getWorkareaClient().getWorkInfoById(workareaId);
            return result.getData();
        });
    }

    public static void putAsyncEntity(Long entityId, String entityType) {
    	if (entityId == null || StringUtil.isBlank(entityType)) {
    		return;
    	}
    	String cacheName = CacheNames.WORKAREA_REL_ASYNC_MAP + StringPool.COLON + entityType;
    	SmartCache.hset(cacheName, entityId, "Y", 300);
    }
    
    public static String getAsyncEntity(Long entityId, String entityType) {
    	if (entityId == null || StringUtil.isBlank(entityType)) {
    		return null;
    	}
    	String cacheName = CacheNames.WORKAREA_REL_ASYNC_MAP + StringPool.COLON + entityType;
    	String asyncResult = SmartCache.hget(cacheName, entityId);
    	return asyncResult;
    }
    
    public static void deleteAsyncEntity(Long entityId, String entityType) {
    	if (entityId == null || StringUtil.isBlank(entityType)) {
    		return;
    	}
    	String cacheName = CacheNames.WORKAREA_REL_ASYNC_MAP + StringPool.COLON + entityType;
    	SmartCache.hdel(cacheName, entityId);
    }

    /**
     * 根据项目获取机动车
     * @param projectCode
     * @param roadLevel
     * @return
     */
    public Double getTotalPlanMotorwayArea(String projectCode,Integer roadLevel){
        return null;
    }
}
