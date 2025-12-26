@echo off
chcp 65001 > nul

echo ========================================
echo Fresh Logistics 数据库数据扩充
echo ========================================
echo.

set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe"
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=123456
set DB_NAME=freshlogistics

echo [1/3] 检查MySQL连接...
%MYSQL_PATH% -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "SELECT VERSION();" 2>nul
if %errorlevel% neq 0 (
    echo ❌ MySQL连接失败！
    pause
    exit /b 1
)
echo ✅ MySQL连接成功
echo.

echo [2/3] 执行数据扩充...
echo 正在为所有表添加更多数据记录...
%MYSQL_PATH% -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% < expand_all_data.sql
if %errorlevel% neq 0 (
    echo ❌ 数据扩充失败！
    pause
    exit /b 1
)
echo ✅ 数据扩充成功
echo.

echo [3/3] 验证扩充结果...
echo.
echo 📊 各表数据统计：
%MYSQL_PATH% -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% -e "SELECT 'sys_user' as '表名', COUNT(*) as '记录数' FROM sys_user UNION ALL SELECT 'products', COUNT(*) FROM products UNION ALL SELECT 'orders', COUNT(*) FROM orders UNION ALL SELECT 'vehicles', COUNT(*) FROM vehicles UNION ALL SELECT 'drivers', COUNT(*) FROM drivers UNION ALL SELECT 'temperature_logs', COUNT(*) FROM temperature_logs UNION ALL SELECT 'alert_records', COUNT(*) FROM alert_records UNION ALL SELECT 'compliance_checks', COUNT(*) FROM compliance_checks UNION ALL SELECT 'supplier_evaluations', COUNT(*) FROM supplier_evaluations ORDER BY 记录数 DESC;"
echo.

echo ========================================
echo 🎉 数据扩充完成！
echo ========================================
echo.
echo 扩充的数据：
echo   - 用户: 新增5个
echo   - 产品: 新增10个  
echo   - 订单: 新增5个
echo   - 车辆: 新增5辆
echo   - 司机: 新增5人
echo   - 温度记录: 新增13条
echo   - 预警记录: 新增4条
echo   - 合规检查: 新增8条
echo   - 供应商评价: 新增7条
echo.
echo 现在访问系统查看扩充后的数据
echo.
pause
