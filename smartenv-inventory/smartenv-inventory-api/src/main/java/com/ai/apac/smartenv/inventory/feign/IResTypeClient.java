package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: IResTypeClient
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/8/11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/8/11     zhaidx           v1.0.0               修改原因
 */
@FeignClient( value = ApplicationConstant.APPLICATION_INVENTORY_NAME,
        fallback = IResTypeClientFallBack.class
)
public interface IResTypeClient {

    String API_PREFIX = "/client";
    String API_LIST_SPEC_NAMES = API_PREFIX + "/listSpecNames";

    @GetMapping(value = API_LIST_SPEC_NAMES)
    R<List<String>> listResTypeResSpecNameIdStrings(@RequestParam(value = "tenantId") String tenantId);

}
