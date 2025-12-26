<template>
  <div class="logistics-dashboard">
    <div class="welcome-card">
      <h1>🚛 物流商控制台</h1>
      <p>欢迎您，{{ userInfo?.realName }}！</p>
      <p>管理车辆运输，监控配送轨迹</p>
    </div>
    
    <div class="dashboard-grid">
      <div class="stat-card">
        <h3>🚛 车辆数量</h3>
        <div class="stat-number">{{ logisticsStats.totalVehicles }}</div>
        <p>冷藏车辆</p>
      </div>
      
      <div class="stat-card">
        <h3>👨‍💼 司机数量</h3>
        <div class="stat-number">{{ logisticsStats.totalDrivers }}</div>
        <p>专业司机</p>
      </div>
      
      <div class="stat-card">
        <h3>🚚 运输中</h3>
        <div class="stat-number">{{ logisticsStats.inTransitCount }}</div>
        <p>运输任务</p>
      </div>
      
      <div class="stat-card">
        <h3>📍 GPS监控</h3>
        <div class="stat-number">实时</div>
        <p>位置追踪</p>
      </div>
    </div>
    
    <div class="function-cards">
      <div class="function-card clickable" @click="goToVehicles">
        <h4>🚛 车辆管理</h4>
        <p>管理冷藏车辆信息和状态</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card clickable" @click="goToTracking">
        <h4>📍 运输跟踪</h4>
        <p>实时跟踪配送轨迹</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card clickable" @click="goToTemperature">
        <h4>🌡️ 温控监测</h4>
        <p>监控车辆温湿度状态</p>
        <div class="card-action">点击进入 →</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userInfo = ref(null)

// 物流统计数据（将从真实数据库获取）
const logisticsStats = reactive({
  totalVehicles: 0,
  totalDrivers: 0,
  inTransitCount: 0,
  availableCount: 0
})

// 功能导航
const goToVehicles = () => {
  console.log('🚛 跳转到车辆管理页面')
  router.push('/logistics/vehicles')
}

const goToTracking = () => {
  console.log('📍 跳转到运输跟踪页面')
  router.push('/logistics/tracking')
}

const goToTemperature = () => {
  console.log('🌡️ 跳转到温控监测页面')
  router.push('/logistics/temperature')
}

// 刷新统计数据
const refreshStats = async () => {
  try {
    console.log('🚀 开始刷新物流商统计数据...')
    
    const [vehicleStatsRes, driversRes, transportsRes] = await Promise.all([
      fetch('http://localhost:8080/database/vehicle/statistics'),
      fetch('http://localhost:8080/database/vehicle/drivers'),
      fetch('http://localhost:8080/database/vehicle/transports')
    ])
    
    const vehicleStatsData = await vehicleStatsRes.json()
    const driversData = await driversRes.json()
    const transportsData = await transportsRes.json()
    
    console.log('📊 车辆统计API响应:', vehicleStatsData)
    console.log('📊 司机数据API响应:', driversData)
    console.log('📊 运输数据API响应:', transportsData)
    
    if (vehicleStatsData.code === 200) {
      const stats = vehicleStatsData.data
      logisticsStats.totalVehicles = stats.total_vehicles || 0
      logisticsStats.availableCount = stats.available_vehicles || 0
      logisticsStats.inTransitCount = stats.in_transit_vehicles || 0
    }
    
    if (driversData.code === 200) {
      logisticsStats.totalDrivers = driversData.data?.length || 0
    }
    
    if (transportsData.code === 200) {
      const activeTransports = transportsData.data?.length || 0
      logisticsStats.inTransitCount = activeTransports
    }
    
    console.log('📈 真实物流统计:', logisticsStats)
    ElMessage.success('✅ 物流统计数据更新成功（真实数据库）')
    
  } catch (error) {
    console.error('💥 刷新统计数据失败:', error)
    ElMessage.error('❌ 刷新统计数据失败')
  }
}

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
  console.log('物流商控制台已加载')
  refreshStats()
})
</script>

<style scoped>
.logistics-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  background: #ffffff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  text-align: center;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 8px;
}

.function-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.function-card {
  background: #ffffff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

.function-card.clickable {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.function-card.clickable:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  border-color: #1890ff;
}

.card-action {
  color: #1890ff;
  font-weight: 500;
  margin-top: 12px;
  font-size: 14px;
}
</style>