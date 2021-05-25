package com.ai.apac.smartenv.websocket.controller;

import cn.hutool.core.util.RandomUtil;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.websocket.service.INotificationService;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * NotificationController
 *
 * @author qianlong
 */
@RestController
@Api("通知管理")
@Slf4j
public class NotificationController extends BladeController {

    public static final String GET_NOTIFICATION = "getNotification";

    @Autowired
    private INotificationService notificationService;

    /**
     * 创建Notification通道
     *
     * @param userId
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_NOTIFICATION)
    @SendTo(WebSocketConsts.PUSH_NOTIFICATION)
    public BaseWebSocketResp<NotificationInfo> getNotification(@Payload String userId,
                                                               SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        notificationService.createChannel(userId, sessionId);
        NotificationInfo notificationInfo = new NotificationInfo();
        return BaseWebSocketResp.data(notificationInfo);
    }

    @PostMapping("/notification")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "发送通知", notes = "发送通知")
    public R pushNotification(@RequestBody NotificationInfo notificationInfo) {
        return R.status(notificationService.pushNotification(notificationInfo));
    }

}
