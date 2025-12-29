package com.freshlogistics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka消费者服务 - 实时处理传感器数据
 * 
 * 功能：
 * 1. 消费温湿度传感器数据
 * 2. 消费GPS定位数据
 * 3. 触发实时预警检测
 * 4. 保存传感器数据到数据库
 */
@Service
public class KafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AlertService alertService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 消费温湿度传感器数据
     */
    @KafkaListener(topics = "sensor-temperature", groupId = "freshlogistics-group")
    public void consumeTemperatureData(String message) {
        try {
            logger.debug("收到温度传感器数据: {}", message);
            
            // 解析消息
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            
            // 保存传感器数据到数据库
            saveSensorData(data);
            
            // 触发温控预警检测
            alertService.checkTemperatureAlert(data);
            
        } catch (Exception e) {
            logger.error("处理温度传感器数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 消费GPS定位数据
     */
    @KafkaListener(topics = "sensor-gps", groupId = "freshlogistics-group")
    public void consumeGpsData(String message) {
        try {
            logger.debug("收到GPS定位数据: {}", message);
            
            // 解析消息
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            
            // 保存GPS数据到数据库
            saveGpsData(data);
            
            // 触发路径偏离预警检测
            alertService.checkRouteDeviationAlert(data);
            
        } catch (Exception e) {
            logger.error("处理GPS定位数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 保存传感器数据到数据库
     */
    private void saveSensorData(Map<String, Object> data) {
        try {
            String sql = "INSERT INTO sensor_data " +
                        "(vehicle_id, sensor_type, temperature, humidity, " +
                        "latitude, longitude, collected_at, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            Long vehicleId = data.get("vehicle_id") != null ? 
                            ((Number) data.get("vehicle_id")).longValue() : null;
            String sensorType = (String) data.get("sensor_type");
            BigDecimal temperature = data.get("temperature") != null ? 
                                    new BigDecimal(data.get("temperature").toString()) : null;
            BigDecimal humidity = data.get("humidity") != null ? 
                                 new BigDecimal(data.get("humidity").toString()) : null;
            BigDecimal latitude = data.get("latitude") != null ? 
                                 new BigDecimal(data.get("latitude").toString()) : null;
            BigDecimal longitude = data.get("longitude") != null ? 
                                  new BigDecimal(data.get("longitude").toString()) : null;
            
            Timestamp collectedAt = data.get("collected_at") != null ? 
                                   Timestamp.valueOf((String) data.get("collected_at")) : 
                                   Timestamp.valueOf(LocalDateTime.now());
            
            jdbcTemplate.update(sql,
                vehicleId,
                sensorType != null ? sensorType : "temperature",
                temperature,
                humidity,
                latitude,
                longitude,
                collectedAt,
                Timestamp.valueOf(LocalDateTime.now())
            );
            
            logger.debug("传感器数据已保存 - 车辆ID: {}", vehicleId);
            
        } catch (Exception e) {
            logger.error("保存传感器数据失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 保存GPS数据到数据库
     */
    private void saveGpsData(Map<String, Object> data) {
        try {
            // 更新车辆实时位置
            String updateSql = "UPDATE vehicles SET " +
                             "current_latitude = ?, " +
                             "current_longitude = ?, " +
                             "location_updated_at = ? " +
                             "WHERE id = ?";
            
            Long vehicleId = ((Number) data.get("vehicle_id")).longValue();
            BigDecimal latitude = new BigDecimal(data.get("latitude").toString());
            BigDecimal longitude = new BigDecimal(data.get("longitude").toString());
            
            jdbcTemplate.update(updateSql,
                latitude,
                longitude,
                Timestamp.valueOf(LocalDateTime.now()),
                vehicleId
            );
            
            // 同时保存到传感器数据表（用于历史轨迹查询）
            saveSensorData(data);
            
            logger.debug("GPS数据已保存 - 车辆ID: {}", vehicleId);
            
        } catch (Exception e) {
            logger.error("保存GPS数据失败: {}", e.getMessage(), e);
        }
    }
}
