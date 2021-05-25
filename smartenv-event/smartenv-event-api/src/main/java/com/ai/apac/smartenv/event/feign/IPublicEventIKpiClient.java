package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.entity.PublicEventKpi;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
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
 * @ClassName: IPublicEventIKpiClient
 * @Description:
 * @version: v1.0.0
 * @author: qianlong
 * @date: 2020/12/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/17  18:07    qianlong          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_EVENT_NAME,
        fallback = PublicEventIKpiClientFallback.class
)
public interface IPublicEventIKpiClient {
    String API_PREFIX = "/client";
    String GET_ALL_KPI = API_PREFIX + "/allKpi";
    String GET_KPI_BY_CITY = API_PREFIX + "/getKpiByCityId";
    String GET_KPI_BY_ID = API_PREFIX + "/getKpiById";

    /**
     * 获取所有KPI
     * @return
     */
    @GetMapping(GET_ALL_KPI)
    R<List<PublicEventKpi>> listAllKpi();

    /**
     * 根据城市ID获取所有KPI
     * @return
     */
    @GetMapping(GET_KPI_BY_CITY)
    R<List<PublicEventKpi>> listKpiByCityId(@RequestParam Long cityId);

    @GetMapping(GET_KPI_BY_ID)
    R<PublicEventKpi> getKpiById(@RequestParam Long kpiId);
}
