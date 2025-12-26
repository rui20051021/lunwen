package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库结构查看控制器
 */
@RestController
@RequestMapping("/database")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查看所有表
     */
    @GetMapping("/tables")
    public Map<String, Object> showTables() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT table_name, table_rows, table_comment " +
                "FROM information_schema.tables " +
                "WHERE table_schema = 'freshlogistics' " +
                "ORDER BY table_name"
            );
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", tables);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 查看指定表结构
     */
    @GetMapping("/table/{tableName}")
    public Map<String, Object> showTableStructure(@PathVariable String tableName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查看表结构
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name, data_type, is_nullable, column_default, column_key, extra, column_comment " +
                "FROM information_schema.columns " +
                "WHERE table_schema = 'freshlogistics' AND table_name = ? " +
                "ORDER BY ordinal_position",
                tableName
            );
            
            // 查看样例数据
            List<Map<String, Object>> sampleData = jdbcTemplate.queryForList(
                "SELECT * FROM " + tableName + " LIMIT 5"
            );
            
            Map<String, Object> tableInfo = new HashMap<>();
            tableInfo.put("columns", columns);
            tableInfo.put("sampleData", sampleData);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", tableInfo);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/test")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            
            Map<String, Object> info = new HashMap<>();
            info.put("mysql_version", version);
            info.put("current_database", database);
            info.put("connection_status", "SUCCESS");
            
            result.put("code", 200);
            result.put("message", "数据库连接正常");
            result.put("data", info);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据库连接失败: " + e.getMessage());
        }
        
        return result;
    }
}
