package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/11/5 4:25 下午
 **/
@FeignClient(value = ApplicationConstant.APPLICATION_OMNIC_NAME, fallback = IDataChangeEventClientFallback.class)
public interface IDataChangeEventClient {

    String client = "/client";
    String DO_WEBSOCKET_EVENT = client + "/websocket-event";
    String DO_WEBSOCKET_EVENT_BY_OBJECT = client + "/websocket-event-by-object";
    String DO_DB_EVENT_BY_OBJECT = client + "/db-event-by-object";

    /**
     * 触发websocket事件
     *
     * @param eventType
     * @param tenantId
     * @param userId
     * @param eventObject
     */
    @PostMapping(DO_WEBSOCKET_EVENT)
    void doWebsocketEvent(@RequestParam String eventType, @RequestParam String tenantId, @RequestParam String userId, @RequestParam Object eventObject);

    /**
     * 触发websocket事件
     *
     * @param wsMonitorEventDTO
     */
    @PostMapping(DO_WEBSOCKET_EVENT_BY_OBJECT)
    void doWebsocketEvent(@RequestBody BaseWsMonitorEventDTO wsMonitorEventDTO);

    /**
     * 触发数据库变更监听事件
     *
     * @param dbEventDTO
     */
    @PostMapping(DO_DB_EVENT_BY_OBJECT)
    void doDbEvent(@RequestBody BaseDbEventDTO dbEventDTO);
}
