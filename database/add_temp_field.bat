@echo off
chcp 65001 > nul

echo ================================
echo 添加车辆温度字段到数据库
echo ================================
echo.

set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe"

echo 正在执行SQL脚本...
%MYSQL_PATH% -hlocalhost -P3306 -uroot -p123456 freshlogistics < add_vehicle_temperature.sql

if %errorlevel% == 0 (
    echo.
    echo ================================
    echo ✅ 温度字段添加成功！
    echo ================================
) else (
    echo.
    echo ================================
    echo ❌ 添加失败，请检查错误信息
    echo ================================
)

echo.
pause

