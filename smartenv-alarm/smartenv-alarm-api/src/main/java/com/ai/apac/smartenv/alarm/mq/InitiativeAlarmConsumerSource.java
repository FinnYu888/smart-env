package com.ai.apac.smartenv.alarm.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface InitiativeAlarmConsumerSource {

    String MINICREATE_INITIATIVE_ALARM_INPUT = "minicreate-initiative-alarm-input";

    String MINICREATE_INITIATIVE_ALARM_ATTACHMENT_INPUT = "minicreate-initiative-alarm-attachment-input";

    /**
     * 主动告警gps信息
     * @return
     */
    @Input(MINICREATE_INITIATIVE_ALARM_INPUT)
    SubscribableChannel initiativeAlarmInput();

    /**
     * 主动告警附件数据
     * @return
     */
    @Input(MINICREATE_INITIATIVE_ALARM_ATTACHMENT_INPUT)
    SubscribableChannel initiativeAlarmAttachmentInput();
}
