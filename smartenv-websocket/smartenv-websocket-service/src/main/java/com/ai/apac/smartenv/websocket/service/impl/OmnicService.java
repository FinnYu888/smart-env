package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.event.feign.IEventInfoClient;
import com.ai.apac.smartenv.omnic.dto.SummaryAmount;
import com.ai.apac.smartenv.omnic.dto.SummaryAmountForHome;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.omnic.feign.IRealTimeStatusClient;
import com.ai.apac.smartenv.omnic.feign.IStatisticsClient;
import com.ai.apac.smartenv.omnic.vo.AlarmAmountInfoVO;
import com.ai.apac.smartenv.omnic.vo.StatusCountVo;
import com.ai.apac.smartenv.websocket.module.main.vo.HomePageDataCountVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IOmnicService;
import com.ai.apac.smartenv.websocket.task.HomeDataCountTask;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @ClassName OmnicService
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/3 15:54
 * @Version 1.0
 */
@Service
public class OmnicService implements IOmnicService {

    @Autowired
    private WebSocketTaskService webSocketTaskService;

    @Autowired
    private IStatisticsClient statisticsClient;

    @Autowired
    private IRealTimeStatusClient realTimeStatusClient;

    //    @Autowired
//    private MongoTemplate mongoTemplate;
    @Autowired
    private IEventInfoClient eventInfoClient;
    @Autowired
    private IAlarmInfoClient alarmInfoClient;


    /**
     * 推送应出勤车辆数量，应出勤人员数量，事件数量，告警数量,车辆出勤数，人员出勤数的实时数据
     *
     * @param websocketTask
     */
    @Override
    public void pushHomeDataCountDaily(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(), websocketTask.getTenantId(), websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        HomeDataCountTask task = new HomeDataCountTask(websocketTask);
        task.run();
    }


    /**
     * 获取当日首页应出勤车辆数量/应出勤人员数量/事件总数/未处理告警数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public SummaryAmountForHome getHomeDataCount(String tenantId) {
        SummaryAmountForHome data = statisticsClient.getHomeSummaryAmountToday(tenantId).getData();
        return data;
    }


    /**
     * 获取今日汇总数据
     *
     * @param tenantId
     * @return
     */
    @Override
    public SummaryAmount getSummaryAmountToday(String tenantId) {
        return statisticsClient.getSummaryAmountToday(tenantId).getData();
    }

    /**
     * 获取今日告警数据汇总
     *
     * @param tenantId
     * @return
     */
    @Override
    public AlarmAmountVO getSummaryAlarmAmountToday(String tenantId) {
        R<AlarmAmountInfoVO> result = statisticsClient.getSummaryAlarmAmountToday(tenantId);
        AlarmAmountVO alarmAmountVO = null;
        if (result.isSuccess() && result.getData() != null) {
            alarmAmountVO = BeanUtil.copy(result.getData(), AlarmAmountVO.class);
        }
        return alarmAmountVO;
    }

    /**
     * 获取车辆各个状态对应的数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public StatusCount getAllVehicleStatusCount(String tenantId) {
        return realTimeStatusClient.getAllVehicleStatusCount(tenantId).getData();
    }

    /**
     * 获取人员各个状态对应的数量
     *
     * @param tenantId
     * @return
     */
    @Override
    public StatusCount getAllPersonStatusCount(String tenantId) {
        return realTimeStatusClient.getAllPersonStatusCount(tenantId).getData();
    }


    /**
     * 获取首页各个数字
     *
     * @param tenantId
     * @return
     */
    @Override
    public HomePageDataCountVO getHomePageCountData(String tenantId) {
        HomePageDataCountVO homePageDataCountVO = new HomePageDataCountVO();
        StatusCount allPersonStatusCount = getAllPersonStatusCount(tenantId);
        StatusCount allVehicleStatusCount = getAllVehicleStatusCount(tenantId);
        SummaryAmountForHome homeDataCount = getHomeDataCount(tenantId);
        homePageDataCountVO.setShouldWorkVehicleCount(homeDataCount.getWorkingVehicleCount().longValue());
        homePageDataCountVO.setShouldWorkPersonCount(homeDataCount.getWorkingPersonCount().longValue());

        homePageDataCountVO.setAlarmCount(alarmInfoClient.countAlarmInfoAmount(tenantId).getData());
        homePageDataCountVO.setEventCount(eventInfoClient.countEventDaily(tenantId).getData());

        homePageDataCountVO.setRestPersonCount(allPersonStatusCount.getSitBack());
        homePageDataCountVO.setWorkingPersonCount(allPersonStatusCount.getWorking());
        homePageDataCountVO.setStaticPersonCount(allPersonStatusCount.getDeparture());
        homePageDataCountVO.setRestVehicleCount(allVehicleStatusCount.getSitBack());
        homePageDataCountVO.setWorkingVehicleCount(allVehicleStatusCount.getWorking());
        homePageDataCountVO.setStaticVehicleCount(allVehicleStatusCount.getDeparture());
        homePageDataCountVO.setWateringVehicleCount(allVehicleStatusCount.getWaterCnt());
        homePageDataCountVO.setOilingVehicleCount(allVehicleStatusCount.getOilCnt());

        return homePageDataCountVO;
    }


}
