package com.ai.apac.smartenv.cache.controller;

import com.ai.apac.smartenv.alarm.cache.AlarmInfoCache;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.cache.service.IStatCacheService;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.facility.cache.AshcanCache;
import com.ai.apac.smartenv.facility.cache.FacilityCache;
import com.ai.apac.smartenv.facility.cache.ToiletCache;
import com.ai.apac.smartenv.omnic.feign.IStatisticsClient;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * @author qianlong
 * @description 数据统计相关Cache
 * @Date 2020/11/4 11:07 上午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/statCache")
@Api(value = "数据统计Cache操作", tags = "数据统计Cache操作")
public class StatCacheController {

    private IStatCacheService statCacheService;

    /**
     * 重新加载首页四个统计数据
     *
     * @return
     */
    @PostMapping("/reloadHomeSummaryAmount/{tenantId}")
    @ApiOperationSupport(order = 1)
    public R reloadHomeSummaryAmount(@PathVariable String tenantId){
        statCacheService.reloadHomeSummaryAmount(tenantId);
        return R.status(true);
    }

    /**
     * 重新加载综合统计数据
     *
     * @return
     */
    @PostMapping("/reloadSummaryAmount/{tenantId}")
    @ApiOperationSupport(order = 1)
    public R reloadSummaryAmount(@PathVariable String tenantId){
        statCacheService.reloadSummaryAmount(tenantId);
        return R.status(true);
    }
}
