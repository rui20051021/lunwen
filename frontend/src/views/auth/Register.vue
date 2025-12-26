<template>
  <div class="register-container">
    <div class="register-form">
      <div class="register-header">
        <h1 class="register-title">用户注册</h1>
        <p class="register-subtitle">创建您的Fresh Logistics账户</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        size="large"
        label-position="top"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（字母、数字、下划线）"
            prefix-icon="User"
            clearable
            @blur="checkUsernameAvailable(registerForm.username)"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            用户名将用于登录，注册后不可修改
          </div>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="registerForm.userType" placeholder="请选择您的角色类型" style="width: 100%">
            <el-option value="admin">
              <span style="display: flex; align-items: center; gap: 8px;">
                <span style="font-size: 18px;">👨‍💼</span>
                <span>
                  <div style="font-weight: 600;">系统管理员</div>
                  <div style="font-size: 12px; color: #999;">系统管理和配置权限</div>
                </span>
              </span>
            </el-option>
            <el-option value="supplier">
              <span style="display: flex; align-items: center; gap: 8px;">
                <span style="font-size: 18px;">🏭</span>
                <span>
                  <div style="font-weight: 600;">供应商</div>
                  <div style="font-size: 12px; color: #999;">产品供应和订单管理</div>
                </span>
              </span>
            </el-option>
            <el-option value="logistics">
              <span style="display: flex; align-items: center; gap: 8px;">
                <span style="font-size: 18px;">🚛</span>
                <span>
                  <div style="font-weight: 600;">物流商</div>
                  <div style="font-size: 12px; color: #999;">运输和车辆管理</div>
                </span>
              </span>
            </el-option>
            <el-option value="purchaser">
              <span style="display: flex; align-items: center; gap: 8px;">
                <span style="font-size: 18px;">🛒</span>
                <span>
                  <div style="font-weight: 600;">采购商</div>
                  <div style="font-size: 12px; color: #999;">采购和收货管理</div>
                </span>
              </span>
            </el-option>
            <el-option value="regulator">
              <span style="display: flex; align-items: center; gap: 8px;">
                <span style="font-size: 18px;">👮</span>
                <span>
                  <div style="font-weight: 600;">监管员</div>
                  <div style="font-size: 12px; color: #999;">监管和合规检查</div>
                </span>
              </span>
            </el-option>
          </el-select>
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            请根据您的实际业务身份选择对应角色
          </div>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            clearable
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            clearable
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="registerLoading"
            @click="handleRegister"
          >
            {{ registerLoading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <el-button type="text" @click="$router.push('/login')">
          已有账户？立即登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()

// 表单引用
const registerFormRef = ref<FormInstance>()

// 注册表单
const registerForm = reactive({
  username: '',
  realName: '',
  userType: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为2-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字、下划线', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度为2-50位', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 注册状态
const registerLoading = ref(false)

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    registerLoading.value = true
    
    try {
      // 准备注册数据（排除confirmPassword）
      const { confirmPassword, ...registerData } = registerForm
      
      console.log('📝 提交注册数据:', registerData)
      
      // 调用真实数据库注册API
      const response = await fetch('http://localhost:8080/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(registerData)
      })
      
      const data = await response.json()
      console.log('📊 注册API响应:', data)
      
      if (data.code === 200) {
        // 注册成功
        ElMessage.success('🎉 注册成功！正在为您登录...')
        
        // 获取跳转路径
        const redirectPath = data.data.redirectPath || '/login'
        
        console.log('✅ 注册成功，准备跳转到:', redirectPath)
        
        // 自动登录
        setTimeout(async () => {
          try {
            const loginResponse = await fetch('http://localhost:8080/auth/login', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                username: registerData.username,
                password: registerData.password
              })
            })
            
            const loginData = await loginResponse.json()
            
            if (loginData.code === 200) {
              // 保存登录信息
              localStorage.setItem('token', loginData.data.accessToken)
              localStorage.setItem('user', JSON.stringify(loginData.data.userInfo))
              localStorage.setItem('roles', JSON.stringify(loginData.data.roles))
              localStorage.setItem('permissions', JSON.stringify(loginData.data.permissions))
              
              ElMessage.success(`✅ 欢迎，${registerData.realName}！正在跳转...`)
              
              // 根据用户类型跳转到对应Dashboard
              setTimeout(() => {
                router.push(redirectPath)
              }, 1000)
            } else {
              // 自动登录失败，跳转到登录页
              ElMessage.info('请使用您的账号登录')
              router.push('/login')
            }
          } catch (error) {
            console.error('自动登录失败:', error)
            router.push('/login')
          }
        }, 1500)
        
      } else {
        // 注册失败
        ElMessage.error(data.message || '注册失败，请重试')
        console.error('❌ 注册失败:', data.message)
      }
      
    } catch (error: any) {
      console.error('💥 注册请求失败:', error)
      ElMessage.error('网络错误，请检查连接后重试')
    } finally {
      registerLoading.value = false
    }
  })
}

// 检查用户名是否可用（实时验证）
const checkUsernameAvailable = async (username: string) => {
  if (!username || username.length < 2) return
  
  try {
    const response = await fetch(`http://localhost:8080/auth/check-username?username=${username}`)
    const data = await response.json()
    
    if (data.code === 200 && !data.data) {
      ElMessage.warning('用户名已存在，请更换')
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.register-form {
  width: 450px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.register-subtitle {
  color: #666;
  font-size: 14px;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
}
</style>

