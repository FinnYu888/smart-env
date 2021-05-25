package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.enums.VehicleStatusImgEnum;
import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.redis.config.BladeRedisCacheAutoConfiguration;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/16 4:29 下午
 **/
@Slf4j
@Service
public class WebSocketTaskService implements IWebSocketTaskService {

    @Autowired
    private BladeRedis bladeRedisCache;
    @Autowired
    private IOssClient ossClient;


    @Autowired
    public BladeRedis getBladeRedisCache() {
        return bladeRedisCache;
    }

    /**
     * 创建一个基本的websocket 的task
     *
     * @param websocketTask
     * @return
     */
    @Override
    public WebsocketTask createTask(WebsocketTask websocketTask) {
        Long taskId = UUID.randomUUID().getMostSignificantBits();
        websocketTask.setCreateTime(new Date());
        websocketTask.setId(taskId);
        websocketTask.setStatus(WebSocketConsts.WebsocketTaskStatus.CREATED);
        websocketTask.setIsDeleted(0);
        List<String> entityIds = websocketTask.getEntityIds();
        //smartenv:ws:eventTask:{tenantId}:{sessionId}:{taskType}:{taskId}
        String key = WebSocketConsts.CacheNames.EVENT_WEBSOCKET_TASK + StringPool.COLON + websocketTask.getTenantId() + StringPool.COLON + websocketTask.getSessionId() + StringPool.COLON + websocketTask.getTaskType() + StringPool.COLON + websocketTask.getId();
        bladeRedisCache.set(key, websocketTask);
//        for (String entityId : entityIds) {
//            key = key + StringPool.COLON + entityId;
//            bladeRedisCache.set(key, websocketTask);
//        }

        return websocketTask;
    }


    /**
     * 创建一个当前Task的子任务，任务中包含实体ID。用于推送具体Entity的时候使用（比如：车辆位置，人员位置）
     *
     * @param websocketTask
     * @param entityId
     * @return
     */
    @Override
    public Boolean createEntityTask(WebsocketTask websocketTask, String entityId) {
        //smartenv:ws:eventTask:{tenantId}:{sessionId}:{taskType}:{taskId}:{entityId}
        String baseKey = WebSocketConsts.CacheNames.EVENT_WEBSOCKET_TASK + StringPool.COLON + websocketTask.getTenantId() + StringPool.COLON + websocketTask.getSessionId() + StringPool.COLON + websocketTask.getTaskType() + StringPool.COLON + websocketTask.getId();
        String key = baseKey + StringPool.COLON + entityId;
        bladeRedisCache.set(key, websocketTask);
        return true;
    }


    @Override
    public Boolean createEasyVTask(WebsocketTask websocketTask) {
        String key = WebSocketConsts.CacheNames.EVENT_WEBSOCKET_EASYV + StringPool.COLON + websocketTask.getTaskType() + StringPool.COLON + websocketTask.getSessionId();
        bladeRedisCache.set(key, websocketTask);
        return true;
    }


    @Override
    public List<WebsocketTask> getWebsocketEasyVTask(String taskType) {
        Set<String> keys = bladeRedisCache.keys(WebSocketConsts.CacheNames.EVENT_WEBSOCKET_EASYV + StringPool.COLON + taskType + "*");
        List<WebsocketTask> collect = keys.stream().map(key -> {
            WebsocketTask websocketTask = bladeRedisCache.get(key);
            return websocketTask;
        }).collect(Collectors.toList());
        return collect;
    }


    /**
     * 获取指定租户指定任务类型的所有任务
     *
     * @param tenantId
     * @param taskType
     * @return
     */
    @Override
    public List<WebsocketTask> getTenantTasksByTypes(String tenantId, String taskType) {
        String key = WebSocketConsts.CacheNames.EVENT_WEBSOCKET_TASK + StringPool.COLON + tenantId + ":*:" + taskType + StringPool.COLON + "*";
        Set<String> keys = bladeRedisCache.keys(key);
        List<WebsocketTask> collect = keys.stream().map(ke -> {
            WebsocketTask websocketTask = bladeRedisCache.get(ke);
            return websocketTask;
        }).collect(Collectors.toList());
        return collect;
    }


    /**
     * 通过实体ID获取所有的task，用于对单个实体的查询（比如：查询了某辆车位置的task）
     *
     * @param tenantId
     * @param taskType
     * @param entityId
     * @return
     */
    @Override
    public List<WebsocketTask> getTenantEntityTasksByTypes(String tenantId, String taskType, String entityId) {
        String key = WebSocketConsts.CacheNames.EVENT_WEBSOCKET_TASK + StringPool.COLON + tenantId + ":*:" + taskType + ":*:" + entityId;
        Set<String> keys = bladeRedisCache.keys(key);
        List<WebsocketTask> collect = keys.stream().map(ke -> {
            WebsocketTask websocketTask = bladeRedisCache.get(ke);
            return websocketTask;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 删除某用户同一session下相同类型的任务
     *
     * @param sessionId
     * @param taskType
     */
    @Override
    public void deleteSameTask(String sessionId, String tenantId, String taskType) {
        Set<String> keys = bladeRedisCache.keys(WebSocketConsts.CacheNames.EVENT_WEBSOCKET_TASK + StringPool.COLON + tenantId + StringPool.COLON + taskType + "*");
        bladeRedisCache.del(keys);


    }


    /**
     * 通过TaskId 来删除task
     *
     * @param userId
     * @param taskId
     * @return
     */
    @Override
    public boolean deleteTaskById(String userId, String taskId) {
        String tenantId = null;
        Set<String> keys = bladeRedisCache.keys(WebSocketConsts.CacheNames.EVENT_WEBSOCKET_TASK + StringPool.COLON + tenantId + StringPool.COLON + "*" + StringPool.COLON + taskId);
        bladeRedisCache.del(keys);

        return true;
    }


    /**
     * 清空所有任务,主要是为了避免有脏数据,一般是每天凌晨定时执行
     */
    @Override
    public boolean clearTask() {
        Set<String> taskSet = bladeRedisCache.keys("smartenv:ws*");
        log.info("taskSet:{}", JSON.toJSONString(taskSet));
        taskSet.stream().forEach(key -> {
            System.out.println(key);
            bladeRedisCache.del(key);
        });
        return true;
    }


    @Override
    public String getOssObjLink(String objectName) {
        String key = "smartenv:polymerization:images:" + objectName;
        String imgLink = bladeRedisCache.get(key);

        if (StringUtil.isBlank(imgLink)) {
            R<String> shareLink = ossClient.getObjectLink(CommonConstant.BUCKET, objectName);
            if (shareLink != null && shareLink.getData() != null) {
                bladeRedisCache.setEx(key, shareLink.getData(), CacheNames.ExpirationTime.EXPIRATION_TIME_24HOURS);
                return shareLink.getData();
            }
            return null;
        } else {
            return imgLink;
        }
    }
}
