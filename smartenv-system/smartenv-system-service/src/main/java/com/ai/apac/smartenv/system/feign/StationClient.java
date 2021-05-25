package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.service.IStationAsyncService;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class StationClient implements IStationClient {

    private IStationAsyncService stationAsyncService;

    @Override
    public R<Boolean> stationInfoAsync(@RequestBody List<List<String>> datasList, @RequestParam String tenantId, @RequestParam String actionType) {
        return R.data(stationAsyncService.thirdStationInfoAsync(datasList,tenantId,actionType,true));
    }
}