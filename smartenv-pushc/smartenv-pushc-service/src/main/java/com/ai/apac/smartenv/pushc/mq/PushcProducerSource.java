package com.ai.apac.smartenv.pushc.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/5/24 9:57 下午
 **/
public interface PushcProducerSource {

    String EMAIL_CHANNEL_OUTPUT = "pushc-email-output";

    String ASSESS_EVENT_CHANNEL_OUTPUT = "pushc-assessEvent-output";

    String MP_ASSESS_EVENT_CHANNEL_OUTPUT = "mp-assessEvent-output";

    String ALARM_EVENT_CHANNEL_OUTPUT = "pushc-alarmEvent-output";

    String MP_ALARM_EVENT_CHANNEL_OUTPUT = "mp-alarmEvent-output";


    /**
     * Email通道
     *
     * @return
     */
    @Output(EMAIL_CHANNEL_OUTPUT)
    MessageChannel emailChannel();

    /**
     * 事件通知通道
     *
     * @return
     */
    @Output(ASSESS_EVENT_CHANNEL_OUTPUT)
    MessageChannel accessEventChannel();

    /**
     * 微信公众平台发送消息通道
     *
     * @return
     */
    @Output(MP_ASSESS_EVENT_CHANNEL_OUTPUT)
    MessageChannel mpAssessEventChannel();

    /**
     * 告警通知通道
     *
     * @return
     */
    @Output(ALARM_EVENT_CHANNEL_OUTPUT)
    MessageChannel alarmEventChannel();

    /**
     * 微信公众平台发送告警通道
     *
     * @return
     */
    @Output(MP_ALARM_EVENT_CHANNEL_OUTPUT)
    MessageChannel mpAlarmEventChannel();
}
