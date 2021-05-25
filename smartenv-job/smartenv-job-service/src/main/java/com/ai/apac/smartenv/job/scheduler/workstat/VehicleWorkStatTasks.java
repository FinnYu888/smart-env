package com.ai.apac.smartenv.job.scheduler.workstat;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.job.entity.JobExecCfg;
import com.ai.apac.smartenv.job.scheduler.arrange.task.ScheduleBeginRun;
import com.ai.apac.smartenv.job.scheduler.workstat.task.VehicleWorkStatRun;
import com.ai.apac.smartenv.job.service.IJobExecCfgService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class VehicleWorkStatTasks {

    @Autowired
    private IJobExecCfgService jobExecCfgService;

    public static TaskScheduler getTaskScheduler() {
        return SpringUtil.getBean(TaskScheduler.class);
    }

    @Scheduled(cron = "0 0 0 ? * *") //@Scheduled(fixedDelay = 60000)   @Scheduled(cron = "0 0 0 ? * *")
    public void scheduleEveryDayTask() {
        log.info("开始执行VehicleWorkStatTasks每天定时任务");
        Date now = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(now);
        QueryWrapper<JobExecCfg> wrapper = new QueryWrapper<JobExecCfg>();
        wrapper.lambda().eq(JobExecCfg::getJobName,"VehicleWorkStatTasks");
        List<JobExecCfg> jobExecCfgList = jobExecCfgService.list(wrapper);
        log.info("jobExecCfgList----------------"+jobExecCfgList);
        if(ObjectUtil.isNotEmpty(jobExecCfgList) && jobExecCfgList.size() > 0 ){
            for(JobExecCfg jobExecCfg:jobExecCfgList) {
                TaskScheduler scheduler = getTaskScheduler();
                Integer hour = jobExecCfg.getExecuteTime().getHours();
                Integer min = jobExecCfg.getExecuteTime().getMinutes();
                Integer sec = jobExecCfg.getExecuteTime().getSeconds();
                cal1.set(Calendar.HOUR_OF_DAY, hour);
                cal1.set(Calendar.MINUTE, min);
                cal1.set(Calendar.SECOND, sec);
                Date parse = cal1.getTime();
                log.info("vehicleWorkStatRun--------parse--------"+TimeUtil.getYYYYMMDDHHMMSS(parse));
                log.info("vehicleWorkStatRun--------jobExecCfg.getParam1()--------"+jobExecCfg.getParam1());
                log.info("vehicleWorkStatRun--------jobExecCfg.getParam2()--------"+jobExecCfg.getParam2());
                log.info("vehicleWorkStatRun--------jobExecCfg.getParam3()--------"+Arrays.asList(jobExecCfg.getParam3().split("\\|")));
                VehicleWorkStatRun vehicleWorkStatRun = new VehicleWorkStatRun(jobExecCfg.getParam1(), jobExecCfg.getParam2(), Arrays.asList(jobExecCfg.getParam3().split(",")));
                scheduler.schedule(vehicleWorkStatRun, parse);
            }
        }
    }
}
