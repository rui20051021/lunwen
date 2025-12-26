package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库预警数据控制器 - 直接使用JdbcTemplate连接真实数据库
 */
@RestController
@RequestMapping("/database/alert")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseAlertController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有预警记录
     */
    @GetMapping("/records")
    public Map<String, Object> getAlertRecords(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 📊 查询预警记录
            String sql = "SELECT * FROM alert_records ORDER BY created_at DESC LIMIT ? OFFSET ?";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, size, offset);
            
            // 查询总数
            Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alert_records", Long.class);
            
            // 构造分页结果
            Map<String, Object> data = new HashMap<>();
            data.put("records", records);
            data.put("total", total);
            data.put("size", size);
            data.put("current", page);
            
            result.put("code", 200);
            result.put("message", "查询成功 - 真实数据库数据");
            result.put("data", data);
            
            System.out.println("✅ 成功获取 " + records.size() + " 条预警记录");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询预警记录失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取预警统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_alerts, " +
                        "COUNT(CASE WHEN alert_level = 'critical' THEN 1 END) as critical_count, " +
                        "COUNT(CASE WHEN alert_level = 'error' THEN 1 END) as error_count, " +
                        "COUNT(CASE WHEN alert_level = 'warning' THEN 1 END) as warning_count, " +
                        "COUNT(CASE WHEN alert_level = 'info' THEN 1 END) as info_count, " +
                        "COUNT(CASE WHEN alert_status = 'pending' THEN 1 END) as pending_count, " +
                        "COUNT(CASE WHEN alert_status = 'processed' THEN 1 END) as processed_count " +
                        "FROM alert_records";
            
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
     * 根据状态获取预警记录
     */
    @GetMapping("/by-status/{status}")
    public Map<String, Object> getAlertsByStatus(@PathVariable String status) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM alert_records WHERE alert_status = ? ORDER BY created_at DESC";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, status);
            
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
     * 获取所有预警规则
     */
    @GetMapping("/rules")
    public Map<String, Object> getAlertRules(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 📊 查询预警规则
            String sql = "SELECT * FROM alert_rules WHERE is_enabled = 1 ORDER BY created_at DESC LIMIT ? OFFSET ?";
            List<Map<String, Object>> rules = jdbcTemplate.queryForList(sql, size, offset);
            
            // 查询总数
            Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alert_rules WHERE is_enabled = 1", Long.class);
            
            // 构造分页结果
            Map<String, Object> data = new HashMap<>();
            data.put("records", rules);
            data.put("total", total);
            data.put("size", size);
            data.put("current", page);
            
            result.put("code", 200);
            result.put("message", "查询成功 - 真实数据库数据");
            result.put("data", data);
            
            System.out.println("✅ 成功获取 " + rules.size() + " 条预警规则");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询预警规则失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取预警规则统计信息
     */
    @GetMapping("/rules/statistics")
    public Map<String, Object> getAlertRuleStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_rules, " +
                        "COUNT(CASE WHEN is_enabled = 1 THEN 1 END) as enabled_rules, " +
                        "COUNT(CASE WHEN is_enabled = 0 THEN 1 END) as disabled_rules, " +
                        "COUNT(CASE WHEN rule_type = 'temperature' THEN 1 END) as temperature_rules, " +
                        "COUNT(CASE WHEN rule_type = 'timeout' THEN 1 END) as timeout_rules, " +
                        "COUNT(CASE WHEN rule_type = 'humidity' THEN 1 END) as humidity_rules " +
                        "FROM alert_rules";
            
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
     * 切换预警规则状态
     */
    @PatchMapping("/rules/{id}/toggle")
    public Map<String, Object> toggleAlertRule(
            @PathVariable Long id,
            @RequestParam int isEnabled) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "UPDATE alert_rules SET is_enabled = ?, updated_at = NOW() WHERE id = ?";
            int updated = jdbcTemplate.update(sql, isEnabled, id);
            
            if (updated > 0) {
                result.put("code", 200);
                result.put("message", "预警规则状态更新成功");
                result.put("data", Map.of("id", id, "isEnabled", isEnabled));
                
                System.out.println("✅ 预警规则状态已更新: ID=" + id + ", enabled=" + isEnabled);
            } else {
                result.put("code", 404);
                result.put("message", "预警规则不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
            System.err.println("🔴 更新预警规则状态失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除预警规则
     */
    @DeleteMapping("/rules/{id}")
    public Map<String, Object> deleteAlertRule(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "UPDATE alert_rules SET deleted_at = NOW() WHERE id = ?";
            int updated = jdbcTemplate.update(sql, id);
            
            if (updated > 0) {
                result.put("code", 200);
                result.put("message", "预警规则删除成功");
                
                System.out.println("✅ 预警规则已删除: ID=" + id);
            } else {
                result.put("code", 404);
                result.put("message", "预警规则不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建新的预警规则
     */
    @PostMapping("/create-rule")
    public Map<String, Object> createAlertRule(@RequestBody Map<String, Object> ruleRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String ruleCode = (String) ruleRequest.get("ruleCode");
            String ruleName = (String) ruleRequest.get("ruleName");
            String ruleType = (String) ruleRequest.get("ruleType");
            String alertLevel = (String) ruleRequest.get("alertLevel");
            Object thresholdValueObj = ruleRequest.get("thresholdValue");
            String description = (String) ruleRequest.get("description");
            Object isEnabledObj = ruleRequest.get("isEnabled");
            
            // 验证必填字段
            if (ruleCode == null || ruleName == null || ruleType == null || alertLevel == null) {
                result.put("code", 400);
                result.put("message", "规则编码、名称、类型和级别不能为空");
                return result;
            }
            
            // 转换阈值
            Double thresholdValue = 0.0;
            if (thresholdValueObj != null) {
                if (thresholdValueObj instanceof Integer) {
                    thresholdValue = ((Integer) thresholdValueObj).doubleValue();
                } else if (thresholdValueObj instanceof Double) {
                    thresholdValue = (Double) thresholdValueObj;
                }
            }
            
            // 转换启用状态
            Integer isEnabled = 1;
            if (isEnabledObj != null) {
                if (isEnabledObj instanceof Integer) {
                    isEnabled = (Integer) isEnabledObj;
                } else if (isEnabledObj instanceof Boolean) {
                    isEnabled = ((Boolean) isEnabledObj) ? 1 : 0;
                }
            }
            
            // 检查规则编码是否已存在
            String checkSql = "SELECT COUNT(*) FROM alert_rules WHERE rule_code = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ruleCode);
            if (count != null && count > 0) {
                result.put("code", 400);
                result.put("message", "规则编码已存在");
                return result;
            }
            
            // 插入规则数据
            String insertSql = "INSERT INTO alert_rules (rule_code, rule_name, rule_type, alert_level, " +
                             "threshold_value, rule_condition, description, is_enabled, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            
            String ruleCondition = String.format("%s > %s", ruleType, thresholdValue);
            
            jdbcTemplate.update(insertSql, ruleCode, ruleName, ruleType, alertLevel, 
                              thresholdValue, ruleCondition, description, isEnabled);
            
            // 获取插入的规则ID
            String getIdSql = "SELECT id FROM alert_rules WHERE rule_code = ?";
            Long ruleId = jdbcTemplate.queryForObject(getIdSql, Long.class, ruleCode);
            
            result.put("code", 200);
            result.put("message", "规则创建成功");
            result.put("data", Map.of("id", ruleId, "ruleCode", ruleCode));
            
            System.out.println("✅ 成功创建预警规则: " + ruleCode);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            System.err.println("🔴 创建预警规则失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 批量导入预警规则
     */
    @PostMapping("/import-rules")
    public Map<String, Object> importAlertRules(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rules = (List<Map<String, Object>>) request.get("rules");
            
            if (rules == null || rules.isEmpty()) {
                result.put("code", 400);
                result.put("message", "导入数据为空");
                return result;
            }
            
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMessages = new StringBuilder();
            
            for (Map<String, Object> rule : rules) {
                try {
                    String ruleCode = (String) rule.get("ruleCode");
                    String ruleName = (String) rule.get("ruleName");
                    String ruleType = (String) rule.get("ruleType");
                    String alertLevel = (String) rule.get("alertLevel");
                    Object thresholdValueObj = rule.get("thresholdValue");
                    String description = (String) rule.get("description");
                    Object isEnabledObj = rule.get("isEnabled");
                    
                    // 转换阈值
                    Double thresholdValue = 0.0;
                    if (thresholdValueObj != null) {
                        if (thresholdValueObj instanceof Integer) {
                            thresholdValue = ((Integer) thresholdValueObj).doubleValue();
                        } else if (thresholdValueObj instanceof Double) {
                            thresholdValue = (Double) thresholdValueObj;
                        } else if (thresholdValueObj instanceof String) {
                            thresholdValue = Double.parseDouble((String) thresholdValueObj);
                        }
                    }
                    
                    // 转换启用状态
                    Integer isEnabled = 1;
                    if (isEnabledObj != null) {
                        if (isEnabledObj instanceof Integer) {
                            isEnabled = (Integer) isEnabledObj;
                        } else if (isEnabledObj instanceof String) {
                            isEnabled = Integer.parseInt((String) isEnabledObj);
                        }
                    }
                    
                    // 检查规则编码是否已存在
                    String checkSql = "SELECT COUNT(*) FROM alert_rules WHERE rule_code = ?";
                    Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ruleCode);
                    
                    if (count != null && count > 0) {
                        // 更新现有规则
                        String updateSql = "UPDATE alert_rules SET rule_name = ?, rule_type = ?, " +
                                         "alert_level = ?, threshold_value = ?, description = ?, " +
                                         "is_enabled = ?, updated_at = NOW() WHERE rule_code = ?";
                        
                        jdbcTemplate.update(updateSql, ruleName, ruleType, alertLevel, 
                                          thresholdValue, description, isEnabled, ruleCode);
                    } else {
                        // 插入新规则
                        String insertSql = "INSERT INTO alert_rules (rule_code, rule_name, rule_type, " +
                                         "alert_level, threshold_value, rule_condition, description, " +
                                         "is_enabled, created_at, updated_at) " +
                                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
                        
                        String ruleCondition = String.format("%s > %s", ruleType, thresholdValue);
                        
                        jdbcTemplate.update(insertSql, ruleCode, ruleName, ruleType, alertLevel, 
                                          thresholdValue, ruleCondition, description, isEnabled);
                    }
                    
                    successCount++;
                    
                } catch (Exception e) {
                    failCount++;
                    errorMessages.append(String.format("规则 %s 导入失败: %s; ", 
                        rule.get("ruleCode"), e.getMessage()));
                }
            }
            
            result.put("code", 200);
            result.put("message", String.format("导入完成：成功 %d 条，失败 %d 条", successCount, failCount));
            result.put("data", Map.of("successCount", successCount, "failCount", failCount));
            
            if (failCount > 0) {
                System.err.println("⚠️ 部分规则导入失败: " + errorMessages.toString());
            }
            
            System.out.println(String.format("✅ 规则导入完成：成功 %d 条，失败 %d 条", successCount, failCount));
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "导入失败: " + e.getMessage());
            System.err.println("🔴 批量导入规则失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
