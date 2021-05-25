package com.ai.apac.smartenv.websocket.strategy.event;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.device.cache.DeviceCache;
import com.ai.apac.smartenv.device.cache.DeviceRelCache;
import com.ai.apac.smartenv.device.entity.DeviceInfo;
import com.ai.apac.smartenv.device.entity.DeviceRel;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.controller.PolymerizationController;
import com.ai.apac.smartenv.websocket.module.polymerization.vo.PolymerizationCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IOmnicService;
import com.ai.apac.smartenv.websocket.service.IStreageService;
import com.ai.apac.smartenv.websocket.service.IWebSocketTaskService;
import com.ai.apac.smartenv.websocket.strategy.BaseWebsocketPushStrategy;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmEvent
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  11:41    panfeng          v1.0.0             修改原因
 */
@Component
public class DeviceStatusEvent extends BaseWebsocketPushStrategy {


    @Autowired
    private IStreageService streageService;

    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.DEVICE_STATUS_EVENT;
    }

    /**
     * eventObject 为设备编码
     * @param baseWsMonitorEventDTO
     */
    @Override
    public void strategy(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {
        streageService.handlePolymerizationCountData(baseWsMonitorEventDTO);


        String deviceCode = (String) baseWsMonitorEventDTO.getEventObject();
        if (StringUtil.isEmpty(deviceCode)){
            return;
        }
        DeviceInfo deviceByCode = DeviceCache.getDeviceByCode(deviceCode);
        if (deviceByCode==null){
            return;
        }
        DeviceRel deviceRel = DeviceRelCache.getDeviceRel(deviceByCode.getId());
        if (deviceRel==null){
            return;
        }
        String entityType = deviceRel.getEntityType();
        if (CommonConstant.ENTITY_TYPE.PERSON.toString().equals(entityType)){
            baseWsMonitorEventDTO.setEventObject(deviceRel.getEntityId().toString());
            streageService.handlePersonDetailData(baseWsMonitorEventDTO);
        }else if (CommonConstant.ENTITY_TYPE.VEHICLE.toString().equals(entityType)){
            baseWsMonitorEventDTO.setEventObject(deviceRel.getEntityId().toString());
            streageService.handleVehicleDetailData(baseWsMonitorEventDTO);
        }

    }


}
