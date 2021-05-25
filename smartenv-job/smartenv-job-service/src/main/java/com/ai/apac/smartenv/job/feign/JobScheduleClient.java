package com.ai.apac.smartenv.job.feign;

import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.job.scheduler.arrange.ScheduledTasks;
import com.ai.apac.smartenv.job.service.IScheduleService;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ScheduleClient
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  15:07    panfeng          v1.0.0             修改原因
 */
//@ApiIgnore
@RestController
@RequiredArgsConstructor
public class JobScheduleClient implements IJobScheduleClient {


    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private IScheduleService scheduleService;

    /**
     * 重新加载今天的排班
     * @return
     */
    @Override
    @GetMapping(RELOAD_ALL_TODAY_SCHEDULE)
    public R reloadAllTodaySchedule(){
        scheduledTasks.reloadScheduleEveryDayTask();
        return R.success("success");
    }

    /**
     * 添加一个今天的排班
     * @param schedule
     * @return
     */
    @Override
    @PostMapping(ADD_TODAY_SCHEDULE)
    public R addTodaySchedule(@RequestBody Schedule schedule){
        scheduleService.addTodaySchedule(schedule);
        return R.success("success");
    }






}
