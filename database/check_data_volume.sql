-- ================================================================
-- Fresh Logistics 数据量检查脚本
-- 用途: 检查所有表的数据量，识别需要补充数据的表
-- 使用: mysql -u root -p123456 freshlogistics < database/check_data_volume.sql
-- ================================================================

USE freshlogistics;

-- 设置输出格式
SET @separator = '================================================================';

SELECT @separator AS '';
SELECT 'Fresh Logistics 数据库数据量检查报告' AS '报告标题';
SELECT NOW() AS '检查时间';
SELECT @separator AS '';

-- 1. 用户权限管理模块
SELECT @separator AS '';
SELECT '1. 用户权限管理模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'sys_user' AS '表名',
    COUNT(*) AS '记录数',
    '用户表' AS '说明'
FROM sys_user
UNION ALL
SELECT 
    'sys_role' AS '表名',
    COUNT(*) AS '记录数',
    '角色表' AS '说明'
FROM sys_role
UNION ALL
SELECT 
    'sys_permission' AS '表名',
    COUNT(*) AS '记录数',
    '权限表' AS '说明'
FROM sys_permission
UNION ALL
SELECT 
    'sys_user_role' AS '表名',
    COUNT(*) AS '记录数',
    '用户角色关联表' AS '说明'
FROM sys_user_role
UNION ALL
SELECT 
    'sys_role_permission' AS '表名',
    COUNT(*) AS '记录数',
    '角色权限关联表' AS '说明'
FROM sys_role_permission;

-- 2. 供应商模块
SELECT @separator AS '';
SELECT '2. 供应商模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'suppliers' AS '表名',
    COUNT(*) AS '记录数',
    '供应商表' AS '说明'
FROM suppliers
UNION ALL
SELECT 
    'products' AS '表名',
    COUNT(*) AS '记录数',
    '产品表' AS '说明'
FROM products
UNION ALL
SELECT 
    'supplier_products' AS '表名',
    COUNT(*) AS '记录数',
    '供应商产品关联表' AS '说明'
FROM supplier_products;

-- 3. 物流模块
SELECT @separator AS '';
SELECT '3. 物流模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'logistics_companies' AS '表名',
    COUNT(*) AS '记录数',
    '物流公司表' AS '说明'
FROM logistics_companies
UNION ALL
SELECT 
    'vehicles' AS '表名',
    COUNT(*) AS '记录数',
    '车辆表' AS '说明'
FROM vehicles
UNION ALL
SELECT 
    'drivers' AS '表名',
    COUNT(*) AS '记录数',
    '司机表' AS '说明'
FROM drivers
UNION ALL
SELECT 
    'transports' AS '表名',
    COUNT(*) AS '记录数',
    '运输任务表' AS '说明'
FROM transports
UNION ALL
SELECT 
    'transport_routes' AS '表名',
    COUNT(*) AS '记录数',
    '运输路线表' AS '说明'
FROM transport_routes;

-- 4. 订单模块
SELECT @separator AS '';
SELECT '4. 订单模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'orders' AS '表名',
    COUNT(*) AS '记录数',
    '订单表' AS '说明'
FROM orders
UNION ALL
SELECT 
    'order_items' AS '表名',
    COUNT(*) AS '记录数',
    '订单明细表' AS '说明'
FROM order_items
UNION ALL
SELECT 
    'logistics_orders' AS '表名',
    COUNT(*) AS '记录数',
    '物流订单表' AS '说明'
FROM logistics_orders;

-- 5. 预警模块
SELECT @separator AS '';
SELECT '5. 预警模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'alert_rules' AS '表名',
    COUNT(*) AS '记录数',
    '预警规则表' AS '说明'
FROM alert_rules
UNION ALL
SELECT 
    'alert_records' AS '表名',
    COUNT(*) AS '记录数',
    '预警记录表' AS '说明'
FROM alert_records;

-- 6. 传感器数据模块
SELECT @separator AS '';
SELECT '6. 传感器数据模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'sensor_data' AS '表名',
    COUNT(*) AS '记录数',
    '传感器数据表' AS '说明'
FROM sensor_data
UNION ALL
SELECT 
    'temperature_logs' AS '表名',
    COUNT(*) AS '记录数',
    '温度记录表' AS '说明'
FROM temperature_logs;

-- 7. 监管模块
SELECT @separator AS '';
SELECT '7. 监管模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'compliance_checks' AS '表名',
    COUNT(*) AS '记录数',
    '合规检查表' AS '说明'
