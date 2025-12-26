package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据分析控制器
 */
@RestController
@RequestMapping("/analysis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalysisController {

    @GetMapping("/overview")
    public Map<String, Object> getAnalysisOverview() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> overview = Map.of(
            "totalOrders", 156,
            "totalRevenue", 2560000.00,
            "totalProducts", 45,
            "totalVehicles", 28,
            "averageDeliveryTime", 18.5,
            "temperatureComplianceRate", 96.2
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", overview);
        
        return result;
    }

    @GetMapping("/efficiency")
    public Map<String, Object> getEfficiencyAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> efficiencyData = List.of(
            Map.of("date", "2025-09-23", "deliveryRate", 98.5, "averageTime", 16.2),
            Map.of("date", "2025-09-24", "deliveryRate", 97.8, "averageTime", 17.1),
            Map.of("date", "2025-09-25", "deliveryRate", 99.1, "averageTime", 15.8),
            Map.of("date", "2025-09-26", "deliveryRate", 96.7, "averageTime", 18.9),
            Map.of("date", "2025-09-27", "deliveryRate", 98.2, "averageTime", 16.5)
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", efficiencyData);
        
        return result;
    }

    @GetMapping("/loss")
    public Map<String, Object> getLossAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> lossData = Map.of(
            "totalLossRate", 2.3,
            "temperatureLoss", 1.2,
            "damageLoss", 0.8,
            "expiredLoss", 0.3,
            "monthlyTrend", List.of(
                Map.of("month", "2025-07", "lossRate", 2.8),
                Map.of("month", "2025-08", "lossRate", 2.5),
                Map.of("month", "2025-09", "lossRate", 2.3)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", lossData);
        
        return result;
    }

    @GetMapping("/temperature")
    public Map<String, Object> getTemperatureAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> temperatureData = List.of(
            Map.of("hour", "00:00", "avgTemp", 3.2, "minTemp", 2.8, "maxTemp", 3.6),
            Map.of("hour", "06:00", "avgTemp", 3.5, "minTemp", 3.1, "maxTemp", 3.9),
            Map.of("hour", "12:00", "avgTemp", 4.1, "minTemp", 3.7, "maxTemp", 4.5),
            Map.of("hour", "18:00", "avgTemp", 3.8, "minTemp", 3.4, "maxTemp", 4.2)
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", temperatureData);
        
        return result;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getAnalysisStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> statistics = Map.of(
            "dataPoints", 50000,
            "reportsGenerated", 25,
            "alertsProcessed", 120,
            "complianceRate", 96.8,
            "systemUptime", "99.9%",
            "lastUpdated", java.time.LocalDateTime.now().toString()
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", statistics);
        
        return result;
    }
}
