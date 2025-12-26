-- ================================================================
-- Fresh Logistics 数据补充脚本
-- 补充完整的业务数据以支持系统演示
-- ================================================================

USE freshlogistics;

-- 设置字符集
SET NAMES utf8mb4;

-- ================================================================
-- 1. 补充用户数据 - 创建各角色用户
-- ================================================================

INSERT INTO sys_user (username, password, real_name, email, phone, user_type, status, created_by) VALUES
-- 供应商用户
('supplier01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '张经理', 'zhang@freshfarm.com', '13811112222', 'supplier', 1, 1),
('supplier02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '李总', 'li@greenveg.com', '13833334444', 'supplier', 1, 1),

-- 物流商用户
('logistics01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '王物流', 'wang@logistics.com', '13900001111', 'logistics', 1, 1),
('logistics02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '赵运输', 'zhao@transport.com', '13900002222', 'logistics', 1, 1),

-- 采购商用户
('purchaser01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '刘采购', 'liu@purchase.com', '13700001111', 'purchaser', 1, 1),
('purchaser02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '陈买手', 'chen@buyer.com', '13700002222', 'purchaser', 1, 1),

-- 监管员用户
('regulator01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '监管员A', 'reg01@gov.com', '13600001111', 'regulator', 1, 1),
('regulator02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '监管员B', 'reg02@gov.com', '13600002222', 'regulator', 1, 1);

-- 分配用户角色
INSERT INTO sys_user_role (user_id, role_id, created_by) VALUES
-- 供应商角色
(2, 2, 1), (3, 2, 1),
-- 物流商角色
(4, 3, 1), (5, 3, 1),
-- 采购商角色
(6, 4, 1), (7, 4, 1),
-- 监管员角色
(8, 5, 1), (9, 5, 1);

-- ================================================================
-- 2. 补充产品数据
-- ================================================================

INSERT INTO products (product_code, product_name, category_id, product_type, unit, min_temp, max_temp, min_humidity, max_humidity, shelf_life, description) VALUES
('PRD009', '进口车厘子', 1, 'fruit', '盒', 0.0, 4.0, 85.00, 95.00, 7, '智利进口车厘子，色泽鲜艳'),
('PRD010', '有机青菜', 2, 'vegetable', '公斤', 0.0, 4.0, 90.00, 98.00, 5, '本地有机青菜，无农药残留'),
('PRD011', '冰鲜带鱼', 4, 'seafood', '公斤', -2.0, 2.0, 80.00, 90.00, 2, '东海冰鲜带鱼，肉质鲜嫩'),
('PRD012', '精品牛排', 3, 'meat', '公斤', -1.0, 4.0, 75.00, 85.00, 14, '澳洲精品牛排，纹理清晰'),
('PRD013', '有机酸奶', 5, 'dairy', '瓶', 2.0, 6.0, 60.00, 70.00, 15, '有机酸奶，益生菌丰富');

-- 补充供应商产品关联
INSERT INTO supplier_products (supplier_id, product_id, supply_price, min_order_quantity, supply_capacity, quality_grade, lead_time, status, created_by) VALUES
-- 新鲜农场的产品 (supplier_id=1)
(1, 1, 15.50, 50, 1000, 'A', 1, 1, 1),  -- 新鲜橙子 (product_id=1)

-- 绿色蔬菜基地的产品 (supplier_id=2)  
(2, 2, 12.00, 100, 2000, 'A', 1, 1, 1), -- 有机菠菜 (product_id=2)

-- 海鲜直供公司的产品 (supplier_id=3)
(3, 3, 58.00, 10, 200, 'A', 1, 1, 1);   -- 新鲜三文鱼 (product_id=3)

-- ================================================================
-- 3. 补充订单数据
-- ================================================================

INSERT INTO orders (order_code, supplier_id, purchaser_id, order_type, order_status, total_amount, currency, order_weight, order_volume, pickup_address, pickup_contact, pickup_phone, delivery_address, delivery_contact, delivery_phone, required_delivery_time, temperature_requirement, order_notes, created_by) VALUES
-- 已完成订单
('ORD20250920001', 1, 6, 'standard', 'completed', 1550.00, 'CNY', 100.0, 5.0, '北京市顺义区农业园区1号', '张经理', '13811112222', '天津市河西区超市', '刘采购', '13700001111', '2025-09-21 10:00:00', '2-8℃', '第一批次橙子订单', 1),
('ORD20250921002', 2, 7, 'standard', 'completed', 890.50, 'CNY', 150.0, 8.0, '山东省寿光市蔬菜产业园', '李总', '13833334444', '北京市朝阳区菜市场', '陈买手', '13700002222', '2025-09-22 08:00:00', '0-4℃', '绿色蔬菜采购', 1),
('ORD20250922003', 3, 6, 'urgent', 'completed', 2100.00, 'CNY', 80.0, 4.0, '大连市海鲜批发市场A区', '王主任', '13855556666', '上海市浦东新区海鲜市场', '刘采购', '13700001111', '2025-09-23 12:00:00', '-2-2℃', '急需海鲜产品', 1),

-- 运输中订单
('ORD20250927001', 1, 6, 'standard', 'in_transit', 1280.00, 'CNY', 90.0, 4.5, '北京市顺义区农业园区1号', '张经理', '13811112222', '天津市河西区超市', '刘采购', '13700001111', '2025-09-27 16:00:00', '2-8℃', '新鲜橙子订单', 1),
('ORD20250927002', 2, 7, 'standard', 'in_transit', 750.00, 'CNY', 120.0, 6.0, '山东省寿光市蔬菜产业园', '李总', '13833334444', '石家庄市桥西区蔬菜市场', '陈买手', '13700002222', '2025-09-27 18:00:00', '0-4℃', '有机蔬菜配送', 1),

-- 待确认订单
('ORD20250929001', 1, 6, 'standard', 'created', 2200.00, 'CNY', 140.0, 7.0, '北京市顺义区农业园区1号', '张经理', '13811112222', '济南市历下区水果批发市场', '刘采购', '13700001111', '2025-09-30 09:00:00', '0-4℃', '车厘子大批量采购', 1),
('ORD20250929002', 3, 7, 'urgent', 'confirmed', 3200.00, 'CNY', 60.0, 3.0, '大连市海鲜批发市场A区', '王主任', '13855556666', '北京市东城区海鲜市场', '陈买手', '13700002222', '2025-09-29 20:00:00', '-2-2℃', '新鲜海鲜急单', 1);

-- ================================================================
-- 4. 补充订单明细数据
-- ================================================================

INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price, product_batch, production_date, expiry_date, quality_grade, item_notes) VALUES
-- 订单1明细（已完成）
(1, 1, 100.0, 15.50, 1550.00, 'BATCH20250920001', '2025-09-19', '2025-10-19', 'A', '一级新鲜橙子'),

-- 订单2明细（已完成）
(2, 2, 50.0, 12.00, 600.00, 'BATCH20250921001', '2025-09-21', '2025-09-28', 'A', '有机菠菜'),
(2, 5, 30.0, 9.80, 294.00, 'BATCH20250921002', '2025-09-21', '2025-09-26', 'A', '新鲜牛奶'),

-- 订单3明细（已完成）
(3, 3, 30.0, 58.00, 1740.00, 'BATCH20250922001', '2025-09-22', '2025-09-25', 'A', '新鲜三文鱼'),
(3, 4, 10.0, 35.00, 350.00, 'BATCH20250922002', '2025-09-22', '2025-09-24', 'A', '优质牛肉'),

-- 订单4明细（运输中）
(4, 1, 80.0, 16.00, 1280.00, 'BATCH20250927001', '2025-09-27', '2025-10-27', 'A', '新鲜橙子'),

-- 订单5明细（运输中）
(5, 2, 40.0, 12.50, 500.00, 'BATCH20250927002', '2025-09-27', '2025-10-04', 'A', '有机菠菜'),
(5, 5, 25.0, 10.00, 250.00, 'BATCH20250927003', '2025-09-27', '2025-10-02', 'A', '新鲜牛奶'),

-- 订单6明细（待确认）
(6, 1, 80.0, 28.00, 2240.00, 'BATCH20250929001', '2025-09-28', '2025-10-05', 'A', '新鲜橙子'),

-- 订单7明细（已确认）
(7, 3, 40.0, 60.00, 2400.00, 'BATCH20250929002', '2025-09-29', '2025-10-02', 'A', '新鲜三文鱼'),
(7, 4, 20.0, 38.00, 760.00, 'BATCH20250929003', '2025-09-29', '2025-10-01', 'A', '优质牛肉');

-- ================================================================
-- 5. 补充运输任务数据
-- ================================================================

INSERT INTO transports (transport_code, order_id, vehicle_id, driver_id, route_id, planned_start_time, actual_start_time, planned_arrival_time, actual_arrival_time, current_longitude, current_latitude, current_address, transport_status, total_distance, fuel_consumption, transport_cost, created_by) VALUES
-- 已完成运输
('TRP20250920001', 1, 1, 1, 1, '2025-09-20 08:00:00', '2025-09-20 08:15:00', '2025-09-20 12:00:00', '2025-09-20 11:45:00', 117.200983, 39.084158, '天津市河西区', 'completed', 150.5, 25.8, 180.00, 1),
('TRP20250921002', 2, 2, 2, 2, '2025-09-21 09:00:00', '2025-09-21 09:10:00', '2025-09-21 15:00:00', '2025-09-21 14:50:00', 114.514793, 38.042307, '石家庄市桥西区', 'completed', 280.3, 42.5, 320.00, 1),
('TRP20250922003', 3, 3, 3, 1, '2025-09-22 10:00:00', '2025-09-22 10:05:00', '2025-09-22 14:00:00', '2025-09-22 13:55:00', 121.473701, 31.230416, '上海市浦东新区', 'completed', 1200.8, 180.5, 1800.00, 1),

-- 运输中任务
('TRP20250927001', 4, 1, 1, 1, '2025-09-27 14:00:00', '2025-09-27 14:10:00', '2025-09-27 18:00:00', NULL, 116.85, 39.45, '京津高速中段', 'in_transit', 150.5, NULL, 180.00, 1),
('TRP20250927002', 5, 2, 2, 2, '2025-09-27 15:00:00', '2025-09-27 15:05:00', '2025-09-27 20:00:00', NULL, 116.12, 39.58, '京石高速', 'in_transit', 280.3, NULL, 320.00, 1);

-- ================================================================
-- 6. 补充物流订单数据
-- ================================================================

INSERT INTO logistics_orders (logistics_code, order_id, logistics_company_id, transport_id, logistics_type, logistics_status, logistics_fee, insurance_fee, pickup_time, delivery_time, signature_person, signature_time, logistics_notes, created_by) VALUES
-- 已完成物流
('LOG20250920001', 1, NULL, 1, 'self_delivery', 'delivered', 180.00, 20.00, '2025-09-20 08:15:00', '2025-09-20 11:45:00', '王收货员', '2025-09-20 11:45:00', '货物完好签收', 1),
('LOG20250921002', 2, NULL, 2, 'self_delivery', 'delivered', 320.00, 35.00, '2025-09-21 09:10:00', '2025-09-21 14:50:00', '张管理员', '2025-09-21 14:50:00', '蔬菜新鲜，质量良好', 1),
('LOG20250922003', 3, NULL, 3, 'self_delivery', 'delivered', 1800.00, 200.00, '2025-09-22 10:05:00', '2025-09-22 13:55:00', '李主管', '2025-09-22 13:55:00', '海鲜保鲜良好', 1),

-- 运输中物流
('LOG20250927001', 4, NULL, 4, 'self_delivery', 'in_transit', 180.00, 20.00, '2025-09-27 14:10:00', NULL, NULL, NULL, '运输中', 1),
('LOG20250927002', 5, NULL, 5, 'self_delivery', 'in_transit', 320.00, 35.00, '2025-09-27 15:05:00', NULL, NULL, NULL, '运输中', 1);

-- ================================================================
-- 7. 补充传感器数据（模拟实时数据）
-- ================================================================

INSERT INTO sensor_data (sensor_id, sensor_type, vehicle_id, transport_id, data_value, data_unit, longitude, latitude, altitude, signal_strength, battery_level, data_quality, collection_time, received_time) VALUES
-- 当前运输任务的传感器数据
('TEMP_001', 'temperature', 1, 4, 3.2, '℃', 116.850000, 39.450000, 50.0, 95, 85.5, 'good', '2025-09-29 19:30:00', '2025-09-29 19:30:05'),
('HUMI_001', 'humidity', 1, 4, 88.5, '%', 116.850000, 39.450000, 50.0, 95, 85.5, 'good', '2025-09-29 19:30:00', '2025-09-29 19:30:05'),
('GPS_001', 'gps', 1, 4, 0.0, '', 116.850000, 39.450000, 50.0, 98, 85.5, 'good', '2025-09-29 19:30:00', '2025-09-29 19:30:05'),

('TEMP_002', 'temperature', 2, 5, 2.8, '℃', 116.120000, 39.580000, 45.0, 92, 78.3, 'good', '2025-09-29 19:30:00', '2025-09-29 19:30:05'),
('HUMI_002', 'humidity', 2, 5, 91.2, '%', 116.120000, 39.580000, 45.0, 92, 78.3, 'good', '2025-09-29 19:30:00', '2025-09-29 19:30:05'),
('GPS_002', 'gps', 2, 5, 0.0, '', 116.120000, 39.580000, 45.0, 92, 78.3, 'good', '2025-09-29 19:30:00', '2025-09-29 19:30:05');

-- ================================================================
-- 8. 补充温度记录数据
-- ================================================================

INSERT INTO temperature_logs (sensor_id, vehicle_id, transport_id, temperature, humidity, location_longitude, location_latitude, is_normal, alert_triggered, record_time) VALUES
-- 正常温度记录
('TEMP_001', 1, 4, 3.2, 88.5, 116.850000, 39.450000, 1, 0, '2025-09-29 19:30:00'),
('TEMP_001', 1, 4, 3.1, 87.8, 116.840000, 39.440000, 1, 0, '2025-09-29 19:25:00'),
('TEMP_001', 1, 4, 3.3, 89.2, 116.860000, 39.460000, 1, 0, '2025-09-29 19:20:00'),

('TEMP_002', 2, 5, 2.8, 91.2, 116.120000, 39.580000, 1, 0, '2025-09-29 19:30:00'),
('TEMP_002', 2, 5, 2.9, 90.8, 116.110000, 39.570000, 1, 0, '2025-09-29 19:25:00'),

-- 异常温度记录（触发预警）
('TEMP_001', 1, 4, 8.5, 85.2, 116.800000, 39.400000, 0, 1, '2025-09-29 17:15:00'),
('TEMP_002', 2, 5, 9.2, 82.1, 116.080000, 39.550000, 0, 1, '2025-09-29 16:30:00');

-- ================================================================
-- 9. 补充预警记录数据
-- ================================================================

INSERT INTO alert_records (alert_code, rule_id, alert_type, alert_level, related_type, related_id, alert_title, alert_message, trigger_value, threshold_value, alert_data, alert_status, processor_id, process_time, process_notes, auto_processed, notification_sent, location_info) VALUES
-- 温度预警记录
('ALT20250929001', 2, 'temperature', 'error', 'transport', 4, '水果温度超标预警', '车辆京A12345运输的水果温度达到8.5℃，超过安全阈值8.0℃', 8.5, 8.0, '{"vehicleId": 1, "productType": "fruit", "location": "京津高速"}', 'processed', 4, '2025-09-29 17:20:00', '已联系司机调节温度，问题已解决', 0, 1, '京津高速中段'),
('ALT20250929002', 2, 'temperature', 'error', 'transport', 5, '蔬菜温度异常预警', '车辆京B67890运输的蔬菜温度达到9.2℃，超过安全阈值4.0℃', 9.2, 4.0, '{"vehicleId": 2, "productType": "vegetable", "location": "京石高速"}', 'processing', 5, NULL, NULL, 0, 1, '京石高速服务区'),

-- 超时预警记录
('ALT20250929003', 1, 'timeout', 'warning', 'transport', 4, '运输延迟预警', '运输任务TRP20250927001预计延迟35分钟到达', 35, 30, '{"transportId": 4, "routeId": 1, "traffic": "heavy"}', 'processed', 4, '2025-09-29 18:00:00', '交通拥堵导致，已重新规划路线', 0, 1, '天津市区入口'),

-- 路径偏离预警
('ALT20250929004', 7, 'route_deviation', 'error', 'transport', 5, '车辆偏离预设路线', '车辆京B67890偏离预设路线600米', 600, 500, '{"vehicleId": 2, "routeId": 2, "deviation": "service_area"}', 'pending', NULL, NULL, NULL, 0, 1, '京石高速服务区');

-- ================================================================
-- 10. 补充合规检查记录
-- ================================================================

INSERT INTO compliance_checks (check_code, check_type, check_category, target_type, target_id, regulator_id, check_date, check_location, check_items, check_results, compliance_score, violations_found, violation_details, corrective_actions, follow_up_required, check_status, created_by) VALUES
('CHK20250925001', 'routine', 'vehicle', 'vehicle', 1, 8, '2025-09-25', '北京市顺义区', '车辆温控设备检查、驾驶员资质验证', '车辆温控设备正常，驾驶员证件齐全', 95.0, 0, NULL, NULL, 0, 'completed', 1),
('CHK20250926001', 'spot', 'facility', 'supplier', 1, 8, '2025-09-26', '北京市顺义区农业园区1号', '冷库设施检查、产品质量抽检', '冷库温度控制良好，产品质量合格', 92.0, 1, '部分标识不清晰', '要求完善产品标识', 1, 'completed', 1),
('CHK20250927001', 'routine', 'process', 'logistics_company', 1, 9, '2025-09-27', '物流中心', '运输流程检查、温控记录审核', '运输流程规范，温控记录完整', 98.0, 0, NULL, NULL, 0, 'completed', 1);

-- ================================================================
-- 11. 补充监管报告数据
-- ================================================================

INSERT INTO regulator_reports (report_code, report_type, report_title, report_period_start, report_period_end, regulator_id, summary, key_findings, statistics_data, compliance_trends, risk_assessment, recommendations, report_content, report_status, reviewer_id, review_time, publish_time, created_by) VALUES
('RPT20250925001', 'weekly', '第38周冷链物流监管报告', '2025-09-19', '2025-09-25', 8, '本周共完成15次合规检查，发现3处轻微违规，整体合规率达95%', '车辆温控设备运行良好；部分供应商标识需要完善；运输流程基本规范', '{"totalChecks": 15, "violations": 3, "complianceRate": 95}', '合规率较上周提升2%，温控违规减少50%', '总体风险较低，主要风险点在产品标识管理', '建议加强供应商标识管理培训；继续保持温控设备监控', '详细监管报告内容...', 'published', 1, '2025-09-26 10:00:00', '2025-09-26 15:00:00', 1),
('RPT20250929001', 'monthly', '9月份冷链物流监管月报', '2025-09-01', '2025-09-29', 9, '9月份共完成65次合规检查，处理12起预警事件，系统运行稳定', '温控预警响应及时；供应商合规意识提升；运输效率持续改善', '{"totalChecks": 65, "alertsHandled": 12, "avgResponseTime": 2.1}', '月度合规率达96.8%，创历史新高', '低风险运行，预警处理效率良好', '建议继续优化预警算法；加强数据分析能力', '9月份监管月报详细内容...', 'reviewing', 1, NULL, NULL, 1);

-- ================================================================
-- 12. 补充系统配置数据
-- ================================================================

INSERT INTO sys_config (config_key, config_value, config_type, config_group, description, is_editable) VALUES
-- 业务配置
('business.order_timeout_hours', '24', 'number', 'business', '订单超时时间(小时)', 1),
('business.max_temperature_deviation', '2.0', 'number', 'business', '最大温度偏差(℃)', 1),
('business.min_delivery_distance', '10', 'number', 'business', '最小配送距离(公里)', 1),

-- 预警配置
('alert.email_enabled', 'true', 'boolean', 'alert', '是否启用邮件预警', 1),
('alert.sms_enabled', 'true', 'boolean', 'alert', '是否启用短信预警', 1),
('alert.max_alert_per_day', '100', 'number', 'alert', '每日最大预警数量', 1),

-- 系统配置
('system.backup_enabled', 'true', 'boolean', 'system', '是否启用自动备份', 1),
('system.log_retention_days', '30', 'number', 'system', '日志保留天数', 1),
('system.maintenance_mode', 'false', 'boolean', 'system', '是否为维护模式', 1);

-- ================================================================
-- 验证数据补充结果
-- ================================================================

SELECT '数据补充完成' as Status, 
       '用户增加8个，订单增加7个，运输任务5个，预警记录4个' as Summary;

-- 显示各表数据统计
SELECT 
    'sys_user' as table_name, COUNT(*) as record_count FROM sys_user
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL
SELECT 'transports', COUNT(*) FROM transports
UNION ALL
SELECT 'logistics_orders', COUNT(*) FROM logistics_orders
UNION ALL
SELECT 'sensor_data', COUNT(*) FROM sensor_data
UNION ALL
SELECT 'temperature_logs', COUNT(*) FROM temperature_logs
UNION ALL
SELECT 'alert_records', COUNT(*) FROM alert_records
UNION ALL
SELECT 'compliance_checks', COUNT(*) FROM compliance_checks
UNION ALL
SELECT 'regulator_reports', COUNT(*) FROM regulator_reports;
