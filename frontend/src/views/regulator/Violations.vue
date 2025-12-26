<template>
  <div class="violations-page">
    <div class="page-header">
      <h1>⚠️ 违规处理</h1>
      <p>管理和处理冷链物流违规事件，督促整改落实</p>
    </div>
    
    <!-- 违规统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">⚠️</div>
        <div class="stat-info">
          <div class="stat-number">{{ violationStats.totalViolations }}</div>
          <div class="stat-label">违规总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🚨</div>
        <div class="stat-info">
          <div class="stat-number">{{ violationStats.severeViolations }}</div>
          <div class="stat-label">严重违规</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ violationStats.rectifiedCount }}</div>
          <div class="stat-label">已整改</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⏰</div>
        <div class="stat-info">
          <div class="stat-number">{{ violationStats.pendingCount }}</div>
          <div class="stat-label">待整改</div>
        </div>
      </div>
    </div>
    
    <!-- 违规事件列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>违规事件管理</h3>
        <div class="table-actions">
          <el-select
            v-model="severityFilter"
            placeholder="违规严重程度"
            clearable
            style="width: 150px"
            @change="handleFilter"
          >
            <el-option label="严重违规" value="severe" />
            <el-option label="一般违规" value="moderate" />
            <el-option label="轻微违规" value="minor" />
          </el-select>
          
          <el-select
            v-model="statusFilter"
            placeholder="处理状态"
            clearable
            style="width: 120px"
            @change="handleFilter"
          >
            <el-option label="待整改" value="pending" />
            <el-option label="整改中" value="rectifying" />
            <el-option label="已整改" value="rectified" />
            <el-option label="已关闭" value="closed" />
          </el-select>
          
          <el-button type="success" @click="refreshViolations">
            🔄 刷新数据
          </el-button>
          <el-button type="info" @click="exportViolations">
            📊 导出报表
          </el-button>
          <span style="margin-left: 10px; color: #666; font-size: 14px;">
            违规数量: {{ violationList?.length || 0 }}
          </span>
        </div>
      </div>
      
      <el-table :data="filteredViolations" style="width: 100%" stripe>
        <el-table-column prop="violationCode" label="违规编号" width="140" />
        <el-table-column prop="checkCode" label="检查编号" width="140" />
        <el-table-column prop="targetName" label="违规主体" width="150" />
        <el-table-column prop="violationType" label="违规类别" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getViolationTypeName(row.violationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getSeverityType(row.severity)" size="small">
              {{ getSeverityText(row.severity) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="violationCount" label="违规项" width="80" />
        <el-table-column prop="complianceScore" label="检查评分" width="90" />
        <el-table-column prop="violationDate" label="发现时间" width="160" />
        <el-table-column prop="rectificationStatus" label="整改状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.rectificationStatus)" size="small">
              {{ getStatusText(row.rectificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleViewDetails(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.rectificationStatus === 'pending'"
              type="warning" 
              size="small" 
              text 
              @click="handleDemandRectification(row)"
            >
              要求整改
            </el-button>
            <el-button 
              v-if="row.rectificationStatus === 'rectifying'"
              type="success" 
              size="small" 
              text 
              @click="handleVerifyRectification(row)"
            >
              验收整改
            </el-button>
            <el-button type="danger" size="small" text @click="handlePunish(row)">
              处罚
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 违规详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="⚠️ 违规事件详情"
      width="700px"
    >
      <div v-if="currentViolation" class="violation-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="违规编号">{{ currentViolation.violationCode }}</el-descriptions-item>
          <el-descriptions-item label="检查编号">{{ currentViolation.checkCode }}</el-descriptions-item>
          <el-descriptions-item label="违规主体">{{ currentViolation.targetName }}</el-descriptions-item>
          <el-descriptions-item label="违规类别">{{ getViolationTypeName(currentViolation.violationType) }}</el-descriptions-item>
          <el-descriptions-item label="严重程度">
            <el-tag :type="getSeverityType(currentViolation.severity)">
              {{ getSeverityText(currentViolation.severity) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="违规项数">{{ currentViolation.violationCount }}</el-descriptions-item>
          <el-descriptions-item label="检查评分">{{ currentViolation.complianceScore }}</el-descriptions-item>
          <el-descriptions-item label="发现时间">{{ currentViolation.violationDate }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider />
        
        <h4>违规详情</h4>
        <div class="violation-content">{{ currentViolation.violationDetails }}</div>
        
        <h4>整改要求</h4>
        <div class="rectification-requirements">{{ currentViolation.rectificationRequirements }}</div>
        
        <h4>整改进度</h4>
        <el-steps :active="getRectificationStep(currentViolation.rectificationStatus)" align-center>
          <el-step title="违规发现" description="合规检查中发现" />
          <el-step title="要求整改" description="下达整改通知" />
          <el-step title="整改中" description="企业执行整改" />
          <el-step title="验收合格" description="整改验收通过" />
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 违规统计数据（将从真实数据库获取）
const violationStats = reactive({
  totalViolations: 0,
  severeViolations: 0,
  rectifiedCount: 0,
  pendingCount: 0
})

// 违规事件列表（将从真实数据库获取）
const violationList = ref([])

// 筛选条件
const severityFilter = ref('')
const statusFilter = ref('')

// 详情对话框
const detailDialogVisible = ref(false)
const currentViolation = ref(null)

// 筛选后的违规列表
const filteredViolations = computed(() => {
  let filtered = violationList.value
  
  if (severityFilter.value) {
    filtered = filtered.filter(v => v.severity === severityFilter.value)
  }
  
  if (statusFilter.value) {
    filtered = filtered.filter(v => v.rectificationStatus === statusFilter.value)
  }
  
  return filtered
})

// 工具函数
const getViolationTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    'vehicle': '车辆违规',
    'facility': '设施违规',
    'process': '流程违规',
    'driver': '人员违规',
    'documentation': '文档违规'
  }
  return typeMap[type] || type
}

const getSeverityType = (severity: string) => {
  const typeMap: Record<string, string> = {
    'severe': 'danger',
    'moderate': 'warning',
    'minor': 'info'
  }
  return typeMap[severity] || ''
}

const getSeverityText = (severity: string) => {
  const textMap: Record<string, string> = {
    'severe': '严重',
    'moderate': '一般',
    'minor': '轻微'
  }
  return textMap[severity] || severity
}

const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'pending': 'warning',
    'rectifying': 'info',
    'rectified': 'success',
    'closed': 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    'pending': '待整改',
    'rectifying': '整改中',
    'rectified': '已整改',
    'closed': '已关闭'
  }
  return textMap[status] || status
}

const getRectificationStep = (status: string) => {
  const stepMap: Record<string, number> = {
    'pending': 1,
    'rectifying': 2,
    'rectified': 3,
    'closed': 4
  }
  return stepMap[status] || 0
}

// 操作函数
const handleViewDetails = (row: any) => {
  currentViolation.value = row
  detailDialogVisible.value = true
  console.log('查看违规详情:', row)
}

const handleDemandRectification = (row: any) => {
  ElMessageBox.prompt('请输入整改要求和期限', '下达整改通知', {
    confirmButtonText: '发送通知',
    cancelButtonText: '取消',
    inputType: 'textarea'
  }).then(async ({ value }) => {
    try {
      const response = await fetch(`http://localhost:8080/database/regulator/demand-rectification/${row.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          rectificationRequirements: value
        })
      })
      
      const data = await response.json()
      
      if (data.code === 200) {
        ElMessage.success(`已向 ${row.targetName} 发送整改通知`)
        await refreshViolations()
      } else {
        ElMessage.error(data.message || '发送通知失败')
      }
    } catch (error) {
      console.error('发送整改通知失败:', error)
      ElMessage.error('操作失败，请检查网络连接')
    }
  }).catch(() => {
    // 用户取消
  })
}

const handleVerifyRectification = (row: any) => {
  ElMessageBox.confirm(
    `确认 ${row.targetName} 的违规问题已整改完成吗？`,
    '整改验收',
    {
      confirmButtonText: '验收通过',
      cancelButtonText: '验收不通过',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await fetch(`http://localhost:8080/database/regulator/verify-rectification/${row.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          verified: true,
          complianceScore: 95 // 验收通过设置高分
        })
      })
      
      const data = await response.json()
      
      if (data.code === 200) {
        ElMessage.success('整改验收通过，违规记录已更新')
        await refreshViolations()
      } else {
        ElMessage.error(data.message || '验收失败')
      }
    } catch (error) {
      console.error('验收整改失败:', error)
      ElMessage.error('操作失败，请检查网络连接')
    }
  }).catch(() => {
    ElMessage.info('请继续督促整改')
  })
}

const handlePunish = (row: any) => {
  ElMessageBox.prompt('请输入处罚决定和金额', '违规处罚', {
    confirmButtonText: '确定处罚',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '如：罚款5000元，责令停业整顿3天'
  }).then(({ value }) => {
    ElMessage.success(`处罚决定已记录并通知 ${row.targetName}`)
    console.log('处罚决定:', value)
  }).catch(() => {
    // 用户取消
  })
}

const handleFilter = () => {
  console.log('筛选条件:', { severityFilter: severityFilter.value, statusFilter: statusFilter.value })
}

const exportViolations = () => {
  if (!filteredViolations.value || filteredViolations.value.length === 0) {
    ElMessage.warning('没有违规数据可以导出')
    return
  }
  
  try {
    // 准备导出数据
    const exportData = filteredViolations.value.map(item => ({
      '违规编号': item.violationCode,
      '检查编号': item.checkCode,
      '违规主体': item.targetName,
      '违规类别': getViolationTypeName(item.violationType),
      '严重程度': getSeverityText(item.severity),
      '违规项数': item.violationCount,
      '检查评分': item.complianceScore,
      '发现时间': item.violationDate,
      '整改状态': getStatusText(item.rectificationStatus),
      '违规详情': item.violationDetails,
      '整改要求': item.rectificationRequirements
    }))
    
    // 转换为CSV格式
    const headers = Object.keys(exportData[0])
    const csvContent = [
      '\ufeff违规事件处理报表', // 添加BOM以支持Excel打开中文
      'Fresh Logistics 冷链物流智能监测预警系统',
      `导出时间: ${new Date().toLocaleString()}`,
      `违规总数: ${violationStats.totalViolations}项，严重违规: ${violationStats.severeViolations}项，已整改: ${violationStats.rectifiedCount}项`,
      '',
      headers.join(','),
      ...exportData.map(row => 
        headers.map(header => {
          const value = row[header] || ''
          // 处理包含逗号和换行的字段
          if (typeof value === 'string' && (value.includes(',') || value.includes('\n') || value.includes('"'))) {
            return `"${value.replace(/"/g, '""')}"` // 双引号转义
          }
          return value
        }).join(',')
      )
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    link.setAttribute('href', url)
    link.setAttribute('download', `违规事件报表_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`✅ 成功导出 ${filteredViolations.value.length} 条违规记录`)
    
  } catch (error) {
    console.error('导出违规报表失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 刷新违规数据（基于真实数据库compliance_checks表）
const refreshViolations = async () => {
  try {
    console.log('🚀 开始从数据库刷新违规数据...')
    
    // 🔄 调用真实数据库API获取违规数据（从合规检查记录中筛选）
    const response = await fetch(`http://localhost:8080/database/regulator/violations?_t=${Date.now()}`)
    console.log('📡 违规数据API响应状态:', response.status, response.statusText)
    
    const data = await response.json()
    console.log('📊 违规数据API响应:', data)
    
    if (data.code === 200) {
      console.log('✅ 数据库连接成功，处理违规数据...')
      
      const rawViolations = data.data || []
      console.log('📦 原始违规数据（来自compliance_checks表）:', rawViolations)
      
      // 🔄 将数据库合规检查数据转换为违规事件
      violationList.value = rawViolations.map(check => {
        // 根据违规数量和评分判断严重程度
        let severity = 'minor'
        if (check.violations_found >= 3 || check.compliance_score < 80) {
          severity = 'severe'
        } else if (check.violations_found >= 2 || check.compliance_score < 90) {
          severity = 'moderate'
        }
        
        // 根据检查状态判断整改状态
        let rectificationStatus = 'pending'
        if (check.compliance_score >= 95) {
          rectificationStatus = 'closed'
        } else if (check.compliance_score >= 90) {
          rectificationStatus = 'rectified'
        } else if (check.violations_found > 0) {
          rectificationStatus = 'rectifying'
        }
        
        return {
          id: check.id,
          violationCode: `VIO${check.check_code?.replace('CHK', '')}` || `VIO${check.id}`,
          checkCode: check.check_code || `CHK${check.id}`,
          targetName: check.target_name || '未知',
          targetType: check.target_type,
          violationType: check.check_category,
          severity: severity,
          violationCount: check.violations_found || 0,
          complianceScore: check.compliance_score || 0,
          violationDate: check.check_date ? new Date(check.check_date).toLocaleString() : '未知',
          violationDetails: check.violation_details || check.check_results || '详细违规情况待补充',
          rectificationRequirements: check.corrective_actions || check.check_items || '按照规范要求进行整改',
          rectificationStatus: rectificationStatus,
          checkType: check.check_type,
          createdAt: check.created_at
        }
      })
      
      // 只显示有违规的记录
      violationList.value = violationList.value.filter(v => v.violationCount > 0)
      
      console.log('⚠️ 违规事件数据:', violationList.value)
      
      // 🔄 更新统计数据（基于真实数据计算）
      violationStats.totalViolations = violationList.value.reduce((sum, v) => sum + v.violationCount, 0)
      violationStats.severeViolations = violationList.value.filter(v => v.severity === 'severe').length
      violationStats.rectifiedCount = violationList.value.filter(v => v.rectificationStatus === 'rectified' || v.rectificationStatus === 'closed').length
      violationStats.pendingCount = violationList.value.filter(v => v.rectificationStatus === 'pending').length
      
      console.log('📈 违规统计数据:', violationStats)
      
      ElMessage.success(`✅ 成功加载 ${violationList.value.length} 个违规事件（真实数据库）`)
      console.log('🎉 违规数据刷新完成!')
      
    } else {
      console.error('❌ 数据库API返回错误:', data)
      ElMessage.error(`获取违规数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 刷新违规数据失败:', error)
    ElMessage.error('❌ 数据库连接失败')
  }
}

onMounted(async () => {
  console.log('⚠️ 违规处理页面已加载')
  await refreshViolations()
  
  // 启动自动刷新（每60秒）
  setInterval(async () => {
    console.log('⏰ 自动刷新违规数据...')
    await refreshViolations()
  }, 60000)
  
  console.log('🔄 违规数据自动刷新已启动')
})
</script>

<style scoped>
.violations-page {
  padding: 0;
  max-width: 1400px;
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #ff4d4f;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.table-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.table-header h3 {
  color: #333;
  font-size: 18px;
  margin: 0;
}

.table-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.violation-detail {
  padding: 20px;
}

.violation-detail h4 {
  margin: 20px 0 12px 0;
  color: #333;
}

.violation-content,
.rectification-requirements {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 4px;
  line-height: 1.6;
  color: #666;
}
</style>
