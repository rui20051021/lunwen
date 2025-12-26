@echo off
chcp 65001 > nul
setlocal EnableDelayedExpansion

echo ================================
echo Fresh Logistics 数据库部署脚本
echo ================================
echo.

rem 设置数据库连接参数
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=123456
set DB_NAME=freshlogistics

echo [1/4] 检查MySQL连接...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "SELECT VERSION();" 2>nul
if !errorlevel! neq 0 (
    echo ❌ MySQL连接失败！请检查MySQL服务和连接参数
    echo 请确认：
    echo   - MySQL服务已启动
    echo   - 用户名密码正确: %DB_USER%/%DB_PASSWORD%
    echo   - 端口可访问: %DB_HOST%:%DB_PORT%
    pause
    exit /b 1
)
echo ✅ MySQL连接成功

echo.
echo [2/4] 创建数据库和表结构...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < schema.sql
if !errorlevel! neq 0 (
    echo ❌ 数据库结构创建失败！
    pause
    exit /b 1
)
echo ✅ 数据库结构创建成功

echo.
echo [3/4] 初始化数据...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% < init_data.sql
if !errorlevel! neq 0 (
    echo ❌ 数据初始化失败！
    pause
    exit /b 1
)
echo ✅ 数据初始化成功

echo.
echo [4/4] 验证部署结果...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% -e "SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema='%DB_NAME%';"
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% -e "SELECT 'Users:' as Type, COUNT(*) as Count FROM sys_user UNION ALL SELECT 'Roles:', COUNT(*) FROM sys_role UNION ALL SELECT 'Products:', COUNT(*) FROM products UNION ALL SELECT 'Vehicles:', COUNT(*) FROM vehicles;"

echo.
echo ================================
echo 🎉 部署完成！
echo ================================
echo 数据库信息：
echo   数据库名: %DB_NAME%
echo   字符集: utf8mb4
echo   主机: %DB_HOST%:%DB_PORT%
echo   用户: %DB_USER%
echo.
echo 默认管理员账户：
echo   用户名: admin
echo   密码: admin123
echo.
echo 可以使用以下命令连接数据库：
echo mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME%
echo.
pause
