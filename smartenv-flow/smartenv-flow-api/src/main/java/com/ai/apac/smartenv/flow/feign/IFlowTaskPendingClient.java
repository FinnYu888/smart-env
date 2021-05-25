package com.ai.apac.smartenv.flow.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 人员待处理任务接口
 *
 * @author Chill
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_FLOW_NAME,
        fallback = IFlowTaskPendingClientBack.class
)
public interface IFlowTaskPendingClient {
    String API_PREFIX = "/client";
    String GET_FLOW_TASK = API_PREFIX + "/get-flow-tasks";
    /**
     * 查询待处理任务
     * @author 66578
     */
    @GetMapping(GET_FLOW_TASK)
    R<List<Long>> getFlowTask(@RequestParam("personId") Long personId, @RequestParam("postionId") Long postionId, @RequestParam("roleIds") String roleIds);
}
