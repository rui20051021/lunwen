<template>
  <div class="temperature-page">
    <div class="page-header">
      <h1>🌡️ 温控监测</h1>
      <p>实时监控车辆温湿度状态，确保冷链安全</p>
    </div>
    
    <!-- 温度监控统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">🌡️</div>
        <div class="stat-info">
          <div class="stat-number">{{ tempStats.avgTemperature }}℃</div>
          <div class="stat-label">平均温度</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">💧</div>
        <div class="stat-info">
          <div class="stat-number">{{ tempStats.avgHumidity }}%</div>
          <div class="stat-label">平均湿度</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⚠️</div>
        <div class="stat-info">
          <div class="stat-number">{{ tempStats.alertCount }}</div>
          <div class="stat-label">温度异常</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🚛</div>
        <div class="stat-info">
          <div class="stat-number">{{ tempStats.vehicleCount }}</div>
          <div class="stat-label">监控车辆</div>
        </div>
      </div>
    </div>
    
    <!-- 温度监控列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>实时温度监控</h3>
        <div class="table-actions">
          <el-button type="success" @click="refreshTemperatureData">
            🔄 刷新数据
          </el-button>
          <el-button type="info" @click="exportData">
            📊 导出数据
          </el-button>
        </div>
      </div>
      
      <el-table :data="temperatureList" style="width: 100%" stripe>
        <el-table-column prop="vehicleCode" label="车辆编号" width="120" />
        <el-table-column prop="licensePlate" label="车牌号" width="120" />
        <el-table-column prop="currentTemp" label="当前温度" width="100">
          <template #default="{ row }">
            <span :class="getTemperatureClass(row.currentTemp)">
              {{ row.currentTemp }}℃
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="currentHumidity" label="当前湿度" width="100">
          <template #default="{ row }">
            <span>{{ row.currentHumidity }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="targetTempRange" label="目标温度范围" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="当前位置" min-width="200" />
        <el-table-column prop="lastUpdate" label="更新时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleDetail(row)">
              详情
            </el-button>
            <el-button type="warning" size="small" text @click="handleAlert(row)">
              预警
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 温度趋势图表 -->
    <div class="chart-container">
      <div class="chart-header">
        <h3>📈 温度趋势分析</h3>
        <div class="chart-actions">
          <el-button type="info" @click="refreshChart">
            🔄 刷新图表
          </el-button>
        </div>
      </div>
      <div class="chart-content">
        <div ref="tempChartRef" style="width: 100%; height: 300px;"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'

// 温度统计数据（将从真实数据库获取）
const tempStats = reactive({
  avgTemperature: 0,
  avgHumidity: 0,
  alertCount: 0,
  vehicleCount: 0
})

// 温度监控列表数据（将从真实数据库获取）
const temperatureList = ref([])

// 图表引用
const tempChartRef = ref<HTMLDivElement>()
let tempChart: ECharts | null = null

// 获取温度状态类名
const getTemperatureClass = (temp: number) => {
  if (temp > 8) return 'temp-danger'
  if (temp > 6) return 'temp-warning'
  return 'temp-normal'
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    normal: 'success',
    alert: 'danger',
    warning: 'warning'
  }
  return typeMap[status] || ''
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    normal: '正常',
    alert: '异常',
    warning: '警告'
  }
  return textMap[status] || status
}

// 操作函数
const handleDetail = (row: any) => {
  ElMessage.info(`查看车辆 ${row.licensePlate} 详细信息`)
}

const handleAlert = (row: any) => {
  ElMessage.info(`设置车辆 ${row.licensePlate} 预警规则`)
}

