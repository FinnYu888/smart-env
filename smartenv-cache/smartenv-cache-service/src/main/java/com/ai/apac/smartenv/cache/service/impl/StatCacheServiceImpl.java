package com.ai.apac.smartenv.cache.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/4 2:37 下午
 **/
@Service
public class StatCacheServiceImpl implements IStatCacheService {

    @Autowired
    private IStatisticsClient statisticsClient;

    /**
     * 重新刷首页四个数字的统计
     *
     * @param tenantId
     */
    @Override
    public void reloadHomeSummaryAmount(String tenantId) {
        ScheduleCache.delWorkingCountForPersonToday(tenantId);
        ScheduleCache.delWorkingCountForVehicleToday(tenantId);
        EventCache.delEventCountToday(tenantId);
        AlarmInfoCache.delUnHandleAlarmCountToday(tenantId);

        statisticsClient.getHomeSummaryAmountToday(tenantId);
    }

    /**
     * 重新刷新综合数字统计
     *
     * @param tenantId
     */
    @Override
    public void reloadSummaryAmount(String tenantId) {
        VehicleCache.delNormalVehicleCount(tenantId);
        PersonCache.delActivePersonCount(tenantId);
        FacilityCache.delFacilityCountToday(tenantId);
        AshcanCache.delAshcanCount(tenantId);
        ToiletCache.delToiletCountToday(tenantId);
        EventCache.delEventCountToday(tenantId);
        AlarmInfoCache.delUnHandleAlarmCountToday(tenantId);

        statisticsClient.getSummaryAmountToday(tenantId);
    }
}
