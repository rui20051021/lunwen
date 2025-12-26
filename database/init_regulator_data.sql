-- ================================================================
-- Fresh Logistics 监管员功能数据初始化脚本
-- 创建时间: 2025-09-30
-- 用途: 为合规检查和监管报告功能提供真实数据
-- ================================================================

USE freshlogistics;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================================
-- 1. 清空现有监管数据（如果需要）
-- ================================================================

DELETE FROM compliance_checks;
DELETE FROM regulator_reports;

-- ================================================================
-- 2. 插入合规检查记录数据
-- ================================================================

INSERT INTO compliance_checks (
    check_code, 
    check_type, 
    check_category, 
    target_type, 
    target_id, 
    regulator_id, 
    check_date, 
    check_items,
    check_results,
    compliance_score, 
    violations_found,
    check_status
) VALUES
-- 检查记录1: 供应商冷库设施检查
('CHK20250925001', 'routine', 'facility', 'supplier', 1, 8, '2025-09-25', 
 '冷库温控系统、卫生状况、消防设施、产品存储规范',
 '冷库温控系统运行正常，温度稳定在2-8℃',
 95.0, 0, 'completed'),

-- 检查记录2: 运输车辆温控检查
('CHK20250926001', 'spot', 'vehicle', 'vehicle', 1, 8, '2025-09-26',
 '车辆温控设备、温度传感器、GPS定位、车辆卫生',
 '车辆温控设备基本正常',
 92.0, 1, 'completed'),

-- 检查记录3: 绿色蔬菜基地流程检查
('CHK20250927001', 'routine', 'process', 'supplier', 2, 9, '2025-09-27',
 '采摘流程、预冷处理、包装规范、温度记录',
 '采摘流程规范；预冷处理及时',
 98.0, 0, 'completed'),

-- 检查记录4: 海鲜运输温控专项检查
('CHK20250928001', 'spot', 'vehicle', 'vehicle', 3, 8, '2025-09-28',
 '海鲜运输温控、保鲜措施、运输时效',
 '发现温控系统故障，温度超过安全范围',
 78.0, 3, 'completed'),

-- 检查记录5: 物流中心卫生检查
('CHK20250929001', 'routine', 'facility', 'logistics_company', 1, 9, '2025-09-29',
 '仓储卫生、消毒记录、人员健康证、防疫措施',
 '仓储卫生状况优秀',
 96.0, 0, 'completed'),

-- 检查记录6: 驾驶员资质检查
('CHK20250930001', 'spot', 'driver', 'driver', 1, 8, '2025-09-30',
 '驾驶证有效期、冷链资格证、健康证明',
 '驾驶证在有效期内；资格证齐全',
 94.0, 0, 'completed');

-- ================================================================
-- 3. 插入监管报告数据
-- ================================================================

INSERT INTO regulator_reports (
    report_code,
    report_type,
    report_title,
    report_period_start,
    report_period_end,
    regulator_id,
    summary,
    key_findings,
    statistics_data,
    compliance_trends,
    risk_assessment,
    recommendations,
    report_content,
    report_status,
    reviewer_id,
    review_time,
    publish_time,
    created_by
) VALUES
-- 报告1: 周报（已发布）
('RPT20250925001', 'weekly', '第38周冷链物流监管报告', 
 '2025-09-19', '2025-09-25', 8,
 '本周共完成15次合规检查，发现3处轻微违规，整体合规率达95%',
 '1. 车辆温控设备运行良好，98%达标\n2. 供应商冷库管理规范\n3. 部分产品标识需要完善\n4. 运输流程基本符合规范',
 '{"totalChecks": 15, "violations": 3, "complianceRate": 95, "vehicleChecks": 6, "supplierChecks": 5, "processChecks": 4}',
 '合规率较上周提升2%，温控违规减少50%，整体趋势向好',
 '总体风险较低。主要风险点：1.产品标识管理；2.部分传感器老化',
 '1. 加强供应商标识管理培训\n2. 及时更新老化的温控设备\n3. 继续保持当前监管力度',
 '详细的周报内容，包括各项检查的具体情况、数据分析、案例研究等...',
 'published', 1, '2025-09-26 10:00:00', '2025-09-26 15:00:00', 1),

