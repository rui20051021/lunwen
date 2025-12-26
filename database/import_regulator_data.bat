@echo off
chcp 65001 > nul

echo ================================
echo Fresh Logistics 监管员数据导入
echo ================================
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

echo [2/3] 导入监管员功能数据...
%MYSQL_PATH% -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% < init_regulator_data.sql
if %errorlevel% neq 0 (
    echo ❌ 数据导入失败！
    pause
    exit /b 1
)
echo ✅ 监管员数据导入成功
echo.

echo [3/3] 验证导入结果...
echo.
echo 合规检查记录统计：
%MYSQL_PATH% -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% -e "SELECT COUNT(*) as '总数', COUNT(CASE WHEN compliance_status='passed' THEN 1 END) as '通过', COUNT(CASE WHEN compliance_status='failed' THEN 1 END) as '失败', ROUND(AVG(compliance_score), 1) as '平均评分' FROM compliance_checks;"
echo.

echo 监管报告统计：
%MYSQL_PATH% -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 %DB_NAME% -e "SELECT COUNT(*) as '总数', COUNT(CASE WHEN report_status='published' THEN 1 END) as '已发布', COUNT(CASE WHEN report_status='reviewing' THEN 1 END) as '审核中', COUNT(CASE WHEN report_status='draft' THEN 1 END) as '草稿' FROM regulator_reports;"
echo.

echo ================================
echo 🎉 监管员数据导入完成！
echo ================================
echo.
echo 导入的数据：
echo   - 合规检查记录: 6条
echo   - 监管报告: 4条
echo.
echo 现在可以访问系统查看监管员功能:
echo   http://localhost:5173/regulator/compliance
echo   http://localhost:5173/regulator/reports
echo.
pause
