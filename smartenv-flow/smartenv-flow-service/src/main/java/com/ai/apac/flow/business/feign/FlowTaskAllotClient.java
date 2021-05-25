package com.ai.apac.flow.business.feign;

import cn.hutool.core.collection.CollectionUtil;
import com.ai.apac.flow.engine.service.IFlowInfoService;
import com.ai.apac.smartenv.flow.entity.FlowTaskAllot;
import com.ai.apac.smartenv.flow.entity.FlowTaskPending;
import com.ai.apac.flow.engine.service.IFlowTaskAllotService;
import com.ai.apac.flow.engine.service.IFlowTaskPendingService;
import com.ai.apac.smartenv.flow.feign.IFlowTaskAllotClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class FlowTaskAllotClient implements IFlowTaskAllotClient {
    @Autowired
    IFlowTaskAllotService flowTaskAllotService;
    @Autowired
    IFlowTaskPendingService flowTaskPendingService;
    @Autowired
    IFlowInfoService flowInfoService;
    @Override
    public R createFlowTask(String flowName, String taskNode, Long orderId,String tenantId) {
        flowTaskAllotService.createFlowTask(flowName,taskNode,orderId,tenantId);

        return null;
    }

    @Override
    public R<List<Long>> getFlowTask(Long personId, Long postionId, String roleIds,String taskNode) {
        List<Long> roleIdList = null;
        if (StringUtils.isNotEmpty(roleIds)) {
            roleIdList = Func.toLongList(roleIds);
        }
        List<Long> orderIdList = flowTaskPendingService.getFlowTask(personId,postionId,roleIdList,taskNode);
        return R.data(orderIdList);
    }

    @Override
    public R finishTask(String flowName,String workflowId, Map<String, Object> paramMap, Long orderId, String currentTask) {
        flowTaskAllotService.finishTask(flowName,workflowId,paramMap,orderId,currentTask);
        return R.status(true);
    }

    @Override
    public R<Boolean> getTaskDonePermission(Long orderId, String taskNode, Long personId, Long postionId, String roleIds) {
        List<FlowTaskPending> list = flowTaskPendingService.getTaskDonePermission(orderId,taskNode,personId,postionId,roleIds);
        return R.data(CollectionUtil.isEmpty(list)?false:true);
    }

    @Override
    public R<Boolean> checkFlowInfoConfig(String flowCode, String tenantId) {
        return R.data(flowInfoService.checkFlowInfoConfig(flowCode,tenantId));
    }
}
