import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest, LoginResponse, UserType } from '@/types/user'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userType = computed(() => user.value?.userType)
  const isAdmin = computed(() => roles.value.includes('admin'))
  const isSupplier = computed(() => roles.value.includes('supplier'))
  const isLogistics = computed(() => roles.value.includes('logistics'))
  const isPurchaser = computed(() => roles.value.includes('purchaser'))
  const isRegulator = computed(() => roles.value.includes('regulator'))
  
  // 操作
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUser = (userData: User) => {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }
  
  const setUserInfo = (userData: User) => {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }
  
  const setRoles = (roleList: string[]) => {
    roles.value = roleList
  }
  
  const setPermissions = (permissionList: string[]) => {
    permissions.value = permissionList
  }
  
  const login = async (loginForm: LoginRequest): Promise<LoginResponse> => {
    try {
      const response = await authApi.login(loginForm)
      
      if (response.code === 200) {
        const loginData = response.data
        setToken(loginData.accessToken)
        setUser(loginData.userInfo)
        setRoles(loginData.roles)
        setPermissions(loginData.permissions)
        
        return loginData
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }
  
  const logout = () => {
    token.value = ''
    user.value = null
    roles.value = []
    permissions.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  const hasRole = (role: string): boolean => {
    return roles.value.includes(role) || roles.value.includes('admin')
  }
  
  const hasPermission = (permission: string): boolean => {
    return permissions.value.includes(permission) || roles.value.includes('admin')
  }
  
  const getUserDashboardPath = (): string => {
    if (!user.value) return '/login'
    
    switch (user.value.userType) {
      case UserType.SUPPLIER:
        return '/supplier/dashboard'
      case UserType.LOGISTICS:
        return '/logistics/dashboard'
      case UserType.PURCHASER:
        return '/purchaser/dashboard'
      case UserType.REGULATOR:
        return '/regulator/dashboard'
      case UserType.ADMIN:
        return '/admin/dashboard'
      default:
        return '/dashboard'
    }
  }
  
  return {
    // 状态
    token,
    user,
    roles,
    permissions,
    
    // 计算属性
    isLoggedIn,
    userType,
    isAdmin,
    isSupplier,
    isLogistics,
    isPurchaser,
    isRegulator,
    
    // 操作
    setToken,
    setUser,
    setUserInfo,
    setRoles,
    setPermissions,
    login,
    logout,
    hasRole,
    hasPermission,
    getUserDashboardPath
  }
})

