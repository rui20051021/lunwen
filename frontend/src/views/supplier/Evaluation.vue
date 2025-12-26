<template>
  <div class="evaluation-page">
    <div class="page-header">
      <h1>⭐ 供应商评价</h1>
      <p>评价供应商服务质量，促进合作关系优化</p>
    </div>
    
    <!-- 评价统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.totalEvaluations }}</div>
          <div class="stat-label">评价总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.avgRating }}/5</div>
          <div class="stat-label">平均评分</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🏆</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.excellentCount }}</div>
          <div class="stat-label">优秀评价</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.satisfactionRate }}%</div>
          <div class="stat-label">满意度</div>
        </div>
      </div>
    </div>
    
    <!-- 供应商列表和评价 -->
    <div class="table-container">
      <div class="table-header">
        <h3>供应商评价管理</h3>
        <div class="table-actions">
          <el-button type="primary" @click="handleCreateEvaluation">
            📝 新增评价
          </el-button>
          <el-button type="success" @click="refreshEvaluations">
            🔄 刷新数据
          </el-button>
          <el-button type="info" @click="exportEvaluations">
            📊 导出评价
          </el-button>
          <span style="margin-left: 10px; color: #666; font-size: 14px;">
            评价数量: {{ evaluationList?.length || 0 }}
          </span>
        </div>
      </div>
      
      <el-table :data="evaluationList" style="width: 100%" stripe>
        <el-table-column prop="supplierName" label="供应商名称" width="150" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="orderCode" label="关联订单" width="140" />
        <el-table-column prop="serviceRating" label="服务评分" width="120">
          <template #default="{ row }">
            <el-rate 
              v-model="row.serviceRating" 
              disabled 
              show-score 
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column prop="qualityRating" label="质量评分" width="120">
          <template #default="{ row }">
            <el-rate 
              v-model="row.qualityRating" 
              disabled 
              show-score 
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column prop="deliveryRating" label="配送评分" width="120">
          <template #default="{ row }">
            <el-rate 
              v-model="row.deliveryRating" 
              disabled 
              show-score 
              text-color="#ff9900"
              score-template="{value}"
            />
          </template>
        </el-table-column>
        <el-table-column prop="overallRating" label="综合评分" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="getRatingType(row.overallRating)" 
              size="large"
              style="font-weight: bold;"
            >
              {{ row.overallRating.toFixed(1) }}⭐
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evaluationDate" label="评价时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleView(row)">
              详情
            </el-button>
            <el-button type="warning" size="small" text @click="handleEdit(row)">
              修改
            </el-button>
            <el-button type="success" size="small" text @click="handleCommunicate(row)">
              沟通
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 评价详情和反馈 -->
    <div class="feedback-section">
      <h3>📝 评价反馈</h3>
      <div class="feedback-grid">
        <div v-for="feedback in recentFeedback" :key="feedback.id" class="feedback-card">
          <div class="feedback-header">
            <span class="supplier-name">{{ feedback.supplierName }}</span>
            <el-tag :type="getRatingType(feedback.rating)" size="small">
              {{ feedback.rating }}⭐
            </el-tag>
          </div>
          <div class="feedback-content">{{ feedback.content }}</div>
          <div class="feedback-time">{{ feedback.createdAt }}</div>
        </div>
      </div>
    </div>

    <!-- 新增评价对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="📝 新增供应商评价"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="evaluationFormRef"
        :model="evaluationForm"
        :rules="evaluationRules"
        label-width="120px"
      >
        <el-form-item label="选择供应商" prop="supplierId">
          <el-select v-model="evaluationForm.supplierId" placeholder="请选择供应商" style="width: 100%">
            <el-option 
              v-for="supplier in supplierOptions" 
              :key="supplier.id" 
              :label="supplier.supplier_name" 
              :value="supplier.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="关联订单" prop="orderCode">
          <el-input v-model="evaluationForm.orderCode" placeholder="请输入订单编号" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="服务评分">
              <el-rate v-model="evaluationForm.serviceRating" show-text />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="质量评分">
              <el-rate v-model="evaluationForm.qualityRating" show-text />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配送评分">
              <el-rate v-model="evaluationForm.deliveryRating" show-text />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="评价内容">
          <el-input
            v-model="evaluationForm.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您对供应商的评价..."
          />
        </el-form-item>
        
        <el-form-item label="改进建议">
          <el-input
            v-model="evaluationForm.suggestions"
            type="textarea"
            :rows="3"
            placeholder="请提出改进建议..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'

