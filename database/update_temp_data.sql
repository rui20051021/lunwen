-- 更新vehicles表的真实温度数据

USE freshlogistics;

-- 设置真实温度数据（基于实际冷链物流场景：0-8℃正常范围）
UPDATE vehicles SET current_temp = 2.5, current_humidity = 85.0 WHERE vehicle_code = 'VEH20250927001';
UPDATE vehicles SET current_temp = 4.0, current_humidity = 82.0 WHERE vehicle_code = 'VEH20250927002';
UPDATE vehicles SET current_temp = 5.5, current_humidity = 88.0 WHERE vehicle_code = 'VEH20250927003';
UPDATE vehicles SET current_temp = 1.5, current_humidity = 83.0 WHERE vehicle_code = 'VEH20250927004';
UPDATE vehicles SET current_temp = 3.8, current_humidity = 86.5 WHERE vehicle_code = 'VEH20250927005';

-- 为所有其他车辆设置默认真实温度（如果有）
UPDATE vehicles 
SET 
    current_temp = CASE 
        WHEN MOD(id, 5) = 0 THEN 2.5
        WHEN MOD(id, 5) = 1 THEN 3.8
        WHEN MOD(id, 5) = 2 THEN 1.5
        WHEN MOD(id, 5) = 3 THEN 4.2
        ELSE 3.0
    END,
    current_humidity = CASE 
        WHEN MOD(id, 3) = 0 THEN 85.0
        WHEN MOD(id, 3) = 1 THEN 82.5
        ELSE 88.0
    END
WHERE current_temp IS NULL OR current_temp = 0;

-- 验证结果
SELECT 
    vehicle_code AS '车辆编码',
    license_plate AS '车牌号',
    current_temp AS '当前温度(℃)',
    current_humidity AS '当前湿度(%)',
    vehicle_status AS '状态'
FROM vehicles
ORDER BY id;

