package com.ai.apac.smartenv.omnic.feign;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.vo.AlarmAmountInfoVO;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qianlong
 * @Description 数据统计相关服务
 * @Date 2020/10/30 5:02 下午
 **/
@FeignClient(
        value = ApplicationConstant.APPLICATION_OMNIC_NAME,
        fallback = IStatisticsClientFallback.class
)
public interface IStatisticsClient {

    String client = "/client";

    String getHomeSummaryDataToday = client + "/homeSummaryDataToday";
    String getSummaryDataToday = client + "/summaryDataToday";
    String getSummaryAlarmAmountToday = client + "/summaryAlarmAmountToday";

    /**
     * 获取当日首页应出勤车辆数量/应出勤人员数量/事件总数/未处理告警数量
     * @param tenantId
     * @return
     */
    @GetMapping(getHomeSummaryDataToday)
    R<SummaryAmountForHome> getHomeSummaryAmountToday(@RequestParam String tenantId);

    /**
     * 获取今日系统中所有关键数据汇总，综合监控和大屏使用
     * @param tenantId
     * @return
     */
    @GetMapping(getSummaryDataToday)
    R<SummaryAmount> getSummaryAmountToday(@RequestParam String tenantId);

    /**
     * 获取今日告警数据汇总
     * @param tenantId
     * @return
     */
    @GetMapping(getSummaryAlarmAmountToday)
    R<AlarmAmountInfoVO> getSummaryAlarmAmountToday(@RequestParam String tenantId);
}
