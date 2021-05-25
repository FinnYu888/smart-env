package com.ai.apac.smartenv.system.feign;

import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class IStationClientFallback implements IStationClient{

    @Override
    public R<Boolean> stationInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.fail("接收数据失败");
    }
}

