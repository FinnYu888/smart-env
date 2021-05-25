package com.ai.apac.smartenv.job.scheduler.arrange;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.job.cache.DistributedLockHandler;
import com.ai.apac.smartenv.job.dto.JobClusterLockDto;
import com.ai.apac.smartenv.job.mq.JobProducerSource;
import com.ai.apac.smartenv.job.scheduler.arrange.task.ScheduleBeginRun;
import com.ai.apac.smartenv.job.scheduler.arrange.task.ScheduleEndRun;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.lock.LockType;
import org.springblade.core.redis.lock.RedisLockClient;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
@Slf4j
public class ScheduledTasks {
    @Autowired
    private MongoTemplate mongoTemplate;


    private IVehicleClient vehicleClient;

    private IPersonClient personClient;

    private IHomeDataClient homeDataClient;

    private IBigScreenDataClient bigScreenDataClient;

    @Autowired
    private JobProducerSource producerSource;


    @Autowired
    private IScheduleClient scheduleClient;


    @Autowired
    private IPolymerizationClient polymerizationClient;


    @Autowired
    private RedisLockClient redisLockClient;


    public static TaskScheduler getTaskScheduler() {
        return SpringUtil.getBean(TaskScheduler.class);
    }


//    @Scheduled(cron = "0 0 0 ? * *")
    public void scheduleEveryDayTask() {
        // 1.查询今天所有人员，车辆的排班，更新Mongo里面的人员车辆为休息。在排班时间启动定时任务，更新排班对应的状态
        // 2.
        // spring 内部 任务执行器
        log.info("开始执行每天定时任务");

        TaskScheduler scheduler = getTaskScheduler();
        //
        List<Schedule> allSchedule = scheduleClient.listAllSchedule().getData();


        if (CollectionUtil.isEmpty(allSchedule)){
            return;
        }

        Map<String, List<Schedule>> toWorkTimeScheduleMap = new HashMap<>();
        Map<String, List<Schedule>> offWorkTimeScheduleMap = new HashMap<>();

        Calendar current = Calendar.getInstance();

        for (Schedule schedule : allSchedule) {
            //取到所有的 上班时间，下班时间。上班时间放入toWorkTimeScheduleMap。下班时间放入offWorkTimeScheduleMap
            // 上班时间，休息结束时间 都为上班时间。下班时间，休息开始时间，都是结束时间
            Date scheduleBeginTime = schedule.getScheduleBeginTime();
            Date scheduleEndTime = schedule.getScheduleEndTime();


            Date breaksBeginTime = schedule.getBreaksBeginTime();
            Date breaksEndTime = schedule.getBreaksEndTime();
            if (scheduleBeginTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(scheduleBeginTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);



                List<Schedule> scheduleList = toWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    toWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);
            }
            if (breaksEndTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(breaksEndTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);

                List<Schedule> scheduleList = toWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    toWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);

            }
            if (scheduleEndTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(scheduleEndTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);

                List<Schedule> scheduleList = offWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    offWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);

            }
            if (breaksBeginTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(breaksBeginTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);

                List<Schedule> scheduleList = offWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    offWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);

            }
        }

//        //获取锁
//        DistributedLockHandler distributedLockHandler=SpringUtil.getBean(DistributedLockHandler.class);
//        JobClusterLockDto joblock=new JobClusterLockDto();
//        joblock.setNextUUID(UUID.randomUUID().toString());
//        joblock.setIsInvalid(false);
//        Date date = new Date();
//        Date expirationTime = new Date(date.getTime() + (5 * 60 * 1000));
//        joblock.setExpirationTime(expirationTime);
//        joblock.setLockName("scheduleJob:"+DateUtil.format(date,DatePattern.PURE_DATE_PATTERN));
//        joblock.setSeconds(24*60L);
//        boolean lock = distributedLockHandler.getScheduleLock(joblock);
//        if (lock){
//            polymerizationClient.initTreeDataToMongoDB();
//        }
//        distributedLockHandler.releaseLock(joblock);
        Date date = new Date();
        try {
            boolean b = redisLockClient.tryLock("lock:scheduleJob:" + DateUtil.format(date, DatePattern.PURE_DATE_PATTERN)
                    , LockType.FAIR, 120, 43200, TimeUnit.SECONDS
            );
            if (b){
                polymerizationClient.initTreeDataToMongoDB();
            }
        } catch (Exception e) {
            redisLockClient.unLock("lock:scheduleJob:" + DateUtil.format(date, DatePattern.PURE_DATE_PATTERN), LockType.FAIR);
        }


        //先初始化一次，避免直接插入导致的索引不生效
