<template>
  <div class="role-selector">
    <h4 class="selector-title">选择角色快速登录</h4>
    <p class="selector-subtitle">点击角色卡片即可一键登录演示账户</p>
    
    <div class="role-grid">
      <div
        v-for="role in roles"
        :key="role.id"
        class="role-card"
        :class="{ active: selectedRole?.id === role.id }"
        @click="handleRoleClick(role)"
      >
        <div class="role-icon">
          <el-icon size="32" :color="getRoleColor(role.roleCode)">
            <component :is="getRoleIcon(role.roleCode)" />
          </el-icon>
        </div>
        
        <div class="role-info">
          <div class="role-name">{{ role.roleName }}</div>
          <div class="role-desc">{{ role.description }}</div>
          <div class="demo-user">
            演示账户: {{ role.demoUser.realName }}
          </div>
        </div>
        
        <div class="role-action">
          <el-button
            type="primary"
            size="small"
            :loading="loginLoading && selectedRole?.id === role.id"
            @click.stop="handleQuickLogin(role)"
          >
            {{ loginLoading && selectedRole?.id === role.id ? '登录中...' : '快速登录' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="role-tips">
      <el-alert
        title="提示"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>• 演示系统已预置各角色账户，密码均为 <code>admin123</code></p>
          <p>• 不同角色登录后将显示对应的功能界面</p>
          <p>• 管理员具有所有权限，其他角色按业务分工限制</p>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Management,
  Shop,
  Van,
  ShoppingCart,
  Monitor
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { demoRoles, type Role } from '@/api/role'

// 定义事件
const emit = defineEmits<{
  roleSelected: [role: Role]
  quickLogin: [credentials: { username: string; password: string; role: Role }]
}>()

const router = useRouter()
const userStore = useUserStore()

// 状态
const roles = ref<Role[]>([])
const selectedRole = ref<Role | null>(null)
const loginLoading = ref(false)

// 获取角色图标
const getRoleIcon = (roleCode: string) => {
  const iconMap: Record<string, any> = {
    admin: Management,
    supplier: Shop,
    logistics: Van,
    purchaser: ShoppingCart,
    regulator: Monitor
  }
  return iconMap[roleCode] || Management
}

// 获取角色颜色
const getRoleColor = (roleCode: string) => {
  const colorMap: Record<string, string> = {
    admin: '#ff4d4f',
    supplier: '#52c41a',
    logistics: '#1890ff',
    purchaser: '#faad14',
    regulator: '#722ed1'
  }
  return colorMap[roleCode] || '#666'
}

// 处理角色点击
const handleRoleClick = (role: Role) => {
  selectedRole.value = role
  emit('roleSelected', role)
}

// 处理快速登录
const handleQuickLogin = async (role: Role) => {
  if (loginLoading.value) return
  
  selectedRole.value = role
  loginLoading.value = true
  
  try {
    const credentials = {
      username: role.demoUser.username,
      password: role.demoUser.password
    }
    
    // 调用用户store的登录方法
    await userStore.login(credentials)
    
    ElMessage.success(`${role.roleName}登录成功！`)
    
    // 根据角色跳转到对应控制台
    const dashboardPath = getDashboardPath(role.roleCode)
    router.push(dashboardPath)
    
    // 触发事件
    emit('quickLogin', {
      username: credentials.username,
      password: credentials.password,
      role
    })
    
  } catch (error: any) {
    console.error('快速登录失败:', error)
    ElMessage.error(error.message || `${role.roleName}登录失败`)
  } finally {
    loginLoading.value = false
  }
}

// 根据角色获取控制台路径
const getDashboardPath = (roleCode: string): string => {
  const dashboardMap: Record<string, string> = {
    admin: '/admin/dashboard',
    supplier: '/supplier/dashboard',
    logistics: '/logistics/dashboard',
    purchaser: '/purchaser/dashboard',
    regulator: '/regulator/dashboard'
  }
  return dashboardMap[roleCode] || '/admin/dashboard'
}

// 组件挂载
onMounted(() => {
  roles.value = demoRoles
})
</script>

<style scoped>
.role-selector {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 0 16px;
}

.selector-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin: 0 0 8px 0;
}

.selector-subtitle {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin: 0 0 24px 0;
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.role-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #ffffff;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-height: 100px;
}

.role-card:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
  transform: translateY(-2px);
}

.role-card.active {
  border-color: #1890ff;
  background: #f0f9ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.role-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: transparent;
  transition: background 0.3s;
}

.role-card.active::before {
  background: #1890ff;
}

.role-icon {
  flex-shrink: 0;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.role-card:hover .role-icon {
  background: #e6f7ff;
  transform: scale(1.05);
}

.role-info {
  flex: 1;
  min-width: 0;
  padding-right: 12px;
}

.role-name {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.2;
}

.role-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.4;
}

.demo-user {
  font-size: 12px;
  color: #1890ff;
  background: #f0f9ff;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
  border: 1px solid #d6f3ff;
}

.role-action {
  flex-shrink: 0;
}

.role-tips {
  margin-top: 32px;
  padding: 0 8px;
}

.role-tips :deep(.el-alert) {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
  padding: 16px;
}

.role-tips :deep(.el-alert__content) {
  font-size: 13px;
  line-height: 1.6;
}

.role-tips :deep(.el-alert__content) p {
  margin: 0 0 8px 0;
}

.role-tips :deep(.el-alert__content) p:last-child {
  margin-bottom: 0;
}

.role-tips :deep(.el-alert__content) code {
  background: #fff1b8;
  color: #d48806;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .role-selector {
    padding: 0 12px;
  }
  
  .role-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .role-card {
    padding: 16px;
    min-height: 80px;
  }
  
  .role-icon {
    width: 48px;
    height: 48px;
  }
  
  .role-name {
    font-size: 16px;
  }
  
  .selector-title {
    font-size: 18px;
  }
}

/* 动画效果 */
.role-card {
  animation: slideInUp 0.4s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 加载状态 */
.role-card .el-button.is-loading {
  pointer-events: none;
}
</style>