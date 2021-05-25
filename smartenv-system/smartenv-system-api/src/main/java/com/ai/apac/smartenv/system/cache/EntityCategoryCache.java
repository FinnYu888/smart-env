package com.ai.apac.smartenv.system.cache;

import com.ai.apac.smartenv.system.entity.EntityCategory;
import com.ai.apac.smartenv.system.feign.IEntityCategoryClient;
import com.ai.smartenv.cache.util.SmartCache;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.ENTITY_CATEGORY_MAP;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/18 5:55 下午
 **/
public class EntityCategoryCache {

    public static BladeRedisCache bladeRedisCache = null;

    public static IEntityCategoryClient entityCategoryClient = null;

    public static BladeRedisCache getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedisCache.class);
        }
        return bladeRedisCache;
    }

    public static IEntityCategoryClient getEntityCategoryClient() {
        if (entityCategoryClient == null) {
            entityCategoryClient = SpringUtil.getBean(IEntityCategoryClient.class);
        }
        return entityCategoryClient;
    }

    public static void reload() {
        SmartCache.clear(ENTITY_CATEGORY_MAP);
        R<List<EntityCategory>> result = getEntityCategoryClient().getAllCategory();
        if (result != null && result.getData() != null) {
            List<EntityCategory> list = result.getData();
            list.stream().forEach(entityCategory -> {
                SmartCache.hset(ENTITY_CATEGORY_MAP, entityCategory.getId(), entityCategory);
            });
        }
    }

    /**
     * 根据主键ID获取categoryName
     * @param id
     * @return
     */
    public static String getCategoryNameById(Long id){
        EntityCategory entityCategory = SmartCache.hget(ENTITY_CATEGORY_MAP,id,()->{
            return getEntityCategoryClient().getCategory(id).getData();
        });
        if(entityCategory != null){
            return entityCategory.getCategoryName();
        }
        return null;
    }

    /**
     * 根据主键ID获取category
     * @param id
     * @return
     */
    public static EntityCategory getCategoryById(Long id){
		EntityCategory entityCategory = SmartCache.hget(ENTITY_CATEGORY_MAP, id, () -> {
			return getEntityCategoryClient().getCategory(id).getData();
		});
		return entityCategory;
    }

}
