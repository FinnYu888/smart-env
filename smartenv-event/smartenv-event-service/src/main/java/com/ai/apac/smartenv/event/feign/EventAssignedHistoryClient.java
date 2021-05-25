package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.entity.EventAssignedHistory;
import com.ai.apac.smartenv.event.service.IEventAssignedHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
 * 2020/2/14  18:24    yupf3          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class EventAssignedHistoryClient implements IEventAssignedHistoryClient {
    @Autowired
    private IEventAssignedHistoryService eventAssignedHistoryService;


    @Override
    @GetMapping(LIST_BY_EVENT_ID)
    public R<List<EventAssignedHistory>> listEventAssignedHistoryById(@RequestParam("eventId") Long eventId, @RequestParam("type") Integer type, @RequestParam("result")Integer result) {

        return R.data(eventAssignedHistoryService.list(new QueryWrapper<EventAssignedHistory>().eq("event_info_id",eventId)
                .eq("type",type)
                .eq("check_result",result)));
    }


}
