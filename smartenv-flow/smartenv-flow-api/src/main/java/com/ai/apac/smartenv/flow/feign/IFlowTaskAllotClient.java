package com.ai.apac.smartenv.flow.feign;
import org.springblade.core.tool.api.R;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 任务分配接口
 *
 * @author Chill
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_FLOW_NAME,
        fallback = IFlowClientFallback.class
)
public interface IFlowTaskAllotClient {
    String API_PREFIX = "/client";
    String CREATE_FLOW_TASK = API_PREFIX + "/create-flow-task";
    String GET_FLOW_TASK = API_PREFIX + "/get-flow-task";
    String FINISH_TASK = API_PREFIX + "/finish-task";
    String GET_TASK_DONE_PERMISSION =API_PREFIX + "/get-task-done-permission";
    String CHECK_FLOWINFO_CONFIG = API_PREFIX +"/check-flowinfo-config";
    /**
    * 创建任务
    * @author 66578
    */
    @GetMapping(CREATE_FLOW_TASK)
    R createFlowTask(@RequestParam("flowType") String flowType, @RequestParam("taskNode") String taskNode, @RequestParam("orderId") Long orderId,@RequestParam("tenantId") String tenantId);

    /**
    * 查询待处理任务
    * @author 66578
    */
    @GetMapping(GET_FLOW_TASK)
    R<List<Long>> getFlowTask(@RequestParam("personId") Long personId, @RequestParam("postionId") Long postionId, @RequestParam("roleIds") String roleIds,@RequestParam(name = "taskNode",required = false)String taskNode);

    /**
    * 处理任务
    * @author 66578
    */
    @PostMapping(FINISH_TASK)
    R finishTask(@RequestParam("flowName") String flowName, @RequestParam("workflowId")String workflowId, @RequestBody Map<String, Object> paramMap, @RequestParam("orderId")Long orderId, @RequestParam("currentTask")String currentTask);

    /**
    * 获取该订单当前节点，该用户有没有处理权限
    * @author 66578
    */
    @GetMapping(GET_TASK_DONE_PERMISSION)
    R<Boolean> getTaskDonePermission(@RequestParam("orderId")Long orderId,@RequestParam("taskNode")String taskNode,@RequestParam("personId")Long personId,@RequestParam("postionId")Long postionId,@RequestParam("roleIds")String roleIds);

    /**
     * 检查流程节点是否配置
     * @author 66578
     */
    @GetMapping(CHECK_FLOWINFO_CONFIG)
    R<Boolean> checkFlowInfoConfig(@RequestParam("flowCode")String flowCode,@RequestParam("tenantId")String tenantId);
}
