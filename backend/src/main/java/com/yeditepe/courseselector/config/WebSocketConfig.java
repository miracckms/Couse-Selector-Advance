package com.yeditepe.courseselector.config;

import com.yeditepe.courseselector.websocket.ActiveUserWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ActiveUserWebSocketHandler activeUserWebSocketHandler;

    public WebSocketConfig(ActiveUserWebSocketHandler activeUserWebSocketHandler) {
        this.activeUserWebSocketHandler = activeUserWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(activeUserWebSocketHandler, "/ws/active-users")
                .setAllowedOrigins("*");
    }
}
