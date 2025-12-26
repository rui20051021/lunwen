package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @GetMapping
    public Map<String, Object> getOrders(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderCode) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 模拟订单数据
        List<Map<String, Object>> orders = new ArrayList<>();
        orders.add(Map.of(
            "id", 1,
            "orderCode", "ORD20250927001",
            "supplierName", "新鲜农场有限公司",
            "purchaserName", "系统管理员",
            "orderStatus", "created",
            "totalAmount", 1550.00,
            "createdAt", "2025-01-27 10:00:00"
        ));
        orders.add(Map.of(
            "id", 2,
            "orderCode", "ORD20250927002",
            "supplierName", "绿色蔬菜基地",
            "purchaserName", "系统管理员", 
            "orderStatus", "in_transit",
            "totalAmount", 890.50,
            "createdAt", "2025-01-27 11:30:00"
        ));
        
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
            "records", orders,
            "total", orders.size(),
            "current", page,
            "size", size
        ));
        
        return result;
    }

    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> order) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟创建订单
            order.put("id", System.currentTimeMillis());
            order.put("orderCode", "ORD" + System.currentTimeMillis());
            order.put("orderStatus", "created");
            order.put("createdAt", java.time.LocalDateTime.now().toString());
            
            result.put("code", 200);
            result.put("message", "订单创建成功");
            result.put("data", order);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getOrderDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟订单详情
        Map<String, Object> order = Map.of(
            "id", id,
            "orderCode", "ORD20250927" + String.format("%03d", id),
            "supplierName", "新鲜农场有限公司",
            "purchaserName", "系统管理员",
            "orderStatus", "created",
            "totalAmount", 1550.00,
            "pickupAddress", "北京市朝阳区农贸市场",
            "deliveryAddress", "天津市河西区超市",
            "orderItems", List.of(
                Map.of("productName", "新鲜橙子", "quantity", 100, "unitPrice", 15.50)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", order);
        
        return result;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> statistics = Map.of(
            "totalCount", 25,
            "todayCount", 3,
            "inTransitCount", 8,
            "overdueCount", 2,
            "statusStatistics", List.of(
                Map.of("status", "created", "count", 5),
                Map.of("status", "in_transit", "count", 8),
                Map.of("status", "completed", "count", 12)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", statistics);
        
        return result;
    }
}
