-- ================================================================
-- Fresh Logistics 冷链物流智能监测预警系统 - 初始化数据
-- 字符集: utf8mb4
-- 创建时间: 2025-01-27
-- ================================================================

USE freshlogistics;

-- ================================================================
-- 1. 初始化系统角色数据
-- ================================================================

INSERT INTO sys_role (role_code, role_name, description, status, sort_order) VALUES
('admin', '系统管理员', '系统管理员，拥有所有权限', 1, 1),
('supplier', '供应商', '供应商角色，负责产品供应和订单管理', 1, 2),
('logistics', '物流商', '物流商角色，负责运输和车辆管理', 1, 3),
('purchaser', '采购商', '采购商角色，负责采购和收货确认', 1, 4),
('regulator', '监管员', '监管员角色，负责监管和合规检查', 1, 5);

-- ================================================================
-- 2. 初始化系统权限数据
-- ================================================================

INSERT INTO sys_permission (permission_code, permission_name, resource_type, resource_path, parent_id, level, sort_order) VALUES
-- 系统管理权限
('system:manage', '系统管理', 'menu', '/system', 0, 1, 1),
('system:user:list', '用户列表', 'menu', '/system/user', 1, 2, 1),
('system:user:add', '添加用户', 'button', null, 2, 3, 1),
('system:user:edit', '编辑用户', 'button', null, 2, 3, 2),
('system:user:delete', '删除用户', 'button', null, 2, 3, 3),
('system:role:list', '角色列表', 'menu', '/system/role', 1, 2, 2),
('system:role:add', '添加角色', 'button', null, 6, 3, 1),
('system:role:edit', '编辑角色', 'button', null, 6, 3, 2),
('system:role:delete', '删除角色', 'button', null, 6, 3, 3),

-- 供应商管理权限
('supplier:manage', '供应商管理', 'menu', '/supplier', 0, 1, 2),
('supplier:list', '供应商列表', 'menu', '/supplier/list', 10, 2, 1),
('supplier:add', '添加供应商', 'button', null, 11, 3, 1),
('supplier:edit', '编辑供应商', 'button', null, 11, 3, 2),
('supplier:delete', '删除供应商', 'button', null, 11, 3, 3),
('supplier:product:list', '供应商产品', 'menu', '/supplier/product', 10, 2, 2),
('supplier:product:add', '添加产品', 'button', null, 15, 3, 1),
('supplier:product:edit', '编辑产品', 'button', null, 15, 3, 2),

-- 订单管理权限
('order:manage', '订单管理', 'menu', '/order', 0, 1, 3),
('order:list', '订单列表', 'menu', '/order/list', 18, 2, 1),
('order:add', '创建订单', 'button', null, 19, 3, 1),
('order:edit', '编辑订单', 'button', null, 19, 3, 2),
('order:view', '查看订单', 'button', null, 19, 3, 3),
('order:cancel', '取消订单', 'button', null, 19, 3, 4),

-- 物流管理权限
('logistics:manage', '物流管理', 'menu', '/logistics', 0, 1, 4),
('logistics:vehicle:list', '车辆列表', 'menu', '/logistics/vehicle', 24, 2, 1),
('logistics:vehicle:add', '添加车辆', 'button', null, 25, 3, 1),
('logistics:vehicle:edit', '编辑车辆', 'button', null, 25, 3, 2),
('logistics:driver:list', '司机列表', 'menu', '/logistics/driver', 24, 2, 2),
('logistics:driver:add', '添加司机', 'button', null, 28, 3, 1),
('logistics:transport:list', '运输任务', 'menu', '/logistics/transport', 24, 2, 3),
('logistics:transport:assign', '分配任务', 'button', null, 30, 3, 1),

-- 监控预警权限
('monitor:manage', '监控预警', 'menu', '/monitor', 0, 1, 5),
('monitor:alert:list', '预警列表', 'menu', '/monitor/alert', 32, 2, 1),
('monitor:alert:process', '处理预警', 'button', null, 33, 3, 1),
('monitor:sensor:list', '传感器数据', 'menu', '/monitor/sensor', 32, 2, 2),
('monitor:rule:list', '预警规则', 'menu', '/monitor/rule', 32, 2, 3),
('monitor:rule:add', '添加规则', 'button', null, 36, 3, 1),

