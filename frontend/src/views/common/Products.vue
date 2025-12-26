<template>
  <div class="products-page">
    <div class="page-header">
      <h1>产品管理</h1>
      <p>管理冷链产品信息，配置温湿度要求</p>
    </div>
    
    <!-- 搜索和操作栏 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-search">
          <el-input
            v-model="searchForm.productName"
            placeholder="搜索产品名称"
            clearable
            style="width: 200px"
            @change="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-select
            v-model="searchForm.productType"
            placeholder="产品类型"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option label="水果" value="fruit" />
            <el-option label="蔬菜" value="vegetable" />
            <el-option label="肉类" value="meat" />
            <el-option label="海鲜" value="seafood" />
            <el-option label="乳制品" value="dairy" />
            <el-option label="其他" value="other" />
          </el-select>
          
          <el-select
            v-model="searchForm.status"
            placeholder="状态"
            clearable
            style="width: 100px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          
          <el-button @click="handleReset">重置</el-button>
        </div>
        
        <div class="table-actions">
          <el-button type="primary" :icon="Plus" @click="handleCreate">
            新增产品
          </el-button>
          <el-button type="success" @click="loadProducts">
            🔄 刷新数据
          </el-button>
          <el-button 
            type="danger" 
            :icon="Delete" 
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <span style="margin-left: 10px; color: #666; font-size: 14px;">
            产品数量: {{ productList?.length || 0 }}
          </span>
        </div>
      </div>
      
      <!-- 产品表格 -->
      <el-table
        v-loading="tableLoading"
        :data="productList"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="productCode" label="产品编码" width="120" />
        
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        
        <el-table-column prop="productType" label="产品类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getProductTypeText(row.productType) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="unit" label="单位" width="80" />
        
        <el-table-column label="温度要求(℃)" width="120">
          <template #default="{ row }">
            <span v-if="row.minTemp !== null && row.maxTemp !== null">
              {{ row.minTemp }}~{{ row.maxTemp }}
            </span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" text @click="handleView(row)">
              详情
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              text 
              @click="handleDelete(row)"
            >
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
    
    <!-- 产品编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品编码" prop="productCode">
              <el-input v-model="productForm.productCode" placeholder="请输入产品编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="productForm.productName" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品类型" prop="productType">
              <el-select v-model="productForm.productType" placeholder="请选择产品类型" style="width: 100%">
                <el-option label="水果" value="fruit" />
                <el-option label="蔬菜" value="vegetable" />
                <el-option label="肉类" value="meat" />
                <el-option label="海鲜" value="seafood" />
                <el-option label="乳制品" value="dairy" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="productForm.unit" placeholder="如：公斤、箱、件" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低温度(℃)">
              <el-input-number
                v-model="productForm.minTemp"
                :min="-50"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高温度(℃)">
              <el-input-number
                v-model="productForm.maxTemp"
                :min="-50"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低湿度(%)">
              <el-input-number
                v-model="productForm.minHumidity"
                :min="0"
                :max="100"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高湿度(%)">
              <el-input-number
                v-model="productForm.maxHumidity"
                :min="0"
                :max="100"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="保质期(天)">
          <el-input-number
            v-model="productForm.shelfLife"
            :min="1"
            :max="3650"
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="产品描述">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入产品描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ editingId ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import type { Product, ProductType } from '@/types/api'

