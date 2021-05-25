package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/19 9:29 上午
 **/
public interface IOmnicService {


    /**
     * 推送应出勤车辆数量，应出勤人员数量，事件数量，告警数量,车辆出勤数，人员出勤数的实时数据
     * @param websocketTask
     */
    void pushHomeDataCountDaily(WebsocketTask websocketTask);

    SummaryAmountForHome getHomeDataCount(String tenantId);

    SummaryAmount getSummaryAmountToday(String tenantId);

    AlarmAmountVO getSummaryAlarmAmountToday(String tenantId);

    StatusCount getAllVehicleStatusCount(String tenantId);

    StatusCount getAllPersonStatusCount(String tenantId);

    HomePageDataCountVO getHomePageCountData(String tenantId);
}