-- 数据分析权限
('analysis:manage', '数据分析', 'menu', '/analysis', 0, 1, 6),
('analysis:efficiency', '时效分析', 'menu', '/analysis/efficiency', 38, 2, 1),
('analysis:loss', '损耗分析', 'menu', '/analysis/loss', 38, 2, 2),
('analysis:alert', '预警统计', 'menu', '/analysis/alert', 38, 2, 3),

-- 监管权限
('regulation:manage', '监管管理', 'menu', '/regulation', 0, 1, 7),
('regulation:check:list', '合规检查', 'menu', '/regulation/check', 42, 2, 1),
('regulation:check:add', '新增检查', 'button', null, 43, 3, 1),
('regulation:report:list', '监管报告', 'menu', '/regulation/report', 42, 2, 2),
('regulation:report:add', '创建报告', 'button', null, 45, 3, 1);

-- ================================================================
-- 3. 初始化角色权限关联
-- ================================================================

-- 管理员权限(所有权限)
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 供应商权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 10), (2, 11), (2, 15), (2, 16), (2, 17), -- 供应商管理
(2, 18), (2, 19), (2, 20), (2, 21), (2, 22), -- 订单管理
(2, 32), (2, 33), (2, 34), -- 监控预警
(2, 38), (2, 39), (2, 40); -- 数据分析

-- 物流商权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 18), (3, 19), (3, 22), -- 订单查看
(3, 24), (3, 25), (3, 26), (3, 27), (3, 28), (3, 29), (3, 30), (3, 31), -- 物流管理
(3, 32), (3, 33), (3, 34), (3, 35), -- 监控预警
(3, 38), (3, 39), (3, 40); -- 数据分析

-- 采购商权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(4, 18), (4, 19), (4, 20), (4, 22), -- 订单管理
(4, 32), (4, 33), (4, 35), -- 监控预警
(4, 38), (4, 39); -- 数据分析

-- 监管员权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(5, 18), (5, 19), (5, 22), -- 订单查看
(5, 32), (5, 33), (5, 35), -- 监控预警
(5, 38), (5, 39), (5, 40), (5, 41), -- 数据分析
(5, 42), (5, 43), (5, 44), (5, 45), (5, 46); -- 监管管理

-- ================================================================
-- 4. 初始化产品分类数据
-- ================================================================

INSERT INTO product_categories (category_code, category_name, parent_id, level, sort_order, description) VALUES
('fruit', '水果类', 0, 1, 1, '新鲜水果类产品'),
('vegetable', '蔬菜类', 0, 1, 2, '新鲜蔬菜类产品'),
('meat', '肉类', 0, 1, 3, '新鲜肉类产品'),
('seafood', '海鲜水产', 0, 1, 4, '海鲜及水产品'),
('dairy', '乳制品', 0, 1, 5, '乳制品及奶制品'),
('frozen', '冷冻食品', 0, 1, 6, '冷冻类食品'),

-- 二级分类
('fruit_citrus', '柑橘类', 1, 2, 1, '橙子、柠檬、柚子等'),
('fruit_berry', '浆果类', 1, 2, 2, '草莓、蓝莓、葡萄等'),
('vegetable_leafy', '叶菜类', 2, 2, 1, '白菜、菠菜、生菜等'),
('vegetable_root', '根茎类', 2, 2, 2, '萝卜、土豆、红薯等'),
('meat_pork', '猪肉类', 3, 2, 1, '各类猪肉制品'),
('meat_beef', '牛肉类', 3, 2, 2, '各类牛肉制品'),
('seafood_fish', '鱼类', 4, 2, 1, '各类新鲜鱼类'),
('seafood_shellfish', '贝类', 4, 2, 2, '各类贝壳类海鲜'),
('dairy_milk', '牛奶类', 5, 2, 1, '各类牛奶产品'),
('dairy_cheese', '奶酪类', 5, 2, 2, '各类奶酪产品');

-- ================================================================
-- 5. 初始化示例产品数据
-- ================================================================

