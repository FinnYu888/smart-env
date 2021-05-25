package com.ai.apac.smartenv.websocket.task;

import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.EventTypeCountDTO;
import com.ai.apac.smartenv.websocket.module.bigscreen.dto.EventTypeCountVO;
import com.ai.apac.smartenv.websocket.module.main.vo.EventVO;
import com.ai.apac.smartenv.websocket.module.main.vo.Last10EventVO;
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
 * @ClassName: EventCountTask
 * @Description: 首页事件数量任务
 * @version: v1.0.0
 * @author: zhanglei25
 * @date: 2020/8/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/20  15:07    zhanglei25          v1.0.0             修改原因
 */
@Getter
@Setter
@Slf4j
public class EventCountTask extends BaseTask implements Runnable {


    public EventCountTask(WebsocketTask websocketTask) {
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
    protected R<EventTypeCountVO> execute() {
        R<EventTypeCountVO> result = null;
        EventTypeCountVO eventTypeCountVO = new EventTypeCountVO();
        List<EventTypeCountDTO> eventTypeCountDTOList = new ArrayList<EventTypeCountDTO>();
        try {
            eventTypeCountDTOList = getEventService().getEventCountByType(getTenantId());
            eventTypeCountVO.setEventTypeCountDTOList(eventTypeCountDTOList);
            eventTypeCountVO.setTopicName(getWebsocketTask().getTopic());
            eventTypeCountVO.setActionName(getWebsocketTask().getTaskType());
            eventTypeCountVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(eventTypeCountVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
