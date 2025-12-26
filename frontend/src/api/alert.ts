import { http } from './request'
import type { AlertRule, AlertType, AlertLevel, PageResponse, PageParams } from '@/types/api'

export const alertApi = {
  // 分页查询预警规则
  getAlertRules(params: PageParams & {
    ruleName?: string
    ruleType?: AlertType
    alertLevel?: AlertLevel
    isEnabled?: number
  }) {
    return http.get<PageResponse<AlertRule>>('/database/alert/rules', { params })
  },
  
  // 创建预警规则
  createAlertRule(data: Partial<AlertRule>) {
    return http.post<AlertRule>('/database/alert/rules', data)
  },
  
  // 更新预警规则
  updateAlertRule(id: number, data: Partial<AlertRule>) {
    return http.put<AlertRule>(`/database/alert/rules/${id}`, data)
  },
  
  // 删除预警规则
  deleteAlertRule(id: number) {
    return http.delete(`/database/alert/rules/${id}`)
  },
  
  // 批量删除预警规则
  batchDeleteAlertRules(ids: number[]) {
    return http.delete('/database/alert/rules/batch', { data: ids })
  },
  
  // 获取预警规则详情
  getAlertRule(id: number) {
    return http.get<AlertRule>(`/database/alert/rules/${id}`)
  },
  
  // 启用/禁用预警规则
  toggleAlertRule(id: number, isEnabled: number) {
    return http.patch(`/database/alert/rules/${id}/toggle`, null, { 
      params: { isEnabled } 
    })
  },
  
  // 根据类型查询启用规则
  getEnabledRulesByType(ruleType: AlertType) {
    return http.get<AlertRule[]>(`/database/alert/rules/enabled/type/${ruleType}`)
  },
  
  // 检查温度预警
  checkTemperatureAlert(productId: number, temperature: number) {
    return http.post<AlertRule[]>('/database/alert/check/temperature', null, {
      params: { productId, temperature }
    })
  },
  
  // 检查湿度预警
  checkHumidityAlert(productId: number, humidity: number) {
    return http.post<AlertRule[]>('/database/alert/check/humidity', null, {
      params: { productId, humidity }
    })
  },
  
  // 检查超时预警
  checkTimeoutAlert(routeId: number, delayMinutes: number) {
    return http.post<AlertRule[]>('/database/alert/check/timeout', null, {
      params: { routeId, delayMinutes }
    })
  },
  
  // 检查路径偏离预警
  checkRouteDeviationAlert(routeId: number, deviationDistance: number) {
    return http.post<AlertRule[]>('/database/alert/check/route-deviation', null, {
      params: { routeId, deviationDistance }
    })
  },
  
  // 获取预警规则统计
  getAlertRuleStatistics() {
    return http.get('/database/alert/rules/statistics')
  },
  
  // 复制预警规则
  copyAlertRule(id: number, newRuleCode: string, newRuleName: string) {
    return http.post<AlertRule>(`/database/alert/rules/${id}/copy`, null, {
      params: { newRuleCode, newRuleName }
    })
  },
  
  // 导出预警规则
  exportAlertRules() {
    return http.get<AlertRule[]>('/database/alert/rules/export')
  },
  
  // 导入预警规则
  importAlertRules(rules: AlertRule[]) {
    return http.post('/database/alert/rules/import', rules)
  }
}

