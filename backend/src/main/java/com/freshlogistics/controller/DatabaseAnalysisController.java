package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库分析数据控制器
 */
@RestController
@RequestMapping("/database/analysis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseAnalysisController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取系统总体统计
     */
    @GetMapping("/overview")
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // 用户统计
            Long totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user WHERE status = 1", Long.class);
            overview.put("totalUsers", totalUsers);
            
            // 产品统计
            Long totalProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products WHERE status = 1", Long.class);
            overview.put("totalProducts", totalProducts);
            
            // 订单统计
            Long totalOrders = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
            overview.put("totalOrders", totalOrders);
            
            // 预警统计
            Long totalAlerts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alert_records", Long.class);
            Long pendingAlerts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alert_records WHERE alert_status = 'pending'", Long.class);
            overview.put("totalAlerts", totalAlerts);
            overview.put("pendingAlerts", pendingAlerts);
            
            // 车辆统计 (vehicles表使用vehicle_status字段)
            Long totalVehicles = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vehicles WHERE vehicle_status = 'available'", Long.class);
            overview.put("totalVehicles", totalVehicles);
            
            // 供应商统计 (suppliers表使用cooperation_status字段)
            Long totalSuppliers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM suppliers WHERE cooperation_status = 'active'", Long.class);
            overview.put("totalSuppliers", totalSuppliers);
            
            result.put("code", 200);
            result.put("message", "统计成功");
            result.put("data", overview);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "统计失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取温度监控数据
     */
    @GetMapping("/temperature")
    public Map<String, Object> getTemperatureAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "DATE(created_at) as date, " +
                        "AVG(temperature) as avg_temp, " +
                        "MIN(temperature) as min_temp, " +
                        "MAX(temperature) as max_temp, " +
                        "COUNT(*) as record_count " +
                        "FROM temperature_logs " +
                        "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY DATE(created_at) " +
                        "ORDER BY date DESC";
            
            List<Map<String, Object>> tempData = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", tempData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取订单趋势分析
     */
    @GetMapping("/order-trends")
    public Map<String, Object> getOrderTrends() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "DATE(created_at) as date, " +
                        "COUNT(*) as order_count, " +
                        "SUM(total_amount) as daily_amount, " +
                        "order_status, " +
                        "COUNT(CASE WHEN order_status = 'delivered' THEN 1 END) as delivered_count " +
                        "FROM orders " +
                        "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
                        "GROUP BY DATE(created_at) " +
                        "ORDER BY date DESC " +
                        "LIMIT 30";
            
            List<Map<String, Object>> trends = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", trends);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取预警趋势分析
     */
    @GetMapping("/alert-trends")
    public Map<String, Object> getAlertTrends() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "DATE(created_at) as date, " +
                        "COUNT(*) as alert_count, " +
                        "alert_level, " +
                        "alert_type, " +
                        "COUNT(CASE WHEN alert_status = 'processed' THEN 1 END) as processed_count " +
                        "FROM alert_records " +
                        "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY DATE(created_at), alert_level " +
                        "ORDER BY date DESC";
            
            List<Map<String, Object>> trends = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", trends);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
}
