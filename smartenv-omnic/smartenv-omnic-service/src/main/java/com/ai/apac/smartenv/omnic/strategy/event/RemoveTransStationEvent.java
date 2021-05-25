package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.facility.cache.AshcanCache;
import com.ai.apac.smartenv.facility.cache.FacilityCache;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description 中转站删除事件
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class RemoveTransStationEvent extends BaseDbEventStrategy {
    @Autowired
    private PolymerizationService polymerizationService;

    @Autowired
    private OmnicProducerSource omnicProducerSource;
    /**
     * 当前策略支持的任务
     *
     * @return
     */
    @Override
    public String getSupportEventType() {
        return DbEventConstant.EventType.REMOVE_TRANS_STATION_EVENT;
    }

    /**
     * 具体的策略实现
     *
     * @param baseDbEventDTO
     */
    @Override
    public void strategy(BaseDbEventDTO baseDbEventDTO) {
        String tenantId = baseDbEventDTO.getTenantId();
        String stationIds = baseDbEventDTO.getEventObject() == null ? null : (String) baseDbEventDTO.getEventObject();
        if (StringUtils.isNotBlank(stationIds)) {
            //删除缓存
            FacilityCache.delFacilityCountToday(baseDbEventDTO.getTenantId());

            polymerizationService.removeFacilityList(Func.toLongList(stationIds));
        }

        Message<?> msg = MessageBuilder.withPayload(
                new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.TRANS_STATION_INFO_EVENT, tenantId, tenantId,null)).build();
        omnicProducerSource.websocketMonitorEvent().send(msg);
    }
}
