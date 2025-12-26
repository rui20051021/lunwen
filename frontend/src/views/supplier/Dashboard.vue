<template>
  <div class="supplier-dashboard">
    <div class="welcome-card">
      <h1>🏭 供应商控制台</h1>
      <p>欢迎您，{{ userInfo?.realName }}！</p>
      <p>您已成功登录Fresh Logistics冷链物流智能监测预警系统</p>
    </div>
    
    <div class="dashboard-grid">
      <div class="stat-card">
        <h3>📦 我的产品</h3>
        <div class="stat-number">{{ supplierStats.totalProducts }}</div>
        <p>产品总数</p>
      </div>
      
      <div class="stat-card">
        <h3>📋 我的订单</h3>
        <div class="stat-number">{{ supplierStats.totalOrders }}</div>
        <p>订单总数</p>
      </div>
      
      <div class="stat-card">
        <h3>⏰ 待处理</h3>
        <div class="stat-number">{{ supplierStats.pendingOrders }}</div>
        <p>待处理订单</p>
      </div>
      
      <div class="stat-card">
        <h3>⚠️ 预警</h3>
        <div class="stat-number">{{ supplierStats.alertCount }}</div>
        <p>预警消息</p>
      </div>
    </div>
    
    <div class="function-cards">
      <div class="function-card" @click="navigateTo('/products')">
        <h4>📦 产品管理</h4>
        <p>管理产品信息，配置冷链要求</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card" @click="navigateTo('/orders')">
        <h4>📋 订单管理</h4>
        <p>查看和处理采购订单</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card" @click="navigateTo('/alerts')">
        <h4>🚨 预警管理</h4>
        <p>查看和处理预警消息</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card" @click="navigateTo('/supplier/evaluation')">
        <h4>⭐ 供应商评价</h4>
        <p>评价供应商服务质量</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card" @click="refreshSupplierStats">
        <h4>🔄 刷新统计</h4>
        <p>更新最新的统计数据</p>
        <div class="card-action">点击刷新 →</div>
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

// 供应商统计数据（将从真实数据库获取）
const supplierStats = reactive({
  totalProducts: 0,
  totalOrders: 0,
  pendingOrders: 0,
  alertCount: 0
})

const navigateTo = (path) => {
  router.push(path)
}

// 刷新供应商统计数据
const refreshSupplierStats = async () => {
  try {
    console.log('🚀 开始刷新供应商统计数据...')
    
    // 🔄 并行调用多个API获取真实统计数据
    const [productRes, orderRes, alertRes] = await Promise.all([
      fetch(`http://localhost:8080/database/product/all?_t=${Date.now()}`),
      fetch(`http://localhost:8080/database/order/all?_t=${Date.now()}`),
      fetch(`http://localhost:8080/database/alert/records?_t=${Date.now()}`)
    ])
    
    const productData = await productRes.json()
    const orderData = await orderRes.json()
    const alertData = await alertRes.json()
    
    console.log('📊 产品数据API响应:', productData)
    console.log('📊 订单数据API响应:', orderData)
    console.log('📊 预警数据API响应:', alertData)
    
    // 🔄 更新产品统计
    if (productData.code === 200) {
      const products = productData.data || []
      supplierStats.totalProducts = products.length
      console.log('📦 真实产品数量:', products.length)
    }
    
    // 🔄 更新订单统计
    if (orderData.code === 200) {
      const orders = orderData.data || []
      supplierStats.totalOrders = orders.length
      supplierStats.pendingOrders = orders.filter(o => 
        o.order_status === 'created' || o.order_status === 'in_transit'
      ).length
      console.log('📋 真实订单数量:', orders.length)
      console.log('⏰ 待处理订单:', supplierStats.pendingOrders)
    }
    
    // 🔄 更新预警统计
    if (alertData.code === 200) {
      const alerts = alertData.data?.records || alertData.data || []
      supplierStats.alertCount = alerts.length
      console.log('🚨 真实预警数量:', alerts.length)
    }
    
    console.log('📈 供应商真实统计数据:', supplierStats)
    ElMessage.success('✅ 供应商统计数据已更新（真实数据库）')
    
  } catch (error) {
    console.error('💥 刷新供应商统计数据失败:', error)
    ElMessage.error('❌ 统计数据刷新失败')
  }
}

onMounted(async () => {
  // 获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
  
  console.log('🏭 供应商控制台已加载，开始获取真实统计数据')
  await refreshSupplierStats()
  
  // 启动自动刷新（每30秒）
  setInterval(async () => {
    console.log('⏰ 自动刷新供应商统计数据...')
    await refreshSupplierStats()
  }, 30000)
})
</script>

<style scoped>
.supplier-dashboard {
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

.welcome-card h1 {
  color: #333;
  margin-bottom: 12px;
}

.welcome-card p {
  color: #666;
  margin: 4px 0;
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

.stat-card h3 {
  margin-bottom: 12px;
  color: #333;
  font-size: 16px;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 8px;
}

.stat-card p {
  color: #666;
  font-size: 14px;
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
  cursor: pointer;
  transition: all 0.3s;
}

.function-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.function-card h4 {
  color: #333;
  margin-bottom: 8px;
}

.function-card p {
  color: #666;
  font-size: 14px;
}

.card-action {
  color: #1890ff;
  font-weight: 500;
  margin-top: 12px;
  font-size: 14px;
}
</style>