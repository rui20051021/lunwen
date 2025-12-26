@echo off
chcp 65001 > nul

echo ================================
echo 添加车辆温度字段（真实数据）
echo ================================
echo.

"C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -hlocalhost -P3306 -uroot -p123456 freshlogistics < add_real_temperature.sql

echo.
echo ✅ 执行完成！
echo.
pause