// 评价统计数据（将从真实数据库获取）
const evaluationStats = reactive({
  totalEvaluations: 0,
  avgRating: 0,
  excellentCount: 0,
  satisfactionRate: 0
})

// 评价列表数据（将从真实数据库获取）
const evaluationList = ref([])

// 最近反馈数据
const recentFeedback = ref([])

// 供应商选项
const supplierOptions = ref([])

// 对话框状态
const dialogVisible = ref(false)
const submitLoading = ref(false)

// 评价表单
const evaluationFormRef = ref<FormInstance>()
const evaluationForm = reactive({
  supplierId: null,
  orderCode: '',
  serviceRating: 5,
  qualityRating: 5,
  deliveryRating: 5,
  content: '',
  suggestions: ''
})

// 表单验证规则
const evaluationRules = {
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  orderCode: [
    { required: true, message: '请输入关联订单编号', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请填写评价内容', trigger: 'blur' }
  ]
}

// 获取评分类型
const getRatingType = (rating: number) => {
  if (rating >= 4.5) return 'success'
  if (rating >= 3.5) return 'warning'
  return 'danger'
}

// 操作函数
const handleCreateEvaluation = () => {
  dialogVisible.value = true
}

const handleView = (row: any) => {
  ElMessage.info(`查看评价详情: ${row.supplierName}`)
}

const handleEdit = (row: any) => {
  ElMessage.info(`修改评价: ${row.supplierName}`)
}

const handleCommunicate = (row: any) => {
  ElMessage.info(`与供应商沟通: ${row.supplierName}`)
}

const exportEvaluations = () => {
  if (!evaluationList.value || evaluationList.value.length === 0) {
    ElMessage.warning('没有评价数据可以导出')
    return
  }
  
  try {
    // 准备导出数据
    const exportData = evaluationList.value.map(item => ({
      '供应商名称': item.supplierName,
      '联系人': item.contactPerson,
      '关联订单': item.orderCode,
      '服务评分': item.serviceRating,
      '质量评分': item.qualityRating,
      '配送评分': item.deliveryRating,
      '综合评分': item.overallRating,
      '评价时间': item.evaluationDate,
      '评价内容': item.content || ''
    }))
    
    // 转换为CSV格式
    const headers = Object.keys(exportData[0])
    const csvContent = [
      '\ufeff' + headers.join(','), // 添加BOM以支持Excel打开中文
      ...exportData.map(row => 
        headers.map(header => {
          const value = row[header] || ''
          // 处理包含逗号和换行的字段
          if (typeof value === 'string' && (value.includes(',') || value.includes('\n'))) {
            return `"${value.replace(/"/g, '""')}"` // 双引号转义
          }
          return value
        }).join(',')
      )
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    link.setAttribute('href', url)
    link.setAttribute('download', `供应商评价数据_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`✅ 成功导出 ${evaluationList.value.length} 条评价数据`)
    
  } catch (error) {
    console.error('导出评价失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 刷新评价数据
const refreshEvaluations = async () => {
  try {
    console.log('🚀 开始刷新供应商评价数据...')
    
    // 🔄 并行调用多个API获取真实评价数据
    const [evaluationsRes, suppliersRes, statsRes] = await Promise.all([
      fetch(`http://localhost:8080/database/supplier/evaluations?_t=${Date.now()}`),
      fetch(`http://localhost:8080/database/supplier/all?_t=${Date.now()}`),
      fetch(`http://localhost:8080/database/supplier/evaluation-statistics?_t=${Date.now()}`)
    ])
    
    const evaluationsData = await evaluationsRes.json()
    const suppliersData = await suppliersRes.json()
    const statsData = await statsRes.json()
    
    console.log('📊 评价数据API响应:', evaluationsData)
    console.log('📊 供应商数据API响应:', suppliersData)
    console.log('📊 评价统计API响应:', statsData)
    
    // 🔄 更新供应商选项
    if (suppliersData.code === 200) {
      supplierOptions.value = suppliersData.data || []
      console.log('🏭 真实供应商数据:', supplierOptions.value.length)
    }
    
    // 🔄 更新评价列表
    if (evaluationsData.code === 200) {
      const rawEvaluations = evaluationsData.data || []
      evaluationList.value = rawEvaluations.map(evaluation => ({
        id: evaluation.id,
        supplierName: evaluation.supplier_name || '未知供应商',
        contactPerson: evaluation.contact_person || '未知',
        orderCode: evaluation.order_code || '无关联订单',
        serviceRating: evaluation.service_rating || 5,
        qualityRating: evaluation.quality_rating || 5,
        deliveryRating: evaluation.delivery_rating || 5,
        overallRating: evaluation.overall_rating || 5,
        content: evaluation.evaluation_content,
        suggestions: evaluation.suggestions,
        evaluationDate: evaluation.created_at ? new Date(evaluation.created_at).toLocaleString() : '未知'
      }))
      
      console.log('⭐ 真实评价数据:', evaluationList.value)
    }
    
    // 🔄 更新统计数据
    if (statsData.code === 200) {
      const stats = statsData.data
      evaluationStats.totalEvaluations = stats.total_evaluations || 0
      evaluationStats.avgRating = (stats.avg_rating || 0).toFixed(1)
      evaluationStats.excellentCount = stats.excellent_count || 0
      evaluationStats.satisfactionRate = Math.round(stats.satisfaction_rate || 0)
      
      console.log('📈 真实评价统计:', evaluationStats)
    }
    
    // 🔄 生成最近反馈（基于评价数据）
    if (evaluationList.value.length > 0) {
      recentFeedback.value = evaluationList.value
        .slice(0, 3)
        .map(item => ({
          id: item.id,
          supplierName: item.supplierName,
          rating: item.overallRating,
          content: item.content || '无评价内容',
          createdAt: item.evaluationDate
        }))
    }
    
    ElMessage.success(`✅ 成功刷新 ${evaluationList.value.length} 个评价数据（真实数据库）`)
    console.log('🎉 供应商评价数据刷新完成!')
    
  } catch (error) {
    console.error('💥 刷新评价数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

// 表单提交
const handleSubmit = async () => {
  if (!evaluationFormRef.value) return
  
  await evaluationFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      // 计算综合评分
      const overallRating = (
        evaluationForm.serviceRating + 
        evaluationForm.qualityRating + 
        evaluationForm.deliveryRating
      ) / 3
      
      const evaluationData = {
        ...evaluationForm,
        overallRating: Math.round(overallRating * 10) / 10
      }
      
      console.log('📝 提交评价数据:', evaluationData)
      
      const response = await fetch('http://localhost:8080/database/supplier/evaluations', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(evaluationData)
      })
      
      const result = await response.json()
      
      if (result.code === 200) {
        ElMessage.success('✅ 评价提交成功')
        dialogVisible.value = false
        resetForm()
        refreshEvaluations()
      } else {
        ElMessage.error(`评价提交失败: ${result.message}`)
      }
    } catch (error) {
      console.error('💥 评价提交失败:', error)
      ElMessage.error('❌ 评价提交失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(evaluationForm, {
    supplierId: null,
    orderCode: '',
    serviceRating: 5,
    qualityRating: 5,
    deliveryRating: 5,
    content: '',
    suggestions: ''
  })
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

onMounted(async () => {
  console.log('⭐ 供应商评价页面已加载')
  await refreshEvaluations()
  
  // 启动自动刷新（每60秒）
  setInterval(async () => {
    console.log('⏰ 自动刷新评价数据...')
    await refreshEvaluations()
  }, 60000)
})
</script>

<style scoped>
.evaluation-page {
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
  align-items: center;
}

.feedback-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.feedback-section h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.feedback-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.feedback-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.supplier-name {
  font-weight: 600;
  color: #333;
}

.feedback-content {
  color: #666;
  line-height: 1.5;
  margin-bottom: 8px;
  font-size: 14px;
}

.feedback-time {
  color: #999;
  font-size: 12px;
}
</style>
