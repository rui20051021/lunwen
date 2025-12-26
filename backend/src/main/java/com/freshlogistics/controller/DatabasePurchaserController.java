package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库采购商数据控制器
 */
@RestController
@RequestMapping("/database/purchaser")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabasePurchaserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取采购商订单统计
     */
    @GetMapping("/order-statistics")
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_orders, " +
                        "COUNT(CASE WHEN order_status = 'completed' THEN 1 END) as completed_orders, " +
                        "COUNT(CASE WHEN order_status IN ('in_transit', 'created') THEN 1 END) as pending_orders, " +
                        "SUM(total_amount) as total_amount " +
                        "FROM orders";
            
            Map<String, Object> stats = jdbcTemplate.queryForMap(sql);
            
            result.put("code", 200);
            result.put("message", "统计成功");
            result.put("data", stats);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "统计失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取待收货订单
     */
    @GetMapping("/pending-orders")
    public Map<String, Object> getPendingOrders() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT o.*, s.supplier_name, " +
                        "COALESCE(d.name, 'Unknown') as driver_name, " +
                        "COALESCE(v.license_plate, 'Unknown') as license_plate " +
                        "FROM orders o " +
                        "LEFT JOIN suppliers s ON o.supplier_id = s.id " +
                        "LEFT JOIN transports t ON o.id = t.order_id " +
                        "LEFT JOIN drivers d ON t.driver_id = d.id " +
                        "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                        "WHERE o.order_status = 'in_transit' " +
                        "ORDER BY o.required_delivery_time ASC";
            
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", orders);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取收货记录
     */
    @GetMapping("/receiving-history")
    public Map<String, Object> getReceivingHistory() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT o.*, s.supplier_name " +
                        "FROM orders o " +
                        "LEFT JOIN suppliers s ON o.supplier_id = s.id " +
                        "WHERE o.order_status = 'completed' " +
                        "ORDER BY o.updated_at DESC " +
                        "LIMIT 10";
            
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", records);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取收货统计信息
     */
    @GetMapping("/receiving-statistics")
    public Map<String, Object> getReceivingStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_received, " +
                        "COUNT(CASE WHEN order_status = 'completed' THEN 1 END) as accepted_count, " +
                        "COUNT(CASE WHEN order_status = 'cancelled' THEN 1 END) as rejected_count, " +
                        "ROUND((COUNT(CASE WHEN order_status = 'completed' THEN 1 END) * 100.0 / COUNT(*)), 1) as qualified_rate " +
                        "FROM orders " +
                        "WHERE order_status IN ('completed', 'cancelled')";
            
            Map<String, Object> stats = jdbcTemplate.queryForMap(sql);
            
            result.put("code", 200);
            result.put("message", "统计成功");
            result.put("data", stats);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "统计失败: " + e.getMessage());
        }
        
        return result;
    }
}
