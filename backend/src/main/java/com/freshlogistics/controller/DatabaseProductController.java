package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库产品数据控制器 - 直接使用JdbcTemplate连接真实数据库
 */
@RestController
@RequestMapping("/database/product")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseProductController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有产品数据
     */
    @GetMapping("/all")
    public Map<String, Object> getAllProducts() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT p.*, c.category_name " +
                        "FROM products p " +
                        "LEFT JOIN product_categories c ON p.category_id = c.id " +
                        "WHERE p.status = 1 " +
                        "ORDER BY p.created_at DESC";
            
            List<Map<String, Object>> products = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功 - 真实数据库数据");
            result.put("data", products);
            
            System.out.println("✅ 成功获取 " + products.size() + " 个产品记录");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询产品数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 分页查询产品数据
     */
    @GetMapping("/list")
    public Map<String, Object> getProductList(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productType) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建查询条件
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT p.*, c.category_name ")
                     .append("FROM products p ")
                     .append("LEFT JOIN product_categories c ON p.category_id = c.id ")
                     .append("WHERE p.status = 1 ");
            
            // 动态添加查询条件
            if (productName != null && !productName.trim().isEmpty()) {
                sqlBuilder.append("AND p.product_name LIKE '%").append(productName.trim()).append("%' ");
            }
            if (productType != null && !productType.trim().isEmpty()) {
                sqlBuilder.append("AND c.category_code = '").append(productType.trim()).append("' ");
            }
            
            sqlBuilder.append("ORDER BY p.created_at DESC ");
            
            // 分页
            int offset = (page - 1) * size;
            sqlBuilder.append("LIMIT ").append(size).append(" OFFSET ").append(offset);
            
            List<Map<String, Object>> products = jdbcTemplate.queryForList(sqlBuilder.toString());
            
            // 查询总数
            String countSql = "SELECT COUNT(*) FROM products p " +
                             "LEFT JOIN product_categories c ON p.category_id = c.id " +
                             "WHERE p.status = 1";
            Long total = jdbcTemplate.queryForObject(countSql, Long.class);
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", products);
            data.put("total", total);
            data.put("size", size);
            data.put("current", page);
            
            result.put("code", 200);
            result.put("message", "分页查询成功");
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "分页查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取产品统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getProductStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_products, " +
                        "COUNT(CASE WHEN status = 1 THEN 1 END) as active_products, " +
                        "COUNT(CASE WHEN status = 0 THEN 1 END) as inactive_products, " +
                        "COUNT(CASE WHEN storage_temp_min IS NOT NULL AND storage_temp_max IS NOT NULL THEN 1 END) as temp_control_products, " +
                        "COUNT(CASE WHEN shelf_life <= 7 THEN 1 END) as short_shelf_life_products " +
                        "FROM products";
            
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
     * 根据类别获取产品
     */
    @GetMapping("/by-category/{categoryCode}")
    public Map<String, Object> getProductsByCategory(@PathVariable String categoryCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT p.*, c.category_name " +
                        "FROM products p " +
                        "LEFT JOIN product_categories c ON p.category_id = c.id " +
                        "WHERE c.category_code = ? AND p.status = 1 " +
                        "ORDER BY p.product_name";
            
            List<Map<String, Object>> products = jdbcTemplate.queryForList(sql, categoryCode);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", products);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取产品类别列表
     */
    @GetMapping("/categories")
    public Map<String, Object> getProductCategories() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM product_categories WHERE status = 1 ORDER BY sort_order, id";
            List<Map<String, Object>> categories = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", categories);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
}
