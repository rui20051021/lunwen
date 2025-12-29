package com.freshlogistics.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka配置类
 * 用于传感器数据流处理
 */
@Configuration
public class KafkaConfig {
    
    /**
     * 创建传感器数据主题
     * 用于存储传感器采集的实时数据
     */
    @Bean
    public NewTopic sensorDataTopic() {
        return TopicBuilder.name("sensor-data")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    /**
     * 创建温度预警主题
     * 用于存储温度异常预警消息
     */
    @Bean
    public NewTopic temperatureAlertTopic() {
        return TopicBuilder.name("temperature-alert")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

