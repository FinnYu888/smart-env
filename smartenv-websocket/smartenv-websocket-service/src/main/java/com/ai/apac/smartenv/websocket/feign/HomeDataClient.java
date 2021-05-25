package com.ai.apac.smartenv.websocket.feign;

import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.inventory.feign.IResOrderClient;
import com.ai.apac.smartenv.inventory.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.module.main.vo.AlarmVO;
import com.ai.apac.smartenv.websocket.module.main.vo.EventVO;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.service.IAlarmService;
import com.ai.apac.smartenv.websocket.service.IEventService;
import com.ai.apac.smartenv.websocket.service.ITaskService;
import com.ai.smartenv.cache.util.SmartCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.ai.apac.smartenv.system.cache.EntityCategoryCache.bladeRedisCache;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: HomeDataClient
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  10:52    zhanglei25          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@Slf4j
public class HomeDataClient implements IHomeDataClient{

    private IAlarmService alarmService;

    private IEventService eventService;

    private BladeRedisCache bladeRedisCache;

    private IFacilityClient facilityClient;

    private IResOrderClient resOrderClient;

    private ITaskService taskService;

    private IAlarmInfoClient alarmInfoClient;

    private IEventInfoClient eventInfoClient;

    private MongoTemplate mongoTemplate;

    @Override
    public R<Boolean> updateHomeCountRedis(String tenantId) {
        try {
            HomePageDataCountVO homePageDataCountVO = new HomePageDataCountVO();
            homePageDataCountVO.setAlarmCount(alarmInfoClient.countAlarmInfoAmount(tenantId).getData());
            homePageDataCountVO.setEventCount(eventInfoClient.countEventDaily(tenantId).getData());

            Query query = new Query();

            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("watchDeviceId").exists(true));
            List<BasicPersonDTO> basicPersonDTOList = mongoTemplate.find(query, BasicPersonDTO.class);
            Map<Integer, Long> collect = basicPersonDTOList.stream().collect(Collectors.groupingBy(BasicPersonDTO::getWorkStatus, Collectors.counting()));
            //静值人员数量
            Long staticPersonCount = collect.containsKey(PersonConstant.PersonStatus.OFF_ONLINE_ALARM) ? collect.get(PersonConstant.PersonStatus.OFF_ONLINE_ALARM) : 0L;
            //在岗人员数量
            Long workingPersonCount = collect.containsKey(PersonConstant.PersonStatus.ONLINE) ? collect.get(PersonConstant.PersonStatus.ONLINE) : 0L;
            //休息人员数量
            Long restPersonCount = collect.containsKey(PersonConstant.PersonStatus.OFF_ONLINE) ? collect.get(PersonConstant.PersonStatus.OFF_ONLINE) : 0L;


            homePageDataCountVO.setRestPersonCount(restPersonCount);
            homePageDataCountVO.setStaticPersonCount(staticPersonCount);
            homePageDataCountVO.setWorkingPersonCount(workingPersonCount);
            homePageDataCountVO.setShouldWorkPersonCount(restPersonCount+staticPersonCount+workingPersonCount);



            Query query1 = new Query();

            query1.addCriteria(Criteria.where("tenantId").is(tenantId));
            query1.addCriteria(new Criteria().orOperator(Criteria.where("gpsDeviceId").exists(true),Criteria.where("nvrDeviceId").exists(true),Criteria.where("cvrDeviceId").exists(true)));
            List<BasicVehicleInfoDTO> basicVehicleInfoDTOList = mongoTemplate.find(query1, BasicVehicleInfoDTO.class);
            Map<Integer, Long> collect1 = basicVehicleInfoDTOList.stream().collect(Collectors.groupingBy(BasicVehicleInfoDTO::getWorkStatus, Collectors.counting()));

            //在岗车辆数量
            Long workingVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.ONLINE) ? collect1.get(VehicleConstant.VehicleStatus.ONLINE) : 0L;

            //静值车辆数量
            Long staticVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM) ? collect1.get(VehicleConstant.VehicleStatus.OFF_ONLINE_ALARM) : 0L;

            //休息车辆数量
            Long restVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OFF_ONLINE) ? collect1.get(VehicleConstant.VehicleStatus.OFF_ONLINE) : 0L;

            //加水车辆数量
            Long wateringVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.WATERING) ? collect1.get(VehicleConstant.VehicleStatus.WATERING) : 0L;

            //加油车辆数量
            Long oilingVehicleCount = collect1.containsKey(VehicleConstant.VehicleStatus.OIL_ING) ? collect1.get(VehicleConstant.VehicleStatus.OIL_ING) : 0L;

            homePageDataCountVO.setOilingVehicleCount(oilingVehicleCount);
            homePageDataCountVO.setRestVehicleCount(restVehicleCount);
            homePageDataCountVO.setStaticVehicleCount(staticVehicleCount);
            homePageDataCountVO.setWorkingVehicleCount(workingVehicleCount);
            homePageDataCountVO.setWateringVehicleCount(wateringVehicleCount);
            homePageDataCountVO.setShouldWorkVehicleCount(oilingVehicleCount+restVehicleCount+staticVehicleCount+workingVehicleCount+wateringVehicleCount);


            String cacheName = CacheNames.HOME_DATA + StringPool.COLON + HomePageController.GET_HOME_DATA_COUNT_DAILY;
            bladeRedisCache.hSet(cacheName, tenantId, homePageDataCountVO);
            Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
            bladeRedisCache.expireAt(cacheName, endToday);

