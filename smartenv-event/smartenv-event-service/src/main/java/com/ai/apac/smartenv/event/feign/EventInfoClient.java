package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.service.IEventInfoKpiRelService;
import com.ai.apac.smartenv.event.service.IEventInfoService;
import com.ai.apac.smartenv.event.service.IEventKpiCatalogService;
import com.ai.apac.smartenv.event.service.IEventKpiDefService;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
public class EventInfoClient implements IEventInfoClient {
    @Autowired
    private IEventInfoService eventInfoService;

    @Autowired
    private IEventKpiCatalogService eventKpiCatalogService;

    @Autowired
    private IEventKpiDefService eventKpiDefService;

    @Autowired
    private IEventInfoKpiRelService eventInfoKpiRelService;


    @Override
    @GetMapping(LIST_ALL_EVENT_KPI_CATALOG)
    public R<List<EventKpiCatalog>> listAllEventKpiCatalog() {
        return R.data(eventKpiCatalogService.list());

    }

    @Override
    @GetMapping(EVENT_KPI_CATALOG_BY_ID)
    public R<EventKpiCatalog> getEventKpiCatalogById(Long eventKpiCatalogId) {
        return R.data(eventKpiCatalogService.getById(eventKpiCatalogId));
    }

    @Override
    @GetMapping(LIST_ALL_EVENT_KPI_DEF)
    public R<List<EventKpiDef>> listAllEventKpiDef() {
        return R.data(eventKpiDefService.list());
    }

    @Override
    @GetMapping(LIST_ALL_EVENT_INFO_KPI_REL)
    public R<List<EventInfoKpiRel>> listAllEventInfoKpiRel() {
        return R.data(eventInfoKpiRelService.list());
    }

    @Override
    @GetMapping(LIST_ALL_EVENT_INFO_KPI_REL_BY_EVENT_ID)
    public R<List<EventInfoKpiRel>> listAllEventInfoKpiRelByEventId(Long eventId) {
        return R.data(eventInfoKpiRelService.list(new QueryWrapper<EventInfoKpiRel>().lambda().eq(EventInfoKpiRel::getEventInfoId, eventId)));
    }


    @Override
    @GetMapping(EVENT_KPI_DEF_BY_ID)
    public R<EventKpiDef> getEventKpiDefById(Long eventKpiDefId) {
        return R.data(eventKpiDefService.getById(eventKpiDefId));
    }

    @Override
    @GetMapping(COUNT_EVENT_GROUP_BY_TYPE)
    public R<List<EventTypeCountVO>> countEventGroupByType(String tenantId, Integer days) {
        List<EventTypeCountVO> eventTypeCountVOList = eventInfoService.countEventGroupByType(days, tenantId);
        eventTypeCountVOList.forEach(eventTypeCountVO -> {
            eventTypeCountVO.setEventTypeName(DictCache.getValue("event_type", eventTypeCountVO.getEventType()));
        });
        return R.data(eventTypeCountVOList);
    }

    @Override
    @GetMapping(LIST_BY_ID)
    public R<List<EventInfo>> listEventInfoById(@RequestParam("id") Long id) {

        return R.data(eventInfoService.list(new QueryWrapper<EventInfo>().eq("workarea_id", id)));
    }

    @Override
    @GetMapping(LIST_BY_EVENT_ID)
    public R<EventInfo> getEventInfoByEventId(@RequestParam("eventId") Long eventId) {

        return R.data(eventInfoService.getById(eventId));
    }

    @Override
    public R<EventInfoVO> getEventDetailById(Long eventId) {
        return R.data(eventInfoService.getDetail(String.valueOf(eventId)));
    }

    @Override
    public R<Integer> countEventDailyByParam(String tenantId, Long belongAreaId) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setTenantId(tenantId);
        eventInfo.setBelongArea(belongAreaId);
        return R.data(eventInfoService.countEventDaily(eventInfo));
    }

    @Override
    @GetMapping(COUNT_DAILY)
    public R<Integer> countEventDaily(String tenantId) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setTenantId(tenantId);
        return R.data(eventInfoService.countEventDaily(eventInfo));
    }

    @Override
    @GetMapping(LIST_BY_PARAM)
    public R<List<EventInfoVO>> listEventInfoByParam(EventQueryDTO eventQueryDTO) {
        return R.data(eventInfoService.listEventInfoByParam(eventQueryDTO));
    }

    @Override
    @PostMapping(UPDATE_EVENT_INFO)
    public R<Boolean> updateEventInfo(@RequestBody EventInfoVO eventInfo) {
        return R.data(eventInfoService.updateById(eventInfo));
    }

    @Override
    @GetMapping(QRY_EVENT_INFO)
    public R<GreenScreenEventsDTO> queryEventInfos(String tenantId) {
        return R.data(eventInfoService.queryEventInfos(tenantId));
    }

    @Override
    @GetMapping(LIST_BY_ALL)
    public R<List<EventInfo>> listEventInfoAll() {
        return R.data(eventInfoService.list(new QueryWrapper<EventInfo>()));
    }

    @Override
    @PostMapping(LIST_BY_CONDITION)
    public R<List<EventInfoVO>> listEventInfoByCondition(@RequestBody EventQueryDTO eventQueryDTO) {
        return R.data(eventInfoService.listEventInfoByCondition(eventQueryDTO));
    }
}
