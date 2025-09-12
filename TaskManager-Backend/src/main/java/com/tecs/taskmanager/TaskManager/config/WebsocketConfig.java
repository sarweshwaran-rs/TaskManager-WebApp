package com.tecs.taskmanager.TaskManager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
 class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

     private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    public WebSocketConfig() {
        logger.info("WebsocketConfig loaded and configured.");
    }
    
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/api");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // This registers the "/ws" endpoint, allowing clients to connect.
        // setAllowedOriginPatterns("*") allows connections from any origin, which is perfect for local development.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }
}