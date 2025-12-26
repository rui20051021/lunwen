@echo off
chcp 65001 > nul

echo ================================
echo Fresh Logistics 数据库连接
echo ================================

set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe"

echo 正在连接到Fresh Logistics数据库...
echo.
echo 连接信息：
echo   数据库: freshlogistics
echo   字符集: utf8mb4
echo   用户: root
echo.
echo 默认管理员账户：
echo   用户名: admin
echo   密码: admin123
echo.

%MYSQL_PATH% -hlocalhost -P3306 -uroot -p123456 --default-character-set=utf8mb4 freshlogistics
