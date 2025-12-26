<template>
  <div class="reports-page">
    <div class="page-header">
      <h1>📊 监管报告</h1>
      <p>生成和管理监管报告，分析合规趋势</p>
    </div>
    
    <!-- 报告统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📄</div>
        <div class="stat-info">
          <div class="stat-number">{{ reportStats.totalReports }}</div>
          <div class="stat-label">报告总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✍️</div>
        <div class="stat-info">
          <div class="stat-number">{{ reportStats.draftReports }}</div>
          <div class="stat-label">草稿报告</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ reportStats.publishedReports }}</div>
          <div class="stat-label">已发布</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <div class="stat-number">{{ reportStats.monthlyAvg }}</div>
          <div class="stat-label">月均报告</div>
        </div>
      </div>
    </div>
    
    <!-- 快速生成报告 -->
    <div class="quick-generate">
      <h3>🚀 快速生成报告</h3>
      <div class="generate-options">
        <div class="option-item">
          <div class="option-icon">📅</div>
          <div class="option-content">
            <div class="option-title">日报</div>
            <div class="option-desc">生成今日合规检查日报</div>
          </div>
          <el-button type="primary" size="small" @click="generateDailyReport">生成</el-button>
        </div>
        
        <div class="option-item">
          <div class="option-icon">📊</div>
          <div class="option-content">
            <div class="option-title">周报</div>
            <div class="option-desc">生成本周合规统计周报</div>
          </div>
          <el-button type="success" size="small" @click="generateWeeklyReport">生成</el-button>
        </div>
        
        <div class="option-item">
          <div class="option-icon">📈</div>
          <div class="option-content">
            <div class="option-title">月报</div>
            <div class="option-desc">生成本月监管分析月报</div>
          </div>
          <el-button type="warning" size="small" @click="generateMonthlyReport">生成</el-button>
        </div>
        
        <div class="option-item">
          <div class="option-icon">⚡</div>
          <div class="option-content">
            <div class="option-title">专项报告</div>
            <div class="option-desc">生成特定主题专项报告</div>
          </div>
          <el-button type="info" size="small" @click="generateSpecialReport">生成</el-button>
        </div>
      </div>
    </div>
    
    <!-- 报告列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>📋 监管报告列表</h3>
        <div class="table-actions">
          <el-select v-model="reportTypeFilter" placeholder="报告类型" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="日报" value="daily" />
            <el-option label="周报" value="weekly" />
            <el-option label="月报" value="monthly" />
            <el-option label="专项" value="special" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="状态" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="草稿" value="draft" />
            <el-option label="审核中" value="reviewing" />
            <el-option label="已发布" value="published" />
          </el-select>
          <el-button type="success" @click="refreshReports">刷新</el-button>
        </div>
      </div>
      
      <el-table :data="reportList" style="width: 100%" stripe>
        <el-table-column prop="reportCode" label="报告编号" width="140" />
        <el-table-column prop="reportType" label="报告类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getReportTypeColor(row.reportType)" size="small">
              {{ getReportTypeName(row.reportType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportTitle" label="报告标题" min-width="200" />
        <el-table-column prop="reportPeriod" label="报告周期" width="180">
          <template #default="{ row }">
            {{ row.reportPeriodStart }} ~ {{ row.reportPeriodEnd }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="110" />
        <el-table-column prop="reportStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getReportStatusType(row.reportStatus)" size="small">
              {{ getReportStatusText(row.reportStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="110" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.reportStatus === 'draft'"
              type="success" 
              size="small" 
              text 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="row.reportStatus === 'published'"
              type="info" 
              size="small" 
              text 
              @click="handleExport(row)"
            >
              导出
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 查看报告详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="currentReport?.reportTitle || '报告详情'"
      width="900px"
    >
      <div v-if="currentReport" class="report-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报告编号">{{ currentReport.reportCode }}</el-descriptions-item>
          <el-descriptions-item label="报告类型">
            <el-tag :type="getReportTypeColor(currentReport.reportType)" size="small">
              {{ getReportTypeName(currentReport.reportType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="报告周期" :span="2">
            {{ currentReport.reportPeriodStart }} ~ {{ currentReport.reportPeriodEnd }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentReport.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ currentReport.publishTime || '未发布' }}</el-descriptions-item>
          <el-descriptions-item label="报告状态" :span="2">
            <el-tag :type="getReportStatusType(currentReport.reportStatus)" size="small">
              {{ getReportStatusText(currentReport.reportStatus) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider />
        
        <h4>📊 报告摘要</h4>
        <p class="report-summary">{{ currentReport.reportSummary }}</p>
        
        <h4>📋 报告内容</h4>
        <div class="report-content">
          <p>{{ currentReport.reportContent }}</p>
        </div>
        
        <div v-if="currentReport.keyFindings">
          <h4>🔍 主要发现</h4>
          <p>{{ currentReport.keyFindings }}</p>
        </div>
        
        <div v-if="currentReport.recommendations">
          <h4>💡 建议措施</h4>
          <p>{{ currentReport.recommendations }}</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleExportCurrent">
          导出此报告
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 报告统计数据（将从真实数据库获取）
const reportStats = reactive({
  totalReports: 0,
  draftReports: 0,
  publishedReports: 0,
  monthlyAvg: 0
})

// 筛选条件
const reportTypeFilter = ref('')
const statusFilter = ref('')

// 报告列表（将从真实数据库获取）
const reportList = ref([])

// 查看对话框
const viewDialogVisible = ref(false)
const currentReport = ref<any>(null)

// 获取报告类型名称和颜色
const getReportTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    daily: '日报',
    weekly: '周报',
    monthly: '月报',
    quarterly: '季报',
    annual: '年报',
    special: '专项'
  }
  return typeMap[type] || type
}

const getReportTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    daily: 'primary',
    weekly: 'success',
    monthly: 'warning',
    quarterly: 'danger',
    annual: 'danger',
    special: 'info'
  }
  return colorMap[type] || 'primary'
}

// 获取报告状态类型和文本
const getReportStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    draft: 'info',
    reviewing: 'warning',
    approved: 'success',
    published: 'success'
  }
  return typeMap[status] || 'info'
}

const getReportStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    draft: '草稿',
    reviewing: '审核中',
    approved: '已批准',
    published: '已发布'
  }
  return textMap[status] || status
}

// 生成报告函数
const generateDailyReport = async () => {
  try {
    ElMessage.info('正在生成今日合规检查日报...')
    
    const response = await fetch('http://localhost:8080/database/regulator/generate-report', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        reportType: 'daily',
        reportTitle: `${new Date().toLocaleDateString()}合规检查日报`
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('✅ 日报生成成功')
      await refreshReports() // 刷新报告列表
    } else {
      ElMessage.error(data.message || '日报生成失败')
    }
  } catch (error) {
    console.error('生成日报失败:', error)
    ElMessage.error('生成失败，请检查网络连接')
  }
}

const generateWeeklyReport = async () => {
  try {
    ElMessage.info('正在生成本周合规统计周报...')
    
    const response = await fetch('http://localhost:8080/database/regulator/generate-report', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        reportType: 'weekly',
        reportTitle: `第${getWeekNumber()}周合规统计周报`
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('✅ 周报生成成功')
      await refreshReports() // 刷新报告列表
    } else {
      ElMessage.error(data.message || '周报生成失败')
    }
  } catch (error) {
    console.error('生成周报失败:', error)
    ElMessage.error('生成失败，请检查网络连接')
  }
}

const generateMonthlyReport = async () => {
  try {
    ElMessage.info('正在生成本月监管分析月报...')
    
    const response = await fetch('http://localhost:8080/database/regulator/generate-report', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        reportType: 'monthly',
        reportTitle: `${new Date().getMonth() + 1}月份冷链物流监管月报`
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('✅ 月报生成成功')
      await refreshReports() // 刷新报告列表
    } else {
      ElMessage.error(data.message || '月报生成失败')
    }
  } catch (error) {
    console.error('生成月报失败:', error)
    ElMessage.error('生成失败，请检查网络连接')
  }
}

const generateSpecialReport = async () => {
  try {
    ElMessage.info('正在生成专项检查报告...')
    
    const response = await fetch('http://localhost:8080/database/regulator/generate-report', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        reportType: 'special',
        reportTitle: '国庆节前安全专项检查报告'
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('✅ 专项报告生成成功')
      await refreshReports() // 刷新报告列表
    } else {
      ElMessage.error(data.message || '专项报告生成失败')
    }
  } catch (error) {
    console.error('生成专项报告失败:', error)
    ElMessage.error('生成失败，请检查网络连接')
  }
}

// 获取当前周数
const getWeekNumber = () => {
  const now = new Date()
  const start = new Date(now.getFullYear(), 0, 1)
  const diff = now.getTime() - start.getTime()
  const oneWeek = 1000 * 60 * 60 * 24 * 7
  return Math.ceil(diff / oneWeek)
}

// 操作函数
const handleView = async (row: any) => {
  try {
    // 获取报告详情
    const response = await fetch(`http://localhost:8080/database/regulator/report-detail/${row.id}`)
    const data = await response.json()
    
    if (data.code === 200) {
      const report = data.data
      currentReport.value = {
        reportCode: report.report_code,
        reportType: report.report_type,
        reportTitle: report.report_title,
        reportPeriodStart: report.report_period_start,
        reportPeriodEnd: report.report_period_end,
        createdAt: report.created_at ? new Date(report.created_at).toLocaleString() : '',
        publishTime: report.publish_time ? new Date(report.publish_time).toLocaleString() : null,
        reportStatus: report.report_status,
        reportSummary: report.summary || report.report_summary || '暂无摘要',
        reportContent: report.report_content || '报告内容暂未填写',
        keyFindings: report.key_findings,
        recommendations: report.recommendations
      }
      viewDialogVisible.value = true
    } else {
      ElMessage.error('获取报告详情失败')
    }
  } catch (error) {
    console.error('获取报告详情失败:', error)
    ElMessage.error('获取报告详情失败')
  }
}

// 导出当前查看的报告
const handleExportCurrent = () => {
  if (!currentReport.value) return
  handleExport(currentReport.value)
  viewDialogVisible.value = false
}

const handleEdit = (row: any) => {
  ElMessage.info(`编辑报告: ${row.reportTitle}`)
}

const handleExport = async (row: any) => {
  try {
    ElMessage.info('正在导出报告...')
    
    // 首先获取完整的报告详情
    const response = await fetch(`http://localhost:8080/database/regulator/report-detail/${row.id}`)
    const data = await response.json()
    
    if (data.code !== 200) {
      ElMessage.error('获取报告详情失败')
      return
    }
    
    const report = data.data
    
    // 准备完整的导出数据
    const reportData = {
      报告编号: report.report_code || row.reportCode,
      报告类型: getReportTypeName(report.report_type || row.reportType),
      报告标题: report.report_title || row.reportTitle,
      报告周期开始: report.report_period_start || row.reportPeriodStart,
      报告周期结束: report.report_period_end || row.reportPeriodEnd,
      创建时间: report.created_at ? new Date(report.created_at).toLocaleString() : row.createdAt,
      发布时间: report.publish_time ? new Date(report.publish_time).toLocaleString() : '未发布',
      报告状态: getReportStatusText(report.report_status || row.reportStatus),
      报告摘要: report.summary || report.report_summary || '',
      主要发现: report.key_findings || '',
      建议措施: report.recommendations || ''
    }
    
    // 转换为CSV格式
    const csvLines = []
    csvLines.push('\ufeff' + (report.report_title || row.reportTitle)) // 添加BOM
    csvLines.push('Fresh Logistics 冷链物流智能监测预警系统')
    csvLines.push('监管报告')
    csvLines.push('')
    csvLines.push('基本信息')
    csvLines.push('字段,内容')
    csvLines.push(`报告编号,${reportData.报告编号}`)
    csvLines.push(`报告类型,${reportData.报告类型}`)
    csvLines.push(`报告标题,${reportData.报告标题}`)
    csvLines.push(`报告周期,${reportData.报告周期开始} ~ ${reportData.报告周期结束}`)
    csvLines.push(`创建时间,${reportData.创建时间}`)
    csvLines.push(`发布时间,${reportData.发布时间}`)
    csvLines.push(`报告状态,${reportData.报告状态}`)
    csvLines.push('')
    
    // 报告摘要
    if (reportData.报告摘要) {
      csvLines.push('报告摘要')
      csvLines.push(reportData.报告摘要.replace(/,/g, '，')) // 替换逗号避免CSV格式问题
      csvLines.push('')
    }
    
    // 主要发现
    if (reportData.主要发现) {
      csvLines.push('主要发现')
      csvLines.push(reportData.主要发现.replace(/,/g, '，'))
      csvLines.push('')
    }
    
    // 建议措施
    if (reportData.建议措施) {
      csvLines.push('建议措施')
      csvLines.push(reportData.建议措施.replace(/,/g, '，'))
      csvLines.push('')
    }
    
    // 报告内容
    csvLines.push('报告内容')
    const reportContent = report.report_content || '本报告基于监管数据库实时数据生成，包含合规检查记录、违规事件处理、整改措施跟踪等内容。'
    csvLines.push(reportContent.replace(/,/g, '，'))
    
    const csvContent = csvLines.join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    const fileName = `${report.report_title || row.reportTitle}_${report.report_code || row.reportCode}.csv`
    
    link.setAttribute('href', url)
    link.setAttribute('download', fileName)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`✅ 报告导出成功`)
    
  } catch (error) {
    console.error('导出报告失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

const refreshReports = async () => {
  try {
    console.log('🚀 开始刷新监管报告数据...')
    
    // 🔄 调用真实数据库API获取监管报告数据
    const [reportsRes, statsRes] = await Promise.all([
      fetch('http://localhost:8080/database/regulator/reports'),
      fetch('http://localhost:8080/database/regulator/report-statistics')
    ])
    
    const reportsData = await reportsRes.json()
    const statsData = await statsRes.json()
    
    console.log('📊 报告列表API响应:', reportsData)
    console.log('📊 报告统计API响应:', statsData)
    
    if (reportsData.code === 200) {
      // 🔄 更新报告列表数据
      const rawReports = reportsData.data || []
      reportList.value = rawReports.map(report => ({
        id: report.id,
        reportCode: report.report_code || `RPT${report.id}`,
        reportType: report.report_type,
        reportTitle: report.report_title,
        reportPeriodStart: report.report_period_start ? new Date(report.report_period_start).toLocaleDateString() : '未知',
        reportPeriodEnd: report.report_period_end ? new Date(report.report_period_end).toLocaleDateString() : '未知',
        createdAt: report.created_at ? new Date(report.created_at).toLocaleDateString() : '未知',
        reportStatus: report.report_status,
        publishTime: report.published_at ? new Date(report.published_at).toLocaleDateString() : null
      }))
      
      console.log('📊 真实报告列表:', reportList.value)
    }
    
    if (statsData.code === 200) {
      // 🔄 更新统计数据
      const stats = statsData.data
      reportStats.totalReports = stats.total_reports || 0
      reportStats.draftReports = stats.draft_reports || 0
      reportStats.publishedReports = stats.published_reports || 0
      reportStats.monthlyAvg = Math.round((stats.total_reports || 0) / 3) // 假设按季度计算
      
      console.log('📈 真实报告统计:', reportStats)
    }
    
    ElMessage.success('✅ 监管报告数据刷新成功（真实数据库）')
    console.log('🎉 监管报告数据刷新完成!')
    
  } catch (error) {
    console.error('💥 刷新报告数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

onMounted(() => {
  console.log('监管员报告管理页面已加载')
  refreshReports()
})
</script>

<style scoped>
.reports-page {
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
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.quick-generate {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.quick-generate h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.generate-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  gap: 12px;
}

.option-icon {
  font-size: 24px;
  width: 40px;
  text-align: center;
}

.option-content {
  flex: 1;
}

.option-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.option-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
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
}

.report-detail {
  padding: 10px;
}

.report-detail h4 {
  color: #333;
  margin: 20px 0 12px 0;
  font-size: 16px;
  font-weight: 600;
}

.report-summary {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 6px;
  color: #333;
  line-height: 1.8;
  margin: 0;
}

.report-content {
  background: #fff;
  padding: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  min-height: 200px;
}

.report-content p {
  color: #666;
  line-height: 1.8;
  margin: 0;
  white-space: pre-wrap;
}
</style>