//            taskService.wakeHomeCountTask(tenantId);
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return R.data(true);
    }

    @Override
    public R<Boolean> updateHomeAlarmListRedis(String tenantId) {
        List<AlarmVO> alarmVOList = new ArrayList<AlarmVO>();
        List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOList = new ArrayList<AlarmInfoHandleInfoVO>();
        try {
            Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
            Date endTime = DateTime.now();

            Future<List<AlarmInfoHandleInfoVO>> alarmInfoHandleInfoVOListResult = alarmService.listAlarmInfoByCondition(null, AlarmConstant.IsHandle.HANDLED_NO, startTime, endTime,null, null, AlarmConstant.AlarmLevel.EMERGENCY,10,tenantId);
            if (alarmInfoHandleInfoVOListResult != null && alarmInfoHandleInfoVOListResult.get() != null) {
                alarmInfoHandleInfoVOList = alarmInfoHandleInfoVOListResult.get();
            }

            if(alarmInfoHandleInfoVOList.size() > 0 ){
                alarmInfoHandleInfoVOList.forEach(alarmInfoHandleInfoVO -> {
                    AlarmVO alarmVO = new AlarmVO();
                    alarmVO.setId(alarmInfoHandleInfoVO.getId().toString());
                    alarmVO.setAlarmType(alarmInfoHandleInfoVO.getAlarmTypeName());
                    alarmVO.setAlarmMessage(alarmInfoHandleInfoVO.getAlarmMessage());
                    alarmVO.setAlarmDate(TimeUtil.getYYYY_MM_DD_HH_MM_SS(alarmInfoHandleInfoVO.getAlarmTime()));
                    alarmVO.setEntityType(alarmInfoHandleInfoVO.getEntityType().toString());
                    alarmVOList.add(alarmVO);
                });

                String cacheName = CacheNames.HOME_DATA + StringPool.COLON + HomePageController.GET_HOME_LAST_ALARM_LIST;
                bladeRedisCache.hSet(cacheName, tenantId, alarmVOList);
                Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
                bladeRedisCache.expireAt(cacheName, endToday);

//                taskService.wakeHomeAlarmListTask(tenantId);

            }} catch (Exception ex) {
                throw new ServiceException(ResultCode.FAILURE, ex);
            }
            return R.data(true);
    }

    @Override
    public R<Boolean> updateHomeEventListRedis(String tenantId) {
        List<EventVO> eventVOList = new ArrayList<EventVO>();
        List<EventInfoVO> eventInfoVOList = new ArrayList<EventInfoVO>();
        try {
            Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
            Date endTime = DateTime.now();
            eventInfoVOList = eventService.listEvevtInfoByParam(startTime,endTime, EventConstant.Event_Status.HANDLE_1, EventConstant.Event_LEVEL.LEVEL_1,10,tenantId);

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

            String cacheName = CacheNames.HOME_DATA + StringPool.COLON + HomePageController.GET_HOME_LAST10_EVENT;
            bladeRedisCache.hSet(cacheName, tenantId, eventVOList);
            Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
            bladeRedisCache.expireAt(cacheName, endToday);

//            taskService.wakeHomeEventListTask(tenantId);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return R.data(true);
    }

    @Override
    public R<Boolean> updateHomeOrderListRedis(String tenantId,String userId) {
        List<ResOrder4HomeVO> resOrder4HomeVOList = resOrderClient.getlastOrders(tenantId,userId).getData();
        String cacheName = CacheNames.HOME_DATA + StringPool.COLON + HomePageController.GET_HOME_LAST6_ORDER;
        bladeRedisCache.hSet(cacheName, tenantId, resOrder4HomeVOList);
        Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
        bladeRedisCache.expireAt(cacheName, endToday);

//        taskService.wakeHomeOrderListTask(tenantId);


        return R.data(true);
    }

    @Override
    public R<Boolean> updateHomeGarbageAmountRedis(String tenantId) {
        Integer days = 30;
        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = facilityClient.getLastDaysGarbageAmount(days,tenantId).getData();
        String cacheName = CacheNames.HOME_DATA + StringPool.COLON + HomePageController.GET_HOME_LAST_GARBAGE_AMOUNT + StringPool.COLON + days;
        bladeRedisCache.hSet(cacheName, tenantId, lastDaysGarbageAmountVOList);
        Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
        bladeRedisCache.expireAt(cacheName, endToday);

//        taskService.wakeHomeGarbageAmountTask(tenantId);

        return R.data(true);
    }
}
