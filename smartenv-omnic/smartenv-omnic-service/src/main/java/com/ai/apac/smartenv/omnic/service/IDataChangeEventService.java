package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;

/**
 * @author qianlong
 * @Description 数据变更事件业务处理接口
 * @Date 2020/11/5 4:14 下午
 **/
public interface IDataChangeEventService {

    /**
     * 触发websocket事件
     *
     * @param wsMonitorEventDTO
     */
    <T> void  doWebsocketEvent(BaseWsMonitorEventDTO<T> wsMonitorEventDTO);

    /**
     * 触发websocket事件
     * @param eventType
     * @param tenantId
     * @param userId
     * @param eventObject
     */
    <T> void doWebsocketEvent(String eventType, String tenantId, String userId, Object eventObject);

    /**
     * 触发websocket事件
     * @param eventType
     * @param tenantId
     * @param userId
     */
    <T> void doWebsocketEvent(String eventType, String tenantId, String userId);

    /**
     * 触发数据库变更事件
     *
     * @param dbEventDTO
     */
    <T> void doDbEvent(BaseDbEventDTO<T> dbEventDTO);
}
