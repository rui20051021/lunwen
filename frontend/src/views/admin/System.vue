<template>
  <div class="system-page">
    <div class="page-header">
      <h1>⚙️ 系统设置</h1>
      <p>管理系统配置参数和功能开关</p>
    </div>
    
    <!-- 系统状态 -->
    <div class="system-status">
      <h3>系统运行状态</h3>
      <div class="status-grid">
        <div class="status-item">
          <div class="status-label">数据库连接</div>
          <div class="status-value success">✅ 正常</div>
        </div>
        <div class="status-item">
          <div class="status-label">后端服务</div>
          <div class="status-value success">✅ 运行中</div>
        </div>
        <div class="status-item">
          <div class="status-label">前端服务</div>
          <div class="status-value success">✅ 运行中</div>
        </div>
        <div class="status-item">
          <div class="status-label">数据表</div>
          <div class="status-value">24张表</div>
        </div>
        <div class="status-item">
          <div class="status-label">数据记录</div>
          <div class="status-value">{{ totalRecords }}条</div>
        </div>
        <div class="status-item">
          <div class="status-label">系统版本</div>
          <div class="status-value">v1.0.0</div>
        </div>
      </div>
    </div>
    
    <!-- 系统配置 -->
    <div class="config-sections">
      <div class="config-section">
        <h4>🔔 预警配置</h4>
        <div class="config-items">
          <div class="config-item">
            <span class="config-label">邮件预警:</span>
            <el-switch v-model="alertConfig.emailEnabled" />
          </div>
          <div class="config-item">
            <span class="config-label">短信预警:</span>
            <el-switch v-model="alertConfig.smsEnabled" />
          </div>
          <div class="config-item">
            <span class="config-label">默认超时阈值:</span>
            <el-input-number v-model="alertConfig.timeoutThreshold" :min="10" :max="120" /> 分钟
          </div>
          <div class="config-item">
            <span class="config-label">每日最大预警数:</span>
            <el-input-number v-model="alertConfig.maxAlertsPerDay" :min="50" :max="500" />
          </div>
        </div>
      </div>
      
      <div class="config-section">
        <h4>🏢 业务配置</h4>
        <div class="config-items">
          <div class="config-item">
            <span class="config-label">订单超时时间:</span>
            <el-input-number v-model="businessConfig.orderTimeoutHours" :min="12" :max="48" /> 小时
          </div>
          <div class="config-item">
            <span class="config-label">最大温度偏差:</span>
            <el-input-number v-model="businessConfig.maxTempDeviation" :min="1" :max="5" :precision="1" /> ℃
          </div>
          <div class="config-item">
            <span class="config-label">最小配送距离:</span>
            <el-input-number v-model="businessConfig.minDeliveryDistance" :min="5" :max="50" /> 公里
          </div>
        </div>
      </div>
      
      <div class="config-section">
        <h4>🖥️ 系统配置</h4>
        <div class="config-items">
          <div class="config-item">
            <span class="config-label">自动备份:</span>
            <el-switch v-model="systemConfig.backupEnabled" />
          </div>
          <div class="config-item">
            <span class="config-label">日志保留天数:</span>
            <el-input-number v-model="systemConfig.logRetentionDays" :min="7" :max="90" />
          </div>
          <div class="config-item">
            <span class="config-label">维护模式:</span>
            <el-switch v-model="systemConfig.maintenanceMode" />
          </div>
          <div class="config-item">
            <span class="config-label">会话超时:</span>
            <el-input-number v-model="systemConfig.sessionTimeout" :min="15" :max="120" /> 分钟
          </div>
        </div>
      </div>
    </div>
    
    <!-- 数据库信息 -->
    <div class="database-info">
      <h3>📊 数据库信息</h3>
      <div class="db-grid">
        <div class="db-item">
          <span class="db-label">数据库名:</span>
          <span class="db-value">freshlogistics</span>
        </div>
        <div class="db-item">
          <span class="db-label">字符集:</span>
          <span class="db-value">utf8mb4</span>
        </div>
        <div class="db-item">
          <span class="db-label">存储引擎:</span>
          <span class="db-value">InnoDB</span>
        </div>
        <div class="db-item">
          <span class="db-label">数据表总数:</span>
          <span class="db-value">24张</span>
        </div>
        <div class="db-item">
          <span class="db-label">用户数据:</span>
          <span class="db-value">9条记录</span>
        </div>
        <div class="db-item">
          <span class="db-label">订单数据:</span>
          <span class="db-value">7条记录</span>
        </div>
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="saveConfig">保存配置</el-button>
      <el-button type="success" @click="exportConfig">导出配置</el-button>
      <el-button type="warning" @click="resetConfig">重置配置</el-button>
      <el-button type="info" @click="testConnection">测试连接</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 系统统计
