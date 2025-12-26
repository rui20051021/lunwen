@echo off
chcp 65001 > nul

echo ================================
echo Fresh Logistics 后端服务启动
echo ================================

rem 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java环境未配置，请安装Java 17+
    pause
    exit /b 1
)

rem 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven环境未配置，请安装Maven 3.6+
    pause
    exit /b 1
)

echo ✅ Java和Maven环境检查通过

echo.
echo [1/3] 清理和编译项目...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo ❌ 项目编译失败！
    pause
    exit /b 1
)

echo ✅ 项目编译成功

echo.
echo [2/3] 检查数据库连接...
mysql -hlocalhost -P3306 -uroot -p123456 -e "USE freshlogistics; SELECT 'Database OK' as Status;" 2>nul
if %errorlevel% neq 0 (
    echo ❌ 数据库连接失败！请确保：
    echo   - MySQL服务已启动
    echo   - freshlogistics数据库已创建
    echo   - 连接参数正确（root/123456）
    pause
    exit /b 1
)

echo ✅ 数据库连接正常

echo.
echo [3/3] 启动Spring Boot应用...
echo.
echo ================================
echo 🚀 启动中...
echo ================================
echo 应用将在以下地址启动：
echo   后端API: http://localhost:8080/api
echo   API文档: http://localhost:8080/api/swagger-ui.html
echo   健康检查: http://localhost:8080/api/actuator/health
echo   Druid监控: http://localhost:8080/api/druid
echo.
echo 按 Ctrl+C 停止服务
echo ================================

rem 启动Spring Boot应用
call mvn spring-boot:run
