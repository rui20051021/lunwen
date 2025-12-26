package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预警管理控制器
 */
@RestController
@RequestMapping("/alerts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlertController {

    @GetMapping
    public Map<String, Object> getAlerts(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String ruleName) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 模拟预警记录数据
        List<Map<String, Object>> alerts = new ArrayList<>();
        alerts.add(Map.of(
            "id", 1,
            "alertCode", "ALT20250929001",
            "alertType", "temperature",
            "alertLevel", "error",
            "alertTitle", "温度超标预警",
            "alertMessage", "车辆京A12345运输温度达到8.5℃，超过安全阈值",
            "triggerValue", 8.5,
            "thresholdValue", 8.0,
            "alertStatus", "processed",
            "createdAt", "2025-09-29 17:15:00"
        ));
        alerts.add(Map.of(
            "id", 2,
            "alertCode", "ALT20250929002",
            "alertType", "timeout",
            "alertLevel", "warning",
            "alertTitle", "运输延迟预警",
            "alertMessage", "运输任务预计延迟35分钟到达",
            "triggerValue", 35,
            "thresholdValue", 30,
            "alertStatus", "pending",
            "createdAt", "2025-09-29 18:00:00"
        ));
        
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
            "records", alerts,
            "total", alerts.size(),
            "current", page,
            "size", size
        ));
        
        return result;
    }

    @GetMapping("/rules")
    public Map<String, Object> getAlertRules(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String ruleName) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 模拟预警规则数据
        List<Map<String, Object>> rules = new ArrayList<>();
        rules.add(Map.of(
            "id", 1,
            "ruleCode", "RULE_TIMEOUT_DEFAULT",
            "ruleName", "默认超时预警规则",
            "ruleType", "timeout",
            "alertLevel", "warning",
            "thresholdValue", 30,
            "isEnabled", 1
        ));
        rules.add(Map.of(
            "id", 2,
            "ruleCode", "RULE_TEMP_FRUIT",
            "ruleName", "水果温度预警规则",
            "ruleType", "temperature",
            "alertLevel", "error",
            "thresholdValue", 8.0,
            "isEnabled", 1
        ));
        
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
            "records", rules,
            "total", rules.size(),
            "current", page,
            "size", size
        ));
        
        return result;
    }

    @PostMapping("/rules")
    public Map<String, Object> createAlertRule(@RequestBody Map<String, Object> rule) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟创建预警规则
            rule.put("id", System.currentTimeMillis());
            rule.put("isEnabled", 1);
            rule.put("createdAt", java.time.LocalDateTime.now().toString());
            
            result.put("code", 200);
            result.put("message", "预警规则创建成功");
            result.put("data", rule);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
        }
        
        return result;
    }

    @PostMapping("/check/temperature")
    public Map<String, Object> checkTemperatureAlert(
            @RequestParam Long productId,
            @RequestParam Double temperature) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 模拟温度预警检查
        List<Map<String, Object>> triggeredRules = new ArrayList<>();
        
        if (temperature > 8.0) {
            triggeredRules.add(Map.of(
                "ruleId", 2,
                "ruleName", "水果温度预警规则",
                "alertLevel", "error",
                "triggerValue", temperature,
                "thresholdValue", 8.0,
                "message", "温度超过阈值，触发预警"
            ));
        }
        
        result.put("code", 200);
        result.put("message", "检查完成");
        result.put("data", triggeredRules);
        
        return result;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> statistics = Map.of(
            "totalRules", 7,
            "enabledRules", 6,
            "todayAlerts", 15,
            "pendingAlerts", 3,
            "typeStatistics", List.of(
                Map.of("type", "timeout", "count", 8),
                Map.of("type", "temperature", "count", 12),
                Map.of("type", "route_deviation", "count", 3)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", statistics);
        
        return result;
    }

    @GetMapping("/rules/statistics")
    public Map<String, Object> getAlertRuleStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> statistics = Map.of(
            "totalRules", 7,
            "enabledRules", 6,
            "disabledRules", 1,
            "ruleTypes", List.of(
                Map.of("type", "timeout", "count", 2),
                Map.of("type", "temperature", "count", 3),
                Map.of("type", "humidity", "count", 2)
            ),
            "alertLevels", List.of(
                Map.of("level", "info", "count", 1),
                Map.of("level", "warning", "count", 3),
                Map.of("level", "error", "count", 2),
                Map.of("level", "critical", "count", 1)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", statistics);
        
        return result;
    }
}
