-- ================================================================
-- Fresh Logistics 数据库快速部署脚本
-- 使用说明: mysql -u root -p < database/setup.sql
-- 注意: 请确保 MySQL 8.0+ 环境
-- ================================================================

-- 设置SQL模式和字符集
SET SQL_MODE = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 显示开始信息
SELECT 'Fresh Logistics 数据库初始化开始...' AS Message;

-- 删除已存在的数据库（谨慎使用）
-- DROP DATABASE IF EXISTS freshlogistics;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS freshlogistics 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE freshlogistics;

SELECT 'Step 1: 创建数据库结构...' AS Message;

-- 导入表结构（这里需要包含 schema.sql 的内容，或者使用 SOURCE 命令）
SOURCE database/schema.sql;

SELECT 'Step 2: 初始化基础数据...' AS Message;

-- 导入初始化数据（这里需要包含 init_data.sql 的内容，或者使用 SOURCE 命令）
SOURCE database/init_data.sql;

-- 显示统计信息
SELECT 'Step 3: 验证数据库创建...' AS Message;

-- 显示所有表
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '记录数',
    ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) as '大小(MB)',
    TABLE_COMMENT as '说明'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'freshlogistics'
ORDER BY TABLE_NAME;

-- 显示用户统计
SELECT 
    '用户数量' as '统计项',
    COUNT(*) as '数量'
FROM sys_user
UNION ALL
SELECT 
    '角色数量' as '统计项',
    COUNT(*) as '数量'
FROM sys_role
UNION ALL
SELECT 
    '权限数量' as '统计项',
    COUNT(*) as '数量'
FROM sys_permission
UNION ALL
SELECT 
    '产品数量' as '统计项',
    COUNT(*) as '数量'
FROM products
UNION ALL
SELECT 
    '供应商数量' as '统计项',
    COUNT(*) as '数量'
FROM suppliers
UNION ALL
SELECT 
    '车辆数量' as '统计项',
    COUNT(*) as '数量'
FROM vehicles
UNION ALL
SELECT 
    '司机数量' as '统计项',
    COUNT(*) as '数量'
FROM drivers
UNION ALL
SELECT 
    '预警规则数量' as '统计项',
    COUNT(*) as '数量'
FROM alert_rules;

-- 显示默认管理员信息
SELECT 
    '默认管理员账户信息：' as '提示',
    '用户名: admin, 密码: admin123' as '登录信息';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Fresh Logistics 数据库初始化完成！' AS Message;

-- ================================================================
-- 常用查询示例
-- ================================================================

-- 查看用户权限分配
/*
SELECT 
    u.username as '用户名',
    u.real_name as '真实姓名',
    u.user_type as '用户类型',
    GROUP_CONCAT(r.role_name) as '角色'
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
WHERE u.deleted_at IS NULL
GROUP BY u.id;
*/

-- 查看产品冷链要求
/*
SELECT 
    p.product_code as '产品编码',
    p.product_name as '产品名称',
    p.product_type as '产品类型',
    CONCAT(p.min_temp, '~', p.max_temp, '℃') as '温度要求',
    CONCAT(p.min_humidity, '~', p.max_humidity, '%') as '湿度要求',
    p.shelf_life as '保质期(天)'
FROM products p
WHERE p.deleted_at IS NULL
ORDER BY p.product_type, p.product_code;
*/

-- 查看车辆设备配置
/*
SELECT 
    v.vehicle_code as '车辆编码',
    v.license_plate as '车牌号',
    v.vehicle_type as '车辆类型',
    v.load_capacity as '载重(吨)',
    v.volume_capacity as '容积(m³)',
    v.gps_device_id as 'GPS设备',
    v.temperature_sensor_id as '温度传感器',
    v.vehicle_status as '状态'
FROM vehicles v
WHERE v.deleted_at IS NULL
ORDER BY v.vehicle_code;
*/

-- 查看预警规则配置
/*
SELECT 
    ar.rule_code as '规则编码',
    ar.rule_name as '规则名称',
    ar.rule_type as '规则类型',
    ar.alert_level as '预警级别',
    ar.threshold_value as '阈值',
    ar.comparison_operator as '比较符',
    CASE ar.is_enabled WHEN 1 THEN '启用' ELSE '禁用' END as '状态'
FROM alert_rules ar
ORDER BY ar.rule_type, ar.alert_level;
*/
