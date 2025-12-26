-- ================================================================
-- 为vehicles表添加温度字段并设置真实数据
-- ================================================================

USE freshlogistics;

-- 1. 添加current_temp字段
ALTER TABLE vehicles 
ADD COLUMN IF NOT EXISTS current_temp DECIMAL(4,1) DEFAULT 2.5 COMMENT '当前温度(℃)';

-- 2. 添加current_humidity字段
ALTER TABLE vehicles 
ADD COLUMN IF NOT EXISTS current_humidity DECIMAL(5,2) DEFAULT 85.0 COMMENT '当前湿度(%)';

-- 3. 为现有车辆设置真实温度数据（基于实际冷链物流场景）
-- 冷藏车辆温度范围：0-8℃，湿度：80-90%

-- 京A12345 (VEH20250927001) - 正常冷藏温度
UPDATE vehicles SET current_temp = 2.5, current_humidity = 85.0 WHERE vehicle_code = 'VEH20250927001';

-- 京B67890 (VEH20250927002) - 正常冷藏温度
UPDATE vehicles SET current_temp = 4.0, current_humidity = 82.0 WHERE vehicle_code = 'VEH20250927002';

-- 京C54321 (VEH20250927003) - 稍高温度
UPDATE vehicles SET current_temp = 5.5, current_humidity = 88.0 WHERE vehicle_code = 'VEH20250927003';

-- 津A11111 (VEH20250927004) - 低温冷藏
UPDATE vehicles SET current_temp = 1.5, current_humidity = 83.0 WHERE vehicle_code = 'VEH20250927004';

-- 津B22222 (VEH20250927005) - 中温冷藏
UPDATE vehicles SET current_temp = 3.8, current_humidity = 86.5 WHERE vehicle_code = 'VEH20250927005';

-- 4. 验证更新结果
SELECT 
    vehicle_code AS '车辆编码',
    license_plate AS '车牌号',
    current_temp AS '当前温度(℃)',
    current_humidity AS '当前湿度(%)',
    vehicle_status AS '状态'
FROM vehicles
WHERE vehicle_code LIKE 'VEH%'
ORDER BY vehicle_code;

SELECT '✅ 车辆温度字段添加完成！' AS '状态';

