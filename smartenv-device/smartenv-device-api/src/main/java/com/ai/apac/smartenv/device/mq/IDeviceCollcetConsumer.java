package com.ai.apac.smartenv.device.mq;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface IDeviceCollcetConsumer {

    String DEVICE_COLLECT_INPUT = "device-info";

    @Input(DEVICE_COLLECT_INPUT)
    SubscribableChannel deviceCollectInputChannelInput();

}
