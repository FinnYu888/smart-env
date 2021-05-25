package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.alarm.cache.AlarmInfoCache;
import com.ai.apac.smartenv.arrange.cache.ScheduleCache;
import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.service.PolymerizationService;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author zhanglei25
 * @description 排班变化
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class NewOrUpdateArrangeEvent extends BaseDbEventStrategy {

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
        return DbEventConstant.EventType.ARRANGE_EVENT;
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
        ScheduleCache.delWorkingCountForPersonToday(tenantId);
        ScheduleCache.delWorkingCountForVehicleToday(tenantId);


        String entityIds = (String) baseDbEventDTO.getEventObject();
        if (StringUtil.isNotBlank(entityIds)){
            polymerizationService.reloadVehicleInfo(Func.toLongList(entityIds));
            polymerizationService.reloadPersonInfo(Func.toLongList(entityIds));
        }


        Message<?> msg = MessageBuilder.withPayload(new BaseWsMonitorEventDTO(WsMonitorEventConstant.EventType.ARRANGE_EVENT, tenantId, tenantId,baseDbEventDTO.getEventObject())).build();

        omnicProducerSource.websocketMonitorEvent().send(msg);




        //TODO

        //首页四个数字
        //首页近10条紧急告警
        //车辆监控实时详情
        //人员监控实时详情
        //大屏总览实时数量
        //大屏，告警数据今日汇总
        //告警实时监控
        //综合监控汇总数据
        //综合监控人员实时位置
    }
}
