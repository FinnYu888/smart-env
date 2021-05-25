package com.ai.apac.smartenv.websocket.strategy.event;

import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IOmnicService;
import com.ai.apac.smartenv.websocket.service.IStreageService;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.ai.apac.smartenv.websocket.strategy.BaseWebsocketPushStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: ArrangeEvent
 * @Description: 排班变更策略
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  11:37    panfeng          v1.0.0             修改原因
 */
@Component
@Slf4j
public class ArrangeEvent extends BaseWebsocketPushStrategy {

    @Autowired
    private IStreageService streageService;



    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.ARRANGE_EVENT;
    }

    @Override
    public void strategy(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {

            streageService.handleHomePageCountData(baseWsMonitorEventDTO);
            streageService.handlePolymerizationCountData(baseWsMonitorEventDTO);
        // 查询所有task type 。然后调用获取数据。把数据send出去。调用omnic 的方法
    }




}
