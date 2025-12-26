package com.freshlogistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库车辆数据控制器
 */
@RestController
@RequestMapping("/database/vehicle")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DatabaseVehicleController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有车辆数据
     */
    @GetMapping("/all")
    public Map<String, Object> getAllVehicles() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM vehicles ORDER BY created_at DESC";
            List<Map<String, Object>> vehicles = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功 - 真实数据库数据");
            result.put("data", vehicles);
            
            System.out.println("✅ 成功获取 " + vehicles.size() + " 个车辆记录");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询车辆数据失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取车辆统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getVehicleStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT " +
                        "COUNT(*) as total_vehicles, " +
                        "COUNT(CASE WHEN vehicle_status = 'available' THEN 1 END) as available_vehicles, " +
                        "COUNT(CASE WHEN vehicle_status = 'in_transit' THEN 1 END) as in_transit_vehicles, " +
                        "COUNT(CASE WHEN vehicle_status = 'maintenance' THEN 1 END) as maintenance_vehicles " +
                        "FROM vehicles";
            
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
     * 获取司机数据
     */
    @GetMapping("/drivers")
    public Map<String, Object> getDrivers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT * FROM drivers ORDER BY created_at DESC";
            List<Map<String, Object>> drivers = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", drivers);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取运输任务数据
     */
    @GetMapping("/transports")
    public Map<String, Object> getTransports() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT t.*, v.license_plate, d.name as driver_name " +
                        "FROM transports t " +
                        "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                        "LEFT JOIN drivers d ON t.driver_id = d.id " +
                        "WHERE t.transport_status IN ('in_transit', 'pending') " +
                        "ORDER BY t.created_at DESC";
            
            List<Map<String, Object>> transports = jdbcTemplate.queryForList(sql);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", transports);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取温度监控数据
     */
    @GetMapping("/temperature")
    public Map<String, Object> getTemperatureMonitoring() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 先尝试从temperature_logs表获取数据
            String logSql = "SELECT tl.*, v.license_plate, v.vehicle_code " +
                           "FROM temperature_logs tl " +
                           "LEFT JOIN vehicles v ON tl.vehicle_id = v.id " +
                           "WHERE tl.created_at >= DATE_SUB(NOW(), INTERVAL 1 DAY) " +
                           "ORDER BY tl.created_at DESC " +
                           "LIMIT 20";
            
            List<Map<String, Object>> tempData = jdbcTemplate.queryForList(logSql);
            
            // 如果temperature_logs表没有数据，从vehicles表获取真实温度数据
            if (tempData.isEmpty()) {
                System.out.println("⚠️ temperature_logs表为空，从vehicles表获取真实温度数据");
                
                String vehicleSql = "SELECT " +
                                  "id as vehicle_id, " +
                                  "vehicle_code, " +
                                  "license_plate, " +
                                  "current_temp as temperature, " +  // 使用真实的current_temp字段
                                  "current_humidity as humidity, " + // 使用真实的current_humidity字段
                                  "-2 as min_temp, " +
                                  "8 as max_temp, " +
                                  "updated_at as created_at, " +
                                  "'配送路线中' as location " +
                                  "FROM vehicles " +
                                  "WHERE vehicle_status IN ('available', 'in_transit') " +
                                  "ORDER BY updated_at DESC";
                
                tempData = jdbcTemplate.queryForList(vehicleSql);
                
                System.out.println("✅ 从vehicles表获取了 " + tempData.size() + " 条真实温度数据");
            }
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", tempData);
            
            System.out.println("✅ 成功返回 " + tempData.size() + " 条温度监控数据");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 查询温度数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 创建新车辆
     */
    @PostMapping("/create")
    public Map<String, Object> createVehicle(@RequestBody Map<String, Object> vehicleRequest) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String vehicleCode = (String) vehicleRequest.get("vehicleCode");
            String licensePlate = (String) vehicleRequest.get("licensePlate");
            String vehicleType = (String) vehicleRequest.get("vehicleType");
            String brand = (String) vehicleRequest.get("brand");
            String model = (String) vehicleRequest.get("model");
            Object loadCapacityObj = vehicleRequest.get("loadCapacity");
            Object volumeCapacityObj = vehicleRequest.get("volumeCapacity");
            Object currentTempObj = vehicleRequest.get("currentTemp");
            String temperatureSensorId = (String) vehicleRequest.get("temperatureSensorId");
            String gpsDeviceId = (String) vehicleRequest.get("gpsDeviceId");
            String vehicleStatus = (String) vehicleRequest.get("vehicleStatus");
            String remarks = (String) vehicleRequest.get("remarks");
            
            // 验证必填字段
            if (vehicleCode == null || licensePlate == null || vehicleType == null) {
                result.put("code", 400);
                result.put("message", "车辆编码、车牌号和车辆类型不能为空");
                return result;
            }
            
            // 转换数值类型
            Double loadCapacity = loadCapacityObj != null ? 
                (loadCapacityObj instanceof Integer ? ((Integer) loadCapacityObj).doubleValue() : (Double) loadCapacityObj) : 5.0;
            
            Double volumeCapacity = volumeCapacityObj != null ? 
                (volumeCapacityObj instanceof Integer ? ((Integer) volumeCapacityObj).doubleValue() : (Double) volumeCapacityObj) : 20.0;
            
            Double currentTemp = currentTempObj != null ? 
                (currentTempObj instanceof Integer ? ((Integer) currentTempObj).doubleValue() : (Double) currentTempObj) : 2.5;
            
            // 检查车辆编码是否已存在
            String checkCodeSql = "SELECT COUNT(*) FROM vehicles WHERE vehicle_code = ?";
            Integer codeCount = jdbcTemplate.queryForObject(checkCodeSql, Integer.class, vehicleCode);
            if (codeCount != null && codeCount > 0) {
                result.put("code", 400);
                result.put("message", "车辆编码已存在");
                return result;
            }
            
            // 检查车牌号是否已存在
            String checkPlateSql = "SELECT COUNT(*) FROM vehicles WHERE license_plate = ?";
            Integer plateCount = jdbcTemplate.queryForObject(checkPlateSql, Integer.class, licensePlate);
            if (plateCount != null && plateCount > 0) {
                result.put("code", 400);
                result.put("message", "车牌号已存在");
                return result;
            }
            
            // 插入车辆数据
            String insertSql = "INSERT INTO vehicles (vehicle_code, license_plate, vehicle_type, brand, model, " +
                             "load_capacity, volume_capacity, current_temp, temperature_sensor_id, gps_device_id, " +
                             "vehicle_status, remarks, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            
            jdbcTemplate.update(insertSql, vehicleCode, licensePlate, vehicleType, brand, model,
                              loadCapacity, volumeCapacity, currentTemp, temperatureSensorId, gpsDeviceId,
                              vehicleStatus != null ? vehicleStatus : "available", remarks);
            
            // 获取插入的车辆ID
            String getIdSql = "SELECT id FROM vehicles WHERE vehicle_code = ?";
            Long vehicleId = jdbcTemplate.queryForObject(getIdSql, Long.class, vehicleCode);
            
            result.put("code", 200);
            result.put("message", "车辆创建成功");
            result.put("data", Map.of("id", vehicleId, "vehicleCode", vehicleCode, "licensePlate", licensePlate));
            
            System.out.println("✅ 成功创建车辆: " + licensePlate + " (" + vehicleCode + ")");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            System.err.println("🔴 创建车辆失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 获取运输任务详情
     */
    @GetMapping("/transport-detail/{id}")
    public Map<String, Object> getTransportDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String sql = "SELECT t.*, v.license_plate, d.driver_name, tr.route_name " +
                        "FROM transports t " +
                        "LEFT JOIN vehicles v ON t.vehicle_id = v.id " +
                        "LEFT JOIN drivers d ON t.driver_id = d.id " +
                        "LEFT JOIN transport_routes tr ON t.route_id = tr.id " +
                        "WHERE t.id = ?";
            
            List<Map<String, Object>> transportList = jdbcTemplate.queryForList(sql, id);
            
            if (transportList.isEmpty()) {
                result.put("code", 404);
                result.put("message", "运输任务不存在");
                return result;
            }
            
            Map<String, Object> transport = transportList.get(0);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", transport);
            
            System.out.println("✅ 成功获取运输任务详情 ID: " + id);
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            System.err.println("🔴 获取运输详情失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
