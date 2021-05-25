package com.ai.apac.smartenv.websocket.service.impl;

import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.facility.feign.IFacilityClient;
import com.ai.apac.smartenv.inventory.feign.IResOrderClient;
import com.ai.apac.smartenv.websocket.module.main.vo.ResOrder4HomeVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.service.IEventService;
import com.ai.apac.smartenv.websocket.service.IResOrderService;
import com.ai.apac.smartenv.websocket.task.EventTask;
import com.ai.apac.smartenv.websocket.task.ResOrderTask;
import org.springblade.core.tool.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/19 Asiainfo
 *
 * @ClassName: ResOrderService
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/19  17:19    zhanglei25          v1.0.0             修改原因
 */
@Service
public class ResOrderService implements IResOrderService {

    @Autowired
    private WebSocketTaskService webSocketTaskService;

    @Autowired
    private IBaseService baseService;


    @Autowired
    private IResOrderClient resOrderClient;

    @Override
    public void pushLast6Order(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        ResOrderTask task = new ResOrderTask(websocketTask);
        task.run();
    }

    @Override
    public List<ResOrder4HomeVO>  getLast6Order(String tenantId,String userId) {
        List<com.ai.apac.smartenv.inventory.vo.ResOrder4HomeVO> resOrder4HomeVOList = resOrderClient.getlastOrders(tenantId,userId).getData();

        return BeanUtil.copyProperties(resOrder4HomeVOList,ResOrder4HomeVO.class);

    }
}
