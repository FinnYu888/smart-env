package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.EventTypeCountDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.EventTypeCountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.EventVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IAlarmService;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IEventService;
import com.ai.apac.smartenv.websocket.task.EventCountTask;
import com.ai.apac.smartenv.websocket.task.EventTask;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName EventService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/11 15:03
 * @Version 1.0
 */
@Service
public class EventService implements IEventService {

    @Autowired
    private IEventInfoClient eventInfoClient;

    @Autowired
    private WebSocketTaskService webSocketTaskService;

    @Autowired
    private IBaseService baseService;


    @Override
    public List<EventInfoVO> listEvevtInfoByParam(Date startDate, Date endDate, Integer status, String level,Integer eventNum,String tenantId) {
        EventQueryDTO eventQueryDTO = new EventQueryDTO();
        Long startTime = startDate == null ? null : startDate.getTime();
        Long endTime = endDate == null ? null : endDate.getTime();
        eventQueryDTO.setStartTime(startTime);
        eventQueryDTO.setEndTime(endTime);
        eventQueryDTO.setStatus(status);
        eventQueryDTO.setEventNum(eventNum);
        eventQueryDTO.setEventLevel(level);
        eventQueryDTO.setTenantId(tenantId);
        R<List<EventInfoVO>> result = eventInfoClient.listEventInfoByParam(eventQueryDTO);
        return result.getData();
    }

    @Override
    public Future<Integer> countEventDaily(String tenantId) {
        R<Integer> countResult = eventInfoClient.countEventDaily(tenantId);
        if (countResult.isSuccess() && countResult.getData() != null) {
            return new AsyncResult<Integer>(countResult.getData());
        }
        return null;
    }

    @Override
    public void pushLastEventList(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        EventTask task = new EventTask(websocketTask);
        task.run();
    }

    @Override
    public List<EventVO> getLastEventList(String tenantId) {
        List<EventVO> eventVOList = new ArrayList<EventVO>();
        List<EventInfoVO> eventInfoVOList = new ArrayList<EventInfoVO>();
        try {
            Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
            Date endTime = DateTime.now();
            eventInfoVOList = this.listEvevtInfoByParam(startTime,endTime, EventConstant.Event_Status.HANDLE_1, EventConstant.Event_LEVEL.LEVEL_1,10,tenantId);

            if(eventInfoVOList.size() > 0 ){
                eventInfoVOList.forEach(eventInfoVO_ -> {
                    EventVO eventVO = new EventVO();
                    eventVO.setId(eventInfoVO_.getId().toString());
                    eventVO.setEventType(eventInfoVO_.getEventType());
                    eventVO.setEventMessage(eventInfoVO_.getEventDesc());
                    eventVO.setEventDate(TimeUtil.getYYYYMMDDHHMMSS(eventInfoVO_.getCreateTime()));
                    eventVOList.add(eventVO);
                });
            }


        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return eventVOList;
    }

    @Override
    public void pushEventCountByType(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        EventCountTask task = new EventCountTask(websocketTask);
        task.run();

    }

    @Override
    public List<EventTypeCountDTO> getEventCountByType(String tenantId) {
        List<com.ai.apac.smartenv.event.vo.EventTypeCountVO> eventTypeCountVOList = eventInfoClient.countEventGroupByType(tenantId,7).getData();
        return BeanUtil.copyProperties(eventTypeCountVOList,EventTypeCountDTO.class);
    }
}
