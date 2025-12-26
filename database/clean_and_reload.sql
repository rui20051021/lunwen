-- ================================================================
-- 清理并重新加载正确的中文数据
-- ================================================================

USE freshlogistics;

-- 清理现有数据（保留表结构）
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM sys_user;
DELETE FROM sys_role;
DELETE FROM sys_permission;
DELETE FROM products;
DELETE FROM suppliers;
DELETE FROM drivers;
DELETE FROM vehicles;
DELETE FROM transport_routes;
DELETE FROM alert_rules;
DELETE FROM product_categories;
DELETE FROM sys_config;

-- 重置自增ID
ALTER TABLE sys_user AUTO_INCREMENT = 1;
ALTER TABLE sys_role AUTO_INCREMENT = 1;
ALTER TABLE sys_permission AUTO_INCREMENT = 1;
ALTER TABLE products AUTO_INCREMENT = 1;
ALTER TABLE suppliers AUTO_INCREMENT = 1;
ALTER TABLE drivers AUTO_INCREMENT = 1;
ALTER TABLE vehicles AUTO_INCREMENT = 1;
ALTER TABLE transport_routes AUTO_INCREMENT = 1;
ALTER TABLE alert_rules AUTO_INCREMENT = 1;
ALTER TABLE product_categories AUTO_INCREMENT = 1;
ALTER TABLE sys_config AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Data cleaned successfully' as Status;
