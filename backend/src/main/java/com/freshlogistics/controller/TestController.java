package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 验证基础功能
 *
 * @author Fresh Logistics Team
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Fresh Logistics API 运行正常!");
        result.put("timestamp", System.currentTimeMillis());
        result.put("status", "success");
        return result;
    }

    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        result.put("database", "freshlogistics");
        result.put("charset", "utf8mb4");
        result.put("status", "ready");
        result.put("message", "数据库配置就绪，等待连接");
        return result;
    }

    @GetMapping("/api")
    public Map<String, Object> testApi() {
        Map<String, Object> result = new HashMap<>();
        result.put("api", "Fresh Logistics API");
        result.put("version", "1.0.0");
        result.put("status", "running");
        result.put("endpoints", new String[]{
            "/test/hello", "/test/db", "/test/health", "/test/api"
        });
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("application", "Fresh Logistics Backend");
        result.put("version", "1.0.0");
        return result;
    }
}
