package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: IBigScreenDataClient
 * @Description:此Client用来更新大屏展现的redis数据
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  15:34    zhanglei25          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_WEBSOCKET_NAME,
        fallback = IBigScreenDataClientFallback.class
)
public interface IBigScreenDataClient {

    String API_PREFIX = "/client";
    String UPDATE_BIGSCREEN_COUNT_REDIS = API_PREFIX + "update-bigscreen-count-redis";
    String UPDATE_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION_REDIS = API_PREFIX + "update-bigscreen-garbage-amount-by-region-redis";
    String UPDATE_BIGSCREEN_GARBAGE_AMOUNT_DAILY_REDIS = API_PREFIX + "update-bigscreen-garbage-amount-daily-redis";
    String UPDATE_BIGSCREEN_ALARM_AMOUNT_REDIS = API_PREFIX + "update-bigscreen-alarm-amount-redis";
    String UPDATE_BIGSCREEN_ALARM_LIST_REDIS = API_PREFIX + "update-bigscreen-alarm-list-redis";
    String UPDATE_BIGSCREEN_EVENT_COUNT_BY_TYPE_REDIS = API_PREFIX + "update-bigscreen-event-count-by-type-redis";
    String GET_PERSON_LOCATION_DETAIL = API_PREFIX + "/getPersonLocationDetail";
    String GET_VEHICLE_LOCATION_DETAIL = API_PREFIX + "/getVehicleLocationDetail";

    @GetMapping(UPDATE_BIGSCREEN_COUNT_REDIS)
    @ResponseBody
    R<Boolean> updateBigscreenCountRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_BIGSCREEN_GARBAGE_AMOUNT_BY_REGION_REDIS)
    @ResponseBody
    R<Boolean> updateBigscreenGarbageAmountByRegionRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_BIGSCREEN_GARBAGE_AMOUNT_DAILY_REDIS)
    @ResponseBody
    R<Boolean> updateBigscreenGarbageAmountDailyRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_BIGSCREEN_ALARM_AMOUNT_REDIS)
    @ResponseBody
    R<Boolean> updateBigscreenAlarmAmountRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_BIGSCREEN_ALARM_LIST_REDIS)
    @ResponseBody
    R<Boolean> updateBigscreenAlarmListRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_BIGSCREEN_EVENT_COUNT_BY_TYPE_REDIS)
    @ResponseBody
    R<Boolean> updateBigscreenEventCountByTypeRedis(@RequestParam("tenantId") String tenantId);

    /**
     * 查询人员定位详细信息
     *
     * @param personId
     * @param tenantId
     * @return
     */
    @GetMapping(GET_PERSON_LOCATION_DETAIL)
    R<PersonDetailVO> getPersonLocationDetail(@RequestParam("personId") Long personId, @RequestParam("tenantId") String tenantId);

    /**
     * 查询车辆定位详细信息
     *
     * @param vehicleId
     * @param tenantId
     * @return
     */
    @GetMapping(GET_VEHICLE_LOCATION_DETAIL)
    R<VehicleDetailVO> getVehicleLocationDetail(@RequestParam("vehicleId") Long vehicleId, @RequestParam("tenantId") String tenantId, @RequestParam("coordsSystemType") String coordsSystemType);
}
