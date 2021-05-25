package com.ai.apac.smartenv.event.feign;

import com.ai.apac.smartenv.event.dto.EventQueryDTO;
import com.ai.apac.smartenv.event.dto.mongo.GreenScreenEventsDTO;
import com.ai.apac.smartenv.event.entity.EventInfo;
import com.ai.apac.smartenv.event.entity.EventKpiCatalog;
import com.ai.apac.smartenv.event.entity.EventKpiDef;
import com.ai.apac.smartenv.event.entity.PublicEventKpi;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.event.vo.EventTypeCountVO;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PublicEventIKpiClientFallback
 * @Description:
 * @version: v1.0.0
 * @author: qianlong
 * @date: 2020/12/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/12/17  18:07    qianlong          v1.0.0             修改原因
 */
public class PublicEventIKpiClientFallback implements IPublicEventIKpiClient {

    @Override
    public R<List<PublicEventKpi>> listAllKpi() {
        return R.fail("接收数据失败");
    }

    /**
     * 根据城市ID获取所有KPI
     *
     * @param cityId
     * @return
     */
    @Override
    public R<List<PublicEventKpi>> listKpiByCityId(Long cityId) {
        return R.fail("接收数据失败");
    }

    @Override
    public R<PublicEventKpi> getKpiById(Long kpiId) {
        return R.fail("接收数据失败");
    }
}
