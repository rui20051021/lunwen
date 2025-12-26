<template>
  <div class="supplier-orders-page">
    <div class="page-header">
      <h1>📋 订单管理</h1>
      <p>查看和处理采购商的订单</p>
    </div>
    
    <!-- 订单统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-number">{{ orderStats.totalOrders }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ orderStats.completedOrders }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🚛</div>
        <div class="stat-info">
          <div class="stat-number">{{ orderStats.inTransitOrders }}</div>
          <div class="stat-label">运输中</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⏰</div>
        <div class="stat-info">
          <div class="stat-number">{{ orderStats.pendingOrders }}</div>
          <div class="stat-label">待处理</div>
        </div>
      </div>
    </div>
    
    <!-- 订单列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>订单列表</h3>
        <div class="table-actions">
          <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 120px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="已创建" value="created" />
            <el-option label="运输中" value="in_transit" />
            <el-option label="已完成" value="completed" />
          </el-select>
          <el-button type="success" @click="refreshOrders">刷新</el-button>
        </div>
      </div>
      
      <el-table :data="filteredOrders" style="width: 100%" stripe>
        <el-table-column prop="orderCode" label="订单编号" width="140" />
        <el-table-column prop="purchaserName" label="采购商" width="120" />
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)" size="small">
              {{ getOrderStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requiredDeliveryTime" label="要求送达时间" width="160" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.orderStatus === 'created'"
              type="success" 
              size="small" 
              text 
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button 
              v-if="row.orderStatus === 'confirmed'"
              type="warning" 
              size="small" 
              text 
              @click="handlePrepare(row)"
            >
              备货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 订单统计数据
const orderStats = reactive({
  totalOrders: 5,
  completedOrders: 3,
  inTransitOrders: 2,
  pendingOrders: 1
})

// 筛选状态
const statusFilter = ref('')

// 订单列表数据（基于数据库数据）
// 订单列表数据 (将从真实数据库获取)
const orderList = ref([])

// 筛选后的订单列表
const filteredOrders = computed(() => {
  if (!statusFilter.value) {
    return orderList.value
  }
  return orderList.value.filter(order => order.orderStatus === statusFilter.value)
})

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    created: '',
    confirmed: 'warning',
    in_transit: 'warning',
    delivered: 'success',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || ''
}

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    created: '已创建',
    confirmed: '已确认',
    in_transit: '运输中',
    delivered: '已送达',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

// 操作函数
const handleView = (row: any) => {
  ElMessage.info(`查看订单详情: ${row.orderCode}`)
}

const handleConfirm = (row: any) => {
  ElMessage.success(`确认订单: ${row.orderCode}`)
}

const handlePrepare = (row: any) => {
  ElMessage.info(`备货准备: ${row.orderCode}`)
}

const handleFilter = () => {
  console.log('筛选状态:', statusFilter.value)
}

const refreshOrders = async () => {
  try {
    // 🔄 调用真实数据库API获取订单数据（直接访问后端）
    const response = await fetch('http://localhost:8080/database/order/all')
    const data = await response.json()
    
    if (data.code === 200) {
      // 更新订单列表数据
      const rawOrders = data.data || []
      const supplierOrders = rawOrders.map(order => ({
        id: order.id,
        orderCode: order.order_code,
        purchaserName: order.delivery_contact || '未知采购商',
        totalAmount: order.total_amount,
        orderStatus: order.order_status,
        requiredDeliveryTime: order.required_delivery_time ? new Date(order.required_delivery_time).toLocaleString() : '未设置',
        createdAt: order.created_at ? new Date(order.created_at).toLocaleString() : ''
      }))
      
      // 更新原有的orderList（如果需要）
      orderList.value.splice(0, orderList.value.length, ...supplierOrders)
      
      ElMessage.success(`✅ 成功获取 ${rawOrders.length} 个订单数据（真实数据库）`)
      console.log('真实订单数据:', rawOrders)
    } else {
      ElMessage.error(`获取订单数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('刷新订单数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

onMounted(() => {
  console.log('供应商订单管理页面已加载')
  refreshOrders()
})
</script>

<style scoped>
.supplier-orders-page {
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

.amount-text {
  font-weight: 600;
  color: #1890ff;
}
</style>