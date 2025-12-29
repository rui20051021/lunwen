package com.freshlogistics.controller;

import com.freshlogistics.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 预警处理控制器
 * 
 * 提供预警处理相关的API接口
 */
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertProcessController {
    
    @Autowired
    private AlertService alertService;
    
    /**
     * 开始处理预警
     * 
     * @param alertId 预警ID
     * @param request 包含 processorId 和 processNotes
     */
    @PostMapping("/{alertId}/process")
    public ResponseEntity<Map<String, Object>> processAlert(
            @PathVariable Long alertId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long processorId = ((Number) request.get("processorId")).longValue();
            String processNotes = (String) request.get("processNotes");
            
            alertService.processAlert(alertId, processorId, processNotes);
            
            response.put("success", true);
            response.put("message", "预警处理已开始");
            response.put("alertId", alertId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "处理预警失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 完成预警处理
     * 
     * @param alertId 预警ID
     * @param request 包含 processResult
     */
    @PostMapping("/{alertId}/complete")
    public ResponseEntity<Map<String, Object>> completeAlert(
            @PathVariable Long alertId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String processResult = (String) request.get("processResult");
            
            alertService.completeAlert(alertId, processResult);
            
            response.put("success", true);
            response.put("message", "预警处理已完成");
            response.put("alertId", alertId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "完成预警处理失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 忽略预警
     * 
     * @param alertId 预警ID
     * @param request 包含 processorId 和 ignoreReason
     */
    @PostMapping("/{alertId}/ignore")
    public ResponseEntity<Map<String, Object>> ignoreAlert(
            @PathVariable Long alertId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long processorId = ((Number) request.get("processorId")).longValue();
            String ignoreReason = (String) request.get("ignoreReason");
            
            alertService.ignoreAlert(alertId, processorId, ignoreReason);
            
            response.put("success", true);
            response.put("message", "预警已忽略");
            response.put("alertId", alertId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "忽略预警失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 标记为误报
     * 
     * @param alertId 预警ID
     * @param request 包含 processorId 和 reason
     */
    @PostMapping("/{alertId}/false-alarm")
    public ResponseEntity<Map<String, Object>> markAsFalseAlarm(
            @PathVariable Long alertId,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long processorId = ((Number) request.get("processorId")).longValue();
            String reason = (String) request.get("reason");
            
            // 使用忽略接口，但标记为误报
            alertService.ignoreAlert(alertId, processorId, "误报: " + reason);
            
            response.put("success", true);
            response.put("message", "已标记为误报");
            response.put("alertId", alertId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "标记误报失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
