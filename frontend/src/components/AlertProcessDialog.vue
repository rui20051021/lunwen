<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="预警编号">
        <el-input v-model="alert.alertCode" disabled />
      </el-form-item>
      
      <el-form-item label="预警类型">
        <el-tag :type="getAlertTypeTag(alert.alertType)">
          {{ getAlertTypeName(alert.alertType) }}
        </el-tag>
      </el-form-item>
      
      <el-form-item label="预警级别">
        <el-tag :type="getAlertLevelTag(alert.alertLevel)">
          {{ getAlertLevelName(alert.alertLevel) }}
        </el-tag>
      </el-form-item>
      
      <el-form-item label="预警内容">
        <el-input
          v-model="alert.alertMessage"
          type="textarea"
          :rows="3"
          disabled
        />
      </el-form-item>
      
      <el-form-item
        v-if="action === 'process'"
        label="处理说明"
        prop="processNotes"
      >
        <el-input
          v-model="form.processNotes"
          type="textarea"
          :rows="3"
          placeholder="请输入处理说明"
        />
      </el-form-item>
      
      <el-form-item
        v-if="action === 'complete'"
        label="处理结果"
        prop="processResult"
      >
        <el-input
          v-model="form.processResult"
          type="textarea"
          :rows="3"
          placeholder="请输入处理结果"
        />
      </el-form-item>
      
      <el-form-item
        v-if="action === 'ignore'"
        label="忽略原因"
        prop="ignoreReason"
      >
        <el-input
          v-model="form.ignoreReason"
          type="textarea"
          :rows="3"
          placeholder="请输入忽略原因"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import axios from 'axios'

interface Alert {
  id: number
  alertCode: string
  alertType: string
  alertLevel: string
  alertTitle: string
  alertMessage: string
  relatedId: number
  relatedType: string
}

interface Props {
  alert: Alert
  action: 'process' | 'complete' | 'ignore'
}

const props = defineProps<Props>()
const emit = defineEmits(['success', 'close'])

const dialogVisible = ref(true)
const loading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  processNotes: '',
  processResult: '',
  ignoreReason: ''
})

const rules: FormRules = {
  processNotes: [
    { required: true, message: '请输入处理说明', trigger: 'blur' }
  ],
  processResult: [
    { required: true, message: '请输入处理结果', trigger: 'blur' }
  ],
  ignoreReason: [
    { required: true, message: '请输入忽略原因', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => {
  switch (props.action) {
    case 'process':
      return '开始处理预警'
    case 'complete':
      return '完成预警处理'
    case 'ignore':
      return '忽略预警'
    default:
      return '处理预警'
  }
})

const getAlertTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    timeout: '超时预警',
    temperature: '温控预警',
    humidity: '湿度预警',
    route_deviation: '路径偏离'
  }
  return typeMap[type] || type
}

const getAlertTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    timeout: 'warning',
    temperature: 'danger',
    humidity: 'warning',
    route_deviation: 'info'
  }
  return tagMap[type] || 'info'
}

const getAlertLevelName = (level: string) => {
  const levelMap: Record<string, string> = {
    critical: '严重',
    high: '高',
    medium: '中',
    low: '低'
  }
  return levelMap[level] || level
}

const getAlertLevelTag = (level: string) => {
  const tagMap: Record<string, string> = {
    critical: 'danger',
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return tagMap[level] || 'info'
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    
    try {
      const userId = localStorage.getItem('userId')
      let url = ''
      let data: any = {
        processorId: userId
      }
      
      switch (props.action) {
        case 'process':
          url = `/api/alerts/${props.alert.id}/process`
          data.processNotes = form.processNotes
          break
        case 'complete':
          url = `/api/alerts/${props.alert.id}/complete`
          data.processResult = form.processResult
          break
        case 'ignore':
          url = `/api/alerts/${props.alert.id}/ignore`
          data.ignoreReason = form.ignoreReason
          break
      }
      
      const response = await axios.post(url, data)
      
      if (response.data.success) {
        ElMessage.success(response.data.message)
        emit('success')
        handleClose()
      } else {
        ElMessage.error(response.data.message)
      }
      
    } catch (error: any) {
      console.error('处理预警失败:', error)
      ElMessage.error(error.response?.data?.message || '处理预警失败')
    } finally {
      loading.value = false
    }
  })
}

const handleClose = () => {
  dialogVisible.value = false
  emit('close')
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
