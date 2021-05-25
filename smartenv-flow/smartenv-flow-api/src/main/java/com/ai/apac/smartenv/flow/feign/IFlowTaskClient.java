package com.ai.apac.smartenv.flow.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springblade.core.tool.api.R;
import java.util.List;

@FeignClient(
        value = ApplicationConstant.APPLICATION_FLOW_NAME,
        fallback = IFlowTaskClientFallBack.class
)
public interface IFlowTaskClient {

    /**
    *
    * @author 66578
    */

}
