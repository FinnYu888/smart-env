/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.arrange.feign;

import com.ai.apac.smartenv.arrange.entity.*;
import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IScheduleClient.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: zhaoaj
 * @date: 2020年2月20日 下午8:47:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 * 2020年2月20日     zhaoaj           v1.0.0               修改原因
 */
@FeignClient(
        value = ApplicationConstant.APPLICATION_ARRANGE_NAME,
        fallback = IScheduleClientFallback.class
)
public interface IScheduleClient {

    String API_PREFIX = "/client";
    String UNFINISH_SCHEDULE_BY_ENTITY = API_PREFIX + "/unfinish-schedule-by-entity";
    String CHECK_NOW_NEED_WORK = API_PREFIX + "/check-now-need-work";
    String CHECK_TODAY_NEED_WORK = API_PREFIX + "/check-today-need-work";
    String CHECK_NEED_WORK = API_PREFIX + "/check-need-work";
    String CHECK_TODAY_NEED_WORK_MAP = API_PREFIX + "/check-today-need-work-map";
    String UNBIND_SCHEDULE = API_PREFIX + "/unbind-schedule";
    String COUNT_VEHICLE_FOR_TODAY = API_PREFIX + "/count-vehicle-for-today";
    String LIST_VEHICLE_FOR_TODAY = API_PREFIX + "/list-vehicle-for-today";


    String LIST_VEHICLE_FOR_NOW = API_PREFIX + "/list-vehicle-for-now";
    String LIST_PERSON_FOR_NOW = API_PREFIX + "/list-person-for-now";
    String LIST_ENTITY_FOR_NOW = API_PREFIX + "/list-entity-for-now";


    String COUNT_PERSON_FOR_TODAY = API_PREFIX + "/count-person-for-today";
    String LIST_PERSON_FOR_TODAY = API_PREFIX + "/list-person-for-today";
    String LIST_ALL_SCHEDULE = API_PREFIX + "/list-all-schedule";
    String LIST_ALL_SCHEDULE_OBJECT_BY_DATE = API_PREFIX + "/list-all-schedule-object-by-date";
    String SCHEDULE_BY_ID = API_PREFIX + "/schedule-by-id";
    String SCHEDULE_OBJECT_BY_ID = API_PREFIX + "/schedule-object-by-id";
    String SCHEDULE_OBJECT_BY_ENTITY_AND_DATE = API_PREFIX + "/schedule-object-by-entity-and-date";
    String SCHEDULE_WORK = API_PREFIX + "/schedule-work";
    String SCHEDULE_AttENDANCE_DETAIL_BY_ANNENDANCE_ID = API_PREFIX + "/schedule-attendance-detail-by-annendance-id";
    String ADD_VEHICLE_ATTENDANCE = API_PREFIX + "/addVehicleAttendance";
    String LIST_ALL_SCHEDULE_OBJECT_BY_DATE_TYPE = API_PREFIX + "/list-all-schedule-object-by-date-type";
    String GET_ATTENDANCE = API_PREFIX + "/get-attendance";
    String GET_ATTENDANCE_BY_DATE = API_PREFIX + "/get-attendance-by-date";
    String LIST_SCHEDULE_OBJECT = API_PREFIX + "/list-schedule-object";
    String LIST_ALL_SCHEDULE_OBJECT_BY_SCHEDULE_ID = API_PREFIX + "/list-all-schedule-object-by-schedule-id";
    String LIST_SCHEDULE_OBJECT_BY_CONDITION = API_PREFIX + "/list-schedule-object-by-condition";

    /**
     * @param entityId
     * @param entityType
     * @return
     * @Function: IScheduleClient::listUnfinishScheduleByEntity
     * @Description: 获取车辆或人未完成的考勤
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月20日 下午6:31:08
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping(UNFINISH_SCHEDULE_BY_ENTITY)
    R<List<ScheduleObject>> listUnfinishScheduleByEntity(@RequestParam("entityId") Long entityId, @RequestParam("entityType") String entityType);

    /**
     * @param entityId
     * @param entityType 1为车，，2为人
     * @return
     * @Function: IScheduleClient::checkNowNeedWork
     * @Description: 校验现在是否需要工作
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年2月20日 下午8:24:26
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping(CHECK_NOW_NEED_WORK)
    R<Boolean> checkNowNeedWork(@RequestParam("entityId") Long entityId, @RequestParam("entityType") String entityType);

    @GetMapping(CHECK_TODAY_NEED_WORK)
    R<Boolean> checkTodayNeedWork(@RequestParam("entityId") Long entityId, @RequestParam("entityType") String entityType);

    /**
     * @param entityId
     * @param entityType 1为车，，2为人
     * @return
     * @Function: IScheduleClient::checkNeedWork
     * @Description: 校验指定时间是否需要工作
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020-2-28 16:21:47
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping(CHECK_NOW_NEED_WORK)
    R<Boolean> checkNeedWork(@RequestParam("entityId") Long entityId, @RequestParam("entityType") String entityType,
                             @RequestParam("checkTime") Date checkTime);

    /**
     * @param entityId
     * @param entityType
     * @return
     * @Function: IScheduleClient::unbindSchedule
     * @Description: 解绑排班
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月3日 下午2:51:34
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @PostMapping(UNBIND_SCHEDULE)
    R<Boolean> unbindSchedule(@RequestParam("entityId") Long entityId, @RequestParam("entityType") String entityType);

    /**
     * @return
     * @Function: IScheduleClient::countVehicleForToday
     * @Description: 今日上班车辆数
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月3日 下午2:51:44
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping(COUNT_VEHICLE_FOR_TODAY)
    R<Integer> countVehicleForToday(@RequestParam String tenantId);

    /*
     * 一天会有多个排班，若是统计出勤数量，注意去重
     */
    @GetMapping(LIST_VEHICLE_FOR_TODAY)
    R<List<ScheduleObject>> listVehicleForToday(@RequestParam String tenantId);