FROM compliance_checks
UNION ALL
SELECT 
    'regulator_reports' AS '表名',
    COUNT(*) AS '记录数',
    '监管报告表' AS '说明'
FROM regulator_reports;

-- 8. 采购商模块
SELECT @separator AS '';
SELECT '8. 采购商模块' AS '模块名称';
SELECT @separator AS '';

SELECT 
    'purchasers' AS '表名',
    COUNT(*) AS '记录数',
    '采购商表' AS '说明'
FROM purchasers
UNION ALL
SELECT 
    'supplier_evaluations' AS '表名',
    COUNT(*) AS '记录数',
    '供应商评价表' AS '说明'
FROM supplier_evaluations;

-- 9. 数据汇总
SELECT @separator AS '';
SELECT '数据量汇总统计' AS '汇总标题';
SELECT @separator AS '';

SELECT 
    '核心业务数据总计' AS '类型',
    (SELECT COUNT(*) FROM suppliers) + 
    (SELECT COUNT(*) FROM products) + 
    (SELECT COUNT(*) FROM orders) + 
    (SELECT COUNT(*) FROM vehicles) AS '总记录数'
UNION ALL
SELECT 
    '用户权限数据总计' AS '类型',
    (SELECT COUNT(*) FROM sys_user) + 
    (SELECT COUNT(*) FROM sys_role) + 
    (SELECT COUNT(*) FROM sys_permission) AS '总记录数'
UNION ALL
SELECT 
    '监控数据总计' AS '类型',
    (SELECT COUNT(*) FROM sensor_data) + 
    (SELECT COUNT(*) FROM temperature_logs) + 
    (SELECT COUNT(*) FROM alert_records) AS '总记录数'
UNION ALL
SELECT 
    '监管数据总计' AS '类型',
    (SELECT COUNT(*) FROM compliance_checks) + 
    (SELECT COUNT(*) FROM regulator_reports) AS '总记录数';

-- 10. 识别数据不足的表（记录数 < 10）
SELECT @separator AS '';
SELECT '需要补充数据的表（记录数 < 10）' AS '提示';
SELECT @separator AS '';

SELECT * FROM (
    SELECT 'suppliers' AS '表名', COUNT(*) AS '当前记录数', '建议至少10条' AS '建议' FROM suppliers WHERE (SELECT COUNT(*) FROM suppliers) < 10
    UNION ALL
    SELECT 'products' AS '表名', COUNT(*) AS '当前记录数', '建议至少20条' AS '建议' FROM products WHERE (SELECT COUNT(*) FROM products) < 20
    UNION ALL
    SELECT 'vehicles' AS '表名', COUNT(*) AS '当前记录数', '建议至少10条' AS '建议' FROM vehicles WHERE (SELECT COUNT(*) FROM vehicles) < 10
    UNION ALL
    SELECT 'drivers' AS '表名', COUNT(*) AS '当前记录数', '建议至少10条' AS '建议' FROM drivers WHERE (SELECT COUNT(*) FROM drivers) < 10
    UNION ALL
    SELECT 'orders' AS '表名', COUNT(*) AS '当前记录数', '建议至少30条' AS '建议' FROM orders WHERE (SELECT COUNT(*) FROM orders) < 30
    UNION ALL
    SELECT 'alert_records' AS '表名', COUNT(*) AS '当前记录数', '建议至少20条' AS '建议' FROM alert_records WHERE (SELECT COUNT(*) FROM alert_records) < 20
    UNION ALL
    SELECT 'sensor_data' AS '表名', COUNT(*) AS '当前记录数', '建议至少50条' AS '建议' FROM sensor_data WHERE (SELECT COUNT(*) FROM sensor_data) < 50
    UNION ALL
    SELECT 'temperature_logs' AS '表名', COUNT(*) AS '当前记录数', '建议至少100条' AS '建议' FROM temperature_logs WHERE (SELECT COUNT(*) FROM temperature_logs) < 100
    UNION ALL
    SELECT 'compliance_checks' AS '表名', COUNT(*) AS '当前记录数', '建议至少15条' AS '建议' FROM compliance_checks WHERE (SELECT COUNT(*) FROM compliance_checks) < 15
    UNION ALL
    SELECT 'transports' AS '表名', COUNT(*) AS '当前记录数', '建议至少20条' AS '建议' FROM transports WHERE (SELECT COUNT(*) FROM transports) < 20
) AS need_data WHERE `当前记录数` > 0 OR `表名` IS NOT NULL;

SELECT @separator AS '';
SELECT '检查完成！' AS '状态';
SELECT @separator AS '';



