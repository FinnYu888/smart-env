package com.ai.apac.smartenv.websocket.controller;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.websocket.common.BaseWebSocketResp;
import com.ai.apac.smartenv.websocket.common.WebSocketDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.*;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.LastGarbageVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.service.*;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.ai.smartenv.cache.util.SmartCache;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@Api("大屏")
@Slf4j
public class BigScreenController {


    @Autowired
    private IPersonService personService;

    @Autowired
    private IAlarmService alarmService;

    @Autowired
    private IOmnicService omnicService;

    @Autowired
    private IEventService eventService;

    @Autowired
    private IFacilityService facilityService;

    @Autowired
    private IVehicleService vehicleService;

    @Autowired
    private SimpMessagingTemplate wsTemplate;


    @Autowired
    private IPolymerizationService polymerizationService;


    public static final String GET_PERSON_INFO_REALTIME = "bigScreen.getPersonInfoRealTime";


    public static final String GET_VEHICLE_INFO_REALTIME = "bigScreen.getVehicleInfoRealTime";

    public static final String GET_BIG_SCREEN_PERSON_AND_VEHICLE = "bigScreenController.getBigScreenPersonAndVehicle";

    //大屏统计数据
    public static final String GET_BIG_SCREEN_DATA_COUNT_DAILY = "bigScreen.getDataCountDaily";

    //大屏按地区统计垃圾总量
    public static final String GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION = "bigScreen.getGarbageAmountByRegion";

    //大屏按垃圾类型统计每天的数量
    public static final String GET_BIGSCREEN_LAST_GARBAGE_AMOUNT = "bigScreen.getLastGarbageAmount";

    //大屏按告警规则类型统计每天的数据
    public static final String GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT = "bigScreen.countAllRuleAlarmAmount";

    //大屏展现最新的告警
    public static final String GET_BIGSCREEN_LAST_ALARM_LIST = "bigScreen.getLastAlarmList";

    //大屏按事件类型展现各类事件数量
    public static final String GET_BIGSCREEN_EVENT_COUNT_BY_TYPE = "bigScreen.getEventCountByType";

    //大屏按事件发生所在片区展现各类事件数量
    public static final String GET_BIGSCREEN_EVENT_COUNT_BY_AREA = "bigScreen.getEventCountByArea";


    // 大屏左上角的数量
    public static final String GET_BIGSCREEN_ALL_ENTITY_COUNT = "bigScreen.getAllEntityCount";


    @MessageMapping(GET_BIGSCREEN_ALL_ENTITY_COUNT)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<PolymerizationCountVO> getAllEntityCount(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIGSCREEN_ALL_ENTITY_COUNT, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", null);
        polymerizationService.pushPolymerizationEntityCount(task);
        PolymerizationCountVO polymerizationCountVO = new PolymerizationCountVO();
        polymerizationCountVO.setTopicName(WebSocketConsts.PUSH_POLYMERIZATION_ENTITYS);
        polymerizationCountVO.setActionName(GET_BIGSCREEN_ALL_ENTITY_COUNT);

        BaseWebSocketResp<PolymerizationCountVO> result = BaseWebSocketResp.data(polymerizationCountVO);
        return result;
    }


    @MessageMapping(GET_BIGSCREEN_EVENT_COUNT_BY_TYPE)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<EventTypeCountVO> getEventCountByType(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIGSCREEN_EVENT_COUNT_BY_TYPE, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", null);
        eventService.pushEventCountByType(task);
        EventTypeCountVO eventTypeCountVO = new EventTypeCountVO();
        eventTypeCountVO.setTopicName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        eventTypeCountVO.setActionName(GET_BIGSCREEN_EVENT_COUNT_BY_TYPE);
        BaseWebSocketResp<EventTypeCountVO> result = BaseWebSocketResp.data(eventTypeCountVO);
        return result;
    }