//
//        Set<String> toWorkTimeScheduleKeys = toWorkTimeScheduleMap.keySet();
//        for (String key : toWorkTimeScheduleKeys) {
//            List<Schedule> scheduleList = toWorkTimeScheduleMap.get(key);
//            Date parse = DateUtil.parse(key, DatePattern.PURE_DATETIME_PATTERN);
//            ScheduleBeginRun scheduleBeginRun = new ScheduleBeginRun(scheduleList,key);
//            scheduler.schedule(scheduleBeginRun,parse);
//        }
//
//
//        Set<String> offWorkTimeScheduleKeys = offWorkTimeScheduleMap.keySet();
//        for (String key : offWorkTimeScheduleKeys) {
//            List<Schedule> scheduleList = offWorkTimeScheduleMap.get(key);
//            Date parse = DateUtil.parse(key, DatePattern.PURE_DATETIME_PATTERN);
//            ScheduleEndRun scheduleEndRun=new ScheduleEndRun(scheduleList,key);
//            scheduler.schedule(scheduleEndRun,parse);
//        }
//        log.info("定时任务分配结束");

    }






    public void reloadScheduleEveryDayTask() {
        // 1.查询今天所有人员，车辆的排班，更新Mongo里面的人员车辆为休息。在排班时间启动定时任务，更新排班对应的状态
        // 2.
        // spring 内部 任务执行器
        TaskScheduler scheduler = getTaskScheduler();
        //
        List<Schedule> allSchedule = scheduleClient.listAllSchedule().getData();


        if (CollectionUtil.isEmpty(allSchedule)){
            return;
        }

        Map<String, List<Schedule>> toWorkTimeScheduleMap = new HashMap<>();
        Map<String, List<Schedule>> offWorkTimeScheduleMap = new HashMap<>();

        Calendar current = Calendar.getInstance();

        for (Schedule schedule : allSchedule) {
            //取到所有的 上班时间，下班时间。上班时间放入toWorkTimeScheduleMap。下班时间放入offWorkTimeScheduleMap
            // 上班时间，休息结束时间 都为上班时间。下班时间，休息开始时间，都是结束时间
            Date scheduleBeginTime = schedule.getScheduleBeginTime();
            Date scheduleEndTime = schedule.getScheduleEndTime();


            Date breaksBeginTime = schedule.getBreaksBeginTime();
            Date breaksEndTime = schedule.getBreaksEndTime();
            if (scheduleBeginTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(scheduleBeginTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);



                List<Schedule> scheduleList = toWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    toWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);
            }
            if (breaksEndTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(breaksEndTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);

                List<Schedule> scheduleList = toWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    toWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);

            }
            if (scheduleEndTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(scheduleEndTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);

                List<Schedule> scheduleList = offWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    offWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);

            }
            if (breaksBeginTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(breaksBeginTime);
                calendar.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DATE));
                String format = DateUtil.format(calendar.getTime(), DatePattern.PURE_DATETIME_PATTERN);

                List<Schedule> scheduleList = offWorkTimeScheduleMap.get(format);
                if (CollectionUtil.isEmpty(scheduleList)) {
                    scheduleList = new ArrayList<>();
                    offWorkTimeScheduleMap.put(format, scheduleList);
                }
                scheduleList.add(schedule);

            }
        }

        //先初始化一次，避免直接插入导致的索引不生效
        polymerizationClient.reloadTreeDataToMongoDb();

        Set<String> toWorkTimeScheduleKeys = toWorkTimeScheduleMap.keySet();
        for (String key : toWorkTimeScheduleKeys) {
            List<Schedule> scheduleList = toWorkTimeScheduleMap.get(key);
            Date parse = DateUtil.parse(key, DatePattern.PURE_DATETIME_PATTERN);
            ScheduleBeginRun scheduleBeginRun = new ScheduleBeginRun(scheduleList,key);
            scheduler.schedule(scheduleBeginRun,parse);
        }


        Set<String> offWorkTimeScheduleKeys = offWorkTimeScheduleMap.keySet();
        for (String key : offWorkTimeScheduleKeys) {
            List<Schedule> scheduleList = offWorkTimeScheduleMap.get(key);
            Date parse = DateUtil.parse(key, DatePattern.PURE_DATETIME_PATTERN);
            ScheduleEndRun scheduleEndRun=new ScheduleEndRun(scheduleList,key);
            scheduler.schedule(scheduleEndRun,parse);
        }

    }













}
