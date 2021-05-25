package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.workarea.entity.WorkareaRel;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
 * @ClassName: IWorkareaRelClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/14
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/14  19:05    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_WORKAREA_NAME,
        fallback = WorkareaClientRelFallback.class
)

public interface IWorkareaRelClient {

    String API_PREFIX = "/client";
    String API_WORKAREA_ID = API_PREFIX+"/getByEntityIdAndType";
    String API_UNBIND_WORKAREA = API_PREFIX+"/unbindWorkarea";
    String API_QUERY_BY_CONDITION = API_PREFIX+"/queryByCondition";

    String API_SYNC_DRIVER_WORKAREA = API_PREFIX+"/syncDriverWorkArea";
    String API_GET_BY_CONDITION = API_PREFIX+"/getByCondition";
    String API_GET_BY_ID_AND_TYPE = API_PREFIX+"/getByIdAndType";
    String GET_BY_WORKAREA_IDS = API_PREFIX+"/getByWorkareaIds";

    @GetMapping(API_WORKAREA_ID)
    R<List<WorkareaRel>> getByEntityIdAndType(@RequestParam("entityId") Long entityId,@RequestParam("entityType") Long entityType);

    @PostMapping(API_GET_BY_CONDITION)
    R<List<WorkareaRel>> getByCondition(@RequestParam("workAreaId") String workAreaId,@RequestParam("entityType") Long entityType,@RequestParam("entityIds") List<Long> entityIds);

    @PostMapping(API_GET_BY_ID_AND_TYPE)
    R<List<WorkareaRel>> getByIdAndType(@RequestParam("workAreaId") String workAreaId,@RequestParam("entityType") Long entityType);


    @PostMapping(API_UNBIND_WORKAREA)
	R<Boolean> unbindWorkarea(@RequestParam("entityId") Long entityId,@RequestParam("entityType") Long entityType);


    @GetMapping(API_QUERY_BY_CONDITION)
    R<List<WorkareaRel>> queryByCondition(@RequestParam("entityId") Long entityId,@RequestParam("entityType") Long entityType,@RequestParam("startTime") Long startTime,@RequestParam("endTime") Long endTime, @RequestParam("tenantId") String tenantId);

    @PostMapping(API_SYNC_DRIVER_WORKAREA)
	R<Boolean> syncDriverWorkArea(@RequestParam("entityId") Long entityId, @RequestParam("personId") Long personId,
			@RequestParam("flag") String flag, @RequestParam("userId")Long userId, @RequestParam("deptId")String deptId, @RequestParam("tenantId")String tenantId);

    @PostMapping(GET_BY_WORKAREA_IDS)
    R<List<WorkareaRel>> getByWorkareaIds(@RequestBody List<String> workareaIds, @RequestParam Long entityType);
}
