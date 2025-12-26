-- ================================================================
-- Fresh Logistics 初始化数据 - UTF-8 编码
-- ================================================================

USE freshlogistics;

-- 设置连接字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 初始化系统角色数据
INSERT INTO sys_role (role_code, role_name, description, status, sort_order) VALUES
('admin', '系统管理员', '系统管理员，拥有所有权限', 1, 1),
('supplier', '供应商', '供应商角色，负责产品供应和订单管理', 1, 2),
('logistics', '物流商', '物流商角色，负责运输和车辆管理', 1, 3),
('purchaser', '采购商', '采购商角色，负责采购和收货确认', 1, 4),
('regulator', '监管员', '监管员角色，负责监管和合规检查', 1, 5);

-- 初始化权限数据
INSERT INTO sys_permission (permission_code, permission_name, resource_type, resource_path, parent_id, level, sort_order) VALUES
('system:manage', '系统管理', 'menu', '/system', 0, 1, 1),
('system:user:list', '用户列表', 'menu', '/system/user', 1, 2, 1),
('system:user:add', '添加用户', 'button', null, 2, 3, 1),
('system:user:edit', '编辑用户', 'button', null, 2, 3, 2),
('system:user:delete', '删除用户', 'button', null, 2, 3, 3),
('supplier:manage', '供应商管理', 'menu', '/supplier', 0, 1, 2),
('order:manage', '订单管理', 'menu', '/order', 0, 1, 3),
('logistics:manage', '物流管理', 'menu', '/logistics', 0, 1, 4),
('monitor:manage', '监控预警', 'menu', '/monitor', 0, 1, 5),
('analysis:manage', '数据分析', 'menu', '/analysis', 0, 1, 6);

-- 创建默认管理员账户
INSERT INTO sys_user (username, password, real_name, email, phone, user_type, status, created_by) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9b3IJqHlT1ke1ay', '系统管理员', 'admin@freshlogistics.com', '13800138000', 'admin', 1, 1);

-- 分配管理员角色
INSERT INTO sys_user_role (user_id, role_id, created_by) VALUES (1, 1, 1);

-- 管理员权限分配
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);

-- 初始化产品分类
INSERT INTO product_categories (category_code, category_name, parent_id, level, sort_order, description) VALUES
('fruit', '水果类', 0, 1, 1, '新鲜水果类产品'),
('vegetable', '蔬菜类', 0, 1, 2, '新鲜蔬菜类产品'),
('meat', '肉类', 0, 1, 3, '新鲜肉类产品'),
('seafood', '海鲜水产', 0, 1, 4, '海鲜及水产品'),
('dairy', '乳制品', 0, 1, 5, '乳制品及奶制品');

-- 初始化示例产品
INSERT INTO products (product_code, product_name, category_id, product_type, unit, min_temp, max_temp, min_humidity, max_humidity, shelf_life, description) VALUES
('PRD001', '新鲜橙子', 1, 'fruit', '公斤', 2.0, 8.0, 85.00, 95.00, 30, '新鲜甜橙，富含维生素C'),
('PRD002', '有机菠菜', 2, 'vegetable', '公斤', 0.0, 4.0, 90.00, 98.00, 7, '有机种植菠菜，绿色健康'),
('PRD003', '新鲜三文鱼', 4, 'seafood', '公斤', -2.0, 2.0, 80.00, 90.00, 3, '挪威进口三文鱼，新鲜美味'),
('PRD004', '优质牛肉', 3, 'meat', '公斤', -1.0, 4.0, 75.00, 85.00, 7, '澳洲进口牛肉，肉质鲜美'),
('PRD005', '新鲜牛奶', 5, 'dairy', '升', 2.0, 6.0, 60.00, 70.00, 5, '新鲜全脂牛奶，营养丰富');

-- 初始化供应商
INSERT INTO suppliers (supplier_code, supplier_name, contact_person, contact_phone, contact_email, address, cooperation_status, created_by) VALUES
('SUP001', '新鲜农场有限公司', '张经理', '13811112222', 'zhang@freshfarm.com', '北京市顺义区农业园区1号', 'active', 1),
('SUP002', '绿色蔬菜基地', '李总', '13833334444', 'li@greenveg.com', '山东省寿光市蔬菜产业园', 'active', 1),
('SUP003', '海鲜直供公司', '王主任', '13855556666', 'wang@seafoodsupply.com', '大连市海鲜批发市场A区', 'active', 1);

-- 初始化车辆
INSERT INTO vehicles (vehicle_code, license_plate, vehicle_type, brand, model, load_capacity, volume_capacity, vehicle_status, created_by) VALUES
('VEH001', '京A12345', 'refrigerated_truck', '福田', '奥铃冷藏车', 5.00, 20.00, 'available', 1),
('VEH002', '京B67890', 'refrigerated_truck', '江铃', '顺达冷藏车', 3.00, 15.00, 'available', 1),
('VEH003', '京C11111', 'large_truck', '解放', 'J6L', 10.00, 40.00, 'available', 1);

-- 初始化司机
INSERT INTO drivers (driver_code, name, id_card, phone, driving_license, license_type, license_expiry, driver_status, created_by) VALUES
('DRV001', '张师傅', '110101198001011234', '13900001111', 'B1234567890', 'B2', '2025-12-31', 'available', 1),
('DRV002', '李师傅', '110101198502022345', '13900002222', 'B2345678901', 'B2', '2026-06-30', 'available', 1),
('DRV003', '王师傅', '110101197912123456', '13900003333', 'B3456789012', 'A2', '2025-08-15', 'available', 1);

-- 初始化运输路线
INSERT INTO transport_routes (route_code, route_name, start_point, start_longitude, start_latitude, end_point, end_longitude, end_latitude, estimated_distance, estimated_duration, created_by) VALUES
('ROUTE001', '北京-天津线', '北京市朝阳区', 116.407526, 39.904030, '天津市河西区', 117.200983, 39.084158, 150.00, 180, 1),
('ROUTE002', '北京-石家庄线', '北京市丰台区', 116.286944, 39.863333, '石家庄市桥西区', 114.514793, 38.042307, 280.00, 300, 1);

-- 初始化预警规则
INSERT INTO alert_rules (rule_code, rule_name, rule_type, rule_category, rule_condition, threshold_value, comparison_operator, alert_level, rule_description, is_enabled) VALUES
('RULE_TIMEOUT_DEFAULT', '默认超时预警规则', 'timeout', 'global', '{"field": "delay_minutes"}', 30, '>', 'warning', '当运输任务延迟超过30分钟时触发预警', 1),
('RULE_TEMP_FRUIT', '水果温度预警规则', 'temperature', 'product_type', '{"product_type": "fruit"}', 8, '>', 'error', '水果类产品温度超过8℃时触发预警', 1),
('RULE_TEMP_MEAT', '肉类温度预警规则', 'temperature', 'product_type', '{"product_type": "meat"}', 4, '>', 'critical', '肉类产品温度超过4℃时触发预警', 1);

-- 初始化系统配置
INSERT INTO sys_config (config_key, config_value, config_type, config_group, description) VALUES
('system.company_name', 'Fresh Logistics', 'string', 'system', '公司名称'),
('alert.timeout.default_threshold', '30', 'number', 'alert', '默认超时预警阈值(分钟)'),
('map.api_key', 'your_amap_api_key_here', 'string', 'map', '高德地图API密钥');

SELECT 'UTF-8数据导入完成' as Status;
