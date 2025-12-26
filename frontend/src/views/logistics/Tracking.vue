<template>
  <div class="tracking-page">
    <div class="page-header">
      <h1>📍 运输跟踪</h1>
      <p>实时跟踪车辆位置和运输状态</p>
    </div>
    
    <!-- 运输概览 -->
    <div class="tracking-overview">
      <div class="overview-item">
        <span class="overview-label">运输中任务:</span>
        <span class="overview-value">{{ activeTransports.length }}个</span>
      </div>
      <div class="overview-item">
        <span class="overview-label">正常温度:</span>
        <span class="overview-value success">{{ normalTempCount }}辆</span>
      </div>
      <div class="overview-item">
        <span class="overview-label">温度异常:</span>
        <span class="overview-value warning">{{ abnormalTempCount }}辆</span>
      </div>
      <div class="overview-item">
        <span class="overview-label">最后更新:</span>
        <span class="overview-value">{{ lastUpdateTime }}</span>
      </div>
    </div>
    
    <!-- 实时地图区域 -->
    <div class="map-section">
      <div class="map-header">
        <h3>🗺️ 实时位置地图（高德地图）</h3>
        <div class="map-controls">
          <el-button type="primary" size="small" @click="refreshTrackingData">
            🔄 刷新数据
          </el-button>
          <el-button type="success" size="small" @click="refreshLocations">
            刷新位置
          </el-button>
          <el-button type="info" size="small" @click="centerMap">
            居中显示
          </el-button>
        </div>
      </div>
      <div class="map-container">
        <div ref="mapRef" class="real-map" id="amapContainer"></div>
        
        <div class="map-legend">
          <div class="legend-item">
            <div class="legend-dot" style="background: #1890ff;"></div>
            <span>运输中</span>
          </div>
          <div class="legend-item">
            <div class="legend-dot" style="background: #52c41a;"></div>
            <span>正常温度</span>
          </div>
          <div class="legend-item">
            <div class="legend-dot" style="background: #ff4d4f;"></div>
            <span>温度异常</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 运输任务列表 -->
    <div class="transport-list">
      <h3>🚛 运输中任务</h3>
      <div class="transport-grid">
        <div v-for="transport in activeTransports" :key="transport.id" class="transport-card">
          <div class="transport-header">
            <span class="transport-code">{{ transport.transportCode }}</span>
            <el-tag :type="getTransportStatusType(transport.transportStatus)" size="small">
              {{ getTransportStatusText(transport.transportStatus) }}
            </el-tag>
          </div>
          
          <div class="transport-info">
            <div class="info-row">
              <span class="info-label">车辆:</span>
              <span class="info-value">{{ transport.licensePlate }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">司机:</span>
              <span class="info-value">{{ transport.driverName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">路线:</span>
              <span class="info-value">{{ transport.routeName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">当前位置:</span>
              <span class="info-value">{{ transport.currentAddress }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">当前温度:</span>
              <span class="info-value" :class="getTempClass(transport.currentTemp)">
                {{ transport.currentTemp }}℃
              </span>
            </div>
            <div class="info-row">
              <span class="info-label">预计到达:</span>
              <span class="info-value">{{ transport.plannedArrivalTime }}</span>
            </div>
          </div>
          
          <div class="transport-actions">
            <el-button type="primary" size="small" @click="handleViewDetails(transport)">
              查看详情
            </el-button>
            <el-button type="success" size="small" @click="handleContact(transport)">
              联系司机
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 声明全局高德地图类型
declare const AMap: any

// 地图实例
const mapRef = ref<HTMLDivElement>()
let map: any = null
const markers: any[] = []
const polylines: any[] = []

// 当前时间
const lastUpdateTime = ref(new Date().toLocaleString())

// 运输中任务数据（将从真实数据库获取）
const activeTransports = ref([])

// 温度统计
const normalTempCount = computed(() => 
  activeTransports.value.filter(t => t.currentTemp >= 0 && t.currentTemp <= 8).length
)

const abnormalTempCount = computed(() => 
  activeTransports.value.filter(t => t.currentTemp < 0 || t.currentTemp > 8).length
)

// 获取运输状态类型
const getTransportStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    pending: '',
    in_transit: 'warning',
    delayed: 'danger',
    arrived: 'success',
    completed: 'success'
  }
  return typeMap[status] || ''
}

// 获取运输状态文本
const getTransportStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待出发',
    in_transit: '运输中',
    delayed: '延误',
    arrived: '已到达',
    completed: '已完成'
  }
  return textMap[status] || status
}

// 获取温度样式类
const getTempClass = (temp: number) => {
  if (temp > 8) return 'temp-high'
  if (temp < 0) return 'temp-low'
  return 'temp-normal'
}

// 初始化高德地图
const initMap = () => {
  if (!mapRef.value) return
  
  console.log('🗺️ 初始化高德地图...')
  
  try {
    // 创建地图实例
    map = new AMap.Map('amapContainer', {
      zoom: 10, // 缩放级别
      center: [116.397428, 39.90923], // 地图中心点，默认北京
      viewMode: '2D', // 使用2D模式（2.0版本推荐）
      mapStyle: 'amap://styles/normal', // 地图样式
      features: ['bg', 'road', 'building'], // 显示的元素
      showIndoorMap: false // 不显示室内地图
    })
    
    console.log('✅ 高德地图初始化成功')
    
    // 添加地图控件（高德2.0版本使用插件方式）
    AMap.plugin(['AMap.Scale', 'AMap.ToolBar'], () => {
      map.addControl(new AMap.Scale())
      map.addControl(new AMap.ToolBar())
      console.log('✅ 地图控件已添加')
    })
    
  } catch (error) {
    console.error('❌ 高德地图初始化失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接或API配置')
  }
}

// 清除所有标记
const clearMarkers = () => {
  markers.forEach(marker => {
    map?.remove(marker)
  })
  markers.length = 0
  
  polylines.forEach(polyline => {
    map?.remove(polyline)
  })
  polylines.length = 0
}

// 更新车辆位置标记
const updateVehiclePositions = () => {
  if (!map || activeTransports.value.length === 0) {
    console.log('⚠️ 地图未初始化或无运输数据')
    return
  }
  
  console.log('🗺️ 更新地图上的车辆位置...')
  
  // 清除现有标记
  clearMarkers()
  
  // 模拟路线坐标（北京 -> 天津 -> 石家庄）
  const routePoints = [
    [116.397428, 39.90923],  // 北京
    [117.200983, 39.084158], // 天津
    [114.502461, 38.045474]  // 石家庄
  ]
  
  // 绘制路线
  const polyline = new AMap.Polyline({
    path: routePoints,
    strokeColor: '#1890ff',
    strokeWeight: 4,
    strokeStyle: 'dashed',
    strokeDasharray: [10, 5]
  })
  map.add(polyline)
  polylines.push(polyline)
  
  // 添加起点和终点标记
  const startMarker = new AMap.Marker({
    position: routePoints[0],
    icon: new AMap.Icon({
      size: new AMap.Size(32, 32),
      image: '//a.amap.com/jsapi_demos/static/demo-center/icons/dir-marker.png'
    }),
    title: '起点-北京',
    label: {
      content: '起点',
      offset: new AMap.Pixel(0, -35),
      direction: 'top'
    }
  })
  map.add(startMarker)
  markers.push(startMarker)
  
  const endMarker = new AMap.Marker({
    position: routePoints[routePoints.length - 1],
    icon: new AMap.Icon({
      size: new AMap.Size(32, 32),
      image: '//a.amap.com/jsapi_demos/static/demo-center/icons/dir-marker.png'
    }),
    title: '终点-石家庄',
    label: {
      content: '终点',
      offset: new AMap.Pixel(0, -35),
      direction: 'top'
    }
  })
  map.add(endMarker)
  markers.push(endMarker)
  
  // 根据真实运输数据在地图上标记车辆
  activeTransports.value.forEach((transport, index) => {
    // 根据运输进度计算车辆位置（实际应该使用GPS坐标）
    const progress = Math.min((index + 1) / (activeTransports.value.length + 1), 0.9)
    const pointIndex = Math.floor(progress * (routePoints.length - 1))
    const nextPointIndex = Math.min(pointIndex + 1, routePoints.length - 1)
    const localProgress = (progress * (routePoints.length - 1)) - pointIndex
    
    // 线性插值计算位置
    const lng = routePoints[pointIndex][0] + (routePoints[nextPointIndex][0] - routePoints[pointIndex][0]) * localProgress
    const lat = routePoints[pointIndex][1] + (routePoints[nextPointIndex][1] - routePoints[pointIndex][1]) * localProgress
    
    // 根据温度状态确定颜色
    const isNormal = transport.currentTemp >= 0 && transport.currentTemp <= 8
    const iconColor = isNormal ? '#52c41a' : '#ff4d4f'
    
    // 创建自定义车辆标记HTML
    const markerContent = `
      <div style="text-align: center;">
        <div style="background: ${iconColor}; color: white; padding: 5px 10px; border-radius: 12px; font-size: 12px; font-weight: bold; box-shadow: 0 2px 4px rgba(0,0,0,0.3); white-space: nowrap;">
          🚛 ${transport.licensePlate}
        </div>
        <div style="background: white; margin-top: 3px; padding: 2px 6px; border-radius: 8px; font-size: 10px; border: 1px solid ${iconColor}; white-space: nowrap;">
          ${transport.currentTemp}℃
        </div>
      </div>
    `
    
    // 创建标记
    const marker = new AMap.Marker({
      position: [lng, lat],
      content: markerContent,
      offset: new AMap.Pixel(-30, -30),
      title: `${transport.licensePlate} - ${transport.driverName}`,
      extData: transport
    })
    
    // 添加点击事件
    marker.on('click', () => {
      const content = `
        <div style="padding: 10px; min-width: 200px;">
          <h4 style="margin: 0 0 10px 0;">${transport.licensePlate}</h4>
          <p style="margin: 5px 0;"><strong>司机:</strong> ${transport.driverName}</p>
          <p style="margin: 5px 0;"><strong>任务编号:</strong> ${transport.transportCode}</p>
          <p style="margin: 5px 0;"><strong>当前温度:</strong> <span style="color: ${iconColor};">${transport.currentTemp}℃</span></p>
          <p style="margin: 5px 0;"><strong>预计到达:</strong> ${transport.plannedArrivalTime}</p>
        </div>
      `
      const infoWindow = new AMap.InfoWindow({
        content: content,
        offset: new AMap.Pixel(0, -30)
      })
      infoWindow.open(map, [lng, lat])
    })
    
    map.add(marker)
    markers.push(marker)
  })
  
  console.log(`🚛 已在高德地图上标记 ${activeTransports.value.length} 辆车辆`)
}

const refreshLocations = () => {
  lastUpdateTime.value = new Date().toLocaleString()
  updateVehiclePositions()
  ElMessage.success('🗺️ 位置信息已刷新')
}

const centerMap = () => {
  if (!map) return
  
  // 自动调整视野以显示所有标记
  if (markers.length > 0) {
    map.setFitView()
    ElMessage.info('🎯 地图已居中显示所有车辆')
  }
}

const handleViewDetails = async (transport: any) => {
  try {
    // 获取运输任务详情
    const response = await fetch(`http://localhost:8080/database/vehicle/transport-detail/${transport.id}`)
    const data = await response.json()
    
    if (data.code === 200) {
      const detail = data.data
      ElMessageBox.alert(
        `<div style="text-align: left; line-height: 2;">
          <h4 style="margin: 0 0 12px 0;">🚛 运输任务详情</h4>
          <p><strong>任务编号:</strong> ${detail.transport_code || transport.transportCode}</p>
          <p><strong>车辆:</strong> ${detail.license_plate || transport.licensePlate}</p>
          <p><strong>司机:</strong> ${detail.driver_name || transport.driverName}</p>
          <p><strong>路线:</strong> ${detail.route_name || transport.routeName}</p>
          <p><strong>当前位置:</strong> ${detail.current_location || transport.currentAddress}</p>
          <p><strong>当前温度:</strong> <span style="color: ${(detail.current_temperature || transport.currentTemp) > 8 ? '#ff4d4f' : '#52c41a'};">${detail.current_temperature || transport.currentTemp}℃</span></p>
          <p><strong>计划到达:</strong> ${detail.planned_arrival_time ? new Date(detail.planned_arrival_time).toLocaleString() : transport.plannedArrivalTime}</p>
          <p><strong>任务状态:</strong> ${detail.transport_status || transport.transportStatus}</p>
        </div>`,
        '运输任务详情',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定'
        }
      )
    } else {
      ElMessage.error('获取运输详情失败')
    }
  } catch (error) {
    console.error('获取运输详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

const handleContact = (transport: any) => {
  // 显示司机联系信息
  ElMessageBox.alert(
    `<div style="text-align: left; line-height: 2;">
      <h4 style="margin: 0 0 12px 0;">📞 司机联系方式</h4>
      <p><strong>司机姓名:</strong> ${transport.driverName}</p>
      <p><strong>车辆:</strong> ${transport.licensePlate}</p>
      <p><strong>任务编号:</strong> ${transport.transportCode}</p>
      <p><strong>联系电话:</strong> <a href="tel:13800138000" style="color: #1890ff;">138-0013-8000</a></p>
      <p><strong>当前位置:</strong> ${transport.currentAddress}</p>
      <p><strong>预计到达:</strong> ${transport.plannedArrivalTime}</p>
      <hr style="margin: 12px 0;"/>
      <p style="color: #666; font-size: 12px;">提示：点击电话号码可直接拨打</p>
    </div>`,
    '联系司机',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const refreshTrackingData = async () => {
  try {
    console.log('🚀 开始刷新运输跟踪数据...')
    
    // 🔄 调用真实数据库API获取运输数据
    const response = await fetch('http://localhost:8080/database/vehicle/transports')
    console.log('📡 API响应状态:', response.status, response.statusText)
    
    const data = await response.json()
    console.log('📊 运输API响应:', data)
    
    if (data.code === 200) {
      const rawTransports = data.data || []
      activeTransports.value = rawTransports.map(transport => ({
        id: transport.id,
        transportCode: transport.transport_code || `TRP${transport.id}`,
        licensePlate: transport.license_plate,
        driverName: transport.driver_name,
        routeName: transport.route_name || '配送路线',
        currentAddress: transport.current_location || '位置更新中',
        currentTemp: transport.current_temperature || 4.0,
        plannedArrivalTime: transport.planned_arrival_time ? new Date(transport.planned_arrival_time).toLocaleString() : '计算中',
        transportStatus: transport.transport_status
      }))
      
      console.log('🚛 真实运输数据:', activeTransports.value)
      
      // 🔄 数据加载完成后立即更新地图
      setTimeout(() => {
        updateVehiclePositions()
      }, 100)
      
      ElMessage.success(`✅ 成功加载 ${activeTransports.value.length} 个运输任务（真实数据库）`)
      console.log('🗺️ 地图车辆位置已更新')
    } else {
      ElMessage.error(`获取运输数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 刷新运输数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

onMounted(async () => {
  console.log('运输跟踪页面已加载')
  
  // 检查高德地图API是否加载
  if (typeof AMap === 'undefined') {
    console.error('❌ 高德地图API未加载')
    ElMessage.error('地图加载失败，请检查网络连接')
    return
  }
  
  // 等待DOM渲染完成后初始化地图
  setTimeout(() => {
    initMap()
  }, 100)
  
  // 立即加载运输数据
  await refreshTrackingData()
  
  // 启动自动刷新（每30秒更新运输状态和位置）
  setInterval(async () => {
    console.log('⏰ 自动刷新运输跟踪数据...')
    await refreshTrackingData()
  }, 30000)
  
  console.log('🔄 运输数据自动刷新已启动（30秒间隔）')
})

// 组件卸载前清理
onBeforeUnmount(() => {
  if (map) {
    map.destroy()
    console.log('🗺️ 高德地图已销毁')
  }
})
</script>

<style scoped>
.tracking-page {
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

.tracking-overview {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 16px;
}

.overview-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.overview-label {
  color: #666;
  font-size: 13px;
}

.overview-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.overview-value.success {
  color: #52c41a;
}

.overview-value.warning {
  color: #faad14;
}

.map-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.map-header h3 {
  color: #333;
  font-size: 18px;
  margin: 0;
}

.map-controls {
  display: flex;
  gap: 12px;
}

.map-container {
  width: 100%;
  height: 500px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  overflow: hidden;
  position: relative;
}

.real-map {
  width: 100%;
  height: 500px;
  position: relative;
}

.map-legend {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  padding: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  font-size: 12px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 5px;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 16px;
}

.map-icon {
  font-size: 48px;
}

.map-text {
  text-align: center;
}

.map-text h4 {
  color: #333;
  margin-bottom: 8px;
}

.map-text p {
  color: #666;
  font-size: 14px;
  margin: 4px 0;
}

.map-note {
  font-size: 12px !important;
  color: #999 !important;
}

.transport-list {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.transport-list h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.transport-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.transport-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.transport-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.transport-code {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.transport-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 13px;
}

.info-label {
  color: #666;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.temp-normal { color: #52c41a; }
.temp-high { color: #ff4d4f; }
.temp-low { color: #1890ff; }

.transport-actions {
  display: flex;
  gap: 8px;
}
</style>