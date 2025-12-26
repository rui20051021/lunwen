<template>
  <div class="purchaser-dashboard">
    <div class="welcome-card">
      <h1>🛒 采购商控制台</h1>
      <p>欢迎您，{{ userInfo?.realName }}！</p>
      <p>管理采购订单，跟踪收货状态</p>
    </div>
    
    <div class="dashboard-grid">
      <div class="stat-card">
        <h3>📋 采购订单</h3>
        <div class="stat-number">{{ orderStats.totalOrders }}</div>
        <p>订单总数</p>
      </div>
      
      <div class="stat-card">
        <h3>✅ 已完成</h3>
        <div class="stat-number">{{ orderStats.completedOrders }}</div>
        <p>完成订单</p>
      </div>
      
      <div class="stat-card">
        <h3>⏰ 待收货</h3>
        <div class="stat-number">{{ orderStats.pendingOrders }}</div>
        <p>待收货订单</p>
      </div>
      
      <div class="stat-card">
        <h3>💰 采购金额</h3>
        <div class="stat-number">{{ orderStats.totalAmount }}万</div>
        <p>累计金额</p>
      </div>
    </div>
    
    <div class="function-cards">
      <div class="function-card clickable" @click="goToOrders">
        <h4>📋 采购订单</h4>
        <p>查看和管理采购订单</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card clickable" @click="goToReceiving">
        <h4>📦 收货管理</h4>
        <p>确认收货和质量检查</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card clickable" @click="goToEvaluation">
        <h4>⭐ 供应商评价</h4>
        <p>评价供应商服务质量</p>
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

// 订单统计数据（将从真实数据库获取）
const orderStats = reactive({
  totalOrders: 0,
  completedOrders: 0,
  pendingOrders: 0,
  totalAmount: 0
})

// 功能导航
const goToOrders = () => {
  console.log('📋 跳转到采购订单页面')
  router.push('/purchaser/orders')
}

const goToReceiving = () => {
  console.log('📦 跳转到收货管理页面')
  router.push('/purchaser/receiving')
}

const goToEvaluation = () => {
  console.log('⭐ 跳转到供应商评价页面')
  router.push('/purchaser/evaluation')
}

// 刷新统计数据
const refreshStats = async () => {
  try {
    console.log('🚀 开始刷新采购商统计数据...')
    
    const response = await fetch('http://localhost:8080/database/purchaser/order-statistics')
    const data = await response.json()
    
    if (data.code === 200) {
      const stats = data.data
      orderStats.totalOrders = stats.total_orders || 0
      orderStats.completedOrders = stats.completed_orders || 0
      orderStats.pendingOrders = stats.pending_orders || 0
      orderStats.totalAmount = ((stats.total_amount || 0) / 10000).toFixed(1) // 转换为万元
      
      console.log('📈 真实统计数据:', orderStats)
      ElMessage.success('✅ 统计数据更新成功（真实数据库）')
    } else {
      ElMessage.error(`获取统计数据失败: ${data.message}`)
    }
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
  console.log('采购商控制台已加载')
  refreshStats()
})
</script>

<style scoped>
.purchaser-dashboard {
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