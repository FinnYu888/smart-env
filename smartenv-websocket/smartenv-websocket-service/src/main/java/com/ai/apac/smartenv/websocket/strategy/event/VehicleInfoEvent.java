package com.ai.apac.smartenv.websocket.strategy.event;

import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.controller.VehicleController;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleDetailVO;
import com.ai.apac.smartenv.websocket.service.IOmnicService;
import com.ai.apac.smartenv.websocket.service.IStreageService;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.ai.apac.smartenv.websocket.service.impl.VehicleService;
import com.ai.apac.smartenv.websocket.strategy.BaseWebsocketPushStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: VehicleInfoEvent
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  14:44    panfeng          v1.0.0             修改原因
 */
@Component
@Slf4j
public class VehicleInfoEvent extends BaseWebsocketPushStrategy<String> {

    @Autowired
    private IStreageService streageService;

    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.VEHICLE_INFO_EVENT;
    }

    @Override
    public void strategy(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO) {
        log.info("处理车辆信息变更推送任务,内容:"+ JSONUtil.toJsonStr(baseWsMonitorEventDTO));

        streageService.handleHomePageCountData(baseWsMonitorEventDTO);
        streageService.handleVehicleDetailData(baseWsMonitorEventDTO);
        streageService.handlePolymerizationCountData(baseWsMonitorEventDTO);
        streageService.handleVehicleMonitorCountTask(baseWsMonitorEventDTO);
    }
}
