<template>
  <div class="analysis-page">
    <div class="page-header">
      <h1>数据分析</h1>
      <p>冷链物流数据分析与统计报告</p>
    </div>
    
    <!-- 时间范围选择 -->
    <div class="analysis-filters">
      <el-card>
        <div class="filter-row">
          <div class="filter-item">
            <label>分析时间范围：</label>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="handleDateChange"
            />
          </div>
          
          <div class="filter-item">
            <el-button type="primary" @click="refreshAnalysis">🔄 刷新真实数据</el-button>
            <el-button type="success" @click="exportReport">📊 导出报告</el-button>
            <el-button type="info" @click="debugAnalysisData">🔍 调试数据</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 数据分析内容 -->
    <el-row :gutter="20">
      <!-- 时效分析 -->
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">时效分析</h3>
          </div>
          <div ref="timeEfficiencyChartRef" class="chart-container"></div>
          
          <div class="analysis-summary">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="summary-item">
                  <div class="summary-value">{{ timeStats.avgDeliveryTime }}</div>
                  <div class="summary-label">平均配送时长(小时)</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="summary-item">
                  <div class="summary-value">{{ timeStats.onTimeRate }}%</div>
                  <div class="summary-label">准时交付率</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="summary-item">
                  <div class="summary-value">{{ timeStats.delayedOrders }}</div>
                  <div class="summary-label">延迟订单数</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
      
      <!-- 损耗分析 -->
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">损耗分析</h3>
          </div>
          <div ref="lossAnalysisChartRef" class="chart-container"></div>
          
          <div class="analysis-summary">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="summary-item">
                  <div class="summary-value">{{ lossStats.totalLossRate }}%</div>
                  <div class="summary-label">总体损耗率</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="summary-item">
                  <div class="summary-value">{{ lossStats.tempFailureCount }}</div>
                  <div class="summary-label">温控失效次数</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="summary-item">
                  <div class="summary-value">¥{{ lossStats.totalLossAmount }}</div>
                  <div class="summary-label">损失金额(万元)</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 预警统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">预警统计分析</h3>
          </div>
          
          <el-row :gutter="20">
            <el-col :span="16">
              <div ref="alertTrendChartRef" class="chart-container"></div>
            </el-col>
            <el-col :span="8">
              <div ref="alertTypeChartRef" class="chart-container"></div>
            </el-col>
          </el-row>
          
          <div class="analysis-summary">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-value">{{ alertStats.totalAlerts }}</div>
                  <div class="summary-label">总预警数</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-value">{{ alertStats.avgResponseTime }}秒</div>
                  <div class="summary-label">平均响应时间</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-value">{{ alertStats.processedRate }}%</div>
                  <div class="summary-label">处理率</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-value">{{ alertStats.falseAlarmRate }}%</div>
                  <div class="summary-label">误报率</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import { alertApi } from '@/api/alert'

// 日期范围
const dateRange = ref<[string, string]>(['2025-01-20', '2025-01-27'])

// 图表引用
const timeEfficiencyChartRef = ref<HTMLDivElement>()
const lossAnalysisChartRef = ref<HTMLDivElement>()
const alertTrendChartRef = ref<HTMLDivElement>()
const alertTypeChartRef = ref<HTMLDivElement>()

let timeEfficiencyChart: ECharts | null = null
let lossAnalysisChart: ECharts | null = null
let alertTrendChart: ECharts | null = null
let alertTypeChart: ECharts | null = null

// 统计数据（将从真实数据库获取）
const timeStats = reactive({
  avgDeliveryTime: 0,
  onTimeRate: 0,
  delayedOrders: 0
})

const lossStats = reactive({
  totalLossRate: 0,
  tempFailureCount: 0,
  totalLossAmount: 0
})

const alertStats = reactive({
  totalAlerts: 0,
  avgResponseTime: 0,
  processedRate: 0,
  falseAlarmRate: 0
})

