package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库管理控制器 - 用于创建表和初始化数据
 */
@RestController
@RequestMapping("/database/management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseManagementController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建供应商评价表
     */
    @PostMapping("/create-evaluation-table")
    public Map<String, Object> createEvaluationTable() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建供应商评价表
            String createTableSql = "CREATE TABLE IF NOT EXISTS supplier_evaluations (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "supplier_id BIGINT NOT NULL, " +
                "order_id BIGINT, " +
                "order_code VARCHAR(50), " +
                "evaluator_id BIGINT, " +
                "evaluator_name VARCHAR(100), " +
                "service_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '服务评分', " +
                "quality_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '质量评分', " +
                "delivery_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '配送评分', " +
                "overall_rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '综合评分', " +
                "evaluation_content TEXT COMMENT '评价内容', " +
                "suggestions TEXT COMMENT '改进建议', " +
                "evaluation_status VARCHAR(20) DEFAULT 'submitted' COMMENT '评价状态', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "deleted_at TIMESTAMP NULL, " +
                "INDEX idx_supplier_id (supplier_id), " +
                "INDEX idx_order_id (order_id), " +
                "INDEX idx_created_at (created_at)" +
                ") COMMENT='供应商评价表'";
            
            jdbcTemplate.execute(createTableSql);
            
            System.out.println("✅ supplier_evaluations 表创建成功");
            
            result.put("code", 200);
            result.put("message", "供应商评价表创建成功");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建表失败: " + e.getMessage());
            System.err.println("🔴 创建评价表失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 插入初始评价数据
     */
    @PostMapping("/init-evaluation-data")
    public Map<String, Object> initEvaluationData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 插入示例评价数据
            String[] insertSqls = {
                "INSERT INTO supplier_evaluations (supplier_id, order_code, evaluator_name, service_rating, quality_rating, delivery_rating, overall_rating, evaluation_content, suggestions) VALUES " +
                "(1, 'ORD20250920001', '采购部张经理', 4.5, 4.8, 4.2, 4.5, '新鲜农场的产品质量很好，配送及时，服务态度良好', '希望能进一步提升包装质量')",
                
                "INSERT INTO supplier_evaluations (supplier_id, order_code, evaluator_name, service_rating, quality_rating, delivery_rating, overall_rating, evaluation_content, suggestions) VALUES " +
                "(2, 'ORD20250921002', '采购部李经理', 4.2, 4.5, 4.0, 4.2, '绿色蔬菜基地的蔬菜新鲜度不错，但配送时间稍有延迟', '建议优化配送时间管理')",
                
                "INSERT INTO supplier_evaluations (supplier_id, order_code, evaluator_name, service_rating, quality_rating, delivery_rating, overall_rating, evaluation_content, suggestions) VALUES " +
                "(3, 'ORD20250922003', '采购部王经理', 4.8, 4.9, 4.6, 4.8, '海鲜直供公司的产品质量优秀，冷链保护到位，非常满意', '继续保持现有服务水平')",
                
                "INSERT INTO supplier_evaluations (supplier_id, order_code, evaluator_name, service_rating, quality_rating, delivery_rating, overall_rating, evaluation_content, suggestions) VALUES " +
                "(1, 'ORD20250927001', '质量部刘主管', 4.0, 4.3, 3.8, 4.0, '产品质量总体良好，但包装有改进空间', '加强产品包装保护')",
                
                "INSERT INTO supplier_evaluations (supplier_id, order_code, evaluator_name, service_rating, quality_rating, delivery_rating, overall_rating, evaluation_content, suggestions) VALUES " +
                "(2, 'ORD20250927002', '采购部周经理', 4.6, 4.4, 4.5, 4.5, '服务响应及时，产品符合要求，配送准时', '无特别建议，继续保持')"
            };
            
            int totalInserted = 0;
            for (String insertSql : insertSqls) {
                try {
                    jdbcTemplate.update(insertSql);
                    totalInserted++;
                } catch (Exception e) {
                    System.err.println("插入数据失败: " + e.getMessage());
                }
            }
            
            System.out.println("✅ 成功插入 " + totalInserted + " 条评价数据");
            
            result.put("code", 200);
            result.put("message", "评价数据初始化成功，插入了" + totalInserted + "条记录");
            result.put("data", Map.of("insertedCount", totalInserted));
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "初始化数据失败: " + e.getMessage());
            System.err.println("🔴 初始化评价数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 验证评价表数据
     */
    @GetMapping("/verify-evaluation-table")
    public Map<String, Object> verifyEvaluationTable() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String countSql = "SELECT COUNT(*) as total FROM supplier_evaluations WHERE deleted_at IS NULL";
            Long count = jdbcTemplate.queryForObject(countSql, Long.class);
            
            String avgRatingSql = "SELECT AVG(overall_rating) as avg_rating FROM supplier_evaluations WHERE deleted_at IS NULL";
            Double avgRating = jdbcTemplate.queryForObject(avgRatingSql, Double.class);
            
            Map<String, Object> verification = new HashMap<>();
            verification.put("table_exists", true);
            verification.put("total_records", count != null ? count : 0);
            verification.put("avg_rating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
            
            result.put("code", 200);
            result.put("message", "评价表验证成功");
            result.put("data", verification);
            
            System.out.println("✅ 评价表验证: " + count + " 条记录，平均评分: " + avgRating);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "评价表验证失败: " + e.getMessage());
            System.err.println("🔴 评价表验证失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 初始化合规检查数据（使用基本字段）
     */
    @PostMapping("/init-compliance-data")
    public Map<String, Object> initComplianceData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用基本字段插入合规检查数据
            String[] insertSqls = {
                "INSERT INTO compliance_checks (check_category, target_name, check_date, compliance_status, compliance_score, violations_found, created_at) VALUES " +
                "('facility', '新鲜农场有限公司', '2025-09-25', 'passed', 95.0, 0, NOW())",
                
                "INSERT INTO compliance_checks (check_category, target_name, check_date, compliance_status, compliance_score, violations_found, created_at) VALUES " +
                "('vehicle', '京A12345', '2025-09-26', 'passed', 92.0, 1, NOW())",
                
                "INSERT INTO compliance_checks (check_category, target_name, check_date, compliance_status, compliance_score, violations_found, created_at) VALUES " +
                "('process', '绿色蔬菜基地', '2025-09-27', 'passed', 98.0, 0, NOW())",
                
                "INSERT INTO compliance_checks (check_category, target_name, check_date, compliance_status, compliance_score, violations_found, created_at) VALUES " +
                "('transport', '海鲜运输专线', '2025-09-28', 'failed', 78.0, 3, NOW())",
                
                "INSERT INTO compliance_checks (check_category, target_name, check_date, compliance_status, compliance_score, violations_found, created_at) VALUES " +
                "('facility', '中央物流中心', '2025-09-29', 'passed', 96.0, 0, NOW())"
            };
            
            int totalInserted = 0;
            for (String insertSql : insertSqls) {
                try {
                    jdbcTemplate.update(insertSql);
                    totalInserted++;
                    System.out.println("✅ 插入合规检查记录成功");
                } catch (Exception e) {
                    System.err.println("插入合规检查数据失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("✅ 成功插入 " + totalInserted + " 条合规检查数据");
            
            result.put("code", 200);
            result.put("message", "合规检查数据初始化成功，插入了" + totalInserted + "条记录");
            result.put("data", Map.of("insertedCount", totalInserted));
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "初始化合规数据失败: " + e.getMessage());
            System.err.println("🔴 初始化合规数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 重新创建并初始化监管报告表
     */
    @PostMapping("/recreate-reports-table")
    public Map<String, Object> recreateReportsTable() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 先删除旧表（如果存在）
            try {
                jdbcTemplate.execute("DROP TABLE IF EXISTS regulator_reports_new");
            } catch (Exception e) {
                // 忽略删除错误
            }
            
            // 创建新的监管报告表
            String createTableSql = 
                "CREATE TABLE regulator_reports_new (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "report_code VARCHAR(50) DEFAULT CONCAT('RPT', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(FLOOR(RAND()*1000), 3, '0')), " +
                "report_title VARCHAR(200) NOT NULL, " +
                "report_type VARCHAR(50) DEFAULT 'weekly', " +
                "report_period_start DATE DEFAULT (CURRENT_DATE - INTERVAL 7 DAY), " +
                "report_period_end DATE DEFAULT (CURRENT_DATE), " +
                "report_content TEXT, " +
                "key_findings TEXT, " +
                "recommendations TEXT, " +
                "report_status VARCHAR(20) DEFAULT 'draft', " +
                "published_at TIMESTAMP NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
            
            jdbcTemplate.execute(createTableSql);
            System.out.println("✅ 新监管报告表创建成功");
            
            // 插入示例数据
            String[] insertSqls = {
                "INSERT INTO regulator_reports_new (report_title, report_type, report_status, report_content, key_findings, recommendations) VALUES " +
                "('第38周冷链物流监管报告', 'weekly', 'published', '本周共检查5家企业，整体合规情况良好', '95%的企业达到合规要求', '建议加强新企业培训')",
                
                "INSERT INTO regulator_reports_new (report_title, report_type, report_status, report_content, key_findings, recommendations) VALUES " +
                "('9月份冷链物流监管月报', 'monthly', 'reviewing', '9月份整体运行平稳，合规检查覆盖率100%', '发现并整改温控问题12处', '建议建立长效监管机制')",
                
                "INSERT INTO regulator_reports_new (report_title, report_type, report_status, report_content, key_findings, recommendations) VALUES " +
                "('9月28日合规检查日报', 'daily', 'draft', '今日完成3家企业例行检查', '发现1处违规需要整改', '要求立即整改并加强监控')",
                
                "INSERT INTO regulator_reports_new (report_title, report_type, report_status, report_content, key_findings, recommendations) VALUES " +
                "('国庆节前安全检查专报', 'special', 'published', '节前专项安全检查完成', '所有单位制定应急预案', '建议节日期间加强值班')"
            };
            
            int totalInserted = 0;
            for (String insertSql : insertSqls) {
                try {
                    jdbcTemplate.update(insertSql);
                    totalInserted++;
                } catch (Exception e) {
                    System.err.println("插入监管报告数据失败: " + e.getMessage());
                }
            }
            
            // 替换旧表
            try {
                jdbcTemplate.execute("DROP TABLE IF EXISTS regulator_reports_old");
                jdbcTemplate.execute("RENAME TABLE regulator_reports TO regulator_reports_old");
                jdbcTemplate.execute("RENAME TABLE regulator_reports_new TO regulator_reports");
                System.out.println("✅ 报告表替换成功");
            } catch (Exception e) {
                System.err.println("报告表替换失败: " + e.getMessage());
            }
            
            System.out.println("✅ 成功插入 " + totalInserted + " 条监管报告数据");
            
            result.put("code", 200);
            result.put("message", "监管报告表重建成功，插入了" + totalInserted + "条记录");
            result.put("data", Map.of("insertedCount", totalInserted));
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "重建报告表失败: " + e.getMessage());
            System.err.println("🔴 重建报告表失败: " + e.getMessage());
        }
        
        return result;
    }
}
