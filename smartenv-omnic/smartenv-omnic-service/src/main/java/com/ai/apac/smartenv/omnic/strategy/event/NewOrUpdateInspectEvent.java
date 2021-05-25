package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.event.cache.EventCache;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description 巡查事件新增/处理事件
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class NewOrUpdateInspectEvent extends BaseDbEventStrategy {

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
        return DbEventConstant.EventType.INSPECT_EVENT;
    }

    /**
     * 具体的策略实现
     *
     * @param baseDbEventDTO
     */
    @Override
    public void strategy(BaseDbEventDTO baseDbEventDTO) {
        String tenantId = baseDbEventDTO.getTenantId();
        //删除缓存中数据
        EventCache.delEventCountToday(tenantId);

        Message<?> msg = MessageBuilder.withPayload(
                new BaseWsMonitorEventDTO<String>(WsMonitorEventConstant.EventType.ASSESS_EVENT, tenantId, null,baseDbEventDTO.getEventObject().toString())).build();
        omnicProducerSource.websocketMonitorEvent().send(msg);

        //TODO

        //首页四个数字
        //首页近10条重要事件
        //大屏总览实时数量
        //大屏考核问题分析
        //综合监控汇总数据
        //综合监控事件实时位置


    }
}
