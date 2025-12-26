package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库订单数据控制器
 */
@RestController
@RequestMapping("/database/order")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseOrderController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有订单数据
     */
    @GetMapping("/all")
    public Map<String, Object> getAllOrders() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT o.*, s.supplier_name " +
                        "FROM orders o " +
                        "LEFT JOIN suppliers s ON o.supplier_id = s.id " +
                        "ORDER BY o.created_at DESC";
            
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功 - 真实数据库数据");
            result.put("data", orders);
            
            System.out.println("✅ 成功获取 " + orders.size() + " 个订单记录");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询订单数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 分页查询订单
     */
    @GetMapping("/list")
    public Map<String, Object> getOrderList(
            @RequestParam(value = "current", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(required = false) String orderStatus) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT o.*, s.supplier_name ")
                     .append("FROM orders o ")
                     .append("LEFT JOIN suppliers s ON o.supplier_id = s.id ")
                     .append("WHERE 1=1 ");
            
            if (orderStatus != null && !orderStatus.trim().isEmpty()) {
                sqlBuilder.append("AND o.order_status = '").append(orderStatus.trim()).append("' ");
            }
            
            sqlBuilder.append("ORDER BY o.created_at DESC ");
            
            int offset = (page - 1) * size;
            sqlBuilder.append("LIMIT ").append(size).append(" OFFSET ").append(offset);
            
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(sqlBuilder.toString());
            
            Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", orders);
            data.put("total", total);
            data.put("size", size);
            data.put("current", page);
            
            result.put("code", 200);
            result.put("message", "分页查询成功");
            result.put("data", data);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "分页查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取订单统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_orders, " +
                        "COUNT(CASE WHEN order_status = 'pending' THEN 1 END) as pending_orders, " +
                        "COUNT(CASE WHEN order_status = 'confirmed' THEN 1 END) as confirmed_orders, " +
                        "COUNT(CASE WHEN order_status = 'shipping' THEN 1 END) as shipping_orders, " +
                        "COUNT(CASE WHEN order_status = 'delivered' THEN 1 END) as delivered_orders, " +
                        "COUNT(CASE WHEN order_status = 'cancelled' THEN 1 END) as cancelled_orders, " +
                        "SUM(total_amount) as total_amount, " +
                        "AVG(total_amount) as avg_amount " +
                        "FROM orders";
            
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
     * 根据状态获取订单
     */
    @GetMapping("/by-status/{status}")
    public Map<String, Object> getOrdersByStatus(@PathVariable String status) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT o.*, s.supplier_name " +
                        "FROM orders o " +
                        "LEFT JOIN suppliers s ON o.supplier_id = s.id " +
                        "WHERE o.order_status = ? " +
                        "ORDER BY o.created_at DESC";
            
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql, status);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", orders);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建新订单
     */
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> orderRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String orderCode = (String) orderRequest.get("orderCode");
            Object supplierIdObj = orderRequest.get("supplierId");
            Object purchaserIdObj = orderRequest.get("purchaserId");
            String orderType = (String) orderRequest.get("orderType");
            String pickupAddress = (String) orderRequest.get("pickupAddress");
            String deliveryAddress = (String) orderRequest.get("deliveryAddress");
            String deliveryContact = (String) orderRequest.get("deliveryContact");
            String deliveryPhone = (String) orderRequest.get("deliveryPhone");
            String requiredDeliveryTime = (String) orderRequest.get("requiredDeliveryTime");
            Object totalAmountObj = orderRequest.get("totalAmount");
            String remarks = (String) orderRequest.get("remarks");
            
            // 验证必填字段
            if (orderCode == null || supplierIdObj == null || purchaserIdObj == null) {
                result.put("code", 400);
                result.put("message", "订单编号、供应商和采购商不能为空");
                return result;
            }
            
            // 转换ID类型
            Long supplierId = supplierIdObj instanceof Integer ? 
                ((Integer) supplierIdObj).longValue() : (Long) supplierIdObj;
            Long purchaserId = purchaserIdObj instanceof Integer ? 
                ((Integer) purchaserIdObj).longValue() : (Long) purchaserIdObj;
            
            // 转换金额
            Double totalAmount = 0.0;
            if (totalAmountObj != null) {
                if (totalAmountObj instanceof Integer) {
                    totalAmount = ((Integer) totalAmountObj).doubleValue();
                } else if (totalAmountObj instanceof Double) {
                    totalAmount = (Double) totalAmountObj;
                }
            }
            
            // 检查订单编号是否已存在
            String checkSql = "SELECT COUNT(*) FROM orders WHERE order_code = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, orderCode);
            if (count != null && count > 0) {
                result.put("code", 400);
                result.put("message", "订单编号已存在");
                return result;
            }
            
            // 插入订单数据
            String insertSql = "INSERT INTO orders (order_code, order_type, supplier_id, purchaser_id, " +
                             "pickup_address, delivery_address, delivery_contact, delivery_phone, " +
                             "required_delivery_time, total_amount, order_status, remarks, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'created', ?, NOW(), NOW())";
            
            jdbcTemplate.update(insertSql, orderCode, orderType != null ? orderType : "purchase", 
                              supplierId, purchaserId, pickupAddress, deliveryAddress, 
                              deliveryContact, deliveryPhone, requiredDeliveryTime, 
                              totalAmount, remarks);
            
            // 获取插入的订单ID
            String getIdSql = "SELECT id FROM orders WHERE order_code = ?";
            Long orderId = jdbcTemplate.queryForObject(getIdSql, Long.class, orderCode);
            
            result.put("code", 200);
            result.put("message", "订单创建成功");
            result.put("data", Map.of("id", orderId, "orderCode", orderCode));
            
            System.out.println("✅ 成功创建订单: " + orderCode);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            System.err.println("🔴 创建订单失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取所有采购商
     */
    @GetMapping("/purchasers")
    public Map<String, Object> getAllPurchasers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM purchasers WHERE status = 1 ORDER BY company_name";
            List<Map<String, Object>> purchasers = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", purchasers);
            
            System.out.println("✅ 成功获取 " + purchasers.size() + " 个采购商");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询采购商失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> getOrderDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT o.*, s.supplier_name " +
                        "FROM orders o " +
                        "LEFT JOIN suppliers s ON o.supplier_id = s.id " +
                        "WHERE o.id = ?";
            
            List<Map<String, Object>> orderList = jdbcTemplate.queryForList(sql, id);
            
            if (orderList.isEmpty()) {
                result.put("code", 404);
                result.put("message", "订单不存在");
                return result;
            }
            
            Map<String, Object> order = orderList.get(0);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", order);
            
            System.out.println("✅ 成功获取订单详情 ID: " + id);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 获取订单详情失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/confirm-receive/{id}")
    public Map<String, Object> confirmReceive(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 更新订单状态为已完成
            String updateSql = "UPDATE orders SET " +
                             "order_status = 'completed', " +
                             "actual_arrival_time = NOW(), " +
                             "updated_at = NOW() " +
                             "WHERE id = ?";
            
            int rows = jdbcTemplate.update(updateSql, id);
            
            if (rows > 0) {
                result.put("code", 200);
                result.put("message", "确认收货成功");
                System.out.println("✅ 订单收货确认成功 ID: " + id);
            } else {
                result.put("code", 404);
                result.put("message", "订单不存在");
            }
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "确认收货失败: " + e.getMessage());
            System.err.println("🔴 确认收货失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
