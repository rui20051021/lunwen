package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库供应商数据控制器
 */
@RestController
@RequestMapping("/database/supplier")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseSupplierController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取供应商信息
     */
    @GetMapping("/all")
    public Map<String, Object> getAllSuppliers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM suppliers WHERE cooperation_status = 'active' ORDER BY created_at DESC";
            List<Map<String, Object>> suppliers = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", suppliers);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取供应商产品关联
     */
    @GetMapping("/products")
    public Map<String, Object> getSupplierProducts() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT sp.*, p.product_name, s.supplier_name " +
                        "FROM supplier_products sp " +
                        "LEFT JOIN products p ON sp.product_id = p.id " +
                        "LEFT JOIN suppliers s ON sp.supplier_id = s.id " +
                        "WHERE sp.status = 1 " +
                        "ORDER BY sp.created_at DESC";
            
            List<Map<String, Object>> supplierProducts = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", supplierProducts);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取供应商统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getSupplierStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_suppliers, " +
                        "COUNT(CASE WHEN cooperation_status = 'active' THEN 1 END) as active_suppliers, " +
                        "AVG(credit_rating) as avg_credit_rating " +
                        "FROM suppliers";
            
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
