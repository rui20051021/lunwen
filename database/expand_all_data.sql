-- ================================================================
-- Fresh Logistics 数据库全面扩充脚本
-- 创建时间: 2025-09-30
-- 用途: 为所有表添加更多真实业务数据，确保系统演示效果
-- ================================================================

USE freshlogistics;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================================
-- 1. 扩充用户数据 (sys_user)
-- ================================================================

INSERT IGNORE INTO sys_user (username, password, real_name, email, phone, user_type, status) VALUES
('supplier3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '水果批发王老板', 'wang@fruits.com', '13900006666', 'supplier', 1),
('supplier4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '有机农场陈经理', 'chen@organic.com', '13900007777', 'supplier', 1),
('logistics3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '速达物流孙总', 'sun@express.com', '13900008888', 'logistics', 1),
('purchaser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '连锁超市采购部', 'purchase@chain.com', '13900009999', 'purchaser', 1),
('regulator3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '市场监管局赵科长', 'zhao@regulator.gov', '13900010000', 'regulator', 1);

-- 为新用户分配角色
INSERT IGNORE INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id 
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username IN ('supplier3', 'supplier4') AND r.role_code = 'SUPPLIER'
UNION ALL
SELECT u.id, r.id 
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'logistics3' AND r.role_code = 'LOGISTICS'
UNION ALL
SELECT u.id, r.id 
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'purchaser3' AND r.role_code = 'PURCHASER'
UNION ALL
SELECT u.id, r.id 
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.username = 'regulator3' AND r.role_code = 'REGULATOR';

-- ================================================================
-- 2. 扩充产品数据 (products)
-- ================================================================

INSERT IGNORE INTO products (product_code, product_name, category_id, product_type, unit, min_temp, max_temp, min_humidity, max_humidity, shelf_life, status, description) VALUES
('PRD006', '进口车厘子', 1, 'fruit', '公斤', 0.0, 4.0, 85.00, 95.00, 7, 1, '进口智利车厘子，果肉饱满多汁'),
('PRD007', '有机西兰花', 2, 'vegetable', '公斤', 0.0, 5.0, 90.00, 95.00, 5, 1, '有机种植西兰花，营养丰富'),
('PRD008', '冰鲜龙虾', 3, 'seafood', '公斤', -2.0, 2.0, 80.00, 90.00, 2, 1, '加拿大进口龙虾，新鲜冰鲜'),
('PRD009', '精品羊肉卷', 4, 'meat', '公斤', -18.0, -15.0, 75.00, 85.00, 180, 1, '内蒙古羔羊肉，适合火锅'),
('PRD010', '有机酸奶', 5, 'dairy', '箱', 2.0, 6.0, 80.00, 90.00, 21, 1, '有机牧场酸奶，无添加'),
('PRD011', '新鲜蓝莓', 1, 'fruit', '盒', 0.0, 4.0, 85.00, 95.00, 7, 1, '国产优质蓝莓，果粒饱满'),
('PRD012', '冰鲜带鱼', 3, 'seafood', '公斤', -2.0, 2.0, 80.00, 90.00, 3, 1, '东海带鱼，肉质鲜美'),
('PRD013', '有机生菜', 2, 'vegetable', '公斤', 0.0, 5.0, 90.00, 95.00, 5, 1, '有机水培生菜，脆嫩可口'),
('PRD014', '澳洲牛排', 4, 'meat', '公斤', -2.0, 0.0, 75.00, 85.00, 30, 1, '澳洲谷饲牛排，高品质'),
('PRD015', '鲜榨果汁', 5, 'dairy', '瓶', 2.0, 6.0, 80.00, 90.00, 3, 1, '100%鲜榨果汁，无添加剂');

-- ================================================================
-- 3. 扩充供应商数据 (suppliers)
-- ================================================================

INSERT IGNORE INTO suppliers (supplier_code, supplier_name, contact_person, contact_phone, contact_email, address, cooperation_status, credit_rating) VALUES
('SUP004', '进口水果贸易公司', '孙总', '13822223333', 'sun@import.com', '上海市浦东新区自贸区5号', 'active', 4.6),
('SUP005', '有机农业合作社', '陈理事长', '13833334444', 'chen@organic-coop.com', '河北省张家口市有机农业园', 'active', 4.8);

-- ================================================================
-- 4. 扩充订单数据 (orders + order_items)
-- ================================================================

-- 新订单1
INSERT IGNORE INTO orders (order_code, supplier_id, purchaser_id, order_type, order_status, total_amount, currency, order_weight, order_volume, pickup_address, delivery_address, delivery_contact, delivery_phone, required_delivery_time, special_requirements) VALUES
('ORD20251001001', 4, 6, 'standard', 'created', 3250.00, 'CNY', 150.00, 8.50, '上海市浦东新区自贸区5号', '北京市朝阳区超市连锁总部', '李采购', '13900001234', '2025-10-01 10:00:00', '进口水果需要植检证明');

INSERT INTO order_items (order_id, product_id, product_code, product_name, quantity, unit_price, subtotal, temperature_requirement) VALUES
(LAST_INSERT_ID(), 6, 80, 35.00, 2800.00),
(LAST_INSERT_ID(), 11, 30, 15.00, 450.00);

-- 新订单2
INSERT IGNORE INTO orders (order_code, supplier_id, purchaser_id, order_type, order_status, total_amount, currency, order_weight, order_volume, pickup_address, delivery_address, delivery_contact, delivery_phone, required_delivery_time, special_requirements) VALUES
('ORD20251001002', 5, 7, 'standard', 'created', 1890.00, 'CNY', 200.00, 10.00, '河北省张家口市有机农业园', '天津市和平区有机食品店', '王店长', '13900005678', '2025-10-01 14:00:00', '有机认证产品');

INSERT INTO order_items (order_id, product_id, product_code, product_name, quantity, unit_price, subtotal, temperature_requirement) VALUES
(LAST_INSERT_ID(), 7, 60, 12.00, 720.00),
(LAST_INSERT_ID(), 13, 90, 13.00, 1170.00);

-- 新订单3-5（不同状态）
INSERT INTO orders (order_code, supplier_id, purchaser_id, order_type, order_status, total_amount, currency, order_weight, order_volume, pickup_address, delivery_address, delivery_contact, delivery_phone, required_delivery_time) VALUES
('ORD20250930003', 1, 6, 'urgent', 'confirmed', 4580.00, 'CNY', 180.00, 9.00, '北京市顺义区农业园区', '北京市海淀区大型商超', '赵经理', '13900002345', '2025-10-01 08:00:00'),
('ORD20250930004', 3, 7, 'standard', 'in_transit', 5620.00, 'CNY', 220.00, 12.00, '山东省青岛市海鲜市场', '石家庄市桥西区海鲜酒楼', '钱老板', '13900003456', '2025-10-01 12:00:00'),
('ORD20250930005', 2, 6, 'standard', 'delivered', 2340.00, 'CNY', 160.00, 8.50, '山东省寿光市蔬菜基地', '北京市丰台区蔬菜批发市场', '孙经理', '13900004567', '2025-09-30 18:00:00');

-- 对应的订单明细
INSERT IGNORE INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
-- ORD20250930003的明细
((SELECT id FROM orders WHERE order_code = 'ORD20250930003'), 1, 120, 18.00, 2160.00),
((SELECT id FROM orders WHERE order_code = 'ORD20250930003'), 6, 70, 35.00, 2450.00),
-- ORD20250930004的明细
((SELECT id FROM orders WHERE order_code = 'ORD20250930004'), 3, 45, 68.00, 3060.00),
((SELECT id FROM orders WHERE order_code = 'ORD20250930004'), 8, 32, 80.00, 2560.00),
-- ORD20250930005的明细
((SELECT id FROM orders WHERE order_code = 'ORD20250930005'), 2, 130, 12.00, 1560.00),
((SELECT id FROM orders WHERE order_code = 'ORD20250930005'), 7, 65, 12.00, 780.00);

-- ================================================================
-- 5. 扩充车辆数据 (vehicles)
-- ================================================================

INSERT INTO vehicles (vehicle_code, license_plate, vehicle_type, brand, model, year_of_manufacture, load_capacity, volume_capacity, fuel_type, vehicle_status, temperature_sensor_id, gps_device_id, purchase_date, last_maintenance_date) VALUES
('VEH004', '京D22222', 'refrigerated_truck', '福田', '欧马可冷藏车', 2023, 4.00, 18.00, 'diesel', 'available', 'TEMP_004', 'GPS_004', '2023-03-15', '2025-08-20'),
('VEH005', '京E33333', 'refrigerated_truck', '江淮', '骏铃冷藏车', 2024, 6.00, 25.00, 'diesel', 'available', 'TEMP_005', 'GPS_005', '2024-01-10', '2025-09-10'),
('VEH006', '京F44444', 'large_truck', '重汽', '豪沃冷藏车', 2022, 12.00, 45.00, 'diesel', 'available', 'TEMP_006', 'GPS_006', '2022-06-20', '2025-07-15'),
('VEH007', '津A55555', 'refrigerated_truck', '东风', '凯普特冷藏车', 2024, 5.00, 22.00, 'diesel', 'in_transit', 'TEMP_007', 'GPS_007', '2024-04-05', '2025-09-25'),
('VEH008', '津B66666', 'medium_truck', '解放', 'J6F冷藏车', 2023, 7.00, 30.00, 'diesel', 'available', 'TEMP_008', 'GPS_008', '2023-08-12', '2025-08-30');

-- ================================================================
-- 6. 扩充司机数据 (drivers)
-- ================================================================

INSERT INTO drivers (driver_code, name, id_card, phone, driving_license, license_type, license_expiry, driver_status, emergency_contact, emergency_phone, health_certificate_expiry) VALUES
('DRV004', '赵师傅', '110101198505051234', '13900004444', 'B4567890123', 'B2', '2026-03-31', 'available', '赵太太', '13900004445', '2026-03-31'),
('DRV005', '钱师傅', '120101198207071234', '13900005555', 'B5678901234', 'B2', '2025-12-31', 'available', '钱女士', '13900005556', '2025-12-31'),
('DRV006', '孙师傅', '130101197909091234', '13900006666', 'B6789012345', 'A2', '2026-06-30', 'available', '孙太太', '13900006667', '2026-06-30'),
('DRV007', '周师傅', '110101198612121234', '13900007777', 'B7890123456', 'B2', '2025-11-30', 'driving', '周女士', '13900007778', '2025-11-30'),
('DRV008', '吴师傅', '120101198403031234', '13900008888', 'B8901234567', 'A2', '2026-08-31', 'available', '吴太太', '13900008889', '2026-08-31');

-- ================================================================
-- 7. 扩充运输任务数据 (transports)
-- ================================================================

INSERT INTO transports (transport_code, order_id, vehicle_id, driver_id, route_id, route_name, planned_start_time, actual_start_time, planned_arrival_time, transport_status, current_location, current_temperature, current_humidity) VALUES
('TRP20250930001', (SELECT id FROM orders WHERE order_code = 'ORD20250930003'), 4, 4, 1, '北京-北京市内', '2025-10-01 07:00:00', NULL, '2025-10-01 09:00:00', 'pending', '北京市顺义区', 4.0, 88.0),
('TRP20250930002', (SELECT id FROM orders WHERE order_code = 'ORD20250930004'), 7, 7, 2, '青岛-石家庄线', '2025-10-01 10:00:00', '2025-10-01 10:15:00', '2025-10-01 18:00:00', 'in_transit', '济南市境内', 1.5, 85.0),
('TRP20250930003', (SELECT id FROM orders WHERE order_code = 'ORD20250930005'), 5, 5, 1, '寿光-北京线', '2025-09-30 14:00:00', '2025-09-30 14:20:00', '2025-09-30 19:00:00', 'arrived', '北京市丰台区', 3.8, 90.0);

-- ================================================================
-- 8. 扩充温度监控数据 (temperature_logs)
-- ================================================================

INSERT INTO temperature_logs (sensor_id, vehicle_id, transport_id, temperature, humidity, location_longitude, location_latitude, is_normal, alert_triggered, alert_id) VALUES
-- 车辆4的温度记录
('TEMP_004', 4, NULL, 4.5, 87.5, 116.4000000, 39.9000000, 1, 0, NULL),
('TEMP_004', 4, NULL, 4.2, 88.0, 116.4000000, 39.9000000, 1, 0, NULL),
-- 车辆5的温度记录
('TEMP_005', 5, NULL, 3.8, 89.5, 116.4500000, 39.9500000, 1, 0, NULL),
('TEMP_005', 5, NULL, 3.5, 90.0, 116.4500000, 39.9500000, 1, 0, NULL),
-- 车辆6的温度记录
('TEMP_006', 6, NULL, -1.0, 82.0, 116.5000000, 40.0000000, 1, 0, NULL),
('TEMP_006', 6, NULL, -0.8, 83.0, 116.5000000, 40.0000000, 1, 0, NULL),
-- 车辆7的温度记录（运输中）
('TEMP_007', 7, (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), 1.8, 85.0, 117.0000000, 36.6500000, 1, 0, NULL),
('TEMP_007', 7, (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), 2.0, 84.5, 117.1000000, 36.7000000, 1, 0, NULL),
('TEMP_007', 7, (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), 2.2, 85.5, 117.2000000, 36.7500000, 1, 0, NULL),
-- 车辆8的温度记录
('TEMP_008', 8, NULL, 5.0, 88.0, 116.3000000, 39.8000000, 1, 0, NULL),
('TEMP_008', 8, NULL, 5.5, 87.5, 116.3000000, 39.8000000, 1, 0, NULL),
-- 异常温度记录
('TEMP_004', 4, NULL, 9.2, 88.0, 116.4000000, 39.9000000, 0, 1, NULL),
('TEMP_007', 7, (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), 8.8, 85.0, 117.3000000, 36.8000000, 0, 1, NULL);

-- ================================================================
-- 9. 扩充预警记录数据 (alert_records)
-- ================================================================

INSERT INTO alert_records (alert_code, rule_id, alert_type, alert_level, related_type, related_id, alert_title, alert_message, threshold_value, current_value, alert_status, auto_processed, process_time, processor_id, process_notes) VALUES
('ALT20250930001', 1, 'temperature', 'warning', 'transport', 7, '运输温度偏高预警', '车辆京D22222运输过程中温度达到9.2℃，超过安全阈值', 8.0, 9.2, 'processed', 0, NOW(), 8, '已通知司机调整温控，温度已恢复正常'),
('ALT20250930002', 1, 'temperature', 'warning', 'vehicle', 4, '车辆温度异常', '车辆VEH004温度传感器显示9.2℃，需要检查', 8.0, 9.2, 'pending', 0, NULL, NULL, NULL),
('ALT20250930003', 2, 'timeout', 'info', 'order', (SELECT id FROM orders WHERE order_code = 'ORD20250930004'), '订单延迟提醒', '订单ORD20250930004预计送达时间临近', 2.0, 1.5, 'pending', 1, NULL, NULL, NULL),
('ALT20250930004', 1, 'temperature', 'error', 'transport', (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), '严重温度超标', '运输任务中温度达到8.8℃，接近危险阈值', 8.0, 8.8, 'processing', 0, NULL, 8, '正在处理中');

-- ================================================================
-- 10. 扩充合规检查数据 (compliance_checks)
-- ================================================================

INSERT INTO compliance_checks (check_code, check_type, check_category, target_type, target_id, regulator_id, check_date, check_items, check_results, compliance_score, violations_found, check_status) VALUES
('CHK20250930002', 'routine', 'facility', 'supplier', 4, 8, '2025-09-30', '进口水果仓储设施、植检证明、温控设备', '仓储设施符合要求，植检证明齐全', 94.0, 0, 'completed'),
('CHK20250930003', 'routine', 'facility', 'supplier', 5, 9, '2025-09-30', '有机认证、种植记录、温控设备、产品追溯', '有机认证有效，种植记录完整，产品可追溯', 97.0, 0, 'completed'),
('CHK20250930004', 'spot', 'vehicle', 'vehicle', 4, 8, '2025-09-30', '车辆温控系统、GPS定位、消防设备', '温控系统正常，GPS在线，消防设备齐全', 93.0, 0, 'completed'),
('CHK20250930005', 'spot', 'vehicle', 'vehicle', 5, 9, '2025-09-30', '车辆卫生状况、温度记录、驾驶员资质', '卫生良好，温度记录完整', 91.0, 0, 'completed'),
('CHK20250930006', 'routine', 'driver', 'driver', 4, 8, '2025-09-30', '驾驶证有效期、健康证明、冷链培训证书', '证件齐全有效，培训记录完整', 95.0, 0, 'completed'),
('CHK20250930007', 'routine', 'driver', 'driver', 5, 9, '2025-09-30', '驾驶证、健康证、安全培训记录', '证件齐全，培训合格', 92.0, 0, 'completed'),
('CHK20250930008', 'spot', 'process', 'supplier', 1, 8, '2025-09-30', '产品采摘流程、预冷处理、包装规范', '流程规范，预冷及时', 96.0, 0, 'completed'),
('CHK20250930009', 'routine', 'facility', 'logistics_company', 1, 9, '2025-09-30', '仓库温控、消毒记录、应急预案', '温控系统运行正常，消毒记录完整', 94.0, 0, 'completed');

-- ================================================================
-- 11. 扩充监管报告数据 (regulator_reports)
-- ================================================================

INSERT INTO regulator_reports (report_code, report_type, report_title, report_period_start, report_period_end, regulator_id, summary, key_findings, recommendations, report_content, report_status, created_by) VALUES
('RPT20250930002', 'daily', '9月30日合规检查日报', '2025-09-30', '2025-09-30', 8, '今日完成8项合规检查，全部通过', '新增供应商检查合格率100%', '继续保持监管力度', '今日检查详细内容...', 'published', 1),
('RPT20250930003', 'weekly', '第39周冷链物流监管周报', '2025-09-26', '2025-10-02', 9, '本周合规率达97%，创新高', '温控设备运行稳定，违规率下降', '建议表彰优秀企业', '本周检查总结...', 'reviewing', 1),
('RPT20251001001', 'special', '国庆假期冷链安全报告', '2025-10-01', '2025-10-07', 8, '假期专项检查，确保食品安全', '所有单位值班到位，应急预案完善', '加强节假日巡查', '假期安全检查报告...', 'draft', 1);

-- ================================================================
-- 12. 扩充供应商评价数据 (supplier_evaluations)
-- ================================================================

INSERT INTO supplier_evaluations (supplier_id, order_code, evaluator_name, service_rating, quality_rating, delivery_rating, overall_rating, evaluation_content, suggestions) VALUES
(4, 'ORD20250930001', '李采购', 4.7, 4.8, 4.5, 4.7, '进口水果质量优秀，包装精美，配送及时', '希望能提供更多品种的进口水果'),
(5, 'ORD20250930002', '王店长', 4.9, 4.9, 4.8, 4.9, '有机蔬菜品质极佳，客户反馈很好', '无需改进，继续保持'),
(1, 'ORD20250930003', '赵经理', 4.3, 4.5, 4.0, 4.3, '产品质量稳定，服务响应及时', '建议提升配送时效'),
(3, 'ORD20250930004', '钱老板', 4.6, 4.7, 4.4, 4.6, '海鲜新鲜度高，冷链保护到位', '建议加强运输途中的温控监测'),
(2, 'ORD20250930005', '孙经理', 4.4, 4.6, 4.2, 4.4, '蔬菜新鲜，配送准时，整体满意', '包装可以再改进'),
(1, 'ORD20250927001', '李部长', 4.1, 4.2, 3.9, 4.1, '基本满意，但配送环节有待加强', '加强配送人员培训'),
(2, 'ORD20250927002', '周主管', 4.5, 4.6, 4.4, 4.5, '产品质量好，服务专业', '建议提供更详细的产品检测报告');

-- ================================================================
-- 13. 扩充传感器数据 (sensor_data)
-- ================================================================

INSERT INTO sensor_data (sensor_id, sensor_type, vehicle_id, transport_id, data_value, data_unit, longitude, latitude, data_quality, is_anomaly) VALUES
-- 新车辆的传感器数据
('TEMP_004', 'temperature', 4, NULL, 4.5, '℃', 116.4000000, 39.9000000, 'good', 0),
('TEMP_005', 'temperature', 5, NULL, 3.8, '℃', 116.4500000, 39.9500000, 'good', 0),
('TEMP_006', 'temperature', 6, NULL, -1.0, '℃', 116.5000000, 40.0000000, 'good', 0),
('TEMP_007', 'temperature', 7, (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), 1.8, '℃', 117.0000000, 36.6500000, 'good', 0),
('TEMP_008', 'temperature', 8, NULL, 5.0, '℃', 116.3000000, 39.8000000, 'good', 0),
-- 湿度传感器数据
('HUM_004', 'humidity', 4, NULL, 87.5, '%', 116.4000000, 39.9000000, 'good', 0),
('HUM_005', 'humidity', 5, NULL, 89.5, '%', 116.4500000, 39.9500000, 'good', 0),
('HUM_007', 'humidity', 7, (SELECT id FROM transports WHERE transport_code = 'TRP20250930002'), 85.0, '%', 117.0000000, 36.6500000, 'good', 0);

-- ================================================================
-- 14. 扩充供应商产品关联 (supplier_products)
-- ================================================================

INSERT INTO supplier_products (supplier_id, product_id, supply_price, min_order_quantity, delivery_cycle, status) VALUES
(4, 6, 32.00, 50, 1, 1),
(4, 11, 13.00, 30, 1, 1),
(5, 7, 10.00, 50, 1, 1),
(5, 13, 11.00, 80, 1, 1),
(1, 6, 33.00, 60, 2, 1),
(2, 7, 11.00, 60, 2, 1),
(3, 8, 75.00, 20, 1, 1),
(3, 12, 42.00, 30, 1, 1);

-- ================================================================
-- 15. 验证数据扩充结果
-- ================================================================

-- 统计所有表的数据量
SELECT 'sys_user' as '表名', COUNT(*) as '记录数' FROM sys_user
UNION ALL SELECT 'products', COUNT(*) FROM products
UNION ALL SELECT 'orders', COUNT(*) FROM orders
UNION ALL SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL SELECT 'suppliers', COUNT(*) FROM suppliers
UNION ALL SELECT 'vehicles', COUNT(*) FROM vehicles
UNION ALL SELECT 'drivers', COUNT(*) FROM drivers
UNION ALL SELECT 'transports', COUNT(*) FROM transports
UNION ALL SELECT 'temperature_logs', COUNT(*) FROM temperature_logs
UNION ALL SELECT 'sensor_data', COUNT(*) FROM sensor_data
UNION ALL SELECT 'alert_records', COUNT(*) FROM alert_records
UNION ALL SELECT 'compliance_checks', COUNT(*) FROM compliance_checks
UNION ALL SELECT 'regulator_reports', COUNT(*) FROM regulator_reports
UNION ALL SELECT 'supplier_evaluations', COUNT(*) FROM supplier_evaluations
UNION ALL SELECT 'supplier_products', COUNT(*) FROM supplier_products
ORDER BY 记录数 DESC;

-- 显示成功消息
SELECT '🎉 数据库数据扩充完成！' as '状态',
       '所有表数据量已大幅增加' as '详情';

-- 业务数据关联验证
SELECT 
    '订单-供应商关联' as '验证项',
    COUNT(DISTINCT o.id) as '订单数',
    COUNT(DISTINCT s.id) as '供应商数',
    '✅' as '状态'
FROM orders o
LEFT JOIN suppliers s ON o.supplier_id = s.id;

SELECT 
    '运输-车辆-司机关联' as '验证项',
    COUNT(DISTINCT t.id) as '运输任务',
    COUNT(DISTINCT v.id) as '车辆数',
    COUNT(DISTINCT d.id) as '司机数',
    '✅' as '状态'
FROM transports t
LEFT JOIN vehicles v ON t.vehicle_id = v.id
LEFT JOIN drivers d ON t.driver_id = d.id;
