package com.ai.apac.smartenv.websocket.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright: Copyright (c) 2020/9/22 Asiainfo
 *
 * @ClassName: IPolymerizationDataClient
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/9/22
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/9/22  16:51    zhanglei25          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_WEBSOCKET_NAME,
        fallback = IHomeDataClientFallback.class
)
public interface IPolymerizationDataClient {
    String API_PREFIX = "/client";
    String UPDATE_POLYMERIZATION_COUNT_REDIS = API_PREFIX + "update-polymerization-count-redis";

    @GetMapping(UPDATE_POLYMERIZATION_COUNT_REDIS)
    @ResponseBody
    R<Boolean> updatePolymerizationCountRedis(@RequestParam("tenantId") String tenantId,@RequestParam("entityType") String entityType);

}
