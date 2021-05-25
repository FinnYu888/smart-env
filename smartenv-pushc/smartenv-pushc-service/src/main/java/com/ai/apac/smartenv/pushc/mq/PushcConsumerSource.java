package com.ai.apac.smartenv.pushc.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/24 10:16 下午
 **/
public interface PushcConsumerSource {

    String EMAIL_CHANNEL_INPUT = "pushc-email-input";

    String ASSESS_EVENT_CHANNEL_INPUT = "pushc-assessEvent-input";

    String ALARM_EVENT_CHANNEL_INPUT = "pushc-alarmEvent-input";

    /**
     * email通道
     * @return
     */
    @Input(EMAIL_CHANNEL_INPUT)
    SubscribableChannel emailChannelInput();

    /**
     * 考核事件通知通道
     * @return
     */
    @Input(ASSESS_EVENT_CHANNEL_INPUT)
    SubscribableChannel accessEventChannelInput();

    /**
     * 微信公众号告警通知通道
     * @return
     */
    @Input(ALARM_EVENT_CHANNEL_INPUT)
    SubscribableChannel alarmEventChannelInput();
}
