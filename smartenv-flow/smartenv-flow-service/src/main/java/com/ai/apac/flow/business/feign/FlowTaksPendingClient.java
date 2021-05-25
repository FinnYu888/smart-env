package com.ai.apac.flow.business.feign;

import com.ai.apac.flow.engine.service.IFlowTaskPendingService;
import com.ai.apac.smartenv.flow.feign.IFlowTaskPendingClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.tool.api.R;

import java.util.List;

public class FlowTaksPendingClient implements IFlowTaskPendingClient {
    IFlowTaskPendingService flowTaskPendingService;
    @Override
    public R<List<Long>> getFlowTask(Long personId, Long postionId, String roleIds) {

       /*List<Long> orderIdList = flowTaskPendingService.getFlowTask(personId,postionId,roleIds);
        return R.data(orderIdList);*/
       return  null;
    }
}