INSERT INTO products (product_code, product_name, category_id, product_type, unit, min_temp, max_temp, min_humidity, max_humidity, shelf_life, description) VALUES
('PRD001', '新鲜橙子', 7, 'fruit', '公斤', 2.0, 8.0, 85.00, 95.00, 30, '新鲜甜橙，富含维生素C'),
('PRD002', '有机菠菜', 9, 'vegetable', '公斤', 0.0, 4.0, 90.00, 98.00, 7, '有机种植菠菜，绿色健康'),
('PRD003', '新鲜三文鱼', 13, 'seafood', '公斤', -2.0, 2.0, 80.00, 90.00, 3, '挪威进口三文鱼，新鲜美味'),
('PRD004', '优质牛肉', 12, 'meat', '公斤', -1.0, 4.0, 75.00, 85.00, 7, '澳洲进口牛肉，肉质鲜美'),
('PRD005', '新鲜牛奶', 15, 'dairy', '升', 2.0, 6.0, 60.00, 70.00, 5, '新鲜全脂牛奶，营养丰富'),
('PRD006', '冷冻虾仁', 14, 'seafood', '公斤', -18.0, -15.0, 75.00, 85.00, 180, '急冻虾仁，保持新鲜'),
('PRD007', '新鲜草莓', 8, 'fruit', '盒', 0.0, 4.0, 85.00, 95.00, 5, '当季新鲜草莓，甜美可口'),
('PRD008', '土豆', 10, 'vegetable', '公斤', 4.0, 10.0, 80.00, 90.00, 60, '新鲜土豆，适合多种烹饪');

-- ================================================================
-- 6. 初始化系统配置数据
-- ================================================================

INSERT INTO sys_config (config_key, config_value, config_type, config_group, description) VALUES
-- 预警配置
('alert.timeout.default_threshold', '30', 'number', 'alert', '默认超时预警阈值(分钟)'),
('alert.temperature.default_range', '{"min": 2, "max": 8}', 'json', 'alert', '默认温度预警范围(℃)'),
('alert.humidity.default_range', '{"min": 80, "max": 95}', 'json', 'alert', '默认湿度预警范围(%)'),
('alert.route.default_radius', '500', 'number', 'alert', '默认路径偏离半径(米)'),
('alert.notification.enabled', 'true', 'boolean', 'alert', '是否启用预警通知'),

-- 地图配置
('map.api_key', 'your_amap_api_key_here', 'string', 'map', '高德地图API密钥'),
('map.default_zoom', '12', 'number', 'map', '地图默认缩放级别'),
('map.center_longitude', '116.397428', 'string', 'map', '地图默认中心经度'),
('map.center_latitude', '39.90923', 'string', 'map', '地图默认中心纬度'),

-- 系统配置
('system.company_name', 'Fresh Logistics', 'string', 'system', '公司名称'),
('system.max_upload_size', '10', 'number', 'system', '最大上传文件大小(MB)'),
('system.session_timeout', '30', 'number', 'system', '会话超时时间(分钟)'),
('system.password_policy', '{"minLength": 8, "requireNumber": true, "requireSpecial": true}', 'json', 'system', '密码策略'),

-- 数据配置
('data.sensor_batch_size', '1000', 'number', 'data', '传感器数据批处理大小'),
('data.retention_days', '90', 'number', 'data', '数据保留天数'),
('data.archive_days', '365', 'number', 'data', '数据归档天数');

-- ================================================================
-- 7. 初始化预警规则模板
-- ================================================================

INSERT INTO alert_rules (rule_code, rule_name, rule_type, rule_category, rule_condition, threshold_value, comparison_operator, alert_level, rule_description, is_enabled) VALUES
-- 通用超时规则
('RULE_TIMEOUT_DEFAULT', '默认超时预警规则', 'timeout', 'global', '{"field": "delay_minutes", "description": "运输延迟时间超过阈值"}', 30, '>', 'warning', '当运输任务延迟超过30分钟时触发预警', 1),

-- 通用温度规则
('RULE_TEMP_FRUIT', '水果温度预警规则', 'temperature', 'product_type', '{"product_type": "fruit", "field": "temperature", "description": "水果类产品温度超出范围"}', 8, '>', 'error', '水果类产品温度超过8℃时触发预警', 1),
('RULE_TEMP_MEAT', '肉类温度预警规则', 'temperature', 'product_type', '{"product_type": "meat", "field": "temperature", "description": "肉类产品温度超出范围"}', 4, '>', 'critical', '肉类产品温度超过4℃时触发预警', 1),
('RULE_TEMP_SEAFOOD', '海鲜温度预警规则', 'temperature', 'product_type', '{"product_type": "seafood", "field": "temperature", "description": "海鲜产品温度超出范围"}', 2, '>', 'critical', '海鲜产品温度超过2℃时触发预警', 1),

