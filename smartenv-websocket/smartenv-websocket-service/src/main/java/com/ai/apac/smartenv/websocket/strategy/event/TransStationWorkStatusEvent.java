package com.ai.apac.smartenv.websocket.strategy.event;

import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.websocket.strategy.BaseWebsocketPushStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: TransStationWorkStatusEvent
 * @Description: 中转站工作状态变更
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  15:07    panfeng          v1.0.0             修改原因
 */
@Component
public class TransStationWorkStatusEvent extends BaseWebsocketPushStrategy<String> {

    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.TRANS_STATION_WORK_STATUS_EVENT;
    }

    @Override
    public void strategy(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {

    }
}
