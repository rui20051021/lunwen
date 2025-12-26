package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库传感器和监控数据控制器
 */
@RestController
@RequestMapping("/database/sensor")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseSensorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取传感器数据
     */
    @GetMapping("/data")
    public Map<String, Object> getSensorData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT sd.*, v.license_plate, v.vehicle_code " +
                        "FROM sensor_data sd " +
                        "LEFT JOIN vehicles v ON sd.vehicle_id = v.id " +
                        "ORDER BY sd.created_at DESC " +
                        "LIMIT 20";
            
            List<Map<String, Object>> sensorData = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", sensorData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取实时温度数据
     */
    @GetMapping("/temperature/realtime")
    public Map<String, Object> getRealTimeTemperature() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "v.vehicle_code, " +
                        "v.license_plate, " +
                        "sd.temperature, " +
                        "sd.humidity, " +
                        "sd.location, " +
                        "sd.timestamp as last_update, " +
                        "CASE WHEN sd.temperature > 8 THEN 'alert' " +
                        "     WHEN sd.temperature > 6 THEN 'warning' " +
                        "     ELSE 'normal' END as status " +
                        "FROM vehicles v " +
                        "LEFT JOIN sensor_data sd ON v.id = sd.vehicle_id " +
                        "WHERE v.vehicle_status IN ('available', 'in_transit') " +
                        "AND sd.timestamp >= DATE_SUB(NOW(), INTERVAL 1 HOUR) " +
                        "ORDER BY sd.timestamp DESC";
            
            List<Map<String, Object>> realtimeData = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", realtimeData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取温度趋势数据
     */
    @GetMapping("/temperature/trends")
    public Map<String, Object> getTemperatureTrends() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "DATE(tl.created_at) as date, " +
                        "AVG(tl.temperature) as avg_temp, " +
                        "MIN(tl.temperature) as min_temp, " +
                        "MAX(tl.temperature) as max_temp, " +
                        "AVG(tl.humidity) as avg_humidity, " +
                        "COUNT(*) as record_count " +
                        "FROM temperature_logs tl " +
                        "WHERE tl.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY DATE(tl.created_at) " +
                        "ORDER BY date DESC";
            
            List<Map<String, Object>> trends = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", trends);
            
            System.out.println("✅ 成功获取 " + trends.size() + " 天的温度趋势数据");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询温度趋势失败: " + e.getMessage());
        }
        
        return result;
    }
}
