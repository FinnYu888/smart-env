package com.ai.apac.smartenv.omnic.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: OmnicConsumerSource
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/8/7
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/8/7  15:35    panfeng          v1.0.0             修改原因
 */
public interface OmnicConsumerSource {

    String WATCH_POSITION_INPUT = "watch-position";
    String VEHICLE_POSITION_INPUT = "vehicle-position";
    String DB_MONITOR_EVENT_INPUT = "db-monitor-event-input";

    @Input(WATCH_POSITION_INPUT)
    SubscribableChannel watchDevicePositionChannelInput();

    @Input(VEHICLE_POSITION_INPUT)
    SubscribableChannel vehicledevicePositionChannelInput();

    @Input(DB_MONITOR_EVENT_INPUT)
    SubscribableChannel dbMonitorEventChannelInput();
}
