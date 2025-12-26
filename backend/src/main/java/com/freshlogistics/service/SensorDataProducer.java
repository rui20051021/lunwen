package com.freshlogistics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshlogistics.entity.SensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 传感器数据生产者
 * 模拟传感器采集数据并发送到Kafka
 */
@Service
public class SensorDataProducer {
    
    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 发送传感器数据到Kafka
     */
    public void sendSensorData(SensorData sensorData) {
        if (kafkaTemplate == null) {
            System.out.println("⚠️ Kafka未配置，跳过数据发送: " + sensorData);
            return;
        }
        
        try {
            String message = objectMapper.writeValueAsString(sensorData);
            kafkaTemplate.send("sensor-data", sensorData.getSensorId(), message);
            System.out.println("✅ 传感器数据已发送到Kafka: " + sensorData);
        } catch (Exception e) {
            System.err.println("🔴 发送传感器数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 模拟车辆温度传感器数据采集
     */
    public void simulateTemperatureData(Long vehicleId, String vehicleCode) {
        // 模拟真实的温度数据（2-6℃正常范围）
        double temperature = 2.0 + (Math.random() * 4.0);
        temperature = Math.round(temperature * 10.0) / 10.0;
        
        SensorData data = new SensorData(
            "TEMP_" + vehicleCode,
            "temperature",
            vehicleId,
            temperature
        );
        data.setDataUnit("℃");
        
        sendSensorData(data);
    }
}

