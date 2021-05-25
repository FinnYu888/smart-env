package com.ai.apac.smartenv.websocket.controller;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.module.main.vo.*;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.*;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName IndexController
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/3 13:45
 * @Version 1.0
 */
@RestController
@Api("首页Websocket")
@Slf4j
public class HomePageController extends BladeController {

    @Autowired
    private IOmnicService omnicService;

    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private IEventService eventService;

    @Autowired
    private IFacilityService facilityService;

    @Autowired
    private IResOrderService resOrderService;

    public static final String GET_HOME_DATA_COUNT_DAILY = "home.getDataCountDaily";

    //public static final String GET_HOME_WORKING_OFF_CURRENT = "home.getWorkingOffCurrent";

    public static final String GET_HOME_LAST_ALARM_LIST = "home.getLastAlarmList";

    public static final String GET_HOME_LAST10_EVENT = "home.getLast10Event";

    public static final String GET_HOME_LAST_GARBAGE_AMOUNT = "home.getLastGarbageAmount";

    public static final String GET_HOME_LAST6_ORDER = "home.getLast6Order";

    /**
     * 首页统计数字websocket
     * @return
     */
    @MessageMapping(GET_HOME_DATA_COUNT_DAILY)
    @SendTo(WebSocketConsts.PUSH_HOME_PAGE)
    public BaseWebSocketResp<HomePageDataCountVO> getDataCountDaily(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_HOME_DATA_COUNT_DAILY, WebSocketConsts.PUSH_HOME_PAGE,
                "0/5 * * * * ?", null);
        omnicService.pushHomeDataCountDaily(task);
        HomePageDataCountVO mainDataCountVO = new HomePageDataCountVO();
        mainDataCountVO.setTopicName(WebSocketConsts.PUSH_HOME_PAGE);
        mainDataCountVO.setActionName(GET_HOME_DATA_COUNT_DAILY);

        BaseWebSocketResp<HomePageDataCountVO> result = BaseWebSocketResp.data(mainDataCountVO);
        return result;
    }

    /**
     * 首页获取今天最新的的10条未处理的紧急告警信息
     *
     * @return
     */
    @MessageMapping(GET_HOME_LAST_ALARM_LIST)
    @SendTo(WebSocketConsts.PUSH_HOME_PAGE)
    public BaseWebSocketResp<Last10AlarmVO> getLast10Alarm(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_HOME_LAST_ALARM_LIST, WebSocketConsts.PUSH_HOME_PAGE,
                "0/5 * * * * ?", null);
        alarmService.pushHomeAlarmList(task);
        Last10AlarmVO last10AlarmVO = new Last10AlarmVO();
        last10AlarmVO.setTopicName(WebSocketConsts.PUSH_HOME_PAGE);
        last10AlarmVO.setActionName(GET_HOME_LAST_ALARM_LIST);
        BaseWebSocketResp<Last10AlarmVO> result = BaseWebSocketResp.data(last10AlarmVO);
        return result;
    }


    /**
     * 首页获取最新的10紧急事件信息
     *
     * @return
     */
    @MessageMapping(GET_HOME_LAST10_EVENT)
    @SendTo(WebSocketConsts.PUSH_HOME_PAGE)
    public BaseWebSocketResp<Last10EventVO> getLast10Event(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_HOME_LAST10_EVENT, WebSocketConsts.PUSH_HOME_PAGE,
                "0/5 * * * * ?", null);
        eventService.pushLastEventList(task);
        Last10EventVO last10EventVO = new Last10EventVO();
        last10EventVO.setTopicName(WebSocketConsts.PUSH_HOME_PAGE);
        last10EventVO.setActionName(GET_HOME_LAST10_EVENT);
        BaseWebSocketResp<Last10EventVO> result = BaseWebSocketResp.data(last10EventVO);
        return result;
    }

    /**
     * 首页获取最新30天垃圾收集数据
     *
     * @return
     */
    @MessageMapping(GET_HOME_LAST_GARBAGE_AMOUNT)
    @SendTo(WebSocketConsts.PUSH_HOME_PAGE)
    public BaseWebSocketResp<LastGarbageVO> getLastGarbage(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_HOME_LAST_GARBAGE_AMOUNT, WebSocketConsts.PUSH_HOME_PAGE,
                "0/5 * * * * ?", null);
        facilityService.pushLastGarbageAmount(task);
        LastGarbageVO lastGarbageVO = new LastGarbageVO();
        lastGarbageVO.setTopicName(WebSocketConsts.PUSH_HOME_PAGE);
        lastGarbageVO.setActionName(GET_HOME_LAST_GARBAGE_AMOUNT);
        BaseWebSocketResp<LastGarbageVO> result = BaseWebSocketResp.data(lastGarbageVO);
        return result;
    }

    /**
     * 首页当天最新待处理任务
     *
     * @return
     */
    @MessageMapping(GET_HOME_LAST6_ORDER)
    @SendTo(WebSocketConsts.PUSH_HOME_PAGE)
    public BaseWebSocketResp<Last6OrderVO> getLast6Order(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_HOME_LAST6_ORDER, WebSocketConsts.PUSH_HOME_PAGE,
                "0/5 * * * * ?", null);
        resOrderService.pushLast6Order(task);
        Last6OrderVO last6OrderVO = new Last6OrderVO();
        last6OrderVO.setTopicName(WebSocketConsts.PUSH_HOME_PAGE);
        last6OrderVO.setActionName(GET_HOME_LAST6_ORDER);
        BaseWebSocketResp<Last6OrderVO> result = BaseWebSocketResp.data(last6OrderVO);
        return result;
    }


}