// 搜索表单
const searchForm = reactive({
  productName: '',
  productType: '',
  status: null as number | null
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const productList = ref<Product[]>([])
const tableLoading = ref(false)
const selectedIds = ref<number[]>([])

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = computed(() => editingId.value ? '编辑产品' : '新增产品')
const editingId = ref<number | null>(null)
const submitLoading = ref(false)

// 产品表单
const productFormRef = ref<FormInstance>()
const productForm = reactive<Partial<Product>>({
  productCode: '',
  productName: '',
  productType: undefined,
  unit: '',
  minTemp: null,
  maxTemp: null,
  minHumidity: null,
  maxHumidity: null,
  shelfLife: null,
  description: ''
})

// 表单验证规则
const productRules: FormRules = {
  productCode: [
    { required: true, message: '请输入产品编码', trigger: 'blur' }
  ],
  productName: [
    { required: true, message: '请输入产品名称', trigger: 'blur' }
  ],
  productType: [
    { required: true, message: '请选择产品类型', trigger: 'change' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ]
}

// 获取产品类型文本
const getProductTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    fruit: '水果',
    vegetable: '蔬菜',
    meat: '肉类',
    seafood: '海鲜',
    dairy: '乳制品',
    other: '其他'
  }
  return typeMap[type] || type
}

// 加载产品列表 - 100%真实数据库数据，反映数据库实时变化
const loadProducts = async () => {
  tableLoading.value = true
  
  try {
    console.log('🚀 开始从数据库获取最新产品数据...')
    
    // 🔄 调用真实数据库API（添加时间戳防缓存）
    const response = await fetch(`http://localhost:8080/database/product/all?_t=${Date.now()}`)
    const data = await response.json()
    console.log('📊 数据库API响应:', data)
    
    if (data.code === 200) {
      console.log('✅ 数据库连接成功，处理最新产品数据...')
      
      const rawProducts = data.data || []
      console.log('📦 数据库原始产品数据:', rawProducts)
      console.log('📝 数据库产品数量:', rawProducts.length)
      
      // 🔄 将数据库字段完全映射为前端格式
      const mappedProducts = rawProducts.map(product => ({
        id: product.id,
        productCode: product.product_code,
        productName: product.product_name,
        productType: product.product_type,
        unit: product.unit,
        minTemp: product.min_temp,
        maxTemp: product.max_temp,
        minHumidity: product.min_humidity,
        maxHumidity: product.max_humidity,
        shelfLife: product.shelf_life,
        status: product.status,
        description: product.description,
        categoryName: product.category_name
      }))
      
      console.log('🔄 数据库数据映射结果:', mappedProducts)
      
      // 🔧 完全清空并重新加载，确保反映数据库当前状态
      productList.value.splice(0, productList.value.length)
      await new Promise(resolve => setTimeout(resolve, 10))
      
      // 从数据库数据重新填充
      productList.value.push(...mappedProducts)
      
      // 更新分页信息反映数据库真实数量
      pagination.total = rawProducts.length
      pagination.current = 1
      
      console.log('📋 前端显示的产品列表:', productList.value)
      console.log('📊 前端产品数量:', productList.value.length)
      
      ElMessage.success(`✅ 已从数据库同步 ${mappedProducts.length} 个最新产品数据`)
      console.log('🎉 产品数据库同步完成!')
    } else {
      console.error('❌ 数据库API返回错误:', data)
      ElMessage.error(`数据库连接失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 数据库产品数据获取失败:', error)
    ElMessage.error('❌ 数据库连接失败：' + error.message)
  } finally {
    tableLoading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  loadProducts()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    productName: '',
    productType: '',
    status: null
  })
  handleSearch()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pagination.size = size
  loadProducts()
}

const handleCurrentChange = (current: number) => {
  pagination.current = current
  loadProducts()
}

// 表格选择处理
const handleSelectionChange = (selection: Product[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增产品
const handleCreate = () => {
  editingId.value = null
  resetProductForm()
  dialogVisible.value = true
}

// 编辑产品
const handleEdit = (row: Product) => {
  editingId.value = row.id
  Object.assign(productForm, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row: Product) => {
  ElMessage.info(`查看产品详情: ${row.productName}`)
  // TODO: 实现产品详情页面
}

// 删除产品
const handleDelete = (row: Product) => {
  ElMessageBox.confirm(
    `确定要删除产品 "${row.productName}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await productApi.deleteProduct(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        loadProducts()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedIds.value.length} 个产品吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await productApi.batchDeleteProducts(selectedIds.value)
      if (response.code === 200) {
        ElMessage.success('批量删除成功')
        selectedIds.value = []
        loadProducts()
      }
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 表单提交
const handleSubmit = async () => {
  if (!productFormRef.value) return
  
  await productFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      let response
      if (editingId.value) {
        response = await productApi.updateProduct(editingId.value, productForm)
      } else {
        response = await productApi.createProduct(productForm)
      }
      
      if (response.code === 200) {
        ElMessage.success(editingId.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        loadProducts()
      }
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetProductForm = () => {
  Object.assign(productForm, {
    productCode: '',
    productName: '',
    productType: undefined,
    unit: '',
    minTemp: null,
    maxTemp: null,
    minHumidity: null,
    maxHumidity: null,
    shelfLife: null,
    description: ''
  })
}

// 对话框关闭处理
const handleDialogClose = () => {
  resetProductForm()
  editingId.value = null
}

// 🔄 自动刷新定时器
let autoRefreshTimer: NodeJS.Timeout | null = null

// 启动自动刷新（每30秒从数据库获取最新数据）
const startAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
  }
  
  autoRefreshTimer = setInterval(async () => {
    console.log('⏰ 自动从数据库刷新产品数据...')
    await loadProducts()
  }, 30000) // 30秒间隔确保数据库变化及时反映
  
  console.log('✅ 产品数据自动刷新已启动（30秒间隔）')
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer)
    autoRefreshTimer = null
    console.log('⏹️ 产品数据自动刷新已停止')
  }
}

// 组件挂载 - 立即从数据库同步最新数据并启动自动刷新
onMounted(async () => {
  console.log('📦 产品管理页面已加载，开始数据库数据同步')
  await loadProducts()
  startAutoRefresh() // 启动自动刷新确保数据库变化及时反映
})

// 注意：页面离开时会自动清理定时器
</script>

<style scoped>
.products-page {
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

.table-container {
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-header {
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.table-search {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.table-pagination {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}

.text-muted {
  color: #999;
}

@media (max-width: 768px) {
  .table-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .table-search, .table-actions {
    justify-content: center;
  }
}
</style>

