package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.vo.BusiRegionTreeVO;
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
 * @ClassName: IRegionClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/9/15
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/15  2020/9/15    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
        fallback = IRegionClientFallback.class
)
public interface IRegionClient {
    String API_PREFIX = "/regionClient";
    String QUERY_CHILD_BUSI_REGION_LIST = API_PREFIX + "/queryChildBusiRegionList";
    String GET_REGION_BY_ID = API_PREFIX + "/getRegionById";
    String REGION_INFO_ASYNC = API_PREFIX + "/region-info-async";

    @PostMapping(REGION_INFO_ASYNC)
    R<Boolean> regionInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType);

    @GetMapping(QUERY_CHILD_BUSI_REGION_LIST)
    R<BusiRegionTreeVO> queryChildBusiRegionList(@RequestParam Long regionId);

    @GetMapping(GET_REGION_BY_ID)
    R<Region> getRegionById(@RequestParam("id") Long regionId);
}
