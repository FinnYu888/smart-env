package com.ai.apac.smartenv.websocket.util;

import com.ai.apac.smartenv.system.user.cache.UserCache;
import com.ai.apac.smartenv.system.user.entity.User;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import org.springblade.core.log.exception.ServiceException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;

import java.util.List;
import java.util.Map;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/16 3:32 下午
 **/
public class WebSocketUtil {

    /**
     * 根据sessionId构造消息头
     *
     * @param sessionId
     * @return
     */
    public static MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    /**
     * 构造基本的WebsocketTask对象
     * 校验请求头中是否有userId,如果没有则抛出异常,有的话则返回WebsocketTask对象
     */
    public static WebsocketTask buildTask(SimpMessageHeaderAccessor headerAccessor) {
        List<String> userId = headerAccessor.getNativeHeader("userId");
        if (userId == null || userId.size() == 0) {
            throw new ServiceException("请求头中必须包含userId");
        }
        WebsocketTask task = new WebsocketTask();
        task.setSessionId(headerAccessor.getSessionId());
        task.setUserId(userId.get(0));
        User user = UserCache.getUser(Long.parseLong(userId.get(0)));
        task.setTenantId(user.getTenantId());


        return task;
    }

    /**
     * 构造基本的WebsocketTask对象
     * 校验请求头中是否有userId,如果没有则抛出异常,有的话则返回WebsocketTask对象
     */
    public static WebsocketTask buildTask(SimpMessageHeaderAccessor headerAccessor, String taskType,
                                          String topic, String schedule, Map<String, Object> params) {
        WebsocketTask task = buildTask(headerAccessor);
        task.setTopic(topic);
        task.setTaskType(taskType);
        task.setParams(params);
        return task;
    }
}
