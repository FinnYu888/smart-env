package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.mq.WebsocketProducerSource;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IPolymerizationService;
import com.ai.apac.smartenv.websocket.service.ITaskService;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.ai.apac.smartenv.websocket.task.*;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: TaskService
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  15:41    zhanglei25          v1.0.0             修改原因
 */
@Slf4j
@Service
@AllArgsConstructor
public class TaskService implements ITaskService {

    @Autowired
    private IWebSocketTaskService webSocketTaskService;

    @Autowired
    private IBaseService baseService;

    private static WebsocketProducerSource websocketProducerSource;

    private IAlarmInfoClient alarmInfoClient;

    private IEventInfoClient eventInfoClient;

    private MongoTemplate mongoTemplate;

    private BladeRedisCache bladeRedisCache;

    private IPolymerizationService polymerizationService;

//
//    private static WebsocketProducerSource getTaskProducerSource() {
//        if (websocketProducerSource == null) {
//            websocketProducerSource = SpringUtil.getBean(WebsocketProducerSource.class);
//        }
//        return websocketProducerSource;
//    }
//
//    @Override
//    public Boolean initPolymerizationDataCount(String tenantId) {
//        polymerizationService.updatePolymerizationCountRedis(tenantId,"-1");
//        return true;
//    }
//
//    @Override
//    public Boolean initDataCount(String tenantId) {
//        HomePageDataCountVO homePageDataCountVO = new HomePageDataCountVO();
//        homePageDataCountVO.setAlarmCount(alarmInfoClient.countAlarmInfoAmount(tenantId).getData());
//        homePageDataCountVO.setEventCount(eventInfoClient.countEventDaily(tenantId).getData());
//
//        Query query = new Query();
//
//        query.addCriteria(Criteria.where("tenantId").is(tenantId));
//        query.addCriteria(Criteria.where("watchDeviceId").exists(true));
//        List<BasicPersonDTO> basicPersonDTOList = mongoTemplate.find(query, BasicPersonDTO.class);
//        Map<Integer, Long> collect = basicPersonDTOList.stream().collect(Collectors.groupingBy(BasicPersonDTO::getWorkStatus, Collectors.counting()));
//        //静值人员数量
//        Long staticPersonCount = collect.containsKey(PersonConstant.PersonStatus.OFF_ONLINE_ALARM) ? collect.get(PersonConstant.PersonStatus.OFF_ONLINE_ALARM) : 0L;
//        //在岗人员数量
//        Long workingPersonCount = collect.containsKey(PersonConstant.PersonStatus.ONLINE) ? collect.get(PersonConstant.PersonStatus.ONLINE) : 0L;
//        //休息人员数量
//        Long restPersonCount = collect.containsKey(PersonConstant.PersonStatus.OFF_ONLINE) ? collect.get(PersonConstant.PersonStatus.OFF_ONLINE) : 0L;
//
//
//        homePageDataCountVO.setRestPersonCount(restPersonCount);
//        homePageDataCountVO.setStaticPersonCount(staticPersonCount);
//        homePageDataCountVO.setWorkingPersonCount(workingPersonCount);
//        homePageDataCountVO.setShouldWorkPersonCount(restPersonCount+staticPersonCount+workingPersonCount);
//
//
//
//        Query query1 = new Query();
//
//        query1.addCriteria(Criteria.where("tenantId").is(tenantId));
//        query1.addCriteria(new Criteria().orOperator(Criteria.where("gpsDeviceId").exists(true),Criteria.where("nvrDeviceId").exists(true),Criteria.where("cvrDeviceId").exists(true)));
//        List<BasicVehicleInfoDTO> basicVehicleInfoDTOList = mongoTemplate.find(query1, BasicVehicleInfoDTO.class);
//        Map<Integer, Long> collect1 = basicVehicleInfoDTOList.stream().collect(Collectors.groupingBy(BasicVehicleInfoDTO::getWorkStatus, Collectors.counting()));
//
//        //在岗车辆数量
//        Long workingVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.ONLINE) ? collect1.get(VehicleConstant.VehicleStatus.ONLINE) : 0L;
//
//        //静值车辆数量
//        Long staticVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM) ? collect1.get(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM) : 0L;
//
//        //休息车辆数量
//        Long restVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OFF_ONLINE) ? collect1.get(VehicleConstant.VehicleStatus.OFF_ONLINE) : 0L;
//
//        //加水车辆数量
//        Long wateringVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.WATERING) ? collect1.get(VehicleConstant.VehicleStatus.WATERING) : 0L;
//
//        //加油车辆数量
//        Long oilingVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OIL_ING) ? collect1.get(VehicleConstant.VehicleStatus.OIL_ING) : 0L;
//
//        homePageDataCountVO.setOilingVehicleCount(oilingVehicleCount);
//        homePageDataCountVO.setRestVehicleCount(restVehicleCount);
//        homePageDataCountVO.setStaticVehicleCount(staticVehicleCount);
//        homePageDataCountVO.setWorkingVehicleCount(workingVehicleCount);
//        homePageDataCountVO.setWateringVehicleCount(wateringVehicleCount);
//        homePageDataCountVO.setShouldWorkVehicleCount(oilingVehicleCount+restVehicleCount+staticVehicleCount+workingVehicleCount+wateringVehicleCount);
//
//
//        String cacheName = CacheNames.HOME_DATA + StringPool.COLON + HomePageController.GET_HOME_DATA_COUNT_DAILY;
//        bladeRedisCache.hSet(cacheName, tenantId, homePageDataCountVO);
//        Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
//        bladeRedisCache.expireAt(cacheName, endToday);
//
//        String cacheName1 = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIG_SCREEN_DATA_COUNT_DAILY;
//        bladeRedisCache.hSet(cacheName1, tenantId, homePageDataCountVO);
//        bladeRedisCache.expireAt(cacheName, endToday);
//
//        return true;
//    }
//
//    @Override
//    public Boolean excuteHomeAlarmListTask(String tenantId) {
//
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST_ALARM_LIST + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                AlarmTask task = new AlarmTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteHomeEventListTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST10_EVENT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                EventTask task = new EventTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteHomeOrderListTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST6_ORDER + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                ResOrderTask task = new ResOrderTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteHomeGarbageAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST_GARBAGE_AMOUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                // TODO
//                LastGarbageAmountTask task = new LastGarbageAmountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteHomeCountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_DATA_COUNT_DAILY + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                HomeDataCountTask task = new HomeDataCountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeHomeCountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_DATA_COUNT_DAILY + ":" + tenantId+"*";
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.homeCountUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeHomeEventListTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST10_EVENT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0){
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId",tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.homeEventListUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeHomeAlarmListTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST_ALARM_LIST + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.homeAlarmListUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeHomeOrderListTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST6_ORDER + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.homeOrderListUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeHomeGarbageAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":homeTask:" + HomePageController.GET_HOME_LAST_GARBAGE_AMOUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.homeGarbageAmountUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteBigScreenDataCountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIG_SCREEN_DATA_COUNT_DAILY + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                HomeDataCountTask task = new HomeDataCountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteBigScreenGarbageAmountByRegionTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION + ":" + tenantId+"*";
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                LastGarbageAmountByRegionTask task = new LastGarbageAmountByRegionTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteBigScreenLastGarbageAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_LAST_GARBAGE_AMOUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                LastGarbageAmountTask task = new LastGarbageAmountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteBigScreenAllRuleAlarmAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                AlarmAmountTask task = new AlarmAmountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excutePolymerizationAlarmAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":polymerizationTask:" + PolymerizationController.GET_ALL_ENTITY_COUNT + ":" + tenantId+"*";
//        log.info("excutePolymerizationAlarmAmountTask 1");
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        String keyParam1 = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":polymerizationTask:" + BigScreenController.GET_BIGSCREEN_ALL_ENTITY_COUNT + ":" + tenantId+"*";
//        log.info("excutePolymerizationAlarmAmountTask 1");
//        List<EntityTaskDto> taskDtos1 =  webSocketTaskService.getTasks(keyParam1);
//        taskDtos.addAll(taskDtos1);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                log.info("excutePolymerizationAlarmAmountTask 2");
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                PolymerizationEntityCountTask task = new PolymerizationEntityCountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//                log.info("excutePolymerizationAlarmAmountTask 3");
//            }
//        }
//
//        return true;
//    }
//
//    @Override
//    public Boolean excuteBigScreenLastAlarmTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_LAST_ALARM_LIST + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                BigScreenAlarmTask task = new BigScreenAlarmTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean excuteBigScreenEventCountByTypeTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_EVENT_COUNT_BY_TYPE + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(ObjectUtil.isNotEmpty(taskDtos) && taskDtos.size() > 0){
//            for (EntityTaskDto entityTaskDto : taskDtos) {
//                WebsocketTask websocketTask = BeanUtil.copy(entityTaskDto, WebsocketTask.class);
//                EventCountTask task = new EventCountTask(websocketTask);
//                baseService.getTaskExecutor().execute(task);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeBigScreenDataCountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIG_SCREEN_DATA_COUNT_DAILY + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.bigScreenCountUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeBigScreenGarbageAmountByRegionTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.bigScreenGarbageAmountByRegionUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeBigScreenLastGarbageAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_LAST_GARBAGE_AMOUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.bigScreenLastGarbageUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeBigScreenAllRuleAlarmAmountTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.bigScreenAllRuleAlarmAmountUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakePolymerizationAlarmAmountUpdateTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":polymerizationTask:" + PolymerizationController.GET_ALL_ENTITY_COUNT + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//
//        String keyParam1 = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":polymerizationTask:" + BigScreenController.GET_BIGSCREEN_ALL_ENTITY_COUNT + ":" + tenantId+"*";
//        List<EntityTaskDto> taskDtos1 =  webSocketTaskService.getTasks(keyParam1);
//
//        taskDtos.addAll(taskDtos1);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.polymerizationAlarmCountUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeBigScreenLastAlarmTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_LAST_ALARM_LIST + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.bigScreenLastAlarmUpdateOutput().send(message);
//        }
//        return true;
//    }
//
//    @Override
//    public Boolean wakeBigScreenEventCountByTypeTask(String tenantId) {
//        String keyParam = WebSocketConsts.CacheNames.USER_WEBSOCKET_TASK + ":bigscreenTask:" + BigScreenController.GET_BIGSCREEN_EVENT_COUNT_BY_TYPE + ":" + tenantId+"*";
//
//        List<EntityTaskDto> taskDtos =  webSocketTaskService.getTasks(keyParam);
//        if(taskDtos.size() > 0) {
//            WebsocketProducerSource websocketProducerSource = getTaskProducerSource();
//            JSONObject messageObj = new JSONObject();
//            messageObj.put("updateDate", TimeUtil.getYYYY_MM_DD_HH_MM_SS(TimeUtil.getSysDate()));
//            messageObj.put("tenantId", tenantId);
//            Message<JSONObject> message = MessageBuilder.withPayload(messageObj).build();
//            websocketProducerSource.bigScreenEventCountByTypeUpdateOutput().send(message);
//        }
//        return true;
//    }
}
