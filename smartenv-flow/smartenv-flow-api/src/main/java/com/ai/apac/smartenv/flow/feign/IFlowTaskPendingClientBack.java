package com.ai.apac.smartenv.flow.feign;

import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class IFlowTaskPendingClientBack implements IFlowTaskPendingClient {
    @Override
    @GetMapping(GET_FLOW_TASK)
    public R<List<Long>> getFlowTask(Long personIds, Long postionIds, String roleIds) {
        return R.fail("远程调用失败");
    }
}
