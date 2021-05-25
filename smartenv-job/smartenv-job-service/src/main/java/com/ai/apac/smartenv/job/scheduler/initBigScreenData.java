package com.ai.apac.smartenv.job.scheduler;

import com.ai.apac.smartenv.assessment.feign.IAssessmentClient;
import com.ai.apac.smartenv.system.cache.SysCache;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/25 Asiainfo
 *
 * @ClassName: initBigScreenData
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/25
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/25  9:50    zhanglei25          v1.0.0             修改原因
 */
@Component
@AllArgsConstructor
public class initBigScreenData {

    private IBigScreenDataClient bigScreenDataClient;


    // 每天0点执行
    //@Scheduled(cron = "0 0 0 ? * *")
    public void syncBigScreenData() {
        List<Tenant> allTenant = TenantCache.getAllTenant();

        if(ObjectUtil.isNotEmpty(allTenant) && allTenant.size() > 0){
            allTenant.forEach(tenant -> {
                bigScreenDataClient.updateBigscreenGarbageAmountDailyRedis(tenant.getTenantId());
                bigScreenDataClient.updateBigscreenGarbageAmountByRegionRedis(tenant.getTenantId());
                bigScreenDataClient.updateBigscreenEventCountByTypeRedis(tenant.getTenantId());
            });
        }
    }
}
