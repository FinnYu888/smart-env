package com.ai.apac.smartenv.websocket.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ai.apac.smartenv.alarm.constant.AlarmConstant;
import com.ai.apac.smartenv.alarm.vo.AlarmInfoHandleInfoVO;
import com.ai.apac.smartenv.common.cache.CacheNames;
import com.ai.apac.smartenv.common.constant.WebSocketConsts;
import com.ai.apac.smartenv.system.dto.WeatherDTO;
import com.ai.apac.smartenv.websocket.module.main.vo.AlarmVO;
import com.ai.apac.smartenv.websocket.module.main.vo.Last10AlarmVO;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @ClassName AlarmTask
 * @Desc TODO
 * @Author ZHANGLEI25
 * @Date 2020/3/5 20:17
 * @Version 1.0
 */
@Getter
@Setter
@Slf4j
public class AlarmTask extends BaseTask implements Runnable {


    public AlarmTask(WebsocketTask websocketTask) {
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
    protected R<Last10AlarmVO> execute() {
        R<Last10AlarmVO> result = null;
        Last10AlarmVO last10AlarmVO = new Last10AlarmVO();
        List<AlarmVO> alarmVOList = new ArrayList<AlarmVO>();
        try {

            alarmVOList = getAlarmService().getHomeAlarmList(getTenantId());

            last10AlarmVO.setAlarmVOList(alarmVOList);
            last10AlarmVO.setTopicName(getWebsocketTask().getTopic());
            last10AlarmVO.setActionName(getWebsocketTask().getTaskType());
            last10AlarmVO.setTaskId(String.valueOf(getWebsocketTask().getId()));
            result = R.data(last10AlarmVO);

        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILURE, ex);
        }
        return result;
    }
}
