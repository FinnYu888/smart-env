package com.ai.apac.smartenv.websocket.strategy.event;

import com.ai.apac.smartenv.alarm.vo.AlarmInfoMongoDBVO;
import com.ai.apac.smartenv.common.constant.WsMonitorEventConstant;
import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.websocket.service.*;
import com.ai.apac.smartenv.websocket.strategy.BaseWebsocketPushStrategy;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AlarmEvent
 * @Description: 告警数据变化策略
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/10/28
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/10/28  11:41    panfeng          v1.0.0             修改原因
 */
@Component
public class AlarmEvent extends BaseWebsocketPushStrategy {


    @Autowired
    private IStreageService streageService;

    @Autowired
    private MongoTemplate mongoTemplate;



    @Override
    public String getSupportEventType() {
        return WsMonitorEventConstant.EventType.ALARM_EVENT;
    }

    @Override
    public void strategy(BaseWsMonitorEventDTO baseWsMonitorEventDTO) {


        /**
         * 先推送非ID查询的任务
         */
        streageService.handleHomePageCountData(baseWsMonitorEventDTO);
        streageService.handlePolymerizationCountData(baseWsMonitorEventDTO);
        streageService.handleBigScreenAlarmRuleCount(baseWsMonitorEventDTO);
        streageService.handleBigScreenAlarmList(baseWsMonitorEventDTO);
        streageService.handleHomeLast10AlarmList(baseWsMonitorEventDTO);

        // 查询告警ID，根据告警ID来进行精准推送
        String alarmId = (String) baseWsMonitorEventDTO.getEventObject();
        if (StringUtil.isEmpty(alarmId)){
            return;
        }
        Query query=new Query();
        query.addCriteria(Criteria.where("id").is(Long.parseLong(alarmId)));
        AlarmInfoMongoDBVO one = mongoTemplate.findOne(query, AlarmInfoMongoDBVO.class);
        if (one==null){
            return;
        }
        if (one.getVehicleId()!=null){
            BaseWsMonitorEventDTO copy = BeanUtil.copy(baseWsMonitorEventDTO, BaseWsMonitorEventDTO.class);
            copy.setEventObject(one.getVehicleId());
            streageService.handleVehicleDetailData(baseWsMonitorEventDTO);
        }else if (one.getPersonId()!=null){
            BaseWsMonitorEventDTO copy = BeanUtil.copy(baseWsMonitorEventDTO, BaseWsMonitorEventDTO.class);
            copy.setEventObject(one.getPersonId());
            streageService.handlePersonDetailData(baseWsMonitorEventDTO);
        }

    }


}
