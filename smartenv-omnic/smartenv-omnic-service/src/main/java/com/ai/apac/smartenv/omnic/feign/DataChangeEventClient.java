package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.service.IDataChangeEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/5 4:35 下午
 **/
@RestController
@RequiredArgsConstructor
public class DataChangeEventClient<T> implements IDataChangeEventClient {

    @Autowired
    private IDataChangeEventService dataChangeEventService;

    /**
     * 触发websocket事件
     *
     * @param eventType
     * @param tenantId
     * @param userId
     * @param eventObject
     */
    @Override
    @PostMapping(DO_WEBSOCKET_EVENT)
    public void doWebsocketEvent(@RequestParam String eventType, @RequestParam String tenantId, @RequestParam String userId, @RequestParam Object eventObject) {
        dataChangeEventService.doWebsocketEvent(eventType, tenantId, userId, eventObject);
    }

    /**
     * 触发websocket事件
     *
     * @param wsMonitorEventDTO
     */
    @Override
    @PostMapping(DO_WEBSOCKET_EVENT_BY_OBJECT)
    public void doWebsocketEvent(@RequestBody BaseWsMonitorEventDTO wsMonitorEventDTO) {
        dataChangeEventService.doWebsocketEvent(wsMonitorEventDTO);
    }

    /**
     * 触发数据库变更监听事件
     *
     * @param dbEventDTO
     */
    @Override
    @PostMapping(DO_DB_EVENT_BY_OBJECT)
    public void doDbEvent(@RequestBody BaseDbEventDTO dbEventDTO) {
        dataChangeEventService.doDbEvent(dbEventDTO);
    }
}
