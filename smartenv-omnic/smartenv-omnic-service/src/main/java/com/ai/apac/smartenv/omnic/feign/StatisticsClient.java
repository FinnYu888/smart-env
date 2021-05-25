package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.service.IStatisticsService;
import com.ai.apac.smartenv.omnic.vo.AlarmAmountInfoVO;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/3 7:46 下午
 **/
@RestController
@RequiredArgsConstructor
public class StatisticsClient implements IStatisticsClient{

    @Autowired
    private IStatisticsService statisticsService;

    /**
     * 获取当日首页应出勤车辆数量/应出勤人员数量/事件总数/未处理告警数量
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(getHomeSummaryDataToday)
    public R<SummaryAmountForHome> getHomeSummaryAmountToday(@RequestParam String tenantId) {
        return R.data(statisticsService.getHomeSummaryAmountToday(tenantId));
    }

    /**
     * 获取今日系统中所有关键数据汇总，综合监控和大屏使用
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(getSummaryDataToday)
    public R<SummaryAmount> getSummaryAmountToday(@RequestParam String tenantId) {
        return R.data(statisticsService.getSummaryAmountToday(tenantId));
    }

    /**
     * 获取今日告警数据汇总
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping(getSummaryAlarmAmountToday)
    public R<AlarmAmountInfoVO> getSummaryAlarmAmountToday(@RequestParam String tenantId) {
        return R.data(statisticsService.getSummaryAlarmAmountToday(tenantId));
    }
}
