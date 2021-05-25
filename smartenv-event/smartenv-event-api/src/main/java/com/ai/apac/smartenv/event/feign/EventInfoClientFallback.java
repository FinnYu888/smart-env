package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventInfoKpiRel;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WorkareaClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  18:12    panfeng          v1.0.0             修改原因
 */
public class EventInfoClientFallback implements IEventInfoClient {
    @Override
    public R<List<EventKpiCatalog>> listAllEventKpiCatalog() {
        return R.fail("接收数据失败");
    }

    @Override
    public R<EventKpiCatalog> getEventKpiCatalogById(Long eventKpiCatalogId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<EventKpiDef>> listAllEventKpiDef() {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<EventInfoKpiRel>> listAllEventInfoKpiRel() {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<EventInfoKpiRel>> listAllEventInfoKpiRelByEventId(Long eventId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<EventKpiDef> getEventKpiDefById(Long eventKpiDefId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<EventTypeCountVO>> countEventGroupByType(String tenantId, Integer days) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<EventInfo>> listEventInfoById(@RequestParam("id") Long id) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<EventInfoVO> getEventDetailById(Long eventId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<EventInfo> getEventInfoByEventId(@RequestParam("eventId") Long eventId){return R.fail("接收数据失败");}

    @Override
    public R<Integer> countEventDailyByParam(String tenantId, Long workAreaId) {
        return R.fail("接收数据失败");
    }


    @Override
    public R<Boolean> updateEventInfo(EventInfoVO eventInfo) {
        return R.fail("更新数据失败");
    }

    @Override
    public R<Integer> countEventDaily(String tenantId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<List<EventInfoVO>> listEventInfoByParam(EventQueryDTO eventQueryDTO) {
        return R.fail("接收数据失败");
    }
    @Override
    public R<GreenScreenEventsDTO> queryEventInfos( String tenantId){return R.data(null);}
    @Override
    public R<List<EventInfo>> listEventInfoAll(){return R.data(null);}

    @Override
    public R<List<EventInfoVO>> listEventInfoByCondition(EventQueryDTO eventQueryDTO) {
        return R.fail("接收数据失败");
    }
}
