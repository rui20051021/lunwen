package com.freshlogistics.config;

import com.freshlogistics.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 
 * 配置WebSocket端点和处理器
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private WebSocketHandler webSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器
        // 端点: ws://localhost:8080/ws/alerts?userId=123
        registry.addHandler(webSocketHandler, "/ws/alerts")
                .setAllowedOrigins("*")  // 允许所有来源（生产环境应该限制具体域名）
                .withSockJS();           // 启用SockJS支持（兼容不支持WebSocket的浏览器）
        
        // 也可以添加其他WebSocket端点
        // registry.addHandler(webSocketHandler, "/ws/notifications")
        //         .setAllowedOrigins("http://localhost:3000");
    }
}
