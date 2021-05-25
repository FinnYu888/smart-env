package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.constant.VehicleConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.service.IPersonService;
import com.ai.apac.smartenv.websocket.service.ITaskService;
import com.ai.apac.smartenv.websocket.service.IVehicleService;
import lombok.AllArgsConstructor;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: BigScreenDataClient
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  15:52    zhanglei25          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@AllArgsConstructor
public class BigScreenDataClient implements IBigScreenDataClient {

    private IFacilityClient facilityClient;

    private IAlarmInfoClient alarmInfoClient;

    private IEventInfoClient eventInfoClient;

    private BladeRedisCache bladeRedisCache;

    private ITaskService taskService;

    private MongoTemplate mongoTemplate;

    private IVehicleService vehicleService;

    private IPersonService personService;


    @Override
    public R<Boolean> updateBigscreenCountRedis(String tenantId) {
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
            homePageDataCountVO.setShouldWorkPersonCount(restPersonCount + staticPersonCount + workingPersonCount);

            Query query1 = new Query();

            query1.addCriteria(Criteria.where("tenantId").is(tenantId));
            query1.addCriteria(new Criteria().orOperator(Criteria.where("gpsDeviceId").exists(true), Criteria.where("nvrDeviceId").exists(true), Criteria.where("cvrDeviceId").exists(true)));
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
            homePageDataCountVO.setShouldWorkVehicleCount(oilingVehicleCount + restVehicleCount + staticVehicleCount + workingVehicleCount + wateringVehicleCount);


            String cacheName = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIG_SCREEN_DATA_COUNT_DAILY;
            bladeRedisCache.hSet(cacheName, tenantId, homePageDataCountVO);
            Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
            bladeRedisCache.expireAt(cacheName, endToday);

//            taskService.wakeBigScreenDataCountTask(tenantId);
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return R.data(true);
    }

    @Override
    public R<Boolean> updateBigscreenGarbageAmountByRegionRedis(String tenantId) {
        Integer days = 7;
        List<LastDaysRegionGarbageAmountVO> lastDaysRegionGarbageAmountVOList = facilityClient.getLastDaysGarbageAmountByRegion(days, tenantId).getData();
        String cacheName = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION + StringPool.COLON + days;
        bladeRedisCache.hSet(cacheName, tenantId, lastDaysRegionGarbageAmountVOList);

//        taskService.wakeBigScreenGarbageAmountByRegionTask(tenantId);


        return R.data(true);
    }

    @Override
    public R<Boolean> updateBigscreenGarbageAmountDailyRedis(String tenantId) {
        Integer days = 7;
        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = facilityClient.getLastDaysGarbageAmount(days, tenantId).getData();
        String cacheName = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIGSCREEN_LAST_GARBAGE_AMOUNT + StringPool.COLON + days;
        bladeRedisCache.hSet(cacheName, tenantId, lastDaysGarbageAmountVOList);

//        taskService.wakeBigScreenLastGarbageAmountTask(tenantId);

        return R.data(true);
    }

    @Override
    public R<Boolean> updateBigscreenAlarmAmountRedis(String tenantId) {
        AlarmAmountVO alarmAmountVO = alarmInfoClient.countAllRuleAlarmAmount(tenantId).getData();
        String cacheName = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIGSCREEN_ALL_RULE_ALARM_AMOUNT;
        bladeRedisCache.hSet(cacheName, tenantId, alarmAmountVO);
        Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
        bladeRedisCache.expireAt(cacheName, endToday);

//        taskService.wakeBigScreenAllRuleAlarmAmountTask(tenantId);


        return R.data(true);
    }

    @Override
    public R<Boolean> updateBigscreenAlarmListRedis(String tenantId) {
        Long alarmNums = 5L;
        List<AlarmInfoScreenViewVO> alarmInfoScreenViewVOList = alarmInfoClient.getBigScreenAlarmList(tenantId, alarmNums).getData();
        String cacheName = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIGSCREEN_LAST_ALARM_LIST;
        bladeRedisCache.hSet(cacheName, tenantId, alarmInfoScreenViewVOList);
        Date endToday = TimeUtil.getDateOfCurrentEndDay(new Date());
        bladeRedisCache.expireAt(cacheName, endToday);

//        taskService.wakeBigScreenLastAlarmTask(tenantId);

        return R.data(true);
    }

    @Override
    public R<Boolean> updateBigscreenEventCountByTypeRedis(String tenantId) {
        Integer days = 7;
        List<EventTypeCountVO> eventTypeCountVOList = eventInfoClient.countEventGroupByType(tenantId, days).getData();
        String cacheName = CacheNames.BIGSCREEN_DATA + StringPool.COLON + BigScreenController.GET_BIGSCREEN_EVENT_COUNT_BY_TYPE;
        bladeRedisCache.hSet(cacheName, tenantId, eventTypeCountVOList);
//        taskService.wakeBigScreenEventCountByTypeTask(tenantId);

        return R.data(true);
    }

    /**
     * 查询人员定位详细信息
     *
     * @param personId
     * @return
     */
    @Override
    @GetMapping(GET_PERSON_LOCATION_DETAIL)
    public R<PersonDetailVO> getPersonLocationDetail(@RequestParam("personId") Long personId, @RequestParam("tenantId") String tenantId) {
        return R.data(personService.getPersonDetailRealTime(String.valueOf(personId),tenantId));
    }

    /**
     * 查询车辆定位详细信息
     *
     * @param vehicleId
     * @param coordsSystemType
     * @return
     */
    @Override
    @GetMapping(GET_VEHICLE_LOCATION_DETAIL)
    public R<VehicleDetailVO> getVehicleLocationDetail(@RequestParam("vehicleId") Long vehicleId, @RequestParam("tenantId") String tenantId, @RequestParam("coordsSystemType") String coordsSystemType) {
        if(coordsSystemType.equalsIgnoreCase("gcj02")){
            return R.data(vehicleService.getVehicleDetailRealTime(vehicleId,tenantId, BaiduMapUtils.CoordsSystem.GC02));
        }else{
            return R.data(vehicleService.getVehicleDetailRealTime(vehicleId,tenantId, BaiduMapUtils.CoordsSystem.BD09LL));
        }
    }
}
