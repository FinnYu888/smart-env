package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.workarea.dto.RoadAreaDTO;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
        value = ApplicationConstant.APPLICATION_WORKAREA_NAME,
        fallback = WorkareaClientFallback.class

)
public interface IWorkareaRoadInfoClient {
    String API_PREFIX = "/client";
    String GET_ROADAREA_BY_TENANT_ID = API_PREFIX + "/getRoadAreaByTenantId";
    String GET_MOTORWAY_AREA = API_PREFIX + "/getMotorwayArea";


    @PostMapping(GET_ROADAREA_BY_TENANT_ID)
    R<List<RoadAreaDTO>> getRoadAreaByTenantId(@RequestParam String tenantId);

    /**
     * 根据项目、道路等级获取机动车道面积
     * @param projectCode
     * @param roadLevel
     * @return
     */
    @GetMapping(GET_MOTORWAY_AREA)
    R<Double> getMotorwayArea(@RequestParam String projectCode,@RequestParam Integer roadLevel);
}