const totalRecords = ref(156)

// 预警配置
const alertConfig = reactive({
  emailEnabled: true,
  smsEnabled: true,
  timeoutThreshold: 30,
  maxAlertsPerDay: 100
})

// 业务配置
const businessConfig = reactive({
  orderTimeoutHours: 24,
  maxTempDeviation: 2.0,
  minDeliveryDistance: 10
})

// 系统配置
const systemConfig = reactive({
  backupEnabled: true,
  logRetentionDays: 30,
  maintenanceMode: false,
  sessionTimeout: 30
})

// 默认配置（用于重置）
const defaultConfig = {
  alertConfig: {
    emailEnabled: true,
    smsEnabled: true,
    timeoutThreshold: 30,
    maxAlertsPerDay: 100
  },
  businessConfig: {
    orderTimeoutHours: 24,
    maxTempDeviation: 2.0,
    minDeliveryDistance: 10
  },
  systemConfig: {
    backupEnabled: true,
    logRetentionDays: 30,
    maintenanceMode: false,
    sessionTimeout: 30
  }
}

// 操作函数
const saveConfig = async () => {
  try {
    const configData = {
      alertConfig: { ...alertConfig },
      businessConfig: { ...businessConfig },
      systemConfig: { ...systemConfig }
    }
    
    // 保存到后端
    const response = await fetch('http://localhost:8080/database/system/save-config', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(configData)
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('✅ 配置保存成功')
      
      // 同时保存到本地存储
      localStorage.setItem('systemConfig', JSON.stringify(configData))
      console.log('配置已保存:', configData)
    } else {
      ElMessage.error(data.message || '配置保存失败')
    }
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存失败，请检查网络连接')
  }
}

