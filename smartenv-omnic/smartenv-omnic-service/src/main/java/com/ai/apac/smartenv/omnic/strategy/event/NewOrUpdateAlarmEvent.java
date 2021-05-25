package com.ai.apac.smartenv.omnic.strategy.event;

import com.ai.apac.smartenv.alarm.cache.AlarmInfoCache;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoMongoDBVO;
import com.ai.apac.smartenv.common.constant.DbEventConstant;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.omnic.feign.IPolymerizationClient;
import com.ai.apac.smartenv.omnic.mq.OmnicProducerSource;
import com.ai.apac.smartenv.omnic.strategy.BaseDbEventStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description 告警新增/处理事件
 * @Date 2020/11/5 8:18 下午
 **/
@Component
public class NewOrUpdateAlarmEvent extends BaseDbEventStrategy {

    @Autowired
    private IPolymerizationClient polymerizationClient;


    @Autowired
    private OmnicProducerSource omnicProducerSource;

    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 当前策略支持的任务
     *
     * @return
     */
    @Override
    public String getSupportEventType() {
        return DbEventConstant.EventType.ALARM_EVENT;
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
        AlarmInfoCache.delSummaryAlarmCount(tenantId);
        AlarmInfoCache.delUnHandleAlarmCountToday(tenantId);

        Message<?> msg = MessageBuilder.withPayload(new BaseWsMonitorEventDTO(WsMonitorEventConstant.EventType.ALARM_EVENT, tenantId, tenantId, baseDbEventDTO.getEventObject())).build();
        omnicProducerSource.websocketMonitorEvent().send(msg);

        //获取目前该设备对象当天所有未处理告警数量
        String alarmId = (String) baseDbEventDTO.getEventObject();
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(Long.parseLong(alarmId)));
        AlarmInfoMongoDBVO alarmInfo = mongoTemplate.findOne(query, AlarmInfoMongoDBVO.class);
        if (alarmInfo == null) {
            return;
        }
        Long entityId = alarmInfo.getEntityId();
        Long entityType = alarmInfo.getEntityType();

        //根据entityId和entityType分别去查询当日未处理告警数量并放入缓存中
        R<Integer> result = alarmInfoClient.countNoHandleAlarmInfoByEntity(entityId, entityType);
        Integer unHandleCount = 0;
        if (result.isSuccess() && result.getData() != null) {
            unHandleCount = result.getData();
        }
        AlarmInfoCache.setUnHandledAlarmCountToday(alarmInfo.getEntityId(), alarmInfo.getEntityType(), unHandleCount);
    }
}
