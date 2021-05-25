package com.ai.apac.smartenv.arrange.cache;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.ArrangeConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.smartenv.cache.util.SmartCache;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.ai.apac.smartenv.common.cache.CacheNames.WORKING_PERSON_COUNT_TODAY;
import static com.ai.apac.smartenv.common.cache.CacheNames.WORKING_VEHICLE_COUNT_TODAY;

public class ScheduleCache {

    private static IScheduleClient scheduleClient = null;

    private static BladeRedis bladeRedisCache = null;

    private static RedisConnectionFactory redisConnectionFactory;

    private static RedisTemplate redisTemplate;

    public static IScheduleClient getScheduleClient() {
        if (scheduleClient == null) {
            scheduleClient = SpringUtil.getBean(IScheduleClient.class);
        }
        return scheduleClient;
    }

    public static BladeRedis getBladeRedisCache() {
        if (bladeRedisCache == null) {
            bladeRedisCache = SpringUtil.getBean(BladeRedis.class);
        }
        return bladeRedisCache;
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
    public static void reload() {
        R<List<Schedule>> result = getScheduleClient().listAllSchedule();
        if (result != null && result.getData() != null) {
            List<Schedule> scheduleList = result.getData();
            String cacheName = CacheNames.SCHEDULE_MAP;
            scheduleList.stream().forEach(schedule -> {
                SmartCache.hset(cacheName, schedule.getId(), schedule);
            });
        }
    }

	/*public static void reloadScheduleObject() {
		LocalDate date = LocalDate.now();
		R<List<ScheduleObject>> result = getScheduleClient().listAllScheduleObjectByDate(date);
		if (result != null && result.getData() != null) {
			List<ScheduleObject> scheduleObjectList = result.getData();
			scheduleObjectList.stream().forEach(scheduleObject -> {
				String cacheName = CacheNames.SCHEDULE_OBJECT_MAP + StringPool.COLON + scheduleObject.getEntityType()
						+ StringPool.COLON + scheduleObject.getScheduleDate().toString();
				SmartCache.hset(cacheName, scheduleObject.getId(), scheduleObject);
			});
		}
	}*/

    /**
     * 获取班次信息
     *
     * @param scheduleId
     * @return
     */
    public static Schedule getScheduleById(Long scheduleId) {
        if (scheduleId == null) {
            return null;
        }
        if (scheduleId.equals(ArrangeConstant.BREAK_SCHEDULE_ID)) {
            Schedule schedule = new Schedule();
            schedule.setId(ArrangeConstant.BREAK_SCHEDULE_ID);
            schedule.setScheduleName(ArrangeConstant.BREAK_SCHEDULE_NAME);
            return schedule;
        }
        String cacheName = CacheNames.SCHEDULE_MAP;
        Schedule schedule = SmartCache.hget(cacheName, String.valueOf(scheduleId));
        if (schedule == null) {
            schedule = getScheduleClient().getScheduleById(scheduleId).getData();
            saveOrUpdateSchedule(schedule);
        }
        return schedule;
    }

    public static List<ScheduleObject> getScheduleObjectByEntityAndDate(Long entityId, String entityType, LocalDate scheduleDate) {
        if (entityId == null || StringUtil.isBlank(entityType) || scheduleDate == null) {
            return null;
        }
        String cacheName = CacheNames.SCHEDULE_OBJECT_MAP + StringPool.COLON + entityType + StringPool.COLON
                + scheduleDate.toString();
        List<ScheduleObject> scheduleObjectList = null;
        try {
            scheduleObjectList = SmartCache.hget(cacheName, String.valueOf(entityId));
        } catch (Exception e) {
            scheduleObjectList = null;
        }
        if (CollUtil.isEmpty(scheduleObjectList)) {
            scheduleObjectList = getScheduleClient().getScheduleObjectByEntityAndDate(entityId, entityType, scheduleDate.toString()).getData();
            if (scheduleObjectList == null || scheduleObjectList.isEmpty()) {
                // 空对象放到缓存，防止穿透
                ScheduleObject scheduleObject = new ScheduleObject();
                scheduleObject.setEntityId(entityId);
                scheduleObject.setEntityType(entityType);
                scheduleObject.setScheduleDate(scheduleDate);
                scheduleObjectList.add(scheduleObject);
            }
            saveOrUpdateScheduleObject(scheduleObjectList);
        }
        // 不过滤空对象，360里需要空对象
        if (scheduleObjectList != null) {
            Iterator<ScheduleObject> iterator = scheduleObjectList.iterator();
            while (iterator.hasNext()) {
                ScheduleObject next = iterator.next();
                if (next.getId() == null || next.getId().equals(-1L)) {
                    iterator.remove();
                }
            }
        }
        return scheduleObjectList;
    }

    /**
     * 更新内存中数据
     *
     * @param schedule
     */
    public static void saveOrUpdateSchedule(Schedule schedule) {
        if (schedule == null || schedule.getId() == null || StringUtil.isBlank(schedule.getScheduleName())) {
            return;
        }
        if (StringUtil.isBlank(schedule.getTenantId())) {
            schedule.setTenantId(AuthUtil.getTenantId());
        }
        String cacheName = CacheNames.SCHEDULE_MAP;
        SmartCache.hset(cacheName, schedule.getId(), schedule);
    }

    public static void saveOrUpdateScheduleObject(List<ScheduleObject> scheduleObjectList) {
        if (scheduleObjectList == null || scheduleObjectList.isEmpty()) {
            return;
        }
        Long entityId = scheduleObjectList.get(0).getEntityId();
        String entityType = scheduleObjectList.get(0).getEntityType();
        String scheduleDate = scheduleObjectList.get(0).getScheduleDate().toString();
        String cacheName = CacheNames.SCHEDULE_OBJECT_MAP + StringPool.COLON + entityType + StringPool.COLON + scheduleDate;
        if (scheduleObjectList.get(0).getId() == null) {
            // 防止缓存穿透
            SmartCache.hset(cacheName, entityId, new ArrayList<>(), 300);
        } else {
            SmartCache.hset(cacheName, entityId, scheduleObjectList);
        }
    }

    /**
     * 从内存中删除某条记录
     *
     * @param scheduleId
     */
    public static void delSchedule(Long scheduleId) {
        if (scheduleId == null) {
            return;
        }
        String cacheName = CacheNames.SCHEDULE_MAP;
        SmartCache.hdel(cacheName, scheduleId);
    }

    public static void delScheduleObject(Long entityId, String entityType, LocalDate scheduleDate) {
        if (entityId == null || StringUtil.isBlank(entityType) || scheduleDate == null) {
            return;
        }
        String cacheName = CacheNames.SCHEDULE_OBJECT_MAP + StringPool.COLON + entityType + StringPool.COLON
                + scheduleDate.toString();
        SmartCache.hdel(cacheName, entityId);
    }

    public static void putAsyncEntity(Long entityId, String entityType) {
        if (entityId == null || StringUtil.isBlank(entityType)) {
            return;
        }
        String cacheName = CacheNames.SCHEDULE_OBJECT_ASYNC_MAP + StringPool.COLON + entityType;
        SmartCache.hset(cacheName, entityId, "Y", 300);
    }

    public static String getAsyncEntity(Long entityId, String entityType) {
        if (entityId == null || StringUtil.isBlank(entityType)) {
            return null;
        }
        String cacheName = CacheNames.SCHEDULE_OBJECT_ASYNC_MAP + StringPool.COLON + entityType;
        String asyncResult = SmartCache.hget(cacheName, entityId);
        return asyncResult;
    }

    public static void deleteAsyncEntity(Long entityId, String entityType) {
        if (entityId == null || StringUtil.isBlank(entityType)) {
            return;
        }
        String cacheName = CacheNames.SCHEDULE_OBJECT_ASYNC_MAP + StringPool.COLON + entityType;
        SmartCache.hdel(cacheName, entityId);
    }

    /**
     * 获取今天应出勤车辆数量
     *
     * @param tenantId
     * @return
     */
    public static Integer getWorkingCountForVehicleToday(String tenantId) {
        Integer count = SmartCache.hget(WORKING_VEHICLE_COUNT_TODAY, tenantId, () -> {
            R<Integer> result = getScheduleClient().countVehicleForToday(tenantId);
            if (result.isSuccess() && result.getData() != null) {
                SmartCache.hset(WORKING_VEHICLE_COUNT_TODAY,tenantId,result.getData(),TimeUtil.getTomorrowZeroSeconds());
                return result.getData();
            }
            return 0;
        });
        return count == null ? 0 : count;
    }

    /**
     * 删除今天应出勤车辆数量缓存
     * @param tenantId
     */
    public static void delWorkingCountForVehicleToday(String tenantId) {
        SmartCache.hdel(WORKING_VEHICLE_COUNT_TODAY, tenantId);
    }

    /**
     * 获取今天应出勤人员数量
     *
     * @param tenantId
     * @return
     */
    public static Integer getWorkingCountForPersonToday(String tenantId) {
        Integer count = SmartCache.hget(WORKING_PERSON_COUNT_TODAY, tenantId, () -> {
            R<Integer> result = getScheduleClient().countPersonForToday(tenantId);
            if (result.isSuccess() && result.getData() != null) {
                SmartCache.hset(WORKING_PERSON_COUNT_TODAY,tenantId,result.getData(),TimeUtil.getTomorrowZeroSeconds());
                return result.getData();
            }
            return 0;
        });
        return count == null ? 0 : count;
    }


    /**
     * 删除今天应出勤人员数量缓存
     * @param tenantId
     */
    public static void delWorkingCountForPersonToday(String tenantId){
        SmartCache.hdel(WORKING_PERSON_COUNT_TODAY, tenantId);
    }
}
