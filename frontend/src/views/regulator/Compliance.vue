<template>
  <div class="compliance-page">
    <div class="page-header">
      <h1>🔍 合规检查</h1>
      <p>执行合规检查，确保冷链运输规范</p>
    </div>
    
    <!-- 检查统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-number">{{ complianceStats.totalChecks }}</div>
          <div class="stat-label">检查总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ complianceStats.complianceRate }}%</div>
          <div class="stat-label">合规率</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⚠️</div>
        <div class="stat-info">
          <div class="stat-number">{{ complianceStats.violationsFound }}</div>
          <div class="stat-label">发现违规</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <div class="stat-number">{{ complianceStats.avgScore }}</div>
          <div class="stat-label">平均评分</div>
        </div>
      </div>
    </div>
    
    <!-- 检查计划 -->
    <div class="check-plan">
      <div class="plan-header">
        <h3>📅 本周检查计划</h3>
        <el-button type="primary" @click="handleAddPlan">
          添加计划
        </el-button>
      </div>
      <div class="plan-timeline">
        <div v-for="plan in checkPlans" :key="plan.id" class="plan-item">
          <div class="plan-date">{{ plan.checkDate }}</div>
          <div class="plan-content">
            <div class="plan-title">{{ plan.title }}</div>
            <div class="plan-target">检查对象: {{ plan.targetName }}</div>
            <div class="plan-type">检查类型: {{ getCheckTypeName(plan.checkType) }}</div>
            <div class="plan-status">
              <el-tag :type="getPlanStatusType(plan.status)" size="small">
                {{ getPlanStatusText(plan.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 检查记录 -->
    <div class="table-container">
      <div class="table-header">
        <h3>📋 检查记录</h3>
        <div class="table-actions">
          <el-select v-model="checkTypeFilter" placeholder="检查类型" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="常规检查" value="routine" />
            <el-option label="抽查" value="spot" />
            <el-option label="跟进检查" value="follow_up" />
          </el-select>
          <el-button type="success" @click="refreshChecks">刷新</el-button>
        </div>
      </div>
      
      <el-table :data="checkRecords" style="width: 100%" stripe>
        <el-table-column prop="checkCode" label="检查编号" width="140" />
        <el-table-column prop="checkType" label="检查类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getCheckTypeName(row.checkType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkCategory" label="检查分类" width="100">
          <template #default="{ row }">
            <span>{{ getCheckCategoryName(row.checkCategory) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="检查对象" width="150" />
        <el-table-column prop="checkDate" label="检查日期" width="110" />
        <el-table-column prop="complianceScore" label="合规评分" width="90">
          <template #default="{ row }">
            <span :class="getScoreClass(row.complianceScore)">
              {{ row.complianceScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="violationsFound" label="违规项" width="80">
          <template #default="{ row }">
            <span v-if="row.violationsFound > 0" class="violation-count">
              {{ row.violationsFound }}项
            </span>
            <span v-else class="no-violation">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="checkStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getCheckStatusType(row.checkStatus)" size="small">
              {{ getCheckStatusText(row.checkStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleView(row)">
              查看
            </el-button>
            <el-button type="success" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" text @click="handleReport(row)">
              报告
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 查看检查详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="检查详情"
      width="800px"
    >
      <div v-if="currentCheck" class="check-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="检查编号">{{ currentCheck.checkCode }}</el-descriptions-item>
          <el-descriptions-item label="检查类型">
            <el-tag size="small">{{ getCheckTypeName(currentCheck.checkType) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="检查分类">{{ getCheckCategoryName(currentCheck.checkCategory) }}</el-descriptions-item>
          <el-descriptions-item label="检查对象">{{ currentCheck.targetName }}</el-descriptions-item>
          <el-descriptions-item label="检查日期">{{ currentCheck.checkDate }}</el-descriptions-item>
          <el-descriptions-item label="合规评分">
            <span :class="getScoreClass(currentCheck.complianceScore)">{{ currentCheck.complianceScore }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="违规项数">
            <span v-if="currentCheck.violationsFound > 0" class="violation-count">{{ currentCheck.violationsFound }}项</span>
            <span v-else class="no-violation">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="检查状态">
            <el-tag :type="getCheckStatusType(currentCheck.checkStatus)" size="small">
              {{ getCheckStatusText(currentCheck.checkStatus) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider />
        
        <h4>检查项目</h4>
        <p>{{ currentCheck.checkItems }}</p>
        
        <h4>检查结果</h4>
        <p>{{ currentCheck.checkResults }}</p>
        
        <div v-if="currentCheck.violationDetails">
          <h4>违规详情</h4>
          <p style="color: #ff4d4f;">{{ currentCheck.violationDetails }}</p>
        </div>
        
        <div v-if="currentCheck.correctiveActions">
          <h4>整改措施</h4>
          <p>{{ currentCheck.correctiveActions }}</p>
        </div>
      </div>
    </el-dialog>
    
    <!-- 编辑检查记录对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑检查记录"
      width="700px"
      @close="handleEditDialogClose"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="120px"
      >
        <el-form-item label="检查编号">
          <el-input v-model="editForm.checkCode" disabled />
        </el-form-item>
        
        <el-form-item label="检查结果" prop="checkResults">
          <el-input 
            v-model="editForm.checkResults" 
            type="textarea"
            :rows="3"
            placeholder="请输入检查结果"
          />
        </el-form-item>
        
        <el-form-item label="合规评分" prop="complianceScore">
          <el-input-number 
            v-model="editForm.complianceScore" 
            :min="0" 
            :max="100"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="违规项数" prop="violationsFound">
          <el-input-number 
            v-model="editForm.violationsFound" 
            :min="0" 
            :max="50"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="违规详情" v-if="editForm.violationsFound > 0">
          <el-input 
            v-model="editForm.violationDetails" 
            type="textarea"
            :rows="3"
            placeholder="请输入违规详情"
          />
        </el-form-item>
        
        <el-form-item label="整改措施" v-if="editForm.violationsFound > 0">
          <el-input 
            v-model="editForm.correctiveActions" 
            type="textarea"
            :rows="3"
            placeholder="请输入整改措施"
          />
        </el-form-item>
        
        <el-form-item label="检查状态" prop="checkStatus">
          <el-radio-group v-model="editForm.checkStatus">
            <el-radio label="planned">计划中</el-radio>
            <el-radio label="in_progress">执行中</el-radio>
            <el-radio label="completed">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 添加检查计划对话框 -->
    <el-dialog
      v-model="planDialogVisible"
      title="添加检查计划"
      width="600px"
      @close="handlePlanDialogClose"
    >
      <el-form
        ref="planFormRef"
        :model="planForm"
        :rules="planRules"
        label-width="120px"
      >
        <el-form-item label="检查编号" prop="checkCode">
          <el-input 
            v-model="planForm.checkCode" 
            placeholder="自动生成或手动输入"
          />
          <el-button 
            type="primary" 
            size="small" 
            style="margin-top: 5px"
            @click="generateCheckCode"
          >
            自动生成
          </el-button>
        </el-form-item>
        
        <el-form-item label="检查类型" prop="checkType">
          <el-select 
            v-model="planForm.checkType" 
            placeholder="请选择检查类型"
            style="width: 100%"
          >
            <el-option label="常规检查" value="routine" />
            <el-option label="抽查" value="spot" />
            <el-option label="投诉检查" value="complaint" />
            <el-option label="跟进检查" value="follow_up" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="检查分类" prop="checkCategory">
          <el-select 
            v-model="planForm.checkCategory" 
            placeholder="请选择检查分类"
            style="width: 100%"
          >
            <el-option label="车辆检查" value="vehicle" />
            <el-option label="司机检查" value="driver" />
            <el-option label="设施检查" value="facility" />
            <el-option label="流程检查" value="process" />
            <el-option label="文档检查" value="documentation" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="检查对象类型" prop="targetType">
          <el-select 
            v-model="planForm.targetType" 
            placeholder="请选择检查对象类型"
            style="width: 100%"
          >
            <el-option label="供应商" value="supplier" />
            <el-option label="物流公司" value="logistics_company" />
            <el-option label="车辆" value="vehicle" />
            <el-option label="司机" value="driver" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="检查对象" prop="companyName">
          <el-input 
            v-model="planForm.companyName" 
            placeholder="请输入被检查的公司或对象名称"
          />
        </el-form-item>
        
        <el-form-item label="检查日期" prop="checkDate">
          <el-date-picker
            v-model="planForm.checkDate"
            type="date"
            placeholder="选择检查日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="检查项目" prop="checkItems">
          <el-input 
            v-model="planForm.checkItems" 
            type="textarea"
            :rows="3"
            placeholder="请输入检查项目，如：车辆温控系统、GPS定位、消防设备"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="planForm.remarks" 
            type="textarea"
            :rows="2"
            placeholder="其他备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePlanSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

// 合规统计数据（将从真实数据库获取）
const complianceStats = reactive({
  totalChecks: 0,
  complianceRate: 0,
  violationsFound: 0,
  avgScore: 0
})

// 筛选条件
const checkTypeFilter = ref('')

// 检查计划（将从真实数据库获取）
const checkPlans = ref([])

// 检查记录（将从真实数据库获取）
const checkRecords = ref([])

// 对话框状态
const planDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const editDialogVisible = ref(false)
const submitLoading = ref(false)
const editLoading = ref(false)
const planFormRef = ref<FormInstance>()
const editFormRef = ref<FormInstance>()

// 当前查看的检查记录
const currentCheck = ref<any>(null)

// 计划表单数据
const planForm = reactive({
  checkCode: '',
  checkType: '',
  checkCategory: '',
  targetType: '',
  companyName: '',
  checkDate: '',
  checkItems: '',
  remarks: ''
})

// 编辑表单数据
const editForm = reactive({
  id: null,
  checkCode: '',
  checkResults: '',
  complianceScore: 0,
  violationsFound: 0,
  violationDetails: '',
  correctiveActions: '',
  checkStatus: 'planned'
})

// 表单验证规则
const planRules: FormRules = {
  checkCode: [
    { required: true, message: '请输入检查编号', trigger: 'blur' }
  ],
  checkType: [
    { required: true, message: '请选择检查类型', trigger: 'change' }
  ],
  checkCategory: [
    { required: true, message: '请选择检查分类', trigger: 'change' }
  ],
  targetType: [
    { required: true, message: '请选择检查对象类型', trigger: 'change' }
  ],
  companyName: [
    { required: true, message: '请输入检查对象', trigger: 'blur' }
  ],
  checkDate: [
    { required: true, message: '请选择检查日期', trigger: 'change' }
  ],
  checkItems: [
    { required: true, message: '请输入检查项目', trigger: 'blur' }
  ]
}

// 编辑表单验证规则
const editRules: FormRules = {
  checkResults: [
    { required: true, message: '请输入检查结果', trigger: 'blur' }
  ],
  complianceScore: [
    { required: true, message: '请输入合规评分', trigger: 'blur' }
  ]
}

// 获取检查类型名称
const getCheckTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    routine: '常规检查',
    spot: '抽查',
    complaint: '投诉检查',
    follow_up: '跟进检查'
  }
  return typeMap[type] || type
}

// 获取检查分类名称
const getCheckCategoryName = (category: string) => {
  const categoryMap: Record<string, string> = {
    vehicle: '车辆检查',
    driver: '司机检查',
    facility: '设施检查',
    process: '流程检查',
    documentation: '文档检查'
  }
  return categoryMap[category] || category
}

// 获取计划状态类型和文本
const getPlanStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    planned: 'warning',
    in_progress: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || ''
}

const getPlanStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    planned: '计划中',
    in_progress: '执行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

// 获取检查状态类型和文本
const getCheckStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    planned: '',
    in_progress: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || ''
}

const getCheckStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    planned: '计划中',
    in_progress: '执行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

// 获取评分样式类
const getScoreClass = (score: number) => {
  if (score >= 95) return 'score-excellent'
  if (score >= 85) return 'score-good'
  if (score >= 70) return 'score-normal'
  return 'score-poor'
}

// 操作函数
const handleAddPlan = () => {
  generateCheckCode()
  planDialogVisible.value = true
}

// 生成检查编号
const generateCheckCode = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 1000)).padStart(3, '0')
  planForm.checkCode = `CHK${year}${month}${day}${random}`
}

// 关闭对话框
const handlePlanDialogClose = () => {
  planFormRef.value?.resetFields()
  Object.assign(planForm, {
    checkCode: '',
    checkType: '',
    checkCategory: '',
    targetType: '',
    companyName: '',
    checkDate: '',
    checkItems: '',
    remarks: ''
  })
}

// 提交计划
const handlePlanSubmit = async () => {
  if (!planFormRef.value) return
  
  await planFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      const response = await fetch('http://localhost:8080/database/regulator/create-check-plan', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(planForm)
      })
      
      const data = await response.json()
      console.log('创建检查计划响应:', data)
      
      if (data.code === 200) {
        ElMessage.success('检查计划创建成功')
        planDialogVisible.value = false
        await refreshChecks() // 刷新列表
      } else {
        ElMessage.error(data.message || '检查计划创建失败')
      }
    } catch (error) {
      console.error('创建检查计划失败:', error)
      ElMessage.error('创建失败，请检查网络连接')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleView = async (row: any) => {
  try {
    // 获取检查详情
    const response = await fetch(`http://localhost:8080/database/regulator/check-detail/${row.id}`)
    const data = await response.json()
    
    if (data.code === 200) {
      currentCheck.value = {
        checkCode: data.data.check_code,
        checkType: data.data.check_type,
        checkCategory: data.data.check_category,
        targetName: data.data.company_name || row.targetName,
        checkDate: data.data.check_date,
        complianceScore: data.data.compliance_score,
        violationsFound: data.data.violations_found,
        checkStatus: data.data.check_status,
        checkItems: data.data.check_items,
        checkResults: data.data.check_results,
        violationDetails: data.data.violation_details,
        correctiveActions: data.data.corrective_actions
      }
      viewDialogVisible.value = true
    } else {
      ElMessage.error('获取检查详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

const handleEdit = async (row: any) => {
  try {
    // 获取检查详情用于编辑
    const response = await fetch(`http://localhost:8080/database/regulator/check-detail/${row.id}`)
    const data = await response.json()
    
    if (data.code === 200) {
      const check = data.data
      Object.assign(editForm, {
        id: check.id,
        checkCode: check.check_code,
        checkResults: check.check_results,
        complianceScore: check.compliance_score || 0,
        violationsFound: check.violations_found || 0,
        violationDetails: check.violation_details || '',
        correctiveActions: check.corrective_actions || '',
        checkStatus: check.check_status
      })
      editDialogVisible.value = true
    } else {
      ElMessage.error('获取检查详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 关闭编辑对话框
const handleEditDialogClose = () => {
  editFormRef.value?.resetFields()
  Object.assign(editForm, {
    id: null,
    checkCode: '',
    checkResults: '',
    complianceScore: 0,
    violationsFound: 0,
    violationDetails: '',
    correctiveActions: '',
    checkStatus: 'planned'
  })
}

// 提交编辑
const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    editLoading.value = true
    
    try {
      const response = await fetch(`http://localhost:8080/database/regulator/update-check/${editForm.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(editForm)
      })
      
      const data = await response.json()
      console.log('更新检查记录响应:', data)
      
      if (data.code === 200) {
        ElMessage.success('检查记录更新成功')
        editDialogVisible.value = false
        await refreshChecks() // 刷新列表
      } else {
        ElMessage.error(data.message || '更新失败')
      }
    } catch (error) {
      console.error('更新检查记录失败:', error)
      ElMessage.error('更新失败，请检查网络连接')
    } finally {
      editLoading.value = false
    }
  })
}

const handleReport = async (row: any) => {
  try {
    ElMessage.info(`正在生成检查报告: ${row.checkCode}`)
    
    // 调用后端生成检查报告
    const response = await fetch('http://localhost:8080/database/regulator/generate-check-report', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        checkId: row.id,
        checkCode: row.checkCode
      })
    })
    
    const data = await response.json()
    console.log('生成报告响应:', data)
    
    if (data.code === 200) {
      // 自动下载生成的报告
      const reportContent = data.data.reportContent
      
      // 转换为CSV格式
      const csvLines = []
      csvLines.push('\ufeff合规检查报告') // 添加BOM
      csvLines.push('')
      csvLines.push('检查编号,' + row.checkCode)
      csvLines.push('检查类型,' + getCheckTypeName(row.checkType))
      csvLines.push('检查分类,' + getCheckCategoryName(row.checkCategory))
      csvLines.push('检查对象,' + row.targetName)
      csvLines.push('检查日期,' + row.checkDate)
      csvLines.push('合规评分,' + row.complianceScore)
      csvLines.push('违规项数,' + row.violationsFound)
      csvLines.push('检查状态,' + getCheckStatusText(row.checkStatus))
      csvLines.push('')
      csvLines.push('报告内容')
      csvLines.push(reportContent || '检查记录已记录在案')
      
      const csvContent = csvLines.join('\n')
      
      // 创建下载链接
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      
      link.setAttribute('href', url)
      link.setAttribute('download', `检查报告_${row.checkCode}.csv`)
      link.style.visibility = 'hidden'
      
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      ElMessage.success(`✅ 检查报告生成成功: ${row.checkCode}`)
    } else {
      ElMessage.error(data.message || '生成报告失败')
    }
  } catch (error) {
    console.error('生成报告失败:', error)
    ElMessage.error('生成报告失败，请检查网络连接')
  }
}

const refreshChecks = async () => {
  try {
    console.log('🚀 开始刷新合规检查数据...')
    
    // 🔄 调用真实数据库API获取合规检查数据
    const [checksRes, statsRes, plansRes] = await Promise.all([
      fetch('http://localhost:8080/database/regulator/compliance-checks'),
      fetch('http://localhost:8080/database/regulator/compliance-statistics'),
      fetch('http://localhost:8080/database/regulator/check-plans')
    ])
    
    const checksData = await checksRes.json()
    const statsData = await statsRes.json()
    const plansData = await plansRes.json()
    
    console.log('📊 检查记录API响应:', checksData)
    console.log('📊 合规统计API响应:', statsData)
    console.log('📊 检查计划API响应:', plansData)
    
    if (checksData.code === 200) {
      // 🔄 更新检查记录数据
      const rawChecks = checksData.data || []
      checkRecords.value = rawChecks.map(check => ({
        id: check.id,
        checkCode: check.check_code || `CHK${check.id}`,
        checkType: check.check_type,
        checkCategory: check.check_category,
        targetName: check.target_name,
        checkDate: check.check_date ? new Date(check.check_date).toLocaleDateString() : '未知日期',
        complianceScore: check.compliance_score || 0,
        violationsFound: check.violations_found || 0,
        checkStatus: check.check_status
      }))
      
      console.log('🔍 真实检查记录:', checkRecords.value)
    }
    
    if (statsData.code === 200) {
      // 🔄 更新统计数据
      const stats = statsData.data
      complianceStats.totalChecks = stats.total_checks || 0
      complianceStats.complianceRate = stats.compliance_rate || 0
      complianceStats.violationsFound = stats.failed_checks || 0
      complianceStats.avgScore = stats.avg_score || 0
      
      console.log('📈 真实合规统计:', complianceStats)
    }
    
    if (plansData.code === 200) {
      // 🔄 更新检查计划数据
      const rawPlans = plansData.data || []
      checkPlans.value = rawPlans.map(plan => ({
        id: plan.id,
        checkDate: plan.scheduled_date ? new Date(plan.scheduled_date).toLocaleDateString() : '待定',
        title: plan.check_title || '例行检查',
        targetName: plan.target_name,
        checkType: plan.check_type,
        status: plan.check_status
      }))
      
      console.log('📅 真实检查计划:', checkPlans.value)
    }
    
    ElMessage.success('✅ 合规检查数据刷新成功（真实数据库）')
    console.log('🎉 合规检查数据刷新完成!')
    
  } catch (error) {
    console.error('💥 刷新检查数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

onMounted(() => {
  console.log('监管员合规检查页面已加载')
  refreshChecks()
})
</script>

<style scoped>
.compliance-page {
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

.check-plan {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.plan-header h3 {
  color: #333;
  font-size: 18px;
  margin: 0;
}

.plan-timeline {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.plan-date {
  min-width: 80px;
  font-weight: 600;
  color: #1890ff;
  font-size: 14px;
}

.plan-content {
  flex: 1;
  margin-left: 20px;
}

.plan-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.plan-target, .plan-type {
  font-size: 13px;
  color: #666;
  margin-bottom: 2px;
}

.plan-status {
  margin-top: 8px;
}

.table-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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
  align-items: center;
}

.score-excellent { color: #52c41a; font-weight: 600; }
.score-good { color: #1890ff; font-weight: 600; }
.score-normal { color: #faad14; font-weight: 600; }
.score-poor { color: #ff4d4f; font-weight: 600; }

.violation-count {
  color: #ff4d4f;
  font-weight: 600;
}

.no-violation {
  color: #52c41a;
  font-size: 12px;
}

.check-detail {
  padding: 10px;
}

.check-detail h4 {
  color: #333;
  margin: 16px 0 8px 0;
  font-size: 14px;
  font-weight: 600;
}

.check-detail p {
  color: #666;
  line-height: 1.6;
  margin: 0 0 12px 0;
}
</style>