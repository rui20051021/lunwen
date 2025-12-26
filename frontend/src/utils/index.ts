// 工具函数

import dayjs from 'dayjs'

/**
 * 格式化时间
 */
export const formatTime = (time: string | Date, format = 'YYYY-MM-DD HH:mm:ss') => {
  return dayjs(time).format(format)
}

/**
 * 格式化金额
 */
export const formatAmount = (amount: number, precision = 2) => {
  return amount.toFixed(precision)
}

/**
 * 获取文件大小文本
 */
export const formatFileSize = (size: number) => {
  if (size < 1024) return `${size}B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
  if (size < 1024 * 1024 * 1024) return `${(size / (1024 * 1024)).toFixed(1)}MB`
  return `${(size / (1024 * 1024 * 1024)).toFixed(1)}GB`
}

/**
 * 防抖函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  wait: number
): ((...args: Parameters<T>) => void) => {
  let timeout: NodeJS.Timeout
  return (...args: Parameters<T>) => {
    clearTimeout(timeout)
    timeout = setTimeout(() => func.apply(this, args), wait)
  }
}

/**
 * 节流函数
 */
export const throttle = <T extends (...args: any[]) => any>(
  func: T,
  limit: number
): ((...args: Parameters<T>) => void) => {
  let inThrottle: boolean
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => (inThrottle = false), limit)
    }
  }
}

/**
 * 深度克隆
 */
export const deepClone = <T>(obj: T): T => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T
  if (obj instanceof Array) return obj.map(item => deepClone(item)) as unknown as T
  if (typeof obj === 'object') {
    const clonedObj = {} as T
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
  return obj
}

/**
 * 生成随机ID
 */
export const generateId = () => {
  return Math.random().toString(36).substr(2, 9)
}

/**
 * 获取环境变量
 */
export const getEnv = (key: string, defaultValue?: string) => {
  return import.meta.env[key] || defaultValue
}

/**
 * 存储操作
 */
export const storage = {
  set(key: string, value: any) {
    try {
      localStorage.setItem(key, JSON.stringify(value))
    } catch (error) {
      console.error('Storage set error:', error)
    }
  },
  
  get<T = any>(key: string, defaultValue?: T): T | undefined {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : defaultValue
    } catch (error) {
      console.error('Storage get error:', error)
      return defaultValue
    }
  },
  
  remove(key: string) {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('Storage remove error:', error)
    }
  },
  
  clear() {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('Storage clear error:', error)
    }
  }
}

/**
 * URL参数解析
 */
export const parseQuery = (search = window.location.search) => {
  const params = new URLSearchParams(search)
  const result: Record<string, string> = {}
  
  for (const [key, value] of params) {
    result[key] = value
  }
  
  return result
}

/**
 * 验证函数
 */
export const validators = {
  // 验证手机号
  phone: (phone: string) => {
    return /^1[3-9]\d{9}$/.test(phone)
  },
  
  // 验证邮箱
  email: (email: string) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  },
  
  // 验证身份证号
  idCard: (idCard: string) => {
    return /^[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(idCard)
  }
}

