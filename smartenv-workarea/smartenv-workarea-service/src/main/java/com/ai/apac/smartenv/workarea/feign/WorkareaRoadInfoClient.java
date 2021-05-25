package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.workarea.dto.RoadAreaDTO;
import com.ai.apac.smartenv.workarea.service.IWorkareaInfoService;
import com.ai.apac.smartenv.workarea.service.IWorkareaRoadInfoService;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@RestController
@RequiredArgsConstructor
public class WorkareaRoadInfoClient implements IWorkareaRoadInfoClient {
    @Autowired
    private IWorkareaRoadInfoService workareaRoadInfoService;

    @Override
    public R<List<RoadAreaDTO>> getRoadAreaByTenantId(String tenantId) {
        return R.data(workareaRoadInfoService.getRoadAreaByTenantId(tenantId));
    }

    /**
     * 根据项目、道路等级获取机动车道面积
     *
     * @param projectCode
     * @param roadLevel
     * @return
     */
    @Override
    public R<Double> getMotorwayArea(String projectCode, Integer roadLevel) {
        return R.data(workareaRoadInfoService.getTotalMotorwayArea(projectCode, roadLevel));
    }
}
