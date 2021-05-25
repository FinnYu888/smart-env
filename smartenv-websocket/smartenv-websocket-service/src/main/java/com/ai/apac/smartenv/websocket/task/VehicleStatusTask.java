package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import com.ai.apac.smartenv.websocket.module.vehicle.vo.VehicleStatusCntVO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author qianlong
 * @description 车辆状态统计任务
 * @Date 2020/2/16 10:28 下午
 **/
@Getter
@Setter
@Slf4j
public class VehicleStatusTask extends BaseTask implements Runnable {

    public VehicleStatusTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
//        try {
//            while (true) {
//                //TODO 先简单实现,每5秒执行一次,以后再用定时任务来实现
//                Thread.sleep(5000);
//            }
//        } catch (InterruptedException e) {
//            log.error("当前线程[{}]中断:", "VehicleStatusTask", e.getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            log.error("获取车辆实时状态统计异常:", ex.getMessage());
//            return;
//        }
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    protected R<VehicleStatusCntVO> execute() {
        R<VehicleStatusCntVO> result = null;
        try {
            Long workingCnt = 0L;
            Long unWorkingCnt = 0L;
            Long restCnt = 0L;
            Long alarmCnt = 0L;
            Long waterCnt = 0L;
            Long oilCnt = 0L;
            Long vacationCnt = 0L;
            Long unArrange=0L;

            //调用接口根据状态查询状态统计数字
            Future<StatusCount> statusCountResult = getVehicleService().getStatusCount(getTenantId());
            if (statusCountResult != null && statusCountResult.get() != null) {
                StatusCount statusCount = statusCountResult.get();
                workingCnt=statusCount.getWorking();
                alarmCnt = statusCount.getAlarm();
                unWorkingCnt = statusCount.getDeparture();
                restCnt = statusCount.getSitBack();
                waterCnt = statusCount.getWaterCnt();
                oilCnt = statusCount.getOilCnt();
                vacationCnt = statusCount.getVacationCnt();
                unArrange = statusCount.getUnArrangeCnt();
            }
            VehicleStatusCntVO vehicleStatusCntVO = new VehicleStatusCntVO();
            vehicleStatusCntVO.setTopicName(getWebsocketTask().getTopic());
            vehicleStatusCntVO.setActionName(getWebsocketTask().getTaskType());
            vehicleStatusCntVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            vehicleStatusCntVO.setAlarmCnt(alarmCnt);
            vehicleStatusCntVO.setWorkingCnt(workingCnt);
            vehicleStatusCntVO.setUnWorkingCnt(unWorkingCnt);
            vehicleStatusCntVO.setRestCnt(restCnt);
            vehicleStatusCntVO.setOilCnt(oilCnt);
            vehicleStatusCntVO.setWaterCnt(waterCnt);
            vehicleStatusCntVO.setVacationCnt(vacationCnt);
            vehicleStatusCntVO.setUnArrangeCnt(unArrange);
//            Map<String, Object> params = validParams();
//            Boolean saveTask = params.get("saveTask") == null ? true : (Boolean) params.get("saveTask");
//            String sessionId = getWebsocketTask().getSessionId();
//            if (sessionId != null && saveTask) {
//                //将当前人员的监控任务放入redis
//                EntityTaskDto entityTask = BeanUtil.copy(getWebsocketTask(), EntityTaskDto.class);
//                List<String> entityIdList = new ArrayList<>();
//                entityIdList.add(getTenantId());
//                entityTask.setEntityIds(entityIdList);
//                entityTask.setEntityType(CommonConstant.ENTITY_TYPE.VEHICLE);
//                getWebSocketTaskService().createEntityTask(entityTask);
//            }

            result = R.data(vehicleStatusCntVO);
            log.debug("================推送车辆实状态状态统计================");
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
