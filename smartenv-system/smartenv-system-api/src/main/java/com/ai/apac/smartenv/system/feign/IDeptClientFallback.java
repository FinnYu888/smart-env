package com.ai.apac.smartenv.system.feign;

import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class IDeptClientFallback implements IDeptClient{

    @Override
    public R<Boolean> deptInfoAsync(List<List<String>> datasListStr,String tenantId, @RequestParam String actionType) {
        return R.fail("接收数据失败");
    }
}
