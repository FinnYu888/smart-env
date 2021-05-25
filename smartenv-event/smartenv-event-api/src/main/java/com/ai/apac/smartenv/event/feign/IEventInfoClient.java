package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
 * 2020/2/14  18:07    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_EVENT_NAME,
        fallback = EventInfoClientFallback.class

)
public interface IEventInfoClient {
    String API_PREFIX = "/client";
    String LIST_BY_ID = API_PREFIX + "/listById";
    String GET_EVENT_DETAIL_BY_ID = API_PREFIX + "/getEventDetailById";
    String LIST_BY_ALL = API_PREFIX + "/listAll";
    String LIST_BY_EVENT_ID = API_PREFIX + "/listByEventId";
    String COUNT_DAILY = API_PREFIX + "/countEventDaily";
    String COUNT_DAILY_BY_PARAM = API_PREFIX + "/countEventDailyByParam";
    String LIST_BY_PARAM = API_PREFIX + "/eventByParam";
    String QRY_EVENT_INFO = "/queryEventInfos";
    String UPDATE_EVENT_INFO = API_PREFIX + "/updateEventInfo";
    String COUNT_EVENT_GROUP_BY_TYPE = API_PREFIX + "/count-event-group-by-type";
    String LIST_BY_CONDITION = API_PREFIX + "/listEventInfoByCondition";

    String LIST_ALL_EVENT_KPI_CATALOG = API_PREFIX + "/list-all-event-kpi-catalog";
    String LIST_ALL_EVENT_KPI_DEF = API_PREFIX + "/list-all-event-kpi-def";
    String EVENT_KPI_CATALOG_BY_ID = API_PREFIX + "/event-kpi-catalog-by-id";
    String EVENT_KPI_DEF_BY_ID = API_PREFIX + "/event-kpi-def-by-id";
    String LIST_ALL_EVENT_INFO_KPI_REL = API_PREFIX + "/list-all-event-info-kpi-rel";
    String LIST_ALL_EVENT_INFO_KPI_REL_BY_EVENT_ID = API_PREFIX + "/list-all-event-info-kpi-rel-by-event-id";


    @GetMapping(LIST_ALL_EVENT_KPI_CATALOG)
    R<List<EventKpiCatalog>> listAllEventKpiCatalog();

    @GetMapping(EVENT_KPI_CATALOG_BY_ID)
    R<EventKpiCatalog> getEventKpiCatalogById(@RequestParam("eventKpiCatalogId") Long eventKpiCatalogId);

    @GetMapping(LIST_ALL_EVENT_KPI_DEF)
    R<List<EventKpiDef>> listAllEventKpiDef();
    @GetMapping(LIST_ALL_EVENT_INFO_KPI_REL)
    R<List<EventInfoKpiRel>> listAllEventInfoKpiRel();


    @GetMapping(LIST_ALL_EVENT_INFO_KPI_REL_BY_EVENT_ID)
    R<List<EventInfoKpiRel>> listAllEventInfoKpiRelByEventId(Long eventId);

    @GetMapping(EVENT_KPI_DEF_BY_ID)
    R<EventKpiDef> getEventKpiDefById(@RequestParam("eventKpiDefId") Long eventKpiDefId);



//    String CHAR_SPECS = API_PREFIX + "/char-specs";

    @GetMapping(COUNT_EVENT_GROUP_BY_TYPE)
    R<List<EventTypeCountVO>> countEventGroupByType(@RequestParam("tenantId") String tenantId,@RequestParam("days") Integer days);

    @GetMapping(LIST_BY_ID)
    R<List<EventInfo>> listEventInfoById(@RequestParam("id") Long id);

    @GetMapping(LIST_BY_EVENT_ID)
    R<EventInfo> getEventInfoByEventId(@RequestParam("eventId") Long eventId);

    @GetMapping(GET_EVENT_DETAIL_BY_ID)
    R<EventInfoVO> getEventDetailById(@RequestParam("eventId") Long eventId);

    @GetMapping(COUNT_DAILY_BY_PARAM)
    R<Integer> countEventDailyByParam(@RequestParam("tenantId") String tenantId,@RequestParam("belongAreaId") Long belongAreaId);

    @GetMapping(COUNT_DAILY)
    R<Integer> countEventDaily(@RequestParam("tenantId") String tenantId);

    @GetMapping(LIST_BY_PARAM)
    R<List<EventInfoVO>> listEventInfoByParam(EventQueryDTO eventQueryDTO);

    @PostMapping(UPDATE_EVENT_INFO)
    R<Boolean> updateEventInfo(EventInfoVO eventInfo);

    @GetMapping(QRY_EVENT_INFO)
    R<GreenScreenEventsDTO> queryEventInfos(@RequestParam("tenantId") String tenantId);

    @GetMapping(LIST_BY_ALL)
    R<List<EventInfo>> listEventInfoAll();

    @PostMapping(LIST_BY_CONDITION)
    R<List<EventInfoVO>> listEventInfoByCondition(@RequestBody  EventQueryDTO eventQueryDTO);
}
