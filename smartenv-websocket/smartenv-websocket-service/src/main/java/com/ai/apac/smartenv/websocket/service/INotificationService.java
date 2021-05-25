package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;

/**
 * @author qianlong
 * @description 全局消息通知
 * @Date 2020/3/10 9:48 上午
 **/
public interface INotificationService {

    /**
     * 推送消息
     *
     * @param notificationInfo
     * @return
     */
    boolean pushNotification(NotificationInfo notificationInfo);

    /**
     * 为该用户建立通讯通道
     *
     * @param userId
     * @param sessionId
     * @return
     */
    boolean createChannel(String userId, String sessionId);
}
