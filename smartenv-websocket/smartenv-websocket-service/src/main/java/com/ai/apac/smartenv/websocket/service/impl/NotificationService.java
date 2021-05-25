package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.websocket.service.INotificationService;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.ai.smartenv.cache.util.SmartCache;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/10 9:28 下午
 **/
@Service
@Slf4j
public class NotificationService implements INotificationService {

    @Autowired
    private BladeRedisCache bladeRedisCache;

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    /**
     * 推送消息
     *
     * @param notificationInfo
     * @return
     */
    @Override
    public boolean pushNotification(NotificationInfo notificationInfo) {
        String cacheName = null;
        //广播模式,则给该租户的所有用户发送
        if (notificationInfo.isBroadCast()) {
            String tenantId = notificationInfo.getTenantId();
            if (StringUtils.isBlank(tenantId)) {
                throw new ServiceException("租户ID不能为空");
            }
            cacheName = WebSocketConsts.CacheNames.NOTIFICATION_CHANNEL + StringPool.COLON + tenantId;
            log.info("向租户[{}]推送消息:{}", tenantId, JSON.toJSONString(notificationInfo));
            //从缓存中获取该租户所有用户
            List<List<String>> userSessionList = bladeRedisCache.hVals(cacheName);
            if (userSessionList != null || userSessionList.size() > 0) {
                userSessionList.stream().forEach(userSession -> {
                    if (userSession != null && userSession.size() > 0) {
                        userSession.stream().forEach(sessionId -> {
                            pushNotification(sessionId, tenantId, notificationInfo);
                        });
                    }
                });
            }
        } else {
            String userIdList = notificationInfo.getUserId();
            if (StringUtils.isBlank(userIdList)) {
                throw new ServiceException("用户ID不能为空");
            }
            String[] userIds = userIdList.split(",");
            for (String id : userIds) {
                User user = UserCache.getUser(Long.valueOf(id));
                if (user == null) {
                    throw new ServiceException("待发送通知的用户不存在");
                }
                String tenantId = user.getTenantId();
                cacheName = WebSocketConsts.CacheNames.NOTIFICATION_CHANNEL + StringPool.COLON + tenantId;
                List<String> sessionIdList = SmartCache.hget(cacheName, id);
                if (sessionIdList != null && sessionIdList.size() > 0) {
                    log.info("向用户[{}]推送消息:{}", id, JSON.toJSONString(notificationInfo));
                    sessionIdList.stream().forEach(sessionId -> {
                        pushNotification(sessionId, tenantId, notificationInfo);
                    });
                }
            }


        }
        return true;
    }

    private void pushNotification(String sessionId, String tenantId, NotificationInfo notificationInfo) {
        //判断sessionId是否还存在,如果不存在则要删除通道
        String userId = bladeRedisCache.get(WebSocketConsts.CacheNames.SESSION_USER + StringPool.COLON + sessionId);
        if (userId == null) {
            String cacheName = WebSocketConsts.CacheNames.NOTIFICATION_CHANNEL + StringPool.COLON + tenantId;
            List<List<String>> userSessionList = bladeRedisCache.hVals(cacheName);
            if(userSessionList != null && userSessionList.size() > 0){

            }
            return;
        }
        wsTemplate.convertAndSendToUser(sessionId, WebSocketConsts.PUSH_NOTIFICATION, JSONUtil.toJsonStr(notificationInfo), WebSocketUtil.createHeaders(sessionId));
    }

    /**
     * 为该用户建立通讯通道
     *
     * @param userId
     * @return
     */
    @Override
    public boolean createChannel(String userId, String sessionId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(sessionId)) {
            throw new ServiceException("参数缺失");
        }
        //根据user获取租户id
        User user = UserCache.getUser(Long.valueOf(userId));
        if (user == null || StringUtils.isBlank(user.getTenantId())) {
            throw new ServiceException("用户不存在");
        }
        String tenantId = user.getTenantId();
        String cacheName = WebSocketConsts.CacheNames.NOTIFICATION_CHANNEL + StringPool.COLON + tenantId;
        //现在允许一个用户同时登录多个浏览器,所以有可能一个用户对应多个session,所以map的value是sessionIdList
        List<String> sessionIdList = SmartCache.hget(cacheName, userId);
        if (sessionIdList == null || sessionIdList.size() == 0) {
            sessionIdList = new ArrayList<String>();
        }
        sessionIdList.add(sessionId);
        SmartCache.hset(cacheName, userId, sessionIdList);
        return true;
    }
}
