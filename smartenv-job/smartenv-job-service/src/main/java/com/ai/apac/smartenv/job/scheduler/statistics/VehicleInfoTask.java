package com.ai.apac.smartenv.job.scheduler.statistics;

import com.ai.apac.smartenv.statistics.feign.IStatisticsClient;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class VehicleInfoTask {

	private IStatisticsClient statisticsClient;
	
	/*
	 */
//	@Scheduled(cron = "0 20 17 * * ?")
//	@Scheduled(fixedRate = 600000)
	public void syncVehicleInfo() {
//		LocalDateTime now = LocalDateTime.now().minusDays(1);
		LocalDateTime now = LocalDateTime.now();
    	String date = DateUtil.format(now, DatePattern.NORM_DATE_PATTERN);
		statisticsClient.syncVehicleInfo(date);
	}
}
