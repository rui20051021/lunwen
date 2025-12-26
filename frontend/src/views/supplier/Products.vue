<template>
  <div class="supplier-products-page">
    <div class="page-header">
      <h1>📦 产品管理</h1>
      <p>管理供应商产品信息，配置冷链要求</p>
    </div>
    
    <!-- 产品统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-info">
          <div class="stat-number">{{ productStats.totalProducts }}</div>
          <div class="stat-label">产品总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ productStats.activeProducts }}</div>
          <div class="stat-label">在售产品</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🌡️</div>
        <div class="stat-info">
          <div class="stat-number">{{ productStats.temperatureControlProducts }}</div>
          <div class="stat-label">温控产品</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">⏰</div>
        <div class="stat-info">
          <div class="stat-number">{{ productStats.shortShelfLifeProducts }}</div>
          <div class="stat-label">短保质期</div>
        </div>
      </div>
    </div>
    
    <!-- 产品列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>我的产品列表</h3>
        <div class="table-actions">
          <el-button type="primary" @click="handleAddProduct">
            添加产品
          </el-button>
          <el-button type="success" @click="refreshProducts">
            🔄 刷新数据
          </el-button>
          <el-button type="info" @click="debugData">
            🔍 调试数据
          </el-button>
          <span style="margin-left: 10px; color: #666;">
            当前产品数量: {{ productList?.length || 0 }}
          </span>
        </div>
      </div>
      
      <el-table :data="productList" :key="productList.length" style="width: 100%" stripe empty-text="暂无产品数据">
        <el-table-column prop="productCode" label="产品编码" width="100" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="productType" label="产品类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getProductTypeName(row.productType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column label="温度要求" width="120">
          <template #default="{ row }">
            <span v-if="row.minTemp !== undefined && row.maxTemp !== undefined">
              {{ row.minTemp }}~{{ row.maxTemp }}℃
            </span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="shelfLife" label="保质期(天)" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '在售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" text @click="handlePricing(row)">
              定价
            </el-button>
            <el-button type="warning" size="small" text @click="handleInventory(row)">
              库存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 产品类型分布 -->
    <div class="product-analysis">
      <h3>📈 产品分析</h3>
      <div class="analysis-grid">
        <div class="analysis-item">
          <h4>🍎 水果类</h4>
          <div class="analysis-count">2个</div>
          <div class="analysis-desc">新鲜橙子、进口车厘子</div>
        </div>
        <div class="analysis-item">
          <h4>🥬 蔬菜类</h4>
          <div class="analysis-count">2个</div>
          <div class="analysis-desc">有机菠菜、有机青菜</div>
        </div>
        <div class="analysis-item">
          <h4>🐟 海鲜类</h4>
          <div class="analysis-count">2个</div>
          <div class="analysis-desc">新鲜三文鱼、冰鲜带鱼</div>
        </div>
        <div class="analysis-item">
          <h4>🥩 肉类</h4>
          <div class="analysis-count">2个</div>
          <div class="analysis-desc">优质牛肉、精品牛排</div>
        </div>
        <div class="analysis-item">
          <h4>🥛 乳制品</h4>
          <div class="analysis-count">2个</div>
          <div class="analysis-desc">新鲜牛奶、有机酸奶</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

// 产品统计数据 (将从真实数据库获取)
const productStats = reactive({
  totalProducts: 0,
  activeProducts: 0,
  temperatureControlProducts: 0,
  shortShelfLifeProducts: 0
})

// 产品列表数据 (将从真实数据库获取)
const productList = ref([])

// 获取产品类型名称
const getProductTypeName = (type: string) => {
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

// 操作函数
const handleAddProduct = () => {
  ElMessage.info('添加产品功能开发中')
}

const handleEdit = (row: any) => {
  ElMessage.info(`编辑产品: ${row.productName}`)
}

const handlePricing = (row: any) => {
  ElMessage.info(`产品定价: ${row.productName}`)
}

const handleInventory = (row: any) => {
  ElMessage.info(`库存管理: ${row.productName}`)
}

const debugData = () => {
  console.log('🔍 开始调试数据状态...')
  console.log('📊 productList.value:', productList.value)
  console.log('📝 productList.value.length:', productList.value.length)
  console.log('📈 productStats:', productStats)
  console.log('🔄 productList类型:', typeof productList.value)
  console.log('✅ 数据是否为数组:', Array.isArray(productList.value))
  
  ElMessage.info(`调试信息已输出到控制台。当前有 ${productList.value?.length || 0} 个产品`)
}

const refreshProducts = async () => {
  try {
    console.log('🚀 开始刷新产品数据...')
    
    // 🔄 调用真实数据库API获取产品数据（直接访问后端）
    const response = await fetch('http://localhost:8080/database/product/all')
    console.log('📡 API响应状态:', response.status, response.statusText)
    
    const data = await response.json()
    console.log('📊 产品API响应:', data) // 调试用
    
    if (data.code === 200) {
      console.log('✅ API调用成功，开始处理数据...')
      
      // 🔄 使用真实数据库数据，转换数据格式
      const rawProducts = data.data || []
      console.log('📦 原始产品数据:', rawProducts)
      console.log('📝 原始数据数量:', rawProducts.length)
      
      const mappedProducts = rawProducts.map(product => ({
        id: product.id,
        productCode: product.product_code,
        productName: product.product_name,
        productType: product.product_type,
        unit: product.unit,
        minTemp: product.min_temp,
        maxTemp: product.max_temp,
        shelfLife: product.shelf_life,
        status: product.status,
        description: product.description,
        categoryName: product.category_name
      }))
      
      console.log('🔄 映射后的产品数据:', mappedProducts)
      
      // 🔧 强制更新productList - 使用多种方式确保响应式更新
      productList.value = []
      await nextTick()
      productList.value = [...mappedProducts]
      
      console.log('📋 更新后的productList.value:', productList.value)
      console.log('📊 productList.value长度:', productList.value.length)
      
      // 🔧 强制更新统计数据
      await nextTick()
      const products = productList.value
      
      // 重置统计数据
      Object.assign(productStats, {
        totalProducts: 0,
        activeProducts: 0,
        temperatureControlProducts: 0,
        shortShelfLifeProducts: 0
      })
      
      // 重新计算统计数据
      await nextTick()
      productStats.totalProducts = products.length
      productStats.activeProducts = products.filter(p => p.status === 1).length
      productStats.temperatureControlProducts = products.filter(p => p.minTemp !== null && p.maxTemp !== null).length
      productStats.shortShelfLifeProducts = products.filter(p => p.shelfLife && p.shelfLife <= 7).length
      
      console.log('📈 更新后的统计数据:', JSON.stringify(productStats))
      
      // 🔧 强制刷新DOM
      await nextTick()
      
      ElMessage.success(`✅ 成功获取 ${products.length} 个产品数据（真实数据库）`)
      console.log('🎉 数据刷新完成! Vue响应式数据已更新')
    } else {
      console.error('❌ API返回错误:', data)
      ElMessage.error(`获取产品数据失败: ${data.message}`)
    }
  } catch (error) {
    console.error('💥 刷新产品数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接或后端服务')
  }
}

onMounted(() => {
  console.log('供应商产品管理页面已加载')
  refreshProducts()
})
</script>

<style scoped>
.supplier-products-page {
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

.product-analysis {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-analysis h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.analysis-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  border: 1px solid #e9ecef;
}

.analysis-item h4 {
  color: #333;
  margin-bottom: 8px;
}

.analysis-count {
  font-size: 24px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 8px;
}

.analysis-desc {
  font-size: 12px;
  color: #666;
}

.text-muted {
  color: #999;
}
</style>