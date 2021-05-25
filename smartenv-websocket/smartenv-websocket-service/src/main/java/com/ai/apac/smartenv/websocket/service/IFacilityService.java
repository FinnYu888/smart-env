package com.ai.apac.smartenv.websocket.service;

import com.ai.apac.smartenv.websocket.module.bigscreen.dto.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Copyright: Copyright (c) 2020/8/17 Asiainfo
 *
 * @ClassName: IFacilityService
 * @Description:
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/17  10:59    zhanglei25          v1.0.0             修改原因
 */
public interface IFacilityService {

    /**
     * 推送首页垃圾转运统计数据
     * @param websocketTask
     */
    void pushLastGarbageAmount(WebsocketTask websocketTask);

    /**
     * 获取首页垃圾转运统计数据用于推送
     */
    List<LastDaysGarbageAmountVO> getLastGarbageAmount(Integer days, String tenantId);

    /**
     * 推送大屏垃圾根据地区统计数据
     * @param websocketTask
     */
    void pushLastGarbageAmountByRegion(WebsocketTask websocketTask);

    /**
     * 获取大屏垃圾根据地区统计数据用于统计
     */
    List<LastDaysRegionGarbageAmountVO> getLastGarbageAmountByRegion(String tenantId);


}
