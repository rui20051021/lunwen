package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @GetMapping
    public Map<String, Object> getUsers(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String userType) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 模拟用户数据（与AuthController中的用户数据保持一致）
        List<Map<String, Object>> users = new ArrayList<>();
        
        // 管理员
        users.add(Map.of(
            "id", 1,
            "username", "admin",
            "realName", "系统管理员",
            "email", "admin@freshlogistics.com",
            "phone", "13800138000",
            "userType", "admin",
            "companyId", 1,
            "status", 1,
            "createTime", "2025-01-01 00:00:00",
            "lastLoginTime", "2025-01-30 10:30:00"
        ));
        
        // 供应商用户
        users.add(Map.of(
            "id", 2,
            "username", "supplier01",
            "realName", "张经理",
            "email", "zhang@freshfarm.com",
            "phone", "13811112222",
            "userType", "supplier",
            "companyId", 1,
            "status", 1,
            "createTime", "2025-01-02 00:00:00",
            "lastLoginTime", "2025-01-29 14:20:00"
        ));
        
        users.add(Map.of(
            "id", 3,
            "username", "supplier02",
            "realName", "李总",
            "email", "li@greenveg.com",
            "phone", "13833334444",
            "userType", "supplier",
            "companyId", 2,
            "status", 1,
            "createTime", "2025-01-03 00:00:00",
            "lastLoginTime", "2025-01-28 16:45:00"
        ));
        
        // 物流商用户
        users.add(Map.of(
            "id", 4,
            "username", "logistics01",
            "realName", "王物流",
            "email", "wang@logistics.com",
            "phone", "13900001111",
            "userType", "logistics",
            "companyId", 3,
            "status", 1,
            "createTime", "2025-01-04 00:00:00",
            "lastLoginTime", "2025-01-30 08:15:00"
        ));
        
        users.add(Map.of(
            "id", 5,
            "username", "logistics02",
            "realName", "赵运输",
            "email", "zhao@transport.com",
            "phone", "13900002222",
            "userType", "logistics",
            "companyId", 3,
            "status", 1,
            "createTime", "2025-01-05 00:00:00",
            "lastLoginTime", "2025-01-27 12:30:00"
        ));
        
        // 采购商用户
        users.add(Map.of(
            "id", 6,
            "username", "purchaser01",
            "realName", "刘采购",
            "email", "liu@purchase.com",
            "phone", "13700001111",
            "userType", "purchaser",
            "companyId", 4,
            "status", 1,
            "createTime", "2025-01-06 00:00:00",
            "lastLoginTime", "2025-01-29 09:45:00"
        ));
        
        users.add(Map.of(
            "id", 7,
            "username", "purchaser02",
            "realName", "陈买手",
            "email", "chen@buyer.com",
            "phone", "13700002222",
            "userType", "purchaser",
            "companyId", 4,
            "status", 1,
            "createTime", "2025-01-07 00:00:00",
            "lastLoginTime", "2025-01-26 15:20:00"
        ));
        
        // 监管员用户
        users.add(Map.of(
            "id", 8,
            "username", "regulator01",
            "realName", "监管员A",
            "email", "reg01@gov.com",
            "phone", "13600001111",
            "userType", "regulator",
            "companyId", 5,
            "status", 1,
            "createTime", "2025-01-08 00:00:00",
            "lastLoginTime", "2025-01-30 11:10:00"
        ));
        
        users.add(Map.of(
            "id", 9,
            "username", "regulator02",
            "realName", "监管员B",
            "email", "reg02@gov.com",
            "phone", "13600002222",
            "userType", "regulator",
            "companyId", 5,
            "status", 1,
            "createTime", "2025-01-09 00:00:00",
            "lastLoginTime", "2025-01-25 13:55:00"
        ));
        
        // 过滤用户数据
        List<Map<String, Object>> filteredUsers = users;
        if (username != null && !username.trim().isEmpty()) {
            filteredUsers = users.stream()
                .filter(user -> ((String) user.get("username")).contains(username) || 
                               ((String) user.get("realName")).contains(username))
                .toList();
        }
        
        if (userType != null && !userType.trim().isEmpty()) {
            filteredUsers = filteredUsers.stream()
                .filter(user -> userType.equals(user.get("userType")))
                .toList();
        }
        
        // 分页处理
        int total = filteredUsers.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Map<String, Object>> pageUsers = filteredUsers.subList(start, end);
        
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
            "records", pageUsers,
            "total", total,
            "current", page,
            "size", size,
            "pages", (int) Math.ceil((double) total / size)
        ));
        
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟根据ID查询用户
        Map<String, Object> user = null;
        
        switch (id) {
            case 1:
                user = Map.of(
                    "id", 1,
                    "username", "admin",
                    "realName", "系统管理员",
                    "email", "admin@freshlogistics.com",
                    "phone", "13800138000",
                    "userType", "admin",
                    "companyId", 1,
                    "status", 1,
                    "createTime", "2025-01-01 00:00:00",
                    "lastLoginTime", "2025-01-30 10:30:00"
                );
                break;
            case 2:
                user = Map.of(
                    "id", 2,
                    "username", "supplier01",
                    "realName", "张经理",
                    "email", "zhang@freshfarm.com",
                    "phone", "13811112222",
                    "userType", "supplier",
                    "companyId", 1,
                    "status", 1,
                    "createTime", "2025-01-02 00:00:00",
                    "lastLoginTime", "2025-01-29 14:20:00"
                );
                break;
            // 可以继续添加其他用户...
        }
        
        if (user != null) {
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        
        return result;
    }
    
    @PostMapping
    public Map<String, Object> createUser(@RequestBody Map<String, Object> userRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟创建用户
            Map<String, Object> newUser = new HashMap<>(userRequest);
            newUser.put("id", System.currentTimeMillis() % 10000); // 模拟生成ID
            newUser.put("createTime", java.time.LocalDateTime.now().toString());
            newUser.put("status", 1);
            
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", newUser);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> userRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟更新用户
            Map<String, Object> updatedUser = new HashMap<>(userRequest);
            updatedUser.put("id", id);
            updatedUser.put("updateTime", java.time.LocalDateTime.now().toString());
            
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", updatedUser);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟删除用户
            result.put("code", 200);
            result.put("message", "删除成功");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }
        
        return result;
    }
}