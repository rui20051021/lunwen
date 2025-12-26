-- ================================================================
-- Fresh Logistics 业务数据补充脚本
-- 只添加订单、运输、预警等业务数据
-- ================================================================

USE freshlogistics;

-- 设置字符集
SET NAMES utf8mb4;

-- ================================================================
-- 1. 补充订单数据
-- ================================================================

INSERT INTO orders (order_code, supplier_id, purchaser_id, order_type, order_status, total_amount, currency, order_weight, order_volume, pickup_address, pickup_contact, pickup_phone, delivery_address, delivery_contact, delivery_phone, required_delivery_time, temperature_requirement, order_notes, created_by) VALUES
-- 已完成订单
('ORD20250920001', 1, 6, 'standard', 'completed', 1550.00, 'CNY', 100.0, 5.0, '北京市顺义区农业园区1号', '张经理', '13811112222', '天津市河西区超市', '刘采购', '13700001111', '2025-09-21 10:00:00', '2-8℃', '橙子批发订单', 1),
('ORD20250921002', 2, 7, 'standard', 'completed', 894.00, 'CNY', 80.0, 4.0, '山东省寿光市蔬菜产业园', '李总', '13833334444', '北京市朝阳区菜市场', '陈买手', '13700002222', '2025-09-22 08:00:00', '0-4℃', '蔬菜配送订单', 1),
('ORD20250922003', 3, 6, 'urgent', 'completed', 2090.00, 'CNY', 40.0, 2.0, '大连市海鲜批发市场A区', '王主任', '13855556666', '上海市浦东新区', '刘采购', '13700001111', '2025-09-23 12:00:00', '-2-2℃', '海鲜急送订单', 1),

-- 运输中订单
('ORD20250927001', 1, 6, 'standard', 'in_transit', 1280.00, 'CNY', 80.0, 4.0, '北京市顺义区农业园区1号', '张经理', '13811112222', '天津市河西区超市', '刘采购', '13700001111', '2025-09-27 16:00:00', '2-8℃', '橙子补货订单', 1),
('ORD20250927002', 2, 7, 'standard', 'in_transit', 750.00, 'CNY', 70.0, 3.5, '山东省寿光市蔬菜产业园', '李总', '13833334444', '石家庄市桥西区', '陈买手', '13700002222', '2025-09-27 18:00:00', '0-4℃', '蔬菜配送', 1),

-- 待确认订单
('ORD20250929001', 1, 6, 'standard', 'created', 2240.00, 'CNY', 80.0, 4.0, '北京市顺义区农业园区1号', '张经理', '13811112222', '济南市历下区批发市场', '刘采购', '13700001111', '2025-09-30 09:00:00', '0-4℃', '大批量采购', 1),
('ORD20250929002', 3, 7, 'urgent', 'confirmed', 3160.00, 'CNY', 60.0, 3.0, '大连市海鲜批发市场A区', '王主任', '13855556666', '北京市东城区海鲜市场', '陈买手', '13700002222', '2025-09-29 20:00:00', '-2-2℃', '海鲜急单', 1);

-- ================================================================
-- 2. 补充订单明细数据
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
-- 3. 补充运输任务数据
-- ================================================================

INSERT INTO transports (transport_code, order_id, vehicle_id, driver_id, route_id, planned_start_time, actual_start_time, planned_arrival_time, actual_arrival_time, current_longitude, current_latitude, current_address, transport_status, total_distance, fuel_consumption, transport_cost, created_by) VALUES
-- 已完成运输
('TRP20250920001', 1, 1, 1, 1, '2025-09-20 08:00:00', '2025-09-20 08:15:00', '2025-09-20 12:00:00', '2025-09-20 11:45:00', 117.200983, 39.084158, '天津市河西区', 'completed', 150.5, 25.8, 180.00, 1),
('TRP20250921002', 2, 2, 2, 2, '2025-09-21 09:00:00', '2025-09-21 09:10:00', '2025-09-21 15:00:00', '2025-09-21 14:50:00', 114.514793, 38.042307, '石家庄市桥西区', 'completed', 280.3, 42.5, 320.00, 1),
('TRP20250922003', 3, 3, 3, 2, '2025-09-22 10:00:00', '2025-09-22 10:05:00', '2025-09-22 16:00:00', '2025-09-22 15:55:00', 114.514793, 38.042307, '石家庄市桥西区', 'completed', 280.0, 45.2, 320.00, 1),

-- 运输中任务
('TRP20250927001', 4, 1, 1, 1, '2025-09-27 14:00:00', '2025-09-27 14:10:00', '2025-09-27 18:00:00', NULL, 116.85, 39.45, '京津高速中段', 'in_transit', 150.5, NULL, 180.00, 1),
('TRP20250927002', 5, 2, 2, 2, '2025-09-27 15:00:00', '2025-09-27 15:05:00', '2025-09-27 20:00:00', NULL, 116.12, 39.58, '京石高速', 'in_transit', 280.3, NULL, 320.00, 1);

