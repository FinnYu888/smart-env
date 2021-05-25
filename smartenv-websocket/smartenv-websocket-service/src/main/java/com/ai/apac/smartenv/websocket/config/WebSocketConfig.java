package com.ai.apac.smartenv.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig
 *
 * @author qianlong
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/ws-mini-app").setAllowedOrigins("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");

		//点对点应配置一个/user消息代理，广播式应配置一个/topic消息代理
		registry.enableSimpleBroker("/topic","/user");

		//点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
		registry.setUserDestinationPrefix("/user");
	}
}
