package com.ai.apac.smartenv.omnic.mq;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Copyright: Copyright (c) 2020/8/12 Asiainfo
 *
 * @ClassName: TrackProducerSource
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/12
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/12  17:21    zhanglei25          v1.0.0             修改原因
 */
public interface OmnicProducerSource {


    String WEBSOCKET_MONITOR_EVENT = "websocket-monitor-event-output";

    String DB_MONITOR_EVENT = "db-monitor-event-output";

    /**
     * websocket事件发送通道
     * @return
     */
    @Output(WEBSOCKET_MONITOR_EVENT)
    MessageChannel websocketMonitorEvent();

    /**
     * 数据库表变更事件发送通道
     * @return
     */
    @Output(DB_MONITOR_EVENT)
    MessageChannel dbMonitorEvent();
}
