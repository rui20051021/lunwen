<template>
  <div class="vehicles-page">
    <div class="page-header">
      <h1>🚛 车辆管理</h1>
      <p>管理冷藏车辆，监控设备状态</p>
    </div>
    
    <!-- 车辆统计 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">🚛</div>
        <div class="stat-info">
          <div class="stat-number">{{ vehicleStats.totalVehicles }}</div>
          <div class="stat-label">车辆总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <div class="stat-number">{{ vehicleStats.availableVehicles }}</div>
          <div class="stat-label">可用车辆</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🚚</div>
        <div class="stat-info">
          <div class="stat-number">{{ vehicleStats.inTransitVehicles }}</div>
          <div class="stat-label">运输中</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">🔧</div>
        <div class="stat-info">
          <div class="stat-number">{{ vehicleStats.maintenanceVehicles }}</div>
          <div class="stat-label">维修中</div>
        </div>
      </div>
    </div>
    
    <!-- 车辆列表 -->
    <div class="table-container">
      <div class="table-header">
        <h3>车辆列表</h3>
        <div class="table-actions">
          <el-button type="primary" @click="handleAddVehicle">
            添加车辆
          </el-button>
          <el-button type="success" @click="refreshVehicles">
            刷新数据
          </el-button>
        </div>
      </div>
      
      <el-table :data="vehicleList" style="width: 100%" stripe>
        <el-table-column prop="vehicleCode" label="车辆编码" width="100" />
        <el-table-column prop="licensePlate" label="车牌号" width="100" />
        <el-table-column prop="vehicleType" label="车辆类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getVehicleTypeName(row.vehicleType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="brand" label="品牌" width="80" />
        <el-table-column prop="model" label="型号" width="100" />
        <el-table-column prop="loadCapacity" label="载重(吨)" width="90" />
        <el-table-column prop="volumeCapacity" label="容积(m³)" width="90" />
        <el-table-column prop="vehicleStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getVehicleStatusType(row.vehicleStatus)" size="small">
              {{ getVehicleStatusText(row.vehicleStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="温控设备" width="120">
          <template #default="{ row }">
            <span v-if="row.temperatureSensorId" class="sensor-online">🌡️ 在线</span>
            <span v-else class="sensor-offline">❌ 离线</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" text @click="handleTracking(row)">
              定位
            </el-button>
            <el-button type="warning" size="small" text @click="handleMaintenance(row)">
              维护
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 司机信息 -->
    <div class="driver-section">
      <h3>👨‍💼 司机信息</h3>
      <div class="driver-grid">
        <div v-for="driver in driverList" :key="driver.id" class="driver-card">
          <div class="driver-header">
            <span class="driver-name">{{ driver.name }}</span>
            <el-tag :type="driver.driverStatus === 'available' ? 'success' : 'warning'" size="small">
              {{ getDriverStatusText(driver.driverStatus) }}
            </el-tag>
          </div>
          <div class="driver-info">
            <div class="driver-detail">
              <span class="detail-label">驾驶证:</span>
              <span class="detail-value">{{ driver.drivingLicense }}</span>
            </div>
            <div class="driver-detail">
              <span class="detail-label">准驾车型:</span>
              <span class="detail-value">{{ driver.licenseType }}</span>
            </div>
            <div class="driver-detail">
              <span class="detail-label">有效期:</span>
              <span class="detail-value">{{ driver.licenseExpiry }}</span>
            </div>
            <div class="driver-detail">
              <span class="detail-label">联系电话:</span>
              <span class="detail-value">{{ driver.phone }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加车辆对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加车辆"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="vehicleFormRef"
        :model="vehicleForm"
        :rules="vehicleRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车辆编码" prop="vehicleCode">
              <el-input 
                v-model="vehicleForm.vehicleCode" 
                placeholder="自动生成或手动输入"
              />
              <el-button 
                type="primary" 
                size="small" 
                style="margin-top: 5px"
                @click="generateVehicleCode"
              >
                自动生成
              </el-button>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="车牌号" prop="licensePlate">
              <el-input 
                v-model="vehicleForm.licensePlate" 
                placeholder="如：京A12345"
                maxlength="8"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车辆类型" prop="vehicleType">
              <el-select 
                v-model="vehicleForm.vehicleType" 
                placeholder="请选择车辆类型"
                style="width: 100%"
              >
                <el-option label="冷藏车" value="refrigerated" />
                <el-option label="冷冻车" value="frozen" />
                <el-option label="保温车" value="insulated" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input 
                v-model="vehicleForm.brand" 
                placeholder="如：福田"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input 
                v-model="vehicleForm.model" 
                placeholder="如：奥铃冷藏车"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="载重(吨)" prop="loadCapacity">
              <el-input-number 
                v-model="vehicleForm.loadCapacity" 
                :min="0.5" 
                :max="50"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="容积(m³)" prop="volumeCapacity">
              <el-input-number 
                v-model="vehicleForm.volumeCapacity" 
                :min="1" 
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="当前温度(℃)">
              <el-input-number 
                v-model="vehicleForm.currentTemp" 
                :min="-30" 
                :max="30"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="温度传感器ID">
          <el-input 
            v-model="vehicleForm.temperatureSensorId" 
            placeholder="如：SENSOR001（可选）"
          />
        </el-form-item>
        
        <el-form-item label="GPS设备ID">
          <el-input 
            v-model="vehicleForm.gpsDeviceId" 
            placeholder="如：GPS001（可选）"
          />
        </el-form-item>
        
        <el-form-item label="车辆状态" prop="vehicleStatus">
          <el-radio-group v-model="vehicleForm.vehicleStatus">
            <el-radio label="available">可用</el-radio>
            <el-radio label="in_transit">运输中</el-radio>
            <el-radio label="maintenance">维修中</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="vehicleForm.remarks" 
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
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
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

// 车辆统计数据（将从真实数据库获取）
const vehicleStats = reactive({
  totalVehicles: 0,
  availableVehicles: 0,
  inTransitVehicles: 0,
  maintenanceVehicles: 0
})

// 车辆列表数据（将从真实数据库获取）
const vehicleList = ref([])

// 司机列表数据（将从真实数据库获取）
const driverList = ref([])

// 对话框状态
const dialogVisible = ref(false)
const submitLoading = ref(false)
const vehicleFormRef = ref<FormInstance>()

// 车辆表单数据
const vehicleForm = reactive({
  vehicleCode: '',
  licensePlate: '',
  vehicleType: '',
  brand: '',
  model: '',
  loadCapacity: 5,
  volumeCapacity: 20,
  currentTemp: 2.5,
  temperatureSensorId: '',
  gpsDeviceId: '',
  vehicleStatus: 'available',
  remarks: ''
})

// 表单验证规则
const vehicleRules: FormRules = {
  vehicleCode: [
    { required: true, message: '请输入车辆编码', trigger: 'blur' }
  ],
  licensePlate: [
    { required: true, message: '请输入车牌号', trigger: 'blur' },
    { pattern: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{5}$/, message: '请输入正确的车牌号格式', trigger: 'blur' }
  ],
  vehicleType: [
    { required: true, message: '请选择车辆类型', trigger: 'change' }
  ],
  brand: [
    { required: true, message: '请输入品牌', trigger: 'blur' }
  ]
}

// 获取车辆类型名称
const getVehicleTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    refrigerated_truck: '冷藏车',
    large_truck: '大货车',
    medium_truck: '中货车',
    small_truck: '小货车'
  }
  return typeMap[type] || type
}

// 获取车辆状态类型
const getVehicleStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    available: 'success',
    in_transit: 'warning',
    maintenance: 'danger',
    retired: 'info'
  }
  return typeMap[status] || ''
}

