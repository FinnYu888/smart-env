package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClient
 * @Description:
 * @version: v1.0.0
 * @author: yupf3
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:07    yupf3          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_EVENT_NAME,
        fallback = EventAssignedHistoryClientFallback.class

)
public interface IEventAssignedHistoryClient {
    String API_PREFIX = "/client";
    String LIST_BY_EVENT_ID = API_PREFIX + "/listEventAssignedHistoryById";


    @GetMapping(LIST_BY_EVENT_ID)
    R<List<EventAssignedHistory>> listEventAssignedHistoryById(@RequestParam("eventId") Long eventId, @RequestParam("type") Integer type, @RequestParam("result")Integer result);

}
