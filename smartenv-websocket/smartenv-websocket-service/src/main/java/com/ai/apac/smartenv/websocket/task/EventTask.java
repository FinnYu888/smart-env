package com.ai.apac.smartenv.websocket.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.CommonConstant;
import com.ai.apac.smartenv.common.constant.EventConstant;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.event.vo.EventInfoVO;
import com.ai.apac.smartenv.websocket.module.main.vo.*;
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

import java.util.*;
import java.util.concurrent.Future;

/**
 * @ClassName EventTask
 * @Desc 首页最近10条事件
 * @Author ZHANGLEI25
 * @Date 2020/3/5 20:17
 * @Version 1.0
 */
@Getter
@Setter
@Slf4j
public class EventTask extends BaseTask implements Runnable {


    public EventTask(WebsocketTask websocketTask) {
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
    protected R<Last10EventVO> execute() {
        R<Last10EventVO> result = null;
        Last10EventVO last10EventVO = new Last10EventVO();
        List<EventVO> eventVOList = new ArrayList<EventVO>();
        try {
            eventVOList = getEventService().getLastEventList(getTenantId());
            last10EventVO.setEventVOList(eventVOList);
            last10EventVO.setTopicName(getWebsocketTask().getTopic());
            last10EventVO.setActionName(getWebsocketTask().getTaskType());
            last10EventVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(last10EventVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
