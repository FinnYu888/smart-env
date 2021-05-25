package com.ai.apac.smartenv.websocket.strategy.event;

import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.common.enums.VehicleStatusImgEnum;
import com.ai.apac.smartenv.common.utils.GPSUtil;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.common.PositionDTO;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.controller.VehicleController;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehiclePositionVO;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleTrackRealTimeVO;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehiclePositionChangeEvent
 * @Description: 车辆位置变更时候的策略
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  2020/10/28    panfeng          v1.0.0             修改原因
 */
@Component
@AllArgsConstructor
@Slf4j
public class VehicleGpsEvent extends BaseWebsocketPushStrategy<String> {

    @Autowired
    private IStreageService streageService;

    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.VEHICLE_GPS_EVENT;
    }

    @Override
    public void strategy(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        streageService.handleVehiclePositionTask(baseWsMonitorEventDTO);
        streageService.handleVehicleTrackTask(baseWsMonitorEventDTO);
    }


}
