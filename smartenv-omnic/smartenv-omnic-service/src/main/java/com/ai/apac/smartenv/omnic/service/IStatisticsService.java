package com.ai.apac.smartenv.omnic.service;

import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.dto.SummaryDataForVehicle;
import com.ai.apac.smartenv.omnic.dto.SummaryDataForPerson;
import com.ai.apac.smartenv.omnic.vo.AlarmAmountInfoVO;

/**
 * @author qianlong
 * @Description 数据统计服务
 * @Date 2020/10/30 10:34 上午
 **/
public interface IStatisticsService {

    /**
     * 获取当日首页应出勤车辆数量/应出勤人员数量/事件总数/未处理告警数量
     * @param tenantId
     * @return
     */
    SummaryAmountForHome getHomeSummaryAmountToday(String tenantId);

    /**
     * 获取今日系统中所有关键数据汇总，综合监控和大屏使用
     * @param tenantId
     * @return
     */
    SummaryAmount getSummaryAmountToday(String tenantId);

    /**
     * 获取今日告警数据汇总
     * @param tenantId
     * @return
     */
    AlarmAmountInfoVO getSummaryAlarmAmountToday(String tenantId);


}
