package com.ai.apac.smartenv.job.feign;

import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IScheduleClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  15:03    panfeng          v1.0.0             修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_JOB_NAME,
        fallback = IJobScheduleClientFallback.class
)
public interface IJobScheduleClient {
    String FEIGN_CLIENT_PREFIX="/client/job/IScheduleClient";
    String RELOAD_ALL_TODAY_SCHEDULE=FEIGN_CLIENT_PREFIX+"/reloadAllTodaySchedule";
    String ADD_TODAY_SCHEDULE=FEIGN_CLIENT_PREFIX+"/addTodaySchedule";

    @GetMapping(RELOAD_ALL_TODAY_SCHEDULE)
    R reloadAllTodaySchedule();

    @PostMapping(ADD_TODAY_SCHEDULE)
    R addTodaySchedule(@RequestBody Schedule schedule);
}
