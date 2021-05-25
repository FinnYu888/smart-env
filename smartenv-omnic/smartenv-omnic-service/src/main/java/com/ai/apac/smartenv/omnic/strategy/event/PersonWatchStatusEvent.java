package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import com.ai.apac.smartenv.person.cache.PersonCache;
import com.ai.apac.smartenv.person.feign.IPersonClient;
import com.ai.apac.smartenv.vehicle.feign.IVehicleClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description 人员设备状态变更事件
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class PersonWatchStatusEvent extends BaseDbEventStrategy {


    @Autowired
    private OmnicProducerSource omnicProducerSource;



    @Autowired
    private PolymerizationService polymerizationService;
    /**
     * 当前策略支持的任务
     *
     * @return
     */
    @Override
    public String getSupportEventType() {
        return DbEventConstant.EventType.PERSON_WATCH_STATUS_EVENT;
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
        if (personId != null && personId != 0L) {
            // 删除缓存
            PersonCache.delPerson(baseDbEventDTO.getTenantId(), personId);
            PersonCache.delPersonDeviceStatusCount(baseDbEventDTO.getTenantId());
        }

        /**
         * 初始化公司下所有项目的数据统计信息
         */
        polymerizationService.initSynthInfo(tenantId);

        Message<?> msg = MessageBuilder.withPayload(
                new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.PERSON_WORK_STATUS_EVENT, tenantId, tenantId,null)).build();
        omnicProducerSource.websocketMonitorEvent().send(msg);
    }
}
