package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.arrange.entity.ScheduleObject;
import com.ai.apac.smartenv.system.entity.Region;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName IWorkAreaService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/5/28 18:42
 * @Version 1.0
 */
public interface IWorkAreaService {

    Future<List<Region>> listRegionByEntityId(Long entityId,Long entityType,String tenantId);

}
