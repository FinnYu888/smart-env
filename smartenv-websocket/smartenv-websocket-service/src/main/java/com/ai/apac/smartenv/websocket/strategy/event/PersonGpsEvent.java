package com.ai.apac.smartenv.websocket.strategy.event;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.common.enums.PersonStatusImgEnum;
import com.ai.apac.smartenv.common.utils.GPSUtil;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.PersonController;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonPositionVO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonTrackRealTimeVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IDeviceService;
import com.ai.apac.smartenv.websocket.service.IStreageService;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.ai.apac.smartenv.websocket.strategy.BaseWebsocketPushStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: PersonGpsEvent
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  14:42    panfeng          v1.0.0             修改原因
 */
@Component
@Slf4j
@AllArgsConstructor
public class PersonGpsEvent extends BaseWebsocketPushStrategy<String> {


    @Autowired
    private IStreageService streageService;
    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.PERSON_GPS_EVENT;
    }

    @Override
    public void strategy(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        streageService.handlePersonPositionTask(baseWsMonitorEventDTO);
        streageService.handlePersonTrackTask(baseWsMonitorEventDTO);

    }


}
