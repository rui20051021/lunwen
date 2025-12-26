<template>
  <div class="users-page">
    <div class="page-header">
      <h1>👥 用户管理</h1>
      <p>管理系统用户，分配角色权限</p>
    </div>
    
    <!-- 用户统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👨‍💼</div>
        <div class="stat-info">
          <div class="stat-number">{{ userStats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🏭</div>
        <div class="stat-info">
          <div class="stat-number">{{ userStats.supplierCount }}</div>
          <div class="stat-label">供应商</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🚛</div>
        <div class="stat-info">
          <div class="stat-number">{{ userStats.logisticsCount }}</div>
          <div class="stat-label">物流商</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🛒</div>
        <div class="stat-info">
          <div class="stat-number">{{ userStats.purchaserCount }}</div>
          <div class="stat-label">采购商</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">👮</div>
        <div class="stat-info">
          <div class="stat-number">{{ userStats.regulatorCount }}</div>
          <div class="stat-label">监管员</div>
        </div>
      </div>
    </div>
    
    <!-- 用户列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>用户列表</h3>
        <div class="table-actions">
          <el-button type="primary" @click="handleAddUser">
            添加用户
          </el-button>
          <el-button type="success" @click="refreshUsers">
            刷新数据
          </el-button>
        </div>
      </div>
      
      <el-table :data="userList" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="userType" label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getUserTypeColor(row.userType)">
              {{ getUserTypeName(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" text @click="handleResetPassword(row)">
              重置密码
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              text 
              @click="handleDelete(row)"
              v-if="row.username !== 'admin'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 角色权限分析 -->
    <div class="role-analysis">
      <h3>角色权限分析</h3>
      <div class="role-grid">
        <div v-for="role in roleStats" :key="role.roleCode" class="role-item">
          <div class="role-header">
            <span class="role-icon">{{ role.icon }}</span>
            <span class="role-name">{{ role.roleName }}</span>
          </div>
          <div class="role-stats">
            <div class="role-stat">
              <span class="stat-label">用户数:</span>
              <span class="stat-value">{{ role.userCount }}</span>
            </div>
            <div class="role-stat">
              <span class="stat-label">权限数:</span>
              <span class="stat-value">{{ role.permissionCount }}</span>
            </div>
          </div>
          <div class="role-desc">{{ role.description }}</div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="userForm.username" 
            placeholder="请输入用户名（用于登录）"
            :disabled="isEdit"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input 
            v-model="userForm.password" 
            type="password"
            placeholder="请输入密码（6-20位）"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input 
            v-model="userForm.realName" 
            placeholder="请输入真实姓名"
          />
        </el-form-item>
        
        <el-form-item label="用户类型" prop="userType">
          <el-select 
            v-model="userForm.userType" 
            placeholder="请选择用户类型"
            style="width: 100%"
          >
            <el-option label="供应商" value="supplier" />
            <el-option label="物流商" value="logistics" />
            <el-option label="采购商" value="purchaser" />
            <el-option label="监管员" value="regulator" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="userForm.email" 
            placeholder="请输入邮箱"
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="userForm.phone" 
            placeholder="请输入手机号"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

// 用户统计数据
const userStats = reactive({
  totalUsers: 0,
  supplierCount: 0,
  logisticsCount: 0,
  purchaserCount: 0,
  regulatorCount: 0
})

// 用户列表数据 (将从真实数据库获取)
const userList = ref([])

// 对话框状态
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const userFormRef = ref<FormInstance>()

// 对话框标题
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '添加用户')

// 用户表单数据
const userForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  userType: '',
  email: '',
  phone: '',
  status: 1
})

// 表单验证规则
const userRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符', trigger: 'blur' }
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
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

// 角色统计数据
const roleStats = ref([
  {
    roleCode: 'admin',
    roleName: '系统管理员',
    icon: '👨‍💼',
    userCount: 1,
    permissionCount: 46,
    description: '拥有系统所有权限'
  },
  {
    roleCode: 'supplier',
    roleName: '供应商',
    icon: '🏭',
    userCount: 2,
    permissionCount: 12,
    description: '产品供应和订单管理'
  },
  {
    roleCode: 'logistics',
    roleName: '物流商',
    icon: '🚛',
    userCount: 2,
    permissionCount: 15,
    description: '运输和车辆管理'
  },
  {
    roleCode: 'purchaser',
    roleName: '采购商',
    icon: '🛒',
    userCount: 2,
    permissionCount: 8,
    description: '采购和收货管理'
  },
  {
    roleCode: 'regulator',
    roleName: '监管员',
    icon: '👮',
    userCount: 2,
    permissionCount: 11,
    description: '监管和合规检查'
  }
])

// 获取用户类型颜色
const getUserTypeColor = (userType: string) => {
  const colorMap: Record<string, string> = {
    admin: 'danger',
    supplier: 'success',
    logistics: 'primary',
    purchaser: 'warning',
    regulator: ''
  }
  return colorMap[userType] || ''
}

// 获取用户类型名称
const getUserTypeName = (userType: string) => {
  const nameMap: Record<string, string> = {
    admin: '管理员',
    supplier: '供应商',
    logistics: '物流商',
    purchaser: '采购商',
    regulator: '监管员'
  }
  return nameMap[userType] || userType
}

// 操作函数
const handleAddUser = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    userType: row.userType,
    email: row.email,
    phone: row.phone,
    status: row.status
  })
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(userForm, {
    id: null,
    username: '',
    password: '',
    realName: '',
    userType: '',
    email: '',
    phone: '',
    status: 1
  })
  userFormRef.value?.clearValidate()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      const url = isEdit.value 
        ? `http://localhost:8080/database/user/update/${userForm.id}`
        : 'http://localhost:8080/database/user/create'
      
      const method = 'POST'
      
      // 准备提交数据
      const submitData = {
        username: userForm.username,
        password: userForm.password,
        realName: userForm.realName,
        userType: userForm.userType,
        email: userForm.email,
        phone: userForm.phone,
        status: userForm.status
      }
      
      // 如果是编辑模式，不发送密码（除非用户要修改）
      if (isEdit.value && !userForm.password) {
        delete submitData.password
      }
      
      console.log('提交数据:', submitData)
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(submitData)
      })
      
      const data = await response.json()
      console.log('API响应:', data)
      
      if (data.code === 200) {
        ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
        dialogVisible.value = false
        await refreshUsers() // 刷新用户列表
      } else {
        ElMessage.error(data.message || '操作失败')
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败，请检查网络连接')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleResetPassword = (row: any) => {
  ElMessage.info(`重置密码: ${row.realName}`)
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.realName}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await fetch(`http://localhost:8080/database/user/delete/${row.id}`, {
      method: 'DELETE'
    })
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success('删除成功')
      await refreshUsers() // 刷新用户列表
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败，请检查网络连接')
    }
  }
}

