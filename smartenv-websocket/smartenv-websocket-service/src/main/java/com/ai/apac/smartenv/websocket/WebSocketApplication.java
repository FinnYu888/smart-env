package com.ai.apac.smartenv.websocket;

import com.ai.apac.smartenv.common.constant.ApplicationConstant;
import com.ai.apac.smartenv.websocket.mq.WebsocketProducerSource;
import org.springblade.core.launch.BladeApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/14 9:13 上午
 **/
@Configuration
@EnableFeignClients({"org.springblade", "com.ai.apac.smartenv"})
@SpringCloudApplication
@EnableWebSocket
@EnableScheduling
@EnableAsync
@EnableBinding(WebsocketProducerSource.class)
public class WebSocketApplication {

    public static void main(String[] args) {
        BladeApplication.run(ApplicationConstant.APPLICATION_WEBSOCKET_NAME, WebSocketApplication.class, args);
    }
}