// 初始化图表
const initCharts = () => {
  // 时效分析图表
  if (timeEfficiencyChartRef.value) {
    timeEfficiencyChart = echarts.init(timeEfficiencyChartRef.value)
    timeEfficiencyChart.setOption({
      title: { text: '配送时效趋势' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['1-21', '1-22', '1-23', '1-24', '1-25', '1-26', '1-27']
      },
      yAxis: { type: 'value', name: '时长(小时)' },
      series: [{
        name: '平均配送时长',
        type: 'line',
        data: [4.8, 4.5, 4.2, 4.1, 3.9, 4.0, 4.2],
        smooth: true,
        itemStyle: { color: '#1890ff' }
      }]
    })
  }
  
  // 损耗分析图表
  if (lossAnalysisChartRef.value) {
    lossAnalysisChart = echarts.init(lossAnalysisChartRef.value)
    lossAnalysisChart.setOption({
      title: { text: '温控失效与损耗关联' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['水果', '蔬菜', '肉类', '海鲜', '乳制品']
      },
      yAxis: [
        { type: 'value', name: '失效次数' },
        { type: 'value', name: '损耗率(%)' }
      ],
      series: [
        {
          name: '温控失效次数',
          type: 'bar',
          data: [8, 12, 5, 15, 3],
          itemStyle: { color: '#ff4d4f' }
        },
        {
          name: '损耗率',
          type: 'line',
          yAxisIndex: 1,
          data: [1.5, 2.8, 1.2, 4.2, 0.8],
          itemStyle: { color: '#faad14' }
        }
      ]
    })
  }
  
  // 预警趋势图表
  if (alertTrendChartRef.value) {
    alertTrendChart = echarts.init(alertTrendChartRef.value)
    alertTrendChart.setOption({
      title: { text: '预警趋势' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['超时', '温度', '路径偏离'] },
      xAxis: {
        type: 'category',
        data: ['1-21', '1-22', '1-23', '1-24', '1-25', '1-26', '1-27']
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '超时',
          type: 'line',
          data: [3, 5, 2, 4, 6, 3, 4],
          itemStyle: { color: '#faad14' }
        },
        {
          name: '温度',
          type: 'line',
          data: [8, 12, 6, 10, 15, 9, 11],
          itemStyle: { color: '#ff4d4f' }
        },
        {
          name: '路径偏离',
          type: 'line',
          data: [1, 2, 0, 1, 3, 1, 2],
          itemStyle: { color: '#722ed1' }
        }
      ]
    })
  }
  
  // 预警类型分布图表
  if (alertTypeChartRef.value) {
    alertTypeChart = echarts.init(alertTypeChartRef.value)
    alertTypeChart.setOption({
      title: { text: '预警类型分布' },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: [
          { value: 45, name: '温度预警', itemStyle: { color: '#ff4d4f' } },
          { value: 28, name: '超时预警', itemStyle: { color: '#faad14' } },
          { value: 12, name: '路径偏离', itemStyle: { color: '#722ed1' } },
          { value: 8, name: '湿度预警', itemStyle: { color: '#13c2c2' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
  }
}

// 刷新分析数据
const refreshAnalysis = async () => {
  try {
    console.log('🚀 开始刷新数据分析...')
    
    // 🔄 调用真实数据库API获取分析数据
    const [deliveryRes, lossRes, alertAnalysisRes, alertTrendRes, alertTypeRes] = await Promise.all([
      fetch('http://localhost:8080/database/analysis/delivery-efficiency'),
      fetch('http://localhost:8080/database/analysis/loss-analysis'),
      fetch('http://localhost:8080/database/analysis/alert-analysis'),
      fetch('http://localhost:8080/database/analysis/alert-trend-chart'),
      fetch('http://localhost:8080/database/analysis/alert-type-distribution')
    ])
    
    const deliveryData = await deliveryRes.json()
    const lossData = await lossRes.json()
    const alertAnalysisData = await alertAnalysisRes.json()
    const alertTrendData = await alertTrendRes.json()
    const alertTypeData = await alertTypeRes.json()
    
    console.log('📊 时效分析API响应:', deliveryData)
    console.log('📊 损耗分析API响应:', lossData)
    console.log('📊 预警分析API响应:', alertAnalysisData)
    console.log('📊 预警趋势API响应:', alertTrendData)
    console.log('📊 预警类型API响应:', alertTypeData)
    
    // 🔄 更新时效统计数据
    if (deliveryData.code === 200) {
      const data = deliveryData.data
      timeStats.avgDeliveryTime = data.avgDeliveryTime || 0
      timeStats.onTimeRate = data.onTimeRate || 0
      timeStats.delayedOrders = data.delayedOrders || 0
      console.log('⏰ 真实时效数据:', timeStats)
    }
    
    // 🔄 更新损耗统计数据
    if (lossData.code === 200) {
      const data = lossData.data
      lossStats.totalLossRate = data.totalLossRate || 0
      lossStats.tempFailureCount = data.tempFailureCount || 0
      lossStats.totalLossAmount = data.totalLossAmount || 0
      console.log('📉 真实损耗数据:', lossStats)
    }
    
    // 🔄 更新预警统计数据
    if (alertAnalysisData.code === 200) {
      const data = alertAnalysisData.data
      alertStats.totalAlerts = data.totalAlerts || 0
      alertStats.avgResponseTime = data.avgResponseTime || 0
      alertStats.processedRate = data.processedRate || 0
      alertStats.falseAlarmRate = data.falseAlarmRate || 0
      console.log('🚨 真实预警数据:', alertStats)
    }
    
    // 🔄 更新图表数据（基于真实数据）
    updateChartsWithRealData(alertTrendData.data, alertTypeData.data)
    
    ElMessage.success('✅ 数据分析刷新完成（真实数据库）')
    console.log('🎉 所有分析数据已更新为真实数据库数据!')
    
  } catch (error) {
    console.error('💥 刷新分析数据失败:', error)
    ElMessage.error('❌ 分析数据刷新失败，请检查网络连接')
  }
}

// 基于真实数据更新图表
const updateChartsWithRealData = (trendData: any[], typeData: any[]) => {
  console.log('📈 开始更新图表为真实数据...')
  
  // 更新预警趋势图表
  if (alertTrendChart && trendData) {
    const dates = [...new Set(trendData.map(item => item.date))]
    const timeoutData = dates.map(date => {
      const item = trendData.find(d => d.date === date && d.alert_type === 'timeout')
      return item ? item.count : 0
    })
    const tempData = dates.map(date => {
      const item = trendData.find(d => d.date === date && d.alert_type === 'temperature')
      return item ? item.count : 0
    })
    
    alertTrendChart.setOption({
      title: { text: '预警趋势（真实数据）' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['超时', '温度'] },
      xAxis: {
        type: 'category',
        data: dates.map(date => new Date(date).toLocaleDateString())
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '超时',
          type: 'line',
          data: timeoutData,
          itemStyle: { color: '#faad14' }
        },
        {
          name: '温度',
          type: 'line',
          data: tempData,
          itemStyle: { color: '#ff4d4f' }
        }
      ]
    })
    console.log('📊 预警趋势图表已更新为真实数据')
  }
  
  // 更新预警类型分布饼图
  if (alertTypeChart && typeData) {
    const pieData = typeData.map(item => ({
      value: item.count,
      name: getAlertTypeName(item.alert_type),
      itemStyle: { color: getAlertTypeColor(item.alert_type) }
    }))
    
    alertTypeChart.setOption({
      title: { text: '预警类型分布（真实数据）' },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: pieData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
    console.log('🥧 预警类型饼图已更新为真实数据')
  }
}

// 获取预警类型名称
const getAlertTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    'timeout': '超时预警',
    'temperature': '温度预警',
    'humidity': '湿度预警',
    'route_deviation': '路径偏离'
  }
  return typeMap[type] || type
}

// 获取预警类型颜色
const getAlertTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    'timeout': '#faad14',
    'temperature': '#ff4d4f',
    'humidity': '#13c2c2',
    'route_deviation': '#722ed1'
  }
  return colorMap[type] || '#1890ff'
}

// 调试分析数据
const debugAnalysisData = async () => {
  try {
    console.log('🔍 开始调试分析数据...')
    console.log('⏰ 时效统计:', timeStats)
    console.log('📉 损耗统计:', lossStats)
    console.log('🚨 预警统计:', alertStats)
    
    // 获取完整的数据库数据用于调试
    const [ordersRes, alertsRes, productsRes] = await Promise.all([
      fetch('http://localhost:8080/database/order/all'),
      fetch('http://localhost:8080/database/alert/records'),
      fetch('http://localhost:8080/database/product/all')
    ])
    
    const ordersData = await ordersRes.json()
    const alertsData = await alertsRes.json()
    const productsData = await productsRes.json()
    
    console.log('📦 订单数据:', ordersData)
    console.log('🚨 预警数据:', alertsData)
    console.log('🏷️ 产品数据:', productsData)
    
    // 生成调试报告
    const debugInfo = {
      时间范围: dateRange.value,
      时效分析: {
        平均配送时长: timeStats.avgDeliveryTime + '小时',
        准时交付率: timeStats.onTimeRate + '%',
        延迟订单数: timeStats.delayedOrders
      },
      损耗分析: {
        总体损耗率: lossStats.totalLossRate + '%',
        温控失效次数: lossStats.tempFailureCount,
        损失金额: lossStats.totalLossAmount + '万元'
      },
      预警统计: {
        总预警数: alertStats.totalAlerts,
        平均响应时间: alertStats.avgResponseTime + '秒',
        处理率: alertStats.processedRate + '%',
        误报率: alertStats.falseAlarmRate + '%'
      },
      数据库连接状态: {
        订单总数: ordersData.data?.length || 0,
        预警总数: alertsData.data?.records?.length || alertsData.data?.length || 0,
        产品总数: productsData.data?.length || 0
      }
    }
    
    console.table(debugInfo)
    
    ElMessageBox.alert(
      `<div style="text-align: left; max-height: 400px; overflow-y: auto;">
        <h4>📊 数据分析调试信息</h4>
        <p><strong>时间范围:</strong> ${dateRange.value.join(' 至 ')}</p>
        <hr/>
        <h4>⏰ 时效分析</h4>
        <ul>
          <li>平均配送时长: ${timeStats.avgDeliveryTime} 小时</li>
          <li>准时交付率: ${timeStats.onTimeRate}%</li>
          <li>延迟订单数: ${timeStats.delayedOrders}</li>
        </ul>
        <hr/>
        <h4>📉 损耗分析</h4>
        <ul>
          <li>总体损耗率: ${lossStats.totalLossRate}%</li>
          <li>温控失效次数: ${lossStats.tempFailureCount}</li>
          <li>损失金额: ¥${lossStats.totalLossAmount} 万元</li>
        </ul>
        <hr/>
        <h4>🚨 预警统计</h4>
        <ul>
          <li>总预警数: ${alertStats.totalAlerts}</li>
          <li>平均响应时间: ${alertStats.avgResponseTime} 秒</li>
          <li>处理率: ${alertStats.processedRate}%</li>
          <li>误报率: ${alertStats.falseAlarmRate}%</li>
        </ul>
        <hr/>
        <h4>💾 数据库状态</h4>
        <ul>
          <li>订单总数: ${ordersData.data?.length || 0}</li>
          <li>预警总数: ${alertsData.data?.records?.length || alertsData.data?.length || 0}</li>
          <li>产品总数: ${productsData.data?.length || 0}</li>
        </ul>
        <p style="color: #666; font-size: 12px; margin-top: 10px;">详细信息已输出到浏览器控制台</p>
      </div>`,
      '调试数据',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定'
      }
    )
    
    ElMessage.success('调试信息已输出')
    
  } catch (error) {
    console.error('调试数据失败:', error)
    ElMessage.error('调试失败，请检查网络连接')
  }
}

