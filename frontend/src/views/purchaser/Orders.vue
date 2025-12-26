<template>
  <div class="purchaser-orders-page">
    <div class="page-header">
      <h1>🛒 采购订单</h1>
      <p>管理采购订单，跟踪配送状态</p>
    </div>
    
    <!-- 订单统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-number">{{ orderStats.totalOrders }}</div>
          <div class="stat-label">采购订单</div>
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
        <div class="stat-icon">⏰</div>
        <div class="stat-info">
          <div class="stat-number">{{ orderStats.pendingOrders }}</div>
          <div class="stat-label">待收货</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-number">¥{{ orderStats.totalAmount }}</div>
          <div class="stat-label">采购总额(万)</div>
        </div>
      </div>
    </div>
    
    <!-- 待收货订单 -->
    <div class="pending-orders">
      <h3>⏰ 待收货订单</h3>
      <div class="pending-grid">
        <div v-for="order in pendingOrders" :key="order.id" class="pending-card">
          <div class="pending-header">
            <span class="order-code">{{ order.orderCode }}</span>
            <el-tag :type="getOrderStatusType(order.orderStatus)" size="small">
              {{ getOrderStatusText(order.orderStatus) }}
            </el-tag>
          </div>
          
          <div class="pending-info">
            <div class="info-row">
              <span class="info-label">供应商:</span>
              <span class="info-value">{{ order.supplierName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">订单金额:</span>
              <span class="info-value amount">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">预计到达:</span>
              <span class="info-value">{{ order.estimatedTime }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">配送地址:</span>
              <span class="info-value">{{ order.deliveryAddress }}</span>
            </div>
          </div>
          
          <div class="pending-actions">
            <el-button type="primary" size="small" @click="handleReceive(order)">
              确认收货
            </el-button>
            <el-button type="success" size="small" @click="handleTrack(order)">
              跟踪配送
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 所有订单列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>📋 全部采购订单</h3>
        <div class="table-actions">
          <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 120px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="运输中" value="in_transit" />
            <el-option label="已完成" value="completed" />
            <el-option label="已创建" value="created" />
          </el-select>
          <el-button type="success" @click="refreshOrders">🔄 刷新数据</el-button>
          <el-button type="primary" @click="handleCreateOrder">新建订单</el-button>
        </div>
      </div>
      
      <el-table :data="filteredOrders" style="width: 100%" stripe>
        <el-table-column prop="orderCode" label="订单编号" width="140" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)" size="small">
              {{ getOrderStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requiredDeliveryTime" label="要求送达" width="160" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleView(row)">
              详情
            </el-button>
            <el-button 
              v-if="row.orderStatus === 'in_transit'"
              type="success" 
              size="small" 
              text 
              @click="handleReceive(row)"
            >
              收货
            </el-button>
            <el-button type="warning" size="small" text @click="handleEvaluate(row)">
              评价
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

// 订单统计数据（将从真实数据计算）
const orderStats = reactive({
  totalOrders: 0,
  completedOrders: 0,
  pendingOrders: 0,
  totalAmount: 0
})

// 筛选状态
const statusFilter = ref('')

// 待收货订单（将从真实数据库获取）
const pendingOrders = ref([])

// 全部订单列表（将从真实数据库获取）
const orderList = ref([])

// 筛选后的订单列表
const filteredOrders = computed(() => {
  if (!statusFilter.value) {
    return orderList.value
  }
  return orderList.value.filter(order => order.orderStatus === statusFilter.value)
})

// 获取订单状态类型和文本
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
const handleCreateOrder = () => {
  // 跳转到通用订单管理页面
  window.location.href = '/orders'
  ElMessage.info('跳转到订单管理页面')
}

const handleView = async (row: any) => {
  try {
    // 获取订单详情
    const response = await fetch(`http://localhost:8080/database/order/detail/${row.id}`)
    const data = await response.json()
    
    if (data.code === 200) {
      const detail = data.data
      const detailInfo = `
订单编号: ${detail.order_code || row.orderCode}
供应商: ${detail.supplier_name || row.supplierName}
订单金额: ¥${detail.total_amount || row.totalAmount}
订单状态: ${getOrderStatusText(detail.order_status || row.orderStatus)}
取货地址: ${detail.pickup_address || '未填写'}
送货地址: ${detail.delivery_address || '未填写'}
创建时间: ${detail.created_at ? new Date(detail.created_at).toLocaleString() : row.createdAt}
      `
      ElMessage.info(detailInfo)
      console.log('订单详情:', detail)
    } else {
      ElMessage.error('获取订单详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

const handleReceive = async (row: any) => {
  try {
    const response = await fetch(`http://localhost:8080/database/order/confirm-receive/${row.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        receivedTime: new Date().toISOString()
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      ElMessage.success(`✅ 订单 ${row.orderCode} 收货成功`)
      await refreshOrders()
    } else {
      ElMessage.error(data.message || '确认收货失败')
    }
  } catch (error) {
    console.error('确认收货失败:', error)
    ElMessage.error('操作失败，请检查网络连接')
  }
}

const handleTrack = (row: any) => {
  // 跳转到运输跟踪页面
  window.location.href = '/logistics/tracking'
  ElMessage.info(`跟踪订单配送: ${row.orderCode}`)
}

const handleEvaluate = (row: any) => {
  // 跳转到供应商评价页面
  window.location.href = '/purchaser/evaluation'
  ElMessage.info(`评价订单: ${row.orderCode}`)
}

const handleFilter = () => {
  console.log('筛选状态:', statusFilter.value)
}

const refreshOrders = async () => {
  try {
    console.log('🚀 开始刷新采购商订单数据...')
    
    // 🔄 调用真实数据库API获取订单数据（直接访问后端）
    const response = await fetch('http://localhost:8080/database/order/all')
    console.log('📡 API响应状态:', response.status, response.statusText)
    
    const data = await response.json()
    console.log('📊 订单API响应:', data)
    
    if (data.code === 200) {
      console.log('✅ API调用成功，开始处理数据...')
      
      const rawOrders = data.data || []
      console.log('📦 原始订单数据:', rawOrders)
      console.log('📝 原始数据数量:', rawOrders.length)
      
      // 🔄 转换数据格式以匹配采购商页面期望
      const mappedOrders = rawOrders.map(order => ({
        id: order.id,
        orderCode: order.order_code,
        supplierName: order.supplier_name || '未知供应商',
        totalAmount: order.total_amount,
        orderStatus: order.order_status,
        requiredDeliveryTime: order.required_delivery_time ? new Date(order.required_delivery_time).toLocaleString() : '未设置',
        createdAt: order.created_at ? new Date(order.created_at).toLocaleString() : '',
        deliveryAddress: order.delivery_address,
        pickupAddress: order.pickup_address
      }))
      
      console.log('🔄 映射后的订单数据:', mappedOrders)
      
      // 🔧 强制更新orderList
      orderList.value = []
      await new Promise(resolve => setTimeout(resolve, 10))
      orderList.value = [...mappedOrders]
      
      console.log('📋 更新后的orderList.value:', orderList.value)
      console.log('📊 orderList长度:', orderList.value.length)
      
      // 更新统计数据
      const orders = orderList.value
      orderStats.totalOrders = orders.length
      orderStats.completedOrders = orders.filter(o => o.orderStatus === 'completed').length
      orderStats.pendingOrders = orders.filter(o => o.orderStatus === 'in_transit' || o.orderStatus === 'created').length
      orderStats.totalAmount = (orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0) / 10000).toFixed(1) // 转为万元
      
      // 🔄 同时更新待收货订单数据
      pendingOrders.value = orders.filter(o => o.orderStatus === 'in_transit').map(order => ({
        id: order.id,
        orderCode: order.orderCode,
        supplierName: order.supplierName,
        totalAmount: order.totalAmount,
        orderStatus: order.orderStatus,
        estimatedTime: order.requiredDeliveryTime,
        deliveryAddress: order.deliveryAddress
      }))
      
      console.log('📈 更新后的统计数据:', orderStats)
      console.log('📦 更新后的待收货订单:', pendingOrders.value)
      
      ElMessage.success(`✅ 成功刷新 ${mappedOrders.length} 个订单数据，${pendingOrders.value.length} 个待收货（真实数据库）`)
      console.log('🎉 订单数据刷新完成!')
    } else {
      console.error('❌ API返回错误:', data)
      ElMessage.error(`获取订单数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 刷新订单数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

onMounted(() => {
  console.log('采购商订单管理页面已加载')
  refreshOrders()
})
</script>

<style scoped>
.purchaser-orders-page {
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

.pending-orders {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.pending-orders h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.pending-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.pending-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.pending-header {
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

.pending-info {
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

.info-value.amount {
  color: #1890ff;
  font-weight: 600;
}

.pending-actions {
  display: flex;
  gap: 8px;
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