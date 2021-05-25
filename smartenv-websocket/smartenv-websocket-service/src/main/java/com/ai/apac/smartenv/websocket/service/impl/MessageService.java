package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.system.user.dto.UserMessageDTO;
import com.ai.apac.smartenv.system.user.feign.IMessageClient;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IMessageService;
import com.ai.apac.smartenv.websocket.task.HomeDataCountTask;
import com.ai.apac.smartenv.websocket.task.UnReadMessageCountTask;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @ClassName MessageService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/4/3 10:44
 * @Version 1.0
 */
@Service
@Slf4j
public class MessageService implements IMessageService {



    @Autowired
    private IMessageClient messageClient;

    @Autowired
    private WebSocketTaskService webSocketTaskService;

    /**
     * 推送应出勤车辆数量，应出勤人员数量，事件数量，告警数量,车辆出勤数，人员出勤数的实时数据
     * @param websocketTask
     */
    @Override
    public void pushUnReadMessage(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        webSocketTaskService.createTask(websocketTask);
        UnReadMessageCountTask task = new UnReadMessageCountTask(websocketTask);
        ThreadUtil.execute(task);
//        task.run();
    }

    @Override
    public Future<UserMessageDTO> unReadMessage(String tenantId, String userId) {
        R<UserMessageDTO> countResult = messageClient.unReadMessage(tenantId,userId);
        if (countResult.isSuccess() && countResult.getData() != null) {
            return new AsyncResult<UserMessageDTO>(countResult.getData());
        }
        return null;
    }
}
