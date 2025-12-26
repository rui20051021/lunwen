-- 为vehicles表添加温度字段并设置真实数据

USE freshlogistics;

-- 1. 添加current_temp字段
ALTER TABLE vehicles ADD COLUMN current_temp DECIMAL(4,1) DEFAULT 2.5 COMMENT '当前温度(℃)';

-- 2. 添加current_humidity字段
ALTER TABLE vehicles ADD COLUMN current_humidity DECIMAL(5,2) DEFAULT 85.0 COMMENT '当前湿度(%)';

-- 3. 设置真实温度数据（基于实际冷链物流场景：0-8℃）
UPDATE vehicles SET current_temp = 2.5, current_humidity = 85.0 WHERE vehicle_code = 'VEH20250927001';
UPDATE vehicles SET current_temp = 4.0, current_humidity = 82.0 WHERE vehicle_code = 'VEH20250927002';
UPDATE vehicles SET current_temp = 5.5, current_humidity = 88.0 WHERE vehicle_code = 'VEH20250927003';
UPDATE vehicles SET current_temp = 1.5, current_humidity = 83.0 WHERE vehicle_code = 'VEH20250927004';
UPDATE vehicles SET current_temp = 3.8, current_humidity = 86.5 WHERE vehicle_code = 'VEH20250927005';

-- 4. 验证结果
SELECT vehicle_code, license_plate, current_temp, current_humidity, vehicle_status FROM vehicles;