const refreshUsers = async () => {
  try {
    // 🔄 调用真实数据库API获取用户数据（直接访问后端）
    const response = await fetch('http://localhost:8080/database/user/all')
    const data = await response.json()
    
    console.log('API响应:', data) // 调试用
    
    if (data.code === 200) {
      // 🔄 使用真实数据库数据，转换数据格式
      const rawUsers = data.data || []
      userList.value = rawUsers.map(user => ({
        id: user.id,
        username: user.username,
        realName: user.real_name, // 数据库字段是 real_name
        email: user.email,
        phone: user.phone,
        userType: user.user_type, // 数据库字段是 user_type
        status: user.status,
        lastLoginTime: user.last_login_time ? new Date(user.last_login_time).toLocaleString() : '从未登录'
      }))
      
      // 更新统计数据
      const users = userList.value
      userStats.totalUsers = users.length
      userStats.supplierCount = users.filter(u => u.userType === 'supplier').length
      userStats.logisticsCount = users.filter(u => u.userType === 'logistics').length
      userStats.purchaserCount = users.filter(u => u.userType === 'purchaser').length
      userStats.regulatorCount = users.filter(u => u.userType === 'regulator').length
      
      ElMessage.success(`✅ 成功获取 ${users.length} 个用户数据（真实数据库）`)
      console.log('真实用户数据:', userList.value)
    } else {
      ElMessage.error(`获取用户数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('刷新用户数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接或后端服务')
  }
}

// 页面加载时获取用户数据
onMounted(() => {
  refreshUsers()
})
</script>

<style scoped>
.users-page {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.table-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h3 {
  color: #333;
  font-size: 18px;
  margin: 0;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.role-analysis {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.role-analysis h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.role-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.role-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.role-icon {
  font-size: 16px;
}

.role-name {
  font-weight: 600;
  color: #333;
}

.role-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.role-stat {
  display: flex;
  gap: 4px;
  font-size: 12px;
}

.stat-label {
  color: #666;
}

.stat-value {
  color: #1890ff;
  font-weight: 600;
}

.role-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .table-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .role-grid {
    grid-template-columns: 1fr;
  }
}
</style>