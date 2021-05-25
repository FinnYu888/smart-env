package com.ai.apac.smartenv.job.cache;

import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description 业务缓存定时刷新任务
 * @Date 2020/4/6 11:04 下午
 **/
@Component
@AllArgsConstructor
public class BizCacheTask {

    /**
     * 每隔5秒检查是否需要刷新
     */
//    @Scheduled(fixedRate = 5000)
//    public void reloadDevice() {
//        DeviceCache.reload();
//    }

    /**
     * 每隔5秒检查是否需要刷新
     */
//    @Scheduled(fixedRate = 5000)
//    public void reloadVehicle() {
//        VehicleCache.reload();
//    }

    /**
     * 每隔5秒检查是否需要刷新
     */
//    @Scheduled(fixedRate = 5000)
//    public void reloadPerson() {
//        PersonCache.reload();
//    }

    /**
     * 每隔5秒检查是否需要刷新
     */
//    @Scheduled(fixedRate = 5000)
//    public void reloadSchedule() {
//        ScheduleCache.reload();
//    }
}
