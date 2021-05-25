package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.websocket.module.notification.dto.NotificationInfo;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import java.util.concurrent.Future;

/**
 * @ClassName IMessageService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 10:42
 * @Version 1.0
 */
public interface IMessageService {

    void pushUnReadMessage(WebsocketTask websocketTask);

    Future<UserMessageDTO> unReadMessage(String tenantId, String userId);


}
