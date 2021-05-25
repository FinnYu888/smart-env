package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.AlarmAmountDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.AlarmInfoScreenViewDTO;
import com.ai.apac.smartenv.websocket.module.main.vo.AlarmVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/2/19 2:07 下午
 **/
public interface IAlarmService {

    /**
     * 根据条件查询告警信息
     *
     * @param entityCategoryId
     * @param isHandle
     * @param startTime
     * @param endTime
     * @param personId
     * @param vehicleId
     * @return
     */
    Future<List<AlarmInfoHandleInfoVO>> listAlarmInfoByCondition(Long entityCategoryId, Integer isHandle, Date startTime, Date endTime,
                                                                 Long personId, Long vehicleId, Integer alarmLevel,Integer alarmNum,String tenantId);

    /**
     * 获取人员今天的所有告警信息
     * @param personId
     * @return
     */
    Future<List<AlarmInfoHandleInfoVO>> getTodayAlarmByPerson(Long personId);

    /**
     * 获取车辆今天的所有告警信息
     * @param vehicleId
     * @return
     */
    Future<List<AlarmInfoHandleInfoVO>> getTodayAlarmByVehicle(Long vehicleId);

    Future<Integer> countAlarmInfoAmount(String tenantId);

    /**
     * 推送首页告警列表
     * @param websocketTask
     */
    void pushHomeAlarmList(WebsocketTask websocketTask);

    /**
     * 获取首页告警列表用于推送
     */
    List<AlarmVO> getHomeAlarmList(String tenantId);

    /**
     * 推送大屏各种种类告警数量
     * @param websocketTask
     */
    void pushAllRuleAlarmAmount(WebsocketTask websocketTask);

    /**
     * 获取大屏各种种类告警数量用于推送
     */
    AlarmAmountDTO getAllRuleAlarmAmount(String tenantId);

    /**
     * 推送大屏告警列表
     * @param websocketTask
     */
    void pushBigScreenLastAlarmList(WebsocketTask websocketTask);

    /**
     * 获取大屏告警列表用于推送
     */
    List<AlarmInfoScreenViewDTO> getBigScreenLastAlarmList(String tenantId);


}