-- ================================================================
-- 4. 补充传感器数据（当前时间）
-- ================================================================

INSERT INTO sensor_data (sensor_id, sensor_type, vehicle_id, transport_id, data_value, data_unit, longitude, latitude, altitude, signal_strength, battery_level, data_quality, collection_time, received_time) VALUES
-- 运输中车辆的实时传感器数据
('TEMP_001', 'temperature', 1, 4, 3.2, '℃', 116.850000, 39.450000, 50.0, 95, 85.5, 'good', NOW(), NOW()),
('HUMI_001', 'humidity', 1, 4, 88.5, '%', 116.850000, 39.450000, 50.0, 95, 85.5, 'good', NOW(), NOW()),
('GPS_001', 'gps', 1, 4, 0.0, '', 116.850000, 39.450000, 50.0, 98, 85.5, 'good', NOW(), NOW()),

('TEMP_002', 'temperature', 2, 5, 2.8, '℃', 116.120000, 39.580000, 45.0, 92, 78.3, 'good', NOW(), NOW()),
('HUMI_002', 'humidity', 2, 5, 91.2, '%', 116.120000, 39.580000, 45.0, 92, 78.3, 'good', NOW(), NOW()),
('GPS_002', 'gps', 2, 5, 0.0, '', 116.120000, 39.580000, 45.0, 92, 78.3, 'good', NOW(), NOW());

-- ================================================================
-- 5. 补充温度记录数据
-- ================================================================

INSERT INTO temperature_logs (sensor_id, vehicle_id, transport_id, temperature, humidity, location_longitude, location_latitude, is_normal, alert_triggered, record_time) VALUES
-- 正常温度记录
('TEMP_001', 1, 4, 3.2, 88.5, 116.850000, 39.450000, 1, 0, NOW()),
('TEMP_001', 1, 4, 3.1, 87.8, 116.840000, 39.440000, 1, 0, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
('TEMP_001', 1, 4, 3.3, 89.2, 116.860000, 39.460000, 1, 0, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),

('TEMP_002', 2, 5, 2.8, 91.2, 116.120000, 39.580000, 1, 0, NOW()),
('TEMP_002', 2, 5, 2.9, 90.8, 116.110000, 39.570000, 1, 0, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),

-- 异常温度记录（触发预警）
('TEMP_001', 1, 4, 8.5, 85.2, 116.800000, 39.400000, 0, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('TEMP_002', 2, 5, 9.2, 82.1, 116.080000, 39.550000, 0, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR));

-- ================================================================
-- 6. 补充预警记录数据
-- ================================================================

INSERT INTO alert_records (alert_code, rule_id, alert_type, alert_level, related_type, related_id, alert_title, alert_message, trigger_value, threshold_value, alert_data, alert_status, processor_id, process_time, process_notes, auto_processed, notification_sent, location_info) VALUES
-- 温度预警记录
('ALT20250929001', 2, 'temperature', 'error', 'transport', 4, '温度超标预警', '车辆京A12345运输温度达到8.5℃，超过安全阈值', 8.5, 8.0, '{"vehicleId": 1, "productType": "fruit"}', 'processed', 4, DATE_SUB(NOW(), INTERVAL 1 HOUR), '已联系司机调节温度，问题已解决', 0, 1, '京津高速中段'),
('ALT20250929002', 2, 'temperature', 'error', 'transport', 5, '蔬菜温度异常', '车辆京B67890运输温度达到9.2℃，超过安全阈值', 9.2, 4.0, '{"vehicleId": 2, "productType": "vegetable"}', 'processing', NULL, NULL, NULL, 0, 1, '京石高速'),

-- 超时预警记录
('ALT20250929003', 1, 'timeout', 'warning', 'transport', 4, '运输延迟预警', '运输任务预计延迟35分钟到达', 35, 30, '{"transportId": 4, "routeId": 1}', 'processed', 4, DATE_SUB(NOW(), INTERVAL 30 MINUTE), '交通拥堵，已重新规划路线', 0, 1, '天津市区');

-- ================================================================
-- 验证数据补充结果
-- ================================================================

SELECT '业务数据补充完成' as Status;

-- 显示数据统计
SELECT 
    'orders' as table_name, COUNT(*) as record_count FROM orders
UNION ALL
SELECT 'order_items', COUNT(*) FROM order_items  
UNION ALL
SELECT 'transports', COUNT(*) FROM transports
UNION ALL
SELECT 'sensor_data', COUNT(*) FROM sensor_data
UNION ALL
SELECT 'temperature_logs', COUNT(*) FROM temperature_logs
UNION ALL
SELECT 'alert_records', COUNT(*) FROM alert_records;
