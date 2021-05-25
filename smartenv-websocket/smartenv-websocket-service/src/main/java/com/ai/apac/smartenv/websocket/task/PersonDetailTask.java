package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.person.dto.BasicPersonDTO;
import com.ai.apac.smartenv.vehicle.dto.BasicVehicleInfoDTO;
import com.ai.apac.smartenv.websocket.module.person.vo.PersonDetailVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import io.micrometer.core.instrument.util.StringUtils;
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

/**
 * @author qianlong
 * @description 人员信息实时推送
 * @Date 2020/2/25 21:28 下午
 **/
@Getter
@Setter
@Slf4j
public class PersonDetailTask extends BaseTask implements Runnable {

    public PersonDetailTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
//        try {
//            while (true) {
//                //TODO 先简单实现,每30秒执行一次,以后再用定时任务来实现
//                Thread.sleep(30000);
//            }
//        } catch (InterruptedException e) {
//            log.error("当前线程[{}]中断:", "PersonDetailTask", e.getMessage());
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
    protected R<PersonDetailVO> execute() {
        R<PersonDetailVO> result = null;
        try {
            Map<String, Object> params = validParams();
            String personId = params.get("personId") == null ? null : (String) params.get("personId");
            BasicPersonDTO basicPersonDTO = params.get("basicPersonDTO") == null ? null : (BasicPersonDTO) params.get("basicPersonDTO");
            if (StringUtils.isBlank(personId)) {
                throw new ServiceException("The param should not be empty!");
            }

            PersonDetailVO personDetailVO=null;
            if (basicPersonDTO!=null){
                personDetailVO=BeanUtil.copy(basicPersonDTO,PersonDetailVO.class);
            }else {
                personDetailVO = getPersonService().getPersonDetailRealTime(personId, getTenantId());
            }

            personDetailVO.setId(personId);
            if (personDetailVO == null) {
                return R.data(null, "没有匹配的数据");
            }

            /**
             * 将人员ID存入Redis,便于指定推送策略
             */
            getWebSocketTaskService().createEntityTask(this.getWebsocketTask(), personId);

            personDetailVO.setTopicName(getWebsocketTask().getTopic());
            personDetailVO.setActionName(getWebsocketTask().getTaskType());
            personDetailVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(personDetailVO);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }

}
