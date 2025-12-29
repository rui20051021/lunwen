package com.freshlogistics.service;

import com.freshlogistics.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 预警服务 - 实现实时预警触发和推送
 * 
 * 功能：
 * 1. 超时预警检测（定时任务）
 * 2. 温控预警检测（实时）
 * 3. 路径偏离预警检测（实时）
 * 4. 预警消息推送（WebSocket）
 * 5. 预警记录管理
 */
@Service
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private WebSocketHandler webSocketHandler;
    
    /**
     * 定时检查超时预警 - 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000) // 60秒
    public void checkTimeoutAlerts() {
        try {
            logger.debug("开始检查超时预警...");
            
            // 查询所有在途订单
            String sql = "SELECT t.id, t.transport_code, t.order_id, t.vehicle_id, t.driver_id, " +
                        "t.route_id, t.planned_arrival_time, t.actual_start_time, " +
                        "r.max_delay_tolerance, r.estimated_duration, o.supplier_id, o.purchaser_id " +
                        "FROM transports t " +
                        "LEFT JOIN transport_routes r ON t.route_id = r.id " +
                        "LEFT JOIN orders o ON t.order_id = o.id " +
                        "WHERE t.transport_status = 'in_transit' " +
                        "AND t.actual_arrival_time IS NULL";
            
            List<Map<String, Object>> transports = jdbcTemplate.queryForList(sql);
            
            for (Map<String, Object> transport : transports) {
                checkSingleTransportTimeout(transport);
            }
            
            logger.debug("超时预警检查完成 - 检查了 {} 个运输任务", transports.size());
            
        } catch (Exception e) {
            logger.error("超时预警检查失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查单个运输任务是否超时
     */
    private void checkSingleTransportTimeout(Map<String, Object> transport) {
        try {
            Long transportId = ((Number) transport.get("id")).longValue();
            Timestamp plannedArrivalTime = (Timestamp) transport.get("planned_arrival_time");
            Timestamp actualStartTime = (Timestamp) transport.get("actual_start_time");
            Integer maxDelayTolerance = (Integer) transport.get("max_delay_tolerance");
            
            if (plannedArrivalTime == null || actualStartTime == null) {
                return;
            }
            
            // 计算当前时间与计划到达时间的差值
            long currentTime = System.currentTimeMillis();
            long plannedTime = plannedArrivalTime.getTime();
            long delayMinutes = (currentTime - plannedTime) / (1000 * 60);
            
            // 如果延迟超过容忍时长，触发预警
            if (delayMinutes > (maxDelayTolerance != null ? maxDelayTolerance : 30)) {
                // 检查是否已经触发过预警（避免重复预警）
                if (!isAlertAlreadyTriggered(transportId, "timeout")) {
                    triggerTimeoutAlert(transport, delayMinutes);
                }
            }
            
        } catch (Exception e) {
            logger.error("检查运输任务超时失败: {}", e.getMessage());
        }
    }
    
    /**
     * 触发超时预警
     */
    @Transactional
    public void triggerTimeoutAlert(Map<String, Object> transport, long delayMinutes) {
        try {
            Long transportId = ((Number) transport.get("id")).longValue();
            String transportCode = (String) transport.get("transport_code");
            Long orderId = ((Number) transport.get("order_id")).longValue();
            Long supplierId = transport.get("supplier_id") != null ? 
                             ((Number) transport.get("supplier_id")).longValue() : null;
            Long purchaserId = transport.get("purchaser_id") != null ? 
                              ((Number) transport.get("purchaser_id")).longValue() : null;
            
            // 生成预警记录
            String alertCode = "TIMEOUT_" + System.currentTimeMillis();
            String alertMessage = String.format("运输任务 %s 已延迟 %d 分钟，请及时处理", 
                                               transportCode, delayMinutes);
            
            // 保存预警记录到数据库
            String insertSql = "INSERT INTO alert_records " +
                             "(alert_code, alert_type, alert_level, alert_title, alert_message, " +
                             "related_id, related_type, alert_status, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(insertSql,
                alertCode,
                "timeout",
                "high",
                "运输超时预警",
                alertMessage,
                transportId,
                "transport",
                "pending",
                Timestamp.valueOf(LocalDateTime.now())
            );
            
            // 获取预警ID
            Long alertId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()", Long.class);
            
            // 构建预警消息
            WebSocketHandler.AlertMessage alert = new WebSocketHandler.AlertMessage();
            alert.setAlertId(alertId);
            alert.setAlertCode(alertCode);
            alert.setAlertType("timeout");
            alert.setAlertLevel("high");
            alert.setAlertTitle("运输超时预警");
            alert.setAlertMessage(alertMessage);
            alert.setRelatedId(transportId);
            alert.setRelatedType("transport");
            
            Map<String, Object> alertData = new HashMap<>();
            alertData.put("transportId", transportId);
            alertData.put("transportCode", transportCode);
            alertData.put("orderId", orderId);
            alertData.put("delayMinutes", delayMinutes);
            alert.setAlertData(alertData);
            
            // 推送预警消息到相关用户
            List<String> userIds = new ArrayList<>();
            if (supplierId != null) {
                userIds.add(String.valueOf(supplierId));
            }
            if (purchaserId != null) {
                userIds.add(String.valueOf(purchaserId));
            }
            
            webSocketHandler.sendAlertToUsers(userIds, alert);
            
            logger.info("超时预警已触发 - 运输任务: {}, 延迟: {} 分钟", transportCode, delayMinutes);
            
        } catch (Exception e) {
            logger.error("触发超时预警失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查温控预警（由Kafka消费者调用）
     */
    @Transactional
    public void checkTemperatureAlert(Map<String, Object> sensorData) {
        try {
            Long vehicleId = ((Number) sensorData.get("vehicle_id")).longValue();
            BigDecimal temperature = (BigDecimal) sensorData.get("temperature");
            BigDecimal humidity = sensorData.get("humidity") != null ? 
                                 (BigDecimal) sensorData.get("humidity") : null;
            
            // 查询该车辆当前运输的订单和产品信息
            String sql = "SELECT t.id as transport_id, t.transport_code, t.order_id, " +
                        "o.supplier_id, o.purchaser_id, p.id as product_id, p.product_name, " +
                        "p.min_temperature, p.max_temperature, p.min_humidity, p.max_humidity " +
                        "FROM transports t " +
                        "JOIN orders o ON t.order_id = o.id " +
                        "JOIN order_items oi ON o.id = oi.order_id " +
                        "JOIN products p ON oi.product_id = p.id " +
                        "WHERE t.vehicle_id = ? AND t.transport_status = 'in_transit' " +
                        "LIMIT 1";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, vehicleId);
            
            if (results.isEmpty()) {
                return; // 该车辆没有在途运输任务
            }
            
            Map<String, Object> transport = results.get(0);
            BigDecimal minTemp = (BigDecimal) transport.get("min_temperature");
            BigDecimal maxTemp = (BigDecimal) transport.get("max_temperature");
            
            // 检查温度是否超出范围
            boolean tempAlert = false;
            String alertReason = "";
            
            if (minTemp != null && temperature.compareTo(minTemp) < 0) {
                tempAlert = true;
                alertReason = String.format("温度过低：当前 %.1f℃，要求 ≥ %.1f℃", 
                                          temperature.doubleValue(), minTemp.doubleValue());
            } else if (maxTemp != null && temperature.compareTo(maxTemp) > 0) {
                tempAlert = true;
                alertReason = String.format("温度过高：当前 %.1f℃，要求 ≤ %.1f℃", 
                                          temperature.doubleValue(), maxTemp.doubleValue());
            }
            
            if (tempAlert) {
                Long transportId = ((Number) transport.get("transport_id")).longValue();
                
                // 检查是否已经触发过预警（5分钟内不重复预警）
                if (!isAlertRecentlyTriggered(transportId, "temperature", 5)) {
                    triggerTemperatureAlert(transport, temperature, humidity, alertReason);
                }
            }
            
        } catch (Exception e) {
            logger.error("检查温控预警失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 触发温控预警
     */
    @Transactional
    public void triggerTemperatureAlert(Map<String, Object> transport, 
                                       BigDecimal temperature, 
                                       BigDecimal humidity, 
                                       String alertReason) {
        try {
            Long transportId = ((Number) transport.get("transport_id")).longValue();
            String transportCode = (String) transport.get("transport_code");
            Long orderId = ((Number) transport.get("order_id")).longValue();
            String productName = (String) transport.get("product_name");
            Long supplierId = transport.get("supplier_id") != null ? 
                             ((Number) transport.get("supplier_id")).longValue() : null;
            Long purchaserId = transport.get("purchaser_id") != null ? 
                              ((Number) transport.get("purchaser_id")).longValue() : null;
            
            // 生成预警记录
            String alertCode = "TEMP_" + System.currentTimeMillis();
            String alertMessage = String.format("运输任务 %s（产品：%s）温控异常：%s", 
                                               transportCode, productName, alertReason);
            
            // 保存预警记录
            String insertSql = "INSERT INTO alert_records " +
                             "(alert_code, alert_type, alert_level, alert_title, alert_message, " +
                             "related_id, related_type, alert_status, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(insertSql,
                alertCode,
                "temperature",
                "critical",
                "温控异常预警",
                alertMessage,
                transportId,
                "transport",
                "pending",
                Timestamp.valueOf(LocalDateTime.now())
            );
            
            Long alertId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()", Long.class);
            
            // 构建预警消息
            WebSocketHandler.AlertMessage alert = new WebSocketHandler.AlertMessage();
            alert.setAlertId(alertId);
            alert.setAlertCode(alertCode);
            alert.setAlertType("temperature");
            alert.setAlertLevel("critical");
            alert.setAlertTitle("温控异常预警");
            alert.setAlertMessage(alertMessage);
            alert.setRelatedId(transportId);
            alert.setRelatedType("transport");
            
            Map<String, Object> alertData = new HashMap<>();
            alertData.put("transportId", transportId);
            alertData.put("transportCode", transportCode);
            alertData.put("orderId", orderId);
            alertData.put("productName", productName);
            alertData.put("currentTemperature", temperature);
            alertData.put("currentHumidity", humidity);
            alertData.put("alertReason", alertReason);
            alert.setAlertData(alertData);
            
            // 推送预警消息
            List<String> userIds = new ArrayList<>();
            if (supplierId != null) {
                userIds.add(String.valueOf(supplierId));
            }
            if (purchaserId != null) {
                userIds.add(String.valueOf(purchaserId));
            }
            
            webSocketHandler.sendAlertToUsers(userIds, alert);
            
            logger.warn("温控预警已触发 - 运输任务: {}, 原因: {}", transportCode, alertReason);
            
        } catch (Exception e) {
            logger.error("触发温控预警失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查路径偏离预警（由GPS数据更新时调用）
     */
    @Transactional
    public void checkRouteDeviationAlert(Map<String, Object> gpsData) {
        try {
            Long vehicleId = ((Number) gpsData.get("vehicle_id")).longValue();
            BigDecimal latitude = (BigDecimal) gpsData.get("latitude");
            BigDecimal longitude = (BigDecimal) gpsData.get("longitude");
            
            // 查询该车辆当前运输任务和路线信息
            String sql = "SELECT t.id as transport_id, t.transport_code, t.order_id, t.route_id, " +
                        "o.supplier_id, o.purchaser_id, r.route_name, r.route_points, " +
                        "r.deviation_threshold " +
                        "FROM transports t " +
                        "JOIN orders o ON t.order_id = o.id " +
                        "JOIN transport_routes r ON t.route_id = r.id " +
                        "WHERE t.vehicle_id = ? AND t.transport_status = 'in_transit'";
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, vehicleId);
            
            if (results.isEmpty()) {
                return;
            }
            
            Map<String, Object> transport = results.get(0);
            String routePoints = (String) transport.get("route_points");
            Integer deviationThreshold = (Integer) transport.get("deviation_threshold");
            
            if (routePoints == null || deviationThreshold == null) {
                return;
            }
            
            // 计算车辆位置到路线的最短距离
            double minDistance = calculateMinDistanceToRoute(
                latitude.doubleValue(), 
                longitude.doubleValue(), 
                routePoints
            );
            
            // 如果偏离距离超过阈值，触发预警
            if (minDistance > deviationThreshold) {
                Long transportId = ((Number) transport.get("transport_id")).longValue();
                
                // 检查是否已经触发过预警（10分钟内不重复预警）
                if (!isAlertRecentlyTriggered(transportId, "route_deviation", 10)) {
                    triggerRouteDeviationAlert(transport, latitude, longitude, minDistance);
                }
            }
            
        } catch (Exception e) {
            logger.error("检查路径偏离预警失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 触发路径偏离预警
     */
    @Transactional
    public void triggerRouteDeviationAlert(Map<String, Object> transport,
                                          BigDecimal latitude,
                                          BigDecimal longitude,
                                          double deviationDistance) {
        try {
            Long transportId = ((Number) transport.get("transport_id")).longValue();
            String transportCode = (String) transport.get("transport_code");
            Long orderId = ((Number) transport.get("order_id")).longValue();
            String routeName = (String) transport.get("route_name");
            Long supplierId = transport.get("supplier_id") != null ? 
                             ((Number) transport.get("supplier_id")).longValue() : null;
            Long purchaserId = transport.get("purchaser_id") != null ? 
                              ((Number) transport.get("purchaser_id")).longValue() : null;
            
            // 生成预警记录
            String alertCode = "ROUTE_" + System.currentTimeMillis();
            String alertMessage = String.format("运输任务 %s 偏离预设路线 %s，偏离距离约 %.0f 米", 
                                               transportCode, routeName, deviationDistance);
            
            // 保存预警记录
            String insertSql = "INSERT INTO alert_records " +
                             "(alert_code, alert_type, alert_level, alert_title, alert_message, " +
                             "related_id, related_type, alert_status, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(insertSql,
                alertCode,
                "route_deviation",
                "medium",
                "路径偏离预警",
                alertMessage,
                transportId,
                "transport",
                "pending",
                Timestamp.valueOf(LocalDateTime.now())
            );
            
            Long alertId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()", Long.class);
            
            // 构建预警消息
            WebSocketHandler.AlertMessage alert = new WebSocketHandler.AlertMessage();
            alert.setAlertId(alertId);
            alert.setAlertCode(alertCode);
            alert.setAlertType("route_deviation");
            alert.setAlertLevel("medium");
            alert.setAlertTitle("路径偏离预警");
            alert.setAlertMessage(alertMessage);
            alert.setRelatedId(transportId);
            alert.setRelatedType("transport");
            
            Map<String, Object> alertData = new HashMap<>();
            alertData.put("transportId", transportId);
            alertData.put("transportCode", transportCode);
            alertData.put("orderId", orderId);
            alertData.put("routeName", routeName);
            alertData.put("currentLatitude", latitude);
            alertData.put("currentLongitude", longitude);
            alertData.put("deviationDistance", deviationDistance);
            alert.setAlertData(alertData);
            
            // 推送预警消息
            List<String> userIds = new ArrayList<>();
            if (supplierId != null) {
                userIds.add(String.valueOf(supplierId));
            }
            if (purchaserId != null) {
                userIds.add(String.valueOf(purchaserId));
            }
            
            webSocketHandler.sendAlertToUsers(userIds, alert);
            
            logger.warn("路径偏离预警已触发 - 运输任务: {}, 偏离距离: {} 米", 
                       transportCode, deviationDistance);
            
        } catch (Exception e) {
            logger.error("触发路径偏离预警失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理预警 - 开始处理
     */
    @Transactional
    public void processAlert(Long alertId, Long processorId, String processNotes) {
        try {
            String sql = "UPDATE alert_records SET " +
                        "alert_status = 'processing', " +
                        "processor_id = ?, " +
                        "process_time = ?, " +
                        "process_notes = ? " +
                        "WHERE id = ?";
            
            jdbcTemplate.update(sql, 
                processorId, 
                Timestamp.valueOf(LocalDateTime.now()),
                processNotes,
                alertId
            );
            
            logger.info("预警开始处理 - 预警ID: {}, 处理人: {}", alertId, processorId);
            
        } catch (Exception e) {
            logger.error("处理预警失败: {}", e.getMessage(), e);
            throw new RuntimeException("处理预警失败", e);
        }
    }
    
    /**
     * 完成预警处理
     */
    @Transactional
    public void completeAlert(Long alertId, String processResult) {
        try {
            String sql = "UPDATE alert_records SET " +
                        "alert_status = 'processed', " +
                        "process_result = ?, " +
                        "updated_at = ? " +
                        "WHERE id = ?";
            
            jdbcTemplate.update(sql,
                processResult,
                Timestamp.valueOf(LocalDateTime.now()),
                alertId
            );
            
            logger.info("预警处理完成 - 预警ID: {}", alertId);
            
        } catch (Exception e) {
            logger.error("完成预警处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("完成预警处理失败", e);
        }
    }
    
    /**
     * 忽略预警
     */
    @Transactional
    public void ignoreAlert(Long alertId, Long processorId, String ignoreReason) {
        try {
            String sql = "UPDATE alert_records SET " +
                        "alert_status = 'ignored', " +
                        "processor_id = ?, " +
                        "process_time = ?, " +
                        "process_notes = ? " +
                        "WHERE id = ?";
            
            jdbcTemplate.update(sql,
                processorId,
                Timestamp.valueOf(LocalDateTime.now()),
                ignoreReason,
                alertId
            );
            
            logger.info("预警已忽略 - 预警ID: {}, 原因: {}", alertId, ignoreReason);
            
        } catch (Exception e) {
            logger.error("忽略预警失败: {}", e.getMessage(), e);
            throw new RuntimeException("忽略预警失败", e);
        }
    }
    
    /**
     * 检查预警是否已经触发过
     */
    private boolean isAlertAlreadyTriggered(Long relatedId, String alertType) {
        String sql = "SELECT COUNT(*) FROM alert_records " +
                    "WHERE related_id = ? AND alert_type = ? " +
                    "AND alert_status IN ('pending', 'processing')";
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, relatedId, alertType);
        return count != null && count > 0;
    }
    
    /**
     * 检查预警是否在指定时间内触发过
     */
    private boolean isAlertRecentlyTriggered(Long relatedId, String alertType, int minutes) {
        String sql = "SELECT COUNT(*) FROM alert_records " +
                    "WHERE related_id = ? AND alert_type = ? " +
                    "AND created_at > DATE_SUB(NOW(), INTERVAL ? MINUTE)";
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
                                                   relatedId, alertType, minutes);
        return count != null && count > 0;
    }
    
    /**
     * 计算点到路线的最短距离（单位：米）
     */
    private double calculateMinDistanceToRoute(double pointLat, double pointLng, String routePointsJson) {
        try {
            // 解析路线坐标点（假设格式为JSON数组）
            // 示例: [{"lat":39.9,"lng":116.4},{"lat":39.95,"lng":116.45}]
            
            // 简化实现：计算到所有路线点的最短距离
            // 实际应该计算到线段的最短距离
            
            double minDistance = Double.MAX_VALUE;
            
            // 这里简化处理，实际应该解析JSON并计算到每个线段的距离
            // 暂时返回一个模拟值
            return 100.0; // 模拟返回100米
            
        } catch (Exception e) {
            logger.error("计算路线距离失败: {}", e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * 计算两点之间的距离（Haversine公式）
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000; // 地球半径（米）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}