-- 通用湿度规则
('RULE_HUMIDITY_LOW', '湿度过低预警规则', 'humidity', 'global', '{"field": "humidity", "description": "湿度低于最低要求"}', 80, '<', 'warning', '湿度低于80%时触发预警', 1),
('RULE_HUMIDITY_HIGH', '湿度过高预警规则', 'humidity', 'global', '{"field": "humidity", "description": "湿度高于最高要求"}', 95, '>', 'warning', '湿度高于95%时触发预警', 1),

-- 路径偏离规则
('RULE_ROUTE_DEVIATION', '路径偏离预警规则', 'route_deviation', 'global', '{"field": "deviation_distance", "description": "车辆偏离预设路线"}', 500, '>', 'error', '车辆偏离预设路线超过500米时触发预警', 1);

-- ================================================================
-- 8. 初始化管理员账户
-- ================================================================

-- 创建默认管理员账户 (密码: admin123，实际使用时需要加密)
INSERT INTO sys_user (username, password, real_name, email, phone, user_type, status, created_by) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '系统管理员', 'admin@freshlogistics.com', '13800138000', 'admin', 1, 1);

-- 分配管理员角色
INSERT INTO sys_user_role (user_id, role_id, created_by) VALUES (1, 1, 1);

-- ================================================================
-- 9. 初始化示例数据（可选）
-- ================================================================

-- 示例供应商
INSERT INTO suppliers (supplier_code, supplier_name, contact_person, contact_phone, contact_email, address, cooperation_status, created_by) VALUES
('SUP001', '新鲜农场有限公司', '张经理', '13811112222', 'zhang@freshfarm.com', '北京市顺义区农业园区1号', 'active', 1),
('SUP002', '绿色蔬菜基地', '李总', '13833334444', 'li@greenveg.com', '山东省寿光市蔬菜产业园', 'active', 1),
('SUP003', '海鲜直供公司', '王主任', '13855556666', 'wang@seafoodsupply.com', '大连市海鲜批发市场A区', 'active', 1);

-- 示例车辆
INSERT INTO vehicles (vehicle_code, license_plate, vehicle_type, brand, model, load_capacity, volume_capacity, vehicle_status, logistics_company_id, created_by) VALUES
('VEH001', '京A12345', 'refrigerated_truck', '福田', '奥铃冷藏车', 5.00, 20.00, 'available', NULL, 1),
('VEH002', '京B67890', 'refrigerated_truck', '江铃', '顺达冷藏车', 3.00, 15.00, 'available', NULL, 1),
('VEH003', '京C11111', 'large_truck', '解放', 'J6L', 10.00, 40.00, 'available', NULL, 1);

-- 示例司机
INSERT INTO drivers (driver_code, name, id_card, phone, driving_license, license_type, license_expiry, driver_status, created_by) VALUES
('DRV001', '张师傅', '110101198001011234', '13900001111', 'B1234567890', 'B2', '2025-12-31', 'available', 1),
('DRV002', '李师傅', '110101198502022345', '13900002222', 'B2345678901', 'B2', '2026-06-30', 'available', 1),
('DRV003', '王师傅', '110101197912123456', '13900003333', 'B3456789012', 'A2', '2025-08-15', 'available', 1);

-- 示例运输路线
INSERT INTO transport_routes (route_code, route_name, start_point, start_longitude, start_latitude, end_point, end_longitude, end_latitude, estimated_distance, estimated_duration, created_by) VALUES
('ROUTE001', '北京-天津线', '北京市朝阳区', 116.407526, 39.904030, '天津市河西区', 117.200983, 39.084158, 150.00, 180, 1),
('ROUTE002', '北京-石家庄线', '北京市丰台区', 116.286944, 39.863333, '石家庄市桥西区', 114.514793, 38.042307, 280.00, 300, 1),
('ROUTE003', '北京-济南线', '北京市大兴区', 116.338611, 39.728889, '济南市历下区', 117.000923, 36.675807, 420.00, 450, 1);

-- ================================================================
-- 完成初始化
-- ================================================================

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 提交事务
COMMIT;
