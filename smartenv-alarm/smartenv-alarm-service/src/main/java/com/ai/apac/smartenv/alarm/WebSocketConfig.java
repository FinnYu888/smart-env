package com.ai.apac.smartenv.alarm;

import com.ai.apac.smartenv.alarm.controller.AlarmWebsocketController;
import org.apache.tomcat.websocket.server.WsServerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerContainer;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: WebSocketConfig
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/1/19
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/1/19  15:04    panfeng          v1.0.0             修改原因
 */
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(new AlarmWebsocketController(),"/ws/alarm").setAllowedOrigins("*");
    }
}
