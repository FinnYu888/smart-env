package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.controller.BigScreenController;
import com.ai.apac.smartenv.websocket.module.main.vo.LastGarbageVO;
import com.ai.apac.smartenv.websocket.module.main.vo.LastDaysGarbageAmountVO;
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
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020/8/17 Asiainfo
 *
 * @ClassName: FacilityGarbageTask
 * @Description: 最近N天垃圾收集吨数
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/17
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/17  11:02    zhanglei25          v1.0.0             修改原因
 */
@Getter
@Setter
@Slf4j
public class LastGarbageAmountTask extends BaseTask implements Runnable {

    public LastGarbageAmountTask(WebsocketTask websocketTask) {
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
    protected R<LastGarbageVO> execute() {
        R<LastGarbageVO> result = null;
        LastGarbageVO lastGarbageVO = new LastGarbageVO();
        List<LastDaysGarbageAmountVO> lastDaysGarbageAmountVOList = new ArrayList<LastDaysGarbageAmountVO>();
        try {

            Integer days = 30;
            if(getWebsocketTask().getTaskType().equals(BigScreenController.GET_BIGSCREEN_LAST_GARBAGE_AMOUNT)){
                days = 7;
            }

            lastDaysGarbageAmountVOList = getFacilityService().getLastGarbageAmount(days,getTenantId());


            lastGarbageVO.setLastDaysGarbageAmountVOList(lastDaysGarbageAmountVOList);
            lastGarbageVO.setTopicName(getWebsocketTask().getTopic());
            lastGarbageVO.setActionName(getWebsocketTask().getTaskType());
            lastGarbageVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(lastGarbageVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