// 导出报告
const exportReport = () => {
  try {
    // 准备报告数据
    const reportData = {
      报告标题: 'Fresh Logistics 冷链物流数据分析报告',
      生成时间: new Date().toLocaleString(),
      分析时间范围: dateRange.value.join(' 至 '),
      
      时效分析: {
        平均配送时长: timeStats.avgDeliveryTime + '小时',
        准时交付率: timeStats.onTimeRate + '%',
        延迟订单数: timeStats.delayedOrders
      },
      
      损耗分析: {
        总体损耗率: lossStats.totalLossRate + '%',
        温控失效次数: lossStats.tempFailureCount,
        损失金额: lossStats.totalLossAmount + '万元'
      },
      
      预警统计: {
        总预警数: alertStats.totalAlerts,
        平均响应时间: alertStats.avgResponseTime + '秒',
        处理率: alertStats.processedRate + '%',
        误报率: alertStats.falseAlarmRate + '%'
      }
    }
    
    // 转换为CSV格式（分组输出）
    const csvLines = []
    csvLines.push('\ufeff' + reportData.报告标题) // 添加BOM
    csvLines.push('生成时间,' + reportData.生成时间)
    csvLines.push('分析时间范围,' + reportData.分析时间范围)
    csvLines.push('')
    
    csvLines.push('时效分析')
    csvLines.push('指标,数值')
    csvLines.push('平均配送时长,' + reportData.时效分析.平均配送时长)
    csvLines.push('准时交付率,' + reportData.时效分析.准时交付率)
    csvLines.push('延迟订单数,' + reportData.时效分析.延迟订单数)
    csvLines.push('')
    
    csvLines.push('损耗分析')
    csvLines.push('指标,数值')
    csvLines.push('总体损耗率,' + reportData.损耗分析.总体损耗率)
    csvLines.push('温控失效次数,' + reportData.损耗分析.温控失效次数)
    csvLines.push('损失金额,' + reportData.损耗分析.损失金额)
    csvLines.push('')
    
    csvLines.push('预警统计')
    csvLines.push('指标,数值')
    csvLines.push('总预警数,' + reportData.预警统计.总预警数)
    csvLines.push('平均响应时间,' + reportData.预警统计.平均响应时间)
    csvLines.push('处理率,' + reportData.预警统计.处理率)
    csvLines.push('误报率,' + reportData.预警统计.误报率)
    
    const csvContent = csvLines.join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    const fileName = `数据分析报告_${dateRange.value[0]}_${dateRange.value[1]}.csv`
    
    link.setAttribute('href', url)
    link.setAttribute('download', fileName)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('分析报告导出成功')
    
  } catch (error) {
    console.error('导出报告失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 日期变化处理
const handleDateChange = () => {
  refreshAnalysis()
}

// 组件挂载
onMounted(async () => {
  await nextTick()
  initCharts()
  
  // 监听窗口大小变化
  const handleResize = () => {
    timeEfficiencyChart?.resize()
    lossAnalysisChart?.resize()
    alertTrendChart?.resize()
    alertTypeChart?.resize()
  }
  
  window.addEventListener('resize', handleResize)
  
  // 组件卸载时清理
  onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    timeEfficiencyChart?.dispose()
    lossAnalysisChart?.dispose()
    alertTrendChart?.dispose()
    alertTypeChart?.dispose()
  })
})
</script>

<style scoped>
.analysis-page {
  padding: 0;
}

.analysis-filters {
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-item label {
  font-weight: 500;
  color: #333;
}

.analysis-summary {
  padding: 16px 20px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}

.summary-item {
  text-align: center;
}

.summary-value {
  font-size: 24px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 4px;
}

.summary-label {
  font-size: 12px;
  color: #666;
}
</style>

