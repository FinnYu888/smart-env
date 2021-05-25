package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.LastDaysRegionGarbageAmountVO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.LastGarbageAmountByRegionVO;
import com.ai.apac.smartenv.websocket.module.main.vo.LastDaysGarbageAmountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.LastGarbageVO;
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
 * Copyright: Copyright (c) 2020/8/20 Asiainfo
 *
 * @ClassName: LastGarbageAmountByRegionTask
 * @Description: 最近N天某区域垃圾收集总数统计任务
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  11:28    zhanglei25          v1.0.0             修改原因
 */
@Getter
@Setter
@Slf4j
public class LastGarbageAmountByRegionTask extends BaseTask implements Runnable {

    public LastGarbageAmountByRegionTask(WebsocketTask websocketTask) {
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
    protected R<LastGarbageAmountByRegionVO> execute() {
        R<LastGarbageAmountByRegionVO> result = null;
        LastGarbageAmountByRegionVO lastGarbageAmountByRegionVO = new LastGarbageAmountByRegionVO();
        List<LastDaysRegionGarbageAmountVO> lastDaysGarbageAmountList = new ArrayList<LastDaysRegionGarbageAmountVO>();
        try {

            lastDaysGarbageAmountList = getFacilityService().getLastGarbageAmountByRegion(getTenantId());

            lastGarbageAmountByRegionVO.setLastDaysRegionGarbageAmountList(lastDaysGarbageAmountList);
            lastGarbageAmountByRegionVO.setTopicName(getWebsocketTask().getTopic());
            lastGarbageAmountByRegionVO.setActionName(getWebsocketTask().getTaskType());
            lastGarbageAmountByRegionVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(lastGarbageAmountByRegionVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
