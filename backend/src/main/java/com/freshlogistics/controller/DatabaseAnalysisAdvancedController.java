package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 高级数据分析控制器 - 提供详细的业务分析数据
 */
@RestController
@RequestMapping("/database/analysis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseAnalysisAdvancedController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取时效分析数据
     */
    @GetMapping("/delivery-efficiency")
    public Map<String, Object> getDeliveryEfficiency() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算平均配送时长（小时）
            String avgDeliveryTimeSql = "SELECT " +
                "AVG(TIMESTAMPDIFF(HOUR, created_at, " +
                "CASE WHEN order_status = 'completed' THEN updated_at ELSE NOW() END)) as avg_delivery_time " +
                "FROM orders WHERE order_status IN ('completed', 'delivered')";
            
            // 计算准时交付率
            String onTimeRateSql = "SELECT " +
                "COUNT(CASE WHEN updated_at <= required_delivery_time THEN 1 END) * 100.0 / COUNT(*) as on_time_rate " +
                "FROM orders WHERE order_status = 'completed'";
            
            // 计算延迟订单数
            String delayedOrdersSql = "SELECT COUNT(*) as delayed_orders " +
                "FROM orders WHERE order_status = 'completed' AND updated_at > required_delivery_time";
            
            Map<String, Object> timeData = new HashMap<>();
            
            try {
                Double avgTime = jdbcTemplate.queryForObject(avgDeliveryTimeSql, Double.class);
                timeData.put("avgDeliveryTime", avgTime != null ? Math.round(avgTime * 10.0) / 10.0 : 0.0);
            } catch (Exception e) {
                timeData.put("avgDeliveryTime", 0.0);
            }
            
            try {
                Double onTimeRate = jdbcTemplate.queryForObject(onTimeRateSql, Double.class);
                timeData.put("onTimeRate", onTimeRate != null ? Math.round(onTimeRate * 10.0) / 10.0 : 0.0);
            } catch (Exception e) {
                timeData.put("onTimeRate", 0.0);
            }
            
            try {
                Long delayedCount = jdbcTemplate.queryForObject(delayedOrdersSql, Long.class);
                timeData.put("delayedOrders", delayedCount != null ? delayedCount : 0);
            } catch (Exception e) {
                timeData.put("delayedOrders", 0);
            }
            
            result.put("code", 200);
            result.put("message", "时效分析成功");
            result.put("data", timeData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "时效分析失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取损耗分析数据
     */
    @GetMapping("/loss-analysis")
    public Map<String, Object> getLossAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算温控失效次数（基于预警记录）
            String tempFailureSql = "SELECT COUNT(*) as temp_failure_count " +
                "FROM alert_records WHERE alert_type = 'temperature' AND alert_level IN ('error', 'critical')";
            
            // 计算损失金额（基于取消订单）
            String lossAmountSql = "SELECT COALESCE(SUM(total_amount), 0) as total_loss_amount " +
                "FROM orders WHERE order_status = 'cancelled'";
            
            // 计算总体损耗率
            String lossRateSql = "SELECT " +
                "COUNT(CASE WHEN order_status = 'cancelled' THEN 1 END) * 100.0 / COUNT(*) as loss_rate " +
                "FROM orders";
            
            Map<String, Object> lossData = new HashMap<>();
            
            Long tempFailures = jdbcTemplate.queryForObject(tempFailureSql, Long.class);
            lossData.put("tempFailureCount", tempFailures != null ? tempFailures : 0);
            
            Double lossAmount = jdbcTemplate.queryForObject(lossAmountSql, Double.class);
            lossData.put("totalLossAmount", lossAmount != null ? Math.round(lossAmount / 10000.0 * 10.0) / 10.0 : 0.0);
            
            Double lossRate = jdbcTemplate.queryForObject(lossRateSql, Double.class);
            lossData.put("totalLossRate", lossRate != null ? Math.round(lossRate * 10.0) / 10.0 : 0.0);
            
            result.put("code", 200);
            result.put("message", "损耗分析成功");
            result.put("data", lossData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "损耗分析失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取预警分析数据
     */
    @GetMapping("/alert-analysis")
    public Map<String, Object> getAlertAnalysis() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 总预警数
            String totalAlertsSql = "SELECT COUNT(*) as total_alerts FROM alert_records";
            
            // 平均响应时间（小时转秒）
            String avgResponseSql = "SELECT " +
                "AVG(TIMESTAMPDIFF(SECOND, created_at, COALESCE(process_time, NOW()))) as avg_response_time " +
                "FROM alert_records WHERE process_time IS NOT NULL";
            
            // 处理率
            String processedRateSql = "SELECT " +
                "COUNT(CASE WHEN alert_status = 'processed' THEN 1 END) * 100.0 / COUNT(*) as processed_rate " +
                "FROM alert_records";
            
            // 误报率（假设自动处理的是误报）
            String falseAlarmRateSql = "SELECT " +
                "COUNT(CASE WHEN auto_processed = 1 THEN 1 END) * 100.0 / COUNT(*) as false_alarm_rate " +
                "FROM alert_records";
            
            Map<String, Object> alertData = new HashMap<>();
            
            Long totalAlerts = jdbcTemplate.queryForObject(totalAlertsSql, Long.class);
            alertData.put("totalAlerts", totalAlerts != null ? totalAlerts : 0);
            
            Double avgResponse = jdbcTemplate.queryForObject(avgResponseSql, Double.class);
            alertData.put("avgResponseTime", avgResponse != null ? Math.round(avgResponse * 10.0) / 10.0 : 0.0);
            
            Double processedRate = jdbcTemplate.queryForObject(processedRateSql, Double.class);
            alertData.put("processedRate", processedRate != null ? Math.round(processedRate * 10.0) / 10.0 : 0.0);
            
            Double falseAlarmRate = jdbcTemplate.queryForObject(falseAlarmRateSql, Double.class);
            alertData.put("falseAlarmRate", falseAlarmRate != null ? Math.round(falseAlarmRate * 10.0) / 10.0 : 0.0);
            
            result.put("code", 200);
            result.put("message", "预警分析成功");
            result.put("data", alertData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "预警分析失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取预警趋势数据（用于图表）
     */
    @GetMapping("/alert-trend-chart")
    public Map<String, Object> getAlertTrendChart() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                "DATE(created_at) as date, " +
                "alert_type, " +
                "COUNT(*) as count " +
                "FROM alert_records " +
                "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(created_at), alert_type " +
                "ORDER BY date, alert_type";
            
            List<Map<String, Object>> trendData = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "预警趋势查询成功");
            result.put("data", trendData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "预警趋势查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取预警类型分布数据（用于饼图）
     */
    @GetMapping("/alert-type-distribution")
    public Map<String, Object> getAlertTypeDistribution() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                "alert_type, " +
                "COUNT(*) as count " +
                "FROM alert_records " +
                "GROUP BY alert_type " +
                "ORDER BY count DESC";
            
            List<Map<String, Object>> distribution = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "预警类型分布查询成功");
            result.put("data", distribution);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "预警类型分布查询失败: " + e.getMessage());
        }
        
        return result;
    }
}
