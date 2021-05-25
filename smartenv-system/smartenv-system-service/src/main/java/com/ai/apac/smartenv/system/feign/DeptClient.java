package com.ai.apac.smartenv.system.feign;

import com.ai.apac.smartenv.system.service.IDeptAsyncService;
import com.ai.apac.smartenv.system.service.IDeptService;
import com.ai.apac.smartenv.system.service.IRegionAsyncService;
import com.ai.apac.smartenv.system.service.IRegionService;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DeptClient implements IDeptClient {

    private IDeptAsyncService deptAsyncService;


    @Override
    public R<Boolean> deptInfoAsync(List<List<String>> datasList,String tenantId, String actionType) {
        return R.data(deptAsyncService.thirdDeptInfoAsync(datasList,tenantId,actionType,true));
    }
}
