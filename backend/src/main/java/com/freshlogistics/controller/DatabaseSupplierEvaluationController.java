package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库供应商评价控制器
 */
@RestController
@RequestMapping("/database/supplier")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseSupplierEvaluationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取供应商评价列表
     */
    @GetMapping("/evaluations")
    public Map<String, Object> getSupplierEvaluations() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "se.*, " +
                        "s.supplier_name, " +
                        "s.contact_person, " +
                        "o.order_code " +
                        "FROM supplier_evaluations se " +
                        "LEFT JOIN suppliers s ON se.supplier_id = s.id " +
                        "LEFT JOIN orders o ON se.order_id = o.id " +
                        "WHERE se.deleted_at IS NULL " +
                        "ORDER BY se.created_at DESC";
            
            List<Map<String, Object>> evaluations = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", evaluations);
            
            System.out.println("✅ 成功获取 " + evaluations.size() + " 个评价记录");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询评价数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取评价统计信息
     */
    @GetMapping("/evaluation-statistics")
    public Map<String, Object> getEvaluationStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_evaluations, " +
                        "AVG(overall_rating) as avg_rating, " +
                        "COUNT(CASE WHEN overall_rating >= 4.5 THEN 1 END) as excellent_count, " +
                        "COUNT(CASE WHEN overall_rating >= 3.5 THEN 1 END) * 100.0 / COUNT(*) as satisfaction_rate " +
                        "FROM supplier_evaluations " +
                        "WHERE deleted_at IS NULL";
            
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
     * 创建供应商评价
     */
    @PostMapping("/evaluations")
    public Map<String, Object> createSupplierEvaluation(@RequestBody Map<String, Object> evaluationData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "INSERT INTO supplier_evaluations " +
                        "(supplier_id, order_code, service_rating, quality_rating, delivery_rating, " +
                        "overall_rating, evaluation_content, suggestions, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            
            int inserted = jdbcTemplate.update(sql,
                evaluationData.get("supplierId"),
                evaluationData.get("orderCode"),
                evaluationData.get("serviceRating"),
                evaluationData.get("qualityRating"),
                evaluationData.get("deliveryRating"),
                evaluationData.get("overallRating"),
                evaluationData.get("content"),
                evaluationData.get("suggestions")
            );
            
            if (inserted > 0) {
                result.put("code", 200);
                result.put("message", "评价创建成功");
                result.put("data", evaluationData);
                
                System.out.println("✅ 供应商评价已创建");
            } else {
                result.put("code", 500);
                result.put("message", "创建失败");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            System.err.println("🔴 创建评价失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 更新供应商评价
     */
    @PutMapping("/evaluations/{id}")
    public Map<String, Object> updateSupplierEvaluation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> evaluationData) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "UPDATE supplier_evaluations SET " +
                        "service_rating = ?, quality_rating = ?, delivery_rating = ?, " +
                        "overall_rating = ?, evaluation_content = ?, suggestions = ?, " +
                        "updated_at = NOW() " +
                        "WHERE id = ? AND deleted_at IS NULL";
            
            int updated = jdbcTemplate.update(sql,
                evaluationData.get("serviceRating"),
                evaluationData.get("qualityRating"),
                evaluationData.get("deliveryRating"),
                evaluationData.get("overallRating"),
                evaluationData.get("content"),
                evaluationData.get("suggestions"),
                id
            );
            
            if (updated > 0) {
                result.put("code", 200);
                result.put("message", "评价更新成功");
                result.put("data", evaluationData);
                
                System.out.println("✅ 供应商评价已更新: ID=" + id);
            } else {
                result.put("code", 404);
                result.put("message", "评价不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除供应商评价
     */
    @DeleteMapping("/evaluations/{id}")
    public Map<String, Object> deleteSupplierEvaluation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "UPDATE supplier_evaluations SET deleted_at = NOW() WHERE id = ?";
            int updated = jdbcTemplate.update(sql, id);
            
            if (updated > 0) {
                result.put("code", 200);
                result.put("message", "评价删除成功");
                
                System.out.println("✅ 供应商评价已删除: ID=" + id);
            } else {
                result.put("code", 404);
                result.put("message", "评价不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取供应商详细评价报告
     */
    @GetMapping("/evaluations/report/{supplierId}")
    public Map<String, Object> getSupplierEvaluationReport(@PathVariable Long supplierId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_evaluations, " +
                        "AVG(service_rating) as avg_service, " +
                        "AVG(quality_rating) as avg_quality, " +
                        "AVG(delivery_rating) as avg_delivery, " +
                        "AVG(overall_rating) as avg_overall, " +
                        "MAX(overall_rating) as max_rating, " +
                        "MIN(overall_rating) as min_rating " +
                        "FROM supplier_evaluations " +
                        "WHERE supplier_id = ? AND deleted_at IS NULL";
            
            Map<String, Object> report = jdbcTemplate.queryForMap(sql, supplierId);
            
            result.put("code", 200);
            result.put("message", "报告生成成功");
            result.put("data", report);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "报告生成失败: " + e.getMessage());
        }
        
        return result;
    }
}