const exportConfig = () => {
  try {
    // 准备导出数据
    const configData = {
      系统信息: {
        系统名称: 'Fresh Logistics 冷链物流智能监测预警系统',
        系统版本: 'v1.0.0',
        导出时间: new Date().toLocaleString(),
        数据库名: 'freshlogistics'
      },
      预警配置: {
        邮件预警: alertConfig.emailEnabled ? '启用' : '禁用',
        短信预警: alertConfig.smsEnabled ? '启用' : '禁用',
        默认超时阈值: alertConfig.timeoutThreshold + '分钟',
        每日最大预警数: alertConfig.maxAlertsPerDay
      },
      业务配置: {
        订单超时时间: businessConfig.orderTimeoutHours + '小时',
        最大温度偏差: businessConfig.maxTempDeviation + '℃',
        最小配送距离: businessConfig.minDeliveryDistance + '公里'
      },
      系统配置: {
        自动备份: systemConfig.backupEnabled ? '启用' : '禁用',
        日志保留天数: systemConfig.logRetentionDays + '天',
        维护模式: systemConfig.maintenanceMode ? '启用' : '禁用',
        会话超时: systemConfig.sessionTimeout + '分钟'
      }
    }
    
    // 方式1: 导出为JSON文件（便于导入）
    const jsonContent = JSON.stringify({
      alertConfig: { ...alertConfig },
      businessConfig: { ...businessConfig },
      systemConfig: { ...systemConfig },
      exportTime: new Date().toISOString()
    }, null, 2)
    
    const jsonBlob = new Blob([jsonContent], { type: 'application/json;charset=utf-8;' })
    const jsonLink = document.createElement('a')
    const jsonUrl = URL.createObjectURL(jsonBlob)
    
    jsonLink.setAttribute('href', jsonUrl)
    jsonLink.setAttribute('download', `系统配置_${new Date().toLocaleDateString()}.json`)
    jsonLink.style.visibility = 'hidden'
    
    document.body.appendChild(jsonLink)
    jsonLink.click()
    document.body.removeChild(jsonLink)
    
    // 方式2: 同时导出CSV格式的可读报告
    const csvLines = []
    csvLines.push('\ufeff' + configData.系统信息.系统名称) // 添加BOM
    csvLines.push('系统版本,' + configData.系统信息.系统版本)
    csvLines.push('导出时间,' + configData.系统信息.导出时间)
    csvLines.push('数据库名,' + configData.系统信息.数据库名)
    csvLines.push('')
    
    csvLines.push('预警配置')
    csvLines.push('配置项,配置值')
    Object.entries(configData.预警配置).forEach(([key, value]) => {
      csvLines.push(`${key},${value}`)
    })
    csvLines.push('')
    
    csvLines.push('业务配置')
    csvLines.push('配置项,配置值')
    Object.entries(configData.业务配置).forEach(([key, value]) => {
      csvLines.push(`${key},${value}`)
    })
    csvLines.push('')
    
    csvLines.push('系统配置')
    csvLines.push('配置项,配置值')
    Object.entries(configData.系统配置).forEach(([key, value]) => {
      csvLines.push(`${key},${value}`)
    })
    
    const csvContent = csvLines.join('\n')
    const csvBlob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const csvLink = document.createElement('a')
    const csvUrl = URL.createObjectURL(csvBlob)
    
    csvLink.setAttribute('href', csvUrl)
    csvLink.setAttribute('download', `系统配置报告_${new Date().toLocaleDateString()}.csv`)
    csvLink.style.visibility = 'hidden'
    
    document.body.appendChild(csvLink)
    csvLink.click()
    document.body.removeChild(csvLink)
    
    ElMessage.success('✅ 配置已导出（JSON + CSV格式）')
    
  } catch (error) {
    console.error('导出配置失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

const resetConfig = () => {
  ElMessageBox.confirm(
    '确定要重置所有配置为默认值吗？此操作不可恢复！',
    '重置配置确认',
    {
      confirmButtonText: '确定重置',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 恢复默认配置
    Object.assign(alertConfig, defaultConfig.alertConfig)
    Object.assign(businessConfig, defaultConfig.businessConfig)
    Object.assign(systemConfig, defaultConfig.systemConfig)
    
    // 清除本地存储的配置
    localStorage.removeItem('systemConfig')
    
    ElMessage.success('✅ 配置已重置为默认值')
    console.log('配置已重置:', {
      alertConfig,
      businessConfig,
      systemConfig
    })
  }).catch(() => {
    ElMessage.info('已取消重置操作')
  })
}

const testConnection = async () => {
  try {
    // 🔄 使用真实数据库健康检查接口（直接访问后端）
    const response = await fetch('http://localhost:8080/database/system/health')
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('✅ 数据库连接测试成功')
      console.log('系统健康状态:', data.data)
      
      // 更新页面显示的统计数据
      const health = data.data
      if (health.table_counts) {
        const counts = health.table_counts
        totalRecords.value = Object.values(counts).reduce((sum, count) => sum + (count || 0), 0)
      }
    } else {
      ElMessage.error(`连接测试失败: ${data.message}`)
    }
  } catch (error) {
    ElMessage.error('❌ 连接测试失败：' + error.message)
  }
}

// 加载保存的配置
const loadSavedConfig = () => {
  try {
    const savedConfig = localStorage.getItem('systemConfig')
    if (savedConfig) {
      const config = JSON.parse(savedConfig)
      
      // 恢复预警配置
      if (config.alertConfig) {
        Object.assign(alertConfig, config.alertConfig)
      }
      
      // 恢复业务配置
      if (config.businessConfig) {
        Object.assign(businessConfig, config.businessConfig)
      }
      
      // 恢复系统配置
      if (config.systemConfig) {
        Object.assign(systemConfig, config.systemConfig)
      }
      
      console.log('✅ 已加载保存的配置:', config)
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

onMounted(() => {
  console.log('系统设置页面已加载')
  loadSavedConfig() // 加载保存的配置
  testConnection() // 测试数据库连接
})
</script>

<style scoped>
.system-page {
  padding: 0;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.system-status {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.system-status h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.status-label {
  color: #666;
  font-size: 14px;
}

.status-value {
  font-weight: 600;
  color: #333;
}

.status-value.success {
  color: #52c41a;
}

.config-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.config-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.config-section h4 {
  color: #333;
  font-size: 16px;
  margin-bottom: 16px;
}

.config-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.config-item:last-child {
  border-bottom: none;
}

.config-label {
  min-width: 120px;
  color: #333;
  font-size: 14px;
}

.database-info {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.database-info h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.db-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.db-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.db-label {
  color: #666;
  font-size: 13px;
}

.db-value {
  color: #1890ff;
  font-weight: 600;
  font-size: 13px;
}

.action-buttons {
  text-align: center;
  padding: 20px;
}

.action-buttons .el-button {
  margin: 0 8px;
}
</style>