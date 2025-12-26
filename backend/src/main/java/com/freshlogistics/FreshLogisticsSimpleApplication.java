package com.freshlogistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Fresh Logistics 冷链物流智能监测预警系统 - 完整版启动类
 * 
 * @author Fresh Logistics Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@SpringBootApplication
public class FreshLogisticsSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshLogisticsSimpleApplication.class, args);
        System.out.println("===============================================");
        System.out.println("Fresh Logistics 系统启动成功!");
        System.out.println("API文档: http://localhost:8080/api/swagger-ui.html");
        System.out.println("===============================================");
    }
}
