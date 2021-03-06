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
        // 1.???????????????????????????????????????????????????Mongo????????????????????????????????????????????????????????????????????????????????????????????????
        // 2.
        // spring ?????? ???????????????
        log.info("??????????????????????????????");

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
            //??????????????? ????????????????????????????????????????????????toWorkTimeScheduleMap?????????????????????offWorkTimeScheduleMap
            // ????????????????????????????????? ???????????????????????????????????????????????????????????????????????????
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

//        //?????????
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


        //???????????????????????????????????????????????????????????????
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
//        log.info("????????????????????????");

    }






    public void reloadScheduleEveryDayTask() {
        // 1.???????????????????????????????????????????????????Mongo????????????????????????????????????????????????????????????????????????????????????????????????
        // 2.
        // spring ?????? ???????????????
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
            //??????????????? ????????????????????????????????????????????????toWorkTimeScheduleMap?????????????????????offWorkTimeScheduleMap
            // ????????????????????????????????? ???????????????????????????????????????????????????????????????????????????
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

        //???????????????????????????????????????????????????????????????
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
