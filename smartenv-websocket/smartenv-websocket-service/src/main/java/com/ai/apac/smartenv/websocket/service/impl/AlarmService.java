package com.ai.apac.smartenv.websocket.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.dto.AlarmInfoQueryDTO;
import com.ai.apac.smartenv.alarm.feign.IAlarmInfoClient;
import com.ai.apac.smartenv.alarm.vo.AlarmAmountVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoScreenViewVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.websocket.controller.HomePageController;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.AlarmAmountDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.AlarmInfoScreenViewDTO;
import com.ai.apac.smartenv.websocket.module.main.vo.AlarmVO;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.service.IAlarmService;
import com.ai.apac.smartenv.websocket.service.IBaseService;
import com.ai.apac.smartenv.websocket.task.AlarmAmountTask;
import com.ai.apac.smartenv.websocket.task.AlarmTask;
import com.ai.apac.smartenv.websocket.task.BigScreenAlarmTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/2 2:18 下午
 **/
@Service
public class AlarmService implements IAlarmService {

    @Autowired
    private IAlarmInfoClient alarmInfoClient;

    @Autowired
    private WebSocketTaskService webSocketTaskService;

    @Autowired
    private IBaseService baseService;

    private static Logger logger = LoggerFactory.getLogger(AlarmService.class);

    /**
     * 根据条件查询告警信息
     *
     * @param entityCategoryId
     * @param isHandle
     * @param startDate
     * @param endDate
     * @param personId
     * @param vehicleId
     * @return
     */
    @Override
    public Future<List<AlarmInfoHandleInfoVO>> listAlarmInfoByCondition(Long entityCategoryId, Integer isHandle, Date startDate, Date endDate, Long personId, Long vehicleId, Integer alarmLevel,Integer alarmNum,String tenantId) {
        AlarmInfoQueryDTO alarmInfoQueryDTO = new AlarmInfoQueryDTO();
        alarmInfoQueryDTO.setEntityCategoryId(entityCategoryId);
        alarmInfoQueryDTO.setAlarmLevel(alarmLevel);
        Long startTime = startDate == null ? null : startDate.getTime();
        Long endTime = endDate == null ? null : endDate.getTime();
        alarmInfoQueryDTO.setStartTime(startTime);
        alarmInfoQueryDTO.setEndTime(endTime);
        alarmInfoQueryDTO.setIsHandle(isHandle);
        alarmInfoQueryDTO.setPersonId(personId);
        alarmInfoQueryDTO.setVehicleId(vehicleId);
        alarmInfoQueryDTO.setAlarmNum(alarmNum);
        alarmInfoQueryDTO.setTenantId(tenantId);
        logger.info("alarmInfoQueryDTO："+alarmInfoQueryDTO.toString());
        R<List<AlarmInfoHandleInfoVO>> result = alarmInfoClient.listAlarmInfoByCondition(alarmInfoQueryDTO);
        if (result != null && result.getData() != null && result.getData().size() > 0) {
            return new AsyncResult<List<AlarmInfoHandleInfoVO>>(result.getData());
        }
        return null;
    }

    /**
     * 获取人员今天的所有告警信息
     *
     * @param personId
     * @return
     */
    @Override
    public Future<List<AlarmInfoHandleInfoVO>> getTodayAlarmByPerson(Long personId) {
        Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
        Date endTime = DateTime.now();
        return listAlarmInfoByCondition(null, AlarmConstant.IsHandle.HANDLED_NO, startTime, endTime, Long.valueOf(personId), null, null,null,null);
    }

    /**
     * 获取车辆今天的所有告警信息
     *
     * @param vehicleId
     * @return
     */
    @Override
    public Future<List<AlarmInfoHandleInfoVO>> getTodayAlarmByVehicle(Long vehicleId) {
        Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
        Date endTime = DateTime.now();
        return listAlarmInfoByCondition(null, AlarmConstant.IsHandle.HANDLED_NO, startTime, endTime, null, Long.valueOf(vehicleId), null,null,null);
    }

