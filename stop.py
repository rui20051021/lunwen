#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Fresh Logistics 停止服务脚本（简化版）
"""

import subprocess
import platform

def main():
    print("\n" + "="*50)
    print("    停止 Fresh Logistics 服务")
    print("="*50 + "\n")
    
    is_windows = platform.system() == "Windows"
    
    print("正在停止服务...\n")
    
    if is_windows:
        # Windows系统
        print("停止后端服务（端口8080）...")
        subprocess.run('netstat -ano | findstr :8080', shell=True, capture_output=True)
        subprocess.run('for /f "tokens=5" %a in (\'netstat -ano ^| findstr :8080\') do taskkill /F /PID %a', shell=True, capture_output=True)
        
        print("停止前端服务（端口5173）...")
        subprocess.run('for /f "tokens=5" %a in (\'netstat -ano ^| findstr :5173\') do taskkill /F /PID %a', shell=True, capture_output=True)
        
        print("停止所有Java进程（Maven）...")
        subprocess.run('taskkill /F /IM java.exe', shell=True, capture_output=True)
        
        print("停止所有Node进程...")
        subprocess.run('taskkill /F /IM node.exe', shell=True, capture_output=True)
    else:
        # Linux/Mac系统
        print("停止后端服务（端口8080）...")
        subprocess.run('lsof -ti:8080 | xargs kill -9', shell=True, capture_output=True)
        
        print("停止前端服务（端口5173）...")
        subprocess.run('lsof -ti:5173 | xargs kill -9', shell=True, capture_output=True)
    
    print("\n" + "="*50)
    print("    ✅ 所有服务已停止")
    print("="*50 + "\n")
    
    print("按回车键退出...")
    input()

if __name__ == "__main__":
    main()
