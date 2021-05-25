package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.AlarmAmountDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.AlarmAmountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.AlarmVO;
import com.ai.apac.smartenv.websocket.module.task.dto.EntityTaskDto;
import com.ai.apac.smartenv.websocket.module.task.dto.WebsocketTask;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.redis.cache.BladeRedisCache;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: AlarmAmountTask
 * @Description: 大屏告警数量Task
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  14:22    zhanglei25          v1.0.0             修改原因
 */
@Getter
@Setter
@Slf4j
public class AlarmAmountTask extends BaseTask implements Runnable {


    public AlarmAmountTask(WebsocketTask websocketTask) {
        super(websocketTask);
    }

    @Override
    public void run() {
        runTask();
    }

    /**
     * 具体的执行方法,由子类实现
     */
    @Override
    protected R<AlarmAmountVO> execute() {
        R<AlarmAmountVO> result = null;
        AlarmAmountVO alarmAmountVO = new AlarmAmountVO();
        AlarmAmountDTO alarmAmountDTO = new AlarmAmountDTO();
        try {
            alarmAmountDTO = getAlarmService().getAllRuleAlarmAmount(getTenantId());
            alarmAmountVO.setAlarmAmountDTO(alarmAmountDTO);
            alarmAmountVO.setTopicName(getWebsocketTask().getTopic());
            alarmAmountVO.setActionName(getWebsocketTask().getTaskType());
            alarmAmountVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(alarmAmountVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
