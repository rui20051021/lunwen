package com.freshlogistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 传感器数据模拟器
 * 
 * 用于测试实时预警功能
 */
@RestController
@RequestMapping("/api/simulator")
@CrossOrigin(origins = "*")
public class SensorSimulatorController {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    
    /**
     * 模拟发送温度传感器数据
     * 
     * @param vehicleId 车辆ID
     * @param temperature 温度（可选，不传则随机生成）
     */
    @PostMapping("/temperature")
    public Map<String, Object> simulateTemperature(
            @RequestParam Long vehicleId,
            @RequestParam(required = false) Double temperature) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 如果没有传入温度，随机生成（-5℃ 到 35℃）
            if (temperature == null) {
                temperature = -5 + random.nextDouble() * 40;
            }
            
            // 构建传感器数据
            Map<String, Object> sensorData = new HashMap<>();
            sensorData.put("vehicle_id", vehicleId);
            sensorData.put("sensor_type", "temperature");
            sensorData.put("temperature", BigDecimal.valueOf(temperature));
            sensorData.put("humidity", BigDecimal.valueOf(50 + random.nextDouble() * 40));
            sensorData.put("collected_at", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 发送到Kafka
            String message = objectMapper.writeValueAsString(sensorData);
            kafkaTemplate.send("sensor-temperature", message);
            
            response.put("success", true);
            response.put("message", "温度数据已发送");
            response.put("data", sensorData);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 模拟发送GPS定位数据
     * 
     * @param vehicleId 车辆ID
     * @param latitude 纬度（可选）
     * @param longitude 经度（可选）
     */
    @PostMapping("/gps")
    public Map<String, Object> simulateGps(
            @RequestParam Long vehicleId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 如果没有传入坐标，使用北京附近的随机坐标
            if (latitude == null) {
                latitude = 39.9 + random.nextDouble() * 0.2;
            }
            if (longitude == null) {
                longitude = 116.3 + random.nextDouble() * 0.2;
            }
            
            // 构建GPS数据
            Map<String, Object> gpsData = new HashMap<>();
            gpsData.put("vehicle_id", vehicleId);
            gpsData.put("sensor_type", "gps");
            gpsData.put("latitude", BigDecimal.valueOf(latitude));
            gpsData.put("longitude", BigDecimal.valueOf(longitude));
            gpsData.put("collected_at", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 发送到Kafka
            String message = objectMapper.writeValueAsString(gpsData);
            kafkaTemplate.send("sensor-gps", message);
            
            response.put("success", true);
            response.put("message", "GPS数据已发送");
            response.put("data", gpsData);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 模拟温度异常（触发温控预警）
     * 
     * @param vehicleId 车辆ID
     * @param abnormalType 异常类型：high（过高）或 low（过低）
     */
    @PostMapping("/temperature-abnormal")
    public Map<String, Object> simulateTemperatureAbnormal(
            @RequestParam Long vehicleId,
            @RequestParam(defaultValue = "high") String abnormalType) {
        
        Double temperature;
        
        if ("high".equals(abnormalType)) {
            // 模拟温度过高（25℃ - 35℃）
            temperature = 25.0 + random.nextDouble() * 10;
        } else {
            // 模拟温度过低（-10℃ - 0℃）
            temperature = -10.0 + random.nextDouble() * 10;
        }
        
        return simulateTemperature(vehicleId, temperature);
    }
    
    /**
     * 批量模拟传感器数据（用于压力测试）
     * 
     * @param count 数据条数
     * @param vehicleIdStart 起始车辆ID
     */
    @PostMapping("/batch")
    public Map<String, Object> simulateBatch(
            @RequestParam(defaultValue = "100") int count,
            @RequestParam(defaultValue = "1") Long vehicleIdStart) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int successCount = 0;
            
            for (int i = 0; i < count; i++) {
                Long vehicleId = vehicleIdStart + (i % 10); // 循环使用10个车辆ID
                
                // 随机发送温度或GPS数据
                if (random.nextBoolean()) {
                    simulateTemperature(vehicleId, null);
                } else {
                    simulateGps(vehicleId, null, null);
                }
                
                successCount++;
                
                // 避免发送过快
                Thread.sleep(10);
            }
            
            response.put("success", true);
            response.put("message", String.format("已发送 %d 条数据", successCount));
            response.put("count", successCount);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量发送失败: " + e.getMessage());
        }
        
        return response;
    }
}
