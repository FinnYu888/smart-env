package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.enums.PersonStatusImgEnum;
import com.ai.apac.smartenv.common.enums.VehicleStatusImgEnum;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.GPSUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IRealTimeStatusClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.*;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.*;
import com.ai.apac.smartenv.websocket.module.main.vo.*;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonPositionVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonStatusCntVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonTrackRealTimeVO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehiclePositionVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleStatusCntVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleTrackRealTimeVO;
import com.ai.apac.smartenv.websocket.service.*;
import com.ai.apac.smartenv.websocket.util.WebSocketUtil;
import com.mongodb.DBCollection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: StreageService
 * @Description: 策略service
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/9
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/9  16:43    panfeng          v1.0.0             修改原因
 */
@Service
@Slf4j
@AllArgsConstructor
public class StreageService implements IStreageService {


    private SimpMessagingTemplate wsTemplate;
    private IWebSocketTaskService webSocketTaskService;
    private IOmnicService omnicService;
    private IVehicleService vehicleService;
    private IPersonService personService;
    private MongoTemplate mongoTemplate;
    private DeviceService deviceService;
    private IAlarmInfoClient alarmInfoClient;
    private AlarmService alarmService;
    private IEventService eventService;
    private IResOrderService resOrderService;
    private IFacilityService facilityService;
    private IPolymerizationService polymerizationService;

    private IRealTimeStatusClient realTimeStatusClient;

