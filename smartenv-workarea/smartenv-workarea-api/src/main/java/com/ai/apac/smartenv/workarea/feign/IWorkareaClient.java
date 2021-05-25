package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
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
public interface IWorkareaClient {
    String API_PREFIX = "/client";
    String GET_BY_ID = API_PREFIX + "/getById";
    String GET_BY_REGION_ID = API_PREFIX + "/getByRegionId";
    String GET_BY_TENANT_ID = API_PREFIX + "/getByTenantId";
    String GET_NAME_BY_IDS = API_PREFIX + "/getAreaNameByIdS";
//    String CHAR_SPECS = API_PREFIX + "/char-specs";
    String UPDATE_WORKAREA_INFO = API_PREFIX + "/updateWorkareaInfo";

    String WORKAREA_INFO_ASYNC = API_PREFIX + "/workarea-info-async";

    @PostMapping(WORKAREA_INFO_ASYNC)
    R<Boolean> workareaInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType);

    @GetMapping(GET_BY_ID)
    R<WorkareaInfo> getWorkInfoById(@RequestParam("id") Long id);

    @PostMapping(UPDATE_WORKAREA_INFO)
    R<Boolean> updateWorkareaInfo(WorkareaInfo workareaInfo);

    @GetMapping(GET_BY_REGION_ID)
    R<List<WorkareaInfo>> getWorkareaInfoByRegion(@RequestParam("regionId") Long regionId);

    @GetMapping(GET_BY_TENANT_ID)
    R<List<WorkareaInfo>> getWorkareaInfoByTenantId(@RequestParam("tenantId") String tenantId);


    @PostMapping(GET_NAME_BY_IDS)
    R<Map<Long,String>> getWorkInfoByIds(@RequestBody List<Long> ids);
}
