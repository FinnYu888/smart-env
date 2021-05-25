package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: IHomeDataClient
 * @Description:此Client用来更新首页展现的redis数据
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  10:02    zhanglei25          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_WEBSOCKET_NAME,
        fallback = IHomeDataClientFallback.class
)
public interface IHomeDataClient {

    String API_PREFIX = "/client";
    String UPDATE_HOME_COUNT_REDIS = API_PREFIX + "update-home-count-redis";
    String UPDATE_HOME_ALARM_LIST_REDIS = API_PREFIX + "update-home-alarm-list-redis";
    String UPDATE_HOME_EVENT_LIST_REDIS = API_PREFIX + "update-home-event-list-redis";
    String UPDATE_HOME_GARBAGE_AMOUNT_REDIS = API_PREFIX + "update-home-garbage-amount-redis";
    String UPDATE_HOME_ORDER_LIST_REDIS = API_PREFIX + "update-home-order-list-redis";


    @GetMapping(UPDATE_HOME_COUNT_REDIS)
    @ResponseBody
    R<Boolean> updateHomeCountRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_HOME_ALARM_LIST_REDIS)
    @ResponseBody
    R<Boolean> updateHomeAlarmListRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_HOME_EVENT_LIST_REDIS)
    @ResponseBody
    R<Boolean> updateHomeEventListRedis(@RequestParam("tenantId") String tenantId);

    @GetMapping(UPDATE_HOME_ORDER_LIST_REDIS)
    @ResponseBody
    R<Boolean> updateHomeOrderListRedis(@RequestParam("tenantId") String tenantId,@RequestParam("userId") String userId);


    @GetMapping(UPDATE_HOME_GARBAGE_AMOUNT_REDIS)
    @ResponseBody
    R<Boolean> updateHomeGarbageAmountRedis(@RequestParam("tenantId") String tenantId);

}
