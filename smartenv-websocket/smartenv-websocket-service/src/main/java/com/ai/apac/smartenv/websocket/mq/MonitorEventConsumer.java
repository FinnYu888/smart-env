package com.ai.apac.smartenv.websocket.mq;

import com.ai.apac.smartenv.omnic.dto.BaseWsMonitorEventDTO;
import com.ai.apac.smartenv.websocket.strategy.StrategyContext;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author qianlong
 * @description 监听任务队列
 * @Date 2020/10/27 5:48 下午
 **/
@Component
@Slf4j
@EnableBinding(IWebsocketConsumer.class)
public class MonitorEventConsumer {

    @Autowired
    private StrategyContext strategyContext;

    @StreamListener(IWebsocketConsumer.WEBSOCKET_MONITOR_EVENT)
    public void websocketMonitorEvent(@Payload BaseWsMonitorEventDTO msg) {
        log.debug("websocketMonitorEvent.messageObj;{}", msg);

        strategyContext.doAction(msg);
    }
}
