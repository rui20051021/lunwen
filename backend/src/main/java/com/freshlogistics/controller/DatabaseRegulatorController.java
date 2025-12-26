package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库监管员数据控制器
 */
@RestController
@RequestMapping("/database/regulator")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseRegulatorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取合规检查统计
     */
    @GetMapping("/compliance-statistics")
    public Map<String, Object> getComplianceStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_checks, " +
                        "COUNT(CASE WHEN violations_found = 0 THEN 1 END) as passed_checks, " +
                        "COUNT(CASE WHEN violations_found > 0 THEN 1 END) as failed_checks, " +
                        "ROUND((COUNT(CASE WHEN violations_found = 0 THEN 1 END) * 100.0 / NULLIF(COUNT(*), 0)), 1) as compliance_rate, " +
                        "AVG(compliance_score) as avg_score " +
                        "FROM compliance_checks";
            
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
     * 获取合规检查记录
     */
    @GetMapping("/compliance-checks")
    public Map<String, Object> getComplianceChecks() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM compliance_checks ORDER BY check_date DESC";
            List<Map<String, Object>> checks = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", checks);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取监管报告列表
     */
    @GetMapping("/reports")
    public Map<String, Object> getRegulatorReports() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM regulator_reports ORDER BY created_at DESC";
            List<Map<String, Object>> reports = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", reports);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取报告统计信息
     */
    @GetMapping("/report-statistics")
    public Map<String, Object> getReportStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_reports, " +
                        "COUNT(CASE WHEN report_status = 'draft' THEN 1 END) as draft_reports, " +
                        "COUNT(CASE WHEN report_status = 'published' THEN 1 END) as published_reports, " +
                        "COUNT(CASE WHEN report_status = 'reviewing' THEN 1 END) as reviewing_reports " +
                        "FROM regulator_reports";
            
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
     * 获取检查计划
     */
    @GetMapping("/check-plans")
    public Map<String, Object> getCheckPlans() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM compliance_checks " +
                        "WHERE check_status = 'planned' " +
                        "ORDER BY check_date ASC";
            
            List<Map<String, Object>> plans = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", plans);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取违规事件（筛选violations_found > 0的检查记录）
     */
    @GetMapping("/violations")
    public Map<String, Object> getViolations() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM compliance_checks " +
                        "WHERE violations_found > 0 " +
                        "ORDER BY check_date DESC";
            
            List<Map<String, Object>> violations = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", violations);
            
            System.out.println("✅ 成功获取 " + violations.size() + " 个违规事件");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询违规数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取违规统计信息
     */
    @GetMapping("/violations/statistics")
    public Map<String, Object> getViolationStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "SUM(violations_found) as total_violations, " +
                        "COUNT(*) as violation_cases, " +
                        "COUNT(CASE WHEN violations_found >= 3 THEN 1 END) as severe_cases, " +
                        "COUNT(CASE WHEN compliance_score >= 90 THEN 1 END) as rectified_count " +
                        "FROM compliance_checks " +
                        "WHERE violations_found > 0";
            
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
     * 创建检查计划
     */
    @PostMapping("/create-check-plan")
    public Map<String, Object> createCheckPlan(@RequestBody Map<String, Object> planRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String checkCode = (String) planRequest.get("checkCode");
            String checkType = (String) planRequest.get("checkType");
            String checkCategory = (String) planRequest.get("checkCategory");
            String targetType = (String) planRequest.get("targetType");
            String companyName = (String) planRequest.get("companyName");
            String checkDate = (String) planRequest.get("checkDate");
            String checkItems = (String) planRequest.get("checkItems");
            String remarks = (String) planRequest.get("remarks");
            
            // 验证必填字段
            if (checkCode == null || checkType == null || companyName == null || checkDate == null) {
                result.put("code", 400);
                result.put("message", "检查编号、类型、对象和日期不能为空");
                return result;
            }
            
            // 检查编号是否已存在
            String checkSql = "SELECT COUNT(*) FROM compliance_checks WHERE check_code = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, checkCode);
            if (count != null && count > 0) {
                result.put("code", 400);
                result.put("message", "检查编号已存在");
                return result;
            }
            
            // 插入检查计划（target_id暂时设为1，regulator_id暂时设为当前监管员ID）
            String insertSql = "INSERT INTO compliance_checks (check_code, check_type, check_category, " +
                             "target_type, target_id, regulator_id, check_date, check_items, " +
                             "check_results, compliance_score, violations_found, check_status, created_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'planned', NOW())";
            
            Long targetId = 1L; // 默认目标ID
            Long regulatorId = 8L; // 默认监管员ID（根据实际情况调整）
            
            jdbcTemplate.update(insertSql, checkCode, checkType, checkCategory, 
                              targetType, targetId, regulatorId, checkDate, checkItems,
                              remarks != null ? remarks : "待执行检查", 0, 0);
            
            // 获取插入的检查ID
            String getIdSql = "SELECT id FROM compliance_checks WHERE check_code = ?";
            Long checkId = jdbcTemplate.queryForObject(getIdSql, Long.class, checkCode);
            
            result.put("code", 200);
            result.put("message", "检查计划创建成功");
            result.put("data", Map.of("id", checkId, "checkCode", checkCode));
            
            System.out.println("✅ 成功创建检查计划: " + checkCode + " - " + companyName);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            System.err.println("🔴 创建检查计划失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 生成监管报告
     */
    @PostMapping("/generate-report")
    public Map<String, Object> generateReport(@RequestBody Map<String, Object> reportRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String reportType = (String) reportRequest.get("reportType");
            String reportTitle = (String) reportRequest.get("reportTitle");
            
            if (reportType == null || reportTitle == null) {
                result.put("code", 400);
                result.put("message", "报告类型和标题不能为空");
                return result;
            }
            
            // 生成报告编号
            String reportCode = "RPT" + System.currentTimeMillis() % 10000000000L;
            
            // 计算报告周期
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate periodStart;
            java.time.LocalDate periodEnd = now;
            
            switch (reportType) {
                case "daily":
                    periodStart = now;
                    break;
                case "weekly":
                    periodStart = now.minusDays(7);
                    break;
                case "monthly":
                    periodStart = now.minusMonths(1);
                    break;
                case "special":
                    periodStart = now.minusDays(30);
                    break;
                default:
                    periodStart = now.minusDays(7);
            }
            
            // 获取报告内容（基于合规检查数据）
            String contentSql = "SELECT COUNT(*) as check_count, " +
                              "COUNT(CASE WHEN violations_found > 0 THEN 1 END) as violation_count, " +
                              "AVG(compliance_score) as avg_score " +
                              "FROM compliance_checks " +
                              "WHERE check_date BETWEEN ? AND ?";
            
            Map<String, Object> content = jdbcTemplate.queryForMap(contentSql, 
                periodStart.toString(), periodEnd.toString());
            
            // 构建报告摘要
            String summary = String.format(
                "检查总数: %d, 发现违规: %d, 平均分: %.1f",
                content.get("check_count"),
                content.get("violation_count"),
                content.get("avg_score") != null ? content.get("avg_score") : 0.0
            );
            
            // 插入报告记录（添加regulator_id字段）
            String insertSql = "INSERT INTO regulator_reports (report_code, report_type, report_title, " +
                             "report_period_start, report_period_end, regulator_id, summary, report_content, " +
                             "report_status, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'published', NOW(), NOW())";
            
            Long regulatorId = 8L; // 默认监管员ID
            
            jdbcTemplate.update(insertSql, reportCode, reportType, reportTitle,
                              periodStart.toString(), periodEnd.toString(), regulatorId, summary, summary);
            
            result.put("code", 200);
            result.put("message", "报告生成成功");
            result.put("data", Map.of("reportCode", reportCode, "reportTitle", reportTitle));
            
            System.out.println("✅ 成功生成" + reportType + "报告: " + reportTitle);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成报告失败: " + e.getMessage());
            System.err.println("🔴 生成报告失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取检查详情
     */
    @GetMapping("/check-detail/{id}")
    public Map<String, Object> getCheckDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM compliance_checks WHERE id = ?";
            List<Map<String, Object>> checkList = jdbcTemplate.queryForList(sql, id);
            
            if (checkList.isEmpty()) {
                result.put("code", 404);
                result.put("message", "检查记录不存在");
                return result;
            }
            
            Map<String, Object> check = checkList.get(0);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", check);
            
            System.out.println("✅ 成功获取检查详情 ID: " + id);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 获取检查详情失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 更新检查记录
     */
    @PostMapping("/update-check/{id}")
    public Map<String, Object> updateCheck(@PathVariable Long id, @RequestBody Map<String, Object> updateRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String checkResults = (String) updateRequest.get("checkResults");
            Object complianceScoreObj = updateRequest.get("complianceScore");
            Object violationsFoundObj = updateRequest.get("violationsFound");
            String violationDetails = (String) updateRequest.get("violationDetails");
            String correctiveActions = (String) updateRequest.get("correctiveActions");
            String checkStatus = (String) updateRequest.get("checkStatus");
            
            // 转换数值类型
            Double complianceScore = complianceScoreObj != null ?
                (complianceScoreObj instanceof Integer ? ((Integer) complianceScoreObj).doubleValue() : (Double) complianceScoreObj) : 0.0;
            
            Integer violationsFound = violationsFoundObj != null ?
                (violationsFoundObj instanceof Integer ? (Integer) violationsFoundObj : ((Double) violationsFoundObj).intValue()) : 0;
            
            // 更新检查记录
            String updateSql = "UPDATE compliance_checks SET " +
                             "check_results = ?, " +
                             "compliance_score = ?, " +
                             "violations_found = ?, " +
                             "violation_details = ?, " +
                             "corrective_actions = ?, " +
                             "check_status = ?, " +
                             "updated_at = NOW() " +
                             "WHERE id = ?";
            
            int rows = jdbcTemplate.update(updateSql, checkResults, complianceScore, violationsFound,
                                         violationDetails, correctiveActions, checkStatus, id);
            
            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "更新成功");
                System.out.println("✅ 成功更新检查记录 ID: " + id);
            } else {
                result.put("code", 404);
                result.put("message", "检查记录不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
            System.err.println("🔴 更新检查记录失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 生成检查报告
     */
    @PostMapping("/generate-check-report")
    public Map<String, Object> generateCheckReport(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Object checkIdObj = request.get("checkId");
            String checkCode = (String) request.get("checkCode");
            
            if (checkIdObj == null) {
                result.put("code", 400);
                result.put("message", "检查ID不能为空");
                return result;
            }
            
            Long checkId = checkIdObj instanceof Integer ? 
                ((Integer) checkIdObj).longValue() : (Long) checkIdObj;
            
            // 获取检查记录详情
            String sql = "SELECT * FROM compliance_checks WHERE id = ?";
            List<Map<String, Object>> checkList = jdbcTemplate.queryForList(sql, checkId);
            
            if (checkList.isEmpty()) {
                result.put("code", 404);
                result.put("message", "检查记录不存在");
                return result;
            }
            
            Map<String, Object> check = checkList.get(0);
            
            // 生成报告内容
            StringBuilder reportContent = new StringBuilder();
            reportContent.append("检查编号: ").append(check.get("check_code")).append("\n");
            reportContent.append("检查类型: ").append(check.get("check_type")).append("\n");
            reportContent.append("检查日期: ").append(check.get("check_date")).append("\n");
            reportContent.append("检查项目: ").append(check.get("check_items")).append("\n");
            reportContent.append("检查结果: ").append(check.get("check_results")).append("\n");
            reportContent.append("合规评分: ").append(check.get("compliance_score")).append("分\n");
            reportContent.append("发现违规: ").append(check.get("violations_found")).append("项\n");
            
            if (check.get("violation_details") != null) {
                reportContent.append("违规详情: ").append(check.get("violation_details")).append("\n");
            }
            
            if (check.get("corrective_actions") != null) {
                reportContent.append("整改措施: ").append(check.get("corrective_actions")).append("\n");
            }
            
            result.put("code", 200);
            result.put("message", "报告生成成功");
            result.put("data", Map.of(
                "checkCode", check.get("check_code"),
                "reportContent", reportContent.toString()
            ));
            
            System.out.println("✅ 成功生成检查报告: " + checkCode);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成报告失败: " + e.getMessage());
            System.err.println("🔴 生成检查报告失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取报告详情
     */
    @GetMapping("/report-detail/{id}")
    public Map<String, Object> getReportDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM regulator_reports WHERE id = ?";
            List<Map<String, Object>> reportList = jdbcTemplate.queryForList(sql, id);
            
            if (reportList.isEmpty()) {
                result.put("code", 404);
                result.put("message", "报告不存在");
                return result;
            }
            
            Map<String, Object> report = reportList.get(0);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", report);
            
            System.out.println("✅ 成功获取报告详情 ID: " + id);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 获取报告详情失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 下达整改要求
     */
    @PostMapping("/demand-rectification/{id}")
    public Map<String, Object> demandRectification(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String rectificationRequirements = (String) request.get("rectificationRequirements");
            
            if (rectificationRequirements == null || rectificationRequirements.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "整改要求不能为空");
                return result;
            }
            
            // 更新检查记录，添加整改要求
            String updateSql = "UPDATE compliance_checks SET " +
                             "corrective_actions = ?, " +
                             "follow_up_required = 1, " +
                             "check_status = 'in_progress', " +
                             "updated_at = NOW() " +
                             "WHERE id = ?";
            
            int rows = jdbcTemplate.update(updateSql, rectificationRequirements, id);
            
            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "整改通知已发送");
                System.out.println("✅ 成功下达整改要求 ID: " + id);
            } else {
                result.put("code", 404);
                result.put("message", "检查记录不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败: " + e.getMessage());
            System.err.println("🔴 下达整改要求失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 验收整改
     */
    @PostMapping("/verify-rectification/{id}")
    public Map<String, Object> verifyRectification(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Boolean verified = (Boolean) request.get("verified");
            Object scoreObj = request.get("complianceScore");
            
            Double complianceScore = scoreObj != null ?
                (scoreObj instanceof Integer ? ((Integer) scoreObj).doubleValue() : (Double) scoreObj) : 95.0;
            
            // 更新检查记录，标记整改完成
            String updateSql = "UPDATE compliance_checks SET " +
                             "compliance_score = ?, " +
                             "check_status = 'completed', " +
                             "violations_found = 0, " +
                             "updated_at = NOW() " +
                             "WHERE id = ?";
            
            int rows = jdbcTemplate.update(updateSql, complianceScore, id);
            
            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "整改验收通过");
                System.out.println("✅ 整改验收通过 ID: " + id);
            } else {
                result.put("code", 404);
                result.put("message", "检查记录不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败: " + e.getMessage());
            System.err.println("🔴 验收整改失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
