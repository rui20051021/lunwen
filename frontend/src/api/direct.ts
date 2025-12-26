// 直接API访问 - 绕过CORS问题的解决方案
const API_BASE = 'http://localhost:8080'

// 直接API请求函数
export async function directApiRequest(endpoint: string, options: RequestInit = {}) {
  try {
    const url = `${API_BASE}${endpoint}`
    console.log('Direct API请求:', url)
    
    const response = await fetch(url, {
      method: 'GET',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      ...options
    })
    
    const data = await response.json()
    console.log('API响应:', data)
    
    return {
      success: true,
      data: data,
      status: response.status
    }
  } catch (error: any) {
    console.error('API请求失败:', error)
    return {
      success: false,
      error: error.message
    }
  }
}

// 具体API函数
export const directApi = {
  // 基础测试
  async testHello() {
    return directApiRequest('/test/hello')
  },
  
  async testHealth() {
    return directApiRequest('/test/health')
  },
  
  // 产品相关
  async getProducts() {
    return directApiRequest('/products')
  },
  
  async getProductStats() {
    return directApiRequest('/products/statistics')
  },
  
  // 订单相关
  async getOrders() {
    return directApiRequest('/orders')
  },
  
  async getOrderStats() {
    return directApiRequest('/orders/statistics')
  },
  
  // 预警相关
  async getAlerts() {
    return directApiRequest('/alerts')
  },
  
  async getAlertRules() {
    return directApiRequest('/alerts/rules')
  },
  
  async getAlertStats() {
    return directApiRequest('/alerts/statistics')
  },
  
  // 认证相关
  async login(credentials: { username: string; password: string }) {
    return directApiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    })
  },
  
  async getRoles() {
    return directApiRequest('/auth/roles')
  },
  
  async quickLogin(roleCode: string) {
    return directApiRequest('/auth/quick-login', {
      method: 'POST',
      body: JSON.stringify({ roleCode })
    })
  }
}
