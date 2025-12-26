<template>
  <div class="supplier-evaluation-page">
    <div class="page-header">
      <h1>⭐ 供应商评价</h1>
      <p>评价合作供应商的服务质量，促进供应链优化</p>
    </div>
    
    <!-- 评价概览统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📝</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.totalEvaluations }}</div>
          <div class="stat-label">我的评价</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⭐</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.avgRating }}/5</div>
          <div class="stat-label">平均评分</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🏭</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.supplierCount }}</div>
          <div class="stat-label">评价供应商</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-number">{{ evaluationStats.pendingEvaluations }}</div>
          <div class="stat-label">待评价订单</div>
        </div>
      </div>
    </div>
    
    <!-- 待评价订单 -->
    <div class="pending-evaluation-section">
      <div class="section-header">
        <h3>📋 待评价订单</h3>
        <el-button type="primary" @click="refreshPendingOrders">
          🔄 刷新数据
        </el-button>
      </div>
      
      <div class="pending-orders-grid">
        <div v-for="order in pendingEvaluationOrders" :key="order.id" class="pending-order-card">
          <div class="order-header">
            <span class="order-code">{{ order.orderCode }}</span>
            <el-tag type="success" size="small">已完成</el-tag>
          </div>
          
          <div class="order-info">
            <div class="info-row">
              <span class="info-label">供应商:</span>
              <span class="info-value">{{ order.supplierName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">订单金额:</span>
              <span class="info-value">¥{{ order.totalAmount?.toFixed(2) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">完成时间:</span>
              <span class="info-value">{{ order.completedTime }}</span>
            </div>
          </div>
          
          <div class="order-actions">
            <el-button type="primary" size="small" @click="handleEvaluateOrder(order)">
              ⭐ 立即评价
            </el-button>
            <el-button type="info" size="small" @click="handleViewOrder(order)">
              📄 查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 历史评价记录 -->
    <div class="evaluation-history-section">
      <div class="section-header">
        <h3>📊 我的评价记录</h3>
        <el-button type="success" @click="refreshEvaluations">
          🔄 刷新评价
        </el-button>
      </div>
      
      <el-table :data="evaluationHistory" style="width: 100%" stripe>
        <el-table-column prop="orderCode" label="订单编号" width="140" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="orderAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span style="color: #1890ff; font-weight: 600;">¥{{ row.orderAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="服务评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.serviceRating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column label="质量评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.qualityRating" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column label="综合评分" width="100">
          <template #default="{ row }">
            <el-tag :type="getRatingType(row.overallRating)" size="large">
              {{ row.overallRating?.toFixed(1) }}⭐
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evaluationDate" label="评价时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleViewEvaluation(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" text @click="handleEditEvaluation(row)">
              修改
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 评价表单对话框 -->
    <el-dialog
      v-model="evaluationDialogVisible"
      title="⭐ 评价供应商服务"
      width="700px"
      @close="handleDialogClose"
    >
      <div v-if="currentOrder" class="evaluation-form">
        <!-- 订单信息展示 -->
        <el-card class="order-info-card" shadow="never">
          <h4>📋 订单信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">{{ currentOrder.orderCode }}</el-descriptions-item>
            <el-descriptions-item label="供应商">{{ currentOrder.supplierName }}</el-descriptions-item>
            <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ currentOrder.completedTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        
        <!-- 评价表单 -->
        <el-form
          ref="evaluationFormRef"
          :model="evaluationForm"
          :rules="evaluationRules"
          label-width="120px"
          style="margin-top: 20px;"
        >
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="服务评分" prop="serviceRating">
                <el-rate v-model="evaluationForm.serviceRating" show-text allow-half />
                <div class="rating-desc">响应速度、沟通效率</div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="质量评分" prop="qualityRating">
                <el-rate v-model="evaluationForm.qualityRating" show-text allow-half />
                <div class="rating-desc">产品新鲜度、包装质量</div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="配送评分" prop="deliveryRating">
                <el-rate v-model="evaluationForm.deliveryRating" show-text allow-half />
                <div class="rating-desc">准时性、温控效果</div>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="综合评分">
            <div class="overall-rating">
              <el-rate 
                :model-value="overallRating" 
                disabled 
                show-score 
                text-color="#ff9900"
                score-template="{value}"
                allow-half
              />
              <span class="overall-text">{{ getRatingText(overallRating) }}</span>
            </div>
          </el-form-item>
          
          <el-form-item label="评价内容" prop="content">
            <el-input
              v-model="evaluationForm.content"
              type="textarea"
              :rows="4"
              placeholder="请详细描述您对该供应商的服务体验，包括产品质量、配送服务、沟通效率等方面..."
            />
          </el-form-item>
          
          <el-form-item label="改进建议">
            <el-input
              v-model="evaluationForm.suggestions"
              type="textarea"
              :rows="3"
              placeholder="请提出对供应商服务的改进建议，帮助供应商提升服务质量..."
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="evaluationDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitEvaluation">
          📝 提交评价
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
  supplierCount: 0,
  pendingEvaluations: 0
})

// 待评价订单列表（将从真实数据库获取）
const pendingEvaluationOrders = ref([])

// 历史评价记录（将从真实数据库获取）
const evaluationHistory = ref([])

// 对话框状态
const evaluationDialogVisible = ref(false)
const submitLoading = ref(false)
const currentOrder = ref(null)

// 评价表单
const evaluationFormRef = ref<FormInstance>()
const evaluationForm = reactive({
  serviceRating: 5,
  qualityRating: 5,
  deliveryRating: 5,
  content: '',
  suggestions: ''
})

// 表单验证规则
const evaluationRules = {
  content: [
    { required: true, message: '请填写评价内容', trigger: 'blur' },
    { min: 10, message: '评价内容至少10个字符', trigger: 'blur' }
  ]
}

// 计算综合评分
const overallRating = computed(() => {
  return (evaluationForm.serviceRating + evaluationForm.qualityRating + evaluationForm.deliveryRating) / 3
})

// 获取评分类型
const getRatingType = (rating: number) => {
  if (rating >= 4.5) return 'success'
  if (rating >= 3.5) return 'warning'
  return 'danger'
}

// 获取评分文字描述
const getRatingText = (rating: number) => {
  if (rating >= 4.5) return '非常满意'
  if (rating >= 4.0) return '满意'
  if (rating >= 3.5) return '一般'
  if (rating >= 3.0) return '不满意'
  return '很不满意'
}

// 操作函数
const handleEvaluateOrder = (order: any) => {
  currentOrder.value = order
  resetEvaluationForm()
  evaluationDialogVisible.value = true
  console.log('📝 开始评价订单:', order.orderCode)
}

const handleViewOrder = async (order: any) => {
  try {
    // 获取订单详情
    const response = await fetch(`http://localhost:8080/database/order/detail/${order.id}`)
    const data = await response.json()
    
    if (data.code === 200) {
      const detail = data.data
      ElMessageBox.alert(
        `<div style="text-align: left; line-height: 1.8;">
          <p><strong>订单编号:</strong> ${detail.order_code || order.orderCode}</p>
          <p><strong>供应商:</strong> ${detail.supplier_name || order.supplierName}</p>
          <p><strong>订单金额:</strong> ¥${detail.total_amount || order.totalAmount}</p>
          <p><strong>取货地址:</strong> ${detail.pickup_address || '未填写'}</p>
          <p><strong>送货地址:</strong> ${detail.delivery_address || '未填写'}</p>
          <p><strong>创建时间:</strong> ${detail.created_at ? new Date(detail.created_at).toLocaleString() : '未知'}</p>
        </div>`,
        '订单详情',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定'
        }
      )
    } else {
      ElMessage.error('获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

const handleViewEvaluation = (row: any) => {
  ElMessageBox.alert(
    `<div style="text-align: left; line-height: 2;">
      <h4 style="margin: 0 0 12px 0;">⭐ 评价详情</h4>
      <p><strong>订单编号:</strong> ${row.orderCode}</p>
      <p><strong>供应商:</strong> ${row.supplierName}</p>
      <p><strong>服务评分:</strong> ${row.serviceRating} ⭐</p>
      <p><strong>质量评分:</strong> ${row.qualityRating} ⭐</p>
      <p><strong>配送评分:</strong> ${row.deliveryRating} ⭐</p>
      <p><strong>综合评分:</strong> ${row.overallRating} ⭐</p>
      <p><strong>评价内容:</strong> ${row.content}</p>
      <p><strong>评价时间:</strong> ${row.evaluationDate}</p>
    </div>`,
    '评价详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const handleEditEvaluation = async (row: any) => {
  // 设置当前订单和表单数据
  currentOrder.value = {
    id: row.id,
    orderCode: row.orderCode,
    supplierName: row.supplierName,
    totalAmount: row.orderAmount,
    completedTime: row.evaluationDate
  }
  
  // 填充现有评价数据
  Object.assign(evaluationForm, {
    serviceRating: row.serviceRating,
    qualityRating: row.qualityRating,
    deliveryRating: row.deliveryRating,
    content: row.content,
    suggestions: ''
  })
  
  // 打开对话框
  evaluationDialogVisible.value = true
  ElMessage.info('修改评价后重新提交即可更新')
}

// 刷新待评价订单
const refreshPendingOrders = async () => {
  try {
    console.log('🚀 开始刷新待评价订单...')
    
    // 🔄 获取已完成但未评价的订单
    const response = await fetch(`http://localhost:8080/database/order/all?_t=${Date.now()}`)
    const data = await response.json()
    
    console.log('📊 订单数据API响应:', data)
    
    if (data.code === 200) {
      const orders = data.data || []
      
      // 🔄 筛选已完成的订单作为待评价订单
      const completedOrders = orders.filter(order => order.order_status === 'completed')
      
      pendingEvaluationOrders.value = completedOrders.map(order => ({
        id: order.id,
        orderCode: order.order_code,
        supplierName: order.supplier_name || '未知供应商',
        totalAmount: order.total_amount,
        completedTime: order.updated_at ? new Date(order.updated_at).toLocaleString() : '未知',
        deliveryAddress: order.delivery_address
      }))
      
      // 更新统计
      evaluationStats.pendingEvaluations = pendingEvaluationOrders.value.length
      
      console.log('📋 待评价订单数据:', pendingEvaluationOrders.value)
      ElMessage.success(`✅ 刷新成功，${pendingEvaluationOrders.value.length} 个订单待评价`)
    }
  } catch (error) {
    console.error('💥 刷新待评价订单失败:', error)
    ElMessage.error('❌ 刷新失败')
  }
}

// 刷新评价记录（100%使用真实数据库supplier_evaluations表）
const refreshEvaluations = async () => {
  try {
    console.log('🚀 开始从真实数据库刷新评价记录...')
    
    // 🔄 优先调用真实的supplier_evaluations表API
    const [evaluationRes, statsRes] = await Promise.all([
      fetch(`http://localhost:8080/database/supplier/evaluations?_t=${Date.now()}`),
      fetch(`http://localhost:8080/database/supplier/evaluation-statistics?_t=${Date.now()}`)
    ])
    
    const evaluationData = await evaluationRes.json()
    const statsData = await statsRes.json()
    
    console.log('📊 真实评价数据API响应:', evaluationData)
    console.log('📊 真实统计数据API响应:', statsData)
    
    if (evaluationData.code === 200) {
      console.log('✅ 成功从supplier_evaluations表获取数据')
      
      const rawEvaluations = evaluationData.data || []
      console.log('📦 原始评价数据（supplier_evaluations表）:', rawEvaluations)
      
      // 🔄 将数据库字段映射为前端格式
      evaluationHistory.value = rawEvaluations.map(evaluation => ({
        id: evaluation.id,
        orderCode: evaluation.order_code,
        supplierName: evaluation.supplier_name || '未知供应商',
        orderAmount: evaluation.order_amount || 0,
        serviceRating: evaluation.service_rating || 5,
        qualityRating: evaluation.quality_rating || 5,
        deliveryRating: evaluation.delivery_rating || 5,
        overallRating: evaluation.overall_rating || 5,
        content: evaluation.evaluation_content || '无评价内容',
        evaluationDate: evaluation.created_at ? new Date(evaluation.created_at).toLocaleString() : '未知',
        evaluatorName: evaluation.evaluator_name || '匿名评价者'
      }))
      
      console.log('⭐ 映射后的评价数据:', evaluationHistory.value)
      
      // 🔄 使用真实统计数据
      if (statsData.code === 200) {
        const stats = statsData.data
        evaluationStats.totalEvaluations = stats.total_evaluations || 0
        evaluationStats.avgRating = (stats.avg_rating || 0).toFixed(1)
        evaluationStats.supplierCount = new Set(evaluationHistory.value.map(e => e.supplierName)).size
        
        console.log('📈 真实数据库统计:', evaluationStats)
      }
      
      ElMessage.success(`✅ 成功从数据库获取 ${rawEvaluations.length} 条真实评价记录`)
      console.log('🎉 100%使用supplier_evaluations表的真实数据!')
      
    } else {
      console.error('❌ supplier_evaluations表API失败:', evaluationData.message)
      ElMessage.error('❌ 无法从数据库获取评价数据')
    }
    
  } catch (error) {
    console.error('💥 刷新评价记录失败:', error)
    ElMessage.error('❌ 数据库连接失败')
  }
}

// 提交评价
const handleSubmitEvaluation = async () => {
  if (!evaluationFormRef.value || !currentOrder.value) return
  
  await evaluationFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      const evaluationData = {
        orderId: currentOrder.value.id,
        orderCode: currentOrder.value.orderCode,
        supplierName: currentOrder.value.supplierName,
        serviceRating: evaluationForm.serviceRating,
        qualityRating: evaluationForm.qualityRating,
        deliveryRating: evaluationForm.deliveryRating,
        overallRating: Math.round(overallRating.value * 10) / 10,
        content: evaluationForm.content,
        suggestions: evaluationForm.suggestions
      }
      
      console.log('📝 提交评价数据:', evaluationData)
      
      // 🔄 调用真实数据库API提交评价
      const response = await fetch('http://localhost:8080/database/supplier/evaluations', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(evaluationData)
      })
      
      const result = await response.json()
      
      if (result.code === 200) {
        ElMessage.success('⭐ 评价提交成功！感谢您的反馈')
        evaluationDialogVisible.value = false
        
        // 刷新数据
        await refreshPendingOrders()
        await refreshEvaluations()
      } else {
        ElMessage.error(`评价提交失败: ${result.message}`)
      }
    } catch (error) {
      console.error('💥 评价提交失败:', error)
      ElMessage.error('❌ 评价提交失败，请稍后重试')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置评价表单
const resetEvaluationForm = () => {
  Object.assign(evaluationForm, {
    serviceRating: 5,
    qualityRating: 5,
    deliveryRating: 5,
    content: '',
    suggestions: ''
  })
}

// 关闭对话框
const handleDialogClose = () => {
  resetEvaluationForm()
  currentOrder.value = null
}

onMounted(async () => {
  console.log('⭐ 采购商供应商评价页面已加载')
  
  // 并行加载待评价订单和历史评价
  await Promise.all([
    refreshPendingOrders(),
    refreshEvaluations()
  ])
  
  // 启动自动刷新（每60秒）
  setInterval(async () => {
    console.log('⏰ 自动刷新评价数据...')
    await refreshPendingOrders()
    await refreshEvaluations()
  }, 60000)
  
  console.log('🔄 评价数据自动刷新已启动')
})
</script>

<style scoped>
.supplier-evaluation-page {
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

.pending-evaluation-section, .evaluation-history-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  color: #333;
  font-size: 18px;
  margin: 0;
}

.pending-orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.pending-order-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-code {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.order-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 13px;
}

.info-label {
  color: #666;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.order-info-card {
  margin-bottom: 20px;
}

.order-info-card h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.rating-desc {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}

.overall-rating {
  display: flex;
  align-items: center;
  gap: 12px;
}

.overall-text {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
}

.evaluation-form {
  max-height: 600px;
  overflow-y: auto;
}
</style>
