package com.ai.apac.smartenv.omnic.service.impl;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.service.IDataChangeEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/5 4:17 下午
 **/
@Service
public class DataChangeEventServiceImpl implements IDataChangeEventService {

    @Autowired
    private OmnicProducerSource omnicProducerSource;

    /**
     * 触发websocket事件
     *
     * @param wsMonitorEventDTO
     */
    @Async
    @Override
    public  void doWebsocketEvent(BaseWsMonitorEventDTO wsMonitorEventDTO) {
        //发送websocket消息
        Message<?> msg = MessageBuilder.withPayload(wsMonitorEventDTO).build();
        omnicProducerSource.websocketMonitorEvent().send(msg);
    }

    /**
     * 触发websocket事件
     *
     * @param eventType
     * @param tenantId
     * @param userId
     * @param eventObject
     */
    @Async
    @Override
    public <T>  void doWebsocketEvent(String eventType, String tenantId, String userId, Object eventObject) {
        BaseWsMonitorEventDTO<T> baseWsMonitorEventDTO = new BaseWsMonitorEventDTO<T>(eventType, tenantId, userId, null);
        if (eventObject != null) {
            baseWsMonitorEventDTO.setEventObject((T) eventObject);
        }
        doWebsocketEvent(baseWsMonitorEventDTO);
    }

    /**
     * 触发websocket事件
     *
     * @param eventType
     * @param tenantId
     * @param userId
     */
    @Async
    @Override
    public  void doWebsocketEvent(String eventType, String tenantId, String userId) {
        doWebsocketEvent(eventType, tenantId, userId, null);
    }

    /**
     * 触发数据库变更事件
     *
     * @param dbEventDTO
     */
    @Async
    @Override
    public void doDbEvent(BaseDbEventDTO dbEventDTO) {
        //发送消息
        Message<?> msg = MessageBuilder.withPayload(dbEventDTO).build();
        omnicProducerSource.dbMonitorEvent().send(msg);
    }
}