    /*
     * 当前时间目前不会有多个排班，不用去重
     */
    @GetMapping(LIST_VEHICLE_FOR_NOW)
    R<List<ScheduleObject>> listVehicleForNow(@RequestParam String tenantId);

    @GetMapping(LIST_PERSON_FOR_NOW)
    R<List<ScheduleObject>> listPersonForNow(@RequestParam String tenantId);

    @GetMapping(LIST_ENTITY_FOR_NOW)
    R<List<ScheduleObject>> listEntityForNow(@RequestParam String tenantId);


    /**
     * @return
     * @Function: IScheduleClient::countPersonForToday
     * @Description: 今日上班人员数
     * @version: v1.0.0
     * @author: zhaoaj
     * @date: 2020年3月3日 下午2:51:58
     * <p>
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     */
    @GetMapping(COUNT_PERSON_FOR_TODAY)
    R<Integer> countPersonForToday(@RequestParam String tenantId);

    @GetMapping(LIST_PERSON_FOR_TODAY)
    R<List<ScheduleObject>> listPersonForToday(@RequestParam String tenantId);

    @GetMapping(LIST_ALL_SCHEDULE)
    R<List<Schedule>> listAllSchedule();

    @GetMapping(LIST_ALL_SCHEDULE_OBJECT_BY_DATE)
    R<List<ScheduleObject>> listAllScheduleObjectByDate(@RequestParam("date") LocalDate date);

    @GetMapping(SCHEDULE_OBJECT_BY_ID)
    R<ScheduleObject> getScheduleObjectById(@RequestParam("scheduleObjectId") Long scheduleObjectId);

    @GetMapping(SCHEDULE_BY_ID)
    R<Schedule> getScheduleById(@RequestParam("scheduleId") Long scheduleId);


    @GetMapping(LIST_ALL_SCHEDULE_OBJECT_BY_DATE_TYPE)
    R<List<ScheduleObject>> listAllScheduleObjectByDateAndType(@RequestParam String date, @RequestParam String entityType);

    @GetMapping(SCHEDULE_OBJECT_BY_ENTITY_AND_DATE)
	R<List<ScheduleObject>> getScheduleObjectByEntityAndDate(@RequestParam("entityId") Long entityId,
			@RequestParam("entityType") String entityType, @RequestParam("scheduleDate") String scheduleDate);

    @PostMapping(SCHEDULE_WORK)
    R saveScheduleWork(@RequestBody ScheduleWork scheduleWork);

    @GetMapping(SCHEDULE_AttENDANCE_DETAIL_BY_ANNENDANCE_ID)
    R<List<ScheduleAttendanceDetail>> getAttendanceDetailListByAttendanceId(@RequestParam("id") Long id);

//
//	@PostMapping(ADD_VEHICLE_ATTENDANCE)
//	R<String> addVehicleAttendance(@RequestBody  ScheduleAttendanceVO attendanceVO);

    @PostMapping(GET_ATTENDANCE)
    R<List<ScheduleAttendance>> getAttendance(@RequestBody ScheduleAttendance scheduleAttendance);

    @PostMapping(GET_ATTENDANCE_BY_DATE)
    R<List<ScheduleAttendance>> getAttendanceByDate(@RequestBody ScheduleAttendance scheduleAttendance, @RequestParam("date") String date);

    @PostMapping(CHECK_TODAY_NEED_WORK_MAP)
    R<Map<Long, Boolean>> checkTodayNeedWorkMap(@RequestBody List<Long> scheduleVehicleIdList, @RequestParam("entityType") String entityType);

    @PostMapping(LIST_SCHEDULE_OBJECT)
    R<List<ScheduleObject>> listScheduleObject(@RequestParam("entityType") String entityType, @RequestParam("beginDate") LocalDate date,
                                               @RequestParam("endDate") LocalDate localDate, @RequestParam("entityIdStr") String entityIdStr);

    @PostMapping(LIST_ALL_SCHEDULE_OBJECT_BY_SCHEDULE_ID)
    R<List<ScheduleObject>> listScheduleObjectByScheduleId(Long scheduleId);


    @PostMapping(LIST_SCHEDULE_OBJECT_BY_CONDITION)
    R<List<ScheduleObject>> listScheduleObjectByCondition(@RequestBody ScheduleObject scheduleObject);
}
