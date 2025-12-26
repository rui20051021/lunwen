package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库用户数据控制器 - 直接使用JdbcTemplate连接真实数据库
 */
@RestController
@RequestMapping("/database/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseUserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 从数据库验证用户登录
     */
    @PostMapping("/validate")
    public Map<String, Object> validateUserFromDatabase(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            // 📊 直接查询数据库验证用户
            String sql = "SELECT u.*, r.role_code as user_type " +
                        "FROM sys_user u " +
                        "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
                        "LEFT JOIN sys_role r ON ur.role_id = r.id " +
                        "WHERE u.username = ? AND u.password = ? AND u.status = 1";
            
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username, password);
            
            if (!users.isEmpty()) {
                Map<String, Object> user = users.get(0);
                
                // 更新最后登录时间
                jdbcTemplate.update("UPDATE sys_user SET last_login_time = NOW() WHERE id = ?", 
                                   user.get("id"));
                
                result.put("code", 200);
                result.put("message", "数据库验证成功");
                result.put("data", user);
                
                System.out.println("✅ 数据库登录成功: " + username + " -> " + user.get("real_name"));
                
            } else {
                result.put("code", 401);
                result.put("message", "用户名或密码错误");
                
                System.out.println("❌ 数据库登录失败: " + username);
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据库连接失败: " + e.getMessage());
            System.err.println("🔴 数据库连接错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取所有用户数据
     */
    @GetMapping("/all")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT u.*, r.role_code as user_type, r.role_name " +
                        "FROM sys_user u " +
                        "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
                        "LEFT JOIN sys_role r ON ur.role_id = r.id " +
                        "WHERE u.status = 1 " +
                        "ORDER BY u.id";
            
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功 - 真实数据库数据");
            result.put("data", users);
            
            System.out.println("✅ 成功获取 " + users.size() + " 个用户的数据库记录");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询用户数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_users, " +
                        "COUNT(CASE WHEN u.status = 1 THEN 1 END) as active_users, " +
                        "COUNT(CASE WHEN u.status = 0 THEN 1 END) as inactive_users, " +
                        "COUNT(CASE WHEN r.role_code = 'admin' THEN 1 END) as admin_count, " +
                        "COUNT(CASE WHEN r.role_code = 'supplier' THEN 1 END) as supplier_count, " +
                        "COUNT(CASE WHEN r.role_code = 'logistics' THEN 1 END) as logistics_count, " +
                        "COUNT(CASE WHEN r.role_code = 'purchaser' THEN 1 END) as purchaser_count, " +
                        "COUNT(CASE WHEN r.role_code = 'regulator' THEN 1 END) as regulator_count " +
                        "FROM sys_user u " +
                        "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
                        "LEFT JOIN sys_role r ON ur.role_id = r.id";
            
            Map<String, Object> stats = jdbcTemplate.queryForMap(sql);
            
            result.put("code", 200);
            result.put("message", "统计成功");
            result.put("data", stats);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "统计失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建新用户
     */
    @PostMapping("/create")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> userRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String username = (String) userRequest.get("username");
            String password = (String) userRequest.get("password");
            String realName = (String) userRequest.get("realName");
            String userType = (String) userRequest.get("userType");
            String email = (String) userRequest.get("email");
            String phone = (String) userRequest.get("phone");
            Integer status = userRequest.get("status") != null ? (Integer) userRequest.get("status") : 1;
            
            // 验证必填字段
            if (username == null || password == null || realName == null || userType == null) {
                result.put("code", 400);
                result.put("message", "用户名、密码、真实姓名和用户类型不能为空");
                return result;
            }
            
            // 检查用户名是否已存在
            String checkSql = "SELECT COUNT(*) FROM sys_user WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
            if (count != null && count > 0) {
                result.put("code", 400);
                result.put("message", "用户名已存在");
                return result;
            }
            
            // 检查邮箱是否已存在
            if (email != null && !email.isEmpty()) {
                String checkEmailSql = "SELECT COUNT(*) FROM sys_user WHERE email = ?";
                Integer emailCount = jdbcTemplate.queryForObject(checkEmailSql, Integer.class, email);
                if (emailCount != null && emailCount > 0) {
                    result.put("code", 400);
                    result.put("message", "邮箱已被使用");
                    return result;
                }
            }
            
            // 检查手机号是否已存在
            if (phone != null && !phone.isEmpty()) {
                String checkPhoneSql = "SELECT COUNT(*) FROM sys_user WHERE phone = ?";
                Integer phoneCount = jdbcTemplate.queryForObject(checkPhoneSql, Integer.class, phone);
                if (phoneCount != null && phoneCount > 0) {
                    result.put("code", 400);
                    result.put("message", "手机号已被使用");
                    return result;
                }
            }
            
            // 插入用户数据
            String insertSql = "INSERT INTO sys_user (username, password, real_name, user_type, email, phone, status, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            
            jdbcTemplate.update(insertSql, username, password, realName, userType, email, phone, status);
            
            // 获取插入的用户ID
            String getIdSql = "SELECT id FROM sys_user WHERE username = ?";
            Long userId = jdbcTemplate.queryForObject(getIdSql, Long.class, username);
            
            // 根据用户类型分配角色
            String getRoleIdSql = "SELECT id FROM sys_role WHERE role_code = ?";
            Long roleId = jdbcTemplate.queryForObject(getRoleIdSql, Long.class, userType);
            
            if (roleId != null) {
                String insertUserRoleSql = "INSERT INTO sys_user_role (user_id, role_id, created_at) VALUES (?, ?, NOW())";
                jdbcTemplate.update(insertUserRoleSql, userId, roleId);
            }
            
            result.put("code", 200);
            result.put("message", "用户创建成功");
            result.put("data", Map.of("id", userId, "username", username));
            
            System.out.println("✅ 成功创建用户: " + username);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            System.err.println("🔴 创建用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> userRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String realName = (String) userRequest.get("realName");
            String email = (String) userRequest.get("email");
            String phone = (String) userRequest.get("phone");
            Integer status = (Integer) userRequest.get("status");
            String userType = (String) userRequest.get("userType");
            
            // 检查用户是否存在
            String checkSql = "SELECT COUNT(*) FROM sys_user WHERE id = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
            if (count == null || count == 0) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 更新用户信息
            StringBuilder updateSql = new StringBuilder("UPDATE sys_user SET ");
            List<Object> params = new ArrayList<>();
            
            if (realName != null) {
                updateSql.append("real_name = ?, ");
                params.add(realName);
            }
            if (email != null) {
                updateSql.append("email = ?, ");
                params.add(email);
            }
            if (phone != null) {
                updateSql.append("phone = ?, ");
                params.add(phone);
            }
            if (status != null) {
                updateSql.append("status = ?, ");
                params.add(status);
            }
            if (userType != null) {
                updateSql.append("user_type = ?, ");
                params.add(userType);
            }
            
            updateSql.append("updated_at = NOW() WHERE id = ?");
            params.add(id);
            
            jdbcTemplate.update(updateSql.toString(), params.toArray());
            
            // 如果更新了用户类型，也更新角色关联
            if (userType != null) {
                // 删除旧的角色关联
                String deleteRoleSql = "DELETE FROM sys_user_role WHERE user_id = ?";
                jdbcTemplate.update(deleteRoleSql, id);
                
                // 添加新的角色关联
                String getRoleIdSql = "SELECT id FROM sys_role WHERE role_code = ?";
                Long roleId = jdbcTemplate.queryForObject(getRoleIdSql, Long.class, userType);
                
                if (roleId != null) {
                    String insertUserRoleSql = "INSERT INTO sys_user_role (user_id, role_id, created_at) VALUES (?, ?, NOW())";
                    jdbcTemplate.update(insertUserRoleSql, id, roleId);
                }
            }
            
            result.put("code", 200);
            result.put("message", "更新成功");
            
            System.out.println("✅ 成功更新用户ID: " + id);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
            System.err.println("🔴 更新用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查是否是管理员账号
            String checkAdminSql = "SELECT username FROM sys_user WHERE id = ?";
            String username = jdbcTemplate.queryForObject(checkAdminSql, String.class, id);
            
            if ("admin".equals(username)) {
                result.put("code", 403);
                result.put("message", "不能删除管理员账号");
                return result;
            }
            
            // 删除用户角色关联
            String deleteUserRoleSql = "DELETE FROM sys_user_role WHERE user_id = ?";
            jdbcTemplate.update(deleteUserRoleSql, id);
            
            // 删除用户
            String deleteUserSql = "DELETE FROM sys_user WHERE id = ?";
            int rows = jdbcTemplate.update(deleteUserSql, id);
            
            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "删除成功");
                System.out.println("✅ 成功删除用户ID: " + id);
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
            System.err.println("🔴 删除用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
