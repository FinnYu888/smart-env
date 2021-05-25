package com.ai.apac.smartenv.alarm.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Copyright: Copyright (c) 2019 Asiainfo
 *
 * @ClassName: WatchSOSAlarmConsumerSource
 * @Description:
 * @version: v1.0.0
 * @author: zhaidx
 * @date: 2020/9/10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/9/10     zhaidx           v1.0.0               修改原因
 */
public interface WatchSOSAlarmConsumerSource {


    String WATCH_SOS_ALARM_INPUT = "watch-sos-alarm-input";

    /**
     * 主动告警gps信息
     * @return
     */
    @Input(WATCH_SOS_ALARM_INPUT)
    SubscribableChannel watchSOSAlarmInput();
}
