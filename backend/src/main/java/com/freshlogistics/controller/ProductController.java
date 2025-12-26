package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品管理控制器
 */
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    @GetMapping
    public Map<String, Object> getProducts(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productType) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 模拟产品数据
        List<Map<String, Object>> products = new ArrayList<>();
        products.add(Map.of(
            "id", 1,
            "productCode", "PRD001",
            "productName", "新鲜橙子",
            "productType", "fruit",
            "unit", "公斤",
            "minTemp", 2.0,
            "maxTemp", 8.0,
            "status", 1
        ));
        products.add(Map.of(
            "id", 2,
            "productCode", "PRD002", 
            "productName", "有机菠菜",
            "productType", "vegetable",
            "unit", "公斤",
            "minTemp", 0.0,
            "maxTemp", 4.0,
            "status", 1
        ));
        
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
            "records", products,
            "total", products.size(),
            "current", page,
            "size", size
        ));
        
        return result;
    }

    @PostMapping
    public Map<String, Object> createProduct(@RequestBody Map<String, Object> product) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟创建产品
            product.put("id", System.currentTimeMillis());
            product.put("status", 1);
            product.put("createdAt", java.time.LocalDateTime.now().toString());
            
            result.put("code", 200);
            result.put("message", "产品创建成功");
            result.put("data", product);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getProduct(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟获取产品详情
        Map<String, Object> product = Map.of(
            "id", id,
            "productCode", "PRD00" + id,
            "productName", "测试产品" + id,
            "productType", "fruit",
            "unit", "公斤",
            "minTemp", 2.0,
            "maxTemp", 8.0,
            "status", 1
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", product);
        
        return result;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getProductStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> statistics = Map.of(
            "totalCount", 8,
            "enabledCount", 5,
            "temperatureControlCount", 3,
            "typeStatistics", List.of(
                Map.of("type", "fruit", "count", 3),
                Map.of("type", "vegetable", "count", 2),
                Map.of("type", "meat", "count", 2),
                Map.of("type", "seafood", "count", 1)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", statistics);
        
        return result;
    }
}