    @MessageMapping(GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<AlarmAmountVO> countAllRuleAlarmAmount(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", null);
        alarmService.pushAllRuleAlarmAmount(task);
        AlarmAmountVO alarmAmountVO = new AlarmAmountVO();
        alarmAmountVO.setTopicName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        alarmAmountVO.setActionName(GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT);
        BaseWebSocketResp<AlarmAmountVO> result = BaseWebSocketResp.data(alarmAmountVO);
        return result;
    }

    @MessageMapping(GET_BIGSCREEN_LAST_ALARM_LIST)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<AlarmInfoScreenViewVO> getLastAlarmList(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIGSCREEN_LAST_ALARM_LIST, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", null);
        alarmService.pushBigScreenLastAlarmList(task);
        AlarmInfoScreenViewVO alarmInfoScreenViewVO = new AlarmInfoScreenViewVO();
        alarmInfoScreenViewVO.setTopicName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        alarmInfoScreenViewVO.setActionName(GET_BIGSCREEN_LAST_ALARM_LIST);
        BaseWebSocketResp<AlarmInfoScreenViewVO> result = BaseWebSocketResp.data(alarmInfoScreenViewVO);
        return result;
    }


    @MessageMapping(GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<LastGarbageAmountByRegionVO> getLastGarbageAmountByRegion(@Payload String days, SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", null);
        facilityService.pushLastGarbageAmountByRegion(task);
        LastGarbageAmountByRegionVO lastGarbageAmountByRegionVO = new LastGarbageAmountByRegionVO();
        lastGarbageAmountByRegionVO.setTopicName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        lastGarbageAmountByRegionVO.setActionName(GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION);
        BaseWebSocketResp<LastGarbageAmountByRegionVO> result = BaseWebSocketResp.data(lastGarbageAmountByRegionVO);
        return result;
    }


    @MessageMapping(GET_BIGSCREEN_LAST_GARBAGE_AMOUNT)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<LastGarbageVO> getLastGarbage(@Payload String days, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("days", days);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIGSCREEN_LAST_GARBAGE_AMOUNT, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", params);
        facilityService.pushLastGarbageAmount(task);
        LastGarbageVO lastGarbageVO = new LastGarbageVO();
        lastGarbageVO.setTopicName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        lastGarbageVO.setActionName(GET_BIGSCREEN_LAST_GARBAGE_AMOUNT);
        BaseWebSocketResp<LastGarbageVO> result = BaseWebSocketResp.data(lastGarbageVO);
        return result;
    }


    @MessageMapping(GET_BIG_SCREEN_PERSON_AND_VEHICLE)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<WebSocketDTO> getBigScreenPersonAndVehicle(@Payload GetBigScreenDto getBigScreenDto, SimpMessageHeaderAccessor headerAccessor) {
        log.info("GET_PERSON_POSITION Request:{}", JSON.toJSONString(getBigScreenDto));

        if (getBigScreenDto.getEntityType() == null) {
            createVehiclePosition(getBigScreenDto, headerAccessor);
            createPersonPosition(getBigScreenDto, headerAccessor);
        } else if (getBigScreenDto.getEntityType().equals(CommonConstant.ENTITY_TYPE.VEHICLE)) {
            createVehiclePosition(getBigScreenDto, headerAccessor);
        } else if (getBigScreenDto.getEntityType().equals(CommonConstant.ENTITY_TYPE.PERSON)) {
            createPersonPosition(getBigScreenDto, headerAccessor);
        }

        WebSocketDTO webSocketDTO = new WebSocketDTO();
        webSocketDTO.setTopicName(WebSocketConsts.PUSH_PERSON_MONITOR);
        webSocketDTO.setActionName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        BaseWebSocketResp<WebSocketDTO> result = BaseWebSocketResp.data(webSocketDTO);
        return result;
    }


    private void createPersonPosition(GetBigScreenDto getBigScreenDto, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tenantId", getBigScreenDto.getTenantId());
        params.put("regionId", getBigScreenDto.getRegionId());
        params.put("isBigScreen", true);
        params.put("isEasyV", getBigScreenDto.getIsEasyV());
        WebsocketTask personTask = WebSocketUtil.buildTask(headerAccessor,
                PersonController.GET_PERSON_POSITION, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", params);
//        personTask.setTaskType("Person");
        personService.pushPersonPosition(personTask);

    }


    private void createVehiclePosition(GetBigScreenDto getBigScreenDto, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> param2 = new HashMap<String, Object>();
        param2.put("tenantId", getBigScreenDto.getTenantId());
        param2.put("regionId", getBigScreenDto.getRegionId());
        param2.put("isBigScreen", true);
        param2.put("isEasyV", getBigScreenDto.getIsEasyV());
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                VehicleController.GET_VEHICLE_POSITION, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", param2);
//        task.setTaskType("Vehicle");
        vehicleService.pushVehiclePosition(task);

    }


    /**
     * 实时获取人员信息,对应客户端的弹窗
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_PERSON_INFO_REALTIME)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp getPersonInfoRealTime(@Payload String personId,
                                                   SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("personId", personId);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_PERSON_INFO_REALTIME, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", params);
        //客户端需要立即显示实时信息,所以这边要立即查询,然后才会进入定时任务
        String tenantId = SmartCache.hget(CacheNames.PERSON_TENANT_MAP, Long.valueOf(personId));
        PersonDetailVO personDetailVO = personService.getPersonDetailRealTime(personId, tenantId);
        String sessionId = headerAccessor.getSessionId();
        wsTemplate.convertAndSendToUser(task.getSessionId(), task.getTopic(), JSONUtil.toJsonStr(personDetailVO), WebSocketUtil.createHeaders(sessionId));
        personService.pushPersonDetail(task);
        return BaseWebSocketResp.status(true);
    }


    /**
     * 实时获取车辆信息,对应客户端打开的弹窗
     *
     * @param headerAccessor
     * @return
     */
    @MessageMapping(GET_VEHICLE_INFO_REALTIME)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<VehicleDetailVO> getVehicleInfoRealTime(@Payload String vehicleId,
                                                                     SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", vehicleId);
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_VEHICLE_INFO_REALTIME, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", params);
        //客户端需要立即显示实时信息,所以这边要立即查询,然后才会进入定时任务
        String tenantId = SmartCache.hget(CacheNames.VEHICLE_TENANT_MAP, Long.valueOf(vehicleId));
        VehicleDetailVO vehicleDetailVO = vehicleService.getVehicleDetailRealTime(Long.valueOf(vehicleId), tenantId, BaiduMapUtils.CoordsSystem.BD09LL);
        String sessionId = headerAccessor.getSessionId();
        wsTemplate.convertAndSendToUser(task.getSessionId(), task.getTopic(), JSONUtil.toJsonStr(vehicleDetailVO), WebSocketUtil.createHeaders(sessionId));
        vehicleService.pushVehicleDetail(task);
        return BaseWebSocketResp.data(vehicleDetailVO);
    }

    @MessageMapping(GET_BIG_SCREEN_DATA_COUNT_DAILY)
    @SendTo(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS)
    public BaseWebSocketResp<HomePageDataCountVO> getDataCountDaily(SimpMessageHeaderAccessor headerAccessor) {
        WebsocketTask task = WebSocketUtil.buildTask(headerAccessor,
                GET_BIG_SCREEN_DATA_COUNT_DAILY, WebSocketConsts.PUSH_BIGSCREEN_ENTITYS,
                "0/5 * * * * ?", null);
        omnicService.pushHomeDataCountDaily(task);
        HomePageDataCountVO mainDataCountVO = new HomePageDataCountVO();
        mainDataCountVO.setTopicName(WebSocketConsts.PUSH_BIGSCREEN_ENTITYS);
        mainDataCountVO.setActionName(GET_BIG_SCREEN_DATA_COUNT_DAILY);

        BaseWebSocketResp<HomePageDataCountVO> result = BaseWebSocketResp.data(mainDataCountVO);
        return result;
    }


}
