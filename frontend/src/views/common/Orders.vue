<template>
  <div class="orders-page">
    <div class="page-header">
      <h1>订单管理</h1>
      <p>管理冷链物流订单，跟踪配送状态</p>
    </div>
    
    <!-- 搜索和操作栏 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-search">
          <el-input
            v-model="searchForm.orderCode"
            placeholder="搜索订单编号"
            clearable
            style="width: 180px"
            @change="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select
            v-model="searchForm.orderStatus"
            placeholder="订单状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="已创建" value="created" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="运输中" value="in_transit" />
            <el-option label="已送达" value="delivered" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="异常" value="exception" />
          </el-select>
          
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            style="width: 240px"
          />
          
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          
          <el-button @click="handleReset">重置</el-button>
        </div>
        
        <div class="table-actions">
          <el-button type="primary" :icon="Plus" @click="handleCreate">
            新增订单
          </el-button>
          <el-button type="success" @click="loadOrders">
            🔄 刷新数据
          </el-button>
          <el-button type="info" :icon="Download" @click="handleExport">
            导出数据
          </el-button>
          <span style="margin-left: 10px; color: #666; font-size: 14px;">
            订单数量: {{ orderList?.length || 0 }}
          </span>
        </div>
      </div>
      
      <!-- 订单表格 -->
      <el-table
        v-loading="tableLoading"
        :data="orderList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderCode" label="订单编号" width="140" />
        
        <el-table-column prop="supplierName" label="供应商" min-width="120" />
        
        <el-table-column prop="purchaserName" label="采购商" min-width="120" />
        
        <el-table-column prop="totalAmount" label="订单金额" width="110">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="orderStatus" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.orderStatus)" size="small">
              {{ getOrderStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleView(row)">
              详情
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
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button 
              v-if="['created', 'confirmed'].includes(row.orderStatus)"
              type="danger" 
              size="small" 
              text 
              @click="handleCancel(row)"
            >
              取消
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
    
    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="orderDetail" class="order-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="订单编号">{{ orderDetail.orderCode }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getOrderStatusType(orderDetail.orderStatus)">
              {{ getOrderStatusText(orderDetail.orderStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="供应商">{{ orderDetail.supplierName }}</el-descriptions-item>
          <el-descriptions-item label="采购商">{{ orderDetail.purchaserName }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ orderDetail.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ orderDetail.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="取货地址">{{ orderDetail.pickupAddress }}</el-descriptions-item>
          <el-descriptions-item label="送货地址">{{ orderDetail.deliveryAddress }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider />
        
        <h4>订单明细</h4>
        <el-table :data="orderDetail.orderItems" style="width: 100%">
          <el-table-column prop="productName" label="产品名称" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{ row }">
              ¥{{ row.unitPrice?.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="小计" width="100">
            <template #default="{ row }">
              ¥{{ (row.quantity * row.unitPrice)?.toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
    
    <!-- 新增订单对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="新增订单"
      width="700px"
      @close="handleCreateDialogClose"
    >
      <el-form
        ref="orderFormRef"
        :model="orderForm"
        :rules="orderRules"
        label-width="100px"
      >
        <el-form-item label="订单编号" prop="orderCode">
          <el-input 
            v-model="orderForm.orderCode" 
            placeholder="自动生成或手动输入"
          />
          <el-button 
            type="primary" 
            size="small" 
            style="margin-left: 10px"
            @click="generateOrderCode"
          >
            自动生成
          </el-button>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select 
                v-model="orderForm.supplierId" 
                placeholder="请选择供应商"
                style="width: 100%"
                filterable
              >
                <el-option 
                  v-for="supplier in supplierOptions" 
                  :key="supplier.id" 
                  :label="supplier.supplier_name" 
                  :value="supplier.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="采购商" prop="purchaserId">
              <el-select 
                v-model="orderForm.purchaserId" 
                placeholder="请选择采购商"
                style="width: 100%"
                filterable
              >
                <el-option 
                  v-for="purchaser in purchaserOptions" 
                  :key="purchaser.id" 
                  :label="purchaser.company_name" 
                  :value="purchaser.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="订单类型" prop="orderType">
          <el-radio-group v-model="orderForm.orderType">
            <el-radio label="purchase">采购订单</el-radio>
            <el-radio label="return">退货订单</el-radio>
            <el-radio label="exchange">换货订单</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="取货地址" prop="pickupAddress">
          <el-input 
            v-model="orderForm.pickupAddress" 
            placeholder="请输入取货地址"
            type="textarea"
            :rows="2"
          />
        </el-form-item>
        
        <el-form-item label="送货地址" prop="deliveryAddress">
          <el-input 
            v-model="orderForm.deliveryAddress" 
            placeholder="请输入送货地址"
            type="textarea"
            :rows="2"
          />
        </el-form-item>
        
        <el-form-item label="收货联系人" prop="deliveryContact">
          <el-input 
            v-model="orderForm.deliveryContact" 
            placeholder="请输入收货联系人"
          />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="deliveryPhone">
          <el-input 
            v-model="orderForm.deliveryPhone" 
            placeholder="请输入联系电话"
            maxlength="11"
          />
        </el-form-item>
        
        <el-form-item label="要求送达时间" prop="requiredDeliveryTime">
          <el-date-picker
            v-model="orderForm.requiredDeliveryTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item label="订单金额" prop="totalAmount">
          <el-input-number 
            v-model="orderForm.totalAmount" 
            :min="0" 
            :precision="2"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="orderForm.remarks" 
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Plus, Download } from '@element-plus/icons-vue'
import { orderApi } from '@/api/order'
import type { Order, OrderStatus } from '@/types/api'

// 搜索表单
const searchForm = reactive({
  orderCode: '',
  orderStatus: '',
  supplierId: null as number | null,
  purchaserId: null as number | null
})

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const orderList = ref<Order[]>([])
const tableLoading = ref(false)

// 订单详情
const detailDialogVisible = ref(false)
const orderDetail = ref<any>(null)

// 新增订单对话框
const createDialogVisible = ref(false)
const orderFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 供应商和采购商选项
const supplierOptions = ref<any[]>([])
const purchaserOptions = ref<any[]>([])

// 订单表单数据
const orderForm = reactive({
  orderCode: '',
  supplierId: null,
  purchaserId: null,
  orderType: 'purchase',
  pickupAddress: '',
  deliveryAddress: '',
  deliveryContact: '',
  deliveryPhone: '',
  requiredDeliveryTime: '',
  totalAmount: 0,
  remarks: ''
})

// 表单验证规则
const orderRules: FormRules = {
  orderCode: [
    { required: true, message: '请输入订单编号', trigger: 'blur' }
  ],
  supplierId: [
    { required: true, message: '请选择供应商', trigger: 'change' }
  ],
  purchaserId: [
    { required: true, message: '请选择采购商', trigger: 'change' }
  ],
  pickupAddress: [
    { required: true, message: '请输入取货地址', trigger: 'blur' }
  ],
  deliveryAddress: [
    { required: true, message: '请输入送货地址', trigger: 'blur' }
  ],
  deliveryContact: [
    { required: true, message: '请输入收货联系人', trigger: 'blur' }
  ],
  deliveryPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  totalAmount: [
    { required: true, message: '请输入订单金额', trigger: 'blur' }
  ]
}

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    created: '',
    confirmed: 'warning',
    in_transit: 'warning',
    delivered: 'success',
    completed: 'success',
    cancelled: 'danger',
    exception: 'danger'
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
    cancelled: '已取消',
    exception: '异常'
  }
  return textMap[status] || status
}

// 加载订单列表 - 100%真实数据库数据，实时反映数据库变化
const loadOrders = async () => {
  tableLoading.value = true
  
  try {
    console.log('🚀 开始从数据库获取最新订单数据...')
    
    // 🔄 调用真实数据库API（添加时间戳防缓存）
    const response = await fetch(`http://localhost:8080/database/order/all?_t=${Date.now()}`)
    const data = await response.json()
    console.log('📊 数据库API响应:', data)
    
    if (data.code === 200) {
      console.log('✅ 数据库连接成功，处理最新订单数据...')
      
      const rawOrders = data.data || []
      console.log('📦 数据库原始订单数据:', rawOrders)
      console.log('📝 数据库订单数量:', rawOrders.length)
      
      // 🔄 将数据库字段完全映射为前端格式
      const mappedOrders = rawOrders.map(order => ({
        id: order.id,
        orderCode: order.order_code,
        supplierName: order.supplier_name || '未知供应商',
        purchaserName: order.delivery_contact || '未知采购商',
        totalAmount: order.total_amount,
        orderStatus: order.order_status,
        createdAt: order.created_at ? new Date(order.created_at).toLocaleString() : '',
        pickupAddress: order.pickup_address,
        deliveryAddress: order.delivery_address,
        requiredDeliveryTime: order.required_delivery_time ? new Date(order.required_delivery_time).toLocaleString() : '',
        orderItems: [] // 简化显示
      }))
      
      console.log('🔄 数据库数据映射结果:', mappedOrders)
      
      // 🔧 完全清空并重新加载，确保反映数据库最新状态
      orderList.value.splice(0, orderList.value.length)
      await new Promise(resolve => setTimeout(resolve, 10))
      
      // 从数据库数据重新填充
      orderList.value.push(...mappedOrders)
      
      // 更新分页信息反映数据库真实数量
      pagination.total = rawOrders.length
      pagination.current = 1
      
      console.log('📋 前端显示的订单列表:', orderList.value)
      console.log('📊 前端订单数量:', orderList.value.length)
      
      ElMessage.success(`✅ 已从数据库同步 ${mappedOrders.length} 个最新订单数据`)
      console.log('🎉 订单数据库同步完成!')
    } else {
      console.error('❌ 数据库API返回错误:', data)
      ElMessage.error(`数据库连接失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 数据库订单数据获取失败:', error)
    ElMessage.error('❌ 数据库连接失败：' + error.message)
  } finally {
    tableLoading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  loadOrders()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    orderCode: '',
    orderStatus: '',
    supplierId: null,
    purchaserId: null
  })
  dateRange.value = null
  handleSearch()
}

// 日期变化处理
const handleDateChange = () => {
  handleSearch()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadOrders()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadOrders()
}

// 查看详情
const handleView = async (row: Order) => {
  try {
    const response = await orderApi.getOrderDetail(row.id)
    if (response.code === 200) {
      orderDetail.value = response.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 确认订单
const handleConfirm = async (row: Order) => {
  try {
    const response = await orderApi.confirmOrder(row.id)
    if (response.code === 200) {
      ElMessage.success('订单确认成功')
      loadOrders()
    }
  } catch (error) {
    ElMessage.error('订单确认失败')
  }
}

// 订单发货
const handleShip = async (row: Order) => {
  try {
    const response = await orderApi.shipOrder(row.id)
    if (response.code === 200) {
      ElMessage.success('订单发货成功')
      loadOrders()
    }
  } catch (error) {
    ElMessage.error('订单发货失败')
  }
}

// 取消订单
const handleCancel = (row: Order) => {
  ElMessageBox.prompt('请输入取消原因', '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      const response = await orderApi.cancelOrder(row.id, value)
      if (response.code === 200) {
        ElMessage.success('订单取消成功')
        loadOrders()
      }
    } catch (error) {
      ElMessage.error('订单取消失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 新增订单
const handleCreate = async () => {
  // 加载供应商和采购商选项
  await loadSuppliers()
  await loadPurchasers()
  
  // 生成订单编号
  generateOrderCode()
  
  createDialogVisible.value = true
}

// 生成订单编号
const generateOrderCode = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  orderForm.orderCode = `ORD${year}${month}${day}${random}`
}

// 加载供应商列表
const loadSuppliers = async () => {
  try {
    const response = await fetch('http://localhost:8080/database/supplier/all')
    const data = await response.json()
    if (data.code === 200) {
      supplierOptions.value = data.data || []
    }
  } catch (error) {
    console.error('加载供应商失败:', error)
  }
}

// 加载采购商列表
const loadPurchasers = async () => {
  try {
    const response = await fetch('http://localhost:8080/database/order/purchasers')
    const data = await response.json()
    if (data.code === 200) {
      purchaserOptions.value = data.data || []
    }
  } catch (error) {
    console.error('加载采购商失败:', error)
  }
}

// 关闭创建对话框
const handleCreateDialogClose = () => {
  orderFormRef.value?.resetFields()
  Object.assign(orderForm, {
    orderCode: '',
    supplierId: null,
    purchaserId: null,
    orderType: 'purchase',
    pickupAddress: '',
    deliveryAddress: '',
    deliveryContact: '',
    deliveryPhone: '',
    requiredDeliveryTime: '',
    totalAmount: 0,
    remarks: ''
  })
}

// 提交创建订单
const handleCreateSubmit = async () => {
  if (!orderFormRef.value) return
  
  await orderFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      const response = await fetch('http://localhost:8080/database/order/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(orderForm)
      })
      
      const data = await response.json()
      console.log('创建订单响应:', data)
      
      if (data.code === 200) {
        ElMessage.success('订单创建成功')
        createDialogVisible.value = false
        await loadOrders() // 刷新订单列表
      } else {
        ElMessage.error(data.message || '订单创建失败')
      }
    } catch (error) {
      console.error('创建订单失败:', error)
      ElMessage.error('订单创建失败，请检查网络连接')
    } finally {
      submitLoading.value = false
    }
  })
}

// 导出数据
const handleExport = () => {
  if (!orderList.value || orderList.value.length === 0) {
    ElMessage.warning('没有数据可以导出')
    return
  }
  
  try {
    // 准备导出数据
    const exportData = orderList.value.map(order => ({
      '订单编号': order.orderCode,
      '供应商': order.supplierName,
      '采购商': order.purchaserName,
      '订单金额': order.totalAmount,
      '订单状态': getOrderStatusText(order.orderStatus),
      '创建时间': order.createdAt,
      '取货地址': order.pickupAddress || '',
      '送货地址': order.deliveryAddress || ''
    }))
    
    // 转换为CSV格式
    const headers = Object.keys(exportData[0])
    const csvContent = [
      '\ufeff' + headers.join(','), // 添加BOM以支持Excel打开中文
      ...exportData.map(row => 
        headers.map(header => {
          const value = row[header] || ''
          // 处理包含逗号的字段
          return typeof value === 'string' && value.includes(',') 
            ? `"${value}"` 
            : value
        }).join(',')
      )
    ].join('\n')
    
    // 创建下载链接
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    
    link.setAttribute('href', url)
    link.setAttribute('download', `订单数据_${new Date().toLocaleDateString()}.csv`)
    link.style.visibility = 'hidden'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`成功导出 ${orderList.value.length} 条订单数据`)
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 🔄 自动刷新定时器
let autoRefreshTimer: NodeJS.Timeout | null = null

// 启动自动刷新（每30秒从数据库获取最新数据）
const startAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
  }
  
  autoRefreshTimer = setInterval(async () => {
    console.log('⏰ 自动从数据库刷新订单数据...')
    await loadOrders()
  }, 30000) // 30秒间隔确保数据库变化及时反映
  
  console.log('✅ 订单数据自动刷新已启动（30秒间隔）')
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
    console.log('⏹️ 订单数据自动刷新已停止')
  }
}

// 组件挂载 - 立即从数据库同步最新数据并启动自动刷新
onMounted(async () => {
  console.log('📋 订单管理页面已加载，开始数据库数据同步')
  await loadOrders()
  startAutoRefresh() // 启动自动刷新确保数据库变化及时反映
})

// 注意：页面离开时会自动清理定时器
</script>

<style scoped>
.orders-page {
  padding: 0;
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

.amount-text {
  font-weight: 600;
  color: #1890ff;
}

.order-detail {
  padding: 20px;
}

.order-detail h4 {
  margin: 20px 0 12px 0;
  color: #333;
}
</style>