// 获取车辆状态文本
const getVehicleStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    available: '可用',
    in_transit: '运输中',
    maintenance: '维修中',
    retired: '已退役'
  }
  return textMap[status] || status
}

// 获取司机状态文本
const getDriverStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    available: '可用',
    driving: '驾驶中',
    rest: '休息',
    leave: '请假',
    resigned: '已离职'
  }
  return textMap[status] || status
}

// 操作函数
const handleAddVehicle = () => {
  generateVehicleCode()
  dialogVisible.value = true
}

// 生成车辆编码
const generateVehicleCode = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = String(Math.floor(Math.random() * 1000)).padStart(3, '0')
  vehicleForm.vehicleCode = `VEH${year}${month}${day}${random}`
}

// 关闭对话框
const handleDialogClose = () => {
  vehicleFormRef.value?.resetFields()
  Object.assign(vehicleForm, {
    vehicleCode: '',
    licensePlate: '',
    vehicleType: '',
    brand: '',
    model: '',
    loadCapacity: 5,
    volumeCapacity: 20,
    currentTemp: 2.5,
    temperatureSensorId: '',
    gpsDeviceId: '',
    vehicleStatus: 'available',
    remarks: ''
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!vehicleFormRef.value) return
  
  await vehicleFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    
    try {
      const response = await fetch('http://localhost:8080/database/vehicle/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(vehicleForm)
      })
      
      const data = await response.json()
      console.log('创建车辆响应:', data)
      
      if (data.code === 200) {
        ElMessage.success('车辆添加成功')
        dialogVisible.value = false
        await refreshVehicles() // 刷新列表
      } else {
        ElMessage.error(data.message || '车辆添加失败')
      }
    } catch (error) {
      console.error('添加车辆失败:', error)
      ElMessage.error('添加失败，请检查网络连接')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleEdit = (row: any) => {
  ElMessage.info(`编辑车辆: ${row.licensePlate}`)
}

const handleTracking = (row: any) => {
  ElMessage.info(`车辆定位: ${row.licensePlate}`)
}

const handleMaintenance = (row: any) => {
  ElMessage.info(`车辆维护: ${row.licensePlate}`)
}

const refreshVehicles = async () => {
  try {
    console.log('🚀 开始刷新车辆数据...')
    
    // 🔄 调用真实数据库API获取车辆数据
    const [vehiclesRes, driversRes, statsRes] = await Promise.all([
      fetch('http://localhost:8080/database/vehicle/all'),
      fetch('http://localhost:8080/database/vehicle/drivers'),
      fetch('http://localhost:8080/database/vehicle/statistics')
    ])
    
    const vehiclesData = await vehiclesRes.json()
    const driversData = await driversRes.json()
    const statsData = await statsRes.json()
    
    console.log('📊 车辆API响应:', vehiclesData)
    console.log('📊 司机API响应:', driversData)
    console.log('📊 统计API响应:', statsData)
    
    if (vehiclesData.code === 200) {
      // 🔄 更新车辆数据
      const rawVehicles = vehiclesData.data || []
      vehicleList.value = rawVehicles.map(vehicle => ({
        id: vehicle.id,
        vehicleCode: vehicle.vehicle_code,
        licensePlate: vehicle.license_plate,
        vehicleType: vehicle.vehicle_type,
        brand: vehicle.brand,
        model: vehicle.model,
        loadCapacity: vehicle.load_capacity,
        volumeCapacity: vehicle.volume_capacity,
        vehicleStatus: vehicle.vehicle_status,
        temperatureSensorId: vehicle.temperature_sensor_id
      }))
      
      console.log('🚛 真实车辆数据:', vehicleList.value)
    }
    
    if (driversData.code === 200) {
      // 🔄 更新司机数据
      const rawDrivers = driversData.data || []
      driverList.value = rawDrivers.map(driver => ({
        id: driver.id,
        driverCode: driver.driver_code,
        name: driver.driver_name,
        phone: driver.phone,
        drivingLicense: driver.driving_license,
        licenseType: driver.license_type,
        licenseExpiry: driver.license_expiry ? new Date(driver.license_expiry).toLocaleDateString() : '未设置',
        driverStatus: driver.driver_status
      }))
      
      console.log('👨‍💼 真实司机数据:', driverList.value)
    }
    
    if (statsData.code === 200) {
      // 🔄 更新统计数据
      const stats = statsData.data
      vehicleStats.totalVehicles = stats.total_vehicles || 0
      vehicleStats.availableVehicles = stats.available_vehicles || 0
      vehicleStats.inTransitVehicles = stats.in_transit_vehicles || 0
      vehicleStats.maintenanceVehicles = stats.maintenance_vehicles || 0
      
      console.log('📈 真实统计数据:', vehicleStats)
    }
    
    ElMessage.success(`✅ 成功加载车辆数据（真实数据库）`)
    console.log('🎉 车辆数据刷新完成!')
    
  } catch (error) {
    console.error('💥 刷新车辆数据失败:', error)
    ElMessage.error('❌ 刷新失败，请检查网络连接')
  }
}

onMounted(() => {
  console.log('物流商车辆管理页面已加载')
  refreshVehicles()
})
</script>

<style scoped>
.vehicles-page {
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

.driver-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.driver-section h3 {
  color: #333;
  font-size: 18px;
  margin-bottom: 16px;
}

.driver-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.driver-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.driver-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.driver-name {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.driver-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.driver-detail {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.detail-label {
  color: #666;
}

.detail-value {
  color: #333;
  font-weight: 500;
}

.sensor-online {
  color: #52c41a;
  font-size: 12px;
}

.sensor-offline {
  color: #ff4d4f;
  font-size: 12px;
}
</style>