package com.ai.apac.smartenv.job.scheduler.arrange.task;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.ai.apac.smartenv.arrange.entity.Schedule;
import com.ai.apac.smartenv.job.cache.DistributedLockHandler;
import com.ai.apac.smartenv.job.dto.JobClusterLockDto;
import com.ai.apac.smartenv.job.mq.JobProducerSource;
import com.ai.apac.smartenv.system.cache.TenantCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.websocket.feign.IBigScreenDataClient;
import com.ai.apac.smartenv.websocket.feign.IHomeDataClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.SpringUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ScheduleBeginRun implements Runnable{

    public List<Schedule> schedules;
    public String time;


    @Override
    public void run() {

        //获取锁
        DistributedLockHandler distributedLockHandler=SpringUtil.getBean(DistributedLockHandler.class);
        JobClusterLockDto joblock=new JobClusterLockDto();
        joblock.setNextUUID(UUID.randomUUID().toString());
        joblock.setIsInvalid(false);
        Date parse = DateUtil.parse(time, DatePattern.PURE_DATETIME_PATTERN);
        Date expirationTime = new Date(parse.getTime() + (5 * 60 * 1000));
        joblock.setExpirationTime(expirationTime);
        joblock.setLockName("scheduleBegin:"+time);
        joblock.setSeconds(24*60L);
        boolean lock = distributedLockHandler.getScheduleLock(joblock);
        if (!lock){
            return;
        }

        JobProducerSource producerSource = SpringUtil.getBean(JobProducerSource.class);

        boolean success=false;
        if (producerSource!=null){
            Message<List<Schedule>> message = MessageBuilder.withPayload(schedules).build();
            success = producerSource.arrangeBeginOutput().send(message);
        }

        if (success){
            distributedLockHandler.releaseLock(joblock);
        }
        //把更新的数据推送给首页和大屏
        List<Tenant> tenantList = TenantCache.getAllTenant();
        if(ObjectUtil.isNotEmpty(tenantList) && tenantList.size() > 0){
            IHomeDataClient homeDataClient = SpringUtil.getBean(IHomeDataClient.class);
            IBigScreenDataClient bigScreenDataClient = SpringUtil.getBean(IBigScreenDataClient.class);
            tenantList.forEach(Tenant->{
                homeDataClient.updateHomeCountRedis(Tenant.getTenantId());
                bigScreenDataClient.updateBigscreenCountRedis(Tenant.getTenantId());
            });
        }



    }
}
