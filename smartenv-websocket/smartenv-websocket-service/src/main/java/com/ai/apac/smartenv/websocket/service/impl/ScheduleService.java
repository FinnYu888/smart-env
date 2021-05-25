package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.arrange.feign.IScheduleClient;
import com.ai.apac.smartenv.websocket.service.IScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName ScheduleService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/3 19:12
 * @Version 1.0
 */
@Slf4j
@Service
public class ScheduleService implements IScheduleService {

    @Autowired
    private IScheduleClient scheduleClient;

    @Override
    public Future<Integer> countWorkingPersonForToday(String tenantId) {
        R<Integer> countResult = scheduleClient.countPersonForToday(tenantId);
        if (countResult.isSuccess() && countResult.getData() != null) {
            return new AsyncResult<Integer>(countResult.getData());
        }
        return null;
    }

    @Override
    public Future<Integer> countWorkingVehicleForToday(String tenantId) {
        R<Integer> countResult = scheduleClient.countVehicleForToday(tenantId);
        if (countResult.isSuccess() && countResult.getData() != null) {
            return new AsyncResult<Integer>(countResult.getData());
        }
        return null;
    }

    @Override
    public Future<List<ScheduleObject>> listEntityForNow(String tenantId) {
        R<List<ScheduleObject>> result = scheduleClient.listEntityForNow(tenantId);
        if (result.isSuccess() && result.getData() != null) {
            return new AsyncResult<List<ScheduleObject>>(result.getData());
        }
        return null;
    }
}