    @Override
    public Future<Integer> countAlarmInfoAmount(String tenantId) {
        R<Integer> countResult = alarmInfoClient.countAlarmInfoAmount(tenantId);
        if (countResult.isSuccess() && countResult.getData() != null) {
            return new AsyncResult<Integer>(countResult.getData());
        }
        return null;
    }

    @Override
    public void pushHomeAlarmList(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        AlarmTask task = new AlarmTask(websocketTask);
        task.run();
    }

    @Override
    public void pushAllRuleAlarmAmount(WebsocketTask websocketTask) {
        //删除目前该session下相同类型的任务,重新创建
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        AlarmAmountTask task = new AlarmAmountTask(websocketTask);
        task.run();
    }

    @Override
    public AlarmAmountDTO getAllRuleAlarmAmount(String tenantId) {
        AlarmAmountVO alarmAmountVO = alarmInfoClient.countAllRuleAlarmAmount(tenantId).getData();

        return BeanUtil.copyProperties(alarmAmountVO,AlarmAmountDTO.class);
    }

    @Override
    public void pushBigScreenLastAlarmList(WebsocketTask websocketTask) {
        webSocketTaskService.deleteSameTask(websocketTask.getSessionId(),websocketTask.getTenantId(),websocketTask.getTaskType());
        webSocketTaskService.createTask(websocketTask);
        BigScreenAlarmTask task = new BigScreenAlarmTask(websocketTask);
        task.run();
    }

    @Override
    public List<AlarmInfoScreenViewDTO> getBigScreenLastAlarmList(String tenantId) {
        List<AlarmInfoScreenViewVO> alarmInfoScreenViewVOList = alarmInfoClient.getBigScreenAlarmList(tenantId,5L).getData();
        return BeanUtil.copyProperties(alarmInfoScreenViewVOList,AlarmInfoScreenViewDTO.class);
    }

    @Override
    public List<AlarmVO> getHomeAlarmList(String tenantId) {
        List<AlarmVO> alarmVOList = new ArrayList<AlarmVO>();
        List<AlarmInfoHandleInfoVO> alarmInfoHandleInfoVOList = new ArrayList<AlarmInfoHandleInfoVO>();
        try {
            Date startTime = DateUtil.beginOfDay(Calendar.getInstance().getTime());
            Date endTime = DateTime.now();

            Future<List<AlarmInfoHandleInfoVO>> alarmInfoHandleInfoVOListResult = this.listAlarmInfoByCondition(null, AlarmConstant.IsHandle.HANDLED_NO, startTime, endTime,null, null, AlarmConstant.AlarmLevel.EMERGENCY,10,tenantId);
            if (alarmInfoHandleInfoVOListResult != null && alarmInfoHandleInfoVOListResult.get() != null) {
                alarmInfoHandleInfoVOList = alarmInfoHandleInfoVOListResult.get();
            }

            if(alarmInfoHandleInfoVOList.size() > 0 ){
                alarmInfoHandleInfoVOList.forEach(alarmInfoHandleInfoVO -> {
                    AlarmVO alarmVO = new AlarmVO();
                    alarmVO.setId(alarmInfoHandleInfoVO.getId().toString());
                    alarmVO.setAlarmType(alarmInfoHandleInfoVO.getAlarmTypeName());
                    alarmVO.setAlarmMessage(alarmInfoHandleInfoVO.getAlarmMessage());
                    alarmVO.setAlarmDate(TimeUtil.getYYYY_MM_DD_HH_MM_SS(alarmInfoHandleInfoVO.getAlarmTime()));
                    alarmVO.setEntityType(alarmInfoHandleInfoVO.getEntityType().toString());
                    alarmVOList.add(alarmVO);
                });

            }} catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return alarmVOList;

    }
}
