package com.freshlogistics.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/roles")
public class RoleController {

    @GetMapping
    public Map<String, Object> getRoles() {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟从数据库获取角色数据
        List<Map<String, Object>> roles = new ArrayList<>();
        
        roles.add(Map.of(
            "id", 1,
            "roleCode", "admin",
            "roleName", "系统管理员",
            "description", "系统管理员，拥有所有权限",
            "status", 1,
            "sortOrder", 1,
            "defaultCredentials", Map.of(
                "username", "admin",
                "password", "admin123"
            )
        ));
        
        roles.add(Map.of(
            "id", 2,
            "roleCode", "supplier",
            "roleName", "供应商",
            "description", "供应商角色，负责产品供应和订单管理",
            "status", 1,
            "sortOrder", 2,
            "defaultCredentials", Map.of(
                "username", "supplier",
                "password", "supplier123"
            )
        ));
        
        roles.add(Map.of(
            "id", 3,
            "roleCode", "logistics",
            "roleName", "物流商",
            "description", "物流商角色，负责运输和车辆管理",
            "status", 1,
            "sortOrder", 3,
            "defaultCredentials", Map.of(
                "username", "logistics",
                "password", "logistics123"
            )
        ));
        
        roles.add(Map.of(
            "id", 4,
            "roleCode", "purchaser",
            "roleName", "采购商",
            "description", "采购商角色，负责采购和收货确认",
            "status", 1,
            "sortOrder", 4,
            "defaultCredentials", Map.of(
                "username", "purchaser",
                "password", "purchaser123"
            )
        ));
        
        roles.add(Map.of(
            "id", 5,
            "roleCode", "regulator",
            "roleName", "监管员",
            "description", "监管员角色，负责监管和合规检查",
            "status", 1,
            "sortOrder", 5,
            "defaultCredentials", Map.of(
                "username", "regulator",
                "password", "regulator123"
            )
        ));
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", roles);
        
        return result;
    }

    @GetMapping("/{roleCode}")
    public Map<String, Object> getRoleByCode(@PathVariable String roleCode) {
        Map<String, Object> result = new HashMap<>();
        
        // 模拟根据角色编码获取角色详情
        Map<String, Object> role = null;
        
        switch (roleCode) {
            case "admin":
                role = Map.of(
                    "id", 1,
                    "roleCode", "admin",
                    "roleName", "系统管理员",
                    "description", "系统管理员，拥有所有权限",
                    "status", 1,
                    "permissions", List.of("system:manage", "order:manage", "product:manage", "logistics:manage", "monitor:manage"),
                    "defaultCredentials", Map.of(
                        "username", "admin",
                        "password", "admin123"
                    )
                );
                break;
            case "supplier":
                role = Map.of(
                    "id", 2,
                    "roleCode", "supplier",
                    "roleName", "供应商",
                    "description", "供应商角色，负责产品供应和订单管理",
                    "status", 1,
                    "permissions", List.of("supplier:manage", "order:manage", "product:manage"),
                    "defaultCredentials", Map.of(
                        "username", "supplier",
                        "password", "supplier123"
                    )
                );
                break;
            case "logistics":
                role = Map.of(
                    "id", 3,
                    "roleCode", "logistics",
                    "roleName", "物流商",
                    "description", "物流商角色，负责运输和车辆管理",
                    "status", 1,
                    "permissions", List.of("logistics:manage", "order:view", "monitor:view"),
                    "defaultCredentials", Map.of(
                        "username", "logistics",
                        "password", "logistics123"
                    )
                );
                break;
            case "purchaser":
                role = Map.of(
                    "id", 4,
                    "roleCode", "purchaser",
                    "roleName", "采购商",
                    "description", "采购商角色，负责采购和收货确认",
                    "status", 1,
                    "permissions", List.of("order:manage", "product:view", "monitor:view"),
                    "defaultCredentials", Map.of(
                        "username", "purchaser",
                        "password", "purchaser123"
                    )
                );
                break;
            case "regulator":
                role = Map.of(
                    "id", 5,
                    "roleCode", "regulator",
                    "roleName", "监管员",
                    "description", "监管员角色，负责监管和合规检查",
                    "status", 1,
                    "permissions", List.of("regulation:manage", "order:view", "monitor:view"),
                    "defaultCredentials", Map.of(
                        "username", "regulator",
                        "password", "regulator123"
                    )
                );
                break;
        }
        
        if (role != null) {
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", role);
        } else {
            result.put("code", 404);
            result.put("message", "角色不存在");
        }
        
        return result;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getRoleStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> statistics = Map.of(
            "totalCount", 5,
            "enabledCount", 5,
            "roleDistribution", List.of(
                Map.of("roleCode", "admin", "roleName", "系统管理员", "userCount", 1),
                Map.of("roleCode", "supplier", "roleName", "供应商", "userCount", 3),
                Map.of("roleCode", "logistics", "roleName", "物流商", "userCount", 2),
                Map.of("roleCode", "purchaser", "roleName", "采购商", "userCount", 4),
                Map.of("roleCode", "regulator", "roleName", "监管员", "userCount", 2)
            )
        );
        
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", statistics);
        
        return result;
    }
}