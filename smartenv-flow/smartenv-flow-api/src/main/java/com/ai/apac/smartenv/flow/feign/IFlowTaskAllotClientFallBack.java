package com.ai.apac.smartenv.flow.feign;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Component
public class IFlowTaskAllotClientFallBack implements IFlowTaskAllotClient{

    @Override
    public R createFlowTask(String flowType, String taskNode, Long orderId,String tenantId) {
        return R.fail("远程调用失败");
    }
    @Override
    @GetMapping(GET_FLOW_TASK)
    public R<List<Long>> getFlowTask(Long personIds, Long postionIds, String roleIds,String taskNode) {
        return R.fail("远程调用失败");
    }

    @Override
    public R finishTask(String flowName,String workflowId, Map<String, Object> paramMap, Long id, String currentTask) {
        return R.fail("远程调用失败");
    }

    @Override
    public R<Boolean> getTaskDonePermission(Long orderId, String taskNode, Long personId, Long postionId, String roleIds) {
        return R.fail("远程调用失败");
    }

    @Override
    public R<Boolean> checkFlowInfoConfig(String flowCode, String tenantId) {
        return R.fail("远程调用失败");
    }
}
