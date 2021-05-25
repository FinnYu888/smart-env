package com.ai.apac.smartenv.websocket.event;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WebSocketEventListener
 *
 * @author rajeevkumarsingh
 */
@Slf4j
@Component
@AllArgsConstructor
public class WebSocketEventListener {

    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private BladeRedisCache bladeRedisCache;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("The sessionId for connect:{}", sessionId);
        Object userIdObj = getHeader("userId", headerAccessor);
        if (userIdObj != null) {
            String userId = ((List<String>) userIdObj).get(0);
            log.info("User Login : {}", userId);
            // 在redis中为该用户增加sessionId,当断开连接的时候需要移除,默认过期时间1小时
            bladeRedisCache.setEx(WebSocketConsts.CacheNames.SESSION_USER + ":" + sessionId, userId,
                    WebSocketConsts.CacheNames.EXPIRE_TIME);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("The sessionId for disconnect:{}", sessionId);
        // TODO 需要从Redis中移除该sessionId
        bladeRedisCache.del(WebSocketConsts.CacheNames.SESSION_USER + ":" + sessionId);

//
//        //移除entity task
//        Set<String> keys = bladeRedisCache.keys(WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":" + "entityTask*");
//        if (CollectionUtil.isNotEmpty(keys)) {
//            keys.forEach(key -> {
//                List<EntityTaskDto> taskDtos = bladeRedisCache.get(key);
//                Boolean isUpdate=false;
//                if (CollectionUtil.isNotEmpty(taskDtos)) {
//                    for (int i = 0; i < taskDtos.size(); i++) {
//                        EntityTaskDto entityTaskDto = taskDtos.get(i);
//                        if (entityTaskDto.getSessionId().equals(sessionId)){
//                            taskDtos.remove(i);
//                            isUpdate=true;
//                        }
//                    }
//                }
//                if (isUpdate){
//                    bladeRedisCache.set(key,taskDtos);
//                }
//            });
//
//        }
//
//        //移除home task
//        Set<String> homeTaskkeys = bladeRedisCache.keys(WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:*" + sessionId);
//        if (CollectionUtil.isNotEmpty(homeTaskkeys)) {
//            bladeRedisCache.del(homeTaskkeys);
//        }
//        //移除bigscreen task
//        Set<String> bigscreenTaskkeys = bladeRedisCache.keys(WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:*" + sessionId);
//        if (CollectionUtil.isNotEmpty(bigscreenTaskkeys)) {
//            bladeRedisCache.del(bigscreenTaskkeys);
//        }
//
//
//        //移除polymerization Task
//        Set<String> polymerizationTaskkeys = bladeRedisCache.keys(WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":polymerizationTask:*" + sessionId);
//        if (CollectionUtil.isNotEmpty(polymerizationTaskkeys)) {
//            bladeRedisCache.del(polymerizationTaskkeys);
//        }
//

    }

    private Object getHeader(String headerName, StompHeaderAccessor headerAccessor) {
        MessageHeaders messageHeaders = headerAccessor.getMessageHeaders();
        GenericMessage simpConnectMessage = (GenericMessage) messageHeaders.get("simpConnectMessage");
        if (simpConnectMessage == null) {
            return null;
        }
        Map<String, Object> nativeHeaders = (Map<String, Object>) simpConnectMessage.getHeaders().get("nativeHeaders");
        if (nativeHeaders == null || nativeHeaders.size() == 0) {
            return null;
        }
        Object headerObj = nativeHeaders.get(headerName);
        return headerObj;
    }
}
