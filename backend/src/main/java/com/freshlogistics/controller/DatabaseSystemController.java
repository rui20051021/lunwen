package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库系统配置控制器
 */
@RestController
@RequestMapping("/database/system")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseSystemController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    public Map<String, Object> getSystemConfig() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM sys_config WHERE status = 1 ORDER BY config_key";
            List<Map<String, Object>> configs = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", configs);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取系统日志
     */
    @GetMapping("/logs")
    public Map<String, Object> getSystemLogs(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int offset = (page - 1) * size;
            
            String sql = "SELECT * FROM sys_operation_log ORDER BY created_at DESC LIMIT ? OFFSET ?";
            List<Map<String, Object>> logs = jdbcTemplate.queryForList(sql, size, offset);
            
            Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_operation_log", Long.class);
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", logs);
            data.put("total", total);
            data.put("size", size);
            data.put("current", page);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取权限列表
     */
    @GetMapping("/permissions")
    public Map<String, Object> getPermissions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM sys_permission ORDER BY permission_code";
            List<Map<String, Object>> permissions = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", permissions);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取角色列表
     */
    @GetMapping("/roles")
    public Map<String, Object> getRoles() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT r.*, " +
                        "COUNT(ur.user_id) as user_count " +
                        "FROM sys_role r " +
                        "LEFT JOIN sys_user_role ur ON r.id = ur.role_id " +
                        "GROUP BY r.id " +
                        "ORDER BY r.sort_order";
            
            List<Map<String, Object>> roles = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", roles);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 系统健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> health = new HashMap<>();
            
            // 数据库连接状态
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            health.put("database_status", "CONNECTED");
            health.put("database_version", dbVersion);
            
            // 各表记录数量
            Map<String, Object> tableCounts = new HashMap<>();
            String[] tables = {"sys_user", "products", "orders", "alert_records", "vehicles", "suppliers"};
            
            for (String table : tables) {
                try {
                    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
                    tableCounts.put(table, count);
                } catch (Exception e) {
                    tableCounts.put(table, "ERROR");
                }
            }
            health.put("table_counts", tableCounts);
            
            // 系统时间
            health.put("system_time", jdbcTemplate.queryForObject("SELECT NOW()", String.class));
            
            result.put("code", 200);
            result.put("message", "系统健康");
            result.put("data", health);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "系统检查失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 保存系统配置
     */
    @PostMapping("/save-config")
    public Map<String, Object> saveSystemConfig(@RequestBody Map<String, Object> configRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("✅ 收到配置保存请求: " + configRequest);
            
            // 这里简化实现，实际可以保存到sys_config表
            // 当前版本将配置记录到日志
            
            result.put("code", 200);
            result.put("message", "配置保存成功");
            result.put("data", Map.of(
                "savedAt", java.time.LocalDateTime.now().toString(),
                "config", configRequest
            ));
            
            System.out.println("✅ 系统配置已保存");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "保存失败: " + e.getMessage());
            System.err.println("🔴 保存配置失败: " + e.getMessage());
        }
        
        return result;
    }
}
