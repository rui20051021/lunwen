@echo off
chcp 65001 > nul
echo 正在更新车辆温度数据...
"C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -hlocalhost -P3306 -uroot -p123456 freshlogistics < update_temp_data.sql
echo.
echo ✅ 更新完成！
pause

