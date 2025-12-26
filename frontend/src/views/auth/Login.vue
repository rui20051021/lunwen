<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-header">
        <h1 class="login-title">Fresh Logistics</h1>
        <p class="login-subtitle">冷链物流智能监测预警系统</p>
      </div>
      
      <!-- 登录方式切换 -->
      <div class="login-mode-switch">
        <el-radio-group v-model="loginMode" size="small">
          <el-radio-button label="role">角色快捷登录</el-radio-button>
          <el-radio-button label="manual">手动登录</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 角色快捷登录 -->
      <div v-if="loginMode === 'role'" class="role-login-section">
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
                <el-icon size="32" :color="role.color">
                  <component :is="role.icon" />
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
                  :loading="quickLoginLoading && selectedRole?.id === role.id"
                  @click.stop="handleQuickLogin(role)"
                >
                  {{ quickLoginLoading && selectedRole?.id === role.id ? '登录中...' : '快速登录' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 手动登录表单 -->
      <div v-else class="manual-login-section">
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          size="large"
          label-position="top"
          @submit.prevent="handleManualLogin"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              clearable
              show-password
              @keyup.enter="handleManualLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="manualLoginLoading"
              @click="handleManualLogin"
            >
              {{ manualLoginLoading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="login-footer">
        <div class="demo-account">
          <h4>💡 快捷登录提示：</h4>
          <p>点击上方角色卡片即可一键登录，无需手动输入账户信息</p>
          <p>或使用演示账户手动登录: <code>admin</code> / <code>admin123</code></p>
        </div>
        <div style="text-align: center; margin-top: 16px;">
          <span style="color: #666;">还没有账户？</span>
          <el-button type="primary" text @click="$router.push('/register')">
            立即注册
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { 
  Management,
  Shop,
  Van,
  ShoppingCart,
  Monitor,
  User,
  Lock
} from '@element-plus/icons-vue'

const router = useRouter()

// 角色数据
const roles = ref([
  {
    id: 1,
    roleCode: 'admin',
    roleName: '系统管理员',
    description: '系统管理和配置权限',
    color: '#ff4d4f',
    icon: 'Management',
    demoUser: {
      username: 'admin',
      password: 'admin123',
      realName: '系统管理员'
    }
  },
  {
    id: 2,
    roleCode: 'supplier',
    roleName: '供应商',
    description: '产品供应和订单管理',
    color: '#52c41a',
    icon: 'Shop',
    demoUser: {
      username: 'supplier01',
      password: 'admin123',
      realName: '张经理'
    }
  },
  {
    id: 3,
    roleCode: 'logistics',
    roleName: '物流商',
    description: '运输和车辆管理',
    color: '#1890ff',
    icon: 'Van',
    demoUser: {
      username: 'logistics01',
      password: 'admin123',
      realName: '王物流'
    }
  },
  {
    id: 4,
    roleCode: 'purchaser',
    roleName: '采购商',
    description: '采购和收货管理',
    color: '#faad14',
    icon: 'ShoppingCart',
    demoUser: {
      username: 'purchaser01',
      password: 'admin123',
      realName: '刘采购'
    }
  },
  {
    id: 5,
    roleCode: 'regulator',
    roleName: '监管员',
    description: '监管和合规检查',
    color: '#722ed1',
    icon: 'Monitor',
    demoUser: {
      username: 'regulator01',
      password: 'admin123',
      realName: '监管员A'
    }
  }
])

// 状态
const loginMode = ref('role')
const selectedRole = ref(null)
const quickLoginLoading = ref(false)
const manualLoginLoading = ref(false)

// 手动登录表单
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 处理角色点击
const handleRoleClick = (role) => {
  selectedRole.value = role
  console.log('选择角色:', role)
}

// 处理快捷登录
const handleQuickLogin = async (role) => {
  if (quickLoginLoading.value) return
  
  selectedRole.value = role
  quickLoginLoading.value = true
  
  try {
    console.log('开始快捷登录:', role.roleName)
    
    // 调用后端登录API
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: role.demoUser.username,
        password: role.demoUser.password
      })
    })
    
    const data = await response.json()
    console.log('登录响应:', data)
    
    if (data.code === 200) {
      // 保存登录信息到localStorage
      localStorage.setItem('token', data.data.accessToken)
      localStorage.setItem('user', JSON.stringify(data.data.userInfo))
      localStorage.setItem('roles', JSON.stringify(data.data.roles))
      localStorage.setItem('permissions', JSON.stringify(data.data.permissions))
      
      ElMessage.success(`${role.roleName}登录成功！`)
      
      // 根据角色跳转到对应控制台
      const dashboardPath = getDashboardPath(role.roleCode)
      console.log('跳转路径:', dashboardPath)
      
      setTimeout(() => {
        router.push(dashboardPath)
      }, 1000)
      
    } else {
      ElMessage.error(data.message || '登录失败')
    }
    
  } catch (error) {
    console.error('快捷登录失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    quickLoginLoading.value = false
  }
}

// 处理手动登录
const handleManualLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    manualLoginLoading.value = true
    
    try {
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginForm)
      })
      
      const data = await response.json()
      
      if (data.code === 200) {
        // 保存登录信息
        localStorage.setItem('token', data.data.accessToken)
        localStorage.setItem('user', JSON.stringify(data.data.userInfo))
        localStorage.setItem('roles', JSON.stringify(data.data.roles))
        localStorage.setItem('permissions', JSON.stringify(data.data.permissions))
        
        ElMessage.success('登录成功')
        
        // 根据用户类型跳转
        const userType = data.data.userInfo.userType
        const dashboardPath = getDashboardPath(userType)
        router.push(dashboardPath)
        
      } else {
        ElMessage.error(data.message || '登录失败')
      }
      
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    } finally {
      manualLoginLoading.value = false
    }
  })
}

// 根据角色获取控制台路径
const getDashboardPath = (roleCode) => {
  const dashboardMap = {
    admin: '/admin/dashboard',
    supplier: '/supplier/dashboard',
    logistics: '/logistics/dashboard',
    purchaser: '/purchaser/dashboard',
    regulator: '/regulator/dashboard'
  }
  return dashboardMap[roleCode] || '/dashboard'
}

// 组件挂载
onMounted(() => {
  console.log('登录页面已加载')
  // 清空之前的登录信息
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('roles')
  localStorage.removeItem('permissions')
})
</script>

<style scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  padding: 30px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.login-subtitle {
  color: #666;
  font-size: 14px;
}

.login-mode-switch {
  text-align: center;
  margin-bottom: 24px;
}

.role-login-section, .manual-login-section {
  margin-bottom: 20px;
}

.role-selector {
  width: 100%;
}

.selector-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 8px;
}

.selector-subtitle {
  font-size: 13px;
  color: #666;
  text-align: center;
  margin-bottom: 20px;
}

.role-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  margin-bottom: 20px;
}

.role-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #ffffff;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.role-card:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.role-card.active {
  border-color: #1890ff;
  background: #f0f9ff;
}

.role-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.role-info {
  flex: 1;
}

.role-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.role-desc {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.demo-user {
  font-size: 11px;
  color: #1890ff;
  background: #f0f9ff;
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
}

.role-action {
  flex-shrink: 0;
}

.login-footer {
  margin-top: 20px;
  font-size: 12px;
  color: #666;
}

.demo-account {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #1890ff;
}

.demo-account h4 {
  margin-bottom: 6px;
  color: #333;
  font-size: 13px;
}

.demo-account p {
  margin: 4px 0;
}

.demo-account code {
  background: #e8f4fd;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Monaco', monospace;
  color: #1890ff;
}
</style>