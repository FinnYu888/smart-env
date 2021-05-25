package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.vo.AlarmAmountInfoVO;
import org.springblade.core.tool.api.R;

/**
 * @author qianlong
 */
public class IStatisticsClientFallback implements IStatisticsClient {

    /**
     * 获取当日首页应出勤车辆数量/应出勤人员数量/事件总数/未处理告警数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<SummaryAmountForHome> getHomeSummaryAmountToday(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取今日系统中所有关键数据汇总，综合监控和大屏使用
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<SummaryAmount> getSummaryAmountToday(String tenantId) {
        return R.fail("获取数据失败");
    }

    /**
     * 获取今日告警数据汇总
     *
     * @param tenantId
     * @return
     */
    @Override
    public R<AlarmAmountInfoVO> getSummaryAlarmAmountToday(String tenantId) {
        return R.fail("获取数据失败");
    }
}
