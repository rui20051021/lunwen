<template>
  <div class="admin-dashboard">
    <div class="welcome-card">
      <h1>👨‍💼 管理员控制台</h1>
      <p>欢迎您，{{ userInfo?.realName }}！</p>
      <p>您拥有系统最高权限，可以管理所有功能模块</p>
    </div>
    
    <div class="dashboard-grid">
      <div class="stat-card">
        <h3>👥 系统用户</h3>
        <div class="stat-number">{{ adminStats.totalUsers }}</div>
        <p>注册用户数</p>
      </div>
      
      <div class="stat-card">
        <h3>📦 产品数量</h3>
        <div class="stat-number">{{ adminStats.totalProducts }}</div>
        <p>产品总数</p>
      </div>
      
      <div class="stat-card">
        <h3>📋 订单数量</h3>
        <div class="stat-number">{{ adminStats.totalOrders }}</div>
        <p>订单总数</p>
      </div>
      
      <div class="stat-card">
        <h3>⚠️ 预警记录</h3>
        <div class="stat-number">{{ adminStats.totalAlerts }}</div>
        <p>预警记录数</p>
      </div>
    </div>
    
    <div class="function-cards">
      <div class="function-card" @click="navigateTo('/admin/users')">
        <h4>用户管理</h4>
        <p>管理系统用户和权限</p>
      </div>
      
      <div class="function-card" @click="navigateTo('/products')">
        <h4>产品管理</h4>
        <p>管理所有产品信息</p>
      </div>
      
      <div class="function-card" @click="navigateTo('/orders')">
        <h4>订单管理</h4>
        <p>监控所有订单状态</p>
      </div>
      
      <div class="function-card" @click="navigateTo('/alerts')">
        <h4>预警管理</h4>
        <p>配置和管理预警规则</p>
      </div>
      
      <div class="function-card" @click="navigateTo('/analysis')">
        <h4>数据分析</h4>
        <p>查看系统统计报告</p>
      </div>
      
      <div class="function-card" @click="navigateTo('/admin/system')">
        <h4>系统设置</h4>
        <p>系统配置和维护</p>
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

// 管理员统计数据（从真实数据库获取）
const adminStats = reactive({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalAlerts: 0
})

const navigateTo = (path) => {
  router.push(path)
}

// 刷新统计数据
const refreshStats = async () => {
  try {
    console.log('🚀 开始刷新管理员统计数据...')
    
    const response = await fetch('http://localhost:8080/database/analysis/overview')
    const data = await response.json()
    
    console.log('📊 系统概览API响应:', data)
    
    if (data.code === 200) {
      const stats = data.data
      adminStats.totalUsers = stats.totalUsers || 0
      adminStats.totalProducts = stats.totalProducts || 0
      adminStats.totalOrders = stats.totalOrders || 0
      adminStats.totalAlerts = stats.totalAlerts || 0
      
      console.log('📈 真实管理员统计:', adminStats)
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
  // 获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
  console.log('管理员控制台已加载')
  refreshStats()
})
</script>

<style scoped>
.admin-dashboard {
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
</style>