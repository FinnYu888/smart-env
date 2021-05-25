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
public interface IStationClient {
    String API_PREFIX = "/staClient";
    String STATION_ASYNC = API_PREFIX + "/station-info-async";

    @PostMapping(STATION_ASYNC)
    R<Boolean> stationInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType);
}
