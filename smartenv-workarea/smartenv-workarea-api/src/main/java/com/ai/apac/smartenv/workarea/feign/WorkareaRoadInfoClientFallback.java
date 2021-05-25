package com.ai.apac.smartenv.workarea.feign;

import com.ai.apac.smartenv.workarea.dto.RoadAreaDTO;
import org.springblade.core.tool.api.R;

import java.util.List;

public class WorkareaRoadInfoClientFallback implements IWorkareaRoadInfoClient{
    @Override
    public R<List<RoadAreaDTO>> getRoadAreaByTenantId(String tenantId) {
        return R.fail("接收数据失败");
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
        return R.fail("接收数据失败");
    }
}
