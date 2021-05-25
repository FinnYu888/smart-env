package com.ai.apac.smartenv.inventory.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.inventory.vo.ResSpecVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: IResSpecClient
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
@FeignClient(value = ApplicationConstant.APPLICATION_INVENTORY_NAME,
        fallback = IResSpecClientFallBack.class
)
public interface IResSpecClient {

    String API_PREFIX = "/client";
    String API_LIST_SPEC_BY_TENANT = API_PREFIX + "/listSpecByTenant";

    @GetMapping(API_LIST_SPEC_BY_TENANT)
    R<List<ResSpecVO>> listSpecByTenant(@RequestParam("tenantId") String tenantId);
    
}
