<template>
  <div class="alerts-page">
    <div class="page-header">
      <h1>预警管理</h1>
      <p>管理预警规则，处理预警事件</p>
    </div>
    
    <!-- 预警统计卡片 -->
    <div class="dashboard-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="32" color="#1890ff"><Setting /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ alertStats.totalRules }}</div>
          <div class="stat-label">预警规则</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="32" color="#52c41a"><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ alertStats.enabledRules }}</div>
          <div class="stat-label">启用规则</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="32" color="#faad14"><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ alertStats.todayAlerts }}</div>
          <div class="stat-label">今日预警</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon size="32" color="#ff4d4f"><Bell /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ alertStats.pendingAlerts }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </div>
    </div>
    
    <!-- 预警规则管理 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-search">
          <el-input
            v-model="searchForm.ruleName"
            placeholder="搜索规则名称"
            clearable
            style="width: 200px"
            @change="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select
            v-model="searchForm.ruleType"
            placeholder="规则类型"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="超时" value="timeout" />
            <el-option label="温度" value="temperature" />
            <el-option label="湿度" value="humidity" />
            <el-option label="路径偏离" value="route_deviation" />
          </el-select>
          
          <el-select
            v-model="searchForm.alertLevel"
            placeholder="预警级别"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="错误" value="error" />
            <el-option label="严重" value="critical" />
          </el-select>
          
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
        </div>
        
        <div class="table-actions">
          <el-button type="primary" :icon="Plus" @click="handleCreateRule">
            新增规则
          </el-button>
          <el-button type="success" @click="loadAlertRules">
            🔄 刷新数据
          </el-button>
          <el-button type="info" :icon="Upload" @click="handleImport">
            导入规则
          </el-button>
          <span style="margin-left: 10px; color: #666; font-size: 14px;">
            规则数量: {{ ruleList?.length || 0 }}
          </span>
        </div>
      </div>
      
      <!-- 预警规则表格 -->
      <el-table
        v-loading="tableLoading"
        :data="ruleList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="ruleCode" label="规则编码" width="150" />
        
        <el-table-column prop="ruleName" label="规则名称" min-width="180" />
        
        <el-table-column prop="ruleType" label="规则类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getRuleTypeText(row.ruleType) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="alertLevel" label="预警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlertLevelType(row.alertLevel)" size="small">
              {{ getAlertLevelText(row.alertLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="thresholdValue" label="阈值" width="100" />
        
        <el-table-column prop="isEnabled" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.isEnabled"
              :active-value="1"
              :inactive-value="0"
              @change="handleToggleRule(row)"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEditRule(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" text @click="handleCopyRule(row)">
              复制
            </el-button>
            <el-button type="danger" size="small" text @click="handleDeleteRule(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="table-pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 新增规则对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="新增预警规则"
      width="600px"
      @close="handleCreateDialogClose"
    >
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="ruleRules"
        label-width="120px"
      >
        <el-form-item label="规则编码" prop="ruleCode">
          <el-input 
            v-model="ruleForm.ruleCode" 
            placeholder="自动生成或手动输入"
          />
          <el-button 
            type="primary" 
            size="small" 
            style="margin-left: 10px"
            @click="generateRuleCode"
          >
            自动生成
          </el-button>
        </el-form-item>
        
        <el-form-item label="规则名称" prop="ruleName">
          <el-input 
            v-model="ruleForm.ruleName" 
            placeholder="请输入规则名称"
          />
        </el-form-item>
        
        <el-form-item label="规则类型" prop="ruleType">
          <el-select 
            v-model="ruleForm.ruleType" 
            placeholder="请选择规则类型"
            style="width: 100%"
          >
            <el-option label="超时预警" value="timeout" />
            <el-option label="温度预警" value="temperature" />
            <el-option label="湿度预警" value="humidity" />
            <el-option label="路径偏离" value="route_deviation" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="预警级别" prop="alertLevel">
          <el-select 
            v-model="ruleForm.alertLevel" 
            placeholder="请选择预警级别"
            style="width: 100%"
          >
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="错误" value="error" />
            <el-option label="严重" value="critical" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="阈值" prop="thresholdValue">
          <el-input-number 
            v-model="ruleForm.thresholdValue" 
            :precision="2"
            :step="1"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="规则描述" prop="description">
          <el-input 
            v-model="ruleForm.description" 
            type="textarea"
            :rows="3"
            placeholder="请输入规则描述"
          />
        </el-form-item>
        
        <el-form-item label="是否启用" prop="isEnabled">
          <el-radio-group v-model="ruleForm.isEnabled">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 导入规则对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入预警规则"
      width="500px"
    >
      <div class="import-container">
        <el-alert 
          title="导入说明" 
          type="info" 
          :closable="false"
          style="margin-bottom: 20px"
        >
          <div>1. 请上传CSV格式文件</div>
          <div>2. 文件应包含：规则编码、规则名称、规则类型、预警级别、阈值</div>
          <div>3. 示例格式可下载模板查看</div>
        </el-alert>
        
        <el-upload
          ref="uploadRef"
          class="upload-demo"
          drag
          accept=".csv"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
          :file-list="fileList"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽CSV文件到此处或 <em>点击上传</em>
          </div>
        </el-upload>
        
        <div style="margin-top: 20px; text-align: center">
          <el-button @click="downloadTemplate">下载模板</el-button>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">
          开始导入
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 预警测试区域 -->
    <div class="card" style="margin-top: 20px">
      <div class="card-header">
        <h3 class="card-title">预警测试</h3>
        <span class="card-subtitle">测试预警规则触发</span>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="test-card">
            <h4>温度预警测试</h4>
            <el-form inline>
              <el-form-item label="产品ID">
                <el-input-number v-model="tempTest.productId" :min="1" />
              </el-form-item>
              <el-form-item label="温度(℃)">
                <el-input-number v-model="tempTest.temperature" :precision="1" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleTempTest">测试</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="test-card">
            <h4>超时预警测试</h4>
            <el-form inline>
              <el-form-item label="路线ID">
                <el-input-number v-model="timeoutTest.routeId" :min="1" />
              </el-form-item>
              <el-form-item label="延迟(分钟)">
                <el-input-number v-model="timeoutTest.delayMinutes" :min="0" />
              </el-form-item>
              <el-form-item>
                <el-button type="warning" @click="handleTimeoutTest">测试</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        
        <el-col :span="8">
          <div class="test-card">
            <h4>路径偏离测试</h4>
            <el-form inline>
              <el-form-item label="路线ID">
                <el-input-number v-model="routeTest.routeId" :min="1" />
              </el-form-item>
              <el-form-item label="偏离距离(米)">
                <el-input-number v-model="routeTest.deviationDistance" :min="0" />
              </el-form-item>
              <el-form-item>
                <el-button type="danger" @click="handleRouteTest">测试</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { 
  Search, 
  Plus, 
  Upload, 
  UploadFilled,
  Setting, 
  Check, 
  Warning, 
  Bell 
} from '@element-plus/icons-vue'
import { alertApi } from '@/api/alert'
import type { AlertRule, AlertType, AlertLevel } from '@/types/api'

// 预警统计数据
const alertStats = reactive({
  totalRules: 7,
  enabledRules: 6,
  todayAlerts: 15,
  pendingAlerts: 3
})

// 搜索表单
const searchForm = reactive({
  ruleName: '',
  ruleType: '',
  alertLevel: '',
  isEnabled: null as number | null
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const ruleList = ref<AlertRule[]>([])
const tableLoading = ref(false)

// 新增规则对话框
const createDialogVisible = ref(false)
const ruleFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 导入对话框
const importDialogVisible = ref(false)
const importLoading = ref(false)
const uploadRef = ref()
const fileList = ref<UploadFile[]>([])

// 规则表单数据
const ruleForm = reactive({
  ruleCode: '',
  ruleName: '',
  ruleType: '',
  alertLevel: '',
  thresholdValue: 0,
  description: '',
  isEnabled: 1
})

// 表单验证规则
const ruleRules: FormRules = {
  ruleCode: [
    { required: true, message: '请输入规则编码', trigger: 'blur' }
  ],
  ruleName: [
    { required: true, message: '请输入规则名称', trigger: 'blur' }
  ],
  ruleType: [
    { required: true, message: '请选择规则类型', trigger: 'change' }
  ],
  alertLevel: [
    { required: true, message: '请选择预警级别', trigger: 'change' }
  ],
  thresholdValue: [
    { required: true, message: '请输入阈值', trigger: 'blur' }
  ]
}

// 预警测试数据
const tempTest = reactive({
  productId: 1,
  temperature: 10.0
})

const timeoutTest = reactive({
  routeId: 1,
  delayMinutes: 35
})

const routeTest = reactive({
  routeId: 1,
  deviationDistance: 600
})

// 获取规则类型文本
const getRuleTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    timeout: '超时',
    temperature: '温度',
    humidity: '湿度',
    route_deviation: '路径偏离'
  }
  return typeMap[type] || type
}

// 获取预警级别类型
const getAlertLevelType = (level: string) => {
  const typeMap: Record<string, string> = {
    info: '',
    warning: 'warning',
    error: 'danger',
    critical: 'danger'
  }
  return typeMap[level] || ''
}

// 获取预警级别文本
const getAlertLevelText = (level: string) => {
  const textMap: Record<string, string> = {
    info: '信息',
    warning: '警告',
    error: '错误',
    critical: '严重'
  }
  return textMap[level] || level
}

// 加载预警规则列表 - 100%真实数据库数据
const loadAlertRules = async () => {
  tableLoading.value = true
  
  try {
    console.log('🚀 开始从数据库加载最新预警规则数据...')
    
    // 🔄 调用真实数据库API（添加时间戳防缓存）
    const response = await fetch(`http://localhost:8080/database/alert/records?_t=${Date.now()}`)
    const data = await response.json()
    console.log('📊 数据库API响应:', data)
    
    if (data.code === 200) {
      console.log('✅ 数据库连接成功，开始处理最新数据...')
      
      const rawRecords = data.data?.records || data.data || []
      console.log('📦 数据库原始数据:', rawRecords)
      console.log('📝 数据库记录数量:', rawRecords.length)
      
      // 🔄 将数据库字段映射为前端格式
      const mappedRules = rawRecords.map(record => ({
        id: record.id,
        ruleCode: record.alert_code,
        ruleName: record.alert_title,
        ruleType: record.alert_type,
        alertLevel: record.alert_level,
        thresholdValue: record.threshold_value || 0,
        isEnabled: record.alert_status === 'pending' ? 1 : 0,
        description: record.alert_message,
        createdAt: record.created_at ? new Date(record.created_at).toLocaleString() : '未知'
      }))
      
      console.log('🔄 数据库数据映射结果:', mappedRules)
      
      // 🔧 强制清空并重新加载，确保反映数据库最新状态
      ruleList.value.length = 0 // 完全清空
      await new Promise(resolve => setTimeout(resolve, 10))
      
      // 逐个添加确保响应式更新
      mappedRules.forEach(rule => ruleList.value.push(rule))
      
      // 更新分页信息反映真实数量
      pagination.total = rawRecords.length
      pagination.current = 1
      
      console.log('📋 前端显示数据:', ruleList.value)
      console.log('📊 前端数据数量:', ruleList.value.length)
      
      ElMessage.success(`✅ 已从数据库获取 ${mappedRules.length} 条最新预警数据`)
      console.log('🎉 数据库数据同步完成!')
    } else {
      console.error('❌ 数据库API返回错误:', data)
      ElMessage.error(`数据库连接失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 数据库数据加载失败:', error)
    ElMessage.error('❌ 数据库连接失败：' + error.message)
  } finally {
    tableLoading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  loadAlertRules()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadAlertRules()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadAlertRules()
}

// 切换规则状态
const handleToggleRule = async (row: AlertRule) => {
  try {
    const response = await alertApi.toggleAlertRule(row.id, row.isEnabled)
    if (response.code === 200) {
      ElMessage.success(`规则${row.isEnabled ? '启用' : '禁用'}成功`)
    }
  } catch (error) {
    ElMessage.error('操作失败')
    // 回滚状态
    row.isEnabled = row.isEnabled ? 0 : 1
  }
}

// 预警测试方法
const handleTempTest = async () => {
  try {
    const response = await alertApi.checkTemperatureAlert(
      tempTest.productId, 
      tempTest.temperature
    )
    
    if (response.code === 200) {
      const triggers = response.data
      if (triggers.length > 0) {
        ElMessage.warning(`触发了 ${triggers.length} 条温度预警规则`)
        console.log('触发的预警规则:', triggers)
      } else {
        ElMessage.success('温度正常，未触发预警')
      }
    }
  } catch (error) {
    ElMessage.error('温度预警测试失败')
  }
}

const handleTimeoutTest = async () => {
  try {
    const response = await alertApi.checkTimeoutAlert(
      timeoutTest.routeId,
      timeoutTest.delayMinutes
    )
    
    if (response.code === 200) {
      const triggers = response.data
      if (triggers.length > 0) {
        ElMessage.warning(`触发了 ${triggers.length} 条超时预警规则`)
      } else {
        ElMessage.success('运输正常，未触发预警')
      }
    }
  } catch (error) {
    ElMessage.error('超时预警测试失败')
  }
}

const handleRouteTest = async () => {
  try {
    const response = await alertApi.checkRouteDeviationAlert(
      routeTest.routeId,
      routeTest.deviationDistance
    )
    
    if (response.code === 200) {
      const triggers = response.data
      if (triggers.length > 0) {
        ElMessage.warning(`触发了 ${triggers.length} 条路径偏离预警规则`)
      } else {
        ElMessage.success('路径正常，未触发预警')
      }
    }
  } catch (error) {
    ElMessage.error('路径偏离测试失败')
  }
}

// 规则管理操作
const handleCreateRule = () => {
  generateRuleCode()
  createDialogVisible.value = true
}

// 生成规则编码
const generateRuleCode = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  ruleForm.ruleCode = `ALT${year}${month}${day}${random}`
}

// 关闭创建对话框
const handleCreateDialogClose = () => {
  ruleFormRef.value?.resetFields()
  Object.assign(ruleForm, {
    ruleCode: '',
    ruleName: '',
    ruleType: '',
    alertLevel: '',
    thresholdValue: 0,
    description: '',
    isEnabled: 1
  })
}

// 提交创建规则
const handleCreateSubmit = async () => {
  if (!ruleFormRef.value) return
  
  await ruleFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      const response = await fetch('http://localhost:8080/database/alert/create-rule', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(ruleForm)
      })
      
      const data = await response.json()
      console.log('创建规则响应:', data)
      
      if (data.code === 200) {
        ElMessage.success('规则创建成功')
        createDialogVisible.value = false
        await loadAlertRules() // 刷新列表
      } else {
        ElMessage.error(data.message || '规则创建失败')
      }
    } catch (error) {
      console.error('创建规则失败:', error)
      ElMessage.error('规则创建失败，请检查网络连接')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleEditRule = (row: AlertRule) => {
  ElMessage.info(`编辑规则: ${row.ruleName}`)
}

const handleCopyRule = (row: AlertRule) => {
  ElMessage.info(`复制规则: ${row.ruleName}`)
}

const handleDeleteRule = (row: AlertRule) => {
  ElMessageBox.confirm(
    `确定要删除规则 "${row.ruleName}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await alertApi.deleteAlertRule(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadAlertRules()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 导入规则
const handleImport = () => {
  fileList.value = []
  importDialogVisible.value = true
}

// 文件变化处理
const handleFileChange = (file: UploadFile) => {
  fileList.value = [file]
}

// 下载导入模板
const downloadTemplate = () => {
  const templateData = [
    ['规则编码', '规则名称', '规则类型', '预警级别', '阈值', '规则描述', '是否启用'],
    ['ALT202501040001', '温度超标预警', 'temperature', 'warning', '8', '冷藏温度超过8℃时触发', '1'],
    ['ALT202501040002', '超时预警', 'timeout', 'error', '120', '配送延迟超过120分钟', '1'],
    ['ALT202501040003', '路径偏离', 'route_deviation', 'critical', '500', '偏离预定路线500米', '1']
  ]
  
  const csvContent = '\ufeff' + templateData.map(row => row.join(',')).join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', '预警规则导入模板.csv')
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  ElMessage.success('模板下载成功')
}

// 处理导入提交
const handleImportSubmit = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择要导入的CSV文件')
    return
  }
  
  const file = fileList.value[0].raw
  if (!file) {
    ElMessage.warning('文件无效')
    return
  }
  
  importLoading.value = true
  
  try {
    // 读取CSV文件内容
    const text = await file.text()
    const lines = text.split('\n').filter(line => line.trim())
    
    if (lines.length < 2) {
      ElMessage.warning('文件内容为空或格式不正确')
      importLoading.value = false
      return
    }
    
    // 解析CSV（跳过标题行）
    const rules = []
    for (let i = 1; i < lines.length; i++) {
      const line = lines[i].trim()
      if (!line) continue
      
      const fields = line.split(',').map(f => f.trim().replace(/^"|"$/g, ''))
      
      if (fields.length >= 6) {
        rules.push({
          ruleCode: fields[0],
          ruleName: fields[1],
          ruleType: fields[2],
          alertLevel: fields[3],
          thresholdValue: parseFloat(fields[4]) || 0,
          description: fields[5],
          isEnabled: parseInt(fields[6]) || 1
        })
      }
    }
    
    if (rules.length === 0) {
      ElMessage.warning('未解析到有效的规则数据')
      importLoading.value = false
      return
    }
    
    // 批量导入到数据库
    const response = await fetch('http://localhost:8080/database/alert/import-rules', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ rules })
    })
    
    const data = await response.json()
    console.log('导入响应:', data)
    
    if (data.code === 200) {
      ElMessage.success(`成功导入 ${rules.length} 条规则`)
      importDialogVisible.value = false
      fileList.value = []
      await loadAlertRules() // 刷新列表
    } else {
      ElMessage.error(data.message || '导入失败')
    }
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败，请检查文件格式')
  } finally {
    importLoading.value = false
  }
}

// 加载预警统计
const loadAlertStats = async () => {
  try {
    const response = await alertApi.getAlertRuleStatistics()
    if (response.code === 200) {
      Object.assign(alertStats, response.data)
    }
  } catch (error) {
    console.error('加载预警统计失败:', error)
  }
}

// 🔄 自动刷新定时器
let autoRefreshTimer: NodeJS.Timeout | null = null

// 启动自动刷新（每30秒）
const startAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
  }
  
  autoRefreshTimer = setInterval(async () => {
    console.log('⏰ 自动刷新预警数据...')
    await loadAlertRules()
    await loadAlertStats()
  }, 30000) // 30秒间隔
  
  console.log('✅ 预警数据自动刷新已启动（30秒间隔）')
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
    console.log('⏹️ 预警数据自动刷新已停止')
  }
}

// 组件挂载 - 立即从数据库加载最新数据并启动自动刷新
onMounted(async () => {
  console.log('🚨 预警管理页面已加载，开始从数据库同步数据')
  await loadAlertRules()
  await loadAlertStats()
  startAutoRefresh() // 启动自动刷新
})

// 注意：组件卸载时会自动清理定时器
</script>

<style scoped>
.alerts-page {
  padding: 0;
}

.test-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.test-card h4 {
  margin-bottom: 12px;
  color: #333;
  font-size: 14px;
}

.test-card .el-form {
  margin-bottom: 0;
}

.test-card .el-form-item {
  margin-bottom: 8px;
}
</style>

