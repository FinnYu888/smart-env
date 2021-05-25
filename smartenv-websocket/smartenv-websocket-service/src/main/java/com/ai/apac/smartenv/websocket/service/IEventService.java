package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.EventTypeCountDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.EventTypeCountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.EventVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

public interface IEventService {

    /**
     * 根据条件查询告警信息
     * @return
     */
    List<EventInfoVO> listEvevtInfoByParam(Date startDate, Date endDate, Integer status,String eventLevel, Integer eventNum,String tenantId);

    Future<Integer> countEventDaily(String tenantId);

    /**
     * 推送首页事件列表
     * @param websocketTask
     */
    void pushLastEventList(WebsocketTask websocketTask);

    /**
     * 获取首页告警列表用于推送
     * @param tenantId
     * @return
     */
    List<EventVO> getLastEventList(String tenantId);

    /**
     * 推送大屏根据事件类型统计数据
     * @param websocketTask
     */
    void pushEventCountByType(WebsocketTask websocketTask);

    /**
     * 获取大屏根据事件类型统计数据用于推送
     * @param tenantId
     */
    List<EventTypeCountDTO> getEventCountByType(String tenantId);
}
