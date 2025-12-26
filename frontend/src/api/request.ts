import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import type { ApiResponse } from '@/types/api'

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  withCredentials: false,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const userStore = useUserStore()
    
    // 添加认证token
    if (userStore.token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response
    
    // 处理业务错误
    if (data.code && data.code !== 200) {
      // 401未授权，跳转登录
      if (data.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        ElMessageBox.alert('登录已过期，请重新登录', '提示', {
          confirmButtonText: '确定',
          type: 'warning'
        }).then(() => {
          window.location.href = '/login'
        })
        return Promise.reject(new Error(data.message))
      }
      
      // 其他业务错误
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    
    return response
  },
  (error) => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败(${status})`)
      }
    } else if (error.request) {
      ElMessage.error('网络连接异常，请检查网络')
    } else {
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default request

// 封装常用请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.get(url, config).then(res => res.data)
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.post(url, data, config).then(res => res.data)
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.put(url, data, config).then(res => res.data)
  },
  
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.delete(url, config).then(res => res.data)
  },
  
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.patch(url, data, config).then(res => res.data)
  }
}

