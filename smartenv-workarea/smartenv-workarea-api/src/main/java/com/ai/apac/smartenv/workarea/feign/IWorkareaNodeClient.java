package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.workarea.entity.WorkareaInfo;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
 * 2020/2/14  18:07    yupf3          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_WORKAREA_NAME,
        fallback = WorkareaNodeClientFallback.class

)
public interface IWorkareaNodeClient {
    String API_PREFIX = "/client";
    String SAVE_NODE = API_PREFIX + "/saveWorkAreaNode";
    String QUERY_NODES = API_PREFIX + "/queryRegionNodesList";
    String DELETE_NODES = API_PREFIX + "/deleteWorkAreaNodes";
    String QUERY_BY_WORKAREA_ID = API_PREFIX + "/queryByWorkAreaId";


//    String CHAR_SPECS = API_PREFIX + "/char-specs";

    @GetMapping(SAVE_NODE)
    R<Boolean> saveWorkAreaNode(WorkareaNode node);
    @GetMapping(QUERY_NODES)
    R<List<WorkareaNode>> queryRegionNodesList(@RequestParam("regionId") Long regionId);

    @GetMapping(DELETE_NODES)
    R<Boolean> deleteWorkAreaNodes(@RequestParam("regionId") Long regionId);


    @GetMapping(QUERY_BY_WORKAREA_ID)
    R<List<WorkareaNode>> queryNodeByWorkareaId(@RequestParam("workAreaId")  Long workAreaId);
}
