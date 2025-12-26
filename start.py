#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Fresh Logistics 一键启动脚本（简化版）
"""

import os
import subprocess
import platform
import time
import webbrowser

def main():
    print("\n" + "="*50)
    print("    Fresh Logistics 一键启动")
    print("="*50 + "\n")
    
    # 获取当前目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    backend_dir = os.path.join(current_dir, "backend")
    frontend_dir = os.path.join(current_dir, "frontend")
    
    # 判断操作系统
    is_windows = platform.system() == "Windows"
    
    print("[1/2] 启动后端服务...")
    if is_windows:
        # Windows
        subprocess.Popen(
            f'start "后端服务" cmd /k "cd /d {backend_dir} && mvn spring-boot:run"',
            shell=True
        )
    else:
        # Linux/Mac
        subprocess.Popen(
            f'cd {backend_dir} && mvn spring-boot:run',
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
    
    print("等待后端启动（15秒）...")
    time.sleep(15)
    
    print("\n[2/2] 启动前端服务...")
    if is_windows:
        # Windows
        subprocess.Popen(
            f'start "前端服务" cmd /k "cd /d {frontend_dir} && npm run dev"',
            shell=True
        )
    else:
        # Linux/Mac
        subprocess.Popen(
            f'cd {frontend_dir} && npm run dev',
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
    
    print("等待前端启动（5秒）...")
    time.sleep(5)
    
    print("\n" + "="*50)
    print("    ✅ 启动完成！")
    print("="*50)
    print("\n🌐 前端: http://localhost:5173")
    print("🔧 后端: http://localhost:8080")
    print("\n测试账号:")
    print("  管理员: admin / admin123")
    print("  供应商: supplier01 / admin123")
    print("  物流商: logistics01 / admin123")
    print("  采购商: purchaser01 / admin123")
    print("  监管员: regulator01 / admin123")
    
    # 询问是否打开浏览器
    try:
        choice = input("\n是否打开浏览器？(y/n): ").strip().lower()
        if choice == 'y':
            print("正在打开浏览器...")
            time.sleep(2)
            webbrowser.open("http://localhost:5173")
    except:
        pass
    
    print("\n启动完成！按回车键退出...")
    input()

if __name__ == "__main__":
    main()
