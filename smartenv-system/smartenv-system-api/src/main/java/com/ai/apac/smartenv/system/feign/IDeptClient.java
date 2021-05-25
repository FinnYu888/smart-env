package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        value = ApplicationConstant.APPLICATION_SYSTEM_NAME,
        fallback = IRegionClientFallback.class
)
public interface IDeptClient {
    String API_PREFIX = "/deptClient";
    String DEPT_ASYNC = API_PREFIX + "/dept-info-async";

    @PostMapping(DEPT_ASYNC)
    R<Boolean> deptInfoAsync(@RequestBody List<List<String>> datasList,@RequestParam String tenantId, @RequestParam String actionType);
}
