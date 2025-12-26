<template>
  <div class="dashboard-layout">
    <div class="dashboard-header">
      <div class="header-left">
        <h2>Fresh Logistics</h2>
      </div>
      <div class="header-right">
        <span class="user-info">{{ currentUser?.realName || '用户' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </div>
    
    <div class="dashboard-content">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const currentUser = ref(null)

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('roles')
  localStorage.removeItem('permissions')
  
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  // 获取当前用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
})
</script>

<style scoped>
.dashboard-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.dashboard-header {
  height: 60px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left h2 {
  color: #333;
  font-size: 20px;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  color: #666;
  font-size: 14px;
}

.dashboard-content {
  flex: 1;
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}
</style>