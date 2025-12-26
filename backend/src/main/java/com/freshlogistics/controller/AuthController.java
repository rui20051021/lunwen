package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            if (username == null || password == null) {
                result.put("code", 400);
                result.put("message", "用户名和密码不能为空");
                return result;
            }
            
            // 多角色用户验证（模拟数据库查询）
            Map<String, Object> userInfo = validateUser(username, password);
            
            if (userInfo != null) {
                result.put("code", 200);
                result.put("message", "登录成功");
                
                Map<String, Object> data = new HashMap<>();
                data.put("accessToken", "mock_jwt_token_" + System.currentTimeMillis());
                data.put("tokenType", "Bearer");
                data.put("expiresAt", "2025-02-03 00:00:00");
                data.put("userInfo", userInfo);
                data.put("permissions", getUserPermissions((String) userInfo.get("userType")));
                data.put("roles", List.of(userInfo.get("userType")));
                
                result.put("data", data);
            } else {
                result.put("code", 401);
                result.put("message", "用户名或密码错误");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败: " + e.getMessage());
        }
        
        return result;
    }

    private Map<String, Object> validateUser(String username, String password) {
        // 使用Mock数据验证（真实数据库验证请使用 /database/user/validate 端点）
        System.out.println("🔄 使用Mock数据验证用户: " + username);
        return validateUserWithMockData(username, password);
    }
    
    /**
     * 备用Mock数据验证方法
     */
    private Map<String, Object> validateUserWithMockData(String username, String password) {
        Map<String, Map<String, Object>> users = new HashMap<>();
        
        // 管理员
        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1);
        admin.put("username", "admin");
        admin.put("realName", "系统管理员");
        admin.put("email", "admin@freshlogistics.com");
        admin.put("phone", "13800138000");
        admin.put("userType", "admin");
        admin.put("companyId", 1);
        admin.put("status", 1);
        users.put("admin", admin);
        
        // 供应商用户
        Map<String, Object> supplier01 = new HashMap<>();
        supplier01.put("id", 2);
        supplier01.put("username", "supplier01");
        supplier01.put("realName", "张经理");
        supplier01.put("email", "zhang@freshfarm.com");
        supplier01.put("phone", "13811112222");
        supplier01.put("userType", "supplier");
        supplier01.put("companyId", 1);
        supplier01.put("status", 1);
        users.put("supplier01", supplier01);
        
        Map<String, Object> supplier02 = new HashMap<>();
        supplier02.put("id", 3);
        supplier02.put("username", "supplier02");
        supplier02.put("realName", "李总");
        supplier02.put("email", "li@greenveg.com");
        supplier02.put("phone", "13833334444");
        supplier02.put("userType", "supplier");
        supplier02.put("companyId", 2);
        supplier02.put("status", 1);
        users.put("supplier02", supplier02);
        
        // 物流商用户
        Map<String, Object> logistics01 = new HashMap<>();
        logistics01.put("id", 4);
        logistics01.put("username", "logistics01");
        logistics01.put("realName", "王物流");
        logistics01.put("email", "wang@logistics.com");
        logistics01.put("phone", "13900001111");
        logistics01.put("userType", "logistics");
        logistics01.put("companyId", 3);
        logistics01.put("status", 1);
        users.put("logistics01", logistics01);
        
        Map<String, Object> logistics02 = new HashMap<>();
        logistics02.put("id", 5);
        logistics02.put("username", "logistics02");
        logistics02.put("realName", "赵运输");
        logistics02.put("email", "zhao@transport.com");
        logistics02.put("phone", "13900002222");
        logistics02.put("userType", "logistics");
        logistics02.put("companyId", 3);
        logistics02.put("status", 1);
        users.put("logistics02", logistics02);
        
        // 采购商用户
        Map<String, Object> purchaser01 = new HashMap<>();
        purchaser01.put("id", 6);
        purchaser01.put("username", "purchaser01");
        purchaser01.put("realName", "刘采购");
        purchaser01.put("email", "liu@purchase.com");
        purchaser01.put("phone", "13700001111");
        purchaser01.put("userType", "purchaser");
        purchaser01.put("companyId", 4);
        purchaser01.put("status", 1);
        users.put("purchaser01", purchaser01);
        
        Map<String, Object> purchaser02 = new HashMap<>();
        purchaser02.put("id", 7);
        purchaser02.put("username", "purchaser02");
        purchaser02.put("realName", "陈买手");
        purchaser02.put("email", "chen@buyer.com");
        purchaser02.put("phone", "13700002222");
        purchaser02.put("userType", "purchaser");
        purchaser02.put("companyId", 4);
        purchaser02.put("status", 1);
        users.put("purchaser02", purchaser02);
        
        // 监管员用户
        Map<String, Object> regulator01 = new HashMap<>();
        regulator01.put("id", 8);
        regulator01.put("username", "regulator01");
        regulator01.put("realName", "监管员A");
        regulator01.put("email", "reg01@gov.com");
        regulator01.put("phone", "13600001111");
        regulator01.put("userType", "regulator");
        regulator01.put("companyId", 5);
        regulator01.put("status", 1);
        users.put("regulator01", regulator01);
        
        Map<String, Object> regulator02 = new HashMap<>();
        regulator02.put("id", 9);
        regulator02.put("username", "regulator02");
        regulator02.put("realName", "监管员B");
        regulator02.put("email", "reg02@gov.com");
        regulator02.put("phone", "13600002222");
        regulator02.put("userType", "regulator");
        regulator02.put("companyId", 5);
        regulator02.put("status", 1);
        users.put("regulator02", regulator02);
        
        // 验证用户名和密码（所有演示账户密码都是admin123）
        Map<String, Object> user = users.get(username);
        if (user != null && "admin123".equals(password)) {
            // 添加最后登录时间
            Map<String, Object> userInfo = new HashMap<>(user);
            userInfo.put("lastLoginTime", java.time.LocalDateTime.now().toString());
            return userInfo;
        }
        
        return null;
    }

    private List<String> getUserPermissions(String userType) {
        // 根据用户类型返回对应权限
        switch (userType) {
            case "admin":
                return List.of("system:manage", "order:manage", "product:manage", "logistics:manage", "monitor:manage");
            case "supplier":
                return List.of("supplier:manage", "order:manage", "product:manage");
            case "logistics":
                return List.of("logistics:manage", "order:view", "monitor:view");
            case "purchaser":
                return List.of("order:manage", "product:view", "monitor:view");
            case "regulator":
                return List.of("regulation:manage", "order:view", "monitor:view");
            default:
                return List.of();
        }
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", Map.of(
            "id", 1,
            "username", "admin",
            "realName", "系统管理员",
            "userType", "admin"
        ));
        return result;
    }
    
    /**
     * 获取所有角色信息（用于快捷登录）
     */
    @GetMapping("/roles")
    public Map<String, Object> getRoles() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> roles = List.of(
            Map.of(
                "id", 1,
                "roleCode", "admin",
                "roleName", "系统管理员",
                "description", "系统管理和配置权限",
                "demoUser", Map.of(
                    "username", "admin",
                    "password", "admin123",
                    "realName", "系统管理员"
                )
            ),
            Map.of(
                "id", 2,
                "roleCode", "supplier",
                "roleName", "供应商",
                "description", "产品供应和订单管理",
                "demoUser", Map.of(
                    "username", "supplier01",
                    "password", "admin123", 
                    "realName", "张经理"
                )
            ),
            Map.of(
                "id", 3,
                "roleCode", "logistics",
                "roleName", "物流商",
                "description", "运输和车辆管理",
                "demoUser", Map.of(
                    "username", "logistics01",
                    "password", "admin123",
                    "realName", "王物流"
                )
            ),
            Map.of(
                "id", 4,
                "roleCode", "purchaser", 
                "roleName", "采购商",
                "description", "采购和收货管理",
                "demoUser", Map.of(
                    "username", "purchaser01",
                    "password", "admin123",
                    "realName", "刘采购"
                )
            ),
            Map.of(
                "id", 5,
                "roleCode", "regulator",
                "roleName", "监管员",
                "description", "监管和合规检查",
                "demoUser", Map.of(
                    "username", "regulator01",
                    "password", "admin123",
                    "realName", "监管员A"
                )
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", roles);
        
        return result;
    }
    
    /**
     * 快捷登录接口
     */
    @PostMapping("/quick-login")
    public Map<String, Object> quickLogin(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String roleCode = request.get("roleCode");
            
            if (roleCode == null) {
                result.put("code", 400);
                result.put("message", "角色代码不能为空");
                return result;
            }
            
            // 根据角色代码获取对应的演示用户
            String username = getDemoUsername(roleCode);
            if (username == null) {
                result.put("code", 400);
                result.put("message", "不支持的角色类型");
                return result;
            }
            
            // 使用演示账户登录
            Map<String, String> loginData = new HashMap<>();
            loginData.put("username", username);
            loginData.put("password", "admin123");
            return login(loginData);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "快捷登录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 根据角色代码获取演示用户名
     */
    private String getDemoUsername(String roleCode) {
        Map<String, String> demoUsers = new HashMap<>();
        demoUsers.put("admin", "admin");
        demoUsers.put("supplier", "supplier01");
        demoUsers.put("logistics", "logistics01");
        demoUsers.put("purchaser", "purchaser01");
        demoUsers.put("regulator", "regulator01");
        
        return demoUsers.get(roleCode);
    }
    
    /**
     * 获取用户详细信息（包含数据库字段映射）
     */
    @GetMapping("/user/{username}")
    public Map<String, Object> getUserInfo(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> userInfo = validateUser(username, "admin123");
            
            if (userInfo != null) {
                result.put("code", 200);
                result.put("message", "获取成功");
                result.put("data", userInfo);
            } else {
                result.put("code", 404);
                result.put("message", "用户不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取用户信息失败: " + e.getMessage());
        }
        
        return result;
    }
}
