package com.ai.apac.smartenv.job.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.job.scheduler.arrange.ScheduledTasks;
import com.ai.apac.smartenv.job.scheduler.arrange.task.ScheduleBeginRun;
import com.ai.apac.smartenv.job.scheduler.arrange.task.ScheduleEndRun;
import com.ai.apac.smartenv.job.service.IScheduleService;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ScheduleServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  14:47    panfeng          v1.0.0             修改原因
 */
@Service
public class ScheduleServiceImpl  implements IScheduleService {


    /**
     * 添加一个今天的排班任务
     * @param schedule
     * @return
     */
    @Override
    public Boolean addTodaySchedule(Schedule schedule){
        TaskScheduler taskScheduler = ScheduledTasks.getTaskScheduler();
        Calendar current = Calendar.getInstance();
        List<Schedule> scheduleList=new ArrayList<>();
        scheduleList.add(schedule);
        Date scheduleBeginTime = schedule.getScheduleBeginTime();
        Date scheduleEndTime = schedule.getScheduleEndTime();
        Date breaksBeginTime = schedule.getBreaksBeginTime();
        Date breaksEndTime = schedule.getBreaksEndTime();
        if (scheduleBeginTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(scheduleBeginTime);
            calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
            String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);
            ScheduleBeginRun scheduleBeginRun = new ScheduleBeginRun(scheduleList,format);
            taskScheduler.schedule(scheduleBeginRun,calendar.getTime());
        }
        if (breaksEndTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(breaksEndTime);
            calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
            String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);
            ScheduleBeginRun scheduleBeginRun = new ScheduleBeginRun(scheduleList,format);
            taskScheduler.schedule(scheduleBeginRun,calendar.getTime());
        }
        if (scheduleEndTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(scheduleEndTime);
            calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
            String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);
            ScheduleEndRun scheduleBeginRun = new ScheduleEndRun(scheduleList,format);
            taskScheduler.schedule(scheduleBeginRun,calendar.getTime());

        }
        if (breaksBeginTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(breaksBeginTime);
            calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
            String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);
            ScheduleEndRun scheduleBeginRun = new ScheduleEndRun(scheduleList,format);
            taskScheduler.schedule(scheduleBeginRun,calendar.getTime());
        }
        return true;

    }


}
