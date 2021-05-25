package com.ai.apac.smartenv.omnic.service.impl;

import com.ai.apac.smartenv.alarm.cache.AlarmInfoCache;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.facility.cache.AshcanCache;
import com.ai.apac.smartenv.facility.cache.FacilityCache;
import com.ai.apac.smartenv.facility.cache.ToiletCache;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.dto.SummaryDataForVehicle;
import com.ai.apac.smartenv.omnic.service.IStatisticsService;
import com.ai.apac.smartenv.omnic.vo.AlarmAmountInfoVO;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.omnic.dto.SummaryDataForPerson;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import org.springblade.core.log.exception.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/10/30 10:48 上午
 **/
@Service
public class StatisticsServiceImpl implements IStatisticsService {


    /**
     * 获取当日首页应出勤车辆数量/应出勤人员数量/事件总数/未处理告警数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public SummaryAmountForHome getHomeSummaryAmountToday(String tenantId) {
        SummaryAmountForHome summaryDataForHome = null;
        Tenant tenant = getTenant(tenantId);
        summaryDataForHome = new SummaryAmountForHome();
        summaryDataForHome.setTenantId(tenantId);
        summaryDataForHome.setTenantName(tenant.getTenantName());

        //获取应出勤车辆数量
        Integer workingCountForVehicle = ScheduleCache.getWorkingCountForVehicleToday(tenantId);
        summaryDataForHome.setWorkingVehicleCount(workingCountForVehicle);

        //获取应出勤人员数量
        Integer workingCountForPerson = ScheduleCache.getWorkingCountForPersonToday(tenantId);
        summaryDataForHome.setWorkingPersonCount(workingCountForPerson);

        //获取事件总数
        Integer eventCount = EventCache.getEventCountToday(tenantId);
        summaryDataForHome.setAssessmentEventCount(eventCount);

        //获取未处理告警数量
        Integer unHandleCount = AlarmInfoCache.getUnHandleAlarmCountToday(tenantId);
        summaryDataForHome.setTotalUnHandleAlarmCount(unHandleCount);

        return summaryDataForHome;
    }

    /**
     * 获取今日系统中所有关键数据汇总，综合监控和大屏使用
     *
     * @param tenantId
     * @return
     */
    @Override
    public SummaryAmount getSummaryAmountToday(String tenantId) {
        SummaryAmount summaryData = null;
        Tenant tenant = getTenant(tenantId);
        summaryData = new SummaryAmount();
        summaryData.setTenantId(tenantId);
        summaryData.setTenantName(tenant.getTenantName());

        //获取车辆数量
        Integer vehicleCount = VehicleCache.getNormalVehicleCount(tenantId);
        summaryData.setVehicleCount(vehicleCount);

        //获取人员数量
        Integer personCount = PersonCache.getActivePersonCount(tenantId);
        summaryData.setPersonCount(personCount);

        //获取中转站数量
        Integer facilityCount = FacilityCache.getFacilityCount(tenantId);
        summaryData.setFacilityCount(facilityCount);

        //获取垃圾桶数量
        Integer trashCount = AshcanCache.getAshcanCount(tenantId);
        summaryData.setTrashCount(trashCount);

        //获取公厕数量
        Integer toiletCount = ToiletCache.getToiletCount(tenantId);
        summaryData.setToiletCount(toiletCount);

        //获取所有事件数量
        Integer eventCount = EventCache.getEventCountToday(tenantId);
        summaryData.setAssessmentEventCount(eventCount);

        //获取所有告警数量(所有未处理告警/所有车辆未处理告警数量/所有人员未处理告警数量)
        Integer unHandleCount = AlarmInfoCache.getUnHandleAlarmCountToday(tenantId);
        summaryData.setTotalUnHandleAlarmCount(unHandleCount);

        return summaryData;
    }

    /**
     * 获取今日告警数据汇总
     *
     * @param tenantId
     * @return
     */
    @Override
    public AlarmAmountInfoVO getSummaryAlarmAmountToday(String tenantId) {
        AlarmAmountInfoVO alarmAmountInfo = new AlarmAmountInfoVO();
        AlarmAmountVO alarmAmountVO = AlarmInfoCache.getSummaryAlarmAmount(tenantId);
        BeanUtils.copyProperties(alarmAmountVO, alarmAmountInfo);
        return alarmAmountInfo;
    }

    /**
     * 根据租户ID获取租户信息
     *
     * @param tenantId
     * @return
     */
    private Tenant getTenant(String tenantId) {
        Tenant tenant = TenantCache.getTenantById(tenantId);
        if (tenant == null) {
            throw new ServiceException("未找到租户信息");
        }
        return tenant;
    }
}
