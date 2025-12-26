<template>
  <div class="regulator-dashboard">
    <div class="welcome-card">
      <h1>👮 监管员控制台</h1>
      <p>欢迎您，{{ userInfo?.realName }}！</p>
      <p>监管合规检查，生成监管报告</p>
    </div>
    
    <div class="dashboard-grid">
      <div class="stat-card">
        <h3>📋 合规检查</h3>
        <div class="stat-number">{{ regulatorStats.totalChecks }}</div>
        <p>检查总数</p>
      </div>
      
      <div class="stat-card">
        <h3>✅ 通过检查</h3>
        <div class="stat-number">{{ regulatorStats.passedChecks }}</div>
        <p>合规通过</p>
      </div>
      
      <div class="stat-card">
        <h3>⚠️ 违规事件</h3>
        <div class="stat-number">{{ regulatorStats.violationsFound }}</div>
        <p>发现违规</p>
      </div>
      
      <div class="stat-card">
        <h3>📊 监管报告</h3>
        <div class="stat-number">{{ regulatorStats.totalReports }}</div>
        <p>已生成报告</p>
      </div>
    </div>
    
    <div class="function-cards">
      <div class="function-card clickable" @click="goToCompliance">
        <h4>🔍 合规检查</h4>
        <p>执行冷链合规检查</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card clickable" @click="goToReports">
        <h4>📊 监管报告</h4>
        <p>生成和管理监管报告</p>
        <div class="card-action">点击进入 →</div>
      </div>
      
      <div class="function-card clickable" @click="goToViolations">
        <h4>⚠️ 违规处理</h4>
        <p>处理违规事件和整改</p>
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

// 监管统计数据（将从真实数据库获取）
const regulatorStats = reactive({
  totalChecks: 0,
  passedChecks: 0,
  violationsFound: 0,
  totalReports: 0
})

// 功能导航
const goToCompliance = () => {
  console.log('🔍 跳转到合规检查页面')
  router.push('/regulator/compliance')
}

const goToReports = () => {
  console.log('📊 跳转到监管报告页面')
  router.push('/regulator/reports')
}

const goToViolations = () => {
  console.log('⚠️ 跳转到违规处理页面')
  router.push('/regulator/violations')
}

// 刷新统计数据
const refreshStats = async () => {
  try {
    console.log('🚀 开始刷新监管员统计数据...')
    
    const [complianceRes, reportRes] = await Promise.all([
      fetch('http://localhost:8080/database/regulator/compliance-statistics'),
      fetch('http://localhost:8080/database/regulator/report-statistics')
    ])
    
    const complianceData = await complianceRes.json()
    const reportData = await reportRes.json()
    
    console.log('📊 合规统计API响应:', complianceData)
    console.log('📊 报告统计API响应:', reportData)
    
    if (complianceData.code === 200) {
      const stats = complianceData.data
      regulatorStats.totalChecks = stats.total_checks || 0
      regulatorStats.passedChecks = stats.passed_checks || 0
      regulatorStats.violationsFound = stats.failed_checks || 0
    }
    
    if (reportData.code === 200) {
      const stats = reportData.data
      regulatorStats.totalReports = stats.total_reports || 0
    }
    
    console.log('📈 真实监管统计:', regulatorStats)
    ElMessage.success('✅ 监管统计数据更新成功（真实数据库）')
    
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
  console.log('监管员控制台已加载')
  refreshStats()
})
</script>

<style scoped>
.regulator-dashboard {
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