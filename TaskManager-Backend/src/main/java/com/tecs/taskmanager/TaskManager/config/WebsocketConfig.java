package com.tecs.taskmanager.TaskManager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
 class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // This is the prefix for topics that clients can subscribe to from the server.
        config.enableSimpleBroker("/api");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This registers the "/ws" endpoint, allowing clients to connect.
        // setAllowedOriginPatterns("*") allows connections from any origin, which is perfect for local development.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }
}