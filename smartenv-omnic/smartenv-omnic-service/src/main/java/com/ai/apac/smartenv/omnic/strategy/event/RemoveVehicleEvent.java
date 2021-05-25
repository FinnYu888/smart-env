package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import com.ai.apac.smartenv.vehicle.cache.VehicleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description 车辆删除事件
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class RemoveVehicleEvent extends BaseDbEventStrategy {

    @Autowired
    private IPolymerizationClient polymerizationClient;


    @Autowired
    private OmnicProducerSource omnicProducerSource;

    /**
     * 当前策略支持的任务
     *
     * @return
     */
    @Override
    public String getSupportEventType() {
        return DbEventConstant.EventType.REMOVE_VEHICLE_EVENT;
    }

    /**
     * 具体的策略实现
     *
     * @param baseDbEventDTO
     */
    @Override
    public void strategy(BaseDbEventDTO baseDbEventDTO) {
        String tenantId = baseDbEventDTO.getTenantId();
        Long vehicleId = baseDbEventDTO.getEventObject() == null ? null : (Long) baseDbEventDTO.getEventObject();
        if (vehicleId != null) {
            //删除缓存
            VehicleCache.delNormalVehicleCount(baseDbEventDTO.getTenantId());
            VehicleCache.delVehicle(baseDbEventDTO.getTenantId(),vehicleId);
            VehicleCache.delVehicleDeviceStatusCount(baseDbEventDTO.getTenantId());

            List<Long> entityList = new ArrayList<>();
            entityList.add(vehicleId);
            polymerizationClient.removeVehicleList(entityList);
        }

        Message<?> msg = MessageBuilder.withPayload(
                new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.VEHICLE_INFO_EVENT, tenantId, tenantId,null)).build();
        omnicProducerSource.websocketMonitorEvent().send(msg);
    }
}
