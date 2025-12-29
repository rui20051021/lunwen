package com.freshlogistics.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理器 - 实现实时预警消息推送
 * 
 * 功能：
 * 1. 管理WebSocket连接会话
 * 2. 向指定用户推送预警消息
 * 3. 支持广播消息到所有在线用户
 * 4. 自动处理连接断开和重连
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    
    // 存储所有在线用户的WebSocket会话 <userId, WebSocketSession>
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 存储会话ID到用户ID的映射 <sessionId, userId>
    private static final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 连接建立成功后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        
        if (userId != null) {
            sessions.put(userId, session);
            sessionUserMap.put(session.getId(), userId);
            logger.info("WebSocket连接建立成功 - 用户ID: {}, 会话ID: {}", userId, session.getId());
            
            // 发送连接成功消息
            sendMessage(session, new WebSocketMessage("system", "连接成功", null));
        } else {
            logger.warn("WebSocket连接失败 - 无法获取用户ID");
            session.close();
        }
    }
    
    /**
     * 接收到客户端消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = sessionUserMap.get(session.getId());
        logger.debug("收到来自用户 {} 的消息: {}", userId, message.getPayload());
        
        // 可以在这里处理客户端发送的消息（如心跳包）
        if ("ping".equals(message.getPayload())) {
            sendMessage(session, new WebSocketMessage("system", "pong", null));
        }
    }
    
    /**
     * 连接关闭后调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = sessionUserMap.remove(session.getId());
        if (userId != null) {
            sessions.remove(userId);
            logger.info("WebSocket连接关闭 - 用户ID: {}, 会话ID: {}, 状态: {}", 
                       userId, session.getId(), status);
        }
    }
    
    /**
     * 传输错误时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = sessionUserMap.get(session.getId());
        logger.error("WebSocket传输错误 - 用户ID: {}, 错误: {}", userId, exception.getMessage());
        
        if (session.isOpen()) {
            session.close();
        }
    }
    
    /**
     * 向指定用户发送预警消息
     * 
     * @param userId 用户ID
     * @param alertMessage 预警消息
     * @return 是否发送成功
     */
    public boolean sendAlertToUser(String userId, AlertMessage alertMessage) {
        WebSocketSession session = sessions.get(userId);
        
        if (session != null && session.isOpen()) {
            try {
                WebSocketMessage message = new WebSocketMessage(
                    "alert",
                    alertMessage.getAlertTitle(),
                    alertMessage
                );
                sendMessage(session, message);
                logger.info("预警消息已推送 - 用户ID: {}, 预警类型: {}", 
                           userId, alertMessage.getAlertType());
                return true;
            } catch (Exception e) {
                logger.error("发送预警消息失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
                return false;
            }
        } else {
            logger.warn("用户不在线或会话已关闭 - 用户ID: {}", userId);
            return false;
        }
    }
    
    /**
     * 向多个用户发送预警消息
     */
    public void sendAlertToUsers(java.util.List<String> userIds, AlertMessage alertMessage) {
        for (String userId : userIds) {
            sendAlertToUser(userId, alertMessage);
        }
    }
    
    /**
     * 广播消息到所有在线用户
     */
    public void broadcastMessage(String messageType, String content, Object data) {
        WebSocketMessage message = new WebSocketMessage(messageType, content, data);
        
        sessions.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    sendMessage(session, message);
                } catch (Exception e) {
                    logger.error("广播消息失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
                }
            }
        });
        
        logger.info("广播消息已发送 - 在线用户数: {}", sessions.size());
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return sessions.size();
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        WebSocketSession session = sessions.get(userId);
        return session != null && session.isOpen();
    }
    
    /**
     * 从会话中提取用户ID
     */
    private String getUserIdFromSession(WebSocketSession session) {
        try {
            // 从URL参数中获取userId: ws://localhost:8080/ws/alerts?userId=123
            String query = session.getUri().getQuery();
            if (query != null && query.contains("userId=")) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("userId=")) {
                        return param.substring(7);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("提取用户ID失败: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 发送消息到指定会话
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(json));
    }
    
    /**
     * WebSocket消息封装类
     */
    public static class WebSocketMessage {
        private String type;        // 消息类型: alert, system, notification
        private String message;     // 消息内容
        private Object data;        // 附加数据
        private long timestamp;     // 时间戳
        
        public WebSocketMessage(String type, String message, Object data) {
            this.type = type;
            this.message = message;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 预警消息类
     */
    public static class AlertMessage {
        private Long alertId;
        private String alertCode;
        private String alertType;
        private String alertLevel;
        private String alertTitle;
        private String alertMessage;
        private Long relatedId;
        private String relatedType;
        private Object alertData;
        
        // Getters and Setters
        public Long getAlertId() { return alertId; }
        public void setAlertId(Long alertId) { this.alertId = alertId; }
        
        public String getAlertCode() { return alertCode; }
        public void setAlertCode(String alertCode) { this.alertCode = alertCode; }
        
        public String getAlertType() { return alertType; }
        public void setAlertType(String alertType) { this.alertType = alertType; }
        
        public String getAlertLevel() { return alertLevel; }
        public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }
        
        public String getAlertTitle() { return alertTitle; }
        public void setAlertTitle(String alertTitle) { this.alertTitle = alertTitle; }
        
        public String getAlertMessage() { return alertMessage; }
        public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }
        
        public Long getRelatedId() { return relatedId; }
        public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
        
        public String getRelatedType() { return relatedType; }
        public void setRelatedType(String relatedType) { this.relatedType = relatedType; }
        
        public Object getAlertData() { return alertData; }
        public void setAlertData(Object alertData) { this.alertData = alertData; }
    }
}
