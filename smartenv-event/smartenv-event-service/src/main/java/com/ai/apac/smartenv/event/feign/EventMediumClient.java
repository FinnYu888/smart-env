package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.entity.EventMedium;
import com.ai.apac.smartenv.event.service.IEventMediumService;
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
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:24    panfeng          v1.0.0             修改原因
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
public class EventMediumClient implements IEventMediumClient {
    @Autowired
    private IEventMediumService eventMediumService;


    @Override
    @GetMapping(LIST_BY_EVENT_ID)
    public R<List<EventMedium>> listEventMediumById(@RequestParam("eventId") Long eventId,@RequestParam("mediumDetailType") Integer mediumDetailType) {

        return R.data(eventMediumService.list(new QueryWrapper<EventMedium>().eq("event_info_id",eventId).eq("medium_detail_type",mediumDetailType)));
    }

    @Override
    @GetMapping(LIST_BY_ASSIGNED_ID)
    public R<List<EventMedium>> listEventMediumByAssignedId(@RequestParam("assignedId") Long assignedId) {

        return R.data(eventMediumService.list(new QueryWrapper<EventMedium>().eq("assigned_id",assignedId)));
    }
}
