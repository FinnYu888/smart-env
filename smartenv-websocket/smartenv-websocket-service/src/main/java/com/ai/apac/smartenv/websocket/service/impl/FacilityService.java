package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IFacilityService;
import com.ai.apac.smartenv.websocket.task.LastGarbageAmountByRegionTask;
import com.ai.apac.smartenv.websocket.task.LastGarbageAmountTask;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/17 Asiainfo
 *
 * @ClassName: FacilityService
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/17  11:00    zhanglei25          v1.0.0             修改原因
 */
@Service
public class FacilityService implements IFacilityService {

    @Autowired
    private WebSocketTaskService webSocketTaskService;

    @Autowired
    private IBaseService baseService;

    @Autowired
    private IFacilityClient facilityClient;

    @Override
    public void pushLastGarbageAmount(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        LastGarbageAmountTask task = new LastGarbageAmountTask(websocketTask);
        task.run();
    }

    @Override
    public List<LastDaysGarbageAmountVO> getLastGarbageAmount(Integer days, String tenantId) {
        List<com.ai.apac.smartenv.facility.vo.LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList =  facilityClient.getLastDaysGarbageAmount(days,tenantId).getData();
        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList1 = new ArrayList<LastDaysGarbageAmountVO>();
        if(ObjectUtil.isNotEmpty(lastDaysGarbageAmountVOList) && lastDaysGarbageAmountVOList.size() > 0){
            lastDaysGarbageAmountVOList1 = BeanUtil.copyProperties(lastDaysGarbageAmountVOList,LastDaysGarbageAmountVO.class);
        }
        return lastDaysGarbageAmountVOList1;


    }

    @Override
    public void pushLastGarbageAmountByRegion(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        LastGarbageAmountByRegionTask task = new LastGarbageAmountByRegionTask(websocketTask);
        task.run();
    }

    @Override
    public List<LastDaysRegionGarbageAmountVO> getLastGarbageAmountByRegion(String tenantId) {
        List<LastDaysRegionGarbageAmountVO> lastDaysGarbageAmountList = new ArrayList<LastDaysRegionGarbageAmountVO>();
        List<com.ai.apac.smartenv.facility.vo.LastDaysRegionGarbageAmountVO> lastDaysRegionGarbageAmountVOList = facilityClient.getLastDaysGarbageAmountByRegion(7,tenantId).getData();
        return BeanUtil.copyProperties(lastDaysRegionGarbageAmountVOList,LastDaysRegionGarbageAmountVO.class);
    }


}
