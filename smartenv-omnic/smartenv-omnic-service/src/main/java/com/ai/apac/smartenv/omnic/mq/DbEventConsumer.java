package com.ai.apac.smartenv.omnic.mq;

import com.ai.apac.smartenv.omnic.dto.BaseDbEventDTO;
import com.ai.apac.smartenv.omnic.strategy.DbStrategyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/11/6 9:45 上午
 **/
@Component
@Slf4j
@EnableBinding(OmnicConsumerSource.class)
public class DbEventConsumer {

    @Autowired
    private DbStrategyContext dbStrategyContext;

    /**
     * 收到数据库表变更事件
     *
     * @param msg
     */
    @StreamListener(OmnicConsumerSource.DB_MONITOR_EVENT_INPUT)
    public void onScheduleToWorkEvent(@Payload BaseDbEventDTO msg) {
        log.debug("websocketMonitorEvent.messageObj;{}", msg);
        dbStrategyContext.doAction(msg);
    }
}
