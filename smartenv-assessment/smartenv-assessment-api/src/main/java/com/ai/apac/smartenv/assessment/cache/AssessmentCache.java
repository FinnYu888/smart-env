package com.ai.apac.smartenv.assessment.cache;

import com.ai.apac.smartenv.assessment.entity.KpiCatalog;
import com.ai.apac.smartenv.assessment.entity.KpiDef;
import com.ai.apac.smartenv.assessment.feign.IAssessmentClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.constant.TenantConstant;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class AssessmentCache {

    private static IAssessmentClient assessmentClient = null;

    private static BladeRedisCache bladeRedisCache = null;

    private static RedisConnectionFactory redisConnectionFactory;

    private static RedisTemplate redisTemplate;

    public static IAssessmentClient getAssessmentClient() {
        if (assessmentClient == null) {
        	assessmentClient = SpringUtil.getBean(IAssessmentClient.class);
        }
        return assessmentClient;
    }

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
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
     * 加载所有数据到缓存
     */
    public static void reloadKpiCatalog() {
        R<List<KpiCatalog>> result = getAssessmentClient().listAllKpiCatalog();
        if (result != null && result.getData() != null) {
            List<KpiCatalog> kpiCatalogList = result.getData();
            String cacheName = CacheNames.KPI_CATALOG_MAP;
            kpiCatalogList.stream().forEach(kpiCatalog -> {
                SmartCache.hset(cacheName, kpiCatalog.getId(), kpiCatalog);
            });
        }
    }

    public static void reloadKpiDef() {
    	R<List<KpiDef>> result = getAssessmentClient().listAllKpiDef();
    	if (result != null && result.getData() != null) {
    		List<KpiDef> kpiDefList = result.getData();
    		String cacheName = CacheNames.KPI_CATALOG_MAP;
    		kpiDefList.stream().forEach(kpiDef -> {
    			SmartCache.hset(cacheName, kpiDef.getId(), kpiDef);
    		});
    	}
    }

    /**
     * 获取考核指标信息
     *
     * @param kpiCatalogId
     * @return
     */
    public static KpiCatalog getKpiCatalogById(Long kpiCatalogId) {
		if (kpiCatalogId == null) {
			return null;
		}
		String cacheName = CacheNames.KPI_CATALOG_MAP;
		KpiCatalog kpiCatalog = SmartCache.hget(cacheName, String.valueOf(kpiCatalogId));
		if (kpiCatalog == null) {
			kpiCatalog = getAssessmentClient().getKpiCatalogById(kpiCatalogId).getData();
			saveOrUpdateKpiCatalog(kpiCatalog);
		}

		return kpiCatalog;
    }

    public static KpiDef getKpiDefById(Long kpiDefId) {
    	if (kpiDefId == null) {
    		return null;
    	}
    	String cacheName = CacheNames.KPI_DEF_MAP;
    	KpiDef kpiDef = SmartCache.hget(cacheName, String.valueOf(kpiDefId));
    	if (kpiDef == null) {
    		kpiDef = getAssessmentClient().getKpiDefById(kpiDefId).getData();
    		saveOrUpdateKpiDef(kpiDef);
    	}
    	return kpiDef;
    }
    
    /**
     * 更新内存中数据
     *
     * @param kpiCatalog
     */
    public static void saveOrUpdateKpiCatalog(KpiCatalog kpiCatalog) {
        if (kpiCatalog == null || kpiCatalog.getId() == null || StringUtil.isBlank(kpiCatalog.getCatalogName())) {
            return;
        }
        if (StringUtil.isBlank(kpiCatalog.getTenantId())) {
        	kpiCatalog.setTenantId(getDefaultTenantId());
        }
        String cacheName = CacheNames.KPI_CATALOG_MAP;
        SmartCache.hset(cacheName, kpiCatalog.getId(), kpiCatalog);
    }

    public static void saveOrUpdateKpiDef(KpiDef kpiDef) {
    	if (kpiDef == null || kpiDef.getId() == null || StringUtil.isBlank(kpiDef.getKpiName())) {
    		return;
    	}
    	if (StringUtil.isBlank(kpiDef.getTenantId())) {
    		kpiDef.setTenantId(getDefaultTenantId());
    	}
    	String cacheName = CacheNames.KPI_DEF_MAP;
    	SmartCache.hset(cacheName, kpiDef.getId(), kpiDef);
    }

    /**
     * 从内存中删除某条记录
     *
     * @param kpiCatalogId
     */
    public static void delKpiCatalog(Long kpiCatalogId) {
        if (kpiCatalogId == null) {
            return;
        }
        String cacheName = CacheNames.KPI_CATALOG_MAP;
        SmartCache.hdel(cacheName, kpiCatalogId);
    }

    public static void delKpiDef(Long kpiDefId) {
    	if (kpiDefId == null) {
    		return;
    	}
    	String cacheName = CacheNames.KPI_DEF_MAP;
    	SmartCache.hdel(cacheName, kpiDefId);
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
