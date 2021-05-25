package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/5 4:26 下午
 **/
public class IDataChangeEventClientFallback implements IDataChangeEventClient {

    @Override
    public void doWebsocketEvent(String eventType, String tenantId, String userId, Object eventObject) {
        return;
    }

    /**
     * 触发websocket事件
     *
     * @param wsMonitorEventDTO
     */
    @Override
    public void doWebsocketEvent(BaseWsMonitorEventDTO wsMonitorEventDTO) {
        return;
    }

    /**
     * 触发数据库变更监听事件
     *
     * @param dbEventDTO
     */
    @Override
    public void doDbEvent(BaseDbEventDTO dbEventDTO) {
        return;
    }
}