-- 报告2: 月报（审核中）
('RPT20250929001', 'monthly', '9月份冷链物流监管月报',
 '2025-09-01', '2025-09-29', 9,
 '9月份共完成65次合规检查，处理12起预警事件，系统整体运行稳定',
 '1. 月度合规率96.8%，创历史新高\n2. 预警响应时间平均2.1秒\n3. 温控设备故障率下降30%\n4. 供应商合规意识显著提升',
 '{"totalChecks": 65, "alertsHandled": 12, "complianceRate": 96.8, "avgResponseTime": 2.1, "vehicleFailures": 3}',
 '月度合规率持续上升，从年初的89%提升至96.8%；预警处理效率提高40%',
 '低风险运行。主要关注：1.节假日期间的值班安排；2.新入驻企业的合规培训',
 '1. 继续优化预警算法，提升准确率\n2. 加强节假日监管力度\n3. 建立新企业培训机制\n4. 完善长效监管体系',
 '9月份完整的监管月报，包含详细的数据分析、趋势预测、案例分享...',
 'reviewing', 1, NULL, NULL, 1),

-- 报告3: 日报（草稿）
('RPT20250928001', 'daily', '9月28日合规检查日报',
 '2025-09-28', '2025-09-28', 8,
 '今日完成3家企业的例行检查，发现1处严重违规，已要求立即整改',
 '1. 新鲜农场有限公司：检查合格\n2. 绿色蔬菜基地：检查合格\n3. 海鲜运输专线：发现温控不达标问题',
 '{"totalChecks": 3, "violations": 1, "urgentCases": 1}',
 '海鲜运输环节需要重点关注，温控失效风险较高',
 '中等风险。海鲜运输专线的温控系统存在隐患，需要紧急处理',
 '1. 海鲜运输专线立即整改温控系统\n2. 一周内进行复查\n3. 加强海鲜类产品的监控频次',
 '今日检查详情：上午检查新鲜农场，下午检查蔬菜基地和海鲜运输...',
 'draft', NULL, NULL, NULL, 1),

-- 报告4: 专项报告（已发布）
('RPT20250930001', 'special', '国庆节前安全专项检查报告',
 '2025-09-28', '2025-09-30', 9,
 '国庆节前完成冷链企业安全专项检查，重点检查冷链设备和应急预案',
 '1. 所有检查单位均制定了节日期间应急预案\n2. 冷链设备运行正常率100%\n3. 值班人员安排到位\n4. 应急物资储备充足',
 '{"totalChecks": 8, "emergencyPlans": 8, "equipmentReady": 8, "staffArranged": 8}',
 '节前准备充分，各单位高度重视节日期间的冷链安全',
 '低风险。各单位准备充分，应急机制完善',
 '1. 建议节日期间加强24小时值班\n2. 加强设备巡检频次\n3. 保持应急响应通道畅通\n4. 做好突发事件预案',
 '国庆节前安全专项检查完整报告，包含所有检查单位的详细检查情况...',
 'published', 1, '2025-09-30 10:00:00', '2025-09-30 14:00:00', 1);

-- ================================================================
-- 4. 验证数据导入结果
-- ================================================================

-- 统计合规检查数据
SELECT 
    '合规检查记录' as '数据类型',
    COUNT(*) as '记录数',
    AVG(compliance_score) as '平均评分'
FROM compliance_checks;

-- 统计监管报告数据
SELECT 
    '监管报告' as '数据类型',
    COUNT(*) as '总数',
    COUNT(CASE WHEN report_status = 'published' THEN 1 END) as '已发布',
    COUNT(CASE WHEN report_status = 'reviewing' THEN 1 END) as '审核中',
    COUNT(CASE WHEN report_status = 'draft' THEN 1 END) as '草稿'
FROM regulator_reports;

-- 显示成功消息
SELECT 
    '✅ 监管员功能数据初始化完成！' as '状态',
    '合规检查记录6条，监管报告4条' as '详情';

-- ================================================================
-- 5. 快速验证查询
-- ================================================================

-- 查看合规检查列表
SELECT 
    check_code as '检查编号',
    check_type as '检查类型',
    check_category as '检查分类',
    target_type as '对象类型',
    DATE_FORMAT(check_date, '%Y-%m-%d') as '检查日期',
    compliance_score as '评分',
    violations_found as '违规数',
    check_status as '状态'
FROM compliance_checks
ORDER BY check_date DESC;

-- 查看监管报告列表
SELECT 
    report_code as '报告编号',
    report_type as '报告类型',
    report_title as '报告标题',
    DATE_FORMAT(report_period_start, '%Y-%m-%d') as '开始日期',
    DATE_FORMAT(report_period_end, '%Y-%m-%d') as '结束日期',
    report_status as '状态',
    DATE_FORMAT(publish_time, '%Y-%m-%d %H:%i') as '发布时间'
FROM regulator_reports
ORDER BY created_at DESC;
