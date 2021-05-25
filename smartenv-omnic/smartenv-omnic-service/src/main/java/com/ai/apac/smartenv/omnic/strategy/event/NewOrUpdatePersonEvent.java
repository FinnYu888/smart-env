package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.DataChangeEventClient;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description 人员信息新增/修改事件
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class NewOrUpdatePersonEvent extends BaseDbEventStrategy {

    @Autowired
    private PolymerizationService polymerizationService;


    @Autowired
    private OmnicProducerSource omnicProducerSource;
    @Autowired
    private DataChangeEventClient dataChangeEventService;

    /**
     * 当前策略支持的任务
     *
     * @return
     */
    @Override
    public String getSupportEventType() {
        return DbEventConstant.EventType.NEW_OR_UPDATE_PERSON_EVENT;
    }

    /**
     * 具体的策略实现
     *
     * @param baseDbEventDTO
     */
    @Override
    public void strategy(BaseDbEventDTO baseDbEventDTO) {
        String tenantId = baseDbEventDTO.getTenantId();
        Long personId = baseDbEventDTO.getEventObject() == null ? null : (Long) baseDbEventDTO.getEventObject();
        if (personId != null) {
            //删除缓存中数据
            PersonCache.delPerson(baseDbEventDTO.getTenantId(), personId);

            List<Long> entityList = new ArrayList<>();
            entityList.add(personId);
            polymerizationService.reloadPersonInfo(entityList);
        }
//
//        Message<?> msg = MessageBuilder.withPayload(
//                new BaseWsMonitorEventDTO<String>(
//                        WsMonitorEventConstant.EventType.PERSON_INFO_EVENT, tenantId, tenantId,personId==null?null:personId.toString())).build();
//        omnicProducerSource.websocketMonitorEvent().send(msg);

        BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO = new BaseWsMonitorEventDTO<>(WsMonitorEventConstant.EventType.PERSON_INFO_EVENT, tenantId, tenantId,personId==null?null:personId.toString());
        dataChangeEventService.doWebsocketEvent(baseWsMonitorEventDTO);

    }
}