const refreshTemperatureData = async () => {
  try {
    console.log('🚀 开始刷新温度监控数据...')
    
    // 🔄 调用真实数据库API获取温度数据
    const response = await fetch('http://localhost:8080/database/vehicle/temperature')
    console.log('📡 API响应状态:', response.status, response.statusText)
    
    const data = await response.json()
    console.log('📊 温度API响应:', data)
    
    if (data.code === 200) {
      const rawTempData = data.data || []
      
      // 🔄 转换数据格式以匹配前端期望
      const mappedTempData = rawTempData.map(temp => ({
        id: temp.id,
        vehicleCode: temp.vehicle_code,
        licensePlate: temp.license_plate,
        currentTemp: temp.temperature,
        currentHumidity: temp.humidity || 85,
        targetTempRange: `${temp.min_temp || -2}-${temp.max_temp || 8}℃`,
        status: temp.temperature > 8 ? 'alert' : 'normal',
        location: temp.location || '位置更新中',
        lastUpdate: temp.created_at ? new Date(temp.created_at).toLocaleString() : '未知'
      }))
      
      // 更新温度列表数据
      temperatureList.value = mappedTempData
      
      // 更新统计数据
      if (rawTempData.length > 0) {
        tempStats.avgTemperature = (rawTempData.reduce((sum, t) => sum + (t.temperature || 0), 0) / rawTempData.length).toFixed(1)
        tempStats.avgHumidity = Math.round(rawTempData.reduce((sum, t) => sum + (t.humidity || 85), 0) / rawTempData.length)
        tempStats.alertCount = mappedTempData.filter(t => t.status === 'alert').length
        tempStats.vehicleCount = mappedTempData.length
      }
      
      console.log('🌡️ 真实温度数据:', temperatureList.value)
      console.log('📈 真实温度统计:', tempStats)
      
      ElMessage.success(`✅ 成功加载 ${mappedTempData.length} 个温度监控数据（真实数据库）`)
      console.log('🎉 温度数据刷新完成!')
      
      // 🔄 数据加载完成后立即更新图表
      if (tempChart) {
        setTimeout(() => {
          updateChartWithCurrentData()
        }, 100)
      }
    } else {
      console.error('❌ API返回错误:', data)
      ElMessage.error(`获取温度数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 刷新温度数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

const exportData = () => {
  if (!temperatureList.value || temperatureList.value.length === 0) {
    ElMessage.warning('没有温度数据可以导出')
    return
  }
  
  try {
    // 准备导出数据
    const exportData = temperatureList.value.map(item => ({
      '车辆编号': item.vehicleCode,
      '车牌号': item.licensePlate,
      '当前温度': item.currentTemp + '℃',
      '当前湿度': item.currentHumidity + '%',
      '目标温度范围': item.targetTempRange,
      '状态': getStatusText(item.status),
      '当前位置': item.location,
      '更新时间': item.lastUpdate
    }))
    
    // 转换为CSV格式
    const headers = Object.keys(exportData[0])
    const csvContent = [
      '\ufeff' + headers.join(','), // 添加BOM以支持Excel打开中文
      ...exportData.map(row => 
        headers.map(header => {
          const value = row[header] || ''
          // 处理包含逗号的字段
          return typeof value === 'string' && value.includes(',') 
            ? `"${value}"` 
            : value
        }).join(',')
      )
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    link.setAttribute('href', url)
    link.setAttribute('download', `温度监控数据_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`✅ 成功导出 ${temperatureList.value.length} 条温度数据`)
    
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 初始化温度趋势图表
const initTemperatureChart = () => {
  if (tempChartRef.value) {
    tempChart = echarts.init(tempChartRef.value)
    
    // 设置初始图表配置
    tempChart.setOption({
      title: {
        text: '车辆温度趋势分析',
        left: 'center',
        textStyle: { fontSize: 16, fontWeight: 'normal' }
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params: any) => {
          let tooltipText = `时间: ${params[0].axisValue}<br/>`
          params.forEach((param: any) => {
            const color = param.color
            const seriesName = param.seriesName
            const value = param.value
            tooltipText += `<span style="color: ${color}">●</span> ${seriesName}: ${value}℃<br/>`
          })
          return tooltipText
        }
      },
      legend: {
        top: 30,
        data: []
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
      },
      yAxis: {
        type: 'value',
        name: '温度(℃)',
        axisLabel: {
          formatter: '{value}℃'
        }
      },
      series: []
    })
    
    console.log('📈 温度趋势图表已初始化')
  }
}

// 刷新温度趋势图表
const refreshChart = async () => {
  try {
    console.log('🚀 开始刷新温度趋势图表...')
    
    // 🔄 调用真实数据库API获取温度趋势数据  
    const response = await fetch('http://localhost:8080/database/sensor/temperature/trends')
    console.log('📡 温度趋势API响应状态:', response.status, response.statusText)
    
    const data = await response.json()
    console.log('📊 温度趋势API响应:', data)
    
    if (data.code === 200 && tempChart) {
      const trendData = data.data || []
      
      if (trendData.length > 0) {
        // 🔄 处理趋势数据
        const dates = trendData.map(item => new Date(item.date).toLocaleDateString())
        const avgTemps = trendData.map(item => parseFloat(item.avg_temp || 0).toFixed(1))
        const minTemps = trendData.map(item => parseFloat(item.min_temp || 0).toFixed(1))
        const maxTemps = trendData.map(item => parseFloat(item.max_temp || 0).toFixed(1))
        
        console.log('📅 日期数据:', dates)
        console.log('🌡️ 平均温度:', avgTemps)
        console.log('🔽 最低温度:', minTemps)
        console.log('🔼 最高温度:', maxTemps)
        
        // 🔄 更新图表配置
        tempChart.setOption({
          legend: {
            data: ['平均温度', '最低温度', '最高温度']
          },
          xAxis: {
            data: dates
          },
          series: [
            {
              name: '平均温度',
              type: 'line',
              data: avgTemps,
              smooth: true,
              itemStyle: { color: '#1890ff' },
              areaStyle: { opacity: 0.3 }
            },
            {
              name: '最低温度',
              type: 'line',
              data: minTemps,
              smooth: true,
              itemStyle: { color: '#52c41a' }
            },
            {
              name: '最高温度',
              type: 'line',
              data: maxTemps,
              smooth: true,
              itemStyle: { color: '#ff4d4f' }
            }
          ]
        })
        
        console.log('📈 温度趋势图表已更新为真实数据')
        ElMessage.success('✅ 温度趋势图表刷新成功（真实数据库）')
      } else {
        // 如果没有趋势数据，显示当前监控数据的简化趋势
        updateChartWithCurrentData()
      }
    } else {
      ElMessage.error(`获取温度趋势数据失败: ${data.message}`)
    }
    
  } catch (error) {
    console.error('💥 刷新温度趋势图表失败:', error)
    // 如果API失败，使用当前监控数据创建简化图表
    updateChartWithCurrentData()
  }
}

// 使用当前监控数据更新图表
const updateChartWithCurrentData = () => {
  if (tempChart && temperatureList.value.length > 0) {
    console.log('📊 使用当前监控数据创建趋势图表...')
    
    // 按车辆分组数据
    const vehicleGroups: Record<string, any[]> = {}
    temperatureList.value.forEach(item => {
      if (!vehicleGroups[item.licensePlate]) {
        vehicleGroups[item.licensePlate] = []
      }
      vehicleGroups[item.licensePlate].push({
        time: item.lastUpdate,
        temp: item.currentTemp
      })
    })
    
    const seriesData = Object.keys(vehicleGroups).map((plate, index) => {
      const colors = ['#1890ff', '#52c41a', '#ff4d4f', '#faad14', '#722ed1']
      return {
        name: plate,
        type: 'line',
        data: vehicleGroups[plate].map(item => item.temp),
        smooth: true,
        itemStyle: { color: colors[index % colors.length] }
      }
    })
    
    const timeLabels = Object.values(vehicleGroups)[0]?.map(item => 
      new Date(item.time).toLocaleTimeString()
    ) || []
    
    tempChart.setOption({
      legend: {
        data: Object.keys(vehicleGroups)
      },
      xAxis: {
        data: timeLabels
      },
      series: seriesData
    })
    
    console.log('📈 已使用当前监控数据生成趋势图表')
    ElMessage.success('✅ 温度趋势图表已生成（基于当前监控数据）')
  }
}

onMounted(async () => {
  console.log('温控监测页面已加载')
  
  // 先加载温度数据
  await refreshTemperatureData()
  
  // 等待DOM渲染完成后初始化图表
  await nextTick()
  initTemperatureChart()
  
  // 初始化图表后立即刷新显示当前数据
  setTimeout(() => {
    updateChartWithCurrentData()
  }, 500)
})

// 组件卸载时清理图表
onBeforeUnmount(() => {
  if (tempChart) {
    tempChart.dispose()
    tempChart = null
    console.log('📈 温度趋势图表已清理')
  }
})
</script>

<style scoped>
.temperature-page {
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

.table-container, .chart-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.table-header, .chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h3, .chart-header h3 {
  color: #333;
  font-size: 18px;
  margin: 0;
}

.table-actions, .chart-actions {
  display: flex;
  gap: 12px;
}

.temp-normal { color: #52c41a; font-weight: 600; }
.temp-warning { color: #faad14; font-weight: 600; }
.temp-danger { color: #ff4d4f; font-weight: 600; }

.chart-content {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  background: #fafafa;
  border-radius: 4px;
}
</style>
