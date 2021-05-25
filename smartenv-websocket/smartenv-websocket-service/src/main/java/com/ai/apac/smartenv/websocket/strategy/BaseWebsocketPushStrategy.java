package com.ai.apac.smartenv.websocket.strategy;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BaseWebsocketPushStrategy
 * @Description: websocket 基本推送策略
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/27
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/27  2020/10/27    panfeng          v1.0.0             修改原因
 */

public abstract class BaseWebsocketPushStrategy<T> {

    private static SimpMessagingTemplate wsTemplate = null;

    //当前策略支持的任务
    public abstract String getSupportEventType();

    /**
     * 具体的策略。
     */
    public abstract void strategy(BaseWsMonitorEventDTO<T> baseWsMonitorEventDTO);

    protected static SimpMessagingTemplate getWsTemplate() {
        if (wsTemplate == null) {
            wsTemplate = SpringUtil.getBean(SimpMessagingTemplate.class);
        }
        return wsTemplate;
    }


    /**
     * 推送指定任务的消息
     *
     * @param sendContent
     */
    public <U> void send(WebsocketTask websocketTask, R<U> sendContent) {
        if (sendContent == null || sendContent.getData() == null || websocketTask == null || StringUtil.isBlank(websocketTask.getSessionId())) {
            return;
        }
        getWsTemplate().convertAndSendToUser(websocketTask.getSessionId(), websocketTask.getTopic(), JSONUtil.toJsonStr(sendContent), WebSocketUtil.createHeaders(websocketTask.getSessionId()));
    }


}
