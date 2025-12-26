@echo off
chcp 65001 > nul
"C:\Program Files\MySQL\MySQL Server 9.1\bin\mysql.exe" -hlocalhost -P3306 -uroot -p123456 freshlogistics < add_temp_fields.sql
pause

