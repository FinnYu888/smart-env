package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.arrange.entity.ScheduleObject;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName IScheduleService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/3 19:07
 * @Version 1.0
 */
public interface IScheduleService {


    /**
     * 根据租户获取当前正在工作的人员个数
     * @return
     */
    Future<Integer> countWorkingPersonForToday(String tenantId);

    /**
     * 根据租户获取当前正在工作的车辆个数
     * @return
     */
    Future<Integer> countWorkingVehicleForToday(String tenantId);


    Future<List<ScheduleObject>> listEntityForNow(String tenantId);


}
