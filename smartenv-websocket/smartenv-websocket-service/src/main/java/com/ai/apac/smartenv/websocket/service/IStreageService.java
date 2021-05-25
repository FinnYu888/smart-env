package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: IStreageService
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/11/9
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/11/9  16:43    panfeng          v1.0.0             修改原因
 */
public interface IStreageService {
    void handleHomePageCountData(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleHomeLast10AlarmList(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleHomeLastGarbageList(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleHomeLast10Event(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleHomeLast6Order(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleBigScreenLastGarbageAmountByRegion(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleBigScreenCountData(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleBigScreenAlarmRuleCount(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleBigScreenAlarmList(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleBigScreenEventCountByType(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    void handleBigScreenLastGarbageList(BaseWsMonitorEventDTO baseWsMonitorEventDTO);

    /**
     * 处理大屏和综合监控全量统计数字
     * @param baseWsMonitorEventDTO
     */
    void handlePolymerizationCountData(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handlePersonDetailData(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handleVehicleDetailData(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handlePersonTrackTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handlePersonPositionTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handlePersonMonitorCountTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handleVehicleTrackTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handleVehiclePositionTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);

    void handleVehicleMonitorCountTask(BaseWsMonitorEventDTO<String> baseWsMonitorEventDTO);
}
