package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册控制器
 * 实现真实的数据库注册功能
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RegisterController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 用户注册
     * 将用户信息存入数据库并自动分配角色
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> registerRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取注册数据
            String username = (String) registerRequest.get("username");
            String password = (String) registerRequest.get("password");
            String realName = (String) registerRequest.get("realName");
            String userType = (String) registerRequest.get("userType");
            String email = (String) registerRequest.get("email");
            String phone = (String) registerRequest.get("phone");
            
            // 1. 数据验证
            if (username == null || username.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "用户名不能为空");
                return result;
            }
            
            if (password == null || password.length() < 6) {
                result.put("code", 400);
                result.put("message", "密码长度至少6位");
                return result;
            }
            
            if (realName == null || realName.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "真实姓名不能为空");
                return result;
            }
            
            if (userType == null || userType.trim().isEmpty()) {
                result.put("code", 400);
                result.put("message", "用户类型不能为空");
                return result;
            }
            
            // 2. 检查用户名是否已存在
            String checkUsernameSql = "SELECT COUNT(*) FROM sys_user WHERE username = ?";
            Integer usernameCount = jdbcTemplate.queryForObject(checkUsernameSql, Integer.class, username);
            if (usernameCount != null && usernameCount > 0) {
                result.put("code", 400);
                result.put("message", "用户名已存在，请更换");
                return result;
            }
            
            // 3. 检查邮箱是否已存在
            if (email != null && !email.trim().isEmpty()) {
                String checkEmailSql = "SELECT COUNT(*) FROM sys_user WHERE email = ?";
                Integer emailCount = jdbcTemplate.queryForObject(checkEmailSql, Integer.class, email);
                if (emailCount != null && emailCount > 0) {
                    result.put("code", 400);
                    result.put("message", "该邮箱已被注册");
                    return result;
                }
            }
            
            // 4. 检查手机号是否已存在
            if (phone != null && !phone.trim().isEmpty()) {
                String checkPhoneSql = "SELECT COUNT(*) FROM sys_user WHERE phone = ?";
                Integer phoneCount = jdbcTemplate.queryForObject(checkPhoneSql, Integer.class, phone);
                if (phoneCount != null && phoneCount > 0) {
                    result.put("code", 400);
                    result.put("message", "该手机号已被注册");
                    return result;
                }
            }
            
            // 5. 插入用户数据到sys_user表
            String insertUserSql = "INSERT INTO sys_user (username, password, real_name, user_type, " +
                                 "email, phone, status, created_at, updated_at) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, 1, NOW(), NOW())";
            
            jdbcTemplate.update(insertUserSql, username, password, realName, userType, email, phone);
            
            // 6. 获取新插入用户的ID
            String getUserIdSql = "SELECT id FROM sys_user WHERE username = ?";
            Long userId = jdbcTemplate.queryForObject(getUserIdSql, Long.class, username);
            
            // 7. 根据用户类型自动分配角色
            String getRoleIdSql = "SELECT id FROM sys_role WHERE role_code = ?";
            Long roleId = jdbcTemplate.queryForObject(getRoleIdSql, Long.class, userType);
            
            if (roleId != null) {
                String insertUserRoleSql = "INSERT INTO sys_user_role (user_id, role_id, created_at) " +
                                         "VALUES (?, ?, NOW())";
                jdbcTemplate.update(insertUserRoleSql, userId, roleId);
            }
            
            // 8. 返回成功结果
            result.put("code", 200);
            result.put("message", "注册成功！请登录");
            result.put("data", Map.of(
                "userId", userId,
                "username", username,
                "userType", userType,
                "redirectPath", getDashboardPath(userType)
            ));
            
            System.out.println("✅ 用户注册成功: " + username + " (类型: " + userType + ")");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败: " + e.getMessage());
            System.err.println("🔴 用户注册失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT COUNT(*) FROM sys_user WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            
            boolean available = (count == null || count == 0);
            
            result.put("code", 200);
            result.put("message", available ? "用户名可用" : "用户名已存在");
            result.put("data", available);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "检查失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 根据用户类型获取Dashboard路径
     */
    private String getDashboardPath(String userType) {
        switch (userType) {
            case "admin":
                return "/admin/dashboard";
            case "supplier":
                return "/supplier/dashboard";
            case "logistics":
                return "/logistics/dashboard";
            case "purchaser":
                return "/purchaser/dashboard";
            case "regulator":
                return "/regulator/dashboard";
            default:
                return "/";
        }
    }
}