    /**
     * 处理首页各个数量变化推送
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handleHomePageCountData(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, HomePageController.GET_HOME_DATA_COUNT_DAILY);

        HomePageDataCountVO homePageCountData = omnicService.getHomePageCountData(tenantId);
        homePageCountData.setActionName(HomePageController.GET_HOME_DATA_COUNT_DAILY);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            homePageCountData.setTopicName(websocketTask.getTopic());
            homePageCountData.setTaskId(String.valueOf(websocketTask.getId()));
            R<HomePageDataCountVO> data = R.data(homePageCountData);
            send(websocketTask, data);
        }
    }

    /**
     * 处理大屏各个数量变化推送
     */
    @Override
    public void handleBigScreenCountData(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIG_SCREEN_DATA_COUNT_DAILY);
        HomePageDataCountVO homePageCountData = omnicService.getHomePageCountData(tenantId);
        homePageCountData.setActionName(BigScreenController.GET_BIG_SCREEN_DATA_COUNT_DAILY);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            homePageCountData.setTopicName(websocketTask.getTopic());
            homePageCountData.setTaskId(String.valueOf(websocketTask.getId()));
            R<HomePageDataCountVO> data = R.data(homePageCountData);
            send(websocketTask, data);
        }

    }

    /**
     * 处理大屏告警数据今日汇总
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handleBigScreenAlarmRuleCount(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT);
        AlarmAmountVO alarmAmountVO = new AlarmAmountVO();
        AlarmAmountDTO alarmAmountDTO = alarmService.getAllRuleAlarmAmount(AuthUtil.getTenantId());
        alarmAmountVO.setAlarmAmountDTO(alarmAmountDTO);
        alarmAmountVO.setActionName(BigScreenController.GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT);
        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            alarmAmountVO.setTopicName(websocketTask.getTopic());
            alarmAmountVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<AlarmAmountVO> data = R.data(alarmAmountVO);
            send(websocketTask, data);
        }

    }


    /**
     * 处理大屏最近10条告警信息
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handleBigScreenAlarmList(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIGSCREEN_LAST_ALARM_LIST);

        try {
            AlarmInfoScreenViewVO alarmInfoScreenViewVO = new AlarmInfoScreenViewVO();
            List<AlarmInfoScreenViewDTO> alarmInfoScreenViewDTOList = alarmService.getBigScreenLastAlarmList(AuthUtil.getTenantId());
            alarmInfoScreenViewVO.setAlarmInfoScreenViewDTOList(alarmInfoScreenViewDTOList);
            alarmInfoScreenViewVO.setActionName(BigScreenController.GET_BIGSCREEN_LAST_ALARM_LIST);
            for (WebsocketTask websocketTask : tenantTasksByTypes) {
                alarmInfoScreenViewVO.setTopicName(websocketTask.getTopic());
                alarmInfoScreenViewVO.setTaskId(String.valueOf(websocketTask.getId()));
                R<AlarmInfoScreenViewVO> viewVOR = R.data(alarmInfoScreenViewVO);
                send(websocketTask, viewVOR);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void handleBigScreenEventCountByType(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIGSCREEN_EVENT_COUNT_BY_TYPE);

        List<EventTypeCountDTO> eventTypeCountDTOList = eventService.getEventCountByType(tenantId);
        EventTypeCountVO eventTypeCountVO = new EventTypeCountVO();

        eventTypeCountVO.setActionName(HomePageController.GET_HOME_LAST_ALARM_LIST);
        eventTypeCountVO.setEventTypeCountDTOList(eventTypeCountDTOList);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            eventTypeCountVO.setTopicName(websocketTask.getTopic());
            eventTypeCountVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<EventTypeCountVO> data = R.data(eventTypeCountVO);
            send(websocketTask, data);
        }
    }

    @Override
    public void handleHomeLast10AlarmList(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, HomePageController.GET_HOME_LAST_ALARM_LIST);

        List<AlarmVO> alarmVOList = alarmService.getHomeAlarmList(tenantId);
        Last10AlarmVO last10AlarmVO = new Last10AlarmVO();

        last10AlarmVO.setActionName(HomePageController.GET_HOME_LAST_ALARM_LIST);
        last10AlarmVO.setAlarmVOList(alarmVOList);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            last10AlarmVO.setTopicName(websocketTask.getTopic());
            last10AlarmVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<Last10AlarmVO> data = R.data(last10AlarmVO);
            send(websocketTask, data);
        }
    }

    @Override
    public void handleHomeLastGarbageList(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, HomePageController.GET_HOME_LAST_GARBAGE_AMOUNT);

        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = facilityService.getLastGarbageAmount(30, tenantId);
        LastGarbageVO lastGarbageVO = new LastGarbageVO();

        lastGarbageVO.setActionName(HomePageController.GET_HOME_LAST_GARBAGE_AMOUNT);
        lastGarbageVO.setLastDaysGarbageAmountVOList(lastDaysGarbageAmountVOList);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            lastGarbageVO.setTopicName(websocketTask.getTopic());
            lastGarbageVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<LastGarbageVO> data = R.data(lastGarbageVO);
            send(websocketTask, data);
        }
    }

    @Override
    public void handleBigScreenLastGarbageAmountByRegion(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION);

        List<LastDaysRegionGarbageAmountVO> lastDaysGarbageAmountVOList = facilityService.getLastGarbageAmountByRegion(tenantId);
        LastGarbageAmountByRegionVO lastGarbageAmountByRegionVO = new LastGarbageAmountByRegionVO();

        lastGarbageAmountByRegionVO.setActionName(HomePageController.GET_HOME_LAST_GARBAGE_AMOUNT);
        lastGarbageAmountByRegionVO.setLastDaysRegionGarbageAmountList(lastDaysGarbageAmountVOList);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            lastGarbageAmountByRegionVO.setTopicName(websocketTask.getTopic());
            lastGarbageAmountByRegionVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<LastGarbageAmountByRegionVO> data = R.data(lastGarbageAmountByRegionVO);
            send(websocketTask, data);
        }
    }

    @Override
    public void handleBigScreenLastGarbageList(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIGSCREEN_LAST_GARBAGE_AMOUNT);

        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = facilityService.getLastGarbageAmount(7, tenantId);
        LastGarbageVO lastGarbageVO = new LastGarbageVO();

        lastGarbageVO.setActionName(BigScreenController.GET_BIGSCREEN_LAST_GARBAGE_AMOUNT);
        lastGarbageVO.setLastDaysGarbageAmountVOList(lastDaysGarbageAmountVOList);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            lastGarbageVO.setTopicName(websocketTask.getTopic());
            lastGarbageVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<LastGarbageVO> data = R.data(lastGarbageVO);
            send(websocketTask, data);
        }
    }

    @Override
    public void handleHomeLast10Event(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, HomePageController.GET_HOME_LAST10_EVENT);

        Last10EventVO last10EventVO = new Last10EventVO();
        List<EventVO> eventVOList = eventService.getLastEventList(tenantId);
        last10EventVO.setEventVOList(eventVOList);

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            last10EventVO.setTopicName(websocketTask.getTopic());
            last10EventVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<Last10EventVO> data = R.data(last10EventVO);
            send(websocketTask, data);
        }

    }

    @Override
    public void handleHomeLast6Order(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, HomePageController.GET_HOME_LAST6_ORDER);

        Last6OrderVO last6OrderVO = new Last6OrderVO();

        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            List<ResOrder4HomeVO> resOrder4HomeVOList = resOrderService.getLast6Order(tenantId,websocketTask.getUserId());
            last6OrderVO.setResOrder4HomeVOList(resOrder4HomeVOList);
            last6OrderVO.setTopicName(websocketTask.getTopic());
            last6OrderVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<Last6OrderVO> data = R.data(last6OrderVO);
            send(websocketTask, data);
        }
    }

    /**
     * 处理人员监控弹框详情实时推送
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handlePersonDetailData(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        String personId = baseWsMonitorEventDTO.getEventObject();
        if (StringUtil.isEmpty(personId)){
            return;
        }
        List<WebsocketTask> personDetail = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), PersonController.GET_PERSON_INFO_REALTIME, personId);
        PersonDetailVO personDetailInfo = personService.getPersonDetailRealTime(personId, baseWsMonitorEventDTO.getTenantId());
        personDetailInfo.setActionName(PersonController.GET_PERSON_INFO_REALTIME);
        for (WebsocketTask websocketTask : personDetail) {
            personDetailInfo.setTaskId(websocketTask.getId().toString());
            personDetailInfo.setTopicName(websocketTask.getTopic());
            send(websocketTask, R.data(personDetailInfo));
        }
    }


    /**
     * 处理车辆监控弹框详情实时推送
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handleVehicleDetailData(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        String vehicleId = baseWsMonitorEventDTO.getEventObject();
        if (StringUtil.isEmpty(vehicleId)){
            return;
        }
        List<WebsocketTask> vehicleDetail = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), VehicleController.GET_VEHICLE_INFO_REALTIME, vehicleId);

        VehicleDetailVO vehicleDetailVO = vehicleService.getVehicleDetailRealTime(Long.valueOf(baseWsMonitorEventDTO.getEventObject()), baseWsMonitorEventDTO.getTenantId(), BaiduMapUtils.CoordsSystem.BD09LL);
        vehicleDetailVO.setActionName(VehicleController.GET_VEHICLE_INFO_REALTIME);
        for (WebsocketTask websocketTask : vehicleDetail) {
            vehicleDetailVO.setTaskId(websocketTask.getId().toString());
            vehicleDetailVO.setTopicName(websocketTask.getTopic());
            send(websocketTask, R.data(vehicleDetailVO));
        }
    }


    /**
     * 处理综合监控数量
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handlePolymerizationCountData(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();
        List<WebsocketTask> allTask = new ArrayList<>();

        List<WebsocketTask> tenantTasksByTypes = webSocketTaskService.getTenantTasksByTypes(tenantId, PolymerizationController.GET_ALL_ENTITY_COUNT);
        List<WebsocketTask> bigdataScreenTasks = webSocketTaskService.getTenantTasksByTypes(tenantId, BigScreenController.GET_BIGSCREEN_ALL_ENTITY_COUNT);

        if (CollectionUtil.isNotEmpty(tenantTasksByTypes)) {
            allTask.addAll(tenantTasksByTypes);

        }
        if (CollectionUtil.isNotEmpty(bigdataScreenTasks)) {
            allTask.addAll(bigdataScreenTasks);
        }

        if (CollectionUtil.isEmpty(allTask)) {
            return;
        }

        PolymerizationCountVO polymerizationCountVO = polymerizationService.getPolymerizationEntityCount(tenantId);
        polymerizationCountVO.setActionName(PolymerizationController.GET_ALL_ENTITY_COUNT);
        for (WebsocketTask websocketTask : tenantTasksByTypes) {
            polymerizationCountVO.setTaskId(websocketTask.getId().toString());
            polymerizationCountVO.setTopicName(websocketTask.getTopic());
            send(websocketTask, R.data(polymerizationCountVO));
        }
    }


    /**
     * 人员历史轨迹实时推送的逻辑
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handlePersonTrackTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        try {
            String personId = baseWsMonitorEventDTO.getEventObject();
            if (StringUtil.isEmpty(personId)){
                return;
            }

            // 查询所有人员历史轨迹的TASK
            List<WebsocketTask> trackTask = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), PersonController.GET_PERSON_TRACK_REALTIME, baseWsMonitorEventDTO.getEventObject());

            if (CollectionUtil.isEmpty(trackTask)) {
                return;
            }
            Query query = Query.query(Criteria.where("id").is(Long.parseLong(personId)));
            //查询人员基本信息
            BasicPersonDTO one = mongoTemplate.findOne(query, BasicPersonDTO.class);
            PersonTrackRealTimeVO personTrackRealTimeVO = new PersonTrackRealTimeVO();
            //获取历史轨迹
            Future<List<PositionDTO>> positionListResult = deviceService.getDeviceTrackRealTime(one.getWatchDeviceCode());

            R<PersonTrackRealTimeVO> result = null;
            if (positionListResult == null || positionListResult.get() == null || positionListResult.get().size() == 0) {
                log.info("找不到人员+历史轨迹推送内容：" + one.getPersonName());
                return;
            }
            personTrackRealTimeVO.setPositionList(positionListResult.get());
            //推送历史轨迹
            for (WebsocketTask websocketTask : trackTask) {

                personTrackRealTimeVO.setTopicName(websocketTask.getTopic());
                personTrackRealTimeVO.setActionName(websocketTask.getTaskType());
                personTrackRealTimeVO.setTaskId(String.valueOf(websocketTask.getId()));
                R<PersonTrackRealTimeVO> data = R.data(personTrackRealTimeVO);
                send(websocketTask, data);
            }
        } catch (Exception e) {
            log.warn("", e);
        }


    }


    /**
     * 处理车辆实时位置的task
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handlePersonPositionTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {

        if (StringUtil.isEmpty(baseWsMonitorEventDTO.getEventObject())){
            return;
        }

        // 查询人员监控实时位置，大屏人员实时位置的Task
        List<WebsocketTask> personPosition = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), PersonController.GET_PERSON_POSITION, baseWsMonitorEventDTO.getEventObject());
        List<WebsocketTask> polymerizationPosition = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), PolymerizationController.GET_ALL_ENTITY_POSITION, baseWsMonitorEventDTO.getEventObject());
        List<WebsocketTask> allTask = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(personPosition)) {
            allTask.addAll(personPosition);
        }
        if (CollectionUtil.isNotEmpty(polymerizationPosition)) {
            allTask.addAll(polymerizationPosition);
        }
        if (CollectionUtil.isEmpty(allTask)) {
            return;
        }

        //从Mongo 中查询数据
        Query query = Query.query(Criteria.where("id").is(Long.parseLong(baseWsMonitorEventDTO.getEventObject())));
        BasicPersonDTO one = mongoTemplate.findOne(query, BasicPersonDTO.class);
        if (one == null) {
            return;
        }
        //准备数据
        double[] doubles = GPSUtil.gcj02_To_Bd09(Double.parseDouble(one.getLat()), Double.parseDouble(one.getLng()));
        PersonPositionVO personMonitorVO = new PersonPositionVO();

        personMonitorVO.setPersonId(one.getId().toString());
        personMonitorVO.setLat(String.valueOf(doubles[0]));
        personMonitorVO.setLng(String.valueOf(doubles[1]));
//        String descByValue = PersonStatusImgEnum.getDescByValue(one.getWorkStatus());
//        String ossObjLink = webSocketTaskService.getOssObjLink(descByValue);

        String personStatusImg = PersonCache.getPersonStatusImg(one.getWorkStatus());

        personMonitorVO.setIcon(personStatusImg);
        personMonitorVO.setDeviceCode(personMonitorVO.getDeviceCode());

        for (WebsocketTask websocketTask : allTask) {
            try {
                //初始化其他数据
                personMonitorVO.setTopicName(websocketTask.getTopic());
                personMonitorVO.setActionName(WebSocketConsts.TaskType.pushPersonPositionAction);
                personMonitorVO.setTaskId(String.valueOf(websocketTask.getId()));
                R<PersonPositionVO> data = R.data(personMonitorVO);
                //推送
                send(websocketTask, data);
            } catch (Exception e) { //异常要在循环内部处理掉，保证各个推送的任务不受影响
                log.warn("向客户端推送人员位置变更消息失败,人员：" + one.getPersonName() + "  客户端:" + websocketTask.getSessionId(), e);
            }
        }
    }

    /**
     * 处理人员监控推送任务
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handlePersonMonitorCountTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();

        List<WebsocketTask> tasks = webSocketTaskService.getTenantTasksByTypes(tenantId, PersonController.GET_PERSON_STATUS_CNT);
        if (CollectionUtil.isEmpty(tasks)) {
            return;
        }

        PersonStatusCntVO statusCntVO = new PersonStatusCntVO();
        StatusCount statusCount = realTimeStatusClient.getAllPersonStatusCount(tenantId).getData();

        statusCntVO.setAlarmCnt(statusCount.getAlarm());
        statusCntVO.setWorkingCnt(statusCount.getWorking());
        statusCntVO.setUnWorkingCnt(statusCount.getDeparture());
        statusCntVO.setRestCnt(statusCount.getSitBack());
        statusCntVO.setVacationCnt(statusCount.getVacationCnt());
        statusCntVO.setUnArrangeCnt(statusCount.getUnArrangeCnt());

        for (WebsocketTask websocketTask : tasks) {
            statusCntVO.setActionName(websocketTask.getTaskType());
            statusCntVO.setTopicName(websocketTask.getTopic());
            statusCntVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<PersonStatusCntVO> data = R.data(statusCntVO);
            send(websocketTask, data);
        }
    }


    @Override
    public void handleVehicleTrackTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        try {
            if (StringUtil.isEmpty(baseWsMonitorEventDTO.getEventObject())){
                return;
            }

            // 查询所有车辆历史轨迹的TASK
            List<WebsocketTask> trackTask = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), VehicleController.GET_VEHICLE_TRACK_REALTIME, baseWsMonitorEventDTO.getEventObject());

            if (CollectionUtil.isEmpty(trackTask)) {
                return;
            }
            Query query = Query.query(Criteria.where("id").is(Long.parseLong(baseWsMonitorEventDTO.getEventObject())));
            //查询车辆基本信息
            BasicVehicleInfoDTO one = mongoTemplate.findOne(query, BasicVehicleInfoDTO.class);
            VehicleTrackRealTimeVO vehicleTrackVO = new VehicleTrackRealTimeVO();
            //获取历史轨迹
            Future<List<PositionDTO>> positionListResult = deviceService.getDeviceTrackRealTime(one.getGpsDeviceCode());

            R<VehicleTrackRealTimeVO> result = null;
            if (positionListResult == null || positionListResult.get() == null || positionListResult.get().size() == 0) {
                log.info("找不到车辆历史轨迹推送内容：" + one.getPlateNumber());
                return;
            }
            vehicleTrackVO.setPositionList(positionListResult.get());
            //推送历史轨迹
            for (WebsocketTask websocketTask : trackTask) {
                try {
                    vehicleTrackVO.setTopicName(websocketTask.getTopic());
                    vehicleTrackVO.setActionName(websocketTask.getTaskType());
                    vehicleTrackVO.setTaskId(String.valueOf(websocketTask.getId()));
                    R<VehicleTrackRealTimeVO> data = R.data(vehicleTrackVO);
                    send(websocketTask, data);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            log.warn("", e);
        }


    }


    /**
     * 处理监控实时位置
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handleVehiclePositionTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        if (StringUtil.isEmpty(baseWsMonitorEventDTO.getEventObject())){
            return;
        }

        // 1.查到所有session。然后调用feign。取数据，然后推给前端

        List<WebsocketTask> allTask = new ArrayList<>();
        //查询车辆监控实时位置，大屏实时位置的task（复用的）
        List<WebsocketTask> vehiclePosition = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), VehicleController.GET_VEHICLE_POSITION, baseWsMonitorEventDTO.getEventObject());
        //查询综合监控车辆实时位置的task
        List<WebsocketTask> polymerization = webSocketTaskService.getTenantEntityTasksByTypes(baseWsMonitorEventDTO.getTenantId(), PolymerizationController.GET_ALL_ENTITY_POSITION, baseWsMonitorEventDTO.getEventObject());
        if (CollectionUtil.isNotEmpty(vehiclePosition)) {
            allTask.addAll(vehiclePosition);
        }
        if (CollectionUtil.isNotEmpty(polymerization)) {
            allTask.addAll(polymerization);
        }
        if (CollectionUtil.isEmpty(allTask)) {
            return;
        }
        //从Mongo 中查询数据
        Query query = Query.query(Criteria.where("id").is(Long.parseLong(baseWsMonitorEventDTO.getEventObject())));
        BasicVehicleInfoDTO one = mongoTemplate.findOne(query, BasicVehicleInfoDTO.class);
        //准备数据
        VehiclePositionVO vehiclePositionVO = new VehiclePositionVO();
        vehiclePositionVO.setVehicleId(one.getId().toString());
        //将坐标系转为BD09
        double[] doubles = GPSUtil.gcj02_To_Bd09(Double.parseDouble(one.getLat()), Double.parseDouble(one.getLng()));
        vehiclePositionVO.setLat(String.valueOf(doubles[0]));
        vehiclePositionVO.setLng(String.valueOf(doubles[1]));
        //初始化其他数据
        vehiclePositionVO.setStatus(one.getWorkStatus());
        vehiclePositionVO.setStatusName(VehicleStatusImgEnum.getDescByValue(one.getWorkStatus()));
        String statusImg = VehicleCache.getVehicleStatusImg(one.getWorkStatus());
        vehiclePositionVO.setIcon(statusImg);
        vehiclePositionVO.setDeviceCode(one.getGpsDeviceCode());
        vehiclePositionVO.setDeviceId(one.getGpsDeviceId());

        vehiclePositionVO.setActionName(WebSocketConsts.TaskType.pushVehiclePositionAction);

        for (WebsocketTask websocketTask : allTask) {
            try {
                //初始化其他数据
                vehiclePositionVO.setTopicName(websocketTask.getTopic());
                vehiclePositionVO.setTaskId(String.valueOf(websocketTask.getId()));
                R<VehiclePositionVO> data = R.data(vehiclePositionVO);
                //推送
                send(websocketTask, data);
            } catch (Exception e) { //异常要在循环内部处理掉，保证各个推送的任务不受影响
                log.warn("向客户端推送车辆变更消息失败,车辆：" + one.getPlateNumber() + "  客户端:" + websocketTask.getSessionId(), e);
            }
        }
    }

    /**
     * 处理人员监控推送任务
     *
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void handleVehicleMonitorCountTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        String tenantId = baseWsMonitorEventDTO.getTenantId();

        List<WebsocketTask> tasks = webSocketTaskService.getTenantTasksByTypes(tenantId, VehicleController.GET_VEHICLE_STATUS_CNT);
        if (CollectionUtil.isEmpty(tasks)) {
            return;
        }
        log.info("处理车辆监控推送任务,内容:"+JSONUtil.toJsonStr(baseWsMonitorEventDTO));

        VehicleStatusCntVO statusCntVO = new VehicleStatusCntVO();
        StatusCount statusCount = realTimeStatusClient.getAllVehicleStatusCount(tenantId).getData();

        statusCntVO.setAlarmCnt(statusCount.getAlarm());
        statusCntVO.setWorkingCnt(statusCount.getWorking());
        statusCntVO.setUnWorkingCnt(statusCount.getDeparture());
        statusCntVO.setRestCnt(statusCount.getSitBack());
        statusCntVO.setVacationCnt(statusCount.getVacationCnt());
        statusCntVO.setUnArrangeCnt(statusCount.getUnArrangeCnt());
        statusCntVO.setWaterCnt(statusCount.getWaterCnt());
        statusCntVO.setOilCnt(statusCount.getOilCnt());
        statusCntVO.setVacationCnt(statusCount.getVacationCnt());
        statusCntVO.setUnArrangeCnt(statusCount.getUnArrangeCnt());
        for (WebsocketTask websocketTask : tasks) {
            statusCntVO.setActionName(websocketTask.getTaskType());
            statusCntVO.setTopicName(websocketTask.getTopic());
            statusCntVO.setTaskId(String.valueOf(websocketTask.getId()));
            R<VehicleStatusCntVO> data = R.data(statusCntVO);
            send(websocketTask, data);
        }
    }


    /**
     * 推送指定任务的消息
     *
     * @param sendContent
     */
    private <U> void send(WebsocketTask websocketTask, R<U> sendContent) {
        if (sendContent == null || sendContent.getData() == null || websocketTask == null || StringUtil.isBlank(websocketTask.getSessionId())) {
            return;
        }
        log.info("发送给客户端实时更新消息,sessionID:"+websocketTask.getSessionId()+",content:"+JSONUtil.toJsonStr(sendContent));
        wsTemplate.convertAndSendToUser(websocketTask.getSessionId(), websocketTask.getTopic(), JSONUtil.toJsonStr(sendContent), WebSocketUtil.createHeaders(websocketTask.getSessionId()));
    }


}
