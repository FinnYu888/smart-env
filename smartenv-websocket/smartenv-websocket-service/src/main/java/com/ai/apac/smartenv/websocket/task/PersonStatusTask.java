package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.omnic.entity.StatusCount;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonStatusCntVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
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
 * @description 人员状态统计任务
 * @Date 2020/2/24 15:28 下午
 **/
@Getter
@Setter
@Slf4j
public class PersonStatusTask extends BaseTask implements Runnable {

    public PersonStatusTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
//        try {
//            while (true) {
//                //TODO 先简单实现,每5秒执行一次,以后再用定时任务来实现
//                Thread.sleep(30000);
//            }
//        } catch (InterruptedException e) {
//            log.error("当前线程[{}]中断:", "PersonStatusTask", e.getMessage());
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
    protected R<PersonStatusCntVO> execute() {
        R<PersonStatusCntVO> result = null;
        try {
            Long workingCnt = 0L;
            Long unWorkingCnt = 0L;
            Long restCnt = 0L;
            Long alarmCnt = 0L;
            Long vacationCnt = 0L;
            Long unArrange=0L;
            //调用接口根据状态查询状态统计数字
            Future<StatusCount> statusCountResult = getPersonService().getStatusCount(getTenantId());
            if (statusCountResult != null && statusCountResult.get() != null) {
                StatusCount statusCount = statusCountResult.get();
                alarmCnt = statusCount.getAlarm();
                unWorkingCnt = statusCount.getDeparture();
                restCnt = statusCount.getSitBack();
                vacationCnt = statusCount.getVacationCnt();
                workingCnt = statusCount.getWorking();
                unArrange=statusCount.getUnArrangeCnt();
            }
            PersonStatusCntVO statusCntVO = new PersonStatusCntVO();
            statusCntVO.setTopicName(getWebsocketTask().getTopic());
            statusCntVO.setActionName(getWebsocketTask().getTaskType());
            statusCntVO.setTaskId(String.valueOf(getWebsocketTask().getId()));


            statusCntVO.setAlarmCnt(alarmCnt);
            statusCntVO.setWorkingCnt(workingCnt);
            statusCntVO.setUnWorkingCnt(unWorkingCnt);
            statusCntVO.setRestCnt(restCnt);
            statusCntVO.setVacationCnt(vacationCnt);
            statusCntVO.setUnArrangeCnt(unArrange);
            result = R.data(statusCntVO);



            log.debug("================推送人员实时状态状态统计================");
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
